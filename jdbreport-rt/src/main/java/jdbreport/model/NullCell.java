/*
 * Created on 26.12.2004
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2004-2013 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.model;

import java.awt.Image;

import javax.swing.Icon;

import jdbreport.design.model.CellObject;

/**
 * @version 2.2 13.04.2013
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class NullCell implements Cell {


	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.Cell#isNull()
	 */
	public boolean isNull() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.Cell#getValue()
	 */
	public Object getValue() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.Cell#setValue(java.lang.Object)
	 */
	public void setValue(Object value) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.Cell#getRowSpan()
	 */
	public int getRowSpan() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.Cell#setRowSpan(int)
	 */
	public void setRowSpan(int value) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.Cell#getColSpan()
	 */
	public int getColSpan() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.Cell#setColSpan(int)
	 */
	public void setColSpan(int value) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.Cell#isChild()
	 */
	public boolean isChild() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.Cell#isSpan()
	 */
	public boolean isSpan() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.Cell#getOwner()
	 */
	public Cell getOwner() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.Cell#setOwner(jdbreport.interfaces.Cell)
	 */
	public void setOwner(Cell cell) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.Cell#getFontIndex()
	 */
	public Object getStyleId() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.interfaces.Cell#setFontIndex(int)
	 */
	public void setStyleId(Object index) {

	}

	public int getExtFlags() {
		return 0;
	}

	public void setExtFlags(int i) {

	}

	public boolean isNotPrint() {
		return false;
	}

	public void setNotPrint(boolean b) {

	}

	public Object clone() {
		return this;
	}

	public String getText() {
		return EMPTY_STRING;
	}

	public void setIcon(Icon icon) {
	}

	public Icon getIcon() {
		return null;
	}

	public boolean isScaleIcon() {
		return false;
	}

	public void setScaleIcon(boolean scale) {
	}

	public void setImageFormat(String format) {
	}

	public String getImageFormat() {
		return null;
	}

	public String getContentType() {
		return TEXT_PLAIN;
	}

	public void clear() {
	}

	public int getTotalFunction() {
		return CellObject.AF_NONE;
	}

	public void setTotalFunction(int func) {
	}

	public Type getValueType() {
		return Type.STRING;
	}

	public void setValueType(Type valueType) {
	}

	public int getDecimals() {
		return 0;
	}

	public void setDecimals(int p) {
	}

	public boolean isEditable() {
		return true;
	}

	public void setEditable(boolean b) {
	}

	public void setImage(Image image) {
	}

	public Picture getPicture() {
		return null;
	}

	public void setPicture(Picture picture) {
	}

	@Override
	public String getCellFormula() {
		return null;
	}

	@Override
	public void setCellFormula(String formula) {
	}
}
