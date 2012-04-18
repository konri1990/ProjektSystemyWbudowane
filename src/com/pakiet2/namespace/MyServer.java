package com.pakiet2.namespace;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

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

	/**
	 * Kontruktor klasy, ustawienie obiektu klasy Context na bierzacy (android-serwer)
	 * @param mycontext Przyjmuje Obiekt klasy Context
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

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						Messages.hint(mycontext, "new client connected FROM "
								+ clientsocket.getInetAddress() + " "
								+ clientsocket.getPort());
					}
				});
				
	            String sAll			= getStringFromInput(input);
                final String header	= sAll.split("\n")[0];
				
              	if (header.equals("GET /secondpage HTTP/1.1")) {
            		String js="";
            		
                	send("<head>" +
                			"<link rel=\"stylesheet\" type=\"text/css\" " +
                			"href=\""+host+"/css.css\" />" +
                	
                			"<meta http-equiv=\"Content-type\" value=\"text/html; charset=ISO-8859-2\">Second page " +
                			"<img src='"+getHost()+"/ic.png'>" +
                			"<br><a href='/'>back</a>" +
                			"<br><a href='/secondpage'>secondpage</a>" +
                			"<br><a href='/takepicture'>take picture</a>" +
                			"<div id='asd' style='clear:left'></div> "+
                			js+"</head>");
                	
                	closeInputOutput();
            	} else {
                send();
            	}
				closeInputOutput();

			}
		} catch (Exception ex) {
			Log.e("doInBackground Exception", " " + ex);
		}

		Log.e("out", "end");
	}

	private void send(String s) {
	    String header=
	    		"HTTP/1.1 200 OK\n" +
	    		"Connection: close\n"+
	    		"Content-type: text/html; charset=utf-8\n"+
	    		"Content-Length: "+s.length()+"\n" +
	    		"\n";

	    try {
	    	output.write((header+s).getBytes());
	    } catch (Exception ex) {
	    	Log.e("ex send", ex+"");
	    }
	}

	/**
	 * Metoda wysyla komunikat do klienta
	 */
	void send() {
		String s = "<head>" +
				"<link rel=\"stylesheet\" type=\"text/css\" "
    			+ "href=\""+ this.getHost() +"/css.css\" />"
				+ "<meta http-equiv=\"Content-type\" value=\"text/html; charset=ISO-8859-2\">Kamera Klient "
				+ "</head><body>" + "<div>Hello Android2</div> " + "<img src='" + this.getHost() + "/ic.png' /></body>";
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

	String getStringFromInput(BufferedReader input) {
        StringBuilder sb = new StringBuilder();
        String sTemp; 
        try {
			while (!(sTemp = input.readLine()).equals(""))  {
				sb.append(sTemp+"\n");
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

}