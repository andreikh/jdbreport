/*
 * Copyright (C) 2006 Andrey Kholmanskih. All rights reserved.
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
