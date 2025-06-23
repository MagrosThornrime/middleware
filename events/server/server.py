import asyncio
import logging
from grpc import aio
import fantasy_pb2_grpc as fantasy_grpc
from fantasy_impl import FantasyImpl


class GrpcServer:
    ADDRESS: str = "[::]"
    PORT: int = 50051
    KEEPALIVE_OPTIONS: list[tuple[str, int]] = [
        ('grpc.keepalive_time_ms', 10000),
        ('grpc.keepalive_timeout_ms', 5000),
        ('grpc.keepalive_permit_without_calls', 1),
        ('grpc.http2.max_pings_without_data', 0),
        ('grpc.http2.min_time_between_pings_ms', 10000),
        ('grpc.http2.min_ping_interval_without_data_ms', 10000),
    ]

    def __init__(self):
        self.server: aio.Server = aio.server(options=self.KEEPALIVE_OPTIONS)
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
