/*
 * NoWrapHTMLKit.java
 *
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
 */

package jdbreport.grid;

import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.InlineView;

/**
 * @version 1.1 03/09/08
 * @author Andrey Kholmanskih
 * 
 */
public class NoWrapHTMLKit extends HTMLEditorKit {

	private static final long serialVersionUID = 1L;
	private boolean shouldWrap = true;
	ViewFactory noWrapFactory = new NoWrapViewFactory();

	public void setWrap(boolean wrap) {
		this.shouldWrap = wrap;
	}

	public ViewFactory getViewFactory() {
		return shouldWrap ? super.getViewFactory() : noWrapFactory;
	}

	static class NoWrapViewFactory extends HTMLEditorKit.HTMLFactory {

		public View create(Element elem) {
			Object o = elem.getAttributes().getAttribute(
					StyleConstants.NameAttribute);

			if (o instanceof HTML.Tag) {
				HTML.Tag kind = (HTML.Tag) o;
				if (kind == HTML.Tag.CONTENT) {
					return new NoWrapBoxView(elem);
				}
			}

			return super.create(elem);
		}
	}

	static class NoWrapBoxView extends InlineView {
		public NoWrapBoxView(Element elem) {
			super(elem);
		}

		public int getBreakWeight(int axis, float pos, float len) {
			return BadBreakWeight;
		}

	}

}
