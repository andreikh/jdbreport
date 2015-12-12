/*
 *
 * Copyright 2008-2015 Andrey Kholmanskih. All rights reserved.
 */

package jdbreport.tutorial;

import java.util.*;

import jdbreport.JDBReport;
import jdbreport.model.io.LoadReportException;
import jdbreport.tutorial.helper.Test;
import jdbreport.util.Utils;

/**
 * @version 1.0 12.05.2008
 * @author Andrey Kholmanskih
 * 
 */
public class SubDetailReport extends SampleItem {

	public static class ColorFavorits {
		
		private String colorName;
		private List<Test> tests = new ArrayList<>();
	
		public ColorFavorits(String colorName) {
			super();
			this.colorName = colorName;			
		}
		
		public String getColorName() {
			return colorName;
		}
		
		public  List<Test> getTests() {
			return tests;
		}
		
		public int getCount() {
			return tests.size();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((colorName == null) ? 0 : colorName.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final ColorFavorits other = (ColorFavorits) obj;
			if (colorName == null) {
				if (other.colorName != null)
					return false;
			} else if (!colorName.equals(other.colorName))
				return false;
			return true;
		}
		
		
	}
	
	final Object[][] data = {
			{ "Zhenya", "Albers", "green", "Brazil", new Double(44.0),
					"strawberry" },
			{ "Mark", "Verzilina", "blue", "Curse of the Demon", new Double(3),
					"grapes" },
			{ "Maria", "Parcker", "black", "The Blues Brothers",
					new Double(2.7182818285), "raspberry" },
			{ "Lara", "Bunny", "red", "Airplane (the whole series)",
					new Double(15), "strawberry" },
			{ "Mike", "Black", "blue", "The Man Who Knew Too Much",
					new Double(13), "peach" },
			{ "Andrew", "Falls", "black", "Blade Runner (Director's Cut)",
					new Double(23), "broccoli" },
			{ "Luck", "Burov", "darkgreen", "Brazil", new Double(27),
					"asparagus" },
			{ "Lucy", "Weeler", "blue", "The Lady Vanishes", new Double(8),
					"kiwi" },
			{ "Ewan", "Spears", "yellow", "A Bug's Life", new Double(2),
					"strawberry" },
			{ "Katerina", "Koshkina", "violet", "Reservoir Dogs",
					new Double(3), "raspberry" },
			{ "Lev", "Zykin", "purple", "Jules et Jim", new Double(5),
					"raspberry" },
			{ "Daria", "White", "blue", "Pulp Fiction", new Double(3),
					"watermelon" },
			{ "Kristina", "Kholmanskih", "pink",
					"Blade Runner (Director's Cut)", new Double(21), "donut" },
			{ "Eric", "Hilary", "blue", "The Shawshank Redemption",
					new Double(.693), "pickle" },
			{ "Paris", "Hilton", "green", "Pulp Fiction", new Double(2),
					"grapes" },
			{ "Stuart", "Little", "green", "Goodfellas", new Double(8),
					"carrot" },
			{ "Robin", "Holms", "green", "The Last of the Mohicans",
					new Double(89), "apple" },
			{ "Jenifer", "Kim", "blue", "Lone Star", new Double(655321),
					"strawberry" },
			{ "Ann", "Rues", "turquoise", "The Stuntman", new Double(7),
					"peach" },
			{ "Justin", "Kennedy", "blue", "Once Upon A Time In The West",
					new Double(17), "pineapple" },
			{ "Avril", "Carry", "orange", "The Music Man", new Double(8),
					"broccoli" },
			{ "Peter", "Sun", "sunpurple", "Harold & Maude", new Double(12),
					"sparegrass" },
			{ "Rick", "Lone", "black", "The Fifth Element", new Double(1327),
					"raspberry" },
			{ "Jeam", "Bullon", "jfcblue", "The Joy Luck Club", new Double(22),
					"pear" },
			{ "Victoria", "Lohan", "beige", "City of Lost Children",
					new Double(9), "corn" },
			{ "Sasha", "Lamp", "green", "Schindler's List", new Double(3),
					"strawberry" },
			{ "David", "Candle", "forestgreen", "Withnail & I", new Double(7),
					"peach" },
			{ "Phillip", "Hengry", "suspectpink", "Das Boot", new Double(3),
					"banana" },
			{ "Phiona", "Makarova", "cybergreen", "Eraserhead", new Double(52),
					"peach" },
			{ "Anton", "Muller", "rustred", "Labyrinth", new Double(0),
					"pineapple" },
			{ "Martin", "King", "blue", "At First Sight", new Double(3),
					"pineapple" },
			{ "Timur", "Jackson", "blue", "None", new Double(69), "pepper" },
			{ "Steve", "Field", "jfcblue2", "Defending Your Life",
					new Double(7), "broccoli" },
			{
					"Garry",
					"Garner",
					"green",
					"The Adventures of Buckaroo Banzai Across the 8th Dimension",
					new Double(7), "strawberry" },
			{
					"Jerry",
					"Plastinin",
					"black",
					"The Bicycle Thief",
					new Double(
							3.141592653589793238462643383279502884197169399375105820974944),
					"banana" },
			{ "Vlad", "Kokarev", "aqua", "This is Spinal Tap", new Double(290),
					"cantaloupe" },
			{ "Lada", "Freeze", "blue", "Repo Man", new Double(241), "pepper" },
			{ "Ashanti", "Cell", "blue", "The Fifth Element", new Double(0xFF),
					"pepper" },
			{ "Elena", "Schort", "green", "2001: A Space Odyssey",
					new Double(47), "watermelon" },
			{ "Katerina", "Bokova", "darkgreen", "Star Wars", new Double(13),
					"watermelon" },
			{ "Sandy", "Tucker", "eblue", "Aliens", new Double(2), "broccoli" },
			{ "Pasha", "Rukov", "red", "Raiders of the Lost Ark",
					new Double(222), "tomato" },
			{ "Scott", "Wild", "violet", "The Thin Man", new Double(-97),
					"banana" },
			{ "Stefany", "Carrot", "blue", "Chusingura (1962)", new Double(8),
					"pear" },
			{ "Pamela", "Gwen", "black", "Raiders of the Lost Ark",
					new Double(3), "grapefruit" },
			{ "Julia", "Petelina", "green", "My Life as a Dog", new Double(7),
					"onion" },
			{ "Jim", "Shilov", "gray", "None", new Double(13), "grapes" } };

