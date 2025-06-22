import asyncio
import random
from typing import List

import fantasy_pb2_grpc as fantasy_grpc
import fantasy_pb2 as fantasy


class FantasyImpl(fantasy_grpc.FantasySubscriberServicer):
    def __init__(self):
        self._lock = asyncio.Lock()
        self._events: List[fantasy.FantasyEvent] = []
        self._generator = random.Random()

        self._descriptions = [
            "Emocjonująca przygoda dla wszystkich odważnych śmiałków",
            "Prawdziwe męskie doświadczenie",
            "Tutaj potrzebna jest silna ręka",
            "Mało kto odważy się tu zaglądnąć"
        ]

        self._factions = [
            "Te świry z sekty",
            "Nowy Obóz",
            "Stary Obóz",
            "Mutanty z bagna",
            "Gildia Złodziei",
            "Bractwo Wielkiego Miecza"
        ]

        self._locations = [
            "Pokój Gomeza",
            "Jaskinia ścierwojadów",
            "Kryjówka na bagnach",
            "Kopalnia magicznej rudy"
        ]

    def _generate_fantasy_event(self):
        event_types = [
            fantasy.FantasyEventType.BATTLE,
            fantasy.FantasyEventType.DUNGEON,
            fantasy.FantasyEventType.FESTIVAL
        ]

        event_type = random.choice(event_types)
        min_level = random.randint(18, 23)
        max_level = random.randint(30, 68)
        location = random.choice(self._locations)
        interested_factions = random.randint(1, 3)
        selected_factions = random.sample(self._factions, interested_factions)
        description = random.choice(self._descriptions)

        return fantasy.FantasyEvent(
            eventType=event_type,
            minimumLevel=min_level,
            maximumLevel=max_level,
            location=location,
            description=description,
            factions=selected_factions
        )

    async def _event_match(self, event: fantasy.FantasyEvent, location: str):
        return event.location == location

    def generate_events(self):
        async def _generate():
            async with self._lock:
                self._events.clear()
                for _ in range(random.randint(1, 4)):
                    self._events.append(self._generate_fantasy_event())
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