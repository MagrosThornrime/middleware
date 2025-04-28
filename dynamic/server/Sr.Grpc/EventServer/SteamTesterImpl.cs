using Grpc.Core;
using Sr.Grpc.gen;
using System;
using System.Threading.Tasks;

public class StreamTesterImpl : StreamTester.StreamTesterBase
{
    public override async Task GeneratePrimeNumbers(MyTask request, IServerStreamWriter<Number> responseStream, ServerCallContext context)
    {
        Console.WriteLine($"generatePrimeNumbers is starting (max={request.Max})");

        for (int i = 0; i < request.Max; i++)
        {
            if (IsPrime(i))
            {
                var number = new Number { Value = i };
                await responseStream.WriteAsync(number);
            }
        }

        Console.WriteLine("generatePrimeNumbers completed");
    }

    private bool IsPrime(int val)
    {
        if (val % 2 == 0) return false;
        
        try { Task.Delay(1000 + val * 200).Wait(); } catch { }
        return true; // of course not truly prime test
    }

    public override async Task<Report> CountPrimeNumbers(IAsyncStreamReader<Number> requestStream, ServerCallContext context)
    {
        Console.WriteLine("BEGIN countPrimeNumbers");

        int count = 0;

        while (await requestStream.MoveNext())
        {
            var number = requestStream.Current;
            Console.WriteLine($"Received number {number.Value}");
            count++;
        }

        Console.WriteLine("END countPrimeNumbers");

        return new Report { Count = count };
    }
}