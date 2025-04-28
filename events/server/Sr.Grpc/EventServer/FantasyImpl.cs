using Grpc.Core;
using Sr.Grpc.gen;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace server.Sr.Grpc.EventServer;

public class FantasyImpl : FantasySubscriber.FantasySubscriberBase
{
    private readonly object _lock = new();
    private readonly Random _generator = new();
    
    private readonly List<FantasyEvent> _events = [];

    private readonly List<string> _descriptions =
    [
        "Emocjonująca przygoda dla wszystkich odważnych śmiałków",
        "Prawdziwe męskie doświadczenie",
        "Tutaj potrzebna jest silna ręka",
        "Mało kto odważy się tu zaglądnąć"
    ];

    private readonly List<string> _factions =
    [
        "Te świry z sekty",
        "Nowy Obóz",
        "Stary Obóz",
        "Mutanty z bagna",
        "Gildia Złodziei",
        "Bractwo Wielkiego Miecza"
    ];

    private readonly List<string> _locations =
    [
        "Pokój Gomeza",
        "Jaskinia ścierwojadów",
        "Kryjówka na bagnach",
        "Kopalnia magicznej rudy"
    ];

    private FantasyEvent _GenerateFantasyEvent()
    {
        var type = (FantasyEventType)_generator.Next(0, 4);
        var minimumLevel = _generator.Next(18, 24);
        var maximumLevel = _generator.Next(30, 69);
        var location = _locations[_generator.Next(_locations.Count)];
        var interestedFactions = _generator.Next(1, 3);
        var chosenFactions = _factions.OrderBy(x => _generator.Next())
            .Take(interestedFactions)
            .ToList();
        var newSubscriptionData = new FantasySubscription{
            EventType = type,
            MinimumLevel = minimumLevel,
            MaximumLevel = maximumLevel,
            Location = location
        };
        foreach (var t in chosenFactions)
        {
            newSubscriptionData.Factions.Add(t);
        }
        var description = _descriptions[_generator.Next(_descriptions.Count)];
        return new FantasyEvent
        {
            Type = newSubscriptionData,
            Description = description,
        };
    }

    private static bool _EventTypeMatch(FantasySubscription eventSubscription, FantasySubscription clientSubscription)
    {
        if (clientSubscription.EventType != eventSubscription.EventType)
        {
            return false;
        }

        if (clientSubscription.MinimumLevel < eventSubscription.MinimumLevel)
        {
            return false;
        }

        if (clientSubscription.MaximumLevel < eventSubscription.MaximumLevel)
        {
            return false;
        }

        if (clientSubscription.Location != eventSubscription.Location)
        {
            return false;
        }

        return clientSubscription.Factions.Any(t => eventSubscription.Factions.Contains(t));
    }
    
    public void GenerateEvents()
    {
        lock (_lock)
        {
            _events.Clear();
            var eventsNumber = _generator.Next(1, 5);
            for (var i = 0; i < eventsNumber; i++)
            {
                _events.Add(_GenerateFantasyEvent());
            }    
        }
    }

    public override async Task Subscribe(FantasySubscription request, IServerStreamWriter<FantasyEvent> responseStream,
        ServerCallContext context)
    {
        Console.WriteLine("Fantasy: A subscription started");
        try
        {
            while (true)
            {
                if (context.CancellationToken.IsCancellationRequested)
                {
                    Console.WriteLine("Fantasy: a client cancelled");
                    return;
                }

                var toSend = new List<FantasyEvent>();
                lock (_lock)
                {
                    toSend.AddRange(_events.Where(eventData => _EventTypeMatch(eventData.Type, request)));
                }

                foreach (var eventData in toSend)
                {
                    await responseStream.WriteAsync(eventData);
                }

            }

        }
        catch (RpcException ex) when (ex.StatusCode == StatusCode.Cancelled)
        {
            Console.WriteLine("Fantasy: a client cancelled");
        }
        catch (RpcException ex) when (ex.StatusCode != StatusCode.Cancelled)
        {
            Console.WriteLine($"Fantasy: an error occured: {ex.StatusCode}");
        }
        
        Console.WriteLine("Fantasy: subscription end");
    }
    
    
}