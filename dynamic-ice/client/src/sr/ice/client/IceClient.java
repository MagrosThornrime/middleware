package sr.ice.client;

import com.zeroc.Ice.*;
import com.zeroc.Ice.Object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Exception;
import java.util.Arrays;
import java.util.Objects;

public class IceClient {

	static ObjectPrx base;

	public static void main(String[] args) {
		int status = 0;
		Communicator communicator = null;

		try {
			communicator = Util.initialize(args);
			base = communicator.stringToProxy("devices/printer:tcp -h 127.0.0.2 -p 10000 -z : udp -h 127.0.0.2 -p 10000 -z"); //opcja -z włącza możliwość kompresji wiadomości

			if (base == null) throw new Error("Invalid proxy");

			String line = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

			String[] printedStrings;

			do {
				try {
					System.out.print("==> ");
					line = in.readLine();
					switch (line) {
						case "duplicated":
							String[] strings = new String[]{"bajo", "jajo", "kłaczki"};
							var count = 3;
							printedStrings = printDuplicates(strings, count);
							System.out.println("RESULT = " + Arrays.toString(printedStrings));
							break;
						case "range":
							printedStrings = printRange(3, 15, 4);
							System.out.println("RESULT = " + Arrays.toString(printedStrings));
							break;
						case "fibonacci":
							printedStrings = printFibonacci(4);
							System.out.println("RESULT = " + Arrays.toString(printedStrings));
							break;
						case "description":
							System.out.println("RESULT = " + getDescription());
							break;
						case "printed":
							System.out.println("RESULT = " + getPrinted());
							break;
						case "x":
							break;
						default:
							System.out.println("Wrong command");
					}
				} catch (IOException | TwowayOnlyException | UnknownException ex) {
					ex.printStackTrace(System.err);
				}
			}
			while (!Objects.equals(line, "x"));


		} catch (LocalException e) {
			e.printStackTrace();
			status = 1;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			status = 1;
		}
		if (communicator != null) {
			try {
				communicator.destroy();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				status = 1;
			}
		}
		System.exit(status);
	}

	private static String[] printDuplicates(String[] strings, int count) {
		OutputStream outStream = new OutputStream(base.ice_getCommunicator());
		outStream.startEncapsulation();
		outStream.writeStringSeq(strings);
		outStream.writeInt(count);
		outStream.endEncapsulation();

		try {
			Object.Ice_invokeResult result = base.ice_invoke("printDuplicated", OperationMode.Normal, outStream.finished());
			InputStream inStream = new InputStream(base.ice_getCommunicator(), result.outParams);
			inStream.startEncapsulation();
			var returnValue = inStream.readStringSeq();
			inStream.endEncapsulation();
			return returnValue;
		} catch (Exception e) {
			throw new RuntimeException("Server returned an error during 'printDuplicated' operation: " + e);
		}
	}

	private static String[] printRange(int startNum, int endNum, int step){
		OutputStream outStream = new OutputStream(base.ice_getCommunicator());
		outStream.startEncapsulation();
		outStream.writeInt(startNum);
		outStream.writeInt(endNum);
		outStream.writeInt(step);
		outStream.endEncapsulation();

		try {
			Object.Ice_invokeResult result = base.ice_invoke("printRange", OperationMode.Normal, outStream.finished());
			InputStream inStream = new InputStream(base.ice_getCommunicator(), result.outParams);
			inStream.startEncapsulation();
			var returnValue = inStream.readStringSeq();
			inStream.endEncapsulation();
			return returnValue;
		} catch (Exception e) {
			throw new RuntimeException("Server returned an error during 'printRange' operation: " + e);
		}
	}

	private static String[] printFibonacci(int count){
		OutputStream outStream = new OutputStream(base.ice_getCommunicator());
		outStream.startEncapsulation();
		outStream.writeInt(count);
		outStream.endEncapsulation();

		try {
			Object.Ice_invokeResult result = base.ice_invoke("printFibonacci", OperationMode.Normal, outStream.finished());
			InputStream inStream = new InputStream(base.ice_getCommunicator(), result.outParams);
			inStream.startEncapsulation();
			var returnValue = inStream.readStringSeq();
			inStream.endEncapsulation();
			return returnValue;
		} catch (Exception e) {
			throw new RuntimeException("Server returned an error during 'printFibonacci' operation: " + e);
		}
	}

	private static String getDescription() {
		OutputStream outStream = new OutputStream(base.ice_getCommunicator());
		outStream.startEncapsulation();
		outStream.endEncapsulation();

		try {
			Object.Ice_invokeResult result = base.ice_invoke("getDescription", OperationMode.Idempotent, outStream.finished());
			InputStream inStream = new InputStream(base.ice_getCommunicator(), result.outParams);
			inStream.startEncapsulation();
			String returnValue = inStream.readString();
			inStream.endEncapsulation();
			return returnValue;
		} catch (Exception e) {
			throw new RuntimeException("Server returned an error for 'getDescription' operation: " + e);
		}
	}

	private static int getPrinted() {
		OutputStream outStream = new OutputStream(base.ice_getCommunicator());
		outStream.startEncapsulation();
		outStream.endEncapsulation();

		try {
			Object.Ice_invokeResult result = base.ice_invoke("getPrintedStringsNum", OperationMode.Idempotent, outStream.finished());
			InputStream inStream = new InputStream(base.ice_getCommunicator(), result.outParams);
			inStream.startEncapsulation();
			var returnValue = inStream.readInt();
			inStream.endEncapsulation();
			return returnValue;
		} catch (Exception e) {
			throw new RuntimeException("Server returned an error for 'getPrinted' operation: " + e);
		}
	}
}