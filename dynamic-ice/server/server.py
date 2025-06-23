import Ice
import PrinterModule


class PrinterImpl(PrinterModule.Printer):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.prints_count = 0

    def printDuplicated(self, strings: list[str], count: int,
                        current=None) -> list[str]:
        if count < 0:
            raise PrinterModule.InvalidCountError(
                reason=f"Number of duplicates '{count}' should be positive"
                )
        result = []
        for string in strings:
            result.extend([string,] * count)
        self.prints_count += len(result)
        return result

    def printRange(self, startNum: int, endNum: int, step: int,
                   current=None) -> list[str]:
        result = [str(i) for i in range(startNum, endNum, step)]
        self.prints_count += len(result)
        return result

    def printFibonacci(self, count: int, current=None) -> list[str]:
        if count < 0:
            raise PrinterModule.InvalidCountError(
                reason=f"Number of elements '{count}' should be positive"
                )
        last_num = 0
        current_num = 1
        result = []
        for _ in range(count):
            result.append(str(last_num))
            current_num, last_num = current_num + last_num, current_num
        self.prints_count += len(result)
        return result

    def getDescription(self, current=None) -> str:
        return "Printer service (ICE)"
    
    def getPrintedStringsNum(self, current=None) -> int:
        return self.prints_count


communicator = Ice.initialize()
adapter = communicator.createObjectAdapterWithEndpoints("PrinterAdapter", "tcp -h 127.0.0.2 -p 10000")
printer = PrinterImpl()
identity = Ice.Identity(name="printer", category="devices")
adapter.add(printer, identity)
adapter.activate()
communicator.waitForShutdown()
