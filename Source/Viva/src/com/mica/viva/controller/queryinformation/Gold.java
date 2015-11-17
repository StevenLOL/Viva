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

public class Gold extends BaseModuleController {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		currentFunction = FunctionsManager.getInstance().getFunction("GOLD");
		super.onCreate(savedInstanceState);
		initFunction();
		String content = "";
		String textResponse = "";
		String url = "http://hn.24h.com.vn/ttcb/giavang/giavang.php";
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httppost = new HttpGet(url);
			HttpResponse response = httpClient.execute(httppost);
			HttpEntity ht = response.getEntity();

			BufferedHttpEntity buf = new BufferedHttpEntity(ht);

			InputStream is = buf.getContent();

			BufferedReader r = new BufferedReader(new InputStreamReader(is));
			StringBuilder SJC10c_HCM = new StringBuilder();
			StringBuilder SJC1c_HCM = new StringBuilder();
			StringBuilder SJC_HN = new StringBuilder();

			String line;
			boolean HCM10c = false;
			boolean HCM1c = false;
			boolean HN = false;
			while ((line = r.readLine()) != null) {
				if (line.contains("SJC10c")) {
					HCM10c = true;
				}
				if (line.contains("SJC1c")) {
					HCM10c = false;
					HCM1c = true;
				}
				if (line.contains("H&#224")) {
					HCM1c = false;
					HN = true;
				}
				if (line.contains("&#272")) {
					HN = false;
				}

				if (HCM10c == true) {
					SJC10c_HCM.append(line);
				}
				if (HCM1c == true) {
					SJC1c_HCM.append(line);
				}
				if (HN == true) {
					SJC_HN.append(line);
				}
			}

			content = "<table border=\"3\" bordercolor=\"#B2B28F\"  cellpadding=\"1\" cellspacing=\"1\" width=\"100%\">"
					+ "<tr>"
					+ "<td width = \"20%\"><span></span></td>"
					+ "<td  width = \"40%\"><span style=\"font-family: Verdana, sans-serif; font-size: 80%; color:#FFF \"> Hôm nay</span></td>"
					+ "<td width = \"40%\"><span style=\"font-family: Verdana, sans-serif; font-size: 80%; color:#FFF \"> Hôm qua</span></td>"
					+ "</tr>"
					+ "</table>"
					+ "<table border=\"3\" bordercolor=\"#B2B28F\" cellpadding=\"1\" cellspacing=\"1\" width=\"100%\">"
					+ "<tr>"
					+ "<td width = \"20%\" align=\"center\"><div><img src=\"file:///android_asset/Giavang/gold_update.png\" alt=\"\" height=\"35\" width=\"35\"></div></td>"
					+ "<td  width = \"20%\"><span style=\"font-family: Verdana, sans-serif; font-size: 80%; color:#FFF \">Mua vào</span></td>"
					+ "<td  width = \"20%\"><span style=\"font-family: Verdana, sans-serif; font-size: 80%; color:#FFF \">Bán ra</span></td>"
					+ "<td width = \"20%\"><span style=\"font-family: Verdana, sans-serif; font-size: 80%; color:#FFF \">Mua vào</span></td>"
					+ "<td width = \"20%\"><span style=\"font-family: Verdana, sans-serif; font-size: 80%; color:#FFF \">Bán ra</span></td>"
					+ "</tr>"
					+ "<tr>"
					+ "<td colspan = \"5\" bgcolor = \"#FC9E07\" ><span style=\"font-family: Verdana, sans-serif; font-size: 80%; color:#FFF \">Giá vàng tại HCM</span></td>"
					+ "</tr>";

			String[] Price_HCMSJC10c = Replace(SJC10c_HCM.toString());
			String[] Price_HCMSJC1c = Replace(SJC1c_HCM.toString());
			String[] Price_HNSJC = Replace(SJC_HN.toString());

			Log.i("Gold Price", Price_HCMSJC10c[0] + " " + Price_HCMSJC10c[1]
					+ " " + Price_HCMSJC10c[2]);

			content += BuildRow("SJC10c", Price_HCMSJC10c)
					+ BuildRow("SJC1c", Price_HCMSJC1c)
					+ "<tr>"
					+ "<td colspan = \"5\" bgcolor = \"#FC9E07\" ><span style=\"font-family: Verdana, sans-serif; font-size: 80%; color:#FFF \">Giá vàng tại Hà Nội</span></td>"
					+ "</tr>" + BuildRow("SJC", Price_HNSJC) + "</table>";

			textResponse = "Giá vàng hôm nay, mua vào , "
					+ TextUtils.getTextOfCurrency(Price_HNSJC[0].replace(".",
							"") + "000")
					+ " đồng một lượng , bán ra , "
					+ TextUtils.getTextOfCurrency(Price_HNSJC[1].replace(".",
							"") + "000") + " đồng một lượng .";

			String textDisplay = "Giá vàng hôm nay, mua vào " + Price_HNSJC[0]
					+ ".000 đ/lượng, bán ra " + Price_HNSJC[1]
					+ ".000 đ/lượng.";

			Log.i("Viva Gold TextResponse", textResponse);
			ResponseController.responseVoiceMessage(textResponse);
			UIController.displayHtmlResult(content);
			UIController.displayResponseText(textDisplay);
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

