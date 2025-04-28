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
import io.grpc.StatusRuntimeException;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import io.grpc.stub.StreamObserver;

import sr.grpc.gen.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import weather.Weather;
import weather.WeatherSubscriberGrpc;


public class grpcClient 
{
	private static final Logger logger = Logger.getLogger(grpcClient.class.getName());

	private final Random _generator = new Random();

	private final ManagedChannel channel;

	private final WeatherSubscriberGrpc.WeatherSubscriberStub _weatherStub;
	private final FantasySubscriberGrpc.FantasySubscriberStub _fantasySubscriberStub;

	private final HashMap<Integer, WeatherExecutor> _weatherExecutors = new HashMap<>();
	private final HashMap<Integer, FantasyExecutor> _fantasyExecutors = new HashMap<>();


	/** Construct client connecting to HelloWorld server at {@code host:port}. */
	public grpcClient(String remoteHost, int remotePort)
	{
		channel = ManagedChannelBuilder.forAddress(remoteHost, remotePort)
				.usePlaintext() // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid needing certificates.
				.build();

		_weatherStub = WeatherSubscriberGrpc.newStub(channel);
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
		String[] parts = line.split(",");
		Fantasy.FantasySubscription.Builder builder = Fantasy.FantasySubscription.newBuilder()
				.setEventType(Fantasy.FantasyEventType.valueOf(parts[1]))
				.setLocation(parts[2])
				.setMinimumLevel(Integer.parseInt(parts[3]))
				.setMaximumLevel(Integer.parseInt(parts[4]));

		for(int i = 5; i < parts.length; i++) {
			builder.addFactions(parts[i]);
		}
		return builder.build();
	}

	private Weather.WeatherSubscription _GetWeatherParams(String line) {
		line = line.strip();
		String[] parts = line.split(",");
		Weather.WeatherSubscription.Builder builder = Weather.WeatherSubscription.newBuilder()
				.setLocation(parts[1]);
		return builder.build();
	}

	private WeatherExecutor _AddWeatherExecutor(Weather.WeatherSubscription params, WeatherSubscriberGrpc.WeatherSubscriberStub stub) {
		Integer id;
		do {
			id = _generator.nextInt();
		}while(_weatherExecutors.containsKey(id));
		_weatherExecutors.put(id, new WeatherExecutor(params, stub));
		System.out.println("Added weather executor: " + id);
		return _weatherExecutors.get(id);
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
		String[] parts = line.split(",");
		return Integer.parseInt(parts[1]);
	}


	public void test() throws InterruptedException
	{
		String line = null;
		java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));

		do 	{
			try	{
				System.out.print("==> ");
				System.out.flush();
				line = in.readLine();
				if(line.startsWith("/sub fantasy")){
					try{
						var params = _GetFantasyParams(line);
						var executor = _AddFantasyExecutor(params, _fantasySubscriberStub);
						executor.start();
					}
					catch(ArrayIndexOutOfBoundsException e){
						System.out.println("wrong num of parameters");
					}
				}
				else if(line.startsWith("/sub weather")){
					try{
						var params = _GetWeatherParams(line);
						var executor = _AddWeatherExecutor(params, _weatherStub);
						executor.start();
					}
					catch(ArrayIndexOutOfBoundsException e){
						System.out.println("wrong num of parameters");
					}
				}
				else if(line.startsWith("/unsub fantasy")){
					var key = _GetKey(line);
					if(_fantasyExecutors.containsKey(key)) {
						var executor = _fantasyExecutors.get(key);
						if(executor.isAlive()){
							executor.isStopped = true;
							wait(10);
						}
						System.out.println("Unsub fantasy: " + key);
					}
				}
				else if(line.startsWith("/unsub weather")){
					var key = _GetKey(line);
					if(_weatherExecutors.containsKey(key)) {
						var executor = _weatherExecutors.get(key);
						if(executor.isAlive()){
							executor.isStopped = true;
							wait(10);
						}
						System.out.println("Unsub fantasy: " + key);
					}
				}
				else if(line.equals("x") || line.equals("")) {
					continue;
				}
				else {
					System.out.println("???");
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

class WeatherExecutor extends Thread{
	boolean isStopped = false;
	WeatherSubscriberGrpc.WeatherSubscriberStub stub;
	Weather.WeatherSubscription params;

	WeatherExecutor(Weather.WeatherSubscription params, WeatherSubscriberGrpc.WeatherSubscriberStub stub)
	{
		this.params = params;
		this.stub = stub;
	}

	public void run()
	{
		System.out.println("Sending subscription request...");

		ClientResponseObserver<Weather.WeatherSubscription, Weather.WeatherEvent> responseObserver =
				new ClientResponseObserver<>() {
			private ClientCallStreamObserver<Weather.WeatherSubscription> requestStream;

			@Override
			public void beforeStart(ClientCallStreamObserver<Weather.WeatherSubscription> requestStream) {
				this.requestStream = requestStream;
			}

			@Override
			public void onNext(Weather.WeatherEvent event) {
				System.out.println("Received weather event: " + event);

				if (isStopped) {
					System.out.println("Cancelling from client...");
					requestStream.cancel("Client no longer interested", null);
				}
			}

			@Override
			public void onError(Throwable t) {
				Status status = Status.fromThrowable(t);
				if(status.getCode() == Status.Code.CANCELLED){
					System.out.println("Client was cancelled");
				}
				else{
					System.err.println("Client got error: " + Status.fromThrowable(t));
				}
			}

			@Override
			public void onCompleted() {
				System.out.println("Server finished sending primes.");
			}
		};

		stub.subscribe(params, responseObserver);
	}
}

class FantasyExecutor extends Thread{
	boolean isStopped = false;
	FantasySubscriberGrpc.FantasySubscriberStub stub;
	Fantasy.FantasySubscription params;

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
					private ClientCallStreamObserver<Fantasy.FantasySubscription> requestStream;

					@Override
					public void beforeStart(ClientCallStreamObserver<Fantasy.FantasySubscription> requestStream) {
						this.requestStream = requestStream;
					}

					@Override
					public void onNext(Fantasy.FantasyEvent event) {
						System.out.println("Received fantasy event: " + event);

						if (isStopped) {
							System.out.println("Cancelling from client...");
							requestStream.cancel("Client no longer interested", null);
						}
					}

					@Override
					public void onError(Throwable t) {
						Status status = Status.fromThrowable(t);
						if(status.getCode() == Status.Code.CANCELLED){
							System.out.println("Client was cancelled");
						}
						else{
							System.err.println("Client got error: " + Status.fromThrowable(t));
						}
					}

					@Override
					public void onCompleted() {
						System.out.println("Server finished sending primes.");
					}
				};

		stub.subscribe(params, responseObserver);
	}
}