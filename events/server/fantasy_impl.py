import asyncio
import random
from typing import List
import json
import os

import fantasy_pb2_grpc as fantasy_grpc
import fantasy_pb2 as fantasy

from events_generator import EventsGenerator


class FantasyImpl(fantasy_grpc.FantasySubscriberServicer):
    def __init__(self):
        self.generator = EventsGenerator()
        self._lock = asyncio.Lock()
        self._events: List[fantasy.FantasyEvent] = []
        self._subscriptions = {}
        self._contexts = {}

    def generate_and_send(self):
        async def _task():
            async with self._lock:
                self._events.clear()
                for _ in range(random.randint(3, 10)):
                    self._events.append(self.generator.generate_fantasy_event())
            async with self._lock:
                for user, context in self._contexts.items():
                    for event in self._events:
                        if event.location in self._subscriptions[user]:
                            await context.write(event)
        asyncio.create_task(_task())

    def load_from_json(self):
        if not os.path.exists("data.json"):
            with open("data.json", 'w') as f:
                json.dump({}, f, indent=2)
        
        with open("data.json", "r") as f:
            self._subscriptions = json.load(f)

    def save_to_json(self):
        with open("data.json", 'w') as f:
            json.dump(self._subscriptions, f, indent=2)

    async def StreamEvents(self, request_iterator, context):
        print("Fantasy: subscription stream started")

        self.load_from_json()
        print(self._subscriptions)
        
        user = None
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
                        if user not in self._contexts:
                            self._contexts[user] = context
                        else:
                            print(f"User {user} already active")
                            return
                    print(f"Subscriber connected: {user}")

                else:
                    print("Fantasy: received unknown control request")

                print(self._subscriptions)
                self.save_to_json()

        except Exception as ex:
            print(f"Fantasy: error while streaming: {ex}")

        print("Fantasy: subscription stream ended")
        async with self._lock:
            if user:
                del self._subscriptions[user]
                del self._contexts[user]
        self.save_to_json()