	private Map<String, ColorFavorits> colorMap;

	private void initTestList() {
		colorMap = new TreeMap<>();
		for (int i = 0; i < data.length; i++) {
			Test test = new Test((String) data[i][0], (String) data[i][1],
					(String) data[i][2], (String) data[i][3],
					(Double) data[i][4], (String) data[i][5]);
			
			ColorFavorits cf = colorMap.get(test.getColorName());
			if (cf == null) {
				cf = new ColorFavorits(test.getColorName());
				colorMap.put(cf.getColorName(), cf);
			}
			cf.getTests().add(test);
		}
	}

	public static void main(String[] args) {
		SubDetailReport report = new SubDetailReport();
		report.run();
	}

	@Override
	public String getCaption() {
		return "Dynamic insert dataset's in functions of cells";
	}

	@Override
	public String getTemplate() {
		return "subdetail.jdbr";
	}

	@Override
	public void run() {
		initTestList();
		try {
			Map<String, Object> dsList = new HashMap<>();
			Collection<ColorFavorits> colors = colorMap.values();
			
			dsList.put("colors", colors);
			dsList.put("test", colorMap.get("black").getTests());
			JDBReport.showReport(getTemplateURL(), dsList);
		} catch (LoadReportException e) {
			Utils.showError(e);
		}
	}

}
