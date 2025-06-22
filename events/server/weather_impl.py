import asyncio
import random

import generated.weather_pb2_grpc as weather_grpc
import generated.weather_pb2 as weather


class WeatherImpl(weather_grpc.WeatherSubscriberServicer):
    def __init__(self):
        self._lock = asyncio.Lock()
        self._events = []
        self._locations = ["Cracow", "Warsaw", "Rzeszow", "Gdansk", "Katowice"]

    def _generate_weather_event(self):
        weather_types = [
            weather.WeatherType.RAIN,
            weather.WeatherType.STORM,
            weather.WeatherType.SUN,
            weather.WeatherType.CLOUDS,
            weather.WeatherType.FOG
        ]
        event = weather.WeatherEvent(
            location=random.choice(self._locations),
            temperature=random.uniform(-10.0, 34.0),
            type=random.choice(weather_types)
        )

        return event

    def generate_events(self):
        events = [self._generate_weather_event() for _ in range(random.randint(1, 4))]
        asyncio.create_task(self._update_events(events))

    async def _update_events(self, events):
        async with self._lock:
            self._events = events

    async def Subscribe(self, request, context):
        print("Weather: a subscription started")
        last_event = None

        try:
            while not context.done():
                to_send = None
                async with self._lock:
                    for e in self._events:
                        if e.location == request.location:
                            to_send = e
                            break

                if to_send and (last_event != to_send):
                    await context.write(to_send)
                    last_event = to_send

                await asyncio.sleep(random.randint(1, 2))
        except asyncio.CancelledError:
            print("Weather: a client cancelled")
        except Exception as ex:
            print(f"Weather: an error occurred: {ex}")
        print("Weather: subscription end")
