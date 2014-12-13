/*
 * NoWrapHTMLKit.java
 *
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
