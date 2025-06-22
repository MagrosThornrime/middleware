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

		System.out.println("Enter commands: sub <location>, unsub <location>, x to exit");

		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();

			if (line.equalsIgnoreCase("x")) {
				break;
			}

			if (requestObserver == null) {
				// Stream disconnected, reconnect
				reconnect(host, port);
				continue;
			}

			if (line.startsWith("sub ")) {
				String location = line.substring(4).trim();
				Fantasy.Subscribe sub = Fantasy.Subscribe.newBuilder()
						.setSubscriptionId(subscriptionId)
						.setLocation(location)
						.build();
				Fantasy.ControlRequest req = Fantasy.ControlRequest.newBuilder().setSub(sub).build();
				requestObserver.onNext(req);

			} else if (line.startsWith("unsub ")) {
				String location = line.substring(6).trim();
				Fantasy.Unsubscribe unsub = Fantasy.Unsubscribe.newBuilder()
						.setSubscriptionId(subscriptionId)
						.setLocation(location)
						.build();
				Fantasy.ControlRequest req = Fantasy.ControlRequest.newBuilder().setUnsub(unsub).build();
				requestObserver.onNext(req);

			} else {
				System.out.println("Unknown command");
			}
		}

		shutdown();
	}

	private static void connect(String host, int port) {
		channel = ManagedChannelBuilder.forAddress(host, port)
				.usePlaintext()
				.build();

		stub = FantasySubscriberGrpc.newStub(channel);

		startStream();
	}

	private static void reconnect(String host, int port) {
		if (requestObserver != null) {
			try {
				requestObserver.onCompleted();
			} catch (Exception ignored) {}
		}
		if (channel != null) {
			channel.shutdownNow();
		}
		connect(host, port);
	}

	private static void startStream() {
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
				requestObserver = null;
				reconnect("localhost", 50051);
			}

			@Override
			public void onCompleted() {
				requestObserver = null;
				System.out.println("Stream closed by server.");
			}
		};

		requestObserver = stub.streamEvents(responseObserver);

		Fantasy.Reconnect reconnect = Fantasy.Reconnect.newBuilder()
				.setSubscriptionId(subscriptionId)
				.build();
		Fantasy.ControlRequest recRequest = Fantasy.ControlRequest.newBuilder()
				.setRec(reconnect)
				.build();

		requestObserver.onNext(recRequest);
	}

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
