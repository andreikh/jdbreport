/*
 * Created on 30.01.2005
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2005-2008 Andrey Kholmanskih. All rights reserved.
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
 * Andrey Kholmanskih
 * support@jdbreport.com
 */
package jdbreport.model.io.xml;

import java.awt.Image;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import jdbreport.model.Cell;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import and.util.xml.XMLCoder;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class CellParser6 extends DefaultReportParser {

	private Cell cell;

	public CellParser6(DefaultReaderHandler reportHandler) {
		super(reportHandler);
	}

	/**
	 * @param reportHandler
	 */
	public CellParser6(DefaultReaderHandler reportHandler, Cell cell) {
		super(reportHandler);
		this.setCell(cell);
	}

	public boolean startElement(String name, Attributes attributes) {
		if (name.equals("t")) {
			return true;
		}
		if (name.equals("print")) {
			return true;
		}
		if (name.equals("formula")) {
			return true;
		}
		if (name.equals("ExtFlags")) {
			return true;
		}
		if (name.equals("img")) {
			getCell().setImageFormat(attributes.getValue("type"));
			if (attributes.getValue("stretch") != null) {
				getCell().setScaleIcon(
						Boolean.parseBoolean(attributes.getValue("stretch")));
			}
			return true;
		}
		return false;
	}

	public void endElement(String name, StringBuffer value) {
		if (name.equals("t")) {
			getCell().setValue(value.toString());
			return;
		}
		if (name.equals(CELL)) {
			try {
				getHandler().popHandler(name);
			} catch (SAXException e) {
				e.printStackTrace();
			}
			return;
		}
		if (name.equals("ExtFlags")) {
			int flag = Integer.parseInt(value.toString());
			getCell().setExtFlags(flag);
			if ((flag & 16) != 0) {
				getCell().setValue(parseFormula(getCell().getText()));
			}
			return;
		}
		if (name.equals("print")) {
			getCell().setNotPrint(!Boolean.parseBoolean(value.toString()));
			return;
		}
		if (name.equals("img")) {
			if (value.length() > 0) {
				try {
					Image image = ImageIO.read(new ByteArrayInputStream(
							XMLCoder.base64Decode(value.toString().trim()
									.getBytes())));
					cell.setIcon(new ImageIcon(image));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return;
		}
	}

	/**
	 * @param cell
	 *            The cell to set.
	 */
	public void setCell(Cell cell) {
		this.cell = cell;
	}

	/**
	 * @return the cell.
	 */
	public Cell getCell() {
		return cell;
	}

	/*
	 * Transformation of formulas
	 */

	private String[] ArrNameSymbol = { "Delta", "Downarrow", "Gamma", "Lambda",
			"LeftArrow", "Leftrightarrow", "Omega", "Phi", "Pi", "Psi",
			"RightArrow", "Sigma", "Theta", "Uparrow", "Upsilon", "Xi",
			"alpha", "angle", "approx", "beta", "bullet", "cap", "cdot", "chi",
			"cong", "delta", "diamond", "div", "downarrow", "epsilon", "equiv",
			"eta", "gamma", "ge", "gets", "in", "infinity", "iota", "kappa",
			"lambda", "le", "mu", "ne", "notin", "nu", "omega", "oplus",
			"oslash", "otimes", "partial", "perp", "phi", "pi", "pm", "psi",
			"rho", "sigma", "sqrt", "subset", "subseteq", "supset", "tau",
			"theta", "times", "to", "uncup", "uparrow", "upsilon",
			"varepsilon", "varphi", "varpi", "varsigma", "vee", "wedge", "xi",
			"zeta" };

	private char[] ArrCodeCharSymbol = { 8710, 8659, 915, 923, 8656, 8660, 937,
			934, 928, 929, 8658, 931, 920, 8657, 933, 926, 945, 8736, 8776,
			946, 8729, 8745, 8901, 967, 8780, 948, 8900, 247, 8595, 949, 8801,
			951, 947, 8805, 8592, 8712, 8734, 953, 954, 955, 8804, 956, 8800,
			8713, 957, 969, 8853, 8856, 8855, 8706, 8869, 981, 960, 177, 968,
			961, 963, 8730, 8834, 8838, 8835, 964, 952, 215, 8594, 8899, 8593,
			965, 949, 966, 982, 986, 8897, 8896, 958, 950 };

	private char[] Blank = { 9, 10, 13, ' ' };
	private char[] Special = { '\\', '^', '_', '}', '{' };
	private int EnPos;
	private String SubStr;
	private char CodeSymbol;

	private enum TToken {
		toEnd, toUpIndex, toDownIndex, toMacros, toOpenBraces, toCloseBraces, toString
	}

	private boolean containingChar(char[] charArr, char ch) {
		for (int i = 0; i < charArr.length; i++) {
			if (charArr[i] == ch)
				return true;
		}
		return false;
	}

	private boolean inLetter(char ch) {
		return (ch >= '!' && ch <= 'Z') || (ch >= '`' && ch <= 'z')
				|| (ch >= 'А' && ch <= 'я') || (ch == 'ё');

	}

	private TToken GetToken(String Str, int StPos) {
		SubStr = "";
		while (Str.length() > StPos && containingChar(Blank, Str.charAt(StPos)))
			StPos++;
		EnPos = StPos;
		if (Str.length() <= StPos) {
			return TToken.toEnd;
		}
		switch (Str.charAt(EnPos)) {
		case '_':
		case '^':
			TToken Result;
			if (Str.charAt(EnPos) == '_')
				Result = TToken.toDownIndex;
			else
				Result = TToken.toUpIndex;
			EnPos++;
			while (Str.length() > EnPos && inLetter(Str.charAt(EnPos)))
				EnPos++;
			SubStr = Str.substring(StPos + 1, EnPos);
			return Result;
		case '\\':
			EnPos++;
			if (Str.length() <= EnPos)
				return TToken.toEnd;
			if (containingChar(Blank, Str.charAt(EnPos))) {
				SubStr = " ";
				EnPos++;
				return TToken.toMacros;
			}
			if (containingChar(Special, Str.charAt(EnPos))) {
				SubStr = Str.substring(EnPos, EnPos);
				EnPos++;
				return TToken.toMacros;
			}
			while (Str.length() > EnPos && inLetter(Str.charAt(EnPos)))
				EnPos++;
			SubStr = Str.substring(StPos + 1, EnPos);
			return TToken.toMacros;
		case '{':
			EnPos++;
			return TToken.toOpenBraces;
		case '}':
			EnPos++;
			return TToken.toCloseBraces;
		default:
			if (containingChar(Special, Str.charAt(EnPos))) {
				SubStr = Str.substring(StPos, EnPos);
				return TToken.toString;
			}
			while (Str.length() > EnPos && inLetter(Str.charAt(EnPos)))
				EnPos++;
			SubStr = Str.substring(StPos, EnPos);
			return TToken.toString;
		}
	}

	private boolean IsMathSymbol(String Name) {
		int M = 0;
		int N = ArrNameSymbol.length - 1;
		int K;
		while (M <= N) {
			K = M + (N - M) / 2;
			if (Name.equals(ArrNameSymbol[K])) {
				CodeSymbol = ArrCodeCharSymbol[K];
				return true;
			} else if (Name.compareTo(ArrNameSymbol[K]) > 0) {
				M = K + 1;
			} else {
				N = K - 1;
			}
		}
		return false;
	}

	private String parseFormula(String text) {
		StringBuffer result = new StringBuffer();
		TToken Token;
		int StPos = 0;
		boolean isHTML = false;
		while (true) {
			Token = GetToken(text, StPos);
			if (Token == TToken.toEnd) {
				break;
			}
			switch (Token) {
			case toUpIndex:
			case toDownIndex:
				if (Token == TToken.toUpIndex)
					result.append("<sup>");
				else
					result.append("<sub>");
				result.append("<small>");
				result.append(SubStr);
				result.append("</small>");
				if (Token == TToken.toUpIndex)
					result.append("</sup>");
				else
					result.append("</sub>");
				isHTML = true;
				break;
			case toMacros:
				if (IsMathSymbol(SubStr)) {
					result.append(CodeSymbol);
				} else if (SubStr.length() == 1
						&& (SubStr.charAt(0) == ' ' || containingChar(Special,
								SubStr.charAt(0)))) {
					result.append(SubStr);
				} else if (SubStr.equals("it")) {
					result.append("<i>");
					isHTML = true;
				} else if (SubStr.equals("bl")) {
					result.append("<b>");
					isHTML = true;
				} else if (SubStr.equals("st")) {
					result.append("<s>");
					isHTML = true;
				} else if (SubStr.equals("ul")) {
					result.append("<u>");
					isHTML = true;
				} else if (SubStr.equals("rm")) {
					result.append("</i></b></u></s>");
					isHTML = true;
				}
				break;
			case toString:
				result.append(SubStr);
				break;
			}
			StPos = EnPos;
		}
		if (isHTML) {
			result.insert(0, "<html>");
			result.append("</html>");
		}
		return result.toString();
	}

}
