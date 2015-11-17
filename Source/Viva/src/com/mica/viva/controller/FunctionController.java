package com.mica.viva.controller;

import java.util.Hashtable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mica.viva.MainActivity;
import com.mica.viva.entity.Function;
import com.mica.viva.entity.Parameter;
import com.mica.viva.resource.FunctionsManager;

public class FunctionController {

	/**
	 * start activity to execute the input function
	 * 
	 * @param context
	 * @param function
	 */
	public static void startFunction(Context context, Function function) {

		Intent mIntent;
		if (MainActivity.isMainActivityStarted()) {
			mIntent = new Intent(function.getActivity());
			Log.i("Viva FunctionController",
					"Started activity " + function.getActivity());
		} else {		
			mIntent = new Intent(context, MainActivity.class);
			mIntent.putExtra("StartupFunctionCode", function.getFucntionCode());
			Log.i("Viva FunctionController",
					"Started activity com.mica.viva.MainActivity");
		}
		
		for (Parameter param : function.getParameters()) {
			if (param.getValue() != null) {
				mIntent.putExtra(param.getKey(), param.getValue().toString());
				param.setValue(null);
			}
		}
		
		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		context.startActivity(mIntent);
	}

	public static void startFunctionForResult(Activity activity,
			Function function) {
		Intent mIntent = new Intent(function.getActivity());
		for (Parameter param : function.getParameters()) {
			if (param.getValue() != null) {
				mIntent.putExtra(param.getKey(), param.getValue().toString());
				param.setValue(null);
			}
		}
		// mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivityForResult(mIntent, function.getFunctionHash());
		Log.i("Viva FunctionController",
				"Started activity " + function.getActivity()
						+ ", requestCode: " + function.getFunctionHash());
	}

	/**
	 * start activity to execute the input function
	 * 
	 * @param context
	 * @param functionCode
	 * @param params
	 */
	public static void startFunction(Context context, String functionCode,
			Hashtable<String, String> params) {

		Function function = FunctionsManager.getInstance().getFunction(
				functionCode);

		Intent mIntent = new Intent(function.getActivity());

		for (String key : params.keySet()) {
			if (params.get(key) != null && params.get(key) != "") {
				mIntent.putExtra(key, params.get(key));
			}
		}

		mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(mIntent);
	}

	public static Function getFunctionFromData(String st) {
		String[] arr = st.split(";");
		Log.i("Length", arr.length + "");
		if (arr != null && arr.length >= 3) {
			String functionCode = arr[1];
			if (functionCode != "") {
				Function function = FunctionsManager.getInstance().getFunction(
						functionCode);
				if (function != null) {
					for (int i = 2; 2 * i < arr.length; i++) {
						function.setParameterValue(arr[2 * i - 1], arr[2 * i]);
					}
					return function;
				}
			}
		}
		// naive understanding
		if (arr != null && arr.length >= 1) {
			String returnResult = arr[0];

			if (returnResult.contains("vàng")) {
				return FunctionsManager.getInstance().getFunction("GOLD");
			}

			if (returnResult.contains("đô la")
					|| returnResult.contains("tỉ giá")
					|| returnResult.contains("ngoại tệ")) {
				return FunctionsManager.getInstance().getFunction("EXCHANGE");
			}

			Function f = FunctionsManager.getInstance().getFunction("TALK");
			f.setParameterValue("USER_SENTENCE", returnResult);
			return f;
		}
		return null;
	}
}
