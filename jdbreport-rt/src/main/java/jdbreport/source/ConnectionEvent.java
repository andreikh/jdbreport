/**
 * Created	16.02.2009
 *
 * Copyright 2009 Aviainstrument. All right reserved.
 */
package jdbreport.source;

import java.util.EventObject;

/**
 * @author Andrey Kholmanskih
 *
 * @version	1.0
 */
public class ConnectionEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	/**
	 * @param source
	 */
	public ConnectionEvent(Object source) {
		super(source);
	}

}
