/*
 * Created	15.10.2010
 *
 * Copyright (C) 2010-2014 Andrey Kholmanskih
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

import jdbreport.model.svg.SVGImage;

import java.util.logging.Logger;

/**
 * @author Andrey Kholmanskih
 * 
 * @version 1.0 28.10.2010
 */
public class PictureFactory {

	private static final Logger logger = Logger.getLogger(PictureFactory.class.getName());

	public static boolean isEnableSVG() {
		return SVGImage.isEnableSVG();
	}

	public static Picture createPicture(String format) {
		if (format != null) {
			if (format.equalsIgnoreCase("svg")) {
				if (isEnableSVG()) {
					return new PictureSVG(format);
				} else {
					logger.warning("Format " + format + " is not supported");
					return null;
				}
			} else if (format.equalsIgnoreCase("wmf")) {
				if (isEnableSVG()) {
					return new PictureWMF(format);
				} else {
					logger.warning("Format " + format + " is not supported");
					return null;
				}
			}
		}
		return new Picture(format);
	}
}
