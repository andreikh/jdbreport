/*
 * Copyright (C) 2006-2014 Andrey Kholmanskih
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

import java.util.Base64;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class XMLCoder {

	public XMLCoder() {
	}

	private static final String GBase64CodeTable = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	private static byte[] gBase64 = GBase64CodeTable.getBytes();

	public static int getUnsignedByte(byte b) {
		int result = b;
		result = result << 24;
		result = result >>> 24;
		return result;
	}

	public static byte[] base64Encode(byte[] source) {
		return  Base64.getEncoder().encode(source);
	}

	public static String base64Encode(String s) {
		return Base64.getEncoder().encodeToString(s.getBytes());
	}

	private static int GetDecodeVal(byte b) throws Exception {

		if (b >= 'A' && b <= 'Z')
			return b - 65;
		if (b >= 'a' && b <= 'z')
			return b - 71;
		if (b >= '1' && b <= '9')
			return b + 4;
		if (b == '0')
			return 52;
		if (b == '+')
			return 62;
		if (b == '/')
			return 63;
		if (b == '=')
			return 0;
		throw new Exception("Error MIME coding. Invalid char code - " + b);

	}

	public static byte[] base64Decode(byte[] source) throws Exception {
		return Base64.getDecoder().decode(source);
	}

	public static String base64Decode(String source) throws Exception {
		return new String(base64Decode(source.getBytes()));
	}

	public static String convertToUTF(String s) {
		try {
			return new String(s.getBytes(), "UTF-8");
		} catch (Exception e) {
		}
		return "";
	}

	public static String replaceSpecChar(String s) {
		StringBuffer sb = new StringBuffer();
		int l = s.length();
		for (int i = 0; i < l; i++)
			switch (s.charAt(i)) {
			case '&':
				sb.append("&amp;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '\'':
				sb.append("&apos;");
				break;
			case '"':
				sb.append("&quot;");
				break;
			default:
				sb.append(s.charAt(i));
				break;
			}
		return sb.toString();
	}

	public static String replaceIllegalCharacter(String name) {
		return name.replace('$', '_');
	}

	public static String field2Xml(String nameField, Object value) {
		if (value != null) {
			return nameField + "=\""
					+ XMLCoder.replaceSpecChar(value.toString()) + "\" ";
		}
		return "";
	}

	public static String field2Element(String nameField, Object value) {
		if (value != null) {
			return "<" + nameField + ">"
					+ XMLCoder.replaceSpecChar(value.toString()) 
					+ "</" + nameField + ">";
		}
		return "";
	}

}
