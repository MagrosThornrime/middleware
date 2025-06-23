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
        self._subscribers = {}
        

    async def _event_match(self, event: fantasy.FantasyEvent, location: str):
        return event.location == location

    def generate_events(self):
        async def _generate():
            async with self._lock:
                self._events.clear()
                for _ in range(random.randint(1, 4)):
                    self._events.append(self.generator.generate_fantasy_event())
        asyncio.create_task(_generate())

    async def StreamEvents(self, request_iterator, context):
        print("Fantasy: subscription stream started")

        if not os.path.exists("data.json"):
            with open("data.json", 'w') as f:
                json.dump({}, f, indent=2)
        
        with open("data.json", "r") as f:
            self._subscribers = json.load(f)

        user = None
        try:
            async for request in request_iterator:
                if request.HasField("sub"):
                    sub = request.sub
                    location = sub.location
                    if location not in self._subscribers[user]:
                        self._subscribers[user].append(location)
                    print(f"Subscribe request received: id={user}, location={location}")

                elif request.HasField("unsub"):
                    unsub = request.unsub
                    location = unsub.location
                    if location in self._subscribers[user]:
                        self._subscribers[user].remove(location)
                    print(f"Unsubscribe request received: id={user}, location={location}")

                elif request.HasField("rec"):
                    rec = request.rec
                    user = rec.subscriptionId
                    if user not in self._subscribers:
                        self._subscribers[user] = []
                    print(f"Subscriber connected: {user}")

                else:
                    print("Fantasy: received unknown control request")

                print(self._subscribers)
                with open("data.json", 'w') as f:
                    json.dump(self._subscribers, f, indent=2)

        except Exception as ex:
            print(f"Fantasy: error while streaming: {ex}")

        print("Fantasy: subscription stream ended")