/**
 * Created	16.02.2009
 *
 * Copyright 2009 Aviainstrument. All right reserved.
 */
package jdbreport.source;

import java.util.EventListener;

/**
 * @author Andrey Kholmanskih
 *
 * @version	1.0
 */
public interface ConnectionListener extends EventListener {

	void connectionClose(ConnectionEvent e);
}
