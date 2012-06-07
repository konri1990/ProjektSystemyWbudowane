package com.pakiet2.namespace;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class KameraInternetowaActivity extends Activity implements
		OnClickListener, SurfaceHolder.Callback, Camera.PictureCallback {
	private Timer timer = new Timer();
	private boolean clicked = false;
	private MyServer Serwer;
	Camera mCamera;
	private ImageView iv;
	Context mycontext;

	// Thread t;
	// a variable to store a reference to the Surface View at the main.xml file
	private SurfaceView sv;

	// a bitmap to display the captured image
	private Bitmap bmp;
	private Button bt2;
	// Camera variables
	// a surface holder
	private SurfaceHolder sHolder;
	// the camera parameters
	private Parameters parameters;
	Handler timerUpdateHandler;
	boolean timelapseRunning = false;
	int currentTime = 0;
	final int SECONDS_BETWEEN_PHOTOS = 1;

	// Thread t = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setText("Kamera Internetowa wersja 1.0\n");
		iv = (ImageView) findViewById(R.id.imageView1);

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
		// t = new Thread(this);
		// get the Surface View at the main.xml file
		sv = (SurfaceView) findViewById(R.id.surfaceView1);

		// Get a surface
		sHolder = sv.getHolder();
		// tells Android that this surface will have its data constantly
		// replaced
		sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// add the callback interface methods defined below as the Surface View
		// callbacks
		sHolder.addCallback(this);

		Button bt = (Button) findViewById(R.id.button1);
		bt.setText("START");
		bt.setOnClickListener(this);

		bt2 = (Button) findViewById(R.id.button2);
		bt2.setText("TEST APARAT");
		bt2.setOnClickListener(this);
		timerUpdateHandler = new Handler();
	}

	private Runnable timerUpdateTask = new Runnable() {
		public void run() {
			if (currentTime < SECONDS_BETWEEN_PHOTOS) {
				currentTime++;
			} else {
				mCamera.takePicture(null, null, null,
						KameraInternetowaActivity.this);
				currentTime = 0;
			}
			//set the time between next photo taken
			timerUpdateHandler.postDelayed(timerUpdateTask, 50);
		}
	};

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
				bt.setText("STOP SERVER");
				clicked = true;
				new Thread(Serwer = new MyServer(this)).start();
			} else {
				Button bt = (Button) findViewById(R.id.button1);
				bt.setText("START SERVER");
				clicked = false;
				Serwer.closeConnections();
			}
		}
		if (v.getId() == R.id.button2) {

			if (!timelapseRunning) {
				bt2.setText("TEST APARAT 1");
				timelapseRunning = true;
				timerUpdateHandler.post(timerUpdateTask);
			} else {
				bt2.setText("TEST APARAT 2");
				timelapseRunning = false;
				timerUpdateHandler.removeCallbacks(timerUpdateTask);
			}
			// Intent cameraIntent = new Intent(
			// android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			// startActivityForResult(cameraIntent, CAMERA_REQUEST);

		}
	}

	/* Cykanie zdjecia *//*
						 * protected void onActivityResult(int requestCode, int
						 * resultCode, Intent data) {
						 * super.onActivityResult(requestCode, resultCode,
						 * data); if (requestCode == CAMERA_REQUEST) { if
						 * (resultCode == RESULT_OK) { Bitmap bm = (Bitmap)
						 * data.getExtras().get("data"); iv.setImageBitmap(bm);
						 * } else if (resultCode == RESULT_CANCELED) {
						 * Toast.makeText(getApplicationContext(), "Cancelled",
						 * Toast.LENGTH_SHORT).show(); }
						 * 
						 * } }
						 */

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// get camera parameters
		// parameters = mCamera.getParameters();
		// parameters.setPictureSize(200, 200);
		// set camera parameters
		// mCamera.setParameters(parameters);
		mCamera.startPreview();
		/*
		 * Camera.PictureCallback mCall = new Camera.PictureCallback() {
		 * 
		 * @Override public void onPictureTaken(byte[] data, Camera camera) { //
		 * decode the data obtained by the camera into a Bitmap InputStream is =
		 * new ByteArrayInputStream(data); Log.e("KONDZIO",
		 * Integer.toString(data.length)); Utils.openFileFromTmp(is); bmp =
		 * BitmapFactory.decodeByteArray(data, 0, data.length); // set the
		 * iv_image
		 * 
		 * iv.setImageBitmap(bmp); } };
		 * 
		 * mCamera.takePicture(null, null, mCall);
		 */
		// t.start();
		// mCamera.startPreview();

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, acquire the camera and tell it where
		// to draw the preview.
		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(holder);
			parameters = mCamera.getParameters();
			if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
				parameters.set("orientation", "portrait");
			}
			mCamera.setParameters(parameters);

		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// stop the preview
		mCamera.stopPreview();
		// release the camera
		mCamera.release();
		// unbind the camera from this object
		mCamera = null;

	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		InputStream is = new ByteArrayInputStream(data);
		Log.e("KONDZIO", Integer.toString(data.length));
		Utils.openFileFromTmp(is);
		bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
		// set the iv_image

		iv.setImageBitmap(bmp);
		// camera.startPreview();
	}

	/*
	 * @Override public void run() { // TODO Auto-generated method stub for (int
	 * i = 0; i < 10; i++) { // sets what code should be executed after the
	 * picture is taken Camera.PictureCallback mCall = new
	 * Camera.PictureCallback() {
	 * 
	 * @Override public void onPictureTaken(byte[] data, Camera camera) { //
	 * decode the data obtained by the camera into a Bitmap bmp =
	 * BitmapFactory.decodeByteArray(data, 0, data.length); // set the iv_image
	 * iv.setImageBitmap(bmp); } }; mCamera.takePicture(null, null, mCall);
	 * 
	 * try { Thread.sleep(3000); } catch (InterruptedException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } } try { t.join(); }
	 * catch (InterruptedException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 */

}