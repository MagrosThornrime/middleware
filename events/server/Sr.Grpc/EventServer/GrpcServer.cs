using System;
using System.Net;
using System.Threading.Tasks;
using Grpc.Core;
using Microsoft.Extensions.Logging;
using Sr.Grpc.gen;

namespace server.Sr.Grpc.EventServer
{
    public class GrpcServer
    {
        private const string Address = "127.0.0.5";
        private const int Port = 50051;

        private Server _server = null!;
        private readonly ILogger<GrpcServer> _logger;
        
        private FantasyImpl _fantasyImpl = new ();
        private WeatherImpl _weatherImpl = new ();

        private GrpcServer(ILogger<GrpcServer> logger)
        {
            _logger = logger;
        }

        private void Start()
        {
            var ipAddress = IPAddress.Parse(Address);
            var endpoint = new IPEndPoint(ipAddress, Port);

            _server = new Server
            {
                Services =
                {
                    FantasySubscriber.BindService(_fantasyImpl),
                    WeatherSubscriber.BindService(_weatherImpl)
                },
                Ports = { new ServerPort(endpoint.Address.ToString(), endpoint.Port, ServerCredentials.Insecure) }
            };

            _server.Start();
            _logger.LogInformation("Server started, listening on {S}:{I}", Address, Port);

            AppDomain.CurrentDomain.ProcessExit += (_, __) => Stop();
            Console.CancelKeyPress += (_, __) => Stop();
        }

        private void Stop()
        {
            _logger.LogInformation("Shutting down gRPC server...");
            _server.ShutdownAsync().Wait();
            _logger.LogInformation("Server shut down.");
        }

        public static async Task Main()
        {
            using var loggerFactory = LoggerFactory.Create(builder => builder.AddConsole());
            var logger = loggerFactory.CreateLogger<GrpcServer>();

            var server = new GrpcServer(logger);
            server.Start();

            while (true)
            {
                server._fantasyImpl.GenerateEvents();
                server._weatherImpl.GenerateEvents();
                await Task.Delay(TimeSpan.FromSeconds(4));
            }
        }
    }
}
