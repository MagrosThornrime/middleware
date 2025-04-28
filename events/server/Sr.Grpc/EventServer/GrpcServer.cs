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

        private Server _server;
        private readonly ILogger<GrpcServer> _logger;

        public GrpcServer(ILogger<GrpcServer> logger)
        {
            _logger = logger;
        }

        public void Start()
        {
            var ipAddress = IPAddress.Parse(Address);
            var endpoint = new IPEndPoint(ipAddress, Port);

            _server = new Server
            {
                Services =
                {
                    Calculator.BindService(new CalculatorImpl()),
                    StreamTester.BindService(new StreamTesterImpl()),
                    FantasySubscriber.BindService(new FantasyImpl())
                    // Add more services here if needed
                },
                Ports = { new ServerPort(endpoint.Address.ToString(), endpoint.Port, ServerCredentials.Insecure) }
            };

            _server.Start();
            _logger.LogInformation($"Server started, listening on {Address}:{Port}");

            AppDomain.CurrentDomain.ProcessExit += (_, __) => Stop();
            Console.CancelKeyPress += (_, __) => Stop();
        }

        public void Stop()
        {
            if (_server != null)
            {
                _logger.LogInformation("Shutting down gRPC server...");
                _server.ShutdownAsync().Wait();
                _logger.LogInformation("Server shut down.");
            }
        }

        public static async Task Main(string[] args)
        {
            using var loggerFactory = LoggerFactory.Create(builder => builder.AddConsole());
            var logger = loggerFactory.CreateLogger<GrpcServer>();

            var server = new GrpcServer(logger);
            server.Start();
            
            // Keep the server alive until shutdown
            await Task.Delay(-1);
        }
    }
}
