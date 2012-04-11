package com.pakiet2.namespace;

import java.util.Timer;

import com.pakiet2.namespace.R;

import android.app.Activity;
import android.content.Context;

import android.net.ConnectivityManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import android.widget.Button;

import android.widget.TextView;

public class KameraInternetowaActivity extends Activity implements
		OnClickListener {
	private Timer timer = new Timer();
	private boolean clicked = false;
	private MyServer Serwer;

	// Thread t = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText("Kamera Internetowa wersja 1.0\n");
		
		
		ConnectivityManager conman = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean wifi = conman.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		TextView wifi_info = (TextView) findViewById(R.id.wifiInfo);
		if(wifi){
			wifi_info.setText("Your WI-FI is ON\n Your IP is \n" + MyServer.getLocalIpAddress());
		}
		else{
			wifi_info.setText("Your WI-FI is OFF");
		}
		

		Button bt = (Button) findViewById(R.id.button1);
		bt.setText("START");
		bt.setOnClickListener(this);
	}

	/**
	 * Zamkniecie serwera 
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();

		if (timer != null)
			timer.cancel();

		try {
			if (Serwer.serversocket != null) {
				Serwer.closeConnections();
			} else {
				Log.e("out", "serversocket null");
			}
		} catch (Exception ex) {
			Log.e("ex", "" + ex);
		}
	}

	/**
	 * Obsluga zdarzenia onClick buttona
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button1) {
			if (!clicked) {
				Button bt = (Button) findViewById(R.id.button1);
				bt.setText("STOP");
				clicked = true;
				new Thread(Serwer = new MyServer(this)).start();
			} else {
				Button bt = (Button) findViewById(R.id.button1);
				bt.setText("START");
				clicked = false;
				Serwer.closeConnections();
			}
		}

	}
}