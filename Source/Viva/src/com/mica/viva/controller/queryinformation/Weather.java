package com.mica.viva.controller.queryinformation;

import android.os.Bundle;

import com.mica.viva.controller.BaseModuleController;
import com.mica.viva.controller.ResponseController;
import com.mica.viva.controller.UIController;

public class Weather extends BaseModuleController {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// Query weather information here

		// Response information
		String message = "Hôm nay trời nắng,nhiệt độ từ hai mươi đến ba mươi độ xê,có mưa nhỏ buổi trưa.";
		ResponseController.responseMessage(message);
		String htmlContent = "<h1>Thời tiết Hà Nội hôm nay</h1>"
				+ "<p><img src='file:///android_asset/weather/fog.png'/></p>"
				+ "<h1>27°C</h1>"
				+ "Khói<br/>Gió: Vận tốc 11 km/h<br/>Độ ẩm: 74%<br/>";
//		UIController.displayHtmlResult(htmlContent);
		finish();
	}

}
