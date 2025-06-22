import asyncio
import random
from typing import List

import generated.fantasy_pb2_grpc as fantasy_grpc
import generated.fantasy_pb2 as fantasy


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
            fantasy.FantasyEventType.DEBATES,
            fantasy.FantasyEventType.DUNGEON,
            fantasy.FantasyEventType.FESTIVAL
        ]

        event_type = random.choice(event_types)
        min_level = random.randint(18, 23)
        max_level = random.randint(30, 68)
        location = random.choice(self._locations)
        interested_factions = random.randint(1, 2)
        selected_factions = random.sample(self._factions, interested_factions)

        subscription = fantasy.FantasySubscription(
            eventType=event_type,
            minimumLevel=min_level,
            maximumLevel=max_level,
            location=location,
            factions=selected_factions
        )


        description = random.choice(self._descriptions)

        return fantasy.FantasyEvent(
            type=subscription,
            description=description
        )

    async def _event_type_match(self, event_sub, client_sub):
        if client_sub.eventType != event_sub.eventType:
            return False
        if client_sub.minimum_level > event_sub.minimum_level:
            return False
        if client_sub.maximum_level < event_sub.maximum_level:
            return False
        if client_sub.location != event_sub.location:
            return False
        return any(f in event_sub.factions for f in client_sub.factions)

    def generate_events(self):
        async def _generate():
            async with self._lock:
                self._events.clear()
                for _ in range(random.randint(1, 4)):
                    self._events.append(self._generate_fantasy_event())
        asyncio.create_task(_generate())

    async def Subscribe(self, request, context):
        print("Fantasy: a subscription started")
        last_events: List[fantasy.FantasyEvent] = []

        try:
            while not context.done():
                current_events = []

                async with self._lock:
                    for event in self._events:
                        if await self._event_type_match(event.type, request):
                            current_events.append(event)

                if current_events != last_events:
                    for event in current_events:
                        await context.write(event)
                    last_events = list(current_events)

                await asyncio.sleep(random.randint(1, 2))
        except asyncio.CancelledError:
            print("Fantasy: a client cancelled")
        except Exception as ex:
            print(f"Fantasy: an error occurred: {ex}")

        print("Fantasy: subscription end")
