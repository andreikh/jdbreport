/*
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2008 Andrey Kholmanskih. All rights reserved.
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
 * 
 */
package jdbreport.grid;

import javax.swing.SizeRequirements;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class NoWrapEditorKit extends StyledEditorKit implements ViewFactory {

	private static final long serialVersionUID = 1L;
	private boolean shouldWrap = false;

	public void setWrap(boolean wrap) {
		this.shouldWrap = wrap;
	}

	private class NoWrapParagraphView extends ParagraphView {
		public NoWrapParagraphView(Element elem) {
			super(elem);
		}

		protected SizeRequirements calculateMinorAxisRequirements(int axis,
				SizeRequirements r) {

			if (shouldWrap)
				return super.calculateMinorAxisRequirements(axis, r);

			SizeRequirements req = super
					.calculateMinorAxisRequirements(axis, r);
			req.minimum = req.preferred;
			return req;
		}

		public int getFlowSpan(int index) {

			if (shouldWrap)
				return super.getFlowSpan(index);

			return Integer.MAX_VALUE;
		}
	}

	public ViewFactory getViewFactory() {
		return this;
	}

	public View create(Element elem) {
		String kind = elem.getName();
		if (kind != null)
			if (kind.equals(AbstractDocument.ContentElementName)) {
				return new LabelView(elem);
			} else if (kind.equals(AbstractDocument.ParagraphElementName)) {
				return new NoWrapParagraphView(elem);
			} else if (kind.equals(AbstractDocument.SectionElementName)) {
				return new BoxView(elem, View.Y_AXIS);
			} else if (kind.equals(StyleConstants.ComponentElementName)) {
				return new ComponentView(elem);
			} else if (kind.equals(StyleConstants.IconElementName)) {
				return new IconView(elem);
			}
		return new LabelView(elem);
	}
}
