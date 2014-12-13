/*
 * Created on 15.03.2005
 *
 * JDBReport Generator
 * 
 * Copyright (C) 2005-2014 Andrey Kholmanskih
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.GridRect;
import jdbreport.model.Group;
import jdbreport.model.HeighCalculator;
import jdbreport.view.model.JReportModel;
import jdbreport.model.TableRowModel;
import jdbreport.source.JdbcReportSource;

/**
 * @version 2.0 21.01.2011
 * @author Andrey Kholmanskih
 * 
 */
public class TemplateModel extends JReportModel {

	private static final long serialVersionUID = -8007578077895039370L;

	private Map<Object, Object> vars;

	private Map<String, CellFunctionObject> functionsList;

	private List<JdbcReportSource> sourcesList;

	private Map<CellObject, TotalInfo> totalList;

	public TemplateModel(Map<Object, CellStyle> styles) {
		super(0, 4, styles);
	}

	protected TableRowModel createRowModel() {
		return new TemplateRowModel();
	}

	/**
	 * Returns the CellObject by row and column
	 * 
	 * @param row
	 *            the row's number
	 * @param column
	 *            the column's number
	 * @return the CellObject
	 */
	public CellObject getCellObject(int row, int column) {
		if (getRowModel().getRow(row).isNull())
			return TemplateRow.nullCellObject;
		else
			return ((TemplateRow) getRowModel().getRow(row))
					.getCellObject(column);
	}

	/**
	 * Sets the function's name for the selected cells
	 * 
	 * @param selectionRect
	 *            coordinates of the selected cells
	 * @param functionName
	 *            the function name
	 */
	public void setFunctionName(GridRect selectionRect, String functionName) {
		Iterator<Cell> it = getSelectedCells(selectionRect);
		while (it.hasNext()) {
			((CellObject) it.next()).setFunctionName(functionName);
		}
	}

	/**
	 * 
	 * @param row
	 *            the row's number
	 * @param col
	 *            the column's number
	 * @return true, if the cell's type is the CellObject.TYPE_FIELD
	 */
	public boolean isCellField(int row, int col) {
		return getCellObject(row, col).getType() == CellObject.TYPE_FIELD;
	}

	@Override
	public String getToolTipText(int row, int column) {
		CellObject cell = (CellObject) getOwnerReportCell(row, column);
		if (cell.isNull())
			return null;
		StringBuffer result = new StringBuffer();
		if (cell.getFunctionName() != null) {
			result.append(Messages.getString("TemplateModel.0")); //$NON-NLS-1$
			result.append(" <b>"); //$NON-NLS-1$
			result.append(cell.getFunctionName());
			result.append("</b><br>"); //$NON-NLS-1$
		}
		if (cell.getType() == CellObject.TYPE_FIELD) {
			result.append(Messages.getString("TemplateModel.3")); //$NON-NLS-1$
			result.append(" <b>"); //$NON-NLS-1$
			result.append(cell.getFieldName());
			result.append("</b> "); //$NON-NLS-1$
			result.append(Messages.getString("TemplateModel.6")); //$NON-NLS-1$
			result.append(" <b>"); //$NON-NLS-1$
			result.append(cell.getDataSetId());
			result.append("</b><br>"); //$NON-NLS-1$
		} else if (cell.getType() == CellObject.TYPE_VAR) {
			result.append(Messages.getString("TemplateModel.9")); //$NON-NLS-1$
			result.append(" <b>"); //$NON-NLS-1$
			result.append(cell.getText());
			result.append("</b><br>"); //$NON-NLS-1$
		}
		if (cell.getTotalFunction() != CellObject.AF_NONE) {
			Group group = getRowModel().getGroup(row);
			if (group.getType() == Group.ROW_FOOTER
					|| group.getType() == Group.ROW_GROUP_FOOTER
					|| group.getType() == Group.ROW_GROUP_HEADER) {
				result.append(Messages.getString("TemplateModel.12")); //$NON-NLS-1$
				result.append("	<b>"); //$NON-NLS-1$
				result
						.append(CellObject.AGR_FUNC_NAME[cell
								.getTotalFunction()]);
				result.append("</b><br>"); //$NON-NLS-1$
			}
		}
		if (result.length() > 0) {
			result.insert(0, "<html>"); //$NON-NLS-1$
			result.append("</html>"); //$NON-NLS-1$
		}
		return result.toString();
	}

	/**
	 * 
	 * @return the variables map
	 */
	public Map<Object, Object> getVars() {
		return vars;
	}

	/**
	 * Sets variables map
	 * 
	 * @param vars
	 *            the variables map
	 */
	protected void setVars(Map<Object, Object> vars) {
		this.vars = vars;
	}

	/**
	 * 
	 * @return the functions' map
	 */
	public Map<String, CellFunctionObject> getFunctionsList() {
		return functionsList;
	}

	/**
	 * 
	 * @param functionsList
	 *            the functions' map
	 */
	protected void setFunctionsList(Map<String, CellFunctionObject> functionsList) {
		this.functionsList = functionsList;
	}

	/**
	 * 
	 * @return the Data Base sources' list
	 */
	public List<JdbcReportSource> getSourcesList() {
		return sourcesList;
	}

	/**
	 * 
	 * @param sourcesList
	 *            the Data Base sources' list
	 */
	protected void setSourcesList(List<JdbcReportSource> sourcesList) {
		this.sourcesList = sourcesList;
	}

	protected Map<CellObject, TotalInfo> getTotalList() {
		return totalList;
	}

	protected void setTotalList(Map<CellObject, TotalInfo> list) {
		this.totalList = list;
	}

	/**
	 * Sets type of the selected rows
	 * 
	 * @param selectedRows
	 *            the selected rows
	 * @param type
	 *            the row's type
	 */
	public void setRowType(int[] selectedRows, int type) {
		((TemplateRowModel) getRowModel()).setRowType(selectedRows, type);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	public int insertDetailGroup(int rowIndex) {
		return ((TemplateRowModel) getRowModel()).insertDetailGroup(rowIndex);
	}

	/**
	 * Adds rows' count of the defined type to the index of the model. The new
	 * rows will contain null values. Notification of the row being added will
	 * be generated.
	 * 
	 * @param count
	 *            - rows' count
	 * @param index
	 *            - the row index of the rows to be inserted
	 * @param type
	 *            - the row's type
	 * @return rows' count in the model
	 */
	public int addRows(int count, int index, int type) {
		return ((TemplateRowModel) getRowModel()).addRows(count, index, type);
	}

	public void updateRowAndPageHeight(HeighCalculator hCalc) {

	}

}
