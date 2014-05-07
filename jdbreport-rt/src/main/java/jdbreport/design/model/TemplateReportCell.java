/*
 * JDBReport Generator
 * 
 * Copyright (C) 2006-2013 Andrey Kholmanskih. All rights reserved.
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
package jdbreport.design.model;

import java.util.ArrayList;
import java.util.List;

import jdbreport.model.Cell;
import jdbreport.model.ReportCell;

/**
 * @version 2.2 13.04.2013
 * @author Andrey Kholmanskih
 * 
 */
public class TemplateReportCell extends ReportCell implements CellObject {

	private static final long serialVersionUID = 1L;
	private String dataSetId;
	private int type;
	private boolean notRepeate;
	private String functionText;
	private byte[] compiledFunction;
	private String functionName;
	private int total_func;
	private transient Object oldValue;
	private Expression[] expressions;
	private boolean formatting;

	public TemplateReportCell() {
		super();
	}

	public String getFieldName() {
		return type == CellObject.TYPE_FIELD ? getText() : "";
	}

	public void setFieldName(String name) {
		setValue(name);
	}

	public String getDataSetId() {
		return dataSetId;
	}

	public void setDataSetId(String tableId) {
		if (dataSetId != null || tableId != null) {
			this.dataSetId = tableId != null && tableId.trim().length() == 0 ? null
					: tableId;
			if (dataSetId == null) {
				if (type == CellObject.TYPE_FIELD)
					type = CellObject.TYPE_NONE;
			} else
				type = CellObject.TYPE_FIELD;
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Cell createCellItem() {
		Cell cell = new ReportCell();
		cell.setColSpan(getColSpan());
		cell.setRowSpan(getRowSpan());
		cell.setValue(getValue());
		cell.setStyleId(getStyleId());
		cell.setExtFlags(getExtFlags());
		cell.setNotPrint(isNotPrint());
		cell.setEditable(isEditable());
		cell.setPicture(getPicture());
		cell.setScaleIcon(isScaleIcon());
		cell.setImageFormat(getImageFormat());
		cell.setCellFormula(getCellFormula());
		return cell;
	}

	public boolean isNotRepeate() {
		return notRepeate;
	}

	public void setNotRepeate(boolean noRepeate) {
		this.notRepeate = noRepeate;
	}

	public String getFunctionText() {
		return functionText;
	}

	public void setFunctionText(String text) {
		this.functionText = text;
	}

	public byte[] getCompiledFunction() {
		return compiledFunction;
	}

	public void setCompiledFunction(byte[] classBuf) {
		compiledFunction = classBuf;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		if (functionName != null && functionName.trim().length() == 0)
			this.functionName = null;
		else
			this.functionName = functionName;
	}

	public int getTotalFunction() {
		return total_func;
	}

	public void setTotalFunction(int func) {
		this.total_func = func;
	}

	public void clear() {
		super.clear();
		dataSetId = null;
		type = 0;
		notRepeate = false;
		functionText = null;
		compiledFunction = null;
		functionName = null;
		total_func = CellObject.AF_NONE;
		expressions = null;
	}

	public boolean isOldEquals(Object value) {
		String v1 = value == null ? "" : value.toString();
		String v2 = oldValue == null ? "" : oldValue.toString();
		return v1.equals(v2);
	}

	public void setOldValue(Object oldValue) {
		this.oldValue = oldValue;
	}

	public Expression[] getExpressions() {
		return expressions;
	}

	public void setExpressions(Expression[] expr) {
		this.expressions = expr;
	}

	public String[] getDataSetIds() {
		if (type == CellObject.TYPE_FIELD)
			return new String[] { dataSetId };
		else if (type == CellObject.TYPE_NONE && expressions != null) {
			if (expressions.length > 0) {
				String[] result = new String[expressions.length];
				int i = 0;
				for (Expression expr : expressions) {
					if (expr.getType() == CellObject.TYPE_FIELD) {
						result[i] = expr.getBaseName();
						i++;
					}
				}
				return result;
			}
		}
		return null;
	}

	public String[] getFieldNames(String dsId) {
		if (type == CellObject.TYPE_FIELD) {
			return new String[] { getFieldName() };
		}
		if (type == CellObject.TYPE_NONE && expressions != null) {
			List<String> list = new ArrayList<>();
			for (Expression expr : expressions) {
				if (dsId.equals(expr.getBaseName())) {
					list.add(expr.getProperty());
				}
			}
			if (list.size() > 0) {
				return list.toArray(new String[list.size()]);
			}
		}
		return null;
	}

	public boolean isReplacement() {
		return formatting;
	}

	public void setReplacement(boolean b) {
		this.formatting = b;
	}

}
