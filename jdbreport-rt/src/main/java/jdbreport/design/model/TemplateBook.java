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

import jdbreport.model.*;
import jdbreport.model.print.ReportPage;
import jdbreport.source.*;
import jdbreport.util.Utils;
import jdbreport.view.model.JReportModel;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * @author Andrey Kholmanskih
 * @version 3.1 14.12.2014
 */
public class TemplateBook extends ReportBook {

    public static final String JDBR = "jdbr";

    public static final String REPORT_CAPTION = "reportCaption";

    private static TreeMap<Object, String> WRITERS_MAP = new TreeMap<>();

    private static TreeMap<Object, String> READERS_MAP = new TreeMap<>();

    private static final Logger logger = Logger.getLogger(TemplateBook.class
            .getName());

    static {
        WRITERS_MAP.putAll(ReportBook.WRITERS_MAP);
        WRITERS_MAP.put(TemplateBook.JDBR,
                "jdbreport.design.model.xml.DbrFileType");

        READERS_MAP.putAll(ReportBook.READERS_MAP);
        READERS_MAP.put(TemplateBook.JDBR,
                "jdbreport.design.model.xml.DbrFileType");
    }

    private List<JdbcReportSource> sourcesList;

    private TemplateModel templModel;

    JReportModel newModel;

    private Map<Object, Object> vars;

    private Map<String, ReportDataSet> reportDSList;

    private Map<String, BufferedDataSet> dataSetList;

    private Map<String, CellFunctionObject> functionsList;

    private PageNumber currentPage;

    private PageCount pageCount;

    private KeyComparator keyComparator;

    private Map<String, BufferedDataSet> dsList;

    private DetailGroup currentGroup;

    private ExpressionFactory exprFactory;

    private ELContext elContext;

    private List<ReplaceItem> patternList;

    public TemplateBook() {
        super();
        currentPage = createPageNumber();
        pageCount = new PageCount();
        addReplacePattern("(0.0+)|(0+)|[\\00+]", "-");
    }

    protected PageNumber createPageNumber() {
        return new PageNumber();
    }

    public String getPageNumberFormat() {
        return currentPage.getFormat();
    }

    public void setPageNumberFormat(String format) {
        currentPage.setFormat(format);
    }

    protected Map<Object, String> getReaders() {
        return READERS_MAP;
    }

    protected Map<Object, String> getWriters() {
        return WRITERS_MAP;
    }

    protected String getDefaultReaderKey() {
        return JDBR;
    }

    protected String getDefaultWriterKey() {
        return JDBR;
    }

    protected ReportModel createDefaultModel() {
        TemplateModel model = new TemplateModel(getStyleList());
        model.setVars(getVars());
        model.setFunctionsList(getFunctionsList());
        model.setSourcesList(getSourcesList());
        return model;
    }

    public ReportBook createReportBook(HeightCalculator hCalc) {

        ReportBook book = new ReportBook();

        book.setSourceTemplate(getSourceTemplate());

        String reportTitle = (String) getVars().get(REPORT_CAPTION);
        if (reportTitle != null) {
            book.setReportCaption(reportTitle);
        } else {
            book.setReportCaption(getReportCaption());
        }
        book.setShowGrid(isShowGrid());
        book.setGlobalPageNumber(isGlobalPageNumber());
        book.getStyleList().putAll(getStyleList());

        try {
            prepareDataSets();

            initElContext();

            try {
                for (int i = 0; i < size(); i++) {
                    if (i == book.size())
                        book.add();
                    try {
                        generateReportModel(book.getReportModel(i), i, hCalc);
                    } catch (Exception e) {
                        Utils.showError(e);
                    }
                }
            } finally {
                getDataSetList().clear();
                closeSources();
            }
        } catch (Exception e) {
            Utils.showError(e);
        } finally {
            exprFactory = null;
            elContext = null;
        }
        return book;
    }

    protected void initElContext() {
        exprFactory = ExpressionFactory.newInstance();
        elContext = new ReportElContext();

        for (Object key : getVars().keySet()) {
            exprFactory.createValueExpression(elContext, "#{" + key + "}",
                    Object.class).setValue(elContext, vars.get(key));
        }

        for (Object key : getDataSetList().keySet()) {
            exprFactory.createValueExpression(elContext, "#{" + key + "}",
                    Object.class)
                    .setValue(elContext, getDataSetList().get(key));
        }

        for (String varName : SystemVar.getNames()) {
            ValueExpression var = exprFactory.createValueExpression(elContext,
                    "${" + varName + "}", Object.class);
            var.setValue(elContext, getSystemVarValue(varName));
            elContext.getVariableMapper().setVariable(varName, var);
        }
    }

