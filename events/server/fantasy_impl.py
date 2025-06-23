import asyncio
import random
from typing import List, Dict, Optional
import json
import os

import fantasy_pb2_grpc as fantasy_grpc
import fantasy_pb2 as fantasy

from events_generator import EventsGenerator


class FantasyImpl(fantasy_grpc.FantasySubscriberServicer):
    def __init__(self):
        self.generator = EventsGenerator()
        self._lock: asyncio.Lock = asyncio.Lock()
        self._events: List[fantasy.FantasyEvent] = []
        self._subscriptions: Dict[str, List[str]] = {}  # user_id -> list of locations
        self._contexts: Dict[str, asyncio.StreamWriter] = {}  # user_id -> gRPC stream context
        self._buffers: Dict[str, List[fantasy.FantasyEvent]] = {}  # user_id -> buffered events when offline

    async def generate_and_send(self) -> None:
        """Generates random events and sends them to all subscribed users."""
        async with self._lock:
            self._events.clear()
            for _ in range(random.randint(3, 10)):
                self._events.append(self.generator.generate_fantasy_event())

            for user, subs in self._subscriptions.items():
                for event in self._events:
                    if event.location in subs:
                        if user in self._contexts:
                            context = self._contexts[user]
                            # Send buffered events first
                            for old_event in self._buffers[user]:
                                await context.write(old_event)
                            self._buffers[user] = []
                            # Send current event
                            await context.write(event)
                        else:
                            # Buffer event if user is not connected
                            self._buffers[user].append(event)

    async def load_from_json(self) -> None:
        """Loads subscription data from a JSON file."""
        if not os.path.exists("data.json"):
            with open("data.json", 'w') as f:
                json.dump({}, f, indent=2)

        with open("data.json", "r") as f:
            async with self._lock:
                self._subscriptions = json.load(f)

    async def save_to_json(self) -> None:
        """Saves subscription data to a JSON file."""
        with open("data.json", 'w') as f:
            async with self._lock:
                json.dump(self._subscriptions, f, indent=2)

    async def StreamEvents(self, request_iterator, context) -> None:
        """Handles the bidirectional gRPC stream for event subscription."""
        print("Fantasy: subscription stream started")

        await self.load_from_json()
        print(self._subscriptions)

        user: Optional[str] = None

        try:
            async for request in request_iterator:
                if request.HasField("sub"):
                    sub = request.sub
                    location = sub.location
                    async with self._lock:
                        if location not in self._subscriptions[user]:
                            self._subscriptions[user].append(location)
                    print(f"Subscribe request received: id={user}, location={location}")

                elif request.HasField("unsub"):
                    unsub = request.unsub
                    location = unsub.location
                    async with self._lock:
                        if location in self._subscriptions[user]:
                            self._subscriptions[user].remove(location)
                    print(f"Unsubscribe request received: id={user}, location={location}")

                elif request.HasField("rec"):
                    rec = request.rec
                    user = rec.subscriptionId
                    async with self._lock:
                        if user not in self._subscriptions:
                            self._subscriptions[user] = []
                        if user not in self._buffers:
                            self._buffers[user] = []
                        if user not in self._contexts:
                            self._contexts[user] = context
                        else:
                            print(f"User {user} already active")
                            return
                    print(f"Subscriber connected: {user}")

                elif request.HasField("dis"):
                    async with self._lock:
                        if user:
                            del self._subscriptions[user]
                            del self._contexts[user]
                            del self._buffers[user]
                    print(f"User disconnected: {user}")

                else:
                    print("Fantasy: received unknown control request")

                print(self._subscriptions)
                await self.save_to_json()

        except Exception as ex:
            print(f"Fantasy: error while streaming: {ex}")

        async with self._lock:
            if user in self._contexts:
                del self._contexts[user]

        print("Fantasy: subscription stream ended")
