import asyncio
import random
from typing import List

import fantasy_pb2_grpc as fantasy_grpc
import fantasy_pb2 as fantasy

from events_generator import EventsGenerator


class FantasyImpl(fantasy_grpc.FantasySubscriberServicer):
    def __init__(self):
        self.generator = EventsGenerator()
        self._lock = asyncio.Lock()
        self._events: List[fantasy.FantasyEvent] = []
        

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
        try:
            async for request in request_iterator:
                if request.HasField("sub"):
                    sub = request.sub
                    print(f"Subscribe request received: id={sub.subscriptionId}, location={sub.location}")

                elif request.HasField("unsub"):
                    unsub = request.unsub
                    print(f"Unsubscribe request received: id={unsub.subscriptionId}, location={unsub.location}")

                elif request.HasField("rec"):
                    rec = request.rec
                    print(f"Reconnect request received: id={rec.subscriptionId}")

                else:
                    print("Fantasy: received unknown control request")

        except Exception as ex:
            print(f"Fantasy: error while streaming: {ex}")

        print("Fantasy: subscription stream ended")