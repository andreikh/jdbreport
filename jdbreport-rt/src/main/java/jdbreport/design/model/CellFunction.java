/*
 * JDBReport Generator
 * 
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
package jdbreport.design.model;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Iterator;

import jdbreport.model.Border;
import jdbreport.model.Cell;
import jdbreport.model.ReportException;
import jdbreport.model.print.ReportPage;
import jdbreport.source.ReportDataSet;

/**
 * 
 * Interface for function in Cell
 * 
 * @version 2.0 28.05.2010
 * @author Andrey Kholmanskih
 * 
 */
public interface CellFunction {

	/**
	 * Runs the current function <br>
	 * do not use this method directly
	 * 
	 * @throws ReportException
	 */
	void run() throws ReportException;

	/**
	 * Returns value of the current cell. If cell's value is null, returns empty
	 * string
	 * 
	 * @return value of the cell
	 */
	String getText();

	/**
	 * Returns value of the current cell
	 * 
	 * @return value of the cell
	 */
	Object getValue();

	/**
	 * Sets value to the current cell
	 * 
	 * @param value
	 *            new value
	 */
	void setValue(Object value);

	/**
	 * Sets value as Image to the current cell
	 * @param value Image, Icon, byte[], InputStream, File
	 * @since 2.0
	 */
	void setImage(Object value);

	/**
	 * Sets value as Image to the current cell
	 * @param value Image, Icon, byte[], String, InputStream, File
	 * @param format image format
	 * @since 2.0
	 */
	void setImage(Object value, String format);
	
	/**
	 * Sets value as MathML data to the current cell
	 * @param value String , File, Reader, InputStream
	 * @since 2.0
	 */
	void setFormula(Object value);
	
	/**
	 * Returns the current cell
	 * 
	 * @return the cell's object
	 */
	Cell getCell();

	/**
	 * Returns the Cell by row and column
	 * 
	 * @param row the row's number
	 * @param column the column's number
	 * @return the cell's object
	 */
	Cell getCell(int row, int column);

	/**
	 * Returns a number of the current row, since with 0
	 * 
	 * @return a row's number
	 */
	int getRow();

	/**
	 * Returns a number of the current column, since with 0
	 * 
	 * @return a column's number
	 */
	int getColumn();
	
	/**
	 * Returns number column as Symbol 0 - A, 1 - B ....
	 * 
	 * @param c column number
	 * @return number column as symbol
	 * @since 2.2 
	 */
	String asSymbol(int c);

	/**
	 * Returns the ReportDataSet
	 * 
	 * @param alias
	 *            the ReportDataSet alias
	 * @return ReportDataSet
	 * @see ReportDataSet
	 */
	ReportDataSet getDataSet(Object alias);

	void setDataSet(ReportDataSet ds);

	void setDataSet(String alias, Iterable<?> ds);

	void setDataSet(String alias, Iterator<?> ds);

	void setDataSet(String alias, Object ds);

	void setDataSet(String alias, Object[] ds);

	/**
	 * Returns a variable's value by name
	 * 
	 * @param name
	 *            the variable's name
	 * @return a variable's value
	 */
	Object getVarValue(Object name);

	/**
	 * Returns the value of the variable as an Integer by name
	 * 
	 * @param name
	 *            the variable's name
	 * @param def
	 *            default value
	 * @return the value of the variable
	 */
	Integer getVarValue(Object name, Integer def);

	/**
	 * Returns the value of the variable as a Double by name
	 * 
	 * @param name
	 *            the variable's name
	 * @param def
	 *            default value
	 * @return the value of the variable
	 */
	Double getVarValue(Object name, Double def);

	/**
	 * Returns the value of the variable as a Boolean by name
	 * 
	 * @param name
	 *            the variable's name
	 * @param def
	 *            default value
	 * @return the value of the variable
	 */
	Boolean getVarValue(Object name, Boolean def);

	/**
	 * Sets the variable's value
	 * 
	 * @param name
	 *            the variable's name
	 * @param value
	 *            new variable's value
	 */
	void setVarValue(Object name, Object value);

	/**
	 * Returns the font's family name of the current cell.
	 * 
	 * @return the font's family name of the current cell
	 */
	String getFontName();

	/**
	 * Returns the font's family name of the cell by row and column.
	 * 
	 * @param row
	 *            the cell's row
	 * @param column
	 *            the cell's column
	 * @return the font's name
	 */
	String getFontName(int row, int column);

	/**
	 * Sets the font's name, style and size for the current cell.
	 * 
	 * @param fontName
	 *            the font's name
	 * @param style
	 *            the font's style The style argument is an integer bitmask that
	 *            may be CellStyle.PLAIN, or a bitwise union of CellStyle.BOLD, 
	 *            CellStyle.ITALIC, CellStyle.UNDERLINE, CellStyle.STRIKETHROUGH
	 * @param size
	 *            the font's size
	 */
	void setFont(String fontName, int style, int size);

