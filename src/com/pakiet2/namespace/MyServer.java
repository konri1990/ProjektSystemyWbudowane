package com.pakiet2.namespace;

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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.StringTokenizer;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class MyServer implements Runnable {

	private Context mycontext;
	ServerSocket serversocket;

	private Socket clientsocket;
	private BufferedReader input;
	private OutputStream output;

	final static Handler mHandler = new Handler();
	boolean isRunning = false;
	private String host;
	//private static int licznikObrow = 0;
	InputStream contentTemp = null;
	OutputStream content2 = null;

	/**
	 * Kontruktor klasy, ustawienie obiektu klasy Context na bierzacy
	 * (android-serwer)
	 * 
	 * @param mycontext
	 *            Przyjmuje Obiekt klasy Context
	 */
	MyServer(Context mycontext) {
		this.mycontext = mycontext;
		isRunning = true;
	}

	/**
	 * Glowna metoda obslugi serwera
	 */
	public void run() {

		try {
			host = getLocalIpAddress();
			int port = 8080; // numer nasluchiwanego portu przez serwer

			serversocket = new ServerSocket(port);
			serversocket.setReuseAddress(true);
			// Glowna petla serwera, nas³uchiwanie portu
			while (isRunning) {

				clientsocket = serversocket.accept();
				input = new BufferedReader(new InputStreamReader(
						clientsocket.getInputStream(), "ISO-8859-2"));
				output = clientsocket.getOutputStream();

				// mHandler.post(new Runnable() {
				// @Override
				// public void run() {
				// Messages.hint(mycontext, "new client connected FROM "
				// + clientsocket.getInetAddress() + " "
				// + clientsocket.getPort());
				// }
				// });

				String sAll = getStringFromInput(input);
				final String header = sAll.split("\n")[0];

				StringTokenizer s = new StringTokenizer(header);
				String temp = s.nextToken();

				if (temp.equals("GET")) { // send picture if any

					String fileName = s.nextToken();
					String localfile = fileName.replace(this.getHost() + "/",
							"").replace("/", "");

					InputStream content = Utils.openFileFromAssets(localfile,
							mycontext);

					// InputStream content2 = Utils.data;
					// send(content2, "image/bmp");
					// if (content2 != null) {
					send(content, Utils.getContentType(localfile));
					// send(content2, "image/bmp");
					// }
				}

				if (header.equals("GET /obraz/klatka1.jpg HTTP/1.1")) {

					//if (licznikObrow < 1) {

						if (Utils.data != null) {
							content2 = Utils.data2;
							//contentTemp = Utils.data;
							Log.e("TOMASZ", "WYS£ANE BEZ POROWNANIA ");
							send(content2, "image/jpg");
						} else {
							Log.e("KONDZIO", "PUSTE");
						}
					/*	licznikObrow++;
					//} else {
						// content2 = Utils.data;
					//	if (isSame(contentTemp, content2)) {
							send(content2, "image/jpg");
							Log.e("TOMASZ", "POROWANIE " + licznikObrow
									+ "  PRAWID£OWE");
						} else {
							Log.e("TOMASZ", "POROWANIE " + licznikObrow
									+ " NIE OK");
							send(contentTemp, "image/jpg");
						}
						licznikObrow++;*/
					//}
				} else {

					send("<head>"
							+ "<link rel=\"stylesheet\" type=\"text/css\" "
							+ "href=\""
							+ this.getHost()
							+ "/css.css\" />"
							+ "<script  type=\"text/javascript\">"
							+ "var t; "
							+ "var timer_is_on=0;"
							+ "var c=0;"
							+ "function timedCount()"
							+ "{"
							+ "if(c%2==0){      "
							+ "    document.getElementById('obrazek').src=\""
							+ this.getHost()
							+ ":8080/obraz/klatka1.jpg\";"
							+ " }else{                "
							+ "       document.getElementById('obrazek').src=\""
							+ this.getHost()
							+ ":8080/obraz/klatka1.jpg\";"
							+ "   }"
							+ "c=c+1;"
							+ "  t=setTimeout(\"timedCount()\",100);"
							+ "}"
							+ "function doTimer()"
							+ "{"
							+ "    if (!timer_is_on)"
							+ "    {"
							+ "       timer_is_on=1;"
							+ "        timedCount();"
							+ "    }"
							+ "}"
							+ "function stopCount()"
							+ "{"
							+ "    clearTimeout(t);"
							+ "    timer_is_on=0;"
							+ "}"
							+ "</script>"
							+ "<meta http-equiv=\"Content-type\" value=\"text/html; charset=ISO-8859-2\"></head>"
							+ "<body  >"
							+ "<div id=\"page\">"
							+ "<div id=\"title\"> CAMERA IP v1.2</div>"
							+ "<div id=\"video\"> <img id='obrazek'  src='http://"
							+ getHost()
							+ ":8080/obraz/klatka1.jpg'/ /></div> "
							+ "<div id=\"copyright\">© Konrad Zapa³a, Tomasz Ziêbiec </div>"
							+ "</div></body>");

				}
				closeInputOutput();
			}
		} catch (Exception ex) {
			Log.e("doInBackground Exception", " " + ex);
		}

		Log.e("out", "end");
	}

	/**
	 * Metoda wysyla komunikat tekstowy do klienta
	 * 
	 * @param s
	 *            Tekst do wyslania
	 */
	private void send(String s) {
		String header = "HTTP/1.1 200 OK\n" + "Connection: close\n"
				+ "Content-type: text/html; charset=utf-8\n"
				+ "Content-Length: " + s.length() + "\n" + "\n";

		try {
			output.write((header + s).getBytes());
		} catch (Exception ex) {
			Log.e("ex send", ex + "");
		}
	}

	/**
	 * Metoda wysyla obrazek do klienta, czyta bajty pliku
	 * 
	 * @param fis
	 *            Strumien wejsciowy, obiekt typu InputStream
	 * @param contenttype
	 *            Content type czyli naglowek strony jako tekst
	 */
	void send(InputStream fis, String contenttype) {
		try {
			String header = "HTTP/1.1 200 OK\n" + "Content-type: "
					+ contenttype + "\n" + "Content-Length: " + fis.available()
					+ "\n" + "\n";

			output.write(header.getBytes());

			byte[] buffer = new byte[1024];
			//byte[] buffer = new byte[400000];
			int bytes = 0;

			while ((bytes = fis.read(buffer)) != -1) {
				output.write(buffer, 0, bytes);
			}

		} catch (Exception ex) {
			Log.e("exxx send", ex + "");
		}
	}
	
	void send(OutputStream os, String contenttype) {
		try {
			String header = "HTTP/1.1 200 OK\n" + "Content-type: "
					+ contenttype + "\n" + "Content-Length: " //fis.available()
					+ "\n" + "\n";

			output.write(header.getBytes());

			//byte[] buffer = new byte[1024];
			//byte[] buffer = new byte[400000];
			//int bytes = 0;

			//while ((bytes = fis.read(buffer)) != -1) {
				//output.write(buffer, 0, bytes);
			//}
			output = os;

		} catch (Exception ex) {
			Log.e("exxx send", ex + "");
		}
	}

	/**
	 * Metoda zwraca bierzacy adres ip serwera
	 * 
	 * @return Zmienna typu string, ip serwera
	 */
	public static String getLocalIpAddress() {
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

	/**
	 * Metoda odbiera tekst z otwartego polaczenia z klientem
	 * 
	 * @param input
	 *            BufferedReader
	 * @return Odczytany tekst od klienta
	 */
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

	/**
	 * Metoda zamyka bezpiecznie serwer
	 */
	void closeConnections() {
		try {
			closeInputOutput();
			serversocket.close();
		} catch (Exception ex) {
			Log.e("err closeConnections", ex + "");
		}

		isRunning = false;
	}

	/**
	 * Metoda bezpiecznie odlacza klientow od serwera
	 */
	void closeInputOutput() {
		try {
			input.close();
			output.close();
			clientsocket.close();
		} catch (Exception ex) {
			Log.e("ex closeInputOutput", ex + "");
		}
	}

	/**
	 * Metoda zwraca Hosta
	 * 
	 * @return Host jako zmienna typu String
	 */
	public String getHost() {
		return host;
	}

	public static boolean isSame(InputStream input1, InputStream input2)
			throws IOException {
		boolean error = false;
		try {
			byte[] buffer1 = new byte[1024];
			byte[] buffer2 = new byte[1024];
			try {
				int numRead1 = 0;
				int numRead2 = 0;
				while (true) {
					numRead1 = input1.read(buffer1);
					numRead2 = input2.read(buffer2);
					if (numRead1 > -1) {
						if (numRead2 != numRead1)
							return false;
						// Otherwise same number of bytes read
						if (!Arrays.equals(buffer1, buffer2))
							return false;
						// Otherwise same bytes read, so continue ...
					} else {
						// Nothing more in stream 1 ...
						return numRead2 < 0;
					}
				}
			} finally {
				input1.close();
			}
		} catch (IOException e) {
			error = true; // this error should be thrown, even if there is an
							// error closing stream 2
			throw e;
		} catch (RuntimeException e) {
			error = true; // this error should be thrown, even if there is an
							// error closing stream 2
			throw e;
		} finally {
			try {
				input2.close();
			} catch (IOException e) {
				if (!error)
					throw e;
			}
		}
	}
}