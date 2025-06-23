import asyncio
import grpc
import echo_pb2
import echo_pb2_grpc


class EchoService(echo_pb2_grpc.EchoServiceServicer):
    async def Echo(self, request, context):
        print(f"Received: {request.message}")
        return echo_pb2.EchoResponse(message=f"Echo: {request.message}")


async def serve():
    server = grpc.aio.server()
    echo_pb2_grpc.add_EchoServiceServicer_to_server(EchoService(), server)
    listen_addr = '[::]:50051'
    server.add_insecure_port(listen_addr)
    await server.start()
    print(f"Async gRPC server listening on {listen_addr}")
    await server.wait_for_termination()


if __name__ == '__main__':
    asyncio.run(serve())