	/**
	 * Sets the font's name, style and size for the cell by row and column.
	 * 
	 * @param fontName
	 *            the font's name
	 * @param style
	 *            the font's style The style argument is an integer bitmask that
	 *            may be CellStyle.PLAIN, or a bitwise union of CellStyle.BOLD, 
	 *            CellStyle.ITALIC, CellStyle.UNDERLINE, CellStyle.STRIKETHROUGH
	 * @param size
	 *            the font's size
	 * @param row
	 *            the cell's row
	 * @param column
	 *            the cell's column
	 */
	void setFont(String fontName, int style, int size, int row, int column);

	/**
	 * Sets background color for the current cell
	 * 
	 * @param color
	 *            the new background color for the current cell
	 */
	void setBackground(Color color);

	/**
	 * Sets background color for the cell by row and column
	 * 
	 * @param color
	 *            the new background color for the cell
	 * @param row
	 *            the cell's row
	 * @param column
	 *            the cell's column
	 */
	void setBackground(Color color, int row, int column);

	/**
	 * Sets foreground color for the current cell
	 * 
	 * @param color
	 *            the new foreground color for the current cell
	 */
	void setForeground(Color color);

	/**
	 * Sets foreground color for the cell by row and column
	 * 
	 * @param color
	 *            the new foreground color for the cell
	 * @param row
	 *            the cell's row
	 * @param column
	 *            the cell's column
	 */
	void setForeground(Color color, int row, int column);

	/**
	 * Returns the row's height for the current cell
	 * 
	 * @return the row's height for the current cell
	 */
	int getRowHeight();

	/**
	 * Returns the row's height by the row's number
	 * 
	 * @param row
	 *            the row's number
	 * @return the row's height by the row's number
	 */
	int getRowHeight(int row);

	/**
	 * Sets the row's height for the current cell
	 * 
	 * @param height
	 *            the new row's height
	 */
	void setRowHeight(int height);

	/**
	 * Sets the row's height by the row
	 * 
	 * @param height
	 *            the new row's height
	 * @param row
	 *            the row's number
	 */
	void setRowHeight(int height, int row);

	/**
	 * Returns the column's width by the column's number
	 * 
	 * @return the column's width by the column's number
	 */
	int getColumnWidth();

	/**
	 * Returns the column's width by the column's number
	 * 
	 * @param column
	 *            the column's number
	 * @return the column's width by the column's number
	 */
	int getColumnWidth(int column);

	/**
	 * Sets the column's width for the current cell
	 * 
	 * @param width
	 *            the new column's width
	 */
	void setColumnWidth(int width);

	/**
	 * Sets the column's width by the column
	 * 
	 * @param width
	 *            the new column's width
	 * @param column
	 *            the column's number
	 */
	void setColumnWidth(int width, int column);

	/**
	 * Sets the vertical alignment for the current cell The vertical alignment
	 * is a constant that may be CellStyle.TOP, CellStyle.BOTTOM or CellStyle.CENTER
	 * 
	 * @param align
	 *            the new vertical alignment
	 */
	void setVerticalAlignment(int align);

	/**
	 * Sets the vertical alignment for the cell by row and column The vertical
	 * alignment is a constant that may be CellStyle.TOP, CellStyle.BOTTOM or 
	 * CellStyle.CENTER
	 * 
	 * @param align
	 *            the new vertical alignment
	 * @param row
	 *            the cell's row
	 * @param column
	 *            the cell's column
	 */
	void setVerticalAlignment(int align, int row, int column);

	/**
	 * Sets the horizontal alignment for the current cell The horizontal
	 * alignment is a constant that may be CellStyle.LEFT, CellStyle.RIGHT, 
	 * CellStyle.CENTER or CellStyle.JUSTIFY
	 * 
	 * @param align
	 *            the new horizontal alignment for the current cell
	 */
	void setHorizontalAlignment(int align);

	/**
	 * Sets the horizontal alignment for the cell by row and column The
	 * horizontal alignment is a constant that may be CellStyle.LEFT, 
	 * CellStyle.RIGHT, CellStyle.CENTER or CellStyle.JUSTIFY
	 * 
	 * @param align
	 *            the new horizontal alignment
	 * @param row
	 *            the cell's row
	 * @param column
	 *            the cell's column
	 */
	void setHorizontalAlignment(int align, int row, int column);

	/**
	 * Adds rows in the report
	 * 
	 * @param count
	 *            count rows
	 * @param index
	 *            the specified position in this report
	 */
	void addRows(int count, int index);

	/**
	 * Removes rows from the report
	 * 
	 * @param count
	 *            count rows
	 * @param index
	 *            the index of the first removing row
	 */
	void removeRows(int count, int index);

