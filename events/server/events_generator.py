import random

import fantasy_pb2 as fantasy


class EventsGenerator:
    def __init__(self):
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

    def generate_fantasy_event(self):
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
    