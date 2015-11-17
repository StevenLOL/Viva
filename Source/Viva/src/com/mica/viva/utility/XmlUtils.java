package com.mica.viva.utility;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtils {
	/**
	 * Get Node value in xml document
	 * 
	 * @param sTag
	 * @param eElement
	 * @return
	 */
	public static String getTagValue(String sTag, Element eElement) {
		try {
			NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
					.getChildNodes();
			Node nValue = (Node) nlList.item(0);
			return nValue.getNodeValue();
		} catch (Exception ex) {
			return null;
		}
	}
}
