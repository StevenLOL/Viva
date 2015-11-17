package com.mica.viva.resource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import android.util.Log;

import com.mica.viva.ApplicationContext;
import com.mica.viva.entity.Function;
import com.mica.viva.entity.Parameter;
import com.mica.viva.utility.XmlUtils;

public class FunctionsManager {
	private static String dataPath_ = "Data/functions.xml";
	private static FunctionsManager instance_ = new FunctionsManager(dataPath_);
	private ArrayList<Function> functions_;
	private Hashtable<String, Function> dictionary_;

	public FunctionsManager(String dataPath_) {
		//
		try {
			functions_ = new ArrayList<Function>();
			dictionary_ = new Hashtable<String, Function>();

			InputStream is = ApplicationContext.getApplicationContext()
					.getAssets().open(dataPath_);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dBuilder = dbFactory
					.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();

			Log.w("ReadXML", "Root element :"
					+ doc.getDocumentElement().getNodeName());

			NodeList functionNodes = doc.getElementsByTagName("Function");
			Log.w("ReadXML", "Function Count :" + functionNodes.getLength());

			for (int i = 0; i < functionNodes.getLength(); i++) {
				if (functionNodes.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element functionNode = (Element) functionNodes.item(i);

				String functionCode = XmlUtils.getTagValue("FunctionCode",
						functionNode);
				Log.w("ReadXML", functionCode);

				String activity = XmlUtils
						.getTagValue("Activity", functionNode);
				Log.w("ReadXML", activity);

				String functionName = XmlUtils.getTagValue("FunctionName",
						functionNode);
				Log.w("ReadXML", functionName);

				String confirmSentences = XmlUtils.getTagValue(
						"ConfirmSentences", functionNode);
				Log.w("ReadXML", confirmSentences);

				String successSentences = XmlUtils.getTagValue(
						"SuccessSentences", functionNode);
				Log.w("ReadXML", successSentences);

				String errorSentences = XmlUtils.getTagValue("ErrorSentences",
						functionNode);
				Log.w("ReadXML", errorSentences);

				ArrayList<Parameter> parameters = new ArrayList<Parameter>();
				NodeList parameterNodes = functionNode
						.getElementsByTagName("Parameter");
				Log.w("ReadXML", "Param: " + parameterNodes.getLength());
				// Read Parameter
				for (int k = 0; k < parameterNodes.getLength(); k++) {
					if (parameterNodes.item(k).getNodeType() != Node.ELEMENT_NODE)
						continue;
					Element parameterNode = (Element) parameterNodes.item(k);

					String key = XmlUtils.getTagValue("Key", parameterNode);
					Log.w("ReadXML", key);

					String name = XmlUtils.getTagValue("Name", parameterNode);
					Log.w("ReadXML", name);

					String requirementSentences = XmlUtils.getTagValue(
							"RequirementSentences", parameterNode);
					Log.w("ReadXML", requirementSentences);

					boolean isRequired = Boolean.parseBoolean(XmlUtils
							.getTagValue("IsRequired", parameterNode));
					Log.w("ReadXML", isRequired + "");

					parameters.add(new Parameter(key, name,
							requirementSentences, isRequired));
				}

				Function function = new Function(functionCode, functionName,
						activity, parameters, confirmSentences,
						successSentences, errorSentences,null);
				functions_.add(function);
				dictionary_.put(functionCode, function);
			}

			Log.w("ReadXML", "Read Functions OK");
		} catch (Exception e) {
			Log.w("ReadXML", e.getMessage());
		}

	}

	/**
	 * 
	 * @return
	 */
	public static FunctionsManager getInstance() {
		return instance_;
	}

	/**
	 * get list of functions
	 * 
	 * @return
	 */
	public ArrayList<Function> getFunctions() {

		return functions_;
	}

	/**
	 * 
	 * @return
	 */
	public Hashtable<String, Function> getDictionaryFunctions() {
		return dictionary_;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public Function getFunction(String key) {
		if(key == null) return null;
		
		if (dictionary_.containsKey(key))
			return dictionary_.get(key);

		return null;
	}
	
}
