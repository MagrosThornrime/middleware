import random
from typing import List

import fantasy_pb2 as fantasy


class EventsGenerator:
    def __init__(self):
        # Internal random generator for reproducibility (if seeded)
        self._generator = random.Random()

        # Predefined event descriptions
        self._descriptions: List[str] = [
            "Emocjonująca przygoda dla wszystkich odważnych śmiałków",
            "Prawdziwe męskie doświadczenie",
            "Tutaj potrzebna jest silna ręka",
            "Mało kto odważy się tu zaglądnąć"
        ]

        # Factions that may be involved in events
        self._factions: List[str] = [
            "Te świry z sekty",
            "Nowy Obóz",
            "Stary Obóz",
            "Mutanty z bagna",
            "Gildia Złodziei",
            "Bractwo Wielkiego Miecza"
        ]

        # Possible locations for events
        self._locations: List[str] = [
            "Pokój Gomeza",
            "Jaskinia ścierwojadów",
            "Kryjówka na bagnach",
            "Kopalnia magicznej rudy"
        ]

    def generate_fantasy_event(self) -> fantasy.FantasyEvent:
        """Generates a single fantasy event with randomized attributes."""

        # Choose a random event type from enum
        event_types: List[fantasy.FantasyEventType.ValueType] = [
            fantasy.FantasyEventType.BATTLE,
            fantasy.FantasyEventType.DUNGEON,
            fantasy.FantasyEventType.FESTIVAL
        ]
        event_type = self._generator.choice(event_types)

        # Random level range
        min_level = self._generator.randint(18, 23)
        max_level = self._generator.randint(30, 68)

        # Random location and factions
        location = self._generator.choice(self._locations)
        interested_factions = self._generator.randint(1, 3)
        selected_factions = self._generator.sample(self._factions, interested_factions)

        # Random description
        description = self._generator.choice(self._descriptions)

        # Create and return the protobuf FantasyEvent
        return fantasy.FantasyEvent(
            eventType=event_type,
            minimumLevel=min_level,
            maximumLevel=max_level,
            location=location,
            description=description,
            factions=selected_factions
        )
