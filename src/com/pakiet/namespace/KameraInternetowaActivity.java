package com.pakiet.namespace;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class KameraInternetowaActivity extends Activity {
	/** Called when the activity is first created. */
	EditText input;
	TextView output;
	Button buttonClick;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		input = (EditText) findViewById(R.id.EditText01);
		output = (TextView) findViewById(R.id.TextView01);
		buttonClick = (Button) findViewById(R.id.Button01);
		buttonClick.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				display(input.getText());
			}
		});
	}

	protected void display(Editable text) {
		output.setText(text);
	}
}