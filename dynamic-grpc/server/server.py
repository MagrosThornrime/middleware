import asyncio
import logging
from grpc import aio

import printer_pb2_grpc as printer_grpc
from printer_impl import PrinterImpl


class GrpcServer:
    ADDRESS: str = "[::]"
    PORT: int = 50051

    def __init__(self):
        self.server: aio.Server = aio.server()
        self.logger: logging.Logger = logging.getLogger("GrpcServer")
        self.printer_impl: PrinterImpl = PrinterImpl()

    async def start(self) -> None:
        """Registers services and starts the gRPC server."""
        printer_grpc.add_PrinterServicer_to_server(self.printer_impl, self.server)
        self.server.add_insecure_port(f"{self.ADDRESS}:{self.PORT}")
        await self.server.start()
        self.logger.info(f"Server started, listening on {self.ADDRESS}:{self.PORT}")

        # Keeps the server running until interrupted
        await self.server.wait_for_termination()


async def main() -> None:
    """Main entrypoint: configures logging, starts the gRPC server and event loop."""
    logging.basicConfig(level=logging.INFO)
    server = GrpcServer()

    # Start the gRPC server
    await server.start()

if __name__ == '__main__':
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        print("\nServer stopped by user (Ctrl+C)")
