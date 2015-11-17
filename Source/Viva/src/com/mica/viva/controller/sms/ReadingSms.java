package com.mica.viva.controller.sms;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;

import com.mica.viva.ApplicationContext;
import com.mica.viva.controller.BaseModuleController;
import com.mica.viva.controller.ForceCloseActivityException;
import com.mica.viva.controller.FunctionController;
import com.mica.viva.controller.ResponseController;
import com.mica.viva.diacritic.VietnameseDiacritic;
import com.mica.viva.entity.Function;
import com.mica.viva.resource.FunctionsManager;
import com.mica.viva.resource.SentencesManager;
import com.mica.viva.utility.ContactInfo;
import com.mica.viva.utility.ContactUtils;
import com.mica.viva.utility.TextUtils;

public class ReadingSms extends BaseModuleController {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Set currentFunction before call super.OnCreate()...
		currentFunction = FunctionsManager.getInstance().getFunction(
				"READING_SMS");
		super.onCreate(savedInstanceState);
		// init function's parameters
		initFunction();
		// Set the process, it will run the script of this function
		process = new Thread(new Runnable() {
			public void run() {
				try {
					String sender = currentFunction.getParameters().get(0)
							.getValue().toString();
					// confirm to read SMS content
					ArrayList<ContactInfo> contacts = ContactUtils.searchContact(
							ApplicationContext.getApplicationContext(), sender,
							ContactUtils.SEARCHMODE_PHONENUMBER);

					if (contacts.size() > 0) {
						sender = contacts.get(0).Name;
					} else {
						sender = "số điện thoại , "
								+ TextUtils.getTextOfPhoneNumber(sender);
					}

					if (confirm("Có tin nhắn mới từ " + sender
							+ " , bạn có muốn đọc tin nhắn này không?")) {
						String smsContent = currentFunction.getParameters()
								.get(1).getValue().toString();
						VietnameseDiacritic vd = new VietnameseDiacritic();
						smsContent = vd.AddDiacritic(smsContent);

						String contentToRead = SentencesManager.getInstance()
								.getRandom("SMS_READCONTENT").getContent()
								.replace("{CONTENT}", smsContent);

						// handler.sendMessage(handler.obtainMessage(
						// RESPONSE_MESSAGE, contentToRead));
						// Thread.sleep(2000);
						
						ResponseController.responseMessageAndWait(contentToRead);
					}
					// confirm to reply SMS
					if (confirm(SentencesManager.getInstance()
							.getRandom("SMS_REPLY_CONFIRM").getContent())) {
						Function function = FunctionsManager.getInstance()
								.getFunction("SENDING_SMS");

						function.getParameters()
								.get(0)
								.setValue(
										currentFunction.getParameters().get(0)
												.getValue());

						FunctionController.startFunction(ReadingSms.this,
								function);
					}
					
				} catch (ForceCloseActivityException e) {
					catchForceCloseActivityException(e);
				} catch (Exception e) {
					Log.e("Viva ReadingSms", "Run thread error ReadingSms");
					e.printStackTrace();
				}				
				finally{
					currentFunction.clearParametersValue();
					finish();
				}
			}
		});

		process.start();
	}
}
