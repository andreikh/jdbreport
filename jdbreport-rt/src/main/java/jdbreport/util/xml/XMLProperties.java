/*
 * Created on 22.03.2004
 *
 * Copyright (C) 2004-2014 Andrey Kholmanskih
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package jdbreport.util.xml;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @version 3.0 13.1.2014
 * @author Andrey Kholmanskih
 * 
 */
public class XMLProperties implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(XMLProperties.class
			.getName());

	public XMLProperties() {
		fHashMap = new HashMap<>();
	}

	public XMLProperties(String fileName) throws Exception {
		this();
		load(fileName);
	}

	private String fileName;

	private HashMap<String, Object> fHashMap;

	public Set<String> getKeys() {
		return fHashMap.keySet();
	}

	public Object get(String key) {
		return fHashMap.get(key);
	}

	public Object get(String key, Object deflt) {
		Object obj = fHashMap.get(key);
		if (obj == null) {
			return deflt;
		}
		return obj;
	}

	public int getInt(String key, int deflt) {
		Object obj = fHashMap.get(key);
		if (obj == null) {
			return deflt;
		} 
		
		if (obj instanceof Integer)
			return (Integer) obj;
		
		if (obj instanceof Short)
			return ((Short) obj).intValue();
		
		return Integer.parseInt((String) obj);

	}

	public long getLong(String key, long deflt) {
		Object obj = fHashMap.get(key);
		if (obj == null) return deflt;
		if (obj instanceof Long)
			return (Long) obj;
		return Long.parseLong((String) obj);

	}

	public double getDouble(String key, double deflt) {
		Object obj = fHashMap.get(key);
		if (obj == null) {
			return deflt;
		}
		if (obj instanceof Double)
			return (Double) obj;
		else if (obj instanceof Float)
			return (Float) obj;
		else
			return Double.parseDouble((String) obj);

	}

	public char getChar(String key, char deflt) {
		Object obj = fHashMap.get(key);
		if (obj != null) {
			if (obj instanceof Character)
				return (Character) obj;
			else if (((String) obj).length() > 0)
				return ((String) obj).charAt(0);
		}
		return deflt;
	}

	public boolean getBoolean(String key, boolean deflt) {
		Object obj = fHashMap.get(key);
		if (obj == null) {
			return deflt;
		} else if (obj instanceof Boolean)
			return (Boolean) obj;
		else
			return Boolean.parseBoolean((String) obj);

	}

	public String getString(String key, String deflt) {
		Object obj = fHashMap.get(key);
		if (obj == null) {
			return deflt;
		}
		return obj.toString();
	}

	public void put(String key, Object data) {
		if (data == null) {
			fHashMap.remove(key);
		} else {
			fHashMap.put(key, data);
		}
	}

	public void put(String key, long data) {
		fHashMap.put(key, data);
	}

	public void put(String key, double data) {
		fHashMap.put(key, data);
	}

	public void put(String key, int data) {
		fHashMap.put(key, data);
	}

	public void put(String key, char data) {
		fHashMap.put(key, data);
	}

	public void put(String key, boolean data) {
		fHashMap.put(key, data);
	}

	public void save() {
		try {
			Document doc = saveProperties();
			DOMSource source = new DOMSource(doc);
			File newXML = new File(fileName);
			try (FileOutputStream os = new FileOutputStream(newXML)) {
				StreamResult result = new StreamResult(os);
				TransformerFactory transFactory = TransformerFactory
						.newInstance();
				Transformer transformer = transFactory.newTransformer();
				transformer.transform(source, result);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage());
		}
	}

	public String saveToXML() {
		try {
			Document doc = saveProperties();
			DOMSource source = new DOMSource(doc);
			StringWriter os = new StringWriter();
			StreamResult result = new StreamResult(os);
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			transformer.transform(source, result);
			return os.toString();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	/**
	 * @return Document
	 * @throws javax.xml.parsers.ParserConfigurationException
	 */
	private Document saveProperties() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		Element root = doc.createElement("app-settings");
		doc.appendChild(root);
		Element propertiesElement = doc.createElement("properties");
		root.appendChild(propertiesElement);
		for (String key : fHashMap.keySet()) {
			Element propertyElement = doc.createElement("property");
			propertyElement.setAttribute("key", key);
			Object o = get(key);
			if (o instanceof XMLStored) {
				propertyElement.setAttribute("class", o.getClass().getName());
				((XMLStored) o).store(propertyElement);
			} else if (o instanceof String[]) {
				propertyElement.setAttribute("class",
						StoredStrings.class.getName());
				new StoredStrings((String[]) o).store(propertyElement);
			} else {
				Text nameText = doc.createTextNode(o.toString());
				propertyElement.appendChild(nameText);
			}
			propertiesElement.appendChild(propertyElement);
		}
		return doc;
	}

	public boolean load(String filename) throws Exception {
		return load(filename, null);
	}

	public boolean load(String filename, ClassFactory classFactory)
			throws Exception {
		fileName = filename;
		boolean result;
		try (FileInputStream in = new FileInputStream(new File(filename))) {
			result = load(in, classFactory);
		}
		return result;
	}

	public boolean load(URL url, ClassFactory classFactory) throws Exception {
		return load(url.openStream(), classFactory);
	}

	public boolean load(URL url) throws Exception {
		return load(url.openStream(), null);
	}

	public boolean load(InputStream is, ClassFactory classFactory)
			throws ParserConfigurationException, SAXException, IOException {
		clear();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(is);
		if (doc == null) {
			doc = builder.newDocument();
			doc.appendChild(doc.createElement("app-settings"));
			return false;
		}
		return fillProperties(doc, classFactory);
	}

	public boolean loadXML(String xml) throws Exception {
		return loadXML(xml, null);
	}

	public boolean loadXML(String xml, ClassFactory classFactory)
			throws Exception {
		clear();
		if (xml == null)
			return false;
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));
		return doc != null && fillProperties(doc, classFactory);
	}

	public boolean loadXML(InputStream stream) throws Exception {
		return loadXML(stream, null);
	}

	public boolean loadXML(InputStream stream, ClassFactory classFactory)
			throws Exception {
		clear();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(stream));
		return doc != null && fillProperties(doc, classFactory);
	}

	private boolean fillProperties(Document doc, ClassFactory factory) {
		NodeList propertiesNL = doc.getDocumentElement().getChildNodes();
		if (propertiesNL != null) {
			for (int i = 0; (i < propertiesNL.getLength()); i++) {
				if (propertiesNL.item(i).getNodeName().equals("properties")) {
					NodeList propertyList = propertiesNL.item(i)
							.getChildNodes();
					for (int j = 0; j < propertyList.getLength(); j++) {
						NamedNodeMap attributes = propertyList.item(j)
								.getAttributes();
						if (attributes != null) {
							Node n = attributes.getNamedItem("key");
							Node c = attributes.getNamedItem("class");
							if (c != null &&  !"".equals(c.getNodeValue())) {
								try {
									Object o;
									if (factory == null)
										o = Class.forName(c.getNodeValue())
												.newInstance();
									else
										o = factory.getClass(c.getNodeValue());

									((XMLStored) o).load((Element) propertyList
											.item(j));
									if (o instanceof StoredStrings) {
										put(n.getNodeValue(),
												((StoredStrings) o).getValues());
									} else {
										put(n.getNodeValue(), o);
									}
								} catch (Exception e) {
									ShowError(e);
								}
							} else {
								NodeList childs = propertyList.item(j)
										.getChildNodes();
								if (childs != null) {
									for (int k = 0; k < childs.getLength(); k++) {
										if (childs.item(k).getNodeType() == Node.TEXT_NODE) {
											put(n.getNodeValue(), childs
													.item(k).getNodeValue());
										}
									}
								}
							}
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	public void clear() {
		fHashMap.clear();
	}

	public void setFileName(String filename) {
		fileName = filename;
	}

	public void ShowError(Exception e) {
		log.log(Level.SEVERE, e.getMessage(), e);
	}

	private static class StoredStrings implements XMLStored {

		private static final String STRING = "string";

		private String[] values = null;

		@SuppressWarnings("unused")
		public StoredStrings() {

		}

		public StoredStrings(String[] values) {
			this.values = values;
		}

		public void store(Element parent) {
			if (values == null)
				return;
			for (String value : values) {
				if (value != null) {
					Element child = parent.getOwnerDocument().createElement(
							STRING);
					child.appendChild(parent.getOwnerDocument().createTextNode(
							value));
					parent.appendChild(child);
				}
			}
		}

		public void load(Element parent) {
			NodeList list = parent.getChildNodes();
			values = new String[list.getLength()];
			for (int i = 0; i < list.getLength(); i++) {
				if (list.item(i).getNodeName().equals(STRING)) {
					Node textNode = list.item(i).getFirstChild();
					if (textNode != null
							&& textNode.getNodeType() == Node.TEXT_NODE) {
						values[i] = textNode.getNodeValue();
					}
				}
			}
		}

		public String[] getValues() {
			return values;
		}

	}
}
