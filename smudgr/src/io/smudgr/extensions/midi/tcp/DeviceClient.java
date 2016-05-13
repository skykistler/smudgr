package io.smudgr.extensions.midi.tcp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;

import io.smudgr.extensions.midi.Device.DeviceObserver;

public class DeviceClient {

	public static final String CONNECT_RESPONSE = "connected";

	@SuppressWarnings("unused")
	private String ip;

	private DataInputStream serverStream;
	private Socket serverSocket;

	private ServerListener listener;

	private DeviceObserver observer;

	public DeviceClient(DeviceObserver observer) {
		this(observer, null);
	}

	public DeviceClient(DeviceObserver observer, String ip) {
		this.observer = observer;
		this.ip = ip;

		if (ip == null)
			attemptLocalServerFind();

		if (serverStream == null)
			return;

		listener = new ServerListener();
		listener.start();
	}

	private void attemptLocalServerFind() {
		String thisIp = null;
		try {
			thisIp = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		String searchSpace = thisIp.substring(0, thisIp.lastIndexOf('.') + 1);

		for (int i = 1; i < 255; i++) {
			String testIp = searchSpace + i;
			try {
				Socket testSocket = new Socket(testIp, DeviceServer.SMUDGR_PORT);

				BufferedReader in = new BufferedReader(new InputStreamReader(testSocket.getInputStream()));

				String received = in.readLine();

				if (received.equals(DeviceServer.CONNECT_HELLO)) {
					PrintWriter out = new PrintWriter(testSocket.getOutputStream(), true);
					out.println(CONNECT_RESPONSE);

					serverSocket = testSocket;
					serverStream = new DataInputStream(testSocket.getInputStream());
					ip = testIp;
					return;
				}
			} catch (IOException e) {
			}
		}
	}

	public void stop() {
		listener.stop();

		try {
			serverStream.close();
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class ServerListener implements Runnable {

		private volatile boolean listening;

		public void start() {
			listening = true;
			(new Thread(this)).start();
		}

		public void run() {
			byte[] message = new byte[64];
			int length = 0;

			try {
				while (listening && (length = serverStream.read(message)) != -1) {
					if (length > 0) {
						DeviceMessage midiMessage = new DeviceMessage(message, length);
						observer.midiInput(midiMessage);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void stop() {
			listening = false;
		}
	}

	public static void main(String[] args) {
		//		DeviceClient test = new DeviceClient(null);
	}

}
