package com.mica.viva.controller.queryinformation;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class XMLParser extends Activity {

	// constructor
	public XMLParser() {

	}

	/**
	 * Getting XML from URL making HTTP request
	 * 
	 * @param url
	 *            string
	 * */

	public static String[][] Conditon = { { "cloudy", "nhiều mây" },
			{ "clear", "trời quang" }, { "mostlycloudy", "nhiều mây" },
			{ "partlycloudy", "ít mây" }, { "chancerain", "mưa nhẹ" },
			{ "rain", "mưa rải rác" }, { "chancesnow", "tuyết" },
			{ "chancetstorms", "mưa lớn" }, { "snow", "tuyết" },
			{ "sunny", "trời đẹp" }, { "cloudyrain", "ít mây, trời mưa" },
			{ "fog", "sương mù" }, { "haze", "có gió" },
			{ "icyrain", "mưa đá" }, { "tstorms", "có lúc có giông" },
			{ "overcast", "trời âm u" },

	};
	public static String[][] Weekday = { { "Monday", "Thứ hai" },
			{ "Tuesday", "Thứ ba" }, { "Wednesday", "Thứ tư" },
			{ "Thursday", "Thứ năm" }, { "Friday", "Thứ sáu" },
			{ "Saturday", "Thứ bảy" }, { "Sunday", "Chủ nhật" }, };

	public String getConditionString(String s) {
		String res = "";
		for (int i = 0; i < Conditon.length; i++) {
			if (s.equals(Conditon[i][0])) {
				res = Conditon[i][1];
			}
		}
		return res;
	}

	public String getWeekdayString(String s) {
		String res = "";
		for (int i = 0; i < Weekday.length; i++) {
			if (s.equals(Weekday[i][0])) {
				res = Weekday[i][1];
			}
		}
		return res;
	}

	public String getXmlFromUrl(String url) {
		String xml = null;

		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(httpEntity);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// return XML
		return xml;
	}

	// getXML method
	public static String getXML(InputStream is) throws IOException {

		BufferedInputStream bis = new BufferedInputStream(is);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result = bis.read();
		while (result != -1) {
			byte b = (byte) result;
			buf.write(b);
			result = bis.read();
		}
		return buf.toString();
	}

	// XMLfromString method

	public String getXmlFromAssets(Context con, String filename) {
		String xml = null;
		try {
			xml = getXML(con.getAssets().open(filename));
		} catch (Exception e) {
			Log.d("Error", e.toString());
		}
		return xml;
	};

	/**
	 * Getting XML DOM element
	 * 
	 * @param XML
	 *            string
	 * */
	public Document getDomElement(String xml) {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);

		} catch (ParserConfigurationException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		}

		return doc;
	}

	/**
	 * Getting node value
	 * 
	 * @param elem
	 *            element
	 */
	public final String getElementValue(Node elem) {
		Node child;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (child = elem.getFirstChild(); child != null; child = child
						.getNextSibling()) {
					if (child.getNodeType() == Node.TEXT_NODE) {
						return child.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	/**
	 * Getting node value
	 * 
	 * @param Element
	 *            node
	 * @param key
	 *            string
	 * */
	public String getValue(Element item, String str) {
		NodeList n = item.getElementsByTagName(str);
		return this.getElementValue(n.item(0));
	}

	public String parseElement(Document SrcDoc, String TagName, String Item) {
		// TODO Auto-generated method stub
		String Result = "";
		Node node = SrcDoc.getElementsByTagName(TagName).item(0);
		Result = node.getAttributes().getNamedItem(Item).getNodeValue()
				.toString();
		return Result;
	}

	public ArrayList<String> parseListSingle(Document SrcDoc, String tagName1) {
		ArrayList<String> List = new ArrayList<String>();
		NodeList nodeListTag1 = SrcDoc.getElementsByTagName(tagName1);
		if (nodeListTag1.getLength() > 0) {
			for (int i = 0; i < nodeListTag1.getLength(); i++) {
				List.add(nodeListTag1.item(i).getTextContent());
			}
		} else {
			List.clear();
		}
		return List;
	}

}
