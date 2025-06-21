using Grpc.Core;
using Sr.Grpc.gen;
using System;
using System.Collections.Generic;
using System.Data;
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
        List<WeatherType> values = [WeatherType.Rain, WeatherType.Storm, WeatherType.Sun,
            WeatherType.Clouds, WeatherType.Fog];
        var type = values[_generator.Next(0, values.Count)];
        var location = _locations[_generator.Next(0, _locations.Count)];
        var temperature = -10.0f + (float)_generator.NextDouble() * 44.0f;
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
        WeatherEvent? lastEvent = null;
        try
        {
            while (true)
            {
                if (context.CancellationToken.IsCancellationRequested)
                {
                    Console.WriteLine("Weather: a client cancelled");
                    return;
                }

                WeatherEvent? toSend = null;
                lock (_lock)
                {
                    var found = _events.Find(weatherEvent => request.Location == weatherEvent.Location);
                    if (found is not null)
                    {
                        toSend = found;
                    }
                }

                if (toSend is not null && !toSend.Equals(lastEvent))
                {
                    await responseStream.WriteAsync(toSend);
                }
                
                lastEvent = toSend;
                await Task.Delay(TimeSpan.FromSeconds(_generator.Next(1, 3)));

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