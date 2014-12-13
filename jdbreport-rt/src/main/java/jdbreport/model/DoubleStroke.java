/*
 * JDBReport Generator
 * 
 * Copyright (C) 2011-2014 Andrey Kholmanskih
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

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * @version 1.0 03.05.2011
 * 
 * @author Andrey Kholmanskih
 * @since 2.0
 */
public class DoubleStroke implements Stroke {

	private Stroke stroke, edgeStroke;

	public DoubleStroke(float width) {
		this.stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
		this.edgeStroke = new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER);
	}

	public Shape createStrokedShape(Shape shape) {
		return edgeStroke.createStrokedShape(stroke.createStrokedShape(shape));
	}
}
