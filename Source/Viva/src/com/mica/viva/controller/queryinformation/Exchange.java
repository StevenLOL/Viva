package com.mica.viva.controller.queryinformation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.mica.viva.controller.BaseModuleController;
import com.mica.viva.controller.ResponseController;
import com.mica.viva.controller.UIController;
import com.mica.viva.resource.FunctionsManager;
import com.mica.viva.utility.TextUtils;

import android.os.Bundle;
import android.util.Log;

public class Exchange extends BaseModuleController {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		currentFunction = FunctionsManager.getInstance().getFunction("GOLD");
		super.onCreate(savedInstanceState);
		initFunction();

		String content = "";
		String textResponse = "";
		String url = "http://hn.24h.com.vn/ttcb/tygia/tygia.php";
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httppost = new HttpGet(url);
			HttpResponse response = httpClient.execute(httppost);
			HttpEntity ht = response.getEntity();

			BufferedHttpEntity buf = new BufferedHttpEntity(ht);

			InputStream is = buf.getContent();

			BufferedReader r = new BufferedReader(new InputStreamReader(is));
			StringBuilder USDollar = new StringBuilder();
			StringBuilder Euro = new StringBuilder();
			StringBuilder BritishPound = new StringBuilder();
			StringBuilder JapanYen = new StringBuilder();
			StringBuilder HKDollar = new StringBuilder();
			String line;
			// while ((line = r.readLine()) != null) {
			Boolean USD = false, EUR = false, BGP = false, JPY = false, HKD = false;
			while ((line = r.readLine()) != null) {
				if (line.contains("US DOLLAR")
						|| line.contains("\"loaiVang\">USD")) {
					USD = true;
				}
				if (line.contains("EURO&") || line.contains("\"loaiVang\">EUR")) {
					USD = false;
					EUR = true;
				}
				if (line.contains("AUST") || line.contains("\"loaiVang\">AUD")) {
					EUR = false;
				}
				if (line.contains("BRITISH POUND")
						|| line.contains("\"loaiVang\">GBP")) {
					BGP = true;
				}
				if (line.contains("HONGKONG")
						|| line.contains("\"loaiVang\">HKD")) {
					BGP = false;
					HKD = true;
				}
				if (line.contains("INDIAN")
						|| line.contains("\"loaiVang\">INR")) {
					HKD = false;
				}
				if (line.contains("JAPAN") || line.contains("\"loaiVang\">JPY")) {
					JPY = true;
				}
				if (line.contains("class=\"note\"")) {
					JPY = false;
				}

				if (USD == true) {
					USDollar.append(line);
				}
				if (EUR == true) {
					Euro.append(line);
				}
				if (BGP == true) {
					BritishPound.append(line);
				}
				if (HKD == true) {
					HKDollar.append(line);
				}
				if (JPY == true) {
					JapanYen.append(line);
				}

			}

			String[] US_Dollar = ExtractInfo(USDollar.toString());
			String[] Euro_EUR = ExtractInfo(Euro.toString());
			String[] British_Pound = ExtractInfo(BritishPound.toString());
			String[] HK_Dollar = ExtractInfo(HKDollar.toString());
			String[] Japan_Yen = ExtractInfo(JapanYen.toString());

			content = "<table border=\"3\" bordercolor=\"#B2B28F\" cellpadding=\"1\" cellspacing=\"1\" width=\"100%\">"
					+ "<tr>"
					+ "<td width = \"10%\" align=\"center\"><div><img src=\"file:///android_asset/Giavang/dollars.png\" alt=\"\" height=\"30\" width=\"30\"></div></td>"
					+ "<td style='background:#FC9E07' width = \"30%\"><span style=\"font-family: Verdana, sans-serif; font-size: 60%; color:#FFF \">Mua vào</span></td>"
					+ "<td style='background:#FC9E07' width = \"30%\"><span style=\"font-family: Verdana, sans-serif; font-size: 60%; color:#FFF \">Chuyển khoản</span></td>"
					+ "<td style='background:#FC9E07' width = \"30%\"><span style=\"font-family: Verdana, sans-serif; font-size: 60%; color:#FFF \">Bán ra</span></td>"
					+ "</tr>";
			content += BuildMoneyRow("USD", US_Dollar)
					+ BuildMoneyRow("EUR", Euro_EUR)
					+ BuildMoneyRow("HKD", HK_Dollar)
					+ BuildMoneyRow("GBP", British_Pound)
					+ BuildMoneyRow("JPY", Japan_Yen) + "</table>";

			String usd = US_Dollar[0].replace(",", "");
			if(usd.length() > 3 && usd.contains("."))
				usd =usd.substring(0,usd.length() - 3);
			
			String euro = Euro_EUR[0].replace(",", "");
			if(euro.length() > 3 && euro.contains("."))
				euro =euro.substring(0, euro.length() - 3);
			
			textResponse = "Tỉ giá hôm nay, một đô la bằng "
					+ TextUtils.getTextOfCurrency(usd)
					+ " đồng , một ơ rô bằng "
					+ TextUtils.getTextOfCurrency(euro) + " đồng";
			
			String textDisplay = "Tỉ giá hôm nay, 1 USD = "
					+ US_Dollar[0] + " đ , 1 EUR = " + Euro_EUR[0]
					+ " đ";
			Log.i("Viva Gold TextResponse", textResponse);

			UIController.displayHtmlResult(content);
			UIController.displayResponseText(textDisplay);
			ResponseController.responseVoiceMessage(textResponse);