	/**
	 * Adds columns in the report
	 * 
	 * @param count
	 *            count columns
	 * @param index
	 *            the specified position in this report
	 */
	void addColumns(int count, int index);

	/**
	 * Removes columns from the report
	 * 
	 * @param count
	 *            count columns
	 * @param index
	 *            the index of the first removing column
	 */
	void removeColumns(int count, int index);

	/**
	 * Sets decimal position of the numeric value of the current cell.
	 * 
	 * @param d
	 *            the new decimal position of the numeric value.
	 */
	void setDecimal(int d);

	/**
	 * Sets decimal position of the numeric value of the cell's by row and
	 * column.
	 * 
	 * @param d
	 *            the new decimal position of the numeric value.
	 * @param row
	 *            the cell's row
	 * @param column
	 *            the cell's column
	 */
	void setDecimal(int d, int row, int column);

	/**
	 * Sets the horizontal break of the page after row
	 * 
	 * @param row
	 *            the row's number
	 * @param b
	 *            a boolean value, where true sets the page break and false
	 *            remove it
	 */
	void setRowBreak(int row, boolean b);

	/**
	 * Returns the title of the sheet
	 * 
	 * @return the title of the sheet
	 */
	String getReportTitle();

	/**
	 * Sets the sheet's title
	 * 
	 * @param reportTitle
	 *            new sheet's title
	 */
	void setReportTitle(String reportTitle);

	/**
	 * Sets the borders at specified positions for the current cell
	 * 
	 * @param line
	 *            the border
	 * @param positions
	 *            boolean values, where true sets border, otherwise does
	 *            nothing. Position's index can be from Border.LINE_LEFT to
	 *            Border.LINE_HMIDDLE
	 */
	void setBorder(Border line, boolean[] positions);

	/**
	 * Sets the borders at specified positions for the cell by row and column
	 * 
	 * @param line
	 *            the border
	 * @param positions
	 *            positions - boolean values, where true sets border, otherwise
	 *            does nothing. Position's index can be from Border.LINE_LEFT to
	 *            Border.LINE_HMIDDLE
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 */
	void setBorder(Border line, boolean[] positions, int row, int column);

	/**
	 * Cleans the cell by row and column
	 * 
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 */
	void deleteCell(int row, int column);

	/**
	 * Returns the cell's sizes for the current cell, including spanned cells
	 * 
	 * @return the cell's sizes
	 */
	Dimension getCellSize();

	/**
	 * Returns the cell's sizes for the cell by row and column, including
	 * spanned cells
	 * 
	 * @param row
	 *            then row's number
	 * @param column
	 *            the column's number
	 * @return the cell's sizes
	 */
	Dimension getCellSize(int row, int column);

	/**
	 * Returns rows' count formed during the current time
	 * 
	 * @return the rows count
	 */
	int getRowCount();

	/**
	 * Returns columns' count formed during the current time
	 * 
	 * @return the columns count
	 */
	int getColumnCount();

	/**
	 * Unions cells The first cell of the top-left corner is specified by topRow
	 * and leftColumn, the last cell of the bottom-right corner is specified by
	 * bottomRow and rightColumn
	 * 
	 * @param topRow
	 *            the top row of the union
	 * @param leftColumn
	 *            the left column of the union
	 * @param bottomRow
	 *            the bottom row of the union
	 * @param rightColumn
	 *            the right column of the union
	 */
	void unionCells(int topRow, int leftColumn, int bottomRow,
			int rightColumn);

	/**
	 * Printing mode that scales the output smaller, if necessary, to fit the
	 * report's entire width (and thereby all columns) on each page; Rows are
	 * spread across multiple pages as necessary.
	 * 
	 * @param stretchPage
	 *            if true sets printing mode in FIT_WIDTH otherwise in NORMAL
	 * 
	 * @see javax.swing.JTable.PrintMode
	 */
	void setStretchPage(boolean stretchPage);

	/**
	 * Runs function by functionName in current cell
	 * 
	 * @param functionName
	 *            function name
	 * @throws ReportException
	 */
	void runFunction(String functionName) throws ReportException;

	/**
	 * Runs function in cell by row and column by functionName <br>
	 * row and column must be less or equal current row and column
	 * 
	 * @param functionName
	 *            the function's name
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 * @throws ReportException
	 */
	void runFunction(String functionName, int row, int column)
			throws ReportException;

	/**
	 * Returns ReportPage
	 * @return ReportPage
	 * @since 2.0
	 */
	ReportPage getReportPage();
	
	/**
	 * Sets visible or invisible the current tab
	 * 
	 * @param visible if false the current tab is invisible 
	 * @since 2.0
	 */
	void setSheetVisible(boolean visible);
}
