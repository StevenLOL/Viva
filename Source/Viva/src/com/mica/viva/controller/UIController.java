package com.mica.viva.controller;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import com.mica.viva.R;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

public class UIController {

	private static ArrayList<View> views = new ArrayList<View>();
	private static int id = 1;

	// Static variable for saving current main activity
	private static Activity currentActivity_;

	public static void setMainActivity(Activity mainActivity) {
		currentActivity_ = mainActivity;
	}

	public static Activity getMainActivity(){
		return currentActivity_;
	}
	
	/**
	 * Display HTML content into WebView in main layout
	 * 
	 * @param htmlContent
	 *            content to display
	 */
	public static void displayHtmlResult(String htmlContent) {
		htmlContent = "<div style='background-color: rgba(10,10,10,0.5);border-radius: 6px;padding:10px;color:#FFFFFF!important'>"
				+ htmlContent + "</div>";

		WebView htmlResult = new WebView(currentActivity_);
		htmlResult.setTag("C");
		htmlResult.setId(id++);
		htmlResult.loadDataWithBaseURL("", htmlContent, "text/html", "UTF-8",
				null);
		htmlResult.setBackgroundColor(0);

		LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		if (views.size() > 0) {
			layoutParams.addRule(RelativeLayout.BELOW,
					views.get(views.size() - 1).getId());
		}

		addNewView(htmlResult, layoutParams);
	}

	/**
	 * Display text of user input into requestTextView in main layout
	 * 
	 * @param requestText
	 *            content to display
	 */
	public static void displayRequestText(String requestText) {
		TextView view = new TextView(currentActivity_);
		view.setTag("L");
		view.setBackgroundResource(R.drawable.balloonleft);
		view.setPadding(40, 10, 20, 10);
		int width = currentActivity_.getWindowManager().getDefaultDisplay()
				.getWidth();
		view.setMaxWidth(width / 2 + 80);
		view.setText(requestText);
		view.setId(id++);
		LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
				RelativeLayout.TRUE);
		if (views.size() > 0) {
			layoutParams.addRule(RelativeLayout.BELOW,
					views.get(views.size() - 1).getId());
			Log.i("chx", Integer.toString(views.get(views.size() - 1).getId()));
		}

		addNewView(view, layoutParams);

	}

	/**
	 * Display text of user input into requestTextView in main layout
	 * 
	 * @param requestText
	 * @param asHtmlContent
	 *            display text as html content
	 */
	public static void displayRequestText(String requestText,
			Boolean asHtmlContent) {
		/*
		 * TextView requestTextView = (TextView) currentActivity_
		 * .findViewById(R.id.requestTextView); if (asHtmlContent)
		 * requestTextView.setText(Html.fromHtml(requestText)); else
		 * requestTextView.setText(requestText);
		 */// Animation here

	}

	/**
	 * Display response text of Viva into responseTextView in main layout
	 * 
	 * @param responseText
	 */
	public static void displayResponseText(String responseText) {
		TextView view = new TextView(currentActivity_);
		view.setTag("R");
		view.setBackgroundResource(R.drawable.balloonright);
		view.setPadding(20, 10, 35, 10);
		int width = currentActivity_.getWindowManager().getDefaultDisplay()
				.getWidth();
		view.setMaxWidth(width / 2 + 80);
		view.setId(id++);
		view.setText(responseText);

		LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
				RelativeLayout.TRUE);
		if (views.size() > 0) {
			layoutParams.addRule(RelativeLayout.BELOW,
					views.get(views.size() - 1).getId());
		}

		addNewView(view, layoutParams);
	}

	/**
	 * Display response text of Viva into responseTextView in main layout
	 * 
	 * @param responseText
	 * @param asHtmlContent
	 *            display text as html content
	 */
	public static void displayResponseText(String responseText,
			Boolean asHtmlContent) {

		/*
		 * TextView responseTextView = (TextView) currentActivity_
		 * .findViewById(R.id.responseTextView); if (asHtmlContent)
		 * responseTextView.setText(Html.fromHtml(responseText)); else
		 * responseTextView.setText(responseText);
		 */// Animation here

	}

	public static void SetButtonRecording() {
		Button startButton = (Button) currentActivity_
				.findViewById(R.id.startButton);
		startButton.setBackgroundDrawable(currentActivity_.getResources()
				.getDrawable(R.drawable.button_recording));
	}

	public static void SetButtonOffRecording() {
		Button startButton = (Button) currentActivity_
				.findViewById(R.id.startButton);
		startButton.setBackgroundDrawable(currentActivity_.getResources()
				.getDrawable(R.drawable.button_defaults));
	}

	private static void scrollToBottom() {
		ScrollView scroll = (ScrollView) currentActivity_
				.findViewById(R.id.scrollview);
		scroll.fullScroll(View.FOCUS_DOWN);
		playAnimation();
	}

	private static void addNewView(final View newView,
			final LayoutParams layoutParams) {
		final RelativeLayout layout = (RelativeLayout) currentActivity_
				.findViewById(R.id.gamehistory);

		views.add(newView);

		if (views.size() > 3) {
			final View removeView = views.get(0);
			TranslateAnimation slide;
			if (removeView.getTag().equals("R")) {
				slide = new TranslateAnimation(0, removeView.getWidth(), 0, 0);
			} else {
				slide = new TranslateAnimation(0, -removeView.getWidth(), 0, 0);
			}

			slide.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub
					layout.addView(newView, layoutParams);
					layout.removeView(removeView);
					
				}
			});

			slide.setDuration(600);
			slide.setFillAfter(true);
			removeView.startAnimation(slide);
			views.remove(0);
			
		} else {
			layout.addView(newView, layoutParams);
		}
	}

	private static void playAnimation() {
		if (views.size() > 3) {
			View view = views.get(0);
			TranslateAnimation slide;
			if (view.getTag().equals("R")) {
				slide = new TranslateAnimation(0, view.getWidth(), 0, 0);
			} else {
				slide = new TranslateAnimation(0, -view.getWidth(), 0, 0);
			}

			slide.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub

				}
			});
			slide.setDuration(600);
			slide.setFillAfter(false);
			view.startAnimation(slide);
			RelativeLayout layout = (RelativeLayout) currentActivity_
					.findViewById(R.id.gamehistory);
			layout.removeView(view);
			views.remove(0);

		}
	}
}
