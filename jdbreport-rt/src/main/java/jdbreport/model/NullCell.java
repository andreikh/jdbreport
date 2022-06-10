/*
 * Created on 26.12.2004
 *
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

import javax.swing.Icon;

/**
 * @author Andrey Kholmanskih
 * @version 3.0 13.12.2014
 */
public class NullCell implements Cell {


    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     *
     * @see jdbreport.interfaces.Cell#isNull()
     */
    public boolean isNull() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see jdbreport.interfaces.Cell#getValue()
     */
    public Object getValue() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see jdbreport.interfaces.Cell#setValue(java.lang.Object)
     */
    public void setValue(Object value) {

    }

    /*
     * (non-Javadoc)
     *
     * @see jdbreport.interfaces.Cell#getRowSpan()
     */
    public int getRowSpan() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see jdbreport.interfaces.Cell#setRowSpan(int)
     */
    public void setRowSpan(int value) {

    }

    /*
     * (non-Javadoc)
     *
     * @see jdbreport.interfaces.Cell#getColSpan()
     */
    public int getColSpan() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see jdbreport.interfaces.Cell#setColSpan(int)
     */
    public void setColSpan(int value) {

    }

    /*
     * (non-Javadoc)
     *
     * @see jdbreport.interfaces.Cell#isChild()
     */
    public boolean isChild() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see jdbreport.interfaces.Cell#isSpan()
     */
    public boolean isSpan() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see jdbreport.interfaces.Cell#getOwner()
     */
    public Cell getOwner() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see jdbreport.interfaces.Cell#setOwner(jdbreport.interfaces.Cell)
     */
    public void setOwner(Cell cell) {

    }

    /*
     * (non-Javadoc)
     *
     * @see jdbreport.interfaces.Cell#getFontIndex()
     */
    public Object getStyleId() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see jdbreport.interfaces.Cell#setFontIndex(int)
     */
    public void setStyleId(Object index) {

    }

    public int getExtFlags() {
        return 0;
    }

    public void setExtFlags(int i) {

    }

    public boolean isNotPrint() {
        return false;
    }

    public void setNotPrint(boolean b) {

    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Object clone() {
        return this;
    }

    public String getText() {
        return EMPTY_STRING;
    }

    public void setIcon(Icon icon) {
    }

    @Deprecated
    public Icon getIcon() {
        return null;
    }

    public boolean isScaleIcon() {
        return false;
    }

    public void setScaleIcon(boolean scale) {
    }

    public void setImageFormat(String format) {
    }

    public String getImageFormat() {
        return null;
    }

    public String getContentType() {
        return TEXT_PLAIN;
    }

    public void clear() {
    }

    public Type getValueType() {
        return Type.STRING;
    }

    public void setValueType(Type valueType) {
    }

    public boolean isEditable() {
        return true;
    }

    public void setEditable(boolean b) {
    }

    public void setImage(Image image) {
    }

    public Picture getPicture() {
        return null;
    }

    public void setPicture(Picture picture) {
    }

    @Override
    public String getCellFormula() {
        return null;
    }

    @Override
    public void setCellFormula(String formula) {
    }
}