	private String BuildRow(String name, String[] Price) {
		// TODO Auto-generated method stub
		String orient_sell = "", orient_buy = "";
		if (!Price[0].equals("") && !Price[0].equals("Không có")
				&& !Price[1].equals("") && !Price[3].equals("Không có")) {
			if (Double.parseDouble(Price[0]) > Double.parseDouble(Price[2])) {
				orient_buy = "price_up.png";
			} else {
				orient_buy = "price_down.png";
			}
			if (Double.parseDouble(Price[1]) > Double.parseDouble(Price[3])) {
				orient_sell = "price_up.png";
			} else {
				orient_sell = "price_down.png";
			}
		} else {
			orient_buy = orient_sell = "gold_update.png";
		}
		String res = "<tr>"
				+ "<td width = \"20%\"><span style=\"font-family: Verdana, sans-serif; font-size: 100%; color:#FFF \">"
				+ name
				+ "</span></td>"
				+ "<td  width = \"20%\"><div><span style=\"font-family: Verdana, sans-serif; font-size: 75%; color:#FFF \">"
				+ Price[0]
				+ "</span><img src=\"file:///android_asset/Giavang/"
				+ orient_buy
				+ "\" alt=\"\" height=\"10\" width=\"10\"></div></td>"
				+ "<td  width = \"20%\"><div><span style=\"font-family: Verdana, sans-serif; font-size: 75%; color:#FFF \">"
				+ Price[1]
				+ "</span><img src=\"file:///android_asset/Giavang/"
				+ orient_sell
				+ "\" alt=\"\" height=\"10\" width=\"10\"></div></td><td width = \"20%\"><span style=\"font-family: Verdana, sans-serif; font-size: 80%; color:#FFF \">"
				+ Price[2]
				+ "</span></td><td width = \"20%\"><span style=\"font-family: Verdana, sans-serif; font-size: 80%; color:#FFF \">"
				+ Price[3] + "</span></td></tr>";
		return res;
	}

	public String[] Replace(String s) {

		String Price[] = new String[4];
		String buyPrice = "", SellPrice = "", buyPriceYes = "", SellPriceYes = "";

		s = s.replace("\"cellWhite\"><span", "");
		if (s.contains("priceUp") || s.contains("priceDown")) {
			// if(s.contains("priceUp"))
			// {
			int start = s.indexOf(GoldOrientation(s));
			Log.i("GOld orient", GoldOrientation(s));
			int end = s.indexOf("</span><img src");

			buyPrice = s.substring(start, end).replace(GoldOrientation(s), "");
			s = s.substring(end);

		} else {
			int start = s.indexOf("\"cellYellow\"><span>");
			s = s.substring(start);
			start = s.indexOf("\"cellYellow\"><span>");
			int end = s.indexOf("</span></td>");

			buyPrice = s.substring(start, end).replace("\"cellYellow\"><span>",
					"");
			s = s.substring(end);
			// buyPrice = "";
		}
		if (s.contains("priceUp") || s.contains("priceDown")) {
			int start = s.indexOf(GoldOrientation(s));
			s = s.substring(start);
			start = s.indexOf("\"priceUp\">");
			int end = s.indexOf("</span><img src");
			SellPrice = s.substring(start, end).replace(GoldOrientation(s), "");
			s = s.substring(end);

		} else {

			int start = s.indexOf("\"cellYellow\"><span >");
			s = s.substring(start);

			start = s.indexOf("\"cellYellow\"><span >");
			int end = s.indexOf("</span></td>");

			SellPrice = s.substring(start, end).replace(
					"\"cellYellow\"><span >", "");
			s = s.substring(end);
		}
		if (s.contains("cellWhite")) {
			int start = s.indexOf("cellWhite");
			s = s.substring(start);
			start = s.indexOf("cellWhite");
			int end = s.indexOf("</td>");
			buyPriceYes = s.substring(start, end).replace("cellWhite\">", "");
			s = s.substring(end);
		}
		if (s.contains("cellWhite")) {
			int start = s.indexOf("cellWhite");
			s = s.substring(start);
			start = s.indexOf("cellWhite");
			int end = s.indexOf("</td>");
			SellPriceYes = s.substring(start, end).replace("cellWhite\">", "");
			s = s.substring(end);
		}
		Price[0] = buyPrice;
		Price[1] = SellPrice;
		Price[2] = buyPriceYes;
		Price[3] = SellPriceYes;
		Log.i("Price Info", buyPrice + "&" + buyPriceYes + "&" + SellPrice
				+ "&" + SellPriceYes);

		return Price;
	}

	public String GoldOrientation(String s) {
		// TODO Auto-generated method stub
		String orient = "priceUp";
		if (s.indexOf("priceUp") == -1 && s.indexOf("priceDown") != -1) {
			orient = "piceDown";
		}
		if (s.indexOf("priceDown") == -1 && s.indexOf("priceUp") != -1) {
			orient = "priceUp";
		}
		if (s.indexOf("priceDown") != -1 && s.indexOf("priceUp") != -1) {
			if (s.indexOf("priceUp") < s.indexOf("priceDown")) {
				orient = "priceUp";
			} else {
				orient = "priceDown";
			}
		}
		return "\"" + orient + "\">";
	}

}
