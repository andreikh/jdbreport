/*
 * JDBReport Generator
 * 
 * Copyright (C) 2011 Andrey Kholmanskih. All rights reserved.
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