    private void generateReportModel(JReportModel model, int index,
                                     HeightCalculator hCalc) throws ReportException {
        newModel = model;
        newModel.getRowModel().startUpdate();
        try {
            templModel = (TemplateModel) getReportModel(index);

            assignModel(newModel, templModel);

            RootGroup rootGroup = templModel.getRowModel().getRootGroup();
            RootGroup newRootGroup = newModel.getRowModel().getRootGroup();

            prepareCells(templModel);

            templModel.setTotalList(new HashMap<>());

            for (DetailGroup detailGroup : rootGroup.getDetailGroups()) {
                detailGroup.updateDataSet(getDataSetList());
                findTotalFunctions(detailGroup);
            }

            fillTotalInfo(rootGroup.getFooterGroup());

            dsList = new TreeMap<>();
            try {
                for (int i = 0; i < rootGroup.getChildCount() - 1; i++) {
                    Group group = rootGroup.getChild(i);
                    if (group.getType() == Group.GROUP_DETAIL) {
                        insertDetail(
                                (DetailGroup) newRootGroup
                                        .addGroup(Group.GROUP_DETAIL),
                                (DetailGroup) group);
                        currentGroup = null;
                    } else
                        insertGroup((RowsGroup) newRootGroup.addGroup(group
                                .getType()), (RowsGroup) group);
                }

                insertHeaderFooterGroup(
                        (RowsGroup) newRootGroup.getGroup(Group.ROW_FOOTER),
                        (RowsGroup) rootGroup.getGroup(Group.ROW_FOOTER));
                resetTotals(rootGroup.getFooterGroup());
            } finally {
                dsList.clear();
                dsList = null;
            }
            newModel.setCanUpdatePages(templModel.isCanUpdatePages());
            newModel.getRowModel().enableSpan();
            newModel.updateRowAndPageHeight(hCalc);
        } finally {
            newModel.getRowModel().endUpdate();
            newModel = null;
        }
    }

    private void prepareCells(TemplateModel templModel) throws ReportException {
        List<Expression> tokens = new ArrayList<>();
        for (TableRow row : templModel.getRowModel()) {
            for (Cell cell : row) {
                if (!cell.isNull() && !cell.isChild()) {
                    try {
                        CellObject co = (CellObject) cell;
                        if (!co.getText().isEmpty()) {
                            prepareCellExpr(co, tokens);
                            if (tokens.size() > 0) {
                                Expression[] expr = new Expression[tokens
                                        .size()];
                                tokens.toArray(expr);
                                co.setExpressions(expr);
                                tokens.clear();
                            }
                        }
                    } catch (Exception e) {
                        CellCoord p = templModel.getCellPosition(cell);
                        throw new ReportException("Error in cellObject - "
                                + p.row + ":" + p.column, e);
                    }
                }
            }
        }
    }

    private void prepareCellExpr(CellObject co, List<Expression> tokens) {
        String s = co.getText();
        while (s.length() > 0) {
            int b = s.indexOf("${");
            if (b < 0)
                b = s.indexOf("#{");
            if (b >= 0) {
                int e = s.indexOf("}", b);
                if (e > 0) {
                    String beforeExpr = s.substring(0, b);
                    if (beforeExpr.length() > 0)
                        tokens.add(new TextExpression(beforeExpr));
                    tokens.add(createExpression(s.substring(b + 2, e)));
                    s = s.substring(e + 1);
                } else {
                    if (tokens.size() > 0) {
                        tokens.add(new TextExpression(s));
                    }
                    break;
                }
            } else {
                if (tokens.size() > 0) {
                    tokens.add(new TextExpression(s));
                }
                break;
            }
        }
    }

    protected Expression createExpression(String s) {
        String t;
        int i = s.indexOf('.');
        if (i > 0) {
            t = s.substring(0, i);
        } else {
            t = s;
        }
        return new ElExpression(t, s);
    }

    private void assignModel(JReportModel newModel, TemplateModel templModel) {
        newModel.setColumnCount(templModel.getColumnCount());
        newModel.getRowModel().removeRows();
        newModel.setStretchPage(templModel.isStretchPage());
        newModel.setReportPage((ReportPage) templModel.getReportPage().clone());
        newModel.setEditable(templModel.isEditable());
        newModel.setVisible(templModel.isVisible());
        newModel.setRowMoving(templModel.isRowMoving());
        newModel.setRowSizing(templModel.isRowSizing());
        newModel.setColMoving(templModel.isColMoving());
        newModel.setColSizing(templModel.isColSizing());
        newModel.setReportTitle(templModel.getReportTitle());
        newModel.setPrintLeftToRight(templModel.isPrintLeftToRight());

        for (int c = 0; c < templModel.getColumnCount(); c++) {
            newModel.getColumnModel().getColumn(c)
                    .setPreferredWidth(templModel.getColumnWidth(c));
            newModel.setColumnBreak(c, templModel.isColumnBreak(c));
        }
    }

