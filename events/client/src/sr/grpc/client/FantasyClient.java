package sr.grpc.client;

import fantasy.Fantasy;
import fantasy.FantasySubscriberGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FantasyClient {

	private static String subscriptionId;
	private static FantasySubscriberGrpc.FantasySubscriberStub stub;
	private static StreamObserver<Fantasy.ControlRequest> requestObserver;
	private static ManagedChannel channel;

	public static void main(String[] args) throws Exception {
		String host = "localhost";
		int port = 50051;

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter your subscription ID: ");
		subscriptionId = reader.readLine().trim();

		connect(host, port);

		// Wait until connection is established
		while (requestObserver == null) {
			System.out.println("Connecting to server...");
			Thread.sleep(500); // Small delay to wait for stream
		}

		System.out.println("Enter commands: sub <location>, unsub <location>, x to exit");

		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();

			if (line.equalsIgnoreCase("x")) {
				break;
			}

			if (requestObserver == null) {
				System.out.println("Not connected. Please wait...");
				continue;
			}

			try {
				if (line.startsWith("sub ")) {
					String location = line.substring(4).trim();
					Fantasy.Subscribe sub = Fantasy.Subscribe.newBuilder()
							.setLocation(location)
							.build();
					Fantasy.ControlRequest req = Fantasy.ControlRequest.newBuilder().setSub(sub).build();
					requestObserver.onNext(req);

				} else if (line.startsWith("unsub ")) {
					String location = line.substring(6).trim();
					Fantasy.Unsubscribe unsub = Fantasy.Unsubscribe.newBuilder()
							.setLocation(location)
							.build();
					Fantasy.ControlRequest req = Fantasy.ControlRequest.newBuilder().setUnsub(unsub).build();
					requestObserver.onNext(req);

				} else {
					System.out.println("Unknown command");
				}
			} catch (Exception e) {
				System.out.println("Failed to send request: " + e.getMessage());
			}
		}

		shutdown();
	}

	// Set up the gRPC channel and stub, then start the stream
	private static void connect(String host, int port) {
		channel = ManagedChannelBuilder.forAddress(host, port)
				.usePlaintext()  // No TLS (insecure)
				.build();

		stub = FantasySubscriberGrpc.newStub(channel);

		startStream(); // Start bidirectional streaming
	}

	// Used to reconnect to the server when the stream is broken
	private static void reconnect(String host, int port) {
		if (requestObserver != null) {
			try {
				requestObserver.onCompleted();  // Notify the server the stream is closing
			} catch (Exception ignored) {}
		}
		if (channel != null) {
			channel.shutdownNow();  // Shutdown the old channel
		}
		connect(host, port);  // Re-establish everything
	}

	// Starts the streaming interaction with the server
	private static void startStream() {
		try {
			requestObserver = null;

			StreamObserver<Fantasy.FantasyEvent> responseObserver = new StreamObserver<>() {
				@Override
				public void onNext(Fantasy.FantasyEvent value) {
					System.out.println("Event received:");
					System.out.println(" Location: " + value.getLocation());
					System.out.println(" Description: " + value.getDescription());
					System.out.println(" Factions: " + value.getFactionsList());
					System.out.println(" Level Range: " + value.getMinimumLevel() + "-" + value.getMaximumLevel());
					System.out.println(" Type: " + value.getEventType());
					System.out.println("------------------------");
				}

				@Override
				public void onError(Throwable t) {
					System.out.println("Stream error: " + t.getMessage());
					requestObserver = null;

					// Attempt to reconnect with a delay
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ignored) {}

					reconnect("localhost", 50051);
				}

				@Override
				public void onCompleted() {
					System.out.println("Stream closed by server.");
					requestObserver = null;
				}
			};

			requestObserver = stub.streamEvents(responseObserver);

			// Send reconnect message safely
			if (requestObserver != null) {
				Fantasy.Reconnect reconnect = Fantasy.Reconnect.newBuilder()
						.setSubscriptionId(subscriptionId)
						.build();
				Fantasy.ControlRequest recRequest = Fantasy.ControlRequest.newBuilder()
						.setRec(reconnect)
						.build();
				requestObserver.onNext(recRequest);
			}

		} catch (Exception e) {
			System.out.println("Failed to start stream: " + e.getMessage());
			requestObserver = null;

			// Retry connecting in 1 second
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignored) {}

			reconnect("localhost", 50051);
		}
	}

	// Clean up resources on shutdown
	private static void shutdown() {
		if (requestObserver != null) {
			try {
				requestObserver.onCompleted();
			} catch (Exception ignored) {}
		}
		if (channel != null) {
			channel.shutdown();
			try {
				channel.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
			} catch (InterruptedException ignored) {}
		}
	}
}
