/*
 * Created 19.03.2011
 *
 * Copyright (C) 2011 Andrey Kholmanskih. All rights reserved.
 *
*/
package jdbreport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;

import jdbreport.model.io.LoadReportException;


/**
 * @author Andrey Kholmanskih
 *
 * @version 1.0 19.03.2011
 */
@Ignore
public class RoundTotalTest {

	
	public void roundTest() throws LoadReportException {
		Map<String, Object> dsList = new HashMap<String, Object>();
		List<Value> values = new ArrayList<Value>();
		values.add(new Value(34485l));
		values.add(new Value(3886.0));
		values.add(new Value(4406538866l));
		values.add(new Value(440696193l));
		
//		dsList.put("list", values);
//		JDBReport.showReport(getClass().getResource("roundtotal.jdbr"), dsList);
		
		dsList.put("traffic", values);
		JDBReport.showReport(getClass().getResource("traffic.jdbr"), dsList);
		
	}
	
	public static class Value {
		public Number value;
		
		public Value(Number v) {
			this.value = v;
		}
		
		public Number getInamount() {
			return value; 
		}
		
		public Number getOutamount() {
			return value; 
		}

	}
	
	public static void main(String[] args)  {
		RoundTotalTest test = new RoundTotalTest();
		try {
			test.roundTest();
		} catch (LoadReportException e) {
			e.printStackTrace();
		}
	}
}
