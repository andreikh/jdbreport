/*
 * JDBReport Designer
 * 
 * Copyright (C) 2006-2010 Andrey Kholmanskih. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, write to the 
 * 
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330,
 * Boston, MA  USA  02111-1307
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
 */
package jdbreport.design.grid;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;

import jdbreport.grid.ReportAction;
import jdbreport.grid.TargetGrid;
import jdbreport.model.Group;
import jdbreport.util.Resources;


/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 *
 */
public class DesignAction extends ReportAction {

	public static final String INSERT_GROUP_ACTION = "insert_group";
	public static final String SELECT_FUNCTION_ACTION = "select_function";
	public static final String VAR_LIST_ACTION = "var_list";
	public static final String GROUP_KEY_ACTION = "group_key";
	public static final String NOTREPEATE_ACTION = "notrepeate";

	public static Action createGridAction(String name, TargetGrid targetGrid) {
		return new DesignGridActionImpl(name, targetGrid);
	}

	public static class DesignGridActionImpl extends DesignBasedAction {

		private static final long serialVersionUID = 1L;
		private TargetGrid targetGrid;

		public DesignGridActionImpl(String name, TargetGrid targetGrid) {
			super(name);
			this.targetGrid = targetGrid;
		}

		public void actionPerformed(ActionEvent e) {
			DesignAction.gridActionPerformed(e, targetGrid);			
		}

	}

	public static abstract class DesignBasedAction extends BasedAction {

		private static final long serialVersionUID = 1L;

		public DesignBasedAction(String name) {
			super(name);
		}

		public Resources getResource() {
			return TemplateReportResources.getInstance();
		}
		
	}

	public static abstract class DesignGridAction extends GridAction {
		
		private static final long serialVersionUID = 1L;

		public DesignGridAction(String name, TargetGrid targetGrid) {
			super(name, targetGrid);
		}
		
		protected TemplateGrid getTemplateGrid(ActionEvent e) {
			return (TemplateGrid) getReportGrid(e);
		}
		
		public Resources getResource() {
			return TemplateReportResources.getInstance();
		}

	}
	
	public static class InsertTypedRowAction extends DesignGridAction {

		private static final long serialVersionUID = 1L;

		private int type = 0;

		public InsertTypedRowAction(int type, TargetGrid targetGrid) {
			super("insert_row_" + Group.typeNames[type], targetGrid); //$NON-NLS-1$
			this.type = type;
		}

		
		public void actionPerformed(ActionEvent e) {
			TemplateGrid reportGrid = getTemplateGrid(e);
			if (reportGrid != null) {
					int count = 1;
					if ((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0) {
		                 	String result = JOptionPane.showInputDialog(reportGrid.getParent(), Messages.getString("DesignAction.6"));  //$NON-NLS-1$
		                 	if (result != null && !"".equals(result)) { //$NON-NLS-1$
		                 		count = Integer.parseInt(result);
		                }
					}
					reportGrid.addRows(count,
							reportGrid.getSelectedRow(), type);
			}
		}
	}

/*	public static class InsertGroupAction extends DesignGridAction {

		private static final long serialVersionUID = 1L;

		public InsertGroupAction(TargetGrid targetGrid) {
			super(INSERT_GROUP_ACTION, targetGrid); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			TemplateGrid reportGrid = getTemplateGrid(e);
			if (reportGrid != null) {
				reportGrid.insertDetailGroup();
			}
		}

	}
	*/
	public static class SetTypeRowAction extends DesignGridAction {

		private static final long serialVersionUID = 1L;

		private int type = 0;

		public SetTypeRowAction(int type, TargetGrid targetGrid) {
			super("insert_row_" + Group.typeNames[type], targetGrid); //$NON-NLS-1$
			this.type = type;
		}

		public void actionPerformed(ActionEvent e) {
			TemplateGrid reportGrid = getTemplateGrid(e);
			if (reportGrid != null) {
				reportGrid.setRowType(
						reportGrid.getSelectedRows(), type);
			}
		}
	}


	public static class AgrFuncAction extends DesignGridAction {

		private static final long serialVersionUID = 1L;
		
		private int kind;

		public AgrFuncAction(int kind, TargetGrid targetGrid) {
			super("agr_func" + kind, targetGrid); //$NON-NLS-1$
			this.kind = kind;
		}

		public void actionPerformed(ActionEvent e) {
			TemplateGrid reportGrid = getTemplateGrid(e);
			if (reportGrid != null) {
				reportGrid.setAgrFunc(kind);
			}
		}
	}
	
/*	public abstract static class GenerateReportAction extends DesignBasedAction {

		private static final long serialVersionUID = 1L;
		
		public GenerateReportAction() {
			super("gen_report"); //$NON-NLS-1$
		}

	}
*/
	public static void gridActionPerformed(ActionEvent e, TargetGrid targetGrid) {
		TemplateGrid grid;
		Object o = e.getSource();
		if (o instanceof TemplateGrid) {
			grid = (TemplateGrid) o;
		} else {
			grid = (TemplateGrid) targetGrid.getFocusedGrid();
		}
		
		if (grid == null) return;
		
		if (NOTREPEATE_ACTION.equals(e.getActionCommand())) {
			grid.setNotRepeate();
		} else if (INSERT_GROUP_ACTION.equals(e.getActionCommand())) {
				grid.insertDetailGroup();
		}

	}
}
