package com.pakiet2.namespace;

import java.util.Timer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
//import android.widget.ImageView;
import android.widget.TextView;

public class KameraInternetowaActivity extends Activity implements
		OnClickListener {
	private Timer timer = new Timer();
	private boolean clicked = false;
	private MyServer Serwer;
	private static final int CAMERA_REQUEST = 1888;
    private ImageView iv;
	Context mycontext;

	// Thread t = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText("Kamera Internetowa wersja 1.0\n");

		ConnectivityManager conman = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean wifi = conman.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();
		TextView wifi_info = (TextView) findViewById(R.id.wifiInfo);
		if (wifi) {
			wifi_info.setText("Your WI-FI is ON\n Your IP is \n"
					+ MyServer.getLocalIpAddress());
		} else {
			wifi_info.setText("Your WI-FI is OFF");
		}

		Button bt = (Button) findViewById(R.id.button1);
		bt.setText("START");
		bt.setOnClickListener(this);

		Button bt2 = (Button) findViewById(R.id.button2);
		bt2.setText("TEST APARAT");
		bt2.setOnClickListener(this);
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
		if (v.getId() == R.id.button2) {
			Intent cameraIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(cameraIntent, CAMERA_REQUEST);
		}
	}

	/* Cykanie zdjecia */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_REQUEST) {
			if (resultCode == RESULT_OK) {
				/* sprawdzenie czy zdjecie jest cykane */
				if (data.hasExtra("data")) {
					/*
					 * if the data has an extra called "data" we assume the
					 * returned data is from the usual camera app
					 */

					// retrieve the bitmap from the intent
					Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

					// update the image view with the bitmap
					iv.setImageBitmap(thumbnail);
					/* wyswietlanie starego zdjecia */
				} else if (data.getExtras() == null) {
					/*
					 * if there are no extras we assume its the miui camera
					 * (which returns the path to the image in the returned
					 * data)
					 */
					Toast.makeText(getApplicationContext(),
							"No extras to retrieve!", Toast.LENGTH_SHORT)
							.show();
					// retrieve the path from the intent using
					// data.getData().getPath() and create a BitmapDrawable
					// using this path
					BitmapDrawable thumbnail = new BitmapDrawable(
							getResources(), data.getData().getPath());
					// update the image view with the newly created drawable
					iv.setImageDrawable(thumbnail);
				}
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(), "Cancelled",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

}