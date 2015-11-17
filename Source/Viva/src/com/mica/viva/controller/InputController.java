package com.mica.viva.controller;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import com.mica.viva.Constant;

public class InputController {

	/**
	 * Start input sentence activity, recognize voice of user and understanding
	 * the return text, return Bundle will contain input sentence module code
	 * function code and params array
	 * 
	 * @param caller
	 */
	public static void startInputSentence(Activity caller) {
		Intent intent = new Intent("com.mica.viva.inputting.VADRecord");
		intent.putExtra("input_type", Constant.INPUT_SENTENCE_CODE);
		caller.startActivityForResult(intent, Constant.INPUT_SENTENCE_CODE);
	}

	public static void startInputSentence(final Activity caller,
			final String message) {
		ResponseController.responseMessage(message);
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				startInputSentence(caller);
			}
		}, ResponseController.estimateReaddingTime(message));
	}

	/**
	 * Start input sentence activity, recognize voice and return the text of
	 * input sentence
	 * 
	 * @param caller
	 */
	public static void startInputParameter(Activity caller) {
		Intent intent = new Intent("com.mica.viva.inputting.VADRecord");
		intent.putExtra("input_type", Constant.INPUT_PARAMETER_CODE);
		caller.startActivityForResult(intent, Constant.INPUT_PARAMETER_CODE);
	}

	/**
	 * Start input sentence activity, recognize voice and return the text of
	 * input sentence
	 * 
	 * @param caller
	 * @param message
	 *            content that Viva will response before user start input voice
	 */
	public static void startInputParameter(final Activity caller,
			final String message) {

		/*
		 * ResponseController.responseVoiceMessageAndWait(message);
		 * startInputParameter(caller);
		 */

		ResponseController.responseMessage(message);
		Timer t = new Timer();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				startInputParameter(caller);
			}
		}, ResponseController.estimateReaddingTime(message));

		/*
		 * ScheduledExecutorService worker = Executors
		 * .newSingleThreadScheduledExecutor(); worker.schedule(new Runnable() {
		 * 
		 * @Override public void run() { startInputParameter(caller); } },
		 * ResponseController.estimateReaddingTime(message),
		 * TimeUnit.MILLISECONDS);
		 */

	}

	/**
	 * Start input sentence activity, recognize voice and understanding and
	 * return user's choose
	 * 
	 * @param caller
	 */
	public static void startInputChoise(Activity caller) {
		Intent intent = new Intent("com.mica.viva.inputting.VADRecord");
		intent.putExtra("input_type", Constant.INPUT_CHOISE_CODE);
		caller.startActivityForResult(intent, Constant.INPUT_CHOISE_CODE);
	}

	public static void startInputChoise(final Activity caller,
			final String message) {
		ResponseController.responseMessage(message);

		Timer t = new Timer();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				startInputChoise(caller);
			}
		}, ResponseController.estimateReaddingTime(message));
	}
}
