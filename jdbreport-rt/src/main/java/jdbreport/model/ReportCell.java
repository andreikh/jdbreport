/*
 * ReportCell.java
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
import java.util.Date;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @version 3.0 13.12.2014
 * 
 * @author Andrey Kholmanskih
 * 
 */
public class ReportCell implements Cell {

	private static final long serialVersionUID = -8821568917060765855L;

	public static HashMap<Class<?>, CellValueInfo> defaultValuesByClass = new HashMap<>();

	private Object value;

	private Object styleIndex;

	private int rowSpan = 0;

	private int colSpan = 0;

	private Cell owner;

	private int extFlags;

	private boolean notPrint;

	private Picture picture;

	private boolean scaleIcon;

	private Type valueType = Type.STRING;

	private boolean editable = true;

	private String cellFormula;

	/** Creates a new instance of ReportCell */
	public ReportCell() {
	}

	/**
	 * Adds or removes default cell values
	 */
	public static void setDefaultCellValueClass(Class<?> valueClass,
			Class<?> cellValueClass, String rendererClass, String editorClass) {
		if (cellValueClass != null) {
			defaultValuesByClass.put(valueClass, new CellValueInfo(
					cellValueClass, rendererClass, editorClass));
		} else {
			defaultValuesByClass.remove(valueClass);
		}
	}

	public String toString() {
		if (value != null)
			return value.toString();
		else
			return "";
	}

	public Object getValue() {
		if (owner == null)
			return value;
		else
			return owner.getValue();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setValue(Object value) {
		if (owner == null) {
			if (value != null) {
				CellValueInfo vi = getDefaultCellValueInfo(value.getClass());
				if (vi != null) {
					CellValue<Object> cellValue;
					try {
						cellValue = (CellValue) vi.getCellValueClass()
								.newInstance();
						cellValue.setValue(value);
						value = cellValue;
					} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
				} else {
					if (value instanceof Icon) {
						Picture pict = new Picture((ImageIcon) value);
						pict.setScale(isScaleIcon());
						picture = pict;
						value = null;
					} else if (value instanceof Image) {
						Picture pict = new Picture((Image) value);
						pict.setScale(isScaleIcon());
						picture = pict;
						value = null;
					} else if (value instanceof Picture) {
						picture = (Picture) value;
						value = null;
					}
				}
			}
			this.value = value;
			if (this.value != null) {
				if (value instanceof Number) {
					valueType = Type.FLOAT;
				} else if (value instanceof Date) {
					valueType = Type.DATE;
				} else if (value instanceof Boolean) {
					valueType = Type.BOOLEAN;
				} else if (getValueType() == Type.FLOAT) {
					try {
						Double.parseDouble(this.value.toString());
					} catch (Exception e) {
						valueType = Type.STRING;
					}
				}
			}
		}
	}

	private CellValueInfo getDefaultCellValueInfo(Class<?> valueClass) {
		if (valueClass == null) {
			return null;
		} else {
			CellValueInfo vi = defaultValuesByClass.get(valueClass);
			if (vi != null) {
				return vi;
			} else {
				vi = getDefaultCellValueInfo(valueClass.getSuperclass());
				if (vi != null) {
					defaultValuesByClass.put(valueClass, vi);
				}
				return vi;
			}
		}
	}

	public int getColSpan() {
		return colSpan;
	}

	public void setColSpan(int value) {
		colSpan = value;
		if (colSpan > 0)
			setOwner(null);
	}

	public int getRowSpan() {
		return rowSpan;
	}

	public void setRowSpan(int value) {
		rowSpan = value;
		if (rowSpan > 0)
			setOwner(null);
	}

	public Cell getOwner() {
		return owner;
	}

	public void setOwner(Cell newOwner) {
		if (newOwner == this) {
			owner = null;
		} else {
			owner = newOwner;
		}
		if (owner != null) {
			colSpan = 0;
			rowSpan = 0;
			value = null;
			picture = null;
		}
	}

	public boolean isChild() {
		return (owner != null && owner != this);
	}

	public boolean isSpan() {
		return (colSpan > 0 || rowSpan > 0);
	}

	public boolean isNull() {
		return false;
	}

	public Object getStyleId() {
		if (owner == null) {
			return styleIndex;
		} else {
			return owner.getStyleId();
		}
	}

	public void setStyleId(Object index) {
		if (owner != null) {
			owner.setStyleId(index);
		} else {
			styleIndex = index;
		}
	}

	public int getExtFlags() {
		return extFlags;
	}

	public void setExtFlags(int i) {
		this.extFlags = i;
	}

	public boolean isNotPrint() {
		return notPrint;
	}

	public void setNotPrint(boolean b) {
		this.notPrint = b;
	}

	public Object clone() {
		Cell cell = null;
		try {
			cell = (Cell) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return cell;
	}

	public String getText() {
		if (owner != null) {
			return owner.getText();
		}
		return toString();
	}

	public void setIcon(Icon icon) {
		if (owner == null) {
			if (icon == null) {
				this.picture = null;
			} else {
				this.picture = new Picture((ImageIcon) icon);
			}
		}
	}

	public void setImage(Image image) {
		if (owner == null) {
			if (image == null) {
				this.picture = null;
			} else {
				this.picture = new Picture(image);
			}
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see jdbreport.model.Cell#getIcon()
	 * @deprecated use getPricture()
	 */
	public Icon getIcon() {
		if (owner != null) {
			return owner.getPicture() != null ? owner.getPicture().getIcon() : null;
		}
		return picture != null ? picture.getIcon() : null;
	}

	public Picture getPicture() {
		if (owner != null) {
			return owner.getPicture();
		}
		return picture;
	}

	public void setPicture(Picture picture) {
		this.picture = picture;
	}

	public boolean isScaleIcon() {
		return picture != null ? picture.isScale() : this.scaleIcon;
	}

	public void setScaleIcon(boolean scale) {
		if (picture != null) {
			picture.setScale(scale);
		}
		this.scaleIcon = scale;
	}

	public void setImageFormat(String format) {
		if (owner != null) {
			owner.setImageFormat(format);
		} else {
			if (picture != null) {
				picture.setFormat(format);
			}
		}
	}

	public String getImageFormat() {
		if (owner == null) {
			return picture != null ? picture.getFormat() : null;
		} else {
			return owner.getImageFormat();
		}
	}

	public String getContentType() {
		if (owner != null) {
			return owner.getContentType();
		} else {
			String str = getText();
			String sign = str == null ? "" : str.trim()
					.substring(0, Math.min(str.trim().length(), 6))
					.toLowerCase();
			if (sign.startsWith("<html")) {
				return Cell.TEXT_HTML;
			} else
				return Cell.TEXT_PLAIN;
		}
	}

	public void clear() {
		value = null;
		styleIndex = null;
		rowSpan = 0;
		colSpan = 0;
		owner = null;
		extFlags = 0;
		notPrint = false;
		picture = null;
		scaleIcon = false;
		this.valueType = Type.STRING;
	}

	public Type getValueType() {
		return valueType;
	}

	public void setValueType(Type valueType) {
		this.valueType = valueType;
	}

	public boolean isEditable() {
		return owner != null ? owner.isEditable() : editable;
	}

	public void setEditable(boolean b) {
		editable = b;

	}

	@Override
	public String getCellFormula() {
		return cellFormula;
	}

	@Override
	public void setCellFormula(String formula) {
		if (formula != null && formula.trim().isEmpty())
			this.cellFormula = null;
		else
			this.cellFormula = formula;
	}

}
