package com.pakiet2.namespace;

import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;

public class Utils {
	
	public static InputStream data;
	public static OutputStream data2;
	/**
	 * Metoda pozwala przygotowac obrazki do wyslania (zlokalizownaie obrazka)
	 * 
	 * @param spath
	 *            sciezka do pliku jako string
	 * @param mycontext
	 *            Context wywolania metody
	 * @return obiekt typu InpuStream przygotowany do odczytania
	 */
	public static InputStream openFileFromAssets(String spath, Context mycontext) {
		try {
			InputStream is = mycontext.getResources().getAssets().open(spath);
			//Log.e("TOMASZ:", spath);
			return is;
		} catch (Exception ex) {
			return null;
		}
	}

	
	public static void openFileFromTmp(InputStream loadIs) {
		try {
			
			data = loadIs;
			//Log.e("KONDZIO", "Ala ");
			//Log.e("KONDZIO", "Ala " + data.toString());
			//return data;
		} catch (Exception ex) {
			Log.e("UTILS", "Problem z wczytaniem");
			data = null;
		}
	}

	/**
	 * Metoda ta pozwala rozpoznac jaki typ pliku bedzie wysylany (potrzebne do
	 * Content type) dla przegladarki HTTP
	 * 
	 * @param fileName
	 *            Pe³na nazwa pliku
	 * @return Zwraca stringa z odpowiednim ustawieniem Content Type
	 */
	public static String getContentType(String fileName) {
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")
				|| fileName.endsWith(".txt")) {
			return "text/html";
		} else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
			return "image/jpeg";
		} else if (fileName.endsWith(".gif")) {
			return "image/gif";
		} else if (fileName.endsWith(".png")) {
			return "image/png";
		} else if (fileName.endsWith(".css")) {
			return "text/css";
		} else {
			return "application/octet-stream";
		}
	}
}
