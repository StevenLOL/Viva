package com.example.com.mica.viva.diacritic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

	private EditText txtContent;
	private Button btnSend;
	private TextView lblResult;
	private EditText txtServer;
	private EditText txtPort;

	private Handler receiveHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			lblResult.setText(msg.obj.toString());
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txtContent = (EditText) findViewById(R.id.txtContent);
		btnSend = (Button) findViewById(R.id.btnSend);
		lblResult = (TextView) findViewById(R.id.lblResult);
		txtServer = (EditText) findViewById(R.id.txtServer);
		txtPort = (EditText) findViewById(R.id.txtPort);	

		btnSend.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				SendReceiveSocketTask task = new SendReceiveSocketTask(
						txtServer.getText().toString(), 
						Integer.parseInt(txtPort.getText().toString()),
						receiveHandler, 
						txtContent.getText().toString());
				task.run();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
