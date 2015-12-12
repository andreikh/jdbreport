/*
 * Created on 28.11.2015
 *
 * Copyright (C) 2015 Andrey Kholmanskih
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
package jdbreport.design.grid.dialogs;

import jdbreport.design.grid.*;
import jdbreport.model.*;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * @author Andrey Kholmanskih
 * @version 3.1 28.11.2015
 */
public class StructurePanel extends JPanel implements ActionListener {

    private final TemplateGrid grid;
    private JToolBar toolBar;
    private JButton collapseButton;
    private JButton expandButton;
    private JTree tree;
    private JPopupMenu treeMenu;
    private Action delAction;
    private Action addDetailGroupAction;
    private Action addHeaderAction;
    private Action addFooterAction;
    private Action addDetailAction;
    private Action addGroupAction;
    private Action addRowAction;

    public StructurePanel(TemplateGrid grid) {
        this.grid = grid;
        setLayout(new BorderLayout());
        tree = new JTree(new GroupNode(grid.getTemplateModel().getRowModel().getRootGroup(), null));
        tree.setDragEnabled(true);
        tree.setDropMode(DropMode.ON_OR_INSERT);
        tree.setTransferHandler(new TreeTransferHandler());
        tree.setComponentPopupMenu(getTreeMenu());
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        expandTree(tree);

        add(getToolBar(), BorderLayout.NORTH);
        add(new JScrollPane(tree), BorderLayout.CENTER);
    }

