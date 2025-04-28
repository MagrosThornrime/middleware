using Grpc.Core;
using Sr.Grpc.gen;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace server.Sr.Grpc.EventServer;

public class WeatherImpl : WeatherSubscriber.WeatherSubscriberBase
{
    private readonly object _lock = new();
    private readonly Random _generator = new();
    
    private readonly List<WeatherEvent> _events = [];

    private readonly List<string> _locations =
    [
        "Cracow", "Warsaw", "Rzeszow", "Gdansk", "Katowice"
    ];
    
    private WeatherEvent _GenerateWeatherEvent()
    {
        var type = (WeatherType)_generator.Next(0, 3);
        var location = _locations[_generator.Next(0, _locations.Count)];
        var temperature = _generator.Next(-10, 33);
        return new WeatherEvent
        {
            Location = location,
            Temperature = temperature,
            Type = type
        };
    }
    
    public void GenerateEvents()
    {
        lock (_lock)
        {
            _events.Clear();
            var eventsNumber = _generator.Next(1, 5);
            for (var i = 0; i < eventsNumber; i++)
            {
                _events.Add(_GenerateWeatherEvent());
            }    
        }
    }

    public override async Task Subscribe(WeatherSubscription request, IServerStreamWriter<WeatherEvent> responseStream,
        ServerCallContext context)
    {
        Console.WriteLine("Weather: A subscription started");
        try
        {
            while (true)
            {
                if (context.CancellationToken.IsCancellationRequested)
                {
                    Console.WriteLine("Weather: a client cancelled");
                    return;
                }

                var toSend = new List<WeatherEvent>();
                lock (_lock)
                {
                    toSend.AddRange(_events.Where(weatherEvent => request.Location == weatherEvent.Location));
                }

                foreach (var eventData in toSend)
                {
                    await responseStream.WriteAsync(eventData);
                }

            }

        }
        catch (RpcException ex) when (ex.StatusCode == StatusCode.Cancelled)
        {
            Console.WriteLine("Weather: a client cancelled");
        }
        catch (RpcException ex) when (ex.StatusCode != StatusCode.Cancelled)
        {
            Console.WriteLine($"Weather: an error occured: {ex.StatusCode}");
        }

        Console.WriteLine("Weather: subscription end");
    }
}