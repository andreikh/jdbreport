/*
 * Copyright (C) 2005-2006 Andrey Kholmanskih. All rights reserved.
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
 * 
 * Andrey Kholmanskih
 * support@jdbreport.com
 */
package jdbreport.view;

import jdbreport.util.ErrorHandler;

import javax.swing.*;

/**
 * @version 3.0 12.12.2014
 * @author Andrey Kholmanskih
 * 
 */
public class DefaultErrorHandler implements ErrorHandler {

	private static ErrorHandler handler;

	public DefaultErrorHandler() {
		super();
	}

	public synchronized void showError(Throwable e) {
		e.printStackTrace();
		String msg = e.getCause() != null && e.getCause().getMessage() != null ? e
				.getCause().getMessage()
				: e.getMessage();
		JOptionPane.showMessageDialog(null, msg, Messages
				.getString("DefaultErrorHandler.0"), //$NON-NLS-1$
				JOptionPane.ERROR_MESSAGE);
	}

	public synchronized void showError(String msg) {
		JOptionPane.showMessageDialog(null, msg, Messages
				.getString("DefaultErrorHandler.0"), //$NON-NLS-1$
				JOptionPane.ERROR_MESSAGE);
	}

	public static ErrorHandler getInstance() {
		if (handler == null) {
			handler = new DefaultErrorHandler();
		}
		return handler;
	}
}
