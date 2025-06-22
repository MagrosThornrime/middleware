import asyncio
import logging
import signal
from grpc import aio
import generated.weather_pb2_grpc as weather_grpc
import generated.fantasy_pb2_grpc as fantasy_grpc
from weather_impl import WeatherImpl
from fantasy_impl import FantasyImpl

class GrpcServer:
    ADDRESS = "[::]"
    PORT = 50051

    def __init__(self):
        self.server = aio.server()
        self.logger = logging.getLogger("GrpcServer")
        self.weather_impl = WeatherImpl()
        self.fantasy_impl = FantasyImpl()  # Needs to be implemented

    async def start(self):
        weather_grpc.add_WeatherSubscriberServicer_to_server(self.weather_impl, self.server)
        fantasy_grpc.add_FantasySubscriberServicer_to_server(self.fantasy_impl, self.server)

        self.server.add_insecure_port(f"{self.ADDRESS}:{self.PORT}")
        await self.server.start()
        self.logger.info(f"Server started, listening on {self.ADDRESS}:{self.PORT}")

        # Handle shutdown gracefully
        loop = asyncio.get_running_loop()
        for sig in (signal.SIGINT, signal.SIGTERM):
            loop.add_signal_handler(sig, lambda: asyncio.create_task(self.stop()))

        await self.server.wait_for_termination()

    async def stop(self):
        self.logger.info("Shutting down gRPC server...")
        await self.server.stop(5)
        self.logger.info("Server shut down.")

async def main():
    logging.basicConfig(level=logging.INFO)
    server = GrpcServer()
    asyncio.create_task(generate_events_loop(server))
    await server.start()

async def generate_events_loop(server):
    while True:
        server.weather_impl.generate_events()
        server.fantasy_impl.generate_events()
        await asyncio.sleep(4)

if __name__ == '__main__':
    asyncio.run(main())
