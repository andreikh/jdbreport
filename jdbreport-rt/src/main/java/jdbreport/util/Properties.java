/*
 * Created on 22.03.2004
 *
 * Copyright (C) 2004-2006 Andrey Kholmanskih. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, write to the 
 *
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 * 
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
 */
package jdbreport.util;

import jdbreport.util.xml.ClassFactory;
import jdbreport.util.xml.XMLProperties;

import java.util.Set;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class Properties {

	private Properties() {
		xmlProperties = new XMLProperties();
	}

	private XMLProperties xmlProperties;

	private static Properties PROPERTIES;

	static {
		PROPERTIES = new Properties();
	}

	public static Set<String> getKeys() {
		return PROPERTIES.xmlProperties.getKeys();
	}

	public static Object get(String key) {
		return PROPERTIES.xmlProperties.get(key);
	}

	public static Object get(String key, Object deflt) {
		return PROPERTIES.xmlProperties.get(key, deflt);
	}

	public static int getInt(String key, int deflt) {
		return PROPERTIES.xmlProperties.getInt(key, deflt);
	}

	public static char getChar(String key, char deflt) {
		return PROPERTIES.xmlProperties.getChar(key, deflt);
	}

	public static boolean getBoolean(String key, boolean deflt) {
		return PROPERTIES.xmlProperties.getBoolean(key, deflt);
	}

	public static String getString(String key, String deflt) {
		return PROPERTIES.xmlProperties.getString(key, deflt);
	}

	public static void put(String key, Object data) {
		PROPERTIES.xmlProperties.put(key, data);
	}

	public static void put(String key, int data) {
		PROPERTIES.xmlProperties.put(key, data);
	}

	public static void put(String key, char data) {
		PROPERTIES.xmlProperties.put(key, data);
	}

	public static void put(String key, boolean data) {
		PROPERTIES.xmlProperties.put(key, data);
	}

	public static void save() {
		PROPERTIES.xmlProperties.save();
	}

	public static String saveToXML() {
		return PROPERTIES.xmlProperties.saveToXML();
	}

	public static boolean load(String filename) throws Exception {
		return PROPERTIES.xmlProperties.load(filename);
	}

	public static boolean load(String filename, ClassFactory classFactory)
			throws Exception {
		return PROPERTIES.xmlProperties.load(filename, classFactory);
	}

	public static boolean loadXML(String xml) throws Exception {
		return PROPERTIES.xmlProperties.loadXML(xml);
	}

	public static boolean loadXML(String xml, ClassFactory classFactory)
			throws Exception {
		return PROPERTIES.xmlProperties.loadXML(xml, classFactory);
	}

	public static void clear() {
		PROPERTIES.xmlProperties.clear();
	}

	public static void setFileName(String filename) {
		PROPERTIES.xmlProperties.setFileName(filename);
	}

}
