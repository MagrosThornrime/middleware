package sr.grpc.client;

import fantasy.Fantasy;
import fantasy.FantasySubscriberGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FantasyClient {

	public static void main(String[] args) throws Exception {
		String host = "localhost";
		int port = 50051;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		// Ask user for subscription ID
		System.out.print("Enter your subscription ID: ");
		String subscriptionId = reader.readLine().trim();

		ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
				.usePlaintext()
				.build();

		FantasySubscriberGrpc.FantasySubscriberStub stub = FantasySubscriberGrpc.newStub(channel);

		// Create response handler
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
				System.err.println("Error from server: " + t.getMessage());
			}

			@Override
			public void onCompleted() {
				System.out.println("Stream closed by server.");
			}
		};

		// Open bidirectional stream
		StreamObserver<Fantasy.ControlRequest> requestObserver = stub.streamEvents(responseObserver);

		// Send initial reconnect request
		Fantasy.Reconnect reconnect = Fantasy.Reconnect.newBuilder()
				.setSubscriptionId(subscriptionId)
				.build();

		Fantasy.ControlRequest recRequest = Fantasy.ControlRequest.newBuilder()
				.setRec(reconnect)
				.build();

		requestObserver.onNext(recRequest);
		System.out.println("Sent reconnect with subscription ID: " + subscriptionId);

		// CLI interaction
		System.out.println("Enter commands: sub <location>, unsub <location>, x to exit");

		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();

			if (line.equalsIgnoreCase("x")) {
				break;
			}

			if (line.startsWith("sub ")) {
				String location = line.substring(4).trim();
				Fantasy.Subscribe sub = Fantasy.Subscribe.newBuilder()
						.setSubscriptionId(subscriptionId)
						.setLocation(location)
						.build();
				Fantasy.ControlRequest req = Fantasy.ControlRequest.newBuilder().setSub(sub).build();
				requestObserver.onNext(req);
				System.out.println("Sent subscribe to: " + location);

			} else if (line.startsWith("unsub ")) {
				String location = line.substring(6).trim();
				Fantasy.Unsubscribe unsub = Fantasy.Unsubscribe.newBuilder()
						.setSubscriptionId(subscriptionId)
						.setLocation(location)
						.build();
				Fantasy.ControlRequest req = Fantasy.ControlRequest.newBuilder().setUnsub(unsub).build();
				requestObserver.onNext(req);
				System.out.println("Sent unsubscribe from: " + location);

			} else {
				System.out.println("Unknown command");
			}
		}

		requestObserver.onCompleted();
		channel.shutdown();
		System.out.println("Client shutdown");
	}
}
