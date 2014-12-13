/*
 * Copyright (C) 2005-2014 Andrey Kholmanskih
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
				.getString("DefaultErrorHandler.0"),
				JOptionPane.ERROR_MESSAGE);
	}

	public synchronized void showError(String msg) {
		JOptionPane.showMessageDialog(null, msg, Messages
				.getString("DefaultErrorHandler.0"),
				JOptionPane.ERROR_MESSAGE);
	}

	public static ErrorHandler getInstance() {
		if (handler == null) {
			handler = new DefaultErrorHandler();
		}
		return handler;
	}
}
