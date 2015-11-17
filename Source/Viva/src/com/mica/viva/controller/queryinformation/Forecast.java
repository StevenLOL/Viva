package com.mica.viva.controller.queryinformation;

import java.util.ArrayList;
import java.util.Calendar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import com.mica.viva.MainActivity;
import com.mica.viva.controller.BaseModuleController;
import com.mica.viva.controller.FunctionController;
import com.mica.viva.controller.InputController;
import com.mica.viva.controller.ResponseController;
import com.mica.viva.controller.UIController;
import com.mica.viva.entity.Function;
import com.mica.viva.resource.FunctionsManager;

public class Forecast extends BaseModuleController {
	static final String URL = "http://api.wunderground.com/api/0fd621542922d099/forecast10day/q/VietNam/";
	// XML node keys
	static final String KEY_PARENT = "simpleforecast";
	static final String KEY_NODE = "forecastday";
	static final String KEY_NODE_DATE = "date";
	static final String KEY_DAY = "day";
	static final String KEY_MONTH = "month";
	static final String KEY_YEAR = "year";// parent node
	static final String KEY_ID = "period";
	static final String KEY_UNIT = "celsius";

	static final String KEY_TEMP_HIGH = "high";
	static final String KEY_TEMP_LOW = "low";
	static final String KEY_WEEKDAY = "weekday";
	static final String KEY_CONDITION = "conditions";
	static final String KEY_ICON = "icon";
	static final String KEY_SKYICON = "skyicon";
	static final String KEY_HUMIDITY = "avehumidity";
	static final String KEY_WIND = "kph";

	ArrayList<String> Temp = new ArrayList<String>();
	ArrayList<String> TempImage = new ArrayList<String>();
	String Location = null, Day = null, result = null, sentenceout = " ";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		currentFunction = FunctionsManager.getInstance().getFunction("WEATHER");
		super.onCreate(savedInstanceState);
		initFunction();

		try {
			Location = "Hà Nội";
			int ngay = Calendar.getInstance().get(Calendar.DATE);
			if (currentFunction.getParameters().get(0).getValue() != null) {
				Location = currentFunction.getParameters().get(0).getValue()
						.toString();
			}
			if (currentFunction.getParameters().get(1).getValue() != null) {
				ngay = Compile.complie_day(currentFunction.getParameters()
						.get(1).getValue().toString());
			}
			Day = Integer.toString(ngay);

			Log.i("Weather", Day + "  " + Location);

			if (getLocation(Location) != null) {
				result = execute(getLocation(Location), Day)
						+ "<table style='background:none' border=\"0\" cellpadding=\"0\" cellspacing=\"0\"   width=\"100%\"><tr>";
				for (int i = 0; i < 4 && i < Temp.size(); i++) {
					result += Temp.get(i).toString();
				}
				result += "</tr><tr>";
				for (int i = 0; i < 4 && i < TempImage.size(); i++) {
					result += TempImage.get(i).toString();
				}
				result += "</tr></table>";
			} else {
				result = execute(replace(Location, " ", "%20"), Day);
			}

			UIController.displayHtmlResult(result);
			// responseVoiceMessage(sentenceout);
			ResponseController.responseMessage(sentenceout);

		} catch (Exception e) {
			ResponseController.responseMessage(currentFunction.getErrorMessage());
			e.printStackTrace();
		}