    private JPopupMenu getTreeMenu() {
        if (treeMenu == null) {
            treeMenu = new JPopupMenu();
            JMenu menu = new JMenu("Добавить");
            menu.add(getAddDetailGroupAction());
            menu.add(getAddHeaderAction());
            menu.add(getAddDetailAction());
            menu.add(getAddFooterAction());
            menu.add(getAddGroupAction());
            menu.add(getAddRowAction());
            treeMenu.add(menu);
            treeMenu.addSeparator();
            treeMenu.add(getDelAction());

            treeMenu.addPopupMenuListener(new PopupMenuListener() {

                public void popupMenuCanceled(PopupMenuEvent e) {
                }

                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                }

                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    updateActions();
                }

            });
        }
        return treeMenu;
    }

    private void updateActions() {
        getDelAction().setEnabled(false);
        getAddDetailGroupAction().setEnabled(false);
        getAddHeaderAction().setEnabled(false);
        getAddDetailAction().setEnabled(false);
        getAddFooterAction().setEnabled(false);
        getAddGroupAction().setEnabled(false);
        getAddRowAction().setEnabled(false);

        DefaultMutableTreeNode node = getSelectedNode();
        if (node != null) {
            Object obj = node.getUserObject();
            if (obj instanceof TableRow) {
                getDelAction().setEnabled(true);
                getAddRowAction().setEnabled(true);
            } else {
                Group group = (Group) obj;
                int type = group.getType();
                if (group instanceof RowsGroup) {
                    getAddRowAction().setEnabled(true);
                    if (type == Group.ROW_GROUP_HEADER || type == Group.ROW_GROUP_FOOTER
                            || type == Group.ROW_DETAIL || type == Group.ROW_NONE) {
                        getDelAction().setEnabled(true);
                    }
                } else if (group instanceof RootGroup) {
                    getAddDetailGroupAction().setEnabled(true);
                    getAddGroupAction().setEnabled(true);
                } else {
                    if (type == Group.GROUP_DETAIL) {
                        getDelAction().setEnabled(true);
                        getAddHeaderAction().setEnabled(((GroupsGroup) group).getGroup(Group.ROW_GROUP_HEADER) == null);
                        getAddFooterAction().setEnabled(((GroupsGroup) group).getGroup(Group.ROW_GROUP_FOOTER) == null);
                        getAddDetailAction().setEnabled(((GroupsGroup) group).getGroup(Group.ROW_DETAIL) == null);
                        getAddGroupAction().setEnabled(((GroupsGroup) group).getGroup(Group.ROW_NONE) == null);
                        getAddDetailGroupAction().setEnabled(true);
                    }
                }
            }
        }
    }

    private JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.setFloatable(false);
            toolBar.add(getCollapseButton());
            toolBar.add(getExpandButton());
        }
        return toolBar;
    }

    private Action getAddDetailGroupAction() {
        if (addDetailGroupAction == null) {
            addDetailGroupAction = new DesignAction.DesignBasedAction("structure_add_detailgroup") {

                public void actionPerformed(ActionEvent e) {
                    DefaultMutableTreeNode node = getSelectedNode();
                    if (node != null) {
                        addGroup(node, Group.GROUP_DETAIL);
                    }
                }

            };
        }
        return addDetailGroupAction;
    }

    private Action getAddHeaderAction() {
        if (addHeaderAction == null) {
            addHeaderAction = new DesignAction.DesignBasedAction("structure_add_groupheader") {

                public void actionPerformed(ActionEvent e) {
                    DefaultMutableTreeNode node = getSelectedNode();
                    if (node != null) {
                        addGroup(node, Group.ROW_GROUP_HEADER);
                    }
                }

            };
        }
        return addHeaderAction;
    }

    private Action getAddFooterAction() {
        if (addFooterAction == null) {
            addFooterAction = new DesignAction.DesignBasedAction("structure_add_groupfooter") {

                public void actionPerformed(ActionEvent e) {
                    DefaultMutableTreeNode node = getSelectedNode();
                    if (node != null) {
                        addGroup(node, Group.ROW_GROUP_FOOTER);
                    }
                }

            };
        }
        return addFooterAction;
    }

    private Action getAddDetailAction() {
        if (addDetailAction == null) {
            addDetailAction = new DesignAction.DesignBasedAction("structure_add_groupdetail") {

                public void actionPerformed(ActionEvent e) {
                    DefaultMutableTreeNode node = getSelectedNode();
                    if (node != null) {
                        addGroup(node, Group.ROW_DETAIL);
                    }
                }

            };
        }
        return addDetailAction;
    }

    private Action getAddGroupAction() {
        if (addGroupAction == null) {
            addGroupAction = new DesignAction.DesignBasedAction("structure_add_group") {

                public void actionPerformed(ActionEvent e) {
                    DefaultMutableTreeNode node = getSelectedNode();
                    if (node != null) {
                        addGroup(node, Group.ROW_NONE);
                    }
                }

            };
        }
        return addGroupAction;
    }

    private Action getAddRowAction() {
        if (addRowAction == null) {
            addRowAction = new DesignAction.DesignBasedAction("structure_add_row") {

                public void actionPerformed(ActionEvent e) {
                    DefaultMutableTreeNode node = getSelectedNode();
                    if (node != null) {
                        addRow(node);
                    }
                }

            };
        }
        return addRowAction;
    }

    private Action getDelAction() {
        if (delAction == null) {
            delAction = new DesignAction.DesignBasedAction("structure_del") {

                public void actionPerformed(ActionEvent e) {
                    DefaultMutableTreeNode node = getSelectedNode();
                    if (node != null) {
                        removeNode(node);
                    }
                }

            };
        }
        return delAction;
    }

    private void removeNode(DefaultMutableTreeNode node) {
        if (node.getUserObject() instanceof TableRow) {
            TableRow row = (TableRow) node.getUserObject();
            grid.removeRow(row);
        } else {
            Group group = (Group) node.getUserObject();
            grid.removeGroup(group);
        }
        getTreeModel().removeNodeFromParent(node);
    }

    private void addRow(DefaultMutableTreeNode node) {
        RowsGroup group = (RowsGroup) node.getUserObject();
        TableRow row = grid.addRow(group);
        getTreeModel().insertNodeInto(new RowNode(row), node, node.getChildCount());
    }

    private void addGroup(DefaultMutableTreeNode node, int type) {
        GroupsGroup group = (GroupsGroup) node.getUserObject();
        Group childGroup = grid.addGroup(group, type);
        GroupNode childNode = new GroupNode(childGroup);
        getTreeModel().insertNodeInto(childNode, node, group.getChildIndex(childGroup));
    }

    private DefaultTreeModel getTreeModel() {
        return (DefaultTreeModel) tree.getModel();
    }

    private DefaultMutableTreeNode getSelectedNode() {
        TreePath path = tree.getSelectionPath();
        if (path != null) {
            return (DefaultMutableTreeNode) path.getLastPathComponent();
        }
        return null;
    }

    private void expandTree(JTree tree) {
        DefaultMutableTreeNode root =
                (DefaultMutableTreeNode) tree.getModel().getRoot();
        Enumeration e = root.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) e.nextElement();
            if (node.isLeaf()) continue;
            int row = tree.getRowForPath(new TreePath(node.getPath()));
            tree.expandRow(row);
        }
    }

    private void collapseTree(JTree tree) {
        int row = tree.getRowCount() - 1;
        int n = 1;
        while (row >= n) {
            tree.collapseRow(row);
            row--;
        }
    }

    public JButton getCollapseButton() {
        if (collapseButton == null) {
            collapseButton = new JButton();
            collapseButton.setActionCommand("collapse");
            collapseButton.addActionListener(this);
            collapseButton.setIcon(TemplateReportResources.getInstance().getIcon("tree_collapse.png"));
            collapseButton.setToolTipText(Messages
                    .getString("StructurePanel.collapse"));
        }
        return collapseButton;
    }

    public JButton getExpandButton() {
        if (expandButton == null) {
            expandButton = new JButton();
            expandButton.setActionCommand("expand");
            expandButton.addActionListener(this);
            expandButton.setIcon(TemplateReportResources.getInstance().getIcon("tree_expand.png"));
            expandButton.setToolTipText(Messages
                    .getString("StructurePanel.expand"));
        }
        return expandButton;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("expand".equals(e.getActionCommand())) {
            expandTree(tree);
        } else if ("collapse".equals(e.getActionCommand())) {
            collapseTree(tree);
        }
    }

    private static class GroupNode extends DefaultMutableTreeNode {

        public GroupNode(Group group) {
            super(group);
            children = new Vector();
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                Object child = group.getChild(i);
                if (child instanceof TableRow) {
                    children.add(new RowNode((TableRow) child, this));
                } else {
                    children.add(new GroupNode((Group) child, this));
                }
            }
        }

        public GroupNode(Group group, GroupNode parent) {
            this(group);
            setParent(parent);
        }

        @Override
        public boolean isLeaf() {
            return false;
        }
    }

    private static class RowNode extends DefaultMutableTreeNode {

        public RowNode(TableRow row) {
            super(row);
        }

        public RowNode(TableRow row, GroupNode parent) {
            super(row);
            setParent(parent);
        }
    }


    private class TreeTransferHandler extends TransferHandler {

        DataFlavor[] flavors = new DataFlavor[1];
        DataFlavor nodesFlavor;
        DefaultMutableTreeNode nodeToRemove;

        public TreeTransferHandler() {
            try {
                String mimeType = DataFlavor.javaJVMLocalObjectMimeType +
                        ";class=\"" +
                        javax.swing.tree.DefaultMutableTreeNode[].class.getName() +
                        "\"";
                nodesFlavor = new DataFlavor(mimeType);
                flavors[0] = nodesFlavor;
            } catch (ClassNotFoundException e) {
                System.out.println("ClassNotFound: " + e.getMessage());
            }
        }

        public boolean canImport(TransferHandler.TransferSupport support) {
            if (!support.isDrop()) {
                return false;
            }
            support.setShowDropLocation(true);
            if (!support.isDataFlavorSupported(nodesFlavor)) {
                return false;
            }

            JTree.DropLocation dl =
                    (JTree.DropLocation) support.getDropLocation();
            JTree tree = (JTree) support.getComponent();
            int dropRow = tree.getRowForPath(dl.getPath());
            int[] selRows = tree.getSelectionRows();
            if (selRows == null) return false;

            for (int selRow : selRows) {
                if (selRow == dropRow) {
                    return false;
                }
            }

            TreePath dest = dl.getPath();
            DefaultMutableTreeNode target =
                    (DefaultMutableTreeNode) dest.getLastPathComponent();
            if (target.isLeaf()) return false;

            Object data = getTransferData(support);
            if (data == null) return false;
            if (data instanceof TableRow
                    && !(target.getUserObject() instanceof RowsGroup)) return false;

            if (data instanceof Group) {
                if (!(target.getUserObject() instanceof TreeRowGroup)) return false;

                int targetType = ((Group) target.getUserObject()).getType();
                int type = ((Group) data).getType();

                if ((type == Group.ROW_DETAIL || type == Group.ROW_GROUP_HEADER || type == Group.ROW_GROUP_FOOTER)
                        && (targetType != Group.GROUP_DETAIL))
                    return false;
            }

            return true;
        }

        public boolean importData(TransferHandler.TransferSupport support) {

            if (!canImport(support)) {
                return false;
            }
            Object data = getTransferData(support);
            if (data == null) return false;

            JTree.DropLocation dl =
                    (JTree.DropLocation) support.getDropLocation();
            TreePath dest = dl.getPath();
            DefaultMutableTreeNode parent =
                    (DefaultMutableTreeNode) dest.getLastPathComponent();
            JTree tree = (JTree) support.getComponent();
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

            int childIndex = dl.getChildIndex();// DropMode.INSERT
             if (childIndex == -1) {     // DropMode.ON
                 childIndex = parent.getChildCount();
            }

            DefaultMutableTreeNode node = null;
            if (data instanceof TableRow) {
                node = insertRow((TableRow) data, parent, childIndex);
            } else {
                childIndex = insertGroup((Group) data, parent, childIndex);
                if (childIndex >= 0) {
                    node = new GroupNode((Group) data);
                }
            }

            if (node != null) {
                model.insertNodeInto(node, parent, childIndex);
                return true;
            }

            return false;
        }

        private int insertGroup(Group group, DefaultMutableTreeNode parent, int index) {
            if (group.getParent() != parent.getUserObject() || group.getParent().getChildIndex(group) != index) {
                return grid.moveGroup(group, index, (TreeRowGroup) parent.getUserObject());
            }
            return -1;
        }

        private DefaultMutableTreeNode insertRow(TableRow row, DefaultMutableTreeNode parent, int index) {
            int oldIndex = row.getGroup().getChildIndex(row);
            if (row.getGroup() != parent.getUserObject() || oldIndex != index) {
                grid.moveRow(row.getGroup(), oldIndex, (Group) parent.getUserObject(), index);
                return new RowNode(row);
            }
            return null;
        }

        private Object getTransferData(TransferSupport support) {
            try {
                Transferable t = support.getTransferable();
                return t.getTransferData(nodesFlavor);
            } catch (UnsupportedFlavorException ufe) {
                System.out.println("UnsupportedFlavor: " + ufe.getMessage());
            } catch (java.io.IOException ioe) {
                System.out.println("I/O error: " + ioe.getMessage());
            }
            return null;
        }

        protected void exportDone(JComponent source, Transferable data, int action) {
            if ((action & MOVE) == MOVE) {
                JTree tree = (JTree) source;
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                model.removeNodeFromParent(nodeToRemove);
            }
        }

        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }

        protected Transferable createTransferable(JComponent c) {
            JTree tree = (JTree) c;
            TreePath path = tree.getSelectionPath();
            if (path != null) {
                DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode) path.getLastPathComponent();
                Object object = node.getUserObject();
                if (object instanceof RootGroup) return null;
                if (object instanceof Group) {
                    int type = ((Group) object).getType();
                    if (type == Group.ROW_TITLE || type == Group.ROW_FOOTER
                            || type == Group.ROW_PAGE_HEADER || type == Group.ROW_PAGE_FOOTER) {
                        return null;
                    }
                }
                nodeToRemove = node;
                return new NodesTransferable(node.getUserObject());
            }
            return null;
        }

        public class NodesTransferable implements Transferable {
            Object node;

            public NodesTransferable(Object node) {
                this.node = node;
            }

            public Object getTransferData(DataFlavor flavor)
                    throws UnsupportedFlavorException {
                if (!isDataFlavorSupported(flavor))
                    throw new UnsupportedFlavorException(flavor);
                return node;
            }

            public DataFlavor[] getTransferDataFlavors() {
                return flavors;
            }

            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return nodesFlavor.equals(flavor);
            }
        }

    }

}
