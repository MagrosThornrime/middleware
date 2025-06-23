import asyncio
import logging
from grpc import aio
import fantasy_pb2_grpc as fantasy_grpc
from fantasy_impl import FantasyImpl


class GrpcServer:
    ADDRESS: str = "[::]"
    PORT: int = 50051

    def __init__(self):
        self.server: aio.Server = aio.server()
        self.logger: logging.Logger = logging.getLogger("GrpcServer")
        self.fantasy_impl: FantasyImpl = FantasyImpl()

    async def start(self) -> None:
        """Registers services and starts the gRPC server."""
        fantasy_grpc.add_FantasySubscriberServicer_to_server(self.fantasy_impl, self.server)
        self.server.add_insecure_port(f"{self.ADDRESS}:{self.PORT}")
        await self.server.start()
        self.logger.info(f"Server started, listening on {self.ADDRESS}:{self.PORT}")

        # Keeps the server running until interrupted
        await self.server.wait_for_termination()


async def main() -> None:
    """Main entrypoint: configures logging, starts the gRPC server and event loop."""
    logging.basicConfig(level=logging.INFO)
    server = GrpcServer()

    # Launch periodic event generation in background
    asyncio.create_task(generate_events_loop(server))

    # Start the gRPC server
    await server.start()


async def generate_events_loop(server: GrpcServer) -> None:
    """Background task that periodically generates and sends events."""
    while True:
        await server.fantasy_impl.generate_and_send()
        await asyncio.sleep(4)


if __name__ == '__main__':
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        print("\nServer stopped by user (Ctrl+C)")
