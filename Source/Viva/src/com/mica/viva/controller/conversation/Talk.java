package com.mica.viva.controller.conversation;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

import com.mica.viva.Constant;
import com.mica.viva.controller.BaseModuleController;
import com.mica.viva.controller.ResponseController;
import com.mica.viva.resource.FunctionsManager;

public class Talk extends BaseModuleController {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		currentFunction = FunctionsManager.getInstance().getFunction("TALK");
		super.onCreate(savedInstanceState);
		initFunction();

		if (currentFunction.getParameters().get(0).getValue() != null) {
			String userSentence = currentFunction.getParameters().get(0)
					.getValue().toString();

			if (userSentence.contains("tên gì")) {
				ResponseController
						.responseMessage("Tôi là Vi Va, thư ký của bạn trên điện thoại này.");
			} else if (userSentence.contains("chào")) {
				ResponseController
						.responseMessageAndWait("Xin chào, tôi có thể giúp gì được bạn?");
				Intent intent = new Intent();
				intent.putExtra(Constant.STARTINPUT_BUNDLE_NAME,
						Constant.STARTINPUT_BUNDLE_VALUE);
				setResult(RESULT_OK, intent);
			} else {
				ResponseController.responseMessage(currentFunction
						.getErrorMessage());
			}
		}

		finish();
	}
}