    private void findTotalFunctions(DetailGroup group) {
        RowsGroup footerGroup = group.getFooterGroup();
        fillTotalInfo(footerGroup);
        RowsGroup headerGroup = group.getHeaderGroup();
        fillTotalInfo(headerGroup);
        Iterator<Group> itg = group.getGroupIterator();
        while (itg.hasNext()) {
            Group g = itg.next();
            if (g instanceof DetailGroup) {
                findTotalFunctions((DetailGroup) g);
            }
        }
    }

    private void fillTotalInfo(RowsGroup group) {
        if (group != null) {
            for (TableRow tableRow : group) {
                for (int column = 0; column < tableRow.getColCount(); column++) {
                    CellObject cellObject = (CellObject) tableRow
                            .getCellItem(column);
                    if (cellObject.getTotalFunction() != CellObject.AF_NONE) {
                        templModel.getTotalList().put(
                                cellObject,
                                new TotalInfo(cellObject.getTotalFunction(),
                                        column, group));
                    }
                }
            }
        }
    }

    private void prepareDataSets() throws ReportException {
        getDataSetList().clear();
        for (String key : getReportDataSetList().keySet()) {
            getDataSetList().put(
                    key,
                    new BufferedDataSet(getReportDataSetList().get(key),
                            getVars()));
        }
        for (JdbcReportSource source : getSourcesList()) {
            for (JdbcDataSet ds : source) {
                ds.setVars(getVars());
                getDataSetList().put(ds.getId(),
                        new BufferedDataSet(ds, getVars()));
            }
        }

        for (BufferedDataSet ds : getDataSetList().values()) {
            openMasterDs(ds);
        }
    }

    private void closeSources() {
        int i = 0;
        while (i < getSourcesList().size()) {
            JdbcReportSource source = getSourcesList().get(i);
            for (JdbcDataSet aSource : source) {
                aSource.close();
            }
            i++;
        }
    }

    private void openMasterDs(BufferedDataSet ds) throws ReportException {
        if (ds.getMasterId() != null) {
            BufferedDataSet masterDS = getDataSetList().get(ds.getMasterId());
            if (masterDS != null) {
                openMasterDs(masterDS);
                if (ds.checkParamsChange(masterDS)) {
                    ds.reopen();
                }
                masterDS.addDataSetListener(ds);
            }
        }
        ds.open();
    }

    private boolean insertDetail(DetailGroup newGroup, DetailGroup group)
            throws ReportException {
        if (group == null || group.getChildCount() == 0)
            return false;
        newGroup.setRepeateHeader(group.isRepeateHeader());
        currentGroup = group;
        if (group.getChild(0).getType() == Group.ROW_NONE) {
            insertGroup((RowsGroup) newGroup.addGroup(Group.ROW_NONE),
                    (RowsGroup) group.getChild(0));
        }
        RowsGroup headerGroup = group.getHeaderGroup();
        RowsGroup footerGroup = group.getFooterGroup();
        DetailGroup parentGroup = (DetailGroup) ((group.getParent() instanceof DetailGroup) ? group
                .getParent() : null);
        boolean changeParentGroupKey;
        boolean eof;
        while ((!(eof = group.isEof()) || group.minRowLimit())
                && !group.maxRowLimit()) {
            boolean changeGroupKey = false;
            if (eof) {
                group.clearKeys();
            }
            group.initGroupKey(getKeyComparator());
            if (headerGroup != null) {
                RowsGroup newHeaderGroup = (RowsGroup) newGroup
                        .addGroup(headerGroup.getType());
                insertHeaderFooterGroup(newHeaderGroup, headerGroup);
            }
            do {
                for (int i = group.getFirstDetailGroup(); i <= group
                        .getLastDetailGroup(); i++) {
                    Group childGroup = group.getChild(i);
                    if (childGroup instanceof RowsGroup) {
                        if (childGroup.getType() == Group.ROW_DETAIL) {
                            insertDetailRowGroup(
                                    (RowsGroup) newGroup.addGroup(childGroup
                                            .getType()), (RowsGroup) childGroup);
                            group.incGenRowCount();
                        } else
                            insertGroup(
                                    (RowsGroup) newGroup.addGroup(childGroup
                                            .getType()), (RowsGroup) childGroup);
                        incTotalValues(templModel.getTotalList(),
                                (RowsGroup) childGroup);
                    } else if (childGroup instanceof DetailGroup) {
                        /*
						 * changeGroupKey = insertDetail((DetailGroup) newGroup
						 * .getParent().addGroup(Group.GROUP_DETAIL),
						 * (DetailGroup) childGroup);
						 */
                        changeGroupKey = insertDetail(
                                (DetailGroup) newGroup
                                        .addGroup(Group.GROUP_DETAIL),
                                (DetailGroup) childGroup);
                        currentGroup = group;
                    }
                }
                if (!changeGroupKey) {
                    changeGroupKey = group.changeGroupKey(getKeyComparator());
                }

                changeParentGroupKey = changeParentGroup(parentGroup);

            } while (!changeParentGroupKey && !changeGroupKey
                    && (next(group.getDsList()) || group.minRowLimit())
                    && !group.maxRowLimit());

            if (footerGroup != null) {
                insertHeaderFooterGroup(
                        (RowsGroup) newGroup.addGroup(footerGroup.getType()),
                        footerGroup);
            }
            resetTotals(group);
            if (changeParentGroupKey) {
                dsList.putAll(group.getDsList());
                return true;
            }
            if (changeGroupKey) {
                dsList.putAll(group.getDsList());
                next(dsList);
                dsList.clear();
                newGroup = (DetailGroup) newGroup.getParent().addGroup(
                        newGroup.getType());
            }
        }
        if (group.getChild(group.getChildCount() - 1).getType() == Group.ROW_NONE) {
            insertGroup((RowsGroup) newGroup.addGroup(Group.ROW_NONE),
                    (RowsGroup) group.getChild(group.getChildCount() - 1));
        }
        return false;
    }

