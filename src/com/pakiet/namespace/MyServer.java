package com.pakiet.namespace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.StringTokenizer;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.pakiet.namespace.ContentType;
import com.pakiet.namespace.Utils;

public class MyServer implements Runnable {

	private Context mycontext;
	ServerSocket serversocket;

	private Socket clientsocket;
	private BufferedReader input;
	private OutputStream output;

	final static Handler mHandler = new Handler();
	boolean isRunning = false;
	private String host;

	MyServer(Context mycontext) {
		this.mycontext = mycontext;
		isRunning = true;
	}

	public void run() {

		try {
			host = getLocalIpAddress();
			int port = 8080;

			serversocket = new ServerSocket(port);
			serversocket.setReuseAddress(true);

			while (isRunning) {

				clientsocket = serversocket.accept();
				input = new BufferedReader(new InputStreamReader(
						clientsocket.getInputStream(), "ISO-8859-2"));
				output = clientsocket.getOutputStream();

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						Utils.hint(mycontext, "new client connected FROM "
								+ clientsocket.getInetAddress() + " "
								+ clientsocket.getPort());
					}
				});

				String sAll = getStringFromInput(input);
				final String header = sAll.split("\n")[0];

				StringTokenizer s = new StringTokenizer(header);
				String temp = s.nextToken();

				if (temp.equals("GET")) { // send picture if any

					String fileName = s.nextToken();
					String localfile = fileName.replace(host + "/", "")
							.replace("/", "");

					InputStream content = Utils.openFileFromAssets(localfile,
							mycontext);
					if (content != null) {
						send(content, ContentType.getContentType(localfile));
					}
				}


					send("<head>"
							+ "<meta http-equiv=\"Content-type\" value=\"text/html; charset=ISO-8859-2\">Kamera Klient "
							+ "</head><body>"
							+ "<div>Hello Android</div> "
							+ "</body>");

					closeInputOutput();

			}
		} catch (Exception ex) {
			Log.e("doInBackground Exception", " " + ex);
		}

		Log.e("out", "end");
	}

	void send(InputStream fis, String contenttype) {
		try {
			String header = "HTTP/1.1 200 OK\n" + "Content-type: "
					+ contenttype + "\n" + "Content-Length: " + fis.available()
					+ "\n" + "\n";

			output.write(header.getBytes());

			byte[] buffer = new byte[1024];
			int bytes = 0;

			while ((bytes = fis.read(buffer)) != -1) {
				output.write(buffer, 0, bytes);
			}

		} catch (Exception ex) {
			Log.e("exxx send", ex + "");
		}
	}

	void send(InputStream fis, String contenttype, OutputStream out,
			Socket socket) {
		try {
			String header = "HTTP/1.1 200 OK\n" + "Content-type: "
					+ contenttype + "\n" + "Content-Length: " + fis.available()
					+ "\n" + "\n";

			out.write(header.getBytes());

			byte[] buffer = new byte[1024];
			int bytes = 0;

			while ((bytes = fis.read(buffer)) != -1) {
				out.write(buffer, 0, bytes);
			}

			out.close();
			socket.close();

		} catch (Exception ex) {
			Log.e("exx send", ex + "");
		}
	}

	void send(String s) {
		String header = "HTTP/1.1 200 OK\n" + "Connection: close\n"
				+ "Content-type: text/html; charset=utf-8\n"
				+ "Content-Length: " + s.length() + "\n" + "\n";

		try {
			output.write((header + s).getBytes());
		} catch (Exception ex) {
			Log.e("ex send", ex + "");
		}
	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("ex getLocalIpAddress", ex.toString());
		}
		return null;
	}

	String getStringFromInput(BufferedReader input) {
		StringBuilder sb = new StringBuilder();
		String sTemp;
		try {
			while (!(sTemp = input.readLine()).equals("")) {
				sb.append(sTemp + "\n");
			}
		} catch (IOException e) {
			return "";
		}

		return sb.toString();
	}

	void closeConnections() {
		try {
			closeInputOutput();
			serversocket.close();
		} catch (Exception ex) {
			Log.e("err closeConnections", ex + "");
		}

		isRunning = false;
	}

	void closeInputOutput() {
		try {
			input.close();
			output.close();
			clientsocket.close();
		} catch (Exception ex) {
			Log.e("ex closeInputOutput", ex + "");
		}
	}

	public String getHost() {
		return host;
	}

}