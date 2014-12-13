/*
 * Created on 20.02.2005
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
package jdbreport.grid;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

import javax.swing.Action;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;

import jdbreport.actions.BaseAction;
import jdbreport.actions.ToggleAction;
import jdbreport.grid.undo.UndoItem;
import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.GridRect;
import jdbreport.util.Resources;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class ReportAction {
	
	public static final String DECIMALS_DEC_ACTION = "decimals_dec";
	public static final String DECIMALS_INC_ACTION = "decimals_inc";
	public static final String PAGE_SETUP_ACTION = "page_setup";
	public static final String CELL_EDITOR_ACTION = "cell_editor";
	public static final String EDIT_SELECT_ALL_ACTION = "edit_select_all";
	public static final String EDIT_DELETE_ACTION = "edit_delete";
	public static final String EDIT_PASTE_ACTION = "edit_paste";
	public static final String EDIT_COPY_ACTION = "edit_copy";
	public static final String EDIT_CUT_ACTION = "edit_cut";
	public static final String DELETE_IMAGE_ACTION = "delete_image";
	public static final String SCALE_IMAGE_ACTION = "scale_image";
	public static final String SAVE_IMAGE_ACTION = "save_image";
	public static final String INSERT_IMAGE_ACTION = "insert_image";
	public static final String BACKGROUND_ACTION = "background";
	public static final String COLUMN_BREAK_ACTION = "column_break";
	public static final String ROW_BREAK_ACTION = "row_break";
	public static final String CELL_PROPERTY_ACTION = "cell_property";
	public static final String GRID_SHOW_ACTION = "grid_show";
	public static final String BRD_CLEAR_ACTION = "brd-clear";
	public static final String BRD_GRID_ACTION = "brd-grid";
	public static final String CELL_UNION_ACTION = "cell-union";
	public static final String CELL_AUTOHEIGHT_ACTION = "cell-autoheight";
	public static final String REMOVE_COLUMN_ACTION = "remove-column";
	public static final String INSERT_COLUMN_ACTION = "insert-column";
	public static final String REMOVE_ROW_ACTION = "remove-row";
	public static final String INSERT_ROW_ACTION = "insert-row";
	public static final String ALIGN_VCENTER_ACTION = "align-vcenter";
	public static final String ALIGN_BOTTOM_ACTION = "align-bottom";
	public static final String ALIGN_TOP_ACTION = "align-top";
	public static final String ALIGN_JUSTIFY_ACTION = "align-justify";
	public static final String ALIGN_CENTER_ACTION = "align-center";
	public static final String ALIGN_RIGHT_ACTION = "align-right";
	public static final String ALIGN_LEFT_ACTION = "align-left";
	public static final String FONT_BOLD_ACTION = "font-bold";
	public static final String FONT_ITALIC_ACTION = "font-italic";
	public static final String FONT_UNDERLINE_ACTION = "font-underline";
	public static final String FONT_STRIKETHROUGH_ACTION = "font-strikethrough";
	public static final String PRINT_REPORT_ACTION = "print_report";
	public static final String PREVIEW_ACTION = "preview";
	public static final String NEW_ACTION = "new";
	public static final String EXPORT_TO_CALC_ACTION = "export2Calc";
	public static final String SAVE_ACTION = "save";
	public static final String SAVE_AS_ACTION = "saveAs";
	public static final String OPEN_ACTION = "open";
	public static final String EXIT_ACTION = "exit";


	public static Action createGridAction(String name, TargetGrid targetGrid) {
		return new GridActionImpl(name, targetGrid);
	}
	
	public static ToggleAction createGridToggleAction(String name, TargetGrid targetGrid) {
		return new GridToggleActionImpl(name, targetGrid);
	}

	public static class GridActionImpl extends BasedAction {

		private static final long serialVersionUID = 1L;
		private TargetGrid targetGrid;

		public GridActionImpl(String name, TargetGrid targetGrid) {
			super(name);
			this.targetGrid = targetGrid;
		}

		public void actionPerformed(ActionEvent e) {
			ReportAction.gridActionPerformed(e, targetGrid);			
		}

	}

	public static class GridToggleActionImpl extends ToggleAction {

		private static final long serialVersionUID = 1L;
		
		private transient TargetGrid targetGrid;

		public GridToggleActionImpl(String name, TargetGrid targetGrid) {
			super(name, ".");
			this.targetGrid = targetGrid;
		}

		public Resources getResource() {
			return ReportResources.getInstance();
		}
		
		public void actionPerformed(ActionEvent e) {
			setSelected(!isSelected());
			ReportAction.gridActionPerformed(e, targetGrid);			
		}
				
	}
	
	public abstract static class BasedAction extends BaseAction {

		private static final long serialVersionUID = 1L;

		public BasedAction(String name) {
			super(name, ".");
		}

		public Resources getResource() {
			return ReportResources.getInstance();
		}

	}

	public abstract static class GridAction extends BasedAction {

		private static final long serialVersionUID = 1L;
		private TargetGrid targetGrid;

		public GridAction(String name, TargetGrid targetGrid) {
			super(name);
			this.targetGrid = targetGrid;
		}

		protected JReportGrid getReportGrid(ActionEvent e) {
			Object o = e.getSource();
			if (o instanceof JReportGrid) {
				return (JReportGrid) o;
			}
			return targetGrid.getFocusedGrid();
		}

	}

	public abstract static class GridToggleAction extends
			ToggleAction {

		private static final long serialVersionUID = 1L;

		private transient TargetGrid targetGrid;

		public GridToggleAction(String name, TargetGrid targetGrid) {
			super(name, ".");
			this.targetGrid = targetGrid;
		}

		public Resources getResource() {
			return ReportResources.getInstance();
		}

		protected JReportGrid getReportGrid(ActionEvent e) {
			Object o = e.getSource();
			if (o instanceof JReportGrid) {
				return (JReportGrid) o;
			}
			return targetGrid.getFocusedGrid();
		}

	}

	public static abstract class ToggleBorderAction extends GridToggleAction {

		private static final long serialVersionUID = 1L;
		protected boolean[] select;

		public ToggleBorderAction(String name, TargetGrid targetGrid) {
			super(name, targetGrid);
		}

		public void actionPerformed(ActionEvent e) {
			super.actionPerformed(e);
			JReportGrid reportGrid = getReportGrid(e);
			if (reportGrid != null) {
				if (isSelected())
					reportGrid.addBorder(select);
				else
					reportGrid.removeBorder(select);
			}
		}

	}

	public static class BorderAction extends ToggleBorderAction {

		private static final long serialVersionUID = -4191493856789788760L;

		public BorderAction(TargetGrid targetGrid) {
			super("brd-all", targetGrid); //$NON-NLS-1$
			select = new boolean[] { true, true, true, true, false, false };
		}

	}

	public static class BorderLeftAction extends ToggleBorderAction {

		private static final long serialVersionUID = -8221667231282119422L;

		public BorderLeftAction(TargetGrid targetGrid) {
			super("brd-left", targetGrid); //$NON-NLS-1$
			select = new boolean[] { true, false, false, false, false, false };
		}

	}

	public static class BorderRightAction extends ToggleBorderAction {

		private static final long serialVersionUID = 6374862690669395381L;

		public BorderRightAction(TargetGrid targetGrid) {
			super("brd-right", targetGrid); //$NON-NLS-1$
			select = new boolean[] { false, false, true, false, false, false };
		}

	}

	public static class BorderTopAction extends ToggleBorderAction {

		private static final long serialVersionUID = -1082870484081526682L;

		public BorderTopAction(TargetGrid targetGrid) {
			super("brd-top", targetGrid); //$NON-NLS-1$
			select = new boolean[] { false, true, false, false, false, false };
		}

	}

	public static class BorderBottomAction extends ToggleBorderAction {

		private static final long serialVersionUID = -1733565228688156871L;

		public BorderBottomAction(TargetGrid targetGrid) {
			super("brd-bottom", targetGrid); //$NON-NLS-1$
			select = new boolean[] { false, false, false, true, false, false };
		}

	}

	public static class BorderLRAction extends GridAction {

		private static final long serialVersionUID = -3136686619107966923L;

		public BorderLRAction(TargetGrid targetGrid) {
			super("brd-lr", targetGrid); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			JReportGrid reportGrid = getReportGrid(e);
			if (reportGrid != null) {
				reportGrid.addBorder(new boolean[] { true, false, true, false,
						false, false });
			}
		}
	}

	public static class BorderTBAction extends GridAction {

		private static final long serialVersionUID = -1960768282948422319L;

		public BorderTBAction(TargetGrid targetGrid) {
			super("brd-tb", targetGrid); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			JReportGrid reportGrid = getReportGrid(e);
			if (reportGrid != null) {
				reportGrid.addBorder(new boolean[] { false, true, false, true,
						false, false });
			}
		}
	}

	public static class BorderTMBAction extends GridAction {

		private static final long serialVersionUID = -8785529345945111368L;

		public BorderTMBAction(TargetGrid targetGrid) {
			super("brd-tmb", targetGrid); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			JReportGrid reportGrid = getReportGrid(e);
			if (reportGrid != null) {
				reportGrid.addBorder(new boolean[] { false, true, false, true,
						false, true });
			}
		}
	}

	public static class BorderHAction extends GridAction {

		private static final long serialVersionUID = 5670189506485840339L;

		public BorderHAction(TargetGrid targetGrid) {
			super("brd-allh", targetGrid); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			JReportGrid reportGrid = getReportGrid(e);
			if (reportGrid != null) {
				reportGrid.addBorder(new boolean[] { true, true, true, true,
						false, true });
			}
		}
	}

	public static class BorderVAction extends GridAction {

		private static final long serialVersionUID = -6745777488973004958L;

		public BorderVAction(TargetGrid targetGrid) {
			super("brd-allv", targetGrid); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			JReportGrid reportGrid = getReportGrid(e);
			if (reportGrid != null) {
				reportGrid.addBorder(new boolean[] { true, true, true, true,
						true, false });
			}
		}
	}

	public static class BorderMiddleAction extends GridAction {

		private static final long serialVersionUID = -6745777488973004958L;

		public BorderMiddleAction(TargetGrid targetGrid) {
			super("brd-middle", targetGrid); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			JReportGrid reportGrid = getReportGrid(e);
			if (reportGrid != null) {
				reportGrid.addBorder(new boolean[] { false, false, false,
						false, true, true });
			}
		}
	}

	public abstract static class UndoRedoAction extends BasedAction {

		private static final long serialVersionUID = 1L;

		private LinkedList<UndoItem> stack;

		public UndoRedoAction(String name, LinkedList<UndoItem> stack) {
			super(name);
			this.stack = stack;
		}

		public LinkedList<UndoItem> getStack() {
			return stack;
		}

		public String getDescription() {
			if (getStack().size() > 0) {
				UndoItem undoItem = getStack().get(0);
				if (undoItem != null) {
					return undoItem.getDescription();
				}
			}
			return ""; //$NON-NLS-1$
		}

		public String getToolTipText() {
			return getValue("Name") + " " + getDescription();
		}

	}

	public abstract static class EditUndoAction extends UndoRedoAction {

		private static final long serialVersionUID = 1L;

		public EditUndoAction(LinkedList<UndoItem> stack) {
			super("edit_undo", stack); //$NON-NLS-1$
			setEnabled(false);
		}

	}

	public abstract static class EditRedoAction extends UndoRedoAction {

		private static final long serialVersionUID = 1L;

		public EditRedoAction(LinkedList<UndoItem> stack) {
			super("edit_redo", stack); //$NON-NLS-1$
			setEnabled(false);
		}

	}

	public static class IncDecimalsAction extends GridAction {

		private static final long serialVersionUID = 1L;

		public IncDecimalsAction(TargetGrid targetGrid) {
			super(DECIMALS_INC_ACTION, targetGrid); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			JReportGrid reportGrid = getReportGrid(e);
			if (reportGrid != null) {
				reportGrid.incDecimals();
			}
		}

	}

	public static class DecDecimalsAction extends GridAction {

		private static final long serialVersionUID = 1L;

		public DecDecimalsAction(TargetGrid targetGrid) {
			super(DECIMALS_DEC_ACTION, targetGrid); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			JReportGrid reportGrid = getReportGrid(e);
			if (reportGrid != null) {
				reportGrid.decDecimals();
			}
		}

	}

	public static void gridActionPerformed(ActionEvent e, TargetGrid targetGrid) {
		JReportGrid grid;
		Object o = e.getSource();
		if (o instanceof JReportGrid) {
			grid = (JReportGrid) o;
		} else {
			grid = targetGrid.getFocusedGrid();
		}
		
		if (grid == null) return;
		
		if (FONT_BOLD_ACTION.equals(e.getActionCommand())) {
				grid.setFontStyle(Font.BOLD);
		} else
		if (FONT_ITALIC_ACTION.equals(e.getActionCommand())) {
				grid.setFontStyle(Font.ITALIC);
		} else if (FONT_UNDERLINE_ACTION.equals(e.getActionCommand())) {
				grid.setFontStyle(CellStyle.UNDERLINE);
		} else if (FONT_STRIKETHROUGH_ACTION.equals(e.getActionCommand())) {
				grid.setFontStyle(CellStyle.STRIKETHROUGH);
		} else if (ALIGN_LEFT_ACTION.equals(e.getActionCommand())) {
			grid.setHorzAlign(CellStyle.LEFT);
		} else if (ALIGN_RIGHT_ACTION.equals(e.getActionCommand())) {
			grid.setHorzAlign(CellStyle.RIGHT);
		}else if (ALIGN_CENTER_ACTION.equals(e.getActionCommand())) {
			grid.setHorzAlign(CellStyle.CENTER);
		} else if (ALIGN_JUSTIFY_ACTION.equals(e.getActionCommand())) {
			grid.setHorzAlign(CellStyle.JUSTIFY);
		} else if (ALIGN_TOP_ACTION.equals(e.getActionCommand())) {
			grid.setVertAlign(CellStyle.TOP);
		} else if (ALIGN_BOTTOM_ACTION.equals(e.getActionCommand())) {
			grid.setVertAlign(CellStyle.BOTTOM);
		} else if (ALIGN_VCENTER_ACTION.equals(e.getActionCommand())) {
			grid.setVertAlign(CellStyle.CENTER);
		}else if (INSERT_ROW_ACTION.equals(e.getActionCommand())) {
			int count = 1;
			if ((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0) {
				String result = JOptionPane
						.showInputDialog(grid.getParent(), Messages
								.getString("ReportAction.13")); //$NON-NLS-1$
				if (result != null && !"".equals(result)) { //$NON-NLS-1$
					count = Integer.parseInt(result);
				}
			}
			grid.addRows(count, grid.getSelectedRow());

		}else if (REMOVE_ROW_ACTION.equals(e.getActionCommand())) {
			GridRect rect = grid.getSelectionRect();
			if (rect != null) {
				grid.removeRows(rect.getBottomRow() - rect.getTopRow() + 1, rect.getTopRow());
			}			
		}else if (INSERT_COLUMN_ACTION.equals(e.getActionCommand())) {
			grid.addColumns();
		}else if (REMOVE_COLUMN_ACTION.equals(e.getActionCommand())) {
			grid.removeColumns();
		}else if (CELL_AUTOHEIGHT_ACTION.equals(e.getActionCommand())) {
			grid.autoHeightCell();
		}else if (CELL_UNION_ACTION.equals(e.getActionCommand())) {
			grid.unionCell();
		}else if (BRD_GRID_ACTION.equals(e.getActionCommand())) {
			grid.addBorder(new boolean[] { true, true, true, true,
					true, true });
		}else if (BRD_CLEAR_ACTION.equals(e.getActionCommand())) {
			grid.removeBorder(new boolean[] { true, true, true, true,
					true, true });
		}else if (GRID_SHOW_ACTION.equals(e.getActionCommand())) {
			grid.setShowGrid(!grid.isShowGrid());
		}else if (CELL_PROPERTY_ACTION.equals(e.getActionCommand())) {
			grid.showCellProperty();
		}else if (ROW_BREAK_ACTION.equals(e.getActionCommand())) {
			grid.horizontalPageBreak();
		}else if (COLUMN_BREAK_ACTION.equals(e.getActionCommand())) {
			grid.verticalPageBreak();
		}else if (BACKGROUND_ACTION.equals(e.getActionCommand())) {
			Cell cell = grid.getSelectedCell();
			Color oldColor = null;
			if (cell != null) {
				oldColor = grid.getReportModel().getStyles(
						cell.getStyleId()).getBackground();
			}
			Color color = JColorChooser.showDialog(grid,
					"Background", oldColor); //$NON-NLS-1$
			if (color != null)
				grid.setCellBackground(color);

		}else if (INSERT_IMAGE_ACTION.equals(e.getActionCommand())) {
			grid.insertIcon();
		}else if (SAVE_IMAGE_ACTION.equals(e.getActionCommand())) {
			grid.saveIcon();
		}else if (SCALE_IMAGE_ACTION.equals(e.getActionCommand())) {
			grid.scaleIcon();
		}else if (DELETE_IMAGE_ACTION.equals(e.getActionCommand())) {
			grid.deleteIcon();
		}else if (EDIT_CUT_ACTION.equals(e.getActionCommand())) {
			grid.cut();
		}else if (EDIT_COPY_ACTION.equals(e.getActionCommand())) {
			grid.copy();
		}else if (EDIT_PASTE_ACTION.equals(e.getActionCommand())) {
			grid.paste();
		}else if (EDIT_DELETE_ACTION.equals(e.getActionCommand())) {
			grid.delete();
		}else if (EDIT_SELECT_ALL_ACTION.equals(e.getActionCommand())) {
			grid.selectAll();
		}else if (CELL_EDITOR_ACTION.equals(e.getActionCommand())) {
			grid.showCellEditor();
		}else if (PAGE_SETUP_ACTION.equals(e.getActionCommand())) {
			grid.pageSetup();
		}else if (DECIMALS_INC_ACTION.equals(e.getActionCommand())) {
			grid.incDecimals();
		}else if (DECIMALS_DEC_ACTION.equals(e.getActionCommand())) {
			grid.decDecimals();
		}
	}

}
