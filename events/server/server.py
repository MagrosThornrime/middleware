import asyncio
import logging
from grpc import aio
import fantasy_pb2_grpc as fantasy_grpc
from fantasy_impl import FantasyImpl

class GrpcServer:
    ADDRESS = "[::]"
    PORT = 50051

    def __init__(self):
        self.server = aio.server()
        self.logger = logging.getLogger("GrpcServer")
        self.fantasy_impl = FantasyImpl()

    async def start(self):
        fantasy_grpc.add_FantasySubscriberServicer_to_server(self.fantasy_impl, self.server)

        self.server.add_insecure_port(f"{self.ADDRESS}:{self.PORT}")
        await self.server.start()
        self.logger.info(f"Server started, listening on {self.ADDRESS}:{self.PORT}")

        await self.server.wait_for_termination()

async def main():
    logging.basicConfig(level=logging.INFO)
    server = GrpcServer()
    asyncio.create_task(generate_events_loop(server))
    await server.start()

async def generate_events_loop(server):
    while True:
        server.fantasy_impl.generate_and_send()
        await asyncio.sleep(4)

if __name__ == '__main__':
    asyncio.run(main())
