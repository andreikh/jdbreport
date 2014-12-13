/*
 * JDBReport Generator
 * 
 * Copyright (C) 2007-2014 Andrey Kholmanskih
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
