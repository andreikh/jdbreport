/*
 * JDBReport Generator
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
package jdbreport.model;

import java.awt.Image;
import java.io.Serializable;

import javax.swing.Icon;

/**
 * 
 * The interface of the cell
 * 
 * @version 2.2 13.04.2013
 * @author Andrey Kholmanskih
 * 
 */
public interface Cell extends Cloneable, Serializable {

	long serialVersionUID = 1L;

	enum Type {
		STRING, FLOAT, DATE, TIME, BOOLEAN, PERCENTAGE, CURRENCY
	}

	Type DEFAULT_TYPE = Type.STRING;

	String TEXT_PLAIN = "text/plain";

	String TEXT_HTML = "text/html";
	
	String EMPTY_STRING = "";

	/**
	 * Returns the value for the cell
	 * 
	 * @return the value Object at the cell
	 */
	Object getValue();

	/**
	 * 
	 * @param value
	 *            to assign to cell
	 */
	void setValue(Object value);

	/**
	 * 
	 * @return the string value of the cell
	 */
	String getText();

	/**
	 * Key of the CellStyle in the map of the CellStyles
	 * 
	 * @return the CellStyle's id
	 */
	Object getStyleId();

	/**
	 * Sets the CellStyle's id for the cell
	 * 
	 * @param id
	 *            the CellStyle's id
	 */
	void setStyleId(Object id);

	/**
	 * Returns a number of spanned rows for the cell
	 * 
	 * @return rowSpan of the cell
	 */
	int getRowSpan();

	/**
	 * Sets a number of spanned rows for the cell
	 * 
	 * @param value
	 *            a number of spanned rows
	 */
	void setRowSpan(int value);

	/**
	 * Returns a number of spanned columns for the cell
	 * 
	 * @return colSpan of the cell
	 */
	int getColSpan();

	/**
	 * Sets a number of spanned columns for the cell
	 * 
	 * @param value
	 *            a number of spanned columns
	 */
	void setColSpan(int value);

	/**
	 * Returns true if the cell has an owner
	 * 
	 * @return true if the cell has an owner
	 */
	boolean isChild();

	/**
	 * 
	 * @return true if the rowSpan or the columnSpan is more than zero
	 */
	boolean isSpan();

	/**
	 * Returns the owner of the cell, can be null
	 * 
	 * @return owner of the cell
	 */
	Cell getOwner();

	/**
	 * Sets the owner of the cell
	 * 
	 * @param cell
	 *            the owner of the cell, can be null
	 */
	void setOwner(Cell cell);

	/**
	 * Determines if the cell is null
	 * 
	 * @return true if the cell is null
	 */
	boolean isNull();

	/**
	 * 
	 * @return extFlags property
	 */
	int getExtFlags();

	void setExtFlags(int i);

	/**
	 * Returns true if the cell is not printed
	 * 
	 * @return true if the cell is not printed
	 */
	boolean isNotPrint();

	/**
	 * Sets the notPrint property, which must be true to disable printing of the
	 * cells
	 * 
	 * @param b
	 *            if true the cell is not printed
	 */
	void setNotPrint(boolean b);

	Object clone();

	/**
	 * Sets an icon to the cell
	 * 
	 * @param icon
	 *            the cell icon
	 */
	void setIcon(Icon icon);

	/**
	 * Sets an icon to the cell
	 * 
	 * @param image
	 *            the cell image
	 * @since 1.2           
	 */
	void setImage(Image image);
	
	/**
	 * Returns the icon of the cell
	 * 
	 * @return the icon of the cell
	 * @deprecated use getPicture()
	 */
	Icon getIcon();

	/**
	 * Returns the picture of the cell
	 * 
	 * @return the picture of the cell
	 * @since 2.0
	 */
	Picture getPicture();

	/**
	 * Sets an picture to the cell
	 * 
	 * @param picture the cell picture
	 * @since 2.0
	 */
	void setPicture(Picture picture);
	
	/**
	 * Sets a scale of the icon. If true, the icon's sizes are set like the
	 * cell's sizes
	 * 
	 * @param scale
	 *            scaleIcon property
	 */
	void setScaleIcon(boolean scale);

	/**
	 * Determines whether the icons are scaled
	 * 
	 * @return true if the icon is scaled
	 */
	boolean isScaleIcon();

	/**
	 * 
	 * @param format
	 *            the image's format e.g. "bmp", "png", "jpg"
	 */
	void setImageFormat(String format);

	/**
	 * 
	 * @return the image's format e.g. "bmp", "png", "jpg"
	 */
	String getImageFormat();

	/**
	 * 
	 * @return the cell's content e.g. "text/plain" or "text/html"
	 */
	String getContentType();

	/**
	 * Sets all properties by default
	 * 
	 */
	void clear();

	/**
	 * 
	 * @return the type of the cell's value
	 */
	Type getValueType();

	/**
	 * 
	 * @param valueType
	 *            new type of the cell's value
	 */
	void setValueType(Type valueType);

	/**
	 * Default true
	 * 
	 * @return editable
	 */
	boolean isEditable();

	/**
	 * Sets the editable property, which must be false to disable edit of the
	 * cells
	 * 
	 * @param b
	 *            if true the cell is editable
	 */
	void setEditable(boolean b);

	/**
	 * Cell formula. Not calculate value. Only for export to Spreadsheet 
	 * 
	 * @return Spreadsheet formula
	 * @since 2.2
	 */
	String getCellFormula();

	/**
	 * Cell formula. Not calculate value. Only for export to Spreadsheet
	 *  
	 * @param formula Spreadsheet formula
	 * @since 2.2
	 */
	void setCellFormula(String formula);
}
