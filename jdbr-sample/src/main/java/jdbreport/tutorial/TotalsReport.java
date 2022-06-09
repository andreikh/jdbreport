/*
 *
 * Copyright 2009 Andrey Kholmanskih. All rights reserved.
 */

package jdbreport.tutorial;

import java.util.ArrayList;
import java.util.List;

import jdbreport.design.model.TemplateBook;
import jdbreport.model.ReportBook;
import jdbreport.model.io.LoadReportException;
import jdbreport.util.Utils;
import jdbreport.view.ReportEditor;

/**
 * @version 1.2 02/04/09
 * @author Andrey Kholmanskih
 * 
 */
public class TotalsReport extends SampleItem {

	public static class Test {

		public String firstName;

		public String lastName;

		public String colorName;

		public Double number;

		public Integer age;

		public Test(String firstName, String lastName, String colorName,
				Integer age, Double number) {
			super();
			this.firstName = firstName;
			this.lastName = lastName;
			this.colorName = colorName;
			this.age = age;
			this.number = number;
		}

	}

	final Object[][] data = {
			{ "Avril", "Carry", "orange", 14, 8.0},
			{ "Jeam", "Bullon", "orange", 16, 22.0},
			{ "Mark", "Verzilina", "blue", 13, 3.0},
			{ "Mike", "Black", "blue", new Integer(11), new Double(13) },
			{ "Lucy", "Weeler", "blue", new Integer(14), new Double(8) },
			{ "Eric", "Hilary", "blue", new Integer(18), new Double(.693) },
			{ "Jenifer", "Kim", "blue", new Integer(15), new Double(655321) },
			{ "Justin", "Kennedy", "blue", new Integer(12), new Double(17) },
			{ "Martin", "King", "blue", new Integer(14), new Double(3) },
			{ "Lada", "Freeze", "blue", new Integer(16), new Double(241) },
			{ "Kristina", "Kholmanskih", "pink", new Integer(17),new Double(21) },
			{ "Phillip", "Hengry", "pink", new Integer(17),	new Double(3) },
			{ "Maria", "Parcker", "black", new Integer(16),	new Double(2.718) },
			{ "Andrew", "Falls", "black", new Integer(12), new Double(23) },
			{ "Jerry", "Plastinin", "black", new Integer(14), new Double(3.14) },
			{ "Rick", "Lone", "black", new Integer(16), new Double(1327) },
			{ "Lara", "Bunny", "red", new Integer(18), new Double(15)},
			{ "Timur", "Jackson", "red", new Integer(15), new Double(69) },
			{ "Daria", "White", "red", new Integer(15), new Double(3) },
			{ "Luck", "Burov", "darkgreen", new Integer(13), new Double(27) },
			{ "Phiona", "Makarova", "darkgreen", new Integer(17), new Double(52) },
			{ "Peter", "Sun", "sunpurple", new Integer(15), new Double(12), },
			{ "Ewan", "Spears", "yellow", new Integer(15), new Double(2) },
			{ "Katerina", "Koshkina", "yellow", new Integer(17), new Double(3) },
			{ "Lev", "Zykin", "purple", new Integer(16), new Double(5) },
			{ "Paris", "Hilton", "green", new Integer(16), new Double(2), },
			{ "Stuart", "Little", "green", new Integer(19), new Double(8) },
			{ "Zhenya", "Albers", "green", new Integer(14), new Double(44.0) },
			{ "Robin", "Holms", "green", new Integer(17), new Double(89) },
			{ "Garry", "Garner", "green", new Integer(19), new Double(7) },
			{ "Sasha", "Lamp", "green", new Integer(12), new Double(3) },
			{ "David", "Candle", "forestgreen", new Integer(13), new Double(7) },
			{ "Victoria", "Lohan", "gray", new Integer(14), new Double(9) },
			{ "Anton", "Muller", "gray", new Integer(13), new Double(0) },
			{ "Vlad", "Kokarev", "gray", new Integer(15), new Double(290) },
			{ "Steve", "Field", "gray", new Integer(18), new Double(7) },
			{ "Jim", "Shilov", "gray", new Integer(12), new Double(13) } };

	private List<Test> testList;

	private void initTestList() {
		testList = new ArrayList<Test>();
		for (int i = 0; i < data.length; i++) {
			Test test = new Test((String) data[i][0], (String) data[i][1],
					(String) data[i][2], (Integer) data[i][3],
					(Double) data[i][4]);
			testList.add(test);
		}
	}

	public static void main(String[] args) {
		TotalsReport report = new TotalsReport();
		report.run();
	}

	@Override
	public String getCaption() {
		return "Totals";
	}

	@Override
	public String getTemplate() {
		return "totalsreport.jdbr";
	}

	@Override
	public void run() {
		initTestList();
		try {
			TemplateBook tbook = new TemplateBook();
			tbook.open(getTemplateURL());
			tbook.addReportDataSet("ds1", testList);
			tbook.addReportDataSet("ds2", testList);
			ReportEditor re = new ReportEditor();
			ReportBook book = tbook.createReportBook(re);
			re.setReportBook(book);
			re.setVisible(true);
		} catch (LoadReportException e) {
			Utils.showError(e);
		}
	}

}
