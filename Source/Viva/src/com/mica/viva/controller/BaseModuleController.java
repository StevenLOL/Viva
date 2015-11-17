package com.mica.viva.controller;

import com.mica.viva.Constant;
import com.mica.viva.entity.Function;
import com.mica.viva.entity.Parameter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BaseModuleController extends Activity {

	protected Function currentFunction;
	protected Thread process;
	protected String returnSentence;
	protected String returnResult;

	protected final int RESPONSE_MESSAGE = 0;

	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			handlingMessage(msg);
		}
	};

	/**
	 * put value to function's parameters
	 */
	protected void initFunction() {
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		// init parameter value
		if (extras != null) {
			for (Parameter param : currentFunction.getParameters()) {
				if (extras.containsKey(param.getKey())) {
					param.setValue(extras.get(param.getKey()));
				}
			}
		}
	}

	/**
	 * check function has any parameter whose value = null, if exist, let user
	 * input its value
	 * 
	 * @throws InterruptedException
	 * @throws ForceCloseActivityException
	 */
	protected void inputRequiredParameters() throws InterruptedException,
			ForceCloseActivityException {
		for (Parameter param : currentFunction.getParameters()) {
			if (param.isRequired() && param.getValue() == null) {
				Log.d("Viva", "Start input parameter " + param.getKey());

				InputController.startInputParameter(this,
						param.getRequirementMessage());

				// Wait user input voice
				synchronized (process) {
					process.wait();
				}

				handleSpecialFunction();

				// input sentence of user
				param.setValue(returnSentence);

				Log.d("Viva", "Finish input parameter " + param.getKey() + ": "
						+ param.getValue());
			}
		}
	}

	/**
	 * confirm to process the function
	 * 
	 * @throws InterruptedException
	 * @throws ForceCloseActivityException
	 */
	protected boolean confirm(String confirmMessage)
			throws InterruptedException, ForceCloseActivityException {
		Log.d("Viva", "Start confirm");
		// Notify user to confirm and start input choose
		InputController.startInputChoise(this, confirmMessage);
		// Wait user input voice to confirm
		synchronized (process) {
			process.wait();
		}
		// return confirm value
		Log.d("Viva", "Finish confirm: " + returnSentence);

		handleSpecialFunction();
		return returnSentence.equals(Constant.CHOICE_OK);
	}

	protected boolean confirm() throws InterruptedException,
			ForceCloseActivityException {
		return confirm(currentFunction.getConfirmMessage());
	}

	public void onActivityResult(int requestCode, int resultCode,
			Intent returnData) {

		if ((requestCode == Constant.INPUT_SENTENCE_CODE
				|| requestCode == Constant.INPUT_PARAMETER_CODE || requestCode == Constant.INPUT_CHOISE_CODE)
				&& resultCode == RESULT_OK) {

			returnSentence = Constant.FUNCTION_IGNORE;

			Bundle extras = returnData.getExtras();
			if (extras != null) {
				returnResult = returnData.getExtras().getString("Result");
				if (returnResult != null) {
					if (returnResult.charAt(0) != ';') {
						String[] strs = returnResult.split(";");
						if (returnResult.contains(";") && strs.length > 0) {
							returnSentence = strs[0];
							UIController.displayRequestText(returnSentence);
						} else {
							returnSentence = returnResult;
						}
					}

				} else {
					returnResult = returnSentence = Constant.INPUT_VOICE_ERROR;
				}

			}

			Log.i("Viva Return Result", "Voice input result :" + returnResult);
			synchronized (process) {
				process.notify();
			}
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	protected void handlingMessage(Message msg) {
		if (msg.what == RESPONSE_MESSAGE) {
			ResponseController.responseMessage(msg.obj.toString());
			return;
		}
	}

//	protected void responseVoiceMessage(String message) {
//		handler.sendMessage(handler.obtainMessage(RESPONSE_MESSAGE, message));
//	}

	protected void handleSpecialFunction() throws ForceCloseActivityException {
		Log.i("Viva", returnSentence);
		if (returnSentence != null) {
			// if return sentence equal to exit function, close current activity
			if (returnSentence.equals(Constant.FUNCTION_IGNORE)) {
				Log.i("Viva", "Force Stop");
				throw new ForceCloseActivityException(
						ForceCloseActivityException.REASON_USER_CANCEL);
			}

			if (returnSentence.equals(Constant.INPUT_VOICE_ERROR)) {
				Log.i("Viva", "Force Stop");
				throw new ForceCloseActivityException(
						ForceCloseActivityException.REASON_INPUT_VOICE_ERROR);
			}
		}
	}

	protected void catchForceCloseActivityException(
			ForceCloseActivityException e) {
		switch (e.getReason()) {
		case ForceCloseActivityException.REASON_USER_CANCEL:
			ResponseController.responseMessage("Vi va đã dừng thao tác");
			break;
		case ForceCloseActivityException.REASON_INPUT_VOICE_ERROR:
			ResponseController.responseMessage("Rất tiếc, không thể kết nối mạng . Bạn hãy kiểm tra và thử lại .");
			break;
		}

	}
}
