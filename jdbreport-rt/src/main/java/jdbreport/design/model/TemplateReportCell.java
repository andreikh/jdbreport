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

import java.util.ArrayList;
import java.util.List;

import jdbreport.model.Cell;
import jdbreport.model.ReportCell;

/**
 * @version 3.1 14.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class TemplateReportCell extends ReportCell implements CellObject {

	private static final long serialVersionUID = 1L;
	private boolean notRepeat;
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

	public boolean isNotRepeat() {
		return notRepeat;
	}

	public void setNotRepeat(boolean noRepeate) {
		this.notRepeat = noRepeate;
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
		notRepeat = false;
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
		if (expressions != null) {
			if (expressions.length > 0) {
				String[] result = new String[expressions.length];
				int i = 0;
				for (Expression expr : expressions) {
					if (expr.getType() == Expression.TYPE_FIELD) {
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
		if (expressions != null) {
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
