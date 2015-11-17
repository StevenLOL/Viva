package com.mica.viva.controller.sms;

import java.util.ArrayList;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.mica.viva.ApplicationContext;
import com.mica.viva.controller.BaseModuleController;
import com.mica.viva.controller.ForceCloseActivityException;
import com.mica.viva.controller.InputController;
import com.mica.viva.controller.ResponseController;
import com.mica.viva.resource.FunctionsManager;
import com.mica.viva.resource.SentencesManager;
import com.mica.viva.utility.ContactInfo;
import com.mica.viva.utility.ContactUtils;

public class SendingSms extends BaseModuleController {

	private BroadcastReceiver smsSent = null;
	private BroadcastReceiver smsDelivered = null;
	private String receiverNumber;
	private String receiverName = "người nhận";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Set currentFunction before call super.OnCreate()...
		currentFunction = FunctionsManager.getInstance().getFunction(
				"SENDING_SMS");
		super.onCreate(savedInstanceState);
		// init function's parameters
		initFunction();
		// Set the process, it will run the script of this function
		process = new Thread(new Runnable() {
			public void run() {
				try {
					do {
						inputRequiredParameters();
						if (confirm()) {
							// String receiver = currentFunction.getParameters()
							// .get(0).getValue().toString();
							Log.i("Viva", "Receiver :" + receiverNumber);
							String content = currentFunction.getParameters()
									.get(1).getValue().toString();
							Log.i("Viva", "Content :" + content);
							sendSms(receiverNumber, content);

							// clear current function's parameters' value
							currentFunction.clearParametersValue();
							finish();
							return;
						}

						currentFunction.clearParametersValue();
					} while (confirm(SentencesManager.getInstance()
							.getRandom("CONFIRM_REDO").getContent()));

				} catch (ForceCloseActivityException e) {
					catchForceCloseActivityException(e);
				} catch (Exception e) {
					Log.e("Viva SendingSms", "Run thread error SendingSms");
					e.printStackTrace();
				} finally {
					currentFunction.clearParametersValue();
					finish();
				}
			}
		});
		// start process
		process.start();
	}

	// Send a SMS with receiver and content parameters
	private void sendSms(String receiver, String content) {
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";
		PendingIntent sentPI = PendingIntent.getBroadcast(
				ApplicationContext.getApplicationContext(), 0,
				new Intent(SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(
				ApplicationContext.getApplicationContext(), 0, new Intent(
						DELIVERED), 0);
		// SMS Sent
		smsSent = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				if (getResultCode() == Activity.RESULT_OK) {
					Log.i("Viva", "Send sms success");

					ResponseController.responseMessage("gửi tin nhắn cho "
							+ receiverName + " thành công ");
				} else {
					Log.e("Viva", "Send sms error");
					ResponseController.responseMessage(currentFunction
							.getErrorMessage());
				}
				if (smsSent != null) {
					ApplicationContext.getApplicationContext()
							.unregisterReceiver(smsSent);
				}

			}
		};
		ApplicationContext.getApplicationContext().registerReceiver(smsSent,
				new IntentFilter(SENT));

		// SMS delivered
		smsDelivered = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				if (getResultCode() == Activity.RESULT_OK) {
					Log.i("Viva", "Delivere sms success");
				} else {
					Log.e("Viva", "Delivere sms error");
					ResponseController.responseMessage(currentFunction
							.getErrorMessage());
				}
				ApplicationContext.getApplicationContext().unregisterReceiver(
						smsDelivered);
			}
		};
		ApplicationContext.getApplicationContext().registerReceiver(
				smsDelivered, new IntentFilter(DELIVERED));

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(receiver, null, content, sentPI, deliveredPI);
	}

	@Override
	protected void inputRequiredParameters() throws InterruptedException,
			ForceCloseActivityException {
		super.inputRequiredParameters();

		if (currentFunction.getParameters().get(0).getValue() == null) {
			// Re-input receiver
			ResponseController
					.responseMessageAndWait("bạn chưa nhập người nhận.");
			inputRequiredParameters();
			return;
		}
		// check phone number or contact address
		String receiver = currentFunction.getParameters().get(0).getValue()
				.toString();

		boolean isPhoneNumber = Pattern
				.matches("^[\\+0]([\\s0-9])+$", receiver);
		if (isPhoneNumber) {
			receiverNumber = receiver;
			return;
		}
		
		String phoneNumber = com.mica.viva.utility.TextUtils
				.getPhoneNumberOfText(receiver);
		if (phoneNumber != "") {
			receiverNumber = phoneNumber;
		} else {
			ArrayList<ContactInfo> contacts = ContactUtils.searchContact(
					ApplicationContext.getApplicationContext(), receiver,
					ContactUtils.SEARCHMODE_DISPLAYNAME);
			if (contacts.size() == 0) {
				ResponseController.responseMessageAndWait(String.format(
						"không tìm thấy %s trong danh bạ", receiver));
				currentFunction.getParameters().get(0).setValue(null);
				inputRequiredParameters();
			} else {
				if (contacts.size() == 1) {
					receiverNumber = contacts.get(0).Phone;
					receiverName = receiver;
				} else {
					// let user choose contact
					// Start Input voice
					InputController.startInputParameter(this, String.format(
							"Tìm thấy %s liên hệ trong danh bạ. bạn hãy chọn",
							com.mica.viva.utility.TextUtils
									.getTextOfNumber(contacts.size())));
					// Wait user input voice
					synchronized (process) {
						process.wait();
					}
					// input sentence of user
					int choise = com.mica.viva.utility.TextUtils
							.getNumberOfText(returnSentence);
					if (choise >= 0 && choise < contacts.size()) {
						receiverNumber = contacts.get(choise).Phone;
					} else {
						receiverNumber = contacts.get(0).Phone;
					}
				}
			}
		}
	}

}