			finish();

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			ResponseController.responseMessage(currentFunction.getErrorMessage());
		} catch (IOException e) {
			e.printStackTrace();
			ResponseController.responseMessage(currentFunction.getErrorMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ResponseController.responseMessage(currentFunction.getErrorMessage());
		}

	}

	private String BuildMoneyRow(String name, String[] Price) {

		String res = "<tr>"
				+ "<td style='background:#FC9E07' width = \"10%\"><span style=\"font-family: Verdana, sans-serif; font-size: 100%; color:#FFF; \">"
				+ name
				+ "</span></td>"
				+ "<td  width = \"30%\"><div><span style=\"font-family: Verdana, sans-serif; font-size: 75%; color:#FFF \">"
				+ Price[0]
				+ "</span></div></td>"
				+ "<td  width = \"30%\"><div><span style=\"font-family: Verdana, sans-serif; font-size: 75%; color:#FFF \">"
				+ Price[1]
				+ "</span></div></td>"
				+ "<td  width = \"30%\"><div><span style=\"font-family: Verdana, sans-serif; font-size: 75%; color:#FFF \">"
				+ Price[2] + "</span></div></td>" + "</tr>";
		return res;

	}

	private String[] ExtractInfo(String s) {
		// TODO Auto-generated method stub
		String[] res = new String[6];

		int start = s.indexOf("cellYellow");

		Log.i("INFO",
				Integer.toString(s.length()) + "  " + s + "  "
						+ Integer.toString(start));
		Log.i("INDex", Integer.toString(s.indexOf("priceUp"))
				+ PriceOrientation(s));

		s = s.substring(start);
		start = s.indexOf(PriceOrientation(s));
		Log.i("INDex", PriceOrientation(s));
		int end = s.indexOf("</span>");
		res[0] = s.substring(start, end).replace(PriceOrientation(s) + "\" >",
				"");// .replace(".", "").replace(",", ".");
		Log.i("S", res[0]);
		// // tv.setText(res[0]);
		s = s.substring(end);
		//
		//
		//
		start = s.indexOf(PriceOrientation(s));
		s = s.substring(start);
		start = s.indexOf(PriceOrientation(s));
		end = s.indexOf("</span>");
		res[1] = s.substring(start, end).replace(PriceOrientation(s) + "\" >",
				"");// .replace(".", "").replace(",", ".");
		s = s.substring(end);

		// start = s.indexOf("priceNormal");
		start = s.indexOf(PriceOrientation(s));
		s = s.substring(start);
		start = s.indexOf(PriceOrientation(s));
		end = s.indexOf("</span>");
		res[2] = s.substring(start, end).replace(PriceOrientation(s) + "\" >",
				"");// .replace(".", "").replace(",", ".");
		s = s.substring(end);

		start = s.indexOf("cellWhite");
		s = s.substring(start);
		start = s.indexOf("cellWhite");
		end = s.indexOf("</td>");
		res[3] = s.substring(start, end).replace("cellWhite\">", "");// .replace(".",
																		// "").replace(",",
																		// ".");
		s = s.substring(end);

		start = s.indexOf("cellWhite");
		s = s.substring(start);
		start = s.indexOf("cellWhite");
		end = s.indexOf("</td>");
		res[4] = s.substring(start, end).replace("cellWhite\">", "");// .replace(".",
																		// "").replace(",",
																		// ".");
		s = s.substring(end);

		start = s.indexOf("cellWhite");
		s = s.substring(start);
		start = s.indexOf("cellWhite");
		end = s.indexOf("</td>");
		res[5] = s.substring(start, end).replace("cellWhite\">", "");// .replace(".",
																		// "").replace(",",
																		// ".");
		s = s.substring(end);

		// tv.setText(res[0]+res[1]+res[2]+res[3]+res[4]+res[5]);

		return res;
	}

	private String PriceOrientation(String s) {
		// TODO Auto-generated method stub
		String orient = "priceNormal";
		if (s.indexOf("priceUp") == -1 && s.indexOf("priceDown") == -1) {
			orient = "priceNormal";
		} else if (s.indexOf("priceNormal") == -1
				&& s.indexOf("priceDown") == -1) {
			orient = "priceUp";
		} else if (s.indexOf("priceNormal") == -1 && s.indexOf("priceUp") == -1) {
			orient = "priceDown";
		} else if (s.indexOf("priceNormal") == -1 && s.indexOf("priceUp") != -1
				&& s.indexOf("priceDown") != -1) {
			if (s.indexOf("priceUp") < s.indexOf("priceDown")) {
				orient = "priceUp";
			} else {
				orient = "priceDown";
			}
		} else if (s.indexOf("priceNormal") != -1 && s.indexOf("priceUp") == -1
				&& s.indexOf("priceDown") != -1) {
			if (s.indexOf("priceNormal") < s.indexOf("priceDown")) {
				orient = "priceNormal";
			} else {
				orient = "priceDown";
			}
		} else if (s.indexOf("priceNormal") != -1 && s.indexOf("priceUp") != -1
				&& s.indexOf("priceDown") == -1) {
			if (s.indexOf("priceUp") < s.indexOf("priceNormal")) {
				orient = "priceUp";
			} else {
				orient = "priceNormal";
			}
		} else if (s.indexOf("priceNormal") != -1 && s.indexOf("priceUp") != -1
				&& s.indexOf("priceDown") != -1) {
			int min;
			min = s.indexOf("priceNormal") < s.indexOf("priceUp") ? s
					.indexOf("priceNormal") : s.indexOf("priceUp");
			min = min < s.indexOf("priceDown") ? min : s.indexOf("priceDown");
			if (min == s.indexOf("priceNormal")) {
				orient = "priceNormal";
			} else if (min == s.indexOf("priceUp")) {
				orient = "priceUp";
			} else {
				orient = "priceDown";
			}
		}
		return orient;

	}

}
