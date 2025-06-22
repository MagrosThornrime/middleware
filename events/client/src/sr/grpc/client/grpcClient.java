/*
 * Copyright 2015, Google Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *    * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *
 *    * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package sr.grpc.client;

import fantasy.Fantasy;
import fantasy.FantasySubscriberGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;


import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class grpcClient 
{
	private final Random _generator = new Random();

	private final ManagedChannel channel;

	private final FantasySubscriberGrpc.FantasySubscriberStub _fantasySubscriberStub;

	private final HashMap<Integer, FantasyExecutor> _fantasyExecutors = new HashMap<>();


	/** Construct client connecting to HelloWorld server at {@code host:port}. */
	public grpcClient(String remoteHost, int remotePort)
	{
		channel = ManagedChannelBuilder.forAddress(remoteHost, remotePort)
				.usePlaintext() // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid needing certificates.
				.build();

		_fantasySubscriberStub = FantasySubscriberGrpc.newStub(channel);
	}
	
	

	public static void main(String[] args) throws Exception 
	{
		grpcClient client = new grpcClient("127.0.0.5", 50051);
		client.test();
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	private Fantasy.FantasySubscription _GetFantasyParams(String line) {
		line = line.strip();
		String[] parts = line.split(";");
		Fantasy.FantasySubscription.Builder builder = Fantasy.FantasySubscription.newBuilder()
				.setEventType(Fantasy.FantasyEventType.valueOf(parts[1]))
				.setLocation(parts[2])
				.setMinimumLevel(Integer.parseInt(parts[3]))
				.setMaximumLevel(Integer.parseInt(parts[4]));

		return builder.build();
	}


	private FantasyExecutor _AddFantasyExecutor(Fantasy.FantasySubscription params, FantasySubscriberGrpc.FantasySubscriberStub stub) {
		Integer id;
		do {
			id = _generator.nextInt();
		}while(_fantasyExecutors.containsKey(id));
		_fantasyExecutors.put(id, new FantasyExecutor(params, stub));
		System.out.println("Added fantasy executor: " + id);
		return _fantasyExecutors.get(id);
	}

	private Integer _GetKey(String line) {
		line = line.strip();
		String[] parts = line.split(";");
		return Integer.parseInt(parts[1]);
	}


	public void test() throws InterruptedException {
		String line = null;
		java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));

		do 	{
			try	{
				System.out.print("==> ");
				System.out.flush();
				line = in.readLine();
				if(line.startsWith("sub")){
					try{
						var params = _GetFantasyParams(line);
						var executor = _AddFantasyExecutor(params, _fantasySubscriberStub);
						executor.start();
					}
					catch(ArrayIndexOutOfBoundsException e){
						System.out.println("Wrong num of parameters");
					}
				}
				else if(line.startsWith("unsub")){
					var key = _GetKey(line);
					if(_fantasyExecutors.containsKey(key)) {
						var executor = _fantasyExecutors.get(key);
						executor.cancelStream();
						Thread.sleep(10);
						_fantasyExecutors.remove(key);
					}
				}
				else if(!(line.equals("x") || line.isEmpty())){
					System.out.println("Wrong command");
				}
			}
			catch (java.io.IOException ex) {
				System.err.println(ex);
			}
		}
		while (!Objects.equals(line, "x"));
		
		shutdown();
	}
}

class FantasyExecutor extends Thread{
	FantasySubscriberGrpc.FantasySubscriberStub stub;
	Fantasy.FantasySubscription params;
	volatile ClientCallStreamObserver<Fantasy.FantasySubscription> requestStream;

	FantasyExecutor(Fantasy.FantasySubscription params, FantasySubscriberGrpc.FantasySubscriberStub stub)
	{
		this.params = params;
		this.stub = stub;
	}

	public void run()
	{
		System.out.println("Sending subscription request...");

		ClientResponseObserver<Fantasy.FantasySubscription, Fantasy.FantasyEvent> responseObserver =
				new ClientResponseObserver<>() {

					@Override
					public void beforeStart(ClientCallStreamObserver<Fantasy.FantasySubscription> requestStream) {
						FantasyExecutor.this.requestStream = requestStream;
					}

					@Override
					public void onNext(Fantasy.FantasyEvent event) {
						System.out.println("Received fantasy event:");
						System.out.println("Event type: " + event.getType().getEventType());
						System.out.println("Location: " + event.getType().getLocation());
						System.out.println("Min level: " + event.getType().getMinimumLevel());
						System.out.println("Max level: " + event.getType().getMaximumLevel());
						System.out.println("Factions: " + event.getFactionsList());
						System.out.println("Description: " + event.getDescription());
						System.out.println();

					}

					@Override
					public void onError(Throwable t) {
						Status status = Status.fromThrowable(t);
						if (status.getCode() == Status.Code.CANCELLED) {
							System.out.println("Subscription cancelled");
						} else {
							System.err.println("Client got error: " + status);
						}
					}

					@Override
					public void onCompleted() {
						System.out.println("Server finished.");
					}
				};

		stub.subscribe(params, responseObserver);
	}

	public void cancelStream() {
		if (requestStream != null) {
			requestStream.cancel("Manual cancel", null);
		}
	}
}