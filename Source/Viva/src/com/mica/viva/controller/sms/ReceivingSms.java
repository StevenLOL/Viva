package com.mica.viva.controller.sms;

import com.mica.viva.ApplicationConfigs;
import com.mica.viva.ApplicationContext;
import com.mica.viva.controller.FunctionController;
import com.mica.viva.entity.Function;
import com.mica.viva.resource.FunctionsManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class ReceivingSms extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (ApplicationContext.getApplicationContext() == null) {
			Log.i("Viva ReceivingSms", "CurrentContext is null");
			ApplicationContext.setApplicationContext(context
					.getApplicationContext());
		}

		Bundle bundle = intent.getExtras();
		SmsMessage[] msgs = null;
		
		if (ApplicationConfigs.getInstance().enableReadReceiverMessage()) {
			if (bundle != null) {
				// Get phone number of sender and SMS content

				String content = "";
				String phoneAddress = "";
				Object[] pdus = (Object[]) bundle.get("pdus");
				msgs = new SmsMessage[pdus.length];
				for (int i = 0; i < msgs.length; i++) {
					msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
					if (i == 0) {
						phoneAddress += msgs[i].getOriginatingAddress();
					}
					content += msgs[i].getMessageBody().toString();
				}
								
				// Call ReadingSMS Function
				Function function = FunctionsManager.getInstance().getFunction(
						"READING_SMS");
				function.getParameters().get(0).setValue(phoneAddress);
				function.getParameters().get(1).setValue(content);

				FunctionController.startFunction(context, function);			
			
			}
		}
	}

}
