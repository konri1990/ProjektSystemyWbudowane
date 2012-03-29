package com.pakiet.namespace;

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

}