		currentFunction.clearParametersValue();
		finish();
	}

	private String getLocation(String name) {

		String LocalUtile = null;
		XMLParser parser = new XMLParser();
		Log.i("access!!!", parser.getXmlFromAssets(getApplicationContext(),
				"location.xml"));
		String xml = parser.getXmlFromAssets(getApplicationContext(),
				"location.xml");
		Document doc = parser.getDomElement(xml);
		NodeList parent = doc.getElementsByTagName("Province");
		for (int i = 0; i < parent.getLength(); i++) {
			Element tag = (Element) parent.item(i);
			String tagName = parser.getValue(tag, "Display");
			Log.i("tagName", tagName);
			if (tagName.equals(name)) {
				String Display = parser.getValue(tag, "Display");
				LocalUtile = replace(parser.getValue(tag, "Utile"), " ", "%20");

				Log.i("found!!!", LocalUtile);
				break;
			} else {
				Log.i("next!!!", "not this one");
			}
		}
		// TODO Auto-generated method stub
		return LocalUtile;
	}

	private String execute(String loc, String time) {
		// TODO Auto-generated method stub

		String res = "";

		// String location = null ;
		XMLParser parser = new XMLParser();
		Log.i("counter", "data");
		// Log.i("local", parser.getXmlFromAssets("location.xml"));

		String xml = parser.getXmlFromUrl(URL + "/" + loc + ".xml"); // getting
																		// XML
																		// from
																		// URL
		Document doc = parser.getDomElement(xml); // getting DOM element
		NodeList parent = doc.getElementsByTagName(KEY_PARENT);
		Element parentElement = (Element) parent.item(0);
		NodeList nl = parentElement.getElementsByTagName(KEY_NODE);

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			String checkTime = parser.getValue(e, KEY_DAY);
			NodeList nlTempHigh = e.getElementsByTagName(KEY_TEMP_HIGH);
			NodeList nlTempLow = e.getElementsByTagName(KEY_TEMP_LOW);

			String DayForecast = "<td style='color:#fff' width=\"25%\"><b><font size = \"1\">"
					+ parser.getValue(e, KEY_DAY)
					+ "/"
					+ parser.getValue(e, KEY_MONTH)
					+ "</font></b><br><i><font size=\"1\">"
					+

					parser.getWeekdayString(parser.getValue(e, KEY_WEEKDAY))
					+ "</font></i></td>";// parser.getValue(e,
											// KEY_CONDITION);

			String ImageForecast = "<td style='color:#fff'><div><img src=\"file:///android_asset/ThoiTiet/"
					+ parser.getValue(e, KEY_ICON)
					+ ".png\" height=\"40\" width=\"40\" alt=\"\"></div><div ><span style=\"font-family: Verdana, sans-serif; font-size: 30%;\">";
			for (int j = 0; j < nlTempHigh.getLength(); j++) {
				Element d = (Element) nlTempHigh.item(j);
				Element f = (Element) nlTempLow.item(j);
				// DayForecast += parser.getValue(d,
				// KEY_UNIT)+"\u2103"+"/"+parser.getValue(f, KEY_UNIT) ;
				ImageForecast += parser.getValue(d, KEY_UNIT)
						+ "<sup>o</sup>C-"
						+ parser.getValue(f, KEY_UNIT)
						+ "<sup>o</sup>C</span><br><span  style=\"font-family: Verdana, sans-serif; font-size: 10%;\">";
			}
			ImageForecast += parser.getConditionString(parser.getValue(e,
					KEY_ICON)) + "</span></div></td>";
			Temp.add(DayForecast);
			TempImage.add(ImageForecast);
			Log.i("day number", DayForecast);
			// So sánh với th�?i gian nhận được từ

			if (checkTime.equals(time)) {
				// Hiển thị thông tin cơ bản
				// res += parser.getValue(e, KEY_WEEKDAY)+
				// parser.getValue(e, KEY_CONDITION) ;
				res = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"   width=\"100%\"><tr><td><font face:\"verdana\" size:\"70%\" style=\"text-transform: uppercase; color:#fff \">"
						+ loc
						+ ","
						+ parser.getWeekdayString(parser.getValue(e,
								KEY_WEEKDAY))
						+ "</font><br><i></i></td></tr><tr><td  width = \"40%\">"
						+ "<div><img src=\"file:///android_asset/ThoiTiet/"
						+ parser.getValue(e, KEY_ICON)
						+ ".png\" height=\"75\" width=\"75\" alt=\"\" ></div><br>"
						+ "<div><img src=\"file:///android_asset/ThoiTiet/humid.png\" alt=\"\" height=\"15\" width=\"15\"><span style=\"font-family: Verdana, sans-serif; color:#FFFFFF\">"
						+ " "
						+ parser.getValue(e, KEY_HUMIDITY)
						+ "%</span></div>"
						+ "<div><img src=\"file:///android_asset/ThoiTiet/wind.png\" alt=\"\" height=\"15\" width=\"15\"><span style=\"font-family: Verdana, sans-serif; color:#FFFFFF\">"
						+ " "
						+ parser.getValue(e, KEY_WIND)
						+ "kmph</span></div>"
						+ "<br></td><td width = 60% ><div>"
						+ "<br><span style=\"font-family: Verdana, sans-serif; font-size: 200%; color:#FFFFFF  \">";
				for (int j = 0; j < nlTempHigh.getLength(); j++) {
					Element d = (Element) nlTempHigh.item(j);
					Element f = (Element) nlTempLow.item(j);
					res += parser.getValue(f, KEY_UNIT)
							+ "<sup>o</sup></span><span style=\"font-family: Verdana, sans-serif; font-size: 300%; color:#FFFFFF \">"
							+ " " + parser.getValue(d, KEY_UNIT)
							+ "<sup>o</sup></span><br>";
					sentenceout = parser.getConditionString(parser.getValue(e,
							KEY_ICON))
							+ ", nhiệt độ từ "
							+ Compile.tenso[Integer.valueOf(parser.getValue(f,
									KEY_UNIT))]
							+ "tới"
							+ Compile.tenso[Integer.valueOf(parser.getValue(d,
									KEY_UNIT))] + "độ";
					// Log.i("thu biet", sentenceout);
				}
				res += "<span style=\"font-family: Verdana, sans-serif; font-size: 100%; color:#FFFFFF \" ><font style=\"text-transform: uppercase;\">"
						+ parser.getConditionString(parser
								.getValue(e, KEY_ICON))
						+ "</font></span></div></td></tr></table>";

				// sentenceout =
				// parser.getConditionString(parser.getValue(e, KEY_ICON)) +
				// " nhiệt độ từ " + compile.tenso(parser.getValue(e,
				// KEY_UNIT)) + "tới" + compile.tenso(parser.getValue(e,
				// KEY_UNIT)) + " độ " ;
			}
		}
	
		return res;

	}

	private String replace(String text, String space, String value) {
		String result = text.replaceAll(space, value);
		return result;

	}

}