    private boolean changeParentGroup(DetailGroup parentGroup)
            throws ReportException {
        while (parentGroup != null) {
            if (parentGroup.changeGroupKey(getKeyComparator())) {
                return true;
            }
            if (parentGroup.getParent() != null
                    && parentGroup.getParent().getType() == Group.GROUP_DETAIL) {
                parentGroup = (DetailGroup) parentGroup.getParent();
            } else {
                return false;
            }
        }
        return false;
    }

    private void resetTotals(Group group) throws ReportException {
        for (CellObject cellObject : templModel.getTotalList().keySet()) {
            TotalInfo ti = templModel.getTotalList().get(cellObject);
            if (ti.getGroup() == group || ti.getGroup().getParent() == group) {
                ti.copyValue();
                if (cellObject.getFunctionName() != null) {
                    for (Cell cell : ti.getTargetCells()) {
                        CellCoord coord = newModel.getCellPosition(cell);
                        runFunction(cellObject, coord.row, coord.column);
                    }
                }
                ti.resetValue();
            }
        }

    }

    private void incTotalValues(Map<CellObject, TotalInfo> totalList,
                                RowsGroup group) throws ReportException {
        for (CellObject cell : totalList.keySet()) {
            TotalInfo ti = totalList.get(cell);
            double value = 0;
            try {
                if (cell.getExpressions() != null && cell.getExpressions().length > 0) {
                    if (isChildGroup(ti.getGroup().getParent(), group)) {
                        Object o = getExprValue(cell.getExpressions());
                        if (o != null) {
                            value = Double.parseDouble(o.toString());
                            ti.incValue(value);
                        }
                    }
                } else if (isChildGroup(ti.getGroup().getParent(), group)) {
                    int row = newModel.getRowCount() - 1;
                    for (int i = 0; i < group.getChildCount(); i++) {
                        Object o = newModel.getValueAt(row - i, ti.getColumn());
                        if (o != null) {
                            value += Double.parseDouble(o.toString());
                        }
                    }
                    ti.incValue(value);
                }

            } catch (NumberFormatException ignored) {
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage());
            }
        }
    }

    /**
     * Determines
     *
     * @param parent     parent group
     * @param childGroup child group
     * @return true if childGroup enters in parent
     */
    private boolean isChildGroup(GroupsGroup parent, RowsGroup childGroup) {
        Iterator<RowsGroup> it = parent.getAllGroupIterator();
        while (it.hasNext()) {
            if (it.next() == childGroup)
                return true;
        }
        return false;
    }

    private boolean next(Map<String, BufferedDataSet> dsList)
            throws ReportException {
        if (dsList == null || dsList.size() == 0)
            return false;
        boolean result = false;
        for (BufferedDataSet ds : dsList.values()) {
            ds.resetCursorPos();
        }

        for (BufferedDataSet ds : dsList.values()) {
            if (!ds.isCursorChange() && nextMasterDs(dsList, ds))
                result = true;
        }
        return result;
    }

    private boolean nextMasterDs(Map<String, BufferedDataSet> dsList,
                                 BufferedDataSet ds) throws ReportException {
        boolean result = false;
        if (ds.getMasterId() != null) {
            BufferedDataSet masterDS = dsList.get(ds.getMasterId());
            if (masterDS != null) {
                result = nextMasterDs(dsList, masterDS);
            }
        }
        if (!ds.isCursorChange() && ds.next())
            result = true;
        return result;
    }

    /**
     * Adds to generating report the rows from group
     *
     * @param group the rows' group
     */
    private void insertGroup(RowsGroup newGroup, RowsGroup group) {
        if (group == null || group.getChildCount() == 0)
            return;
        for (TableRow row : group) {
            TableRow newRow = newModel.getRowModel().createTableRow();
            newRow.setHeight(row.getHeight());
            newRow.setPageBreak(row.isPageBreak());
            int index = newModel.getRowModel().addRow(newGroup, -1, newRow);
            updateRow(row, newRow, index);
        }
    }

    private void insertDetailRowGroup(RowsGroup newGroup, RowsGroup group) {
        if (group == null || group.getChildCount() == 0)
            return;
        for (TableRow row : group) {
            TableRow newRow = newModel.getRowModel().createTableRow();
            newRow.setHeight(row.getHeight());
            newRow.setPageBreak(row.isPageBreak());
            int index = newModel.getRowModel().addRow(newGroup, -1, newRow);
            updateDetailRow(row, newRow, index, newGroup);
        }
    }

    private void insertHeaderFooterGroup(RowsGroup newGroup, RowsGroup group) {
        if (group == null || group.getChildCount() == 0)
            return;
        for (TableRow row : group) {
            TableRow newRow = newModel.getRowModel().createTableRow();
            newRow.setHeight(row.getHeight());
            newRow.setPageBreak(row.isPageBreak());
            int index = newModel.getRowModel().addRow(newGroup, -1, newRow);
            updateHeaderFooterRow(row, newRow, index);
        }
    }

    /**
     * Copy cell's property from template to result report
     *
     * @param row    the template report row
     * @param newRow a result report row
     */
    private void updateHeaderFooterRow(TableRow row, TableRow newRow,
                                       int currentRow) {
        for (int column = 0; column < newRow.getColCount(); column++) {
            CellObject oldCell = (CellObject) row.getCellItem(column);
            if (oldCell.isNull() || oldCell.isChild())
                continue;
            try {
                generateHeaderFooterCell(newRow, currentRow, column, oldCell);
            } catch (ReportException e) {
                e.printStackTrace();
            }
            if (oldCell.getColSpan() > 0) {
                column += oldCell.getColSpan();
            }
        }
    }

    protected Cell generateHeaderFooterCell(TableRow newRow, int currentRow,
                                            int column, CellObject oldCell) throws ReportException {

        Cell newCell = oldCell.createCellItem();
        newRow.setCellItem(newCell, column);

        if (oldCell.getTotalFunction() != CellObject.AF_NONE) {
            TotalInfo ti = templModel.getTotalList().get(oldCell);
            if (ti != null) {
                ti.addTargetCell(newCell);
            }
        } else {
            if (oldCell.getExpressions() != null) {
                newCell.setValue(getExprValue(oldCell.getExpressions()));
            }
            runFunction(oldCell, currentRow, column);
        }

        checkReplacement(newCell, oldCell);
        return newCell;
    }

    /**
     * Copy cell's property from template to result report
     *
     * @param row    the template report row
     * @param newRow a result report row
     */
    private void updateRow(TableRow row, TableRow newRow, int currentRow) {
        for (int column = 0; column < newRow.getColCount(); column++) {
            CellObject oldCell = (CellObject) row.getCellItem(column);
            if (oldCell.isNull() || oldCell.isChild())
                continue;
            try {
                generateCell(newRow, currentRow, column, oldCell);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error in cell - " + currentRow + ":"
                        + column, e);
            }
            if (oldCell.getColSpan() > 0) {
                column += oldCell.getColSpan();
            }
        }
    }

    protected Cell generateCell(TableRow newRow, int currentRow, int column,
                                CellObject oldCell) throws ReportException {

        Cell newCell = oldCell.createCellItem();

        if (oldCell.getExpressions() != null) {
            newCell.setValue(getExprValue(oldCell.getExpressions()));
        }

        newRow.setCellItem(newCell, column);
        runFunction(oldCell, currentRow, column);
        checkReplacement(newCell, oldCell);
        return newCell;
    }

    private void checkReplacement(Cell cell, CellObject oldCell) {
        if (patternList == null) return;

        if (oldCell.isReplacement()) {
            Object value = cell.getValue();
            String s = (value == null) ? "\0" : value.toString();
            for (ReplaceItem f : patternList) {
                Matcher m = f.getPattern().matcher(s);
                if (m.matches()) {
                    s = m.replaceAll(f.getReplacement());
                    cell.setValue(s);
                }
            }
        }
    }

    /**
     * @param regexp      old value
     * @param replacement new value
     * @since 2.0
     */
    public void addReplacePattern(String regexp, String replacement) {
        if (patternList == null) {
            patternList = new ArrayList<>();
        }
        patternList.add(new ReplaceItem(regexp, replacement));
    }

    public List<ReplaceItem> getReplacePatterns() {
        return patternList;
    }

    /**
     * @since 2.0
     */
    public void clearReplacePatterns() {
        patternList = null;
    }


    private Object getExprValue(Expression[] expressions)
            throws ReportException {
        if (expressions.length > 1) {
            String value = "";
            for (Expression expression : expressions) {
                try {
                    value += expression.getFormatValue();
                } catch (Exception e) {
                    logger.log(Level.WARNING, e.getMessage());
                }
            }
            return value;
        } else {
            try {
                return expressions[0].getValue();
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage());
                return "";
            }
        }
    }

    private Object getDetailExprValue(Expression[] expressions)
            throws ReportException {
        if (expressions.length > 1) {
            String value = ""; //$NON-NLS-1$
            for (Expression expression : expressions) {
                try {
                    String v = getExprDsValue(expression);
                    if (v != null) {
                        value += v;
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, e.getMessage());
                }
            }
            return value;
        } else {
            try {
                ReportDataSet ds = getDataSet(expressions[0].getBaseName());
                if (ds != null) {
                    if (!((BufferedDataSet) ds).isEof()) {
                        return expressions[0].getValue();
                    } else {
                        return null;
                    }
                } else {
                    return expressions[0].getValue();
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage());
                return "";
            }
        }
    }

    private String getExprDsValue(Expression expression) throws ReportException {
        ReportDataSet ds = getDataSet(expression.getBaseName());
        if (ds != null) {
            if (!((BufferedDataSet) ds).isEof()) {
                return expression.getFormatValue();
            } else {
                return null;
            }
        } else {
            return expression.getFormatValue();
        }
    }

    /**
     * Copyes cell's property from template to result report
     *
     * @param row    the template report row
     * @param newRow a result report row
     */
    private void updateDetailRow(TableRow row, TableRow newRow, int currentRow,
                                 RowsGroup group) {
        int isNoRep = 1;

        for (String v : SystemVar._ROW.names())
            elContext.getVariableMapper().resolveVariable(v)
                    .setValue(elContext, getCurrentRow());

        for (int column = 0; column < newRow.getColCount(); column++) {
            CellObject cell = (CellObject) row.getCellItem(column);
            if (cell.isNull() || cell.isChild())
                continue;
            try {
                Cell newCell = generateDetailCell(newRow, currentRow, column,
                        cell);

                if (cell.isNotRepeat()) {
                    if (isNoRep > 0) {
                        isNoRep = cell.isOldEquals(newCell.getValue()) ? 2 : 0;
                    }
                    cell.setOldValue(newCell.getValue());
                }
            } catch (ReportException e) {
                Utils.showError(e);
                throw new RuntimeException();
            }
            if (cell.getColSpan() > 0)
                column += cell.getColSpan();

        }
        if (isNoRep == 2) {
            for (int column = 0; column < newRow.getColCount(); column++) {
                CellObject cell = (CellObject) row.getCellItem(column);
                if (cell.isNull() || cell.isChild() || !cell.isNotRepeat())
                    continue;
                Cell newCell = newRow.getCellItem(column);
                if (newCell.isNull() || newCell.isChild())
                    continue;
                newCell.setValue(null);
                int rowIndex = group.getChildIndex(newRow) - 1;
                if (rowIndex >= 0) {
                    Cell ownerCell = group.getChild(rowIndex).getCellItem(
                            column);
                    if (ownerCell.isChild())
                        ownerCell = ownerCell.getOwner();
                    ownerCell.setRowSpan(ownerCell.getRowSpan() + 1);
                    newCell.setOwner(ownerCell);
                    if (cell.getColSpan() > 0)
                        column += cell.getColSpan();
                }
            }
        }
    }

    protected Cell generateDetailCell(TableRow newRow, int currentRow,
                                      int column, CellObject oldCell) throws ReportException {

        Cell newCell = oldCell.createCellItem();

        if (oldCell.getExpressions() != null) {
            newCell.setValue(getDetailExprValue(oldCell.getExpressions()));
        }

        newRow.setCellItem(newCell, column);
        runFunction(oldCell, currentRow, column);
        checkReplacement(newCell, oldCell);
        return newCell;
    }


    private void runFunction(CellObject cell, int row, int column)
            throws ReportException {
        if (cell.getFunctionName() == null)
            return;
        CellFunction cellFunction = findCellFunction(cell.getFunctionName(),
                row, column);
        if (cellFunction != null) {
            try {
                cellFunction.run();
            } catch (OutOfMemoryError e) {
                System.err.println(String.format(
                        Messages.getString("TemplateBook.errorfunc"),
                        cell.getFunctionName(), row, column));
                e.printStackTrace();
                throw new OutOfMemoryError(
                        Messages.getString("TemplateBook.outofmemory"));
            } catch (Exception e) {
                throw new ReportException(String.format(
                        Messages.getString("TemplateBook.errorfunc"),
                        cell.getFunctionName(), row, column), e);
            }
        }

    }

    protected Object getSystemVarValue(String varName) {
        SystemVar var = SystemVar.find(varName);
        if (var == null) return null;
        return getSystemVarValue(var);
    }

    protected Object getSystemVarValue(SystemVar var) {
        switch (var) {
            case _PAGE:
                return getCurrentPage();
            case _ROW:
                return getCurrentRow();
            case _PAGE_COUNT:
                return getPageCount();
        }
        return null;
    }

    /**
     * Current value of variable - VAR_ROW
     *
     * @return the detail row's number
     */
    private Integer getCurrentRow() {
        return currentGroup != null ? currentGroup.getCurrentRow() + 1 : 0;
    }

    private PageNumber getCurrentPage() {
        return currentPage;
    }

    private PageCount getPageCount() {
        return pageCount;
    }


    protected void revalidatePageNumbers() {
    }

    public Object getVarValue(Object name) {
        SystemVar var = SystemVar.find((String) name);
        if (var != null) {
            return getSystemVarValue(var);
        }
        return getVars().get(name);
    }

    /**
     * Sets variable
     *
     * @param name  variable name
     * @param value variable value
     */
    public void setVarValue(Object name, Object value) {
        SystemVar var = SystemVar.find(name.toString());
        if (var != null) {
            return;
        }
        getVars().put(name, value);

        if (elContext != null) {
            exprFactory.createValueExpression(elContext, "#{" + name + "}",
                    Object.class).setValue(elContext, value);
        }
    }

    public boolean findVar(String name) {
        SystemVar var = SystemVar.find(name);
        if (var != null) return true;
        return getVars().containsKey(name);
    }

    public Map<Object, Object> getVars() {
        if (vars == null) {
            vars = new HashMap<>();
        }
        return vars;
    }

    public void addDataSet(JdbcDataSet ds) {
        getDefaultSource().add(ds);
    }

    public void addReportDataSet(ReportDataSet ds) {
        getReportDataSetList().put(ds.getId(), ds);
    }

    public void addReportDataSet(String id, Iterable<?> ds) {
        addReportDataSet(new IterableDataSet(id, ds));
    }

    public void addReportDataSet(String id, Iterator<?> ds) {
        addReportDataSet(new IteratorDataSet(id, ds));
    }

    public void addReportDataSet(String id, Object ds) {
        addReportDataSet(new ObjectDataSet(id, ds));
    }

    public void addReportDataSet(String id, Object[] ds) {
        addReportDataSet(new ArrayDataSet(id, ds));
    }

    /**
     * @param id dataset id
     * @param ds dataset
     * @since 2.0
     */
    public void addReportDataSet(String id, Map<String, Object> ds) {
        addReportDataSet(new MapDataSet(id, ds));
    }

    private Map<String, ReportDataSet> getReportDataSetList() {
        if (reportDSList == null) {
            reportDSList = new HashMap<>();
        }
        return reportDSList;
    }

    /**
     * Returns the dataSetList
     *
     * @return the dataSetList.
     */
    protected Map<String, BufferedDataSet> getDataSetList() {
        if (dataSetList == null) {
            dataSetList = new HashMap<>();
        }
        return dataSetList;
    }

    public ReportDataSet getDataSet(Object key) {
        return getDataSetList().get(key);
    }

    void setDataSet(ReportDataSet ds) {
        BufferedDataSet bds = new BufferedDataSet(ds);
        getDataSetList().put(ds.getId(), bds);
        exprFactory.createValueExpression(elContext, "#{" + ds.getId() + "}",
                Object.class).setValue(elContext, bds);
        try {
            bds.open();
        } catch (ReportException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        try {
            RootGroup rootGroup = templModel.getRowModel().getRootGroup();
            for (DetailGroup detailGroup : rootGroup.getDetailGroups()) {
                detailGroup.replaceDataSet(bds);
            }
        } catch (Exception e) {
            Utils.showError(e);
        }
    }

    /**
     * @param driverName JDBC driver
     * @param url        the DataBase url
     * @param properties the connection's properties
     */
    public void setDefaultSource(String driverName, String url,
                                 Properties properties) {
        JdbcReportSource dataSource = new JdbcReportSource();
        dataSource.setDriverName(driverName);
        dataSource.setUrl(url);
        dataSource.setProperties(properties);
        if (getSourcesList().size() > 0)
            getSourcesList().set(0, dataSource);
        else
            getSourcesList().add(dataSource);
    }

    public JdbcReportSource getDefaultSource() {
        if (getSourcesList().size() == 0) {
            getSourcesList().add(new JdbcReportSource());
        }
        return getSourcesList().get(0);
    }

    public List<JdbcReportSource> getSourcesList() {
        if (sourcesList == null) {
            sourcesList = new ArrayList<>();
        }
        return sourcesList;
    }

    public void addSource(JdbcReportSource source) {
        getSourcesList().add(source);
    }

    public Map<String, CellFunctionObject> getFunctionsList() {
        if (functionsList == null) {
            functionsList = new TreeMap<>();
        }
        return functionsList;
    }

    public CellFunction findCellFunction(String functionName, int row,
                                         int column) {
        CellFunctionObject funcObject = getFunctionsList().get(functionName);
        if (funcObject != null) {
            CellFunction cellFunction = funcObject.getCellFunction();
            if (cellFunction != null) {
                ((AbstractCellFunction) cellFunction).setProvider(this, row,
                        column);
                return cellFunction;
            }
        }
        return null;
    }

    private KeyComparator getKeyComparator() {
        if (keyComparator == null) {
            keyComparator = new KeyComparator() {

                public void init(GroupKey groupKey) throws ReportException {
                    if (groupKey.getType() == Expression.TYPE_VAR) {
                        groupKey.setValue(vars.get(groupKey.getName()));
                    } else {
                        BufferedDataSet ds = getDataSetList().get(
                                groupKey.getDataSetID());
                        if (ds != null && !ds.isEof())
                            groupKey.setValue(ds.getValue(groupKey.getName()));
                    }

                }

                public boolean compare(GroupKey groupKey)
                        throws ReportException {
                    Object newValue = null;
                    if (groupKey.getType() == Expression.TYPE_VAR) {
                        newValue = getVars().get(groupKey.getName());
                    } else {
                        BufferedDataSet ds = getDataSetList().get(
                                groupKey.getDataSetID());
                        if (ds != null && !ds.isEof()) {
                            if (ds.isDsEof())
                                return false;
                            newValue = ds.getNextValue(groupKey.getName());
                        }
                    }
                    if (newValue == null) {
                        if (groupKey.getValue() == null)
                            return true;
                        groupKey.setValue(null);
                        return false;
                    }
                    if (!newValue.equals(groupKey.getValue())) {
                        groupKey.setValue(newValue);
                        return false;
                    }
                    return true;
                }

            };
        }
        return keyComparator;
    }

    public void clear() {
        getVars().clear();
        getSourcesList().clear();
        getFunctionsList().clear();
        super.clear();
    }

    public String getMimeType() {
        return "application/jdbreport.template";
    }

    private class ElExpression implements Expression {

        private ValueExpression expr;
        private String baseName;

        public ElExpression(String baseName, String text) {
            this.baseName = baseName;
            expr = exprFactory.createValueExpression(elContext, "#{" + text
                    + "}", Object.class);
        }

        public String getText() {
            return expr.getExpressionString();
        }

        public String getBaseName() {
            return baseName;
        }

        public String getProperty() {
            String text = getText();
            if (text != null) {
                int i = text.indexOf(baseName);
                if (i > 0) {
                    text = text.substring(i + baseName.length() + 1,
                            text.length() - 1);
                    text = sub(text, '.');
                    text = sub(text, '[');
                    text = sub(text, '(');
                    return text;
                }
            }
            return null;
        }

        private String sub(String text, char c) {
            int n = text.indexOf(c);
            if (n > 0) {
                text = text.substring(0, n);
            }
            return text;
        }

        public Object getValue() throws ReportException {
            return expr.getValue(elContext);
        }

        public int getType() {
            return TYPE_FIELD;
        }

        public String getFormatValue() throws ReportException {
            Object value = getValue();
            if (value instanceof Date) {
                return getDateFormatter().format(value);
            }
            return value != null ? value.toString() : "";
        }

    }

}
