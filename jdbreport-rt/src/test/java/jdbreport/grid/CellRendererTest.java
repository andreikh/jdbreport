package jdbreport.grid;

import org.junit.Test;

import java.text.NumberFormat;

import static org.junit.Assert.assertEquals;

/**
 * Author: andrey
 * Date: 09.09.16
 */
public class CellRendererTest {

    @Test
    public void testNumberFormat() {
        NumberFormat formatter = NumberFormat.getInstance();
        assertEquals("100", formatter.format(100.0d));
        assertEquals("100,1", formatter.format(100.1d));
    }
}
