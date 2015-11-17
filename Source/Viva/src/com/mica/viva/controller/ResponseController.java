package com.mica.viva.controller;

import com.mica.viva.entity.Sentence;
import com.mica.viva.resource.SentencesManager;
import com.mica.viva.synthesis.SynthesisVoice;

public class ResponseController {

	/**
	 * Response message from Viva: synthesis & read message and display message
	 * to main layout
	 * 
	 * @param message
	 */
	public static void responseMessage(final String message) {
		responseVoiceMessage(message);
		UIController.getMainActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				UIController.displayResponseText(message);
			}
		});
	}

	public static void responseMessageAndWait(final String message) {
		responseVoiceMessageAndWait(message);
		UIController.getMainActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				UIController.displayResponseText(message);
			}
		});
	}

	public static void responseVoiceMessage(String message) {
		// TextNormalizer tn = new TextNormalizer(FileUtils.getWritablePath()
		// + "PhonemesConnectionRules/", FileUtils.getWritablePath()
		// + "Dictionary/read/");
		// String nmMessage = tn.normalize(message);
		// Log.i("Viva TextNormarlizer", nmMessage);

		SynthesisVoice syn = new SynthesisVoice();
		// Bug: sometime can not synthesis text with uppercase character
		syn.readText(message.toLowerCase());
	}

	public static void responseVoiceMessageAndWait(String message) {
		SynthesisVoice syn = new SynthesisVoice();
		syn.readText(message.toLowerCase());
		// stop all process and wait to response completed
		try {
			Thread.sleep(estimateReaddingTime(message));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get random message with the type in database and response to user
	 * 
	 * @param type
	 */
	public static void responseRandomMessage(String type) {
		Sentence sentence = SentencesManager.getInstance().getRandom(type);
		if (sentence != null)
			responseMessage(sentence.getContent());
	}

	public static void responseRandomMessageAndWait(String type) {
		Sentence sentence = SentencesManager.getInstance().getRandom(type);
		if (sentence != null)
			responseMessageAndWait(sentence.getContent());
	}

	public static int estimateReaddingTime(String sentenceToRead) {
		if (sentenceToRead == null || sentenceToRead == "")
			return 0;
		int count = 0;
		for (char ch : sentenceToRead.toCharArray()) {
			if (ch == ' ') {
				count++;
			}
		}
		return count > 10 ? (count + 1) * 300 : (count + 1) * 350;
	}
}
