import printer_pb2_grpc as printer_grpc
import printer_pb2 as printer


class PrinterImpl(printer_grpc.PrinterServicer):
    async def PrintRange(self, request, context) -> None:
        print("Range: stream started")

        start_number = request.startNumber
        end_number = request.endNumber
        step = request.step

        try:
            for num in range(start_number, end_number, step):
                response = printer.PrinterResponse(message=str(num))
                await context.write(response)
        except Exception as ex:
            print(f"Range: error while streaming: {ex}")

        print("Range: stream ended")

    async def PrintFibonacci(self, request, context) -> None:
        print("Fibonacci: stream started")

        last = 0
        current = 1
        try:
            for _ in range(request.number):
                response = printer.PrinterResponse(message=str(current))
                await context.write(response)
                last, current = current, last + current
        except Exception as ex:
            print(f"Fibonacci: error while streaming: {ex}")

        print("Fibonacci: stream ended")

    async def PrintStrings(self, request, context) -> None:
        print("Strings: stream started")

        try:
            for task in request.tasks:
                for _ in range(task.count):
                    response = printer.PrinterResponse(message=str(task.text))
                    await context.write(response)
        except Exception as ex:
            print(f"Strings: error while streaming: {ex}")

        print("Strings: stream ended")
