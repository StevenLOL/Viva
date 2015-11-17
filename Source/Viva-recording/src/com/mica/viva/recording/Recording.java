package com.mica.viva.recording;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import com.mica.viva.recording.R;
import com.mica.viva.utility.ExtAudioRecorder;

import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder.AudioSource;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Recording extends Activity {

	// private controls
	private Button startButton;
	private Button saveButton;
	private Button playButton;
	private Button nextButton;

	private TextView contentTextView;
	private TextView timerTextView;
	private TextView metaTextView;

	// private properties
	private boolean isRecording = false;
	private ExtAudioRecorder recorder;
	private String outputFilePath = null;
	private SharedPreferences preferences;
	private ArrayList<String> listSentences;
	private int currentSentenceIndex = 0;
	private Timer counter;
	private int count;
	private OnSharedPreferenceChangeListener preferencesListener;

	// configurations
	private String SPEAKER_NAME = "anonymouse";
	private String SPEAKER_AGE = "30";
	private String SPEAKER_GENDER = "male";
	private String SPEAKER_LOCATION = "Hà Nội";
	private String TOPIC = "message";
	private static int RECORDER_SAMPLERATE = 44100;
	private static int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	private Hashtable<String, Integer> START_INDEXS = new Hashtable<String, Integer>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recording);

		startButton = (Button) findViewById(R.id.startButton);
		saveButton = (Button) findViewById(R.id.saveButton);
		playButton = (Button) findViewById(R.id.playButton);
		nextButton = (Button) findViewById(R.id.nextButton);

		contentTextView = (TextView) findViewById(R.id.contentTextView);
		timerTextView = (TextView) findViewById(R.id.timerTextView);
		metaTextView = (TextView) findViewById(R.id.metaTextView);

		startButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (!isRecording) {
					// start recording
					startRecording();
				} else {
					// stop recording
					stopRecording();
				}
			}
		});

		saveButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				saveRecord();
			}
		});
		playButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				playRecord();
			}
		});
		nextButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// nextSentence();
				saveRecord();
			}
		});

		// preferences
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferencesListener = new OnSharedPreferenceChangeListener() {
			// @Override
			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				Log.i("Refereces", "changed");
				setParamsForRecord();
			}
		};
		preferences
				.registerOnSharedPreferenceChangeListener(preferencesListener);

		setParamsForRecord();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_recording, menu);
		return true;
	}

	// Set menu Settings
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingActivity.class));
			// preferences.registerOnSharedPreferenceChangeListener(listener);
			return true;
		}
		return false;
	}

	// private methods
	private void startRecording() {
		// clear temp file
		clearTempRecord();
		getOutPutFilePath();

		recorder = new ExtAudioRecorder(true, AudioSource.MIC,
				RECORDER_SAMPLERATE, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				RECORDER_AUDIO_ENCODING);

		recorder.setOutputFile(outputFilePath + ".wav");
		recorder.prepare();
		recorder.start();
		isRecording = true;

		// UI
		startTimeCounter();
		startButton.setText("Stop");
		saveButton.setEnabled(false);
		playButton.setEnabled(false);
		nextButton.setEnabled(false);

		//Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();

		// debug
		Log.i("StartRecording", RECORDER_AUDIO_ENCODING + " - "
				+ RECORDER_SAMPLERATE);
	}

	private void stopRecording() {
		isRecording = false;
		recorder.stop();
		recorder.release();

		// UI
		counter.cancel();
		startButton.setText("Re-Record");
		saveButton.setEnabled(true);
		playButton.setEnabled(true);
		nextButton.setEnabled(true);
		//Toast.makeText(this, "Recording stoped", Toast.LENGTH_SHORT).show();
	}

	private void playRecord() {
		final MediaPlayer mp = new MediaPlayer();
		mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

			public void onCompletion(MediaPlayer arg0) {
				startButton.setEnabled(true);
				playButton.setEnabled(true);
				nextButton.setEnabled(true);
				mp.release();
			}
		});

		try {
			mp.setDataSource(outputFilePath + ".wav");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mp.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mp.start();
		startButton.setEnabled(false);
		playButton.setEnabled(false);
		nextButton.setEnabled(false);
	}

	private void saveRecord() {
		writeTextFile();
		// UI
		saveButton.setEnabled(false);
		Toast.makeText(this, "Save record success!", Toast.LENGTH_SHORT).show();
		// next
		nextSentence();
		startRecording();
	}

	private void nextSentence() {
		if (listSentences.size() > 0) {
			currentSentenceIndex = (listSentences.size() > currentSentenceIndex + 1) ? currentSentenceIndex + 1
					: 0;
			contentTextView.setText(listSentences.get(currentSentenceIndex));
			START_INDEXS.put(TOPIC, currentSentenceIndex);
			metaTextView.setText("Topic: " + TOPIC + " - Câu: "
					+ (currentSentenceIndex + 1) + "/" + listSentences.size());
		}
	}

	private void setParamsForRecord() {

		SPEAKER_NAME = preferences.getString("SPEAKER_NAME", "anonymouse");
		SPEAKER_AGE = preferences.getString("SPEAKER_AGE", "30");
		SPEAKER_GENDER = preferences.getString("SPEAKER_GENDER", "male");
		SPEAKER_LOCATION = preferences.getString("SPEAKER_LOCATION", "Hà Nội");
		TOPIC = preferences.getString("TOPIC", "message");

		RECORDER_SAMPLERATE = Integer.parseInt(preferences.getString(
				"SAMPLE_RATE", "44100"));

		String bitRate = preferences.getString("BIT", "16");
		if (bitRate.equals("16")) {
			RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
		} else if (bitRate.equals("8")) {
			RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_8BIT;
		} else if (bitRate.equals("-1")) {
			RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_DEFAULT;
		}

		readSentences();

		// get start positions of text
		String str = preferences.getString("START_INDEXS", "0,0,0,0,0,0");
		String[] startIndexs = str.split(",");
		if (startIndexs.length < 6) {
			startIndexs = new String[] { "0", "0", "0", "0", "0", "0" };
		}
		START_INDEXS.put("message", Integer.parseInt(startIndexs[0]));
		START_INDEXS.put("weather", Integer.parseInt(startIndexs[1]));
		START_INDEXS.put("conversation", Integer.parseInt(startIndexs[2]));
		START_INDEXS.put("information", Integer.parseInt(startIndexs[3]));
		START_INDEXS.put("number", Integer.parseInt(startIndexs[4]));
		START_INDEXS.put("name", Integer.parseInt(startIndexs[5]));

		// Display sentence
		if (listSentences.size() > 0) {
			currentSentenceIndex = listSentences.size() > START_INDEXS
					.get(TOPIC) ? START_INDEXS.get(TOPIC) : 0;
			contentTextView.setText(listSentences.get(currentSentenceIndex));
		}
		// Display topic
		metaTextView.setText("Topic: " + TOPIC + " - Câu: "
				+ (currentSentenceIndex + 1) + "/" + listSentences.size());
	}

	private void getOutPutFilePath() {
		// folder in SD card
		outputFilePath = Environment.getExternalStorageDirectory().getPath()
				+ "/Viva/" + TOPIC;
		File file = new File(outputFilePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		// build file name
		outputFilePath += "/";
		// gender
		outputFilePath += (SPEAKER_GENDER.equals("male") ? "m" : "f");
		// sample rate
		outputFilePath += ((int) RECORDER_SAMPLERATE / 1000) + "k";
		// speaker name
		if (SPEAKER_NAME != null && SPEAKER_NAME != "") {
			String[] strs = SPEAKER_NAME.split(" ");
			for (String str : strs) {
				if (str.length() > 0) {
					outputFilePath += str.substring(0, 1).toLowerCase();
				}
			}
		}
		// date time
		outputFilePath += new Date().getTime();
	}

	private void writeTextFile() {
		try {
			File file = new File(outputFilePath + ".txt");
			FileOutputStream fOut;
			fOut = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fOut);

			osw.write(SPEAKER_NAME.trim() + "\n");
			osw.write(SPEAKER_AGE.trim() + "\n");
			osw.write(SPEAKER_GENDER + "\n");
			osw.write(SPEAKER_LOCATION.trim() + "\n");
			osw.write(TOPIC + "\n");
			osw.write(getDeviceInfo() + "\n");
			osw.write(RECORDER_SAMPLERATE + "\n");
			osw.write((RECORDER_AUDIO_ENCODING == AudioFormat.ENCODING_PCM_8BIT ? "8"
					: "16")
					+ "\n");
			osw.write("1\n");
			osw.write("signed\n");
			osw.write(contentTextView.getText().toString().trim()
					.replace(".", "").replace(",", "").replace("!", "")
					.replace("?", "").replace(";", "").replace(":", "")
					+ "\n");

			osw.flush();
			osw.close();

			// Logging
			Log.i("SaveInfoFile",
					"Write info file success: " + file.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void clearTempRecord() {
		if (null != outputFilePath) {
			File fileRecord = new File(outputFilePath + ".wav");
			File fileInfo = new File(outputFilePath + ".txt");
			if (fileRecord.exists() && !fileInfo.exists()) {
				if (fileRecord.delete()) {
					// Logging
					Log.i("DeleteTempFile",
							"Deleted " + fileRecord.getAbsolutePath());
				}
			}
		}
	}

	private void readSentences() {
		listSentences = new ArrayList<String>();
		InputStream is;
		try {
			// check exist from sd card
			String dataPath = Environment.getExternalStorageDirectory()
					.getPath() + "/Viva/txt/" + TOPIC + ".txt";
			File f = new File(dataPath);
			if (f.exists()) {
				is = new FileInputStream(f);
			} else {
				// if not exist in sd card, read from asset folder
				is = getApplicationContext().getAssets().open(TOPIC + ".txt");
			}

			// DataInputStream in = new DataInputStream(is);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line = "";
			while ((line = reader.readLine()) != null) {
				listSentences.add(line);
			}
			// Logging
			Log.i("ReadTextFile", "Read text file completed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startTimeCounter() {
		counter = new Timer();
		count = 0;
		counter.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						timerTextView.setText("Time (s): " + count);
						count++;
					}
				});
			}
		}, 0, 1000);

	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i("Viva.Recording", "onStop");
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(
				"START_INDEXS",
						START_INDEXS.get("message") + "," 
						+ START_INDEXS.get("weather")+ "," 
						+ START_INDEXS.get("conversation") + ","
						+ START_INDEXS.get("information") + ","
						+ START_INDEXS.get("number") + ","
						+ START_INDEXS.get("name"));

		editor.commit();
	};

	private String getDeviceInfo() {
		String[] androidVersion = { "BASE", "BASE_1_1", "CUPCAKE", "DONUT",
				"ECLAIR", "ECLAIR_0_1", "ECLAIR_MR1", "FROYO", "GINGERBREAD",
				"GINGERBREAD_MR1", "HONEYCOMB", "HONEYCOMB_MR1",
				"HONEYCOMB_MR2", "ICE_CREAM_SANDWICH",
				"ICE_CREAM_SANDWICH_MR1", "JELLY_BEAN", "JELLY_BEAN_MR1" };
		return Build.MANUFACTURER
				+ ", "
				+ Build.MODEL
				+ ", Android version "
				+ Build.VERSION.RELEASE
				+ " - "
				+ (androidVersion.length >= Build.VERSION.SDK_INT ? androidVersion[Build.VERSION.SDK_INT - 1]
						: "");
	}
}
