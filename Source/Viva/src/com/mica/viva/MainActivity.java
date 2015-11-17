/**
 * Copyright By MICA CENTER – VIVA PROJECT
 * Created By Binh.N.D – 02/10/2012
 */

package com.mica.viva;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.mica.viva.controller.BaseModuleController;
import com.mica.viva.controller.ForceCloseActivityException;
import com.mica.viva.controller.FunctionController;
import com.mica.viva.controller.InputController;
import com.mica.viva.controller.ResponseController;
import com.mica.viva.controller.UIController;
import com.mica.viva.entity.Function;
import com.mica.viva.entity.Parameter;
import com.mica.viva.resource.FunctionsManager;
import com.mica.viva.synthesis.SynthesisVoice;
import com.mica.viva.utility.ContactInfo;
import com.mica.viva.utility.ContactUtils;
import com.mica.viva.utility.FileUtils;
import com.mica.viva.utility.TextUtils;
import com.mica.viva.utility.textnormarlizer.TextNormalizer;

import android.os.Bundle;
import android.os.StrictMode;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends BaseModuleController {

	private static boolean mainActivityStarted_ = false;

	
	public static boolean isMainActivityStarted() {
		return mainActivityStarted_;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		int SDK_INT = android.os.Build.VERSION.SDK_INT;
//
//		if (SDK_INT > 8) {
//			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//					.permitAll().build();
//			StrictMode.setThreadPolicy(policy);
//		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		UIController.setMainActivity(MainActivity.this);
		UIController
				.displayResponseText("Chào mừng bạn đến với Viva. Hãy bấm nút bắt đầu và yêu cầu Viva bất cứ điều gì!");
		SynthesisVoice syn = new SynthesisVoice();
		syn.prepareToSynthesis(getApplicationContext());

		// Set application context
		ApplicationContext.setApplicationContext(getApplicationContext());

		// Test area
		/*
		 * Log.i("Viva Number", TextUtils.getTextOfPhoneNumber("+84936990866"));
		 * Log.i("Viva Number", TextUtils.getTextOfPhoneNumber("0936990866"));
		 * ArrayList<ContactInfo> contacts = ContactUtils.searchContact(
		 * ApplicationContext.getApplicationContext(), "+84936990866",
		 * ContactUtils.SEARCHMODE_PHONENUMBER);
		 * 
		 * if (contacts.size() > 0) { Log.i("Viva Contact",
		 * contacts.get(0).Name); }
		 * 
		 * ArrayList<ContactInfo> contacts2 = ContactUtils.searchContact(
		 * ApplicationContext.getApplicationContext(), "bình",
		 * ContactUtils.SEARCHMODE_DISPLAYNAME);
		 * 
		 * if (contacts2.size() > 0) { Log.i("Viva Contact",
		 * contacts2.get(0).Name + " " + contacts2.get(0).Phone); } try {
		 * FileUtils.copyDirFromAssest("PhonemesConnectionRules");
		 * FileUtils.copyDirFromAssest("Dictionary/read");
		 * 
		 * } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 * 
		 * String inputText = "tôi yêu bạn"; TextNormalizer tn = new
		 * TextNormalizer(FileUtils.getWritablePath() +
		 * "PhonemesConnectionRules/", FileUtils.getWritablePath() +
		 * "Dictionary/read/"); Log.i("Viva TextNormarlizer",
		 * tn.normalize(inputText));
		 * 
		 * Log.i("Viva TextNormarlizer", tn.normalize("một USD đổi 20100 đ"));
		 * Log.i("Viva TextNormarlizer", tn.normalize("ngày 20/10/2013"));
		 */
		
		String result = TextUtils.getPhoneNumberOfText("không một sáu tám chín chín hai tám sáu không không");
		Log.i("Viva PhoneNumber",result);
		// End test area

		process = null;
		Button startButton = (Button) findViewById(R.id.startButton);

		startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				startGame();
			}
		});

		mainActivityStarted_ = true;
		checkStartupFunction();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this, SettingActivity.class));
			return true;
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent returnData) {
		super.onActivityResult(requestCode, resultCode, returnData);

		if (resultCode == RESULT_OK && returnData != null) {
			Bundle extras = returnData.getExtras();
			if (extras != null) {
				if (extras.getInt(Constant.STARTINPUT_BUNDLE_NAME, 0) == Constant.STARTINPUT_BUNDLE_VALUE) {
					startGame();
				}
			}
		}

	}

	private void startGame() {
		process = new Thread(new Runnable() {

			public void run() {
				try {
					InputController.startInputSentence(MainActivity.this);
					synchronized (process) {
						process.wait();
					}

					handleSpecialFunction();
					Function function = FunctionController
							.getFunctionFromData(returnResult);

					if (function != null) {
						FunctionController.startFunctionForResult(
								MainActivity.this, function);
					} else {
						ResponseController.responseMessage("Rất tiếc, tôi không hiểu yêu cầu của bạn.");
					}
				} catch (ForceCloseActivityException e) {
					catchForceCloseActivityException(e);
				} catch (Exception e) {
					Log.e("Viva MainActivity", "Run thread error MainActivity");
					e.printStackTrace();
				}
			}
		});
		process.start();
	}

	private void checkStartupFunction() {
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		if (extras != null) {
			if (extras.containsKey("StartupFunctionCode")) {
				Function startup = FunctionsManager.getInstance().getFunction(
						extras.getString("StartupFunctionCode"));
				if (startup != null) {
					for (Parameter param : startup.getParameters()) {
						if (extras.containsKey(param.getKey())) {
							param.setValue(extras.get(param.getKey()));
						}
					}
					FunctionController.startFunctionForResult(
							MainActivity.this, startup);
				}
			}
		}
	}
}
