package com.pakiet2.namespace;

import java.io.InputStream;

import android.content.Context;
import android.widget.Toast;

public class Messages {

	/**
	 * Metoda wysyla komunikat po stronie serwera kto zostal polaczony
	 * @param mycontext Obiekt klasy Context (serwer Andoid).
	 * @param s Tekst do wyswietlenia
	 */
	public static void hint(final Context mycontext, final String s) {
		Toast toast = Toast.makeText(mycontext, s, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public static InputStream openFileFromAssets(String spath, Context mycontext) {
		try {
			InputStream is = mycontext.getResources().getAssets().open(spath);
			return is;
		} catch (Exception ex) {
			return null;
		}
	}
}
