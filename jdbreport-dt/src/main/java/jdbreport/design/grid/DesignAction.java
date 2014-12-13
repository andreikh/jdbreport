/*
 * JDBReport Designer
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
