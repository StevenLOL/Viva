package com.mica.viva.resource;

import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

import com.mica.viva.ApplicationContext;
import com.mica.viva.entity.Function;
import com.mica.viva.entity.Sentence;
import com.mica.viva.utility.XmlUtils;

public class SentencesManager {
	private static String dataPath_ = "Data/sentences.xml";
	private static SentencesManager instance_ = new SentencesManager(dataPath_);
	private ArrayList<Sentence> sentences_;
	private Hashtable<String, ArrayList<Sentence>> dictionary_;

	/**
	 * 
	 * @return
	 */
	public static SentencesManager getInstance() {
		return instance_;
	}

	/**
	 * 
	 * @param dataPath
	 */
	public SentencesManager(String dataPath) {
		// Read XML Data
		readData(dataPath);
	}

	/**
	 * Get all of sentences in xml data file
	 * 
	 * @return
	 */
	public ArrayList<Sentence> getSentences() {
		return sentences_;
	}

	/**
	 * Get dictionary which has key is sentence's type, value is ArrayList of
	 * senteces which has this type
	 * 
	 * @return
	 */
	public Hashtable<String, ArrayList<Sentence>> getDictionarySentences() {
		return dictionary_;
	}

	/**
	 * Get ArrayList of sentences by type
	 * 
	 * @param type
	 * @return
	 */
	public ArrayList<Sentence> getSentences(String type) {
		return dictionary_.get(type);
	}

	/**
	 * Get a random sentence which has the inputed type
	 * 
	 * @param type
	 * @return
	 */
	public Sentence getRandom(String type) {
		ArrayList<Sentence> sentences = getSentences(type);
		Log.w("Sentence Random", sentences.size()+"");
		if (sentences.size() == 0)
			return null;
		Random generator = new Random();
		int index = generator.nextInt(sentences.size());
		Log.w("Sentence Random", index+"");
		return sentences.get(index);
	}

	// Read data form xml file to ArrayList and buid dictionary
	private void readData(String dataPath) {
		try {
			sentences_ = new ArrayList<Sentence>();
			dictionary_ = new Hashtable<String, ArrayList<Sentence>>();

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

			NodeList sentenceNodes = doc.getElementsByTagName("Sentence");
			Log.w("ReadXML", "Sentence Count :" + sentenceNodes.getLength());

			for (int i = 0; i < sentenceNodes.getLength(); i++) {
				if (sentenceNodes.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element sentenceNode = (Element) sentenceNodes.item(i);

				String type = XmlUtils.getTagValue("Type", sentenceNode);
				Log.w("ReadXML", type);

				String content = XmlUtils.getTagValue("Content", sentenceNode);
				Log.w("ReadXML", content);

				Sentence sentence = new Sentence(type, content);
				sentences_.add(sentence);
				Log.w("ReadXML", "Sentences Count: "
						+ sentences_.size() + "");

				if (!dictionary_.containsKey(type)) {
					dictionary_.put(type, new ArrayList<Sentence>());
				}
				dictionary_.get(type).add(sentence);
				Log.w("ReadXML", "Sentences Tpye " + type + ": "
						+ dictionary_.get(type).size() + "");
			}
		} catch (Exception e) {
			Log.w("ReadXML", e.getMessage());
		}
	}

}
