using Grpc.Core;
using Sr.Grpc.gen;
using System;
using System.Threading.Tasks;

public class CalculatorImpl : Calculator.CalculatorBase
{
    public override Task<ArithmeticOpResult> Add(ArithmeticOpArguments request, ServerCallContext context)
    {
        Console.WriteLine($"addRequest ({request.Arg1}, {request.Arg2})");

        int val = request.Arg1 + request.Arg2;
        var result = new ArithmeticOpResult { Res = val };

        // If both numbers are > 100, simulate a delay
        if (request.Arg1 > 100 && request.Arg2 > 100)
        {
            Task.Delay(5000).Wait();
        }

        return Task.FromResult(result);
    }

    public override Task<ArithmeticOpResult> Subtract(ArithmeticOpArguments request, ServerCallContext context)
    {
        Console.WriteLine($"subtractRequest ({request.Arg1}, {request.Arg2})");

        int val = request.Arg1 - request.Arg2;
        var result = new ArithmeticOpResult { Res = val };

        return Task.FromResult(result);
    }
}