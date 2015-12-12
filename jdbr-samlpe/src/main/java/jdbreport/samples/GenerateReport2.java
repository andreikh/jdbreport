package jdbreport.samples;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jdbreport.design.model.TemplateBook;
import jdbreport.model.Border;
import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.Group;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportModel;
import jdbreport.model.RowsGroup;
import jdbreport.model.print.ReportPage;
import jdbreport.view.ReportEditor;

public class GenerateReport2 {

	private static final String CUSTOMERS = "customers";

	public static class Customer {
		private int custNo;
		private String customer;
		private String contactFirst;
		private String contactLast;
		private String phone;
		private String address;
		private String city;
		private String postCode;
		private String country;

		public Customer() {

		}

		public Customer(int custNo, String customer, String contactFirst,
				String contactLast, String phone, String address, String city,
				String country, String postCode) {
			super();
			this.custNo = custNo;
			this.customer = customer;
			this.contactFirst = contactFirst;
			this.contactLast = contactLast;
			this.phone = phone;
			this.address = address;
			this.city = city;
			this.postCode = postCode;
			this.country = country;
		}

		/**
		 * @return the custNo
		 */
		public int getCustNo() {
			return custNo;
		}

		/**
		 * @param custNo
		 *            the custNo to set
		 */
		public void setCustNo(int custNo) {
			this.custNo = custNo;
		}

		/**
		 * @return the customer
		 */
		public String getCustomer() {
			return customer;
		}

		/**
		 * @param customer
		 *            the customer to set
		 */
		public void setCustomer(String customer) {
			this.customer = customer;
		}

		/**
		 * @return the contactFirst
		 */
		public String getContactFirst() {
			return contactFirst;
		}

		/**
		 * @param contactFirst
		 *            the contactFirst to set
		 */
		public void setContactFirst(String contactFirst) {
			this.contactFirst = contactFirst;
		}

		/**
		 * @return the contactLast
		 */
		public String getContactLast() {
			return contactLast;
		}

		/**
		 * @param contactLast
		 *            the contactLast to set
		 */
		public void setContactLast(String contactLast) {
			this.contactLast = contactLast;
		}

		/**
		 * @return the phone
		 */
		public String getPhone() {
			return phone;
		}

		/**
		 * @param phone
		 *            the phone to set
		 */
		public void setPhone(String phone) {
			this.phone = phone;
		}

		/**
		 * @return the address
		 */
		public String getAddress() {
			return address;
		}

		/**
		 * @param address
		 *            the address to set
		 */
		public void setAddress(String address) {
			this.address = address;
		}

		/**
		 * @return the city
		 */
		public String getCity() {
			return city;
		}

		/**
		 * @param city
		 *            the city to set
		 */
		public void setCity(String city) {
			this.city = city;
		}

		/**
		 * @return the postCode
		 */
		public String getPostCode() {
			return postCode;
		}

		/**
		 * @param postCode
		 *            the postCode to set
		 */
		public void setPostCode(String postCode) {
			this.postCode = postCode;
		}

		/**
		 * @return the country
		 */
		public String getCountry() {
			return country;
		}

		/**
		 * @param country
		 *            the country to set
		 */
		public void setCountry(String country) {
			this.country = country;
		}

		public String getContact() {
			return (getContactFirst() == null ? "" : (getContactFirst() + " "))
					+ (getContactLast() == null ? "" : getContactLast());
		}

	}

	private static final int CUST_NO = 0;
	private static final int CUSTOMER = 1;
	private static final int CONTACT = 2;
	private static final int PHONE = 3;
	private static final int ADDRESS = 4;
	private static final int CITY = 5;
	private static final int COUNTRY = 6;
	private static final int POSTAL_CODE = 7;

	private static List<Customer> getData() {
		List<Customer> customers = new ArrayList<Customer>();
		customers.add(new Customer(1001, "Signature Design", "Dale J.",
				"Little", "(619) 530-2710", "15500 Pacific Heights Blvd.",
				"San Diego", "USA", "92121"));
		customers.add(new Customer(1002, "Dallas Technologies", "Glen",
				"Brown", "(214) 960-2233", "P. O. Box 47000", "Dallas", "USA",
				"75205"));
		customers.add(new Customer(1003, "Buttle, Griffith and Co.", "James",
				"Buttle", "(617) 488-1864", "2300 Newbury Street Suite 101",
				"Boston", "USA", "02115"));
		customers.add(new Customer(1004, "Central Bank", "Elizabeth",
				"Brocket", "61 211 99 88", "66 Lloyd Street", "Manchester",
				"England", "M2 3LA"));
		customers.add(new Customer(1005, "DT Systems, LTD.", "Tai", "Wu",
				"(852) 850 43 98", "400 Connaught Road", "Central Hong Kong",
				"Hong Kong", null));
		customers.add(new Customer(1006, "DataServe International", "Tomas",
				"Bright", "(613) 229 3323", "2000 Carling Avenue Suite 150",
				"Ottawa", "Canada", "K1V 9G1"));
		customers.add(new Customer(1007, "Mrs. Beauvais", "", "Mrs. Beauvais",
				"", "P.O. Box 22743", "Pebble Beach", "USA", "93953"));
		customers.add(new Customer(1008, "Anini Vacation Rentals", "Leilani",
				"Briggs", "(808) 835-7605", "3320 Lawai Road", "Lihue", "USA",
				"96766"));
		customers.add(new Customer(1009, "Max", "Max", null, "22 01 23",
				"1 Emerald Cove", "Turtle Island", "Fiji", null));
		customers.add(new Customer(1010, "MPM Corporation", "Miwako",
				"Miyamoto", "3 880 77 19", "2-64-7 Sasazuka", "Tokyo", "Japan",
				"150"));
		customers.add(new Customer(1011, "Dynamic Intelligence Corp", "Victor",
				"Granges", "01 221 16 50", "Florhofgasse 10", "Zurich",
				"Switzerland", "8005"));
		customers.add(new Customer(1012, "3D-Pad Corp.", "Michelle", "Roche",
				"1 43 60 61", "22 Place de la Concorde", "Paris", "France",
				"75008"));
		customers.add(new Customer(1013, "Lorenzi Export, Ltd.", "Andreas",
				"Lorenzi", "02 404 6284", "Via Eugenia, 15", "Milan", "Italy",
				"20124"));
		customers
				.add(new Customer(1014, "Dyno Consulting", "Greta", "Hessels",
						"02 500 5940", "Rue Royale 350", "Brussels", "Belgium",
						"1210"));
		customers.add(new Customer(1015, "GeoTech Inc.", "K.M.",
				"Neppelenbroek", "(070) 44 91 18", "P.0.Box 702", "Den Haag",
				"Netherlands", "2514"));
		return customers;
	}

	private static final int[] WIDTHS = {50, 130, 90, 100, 140, 75, 80, 60 };
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		List<Customer> customers = getData();

		TemplateBook tb = new TemplateBook();
		tb.setShowGrid(false);
		ReportModel rm = tb.getReportModel(0);
		rm.getReportPage().setOrientation(ReportPage.LANDSCAPE);
		rm.setColumnCount(8);
		rm.setColumnWidths(WIDTHS);

		RowsGroup group = (RowsGroup) rm.getRowModel().getRootGroup().addGroup(
				Group.ROW_TITLE);
		rm.getRowModel().addRow(group, -1);

		Cell cell = rm.createReportCell(0, 0);
		cell.setColSpan(7);
		cell.setValue("Customers");
		CellStyle titleStyle = tb.getStyles(null);
		titleStyle = titleStyle.deriveAutoHeight(true);
		titleStyle = titleStyle.deriveFont(18f);
		titleStyle = titleStyle.deriveFont(CellStyle.BOLD);
		titleStyle = titleStyle.deriveHAlign(CellStyle.CENTER);
		Object idTitleStyle = tb.addStyle(titleStyle);
		cell.setStyleId(idTitleStyle);

		CellStyle tableTitleStyle = tb.getStyles(null);
		tableTitleStyle = tableTitleStyle.deriveAutoHeight(true);
		tableTitleStyle = tableTitleStyle.deriveFont(12f);
		tableTitleStyle = tableTitleStyle.deriveFont(CellStyle.BOLD);
		tableTitleStyle = tableTitleStyle.deriveHAlign(CellStyle.CENTER);
		tableTitleStyle = tableTitleStyle.deriveBorder(Border.LINE_TOP,
				new Border());
		tableTitleStyle = tableTitleStyle.deriveBorder(Border.LINE_RIGHT,
				new Border());
		Object idTableTitleStyle = tb.addStyle(tableTitleStyle);

		CellStyle leftTitleStyle = tableTitleStyle.deriveBorder(
				Border.LINE_LEFT, new Border());
		Object idLeftTitleStyle = tb.addStyle(leftTitleStyle);

		rm.getRowModel().addRow(group, -1);

		group = (RowsGroup) rm.getRowModel().getRootGroup().addGroup(Group.ROW_PAGE_HEADER);
		int row = rm.getRowModel().addRow(group, 0);

		cell = rm.createReportCell(row, CUST_NO);
		cell.setValue("Cust.No");
		cell.setStyleId(idLeftTitleStyle);

		cell = rm.createReportCell(row, CUSTOMER);
		cell.setValue("Customer");
		cell.setStyleId(idTableTitleStyle);

		cell = rm.createReportCell(row, CONTACT);
		cell.setValue("Contact");
		cell.setStyleId(idTableTitleStyle);

		cell = rm.createReportCell(row, PHONE);
		cell.setValue("Phone");
		cell.setStyleId(idTableTitleStyle);

		cell = rm.createReportCell(row, ADDRESS);
		cell.setValue("Address");
		cell.setStyleId(idTableTitleStyle);

		cell = rm.createReportCell(row, CITY);
		cell.setValue("City");
		cell.setStyleId(idTableTitleStyle);

		cell = rm.createReportCell(row, COUNTRY);
		cell.setValue("Country");
		cell.setStyleId(idTableTitleStyle);

		cell = rm.createReportCell(row, POSTAL_CODE);
		cell.setValue("Postal Code");
		cell.setStyleId(idTableTitleStyle);

		CellStyle cellStyle = tableTitleStyle.deriveFont(CellStyle.PLAIN);
		cellStyle = cellStyle.deriveFont(10f);
		cellStyle = cellStyle.deriveHAlign(CellStyle.LEFT);
		Object idCellStyle = tb.addStyle(cellStyle);

		CellStyle leftCellStyle = cellStyle.deriveBorder(Border.LINE_LEFT,
				new Border());
		leftCellStyle = leftCellStyle.deriveHAlign(CellStyle.CENTER);
		Object idLeftCellStyle = tb.addStyle(leftCellStyle);

		group = (RowsGroup) rm.getRowModel().getRootGroup().addGroup(Group.ROW_DETAIL);
		row = rm.getRowModel().addRow(group, 0);

		cell = rm.createReportCell(row, CUST_NO);
		cell.setValue(toFieldExpr("custNo"));
		cell.setStyleId(idLeftCellStyle);

		cell = rm.createReportCell(row, CUSTOMER);
		cell.setValue(toFieldExpr("customer"));
		cell.setStyleId(idCellStyle);

		cell = rm.createReportCell(row, CONTACT);
		cell.setValue(toFieldExpr("contact"));
		cell.setStyleId(idCellStyle);

		cell = rm.createReportCell(row, PHONE);
		cell.setValue(toFieldExpr("phone"));
		cell.setStyleId(idCellStyle);

		cell = rm.createReportCell(row, ADDRESS);
		cell.setValue(toFieldExpr("address"));
		cell.setStyleId(idCellStyle);

		cell = rm.createReportCell(row, CITY);
		cell.setValue(toFieldExpr("city"));
		cell.setStyleId(idCellStyle);

		cell = rm.createReportCell(row, COUNTRY);
		cell.setValue(toFieldExpr("country"));
		cell.setStyleId(idCellStyle);

		cell = rm.createReportCell(row, POSTAL_CODE);
		cell.setValue(toFieldExpr("postCode"));
		cell.setStyleId(idCellStyle);

		group = (RowsGroup) rm.getRowModel().getRootGroup().addGroup(Group.ROW_FOOTER);
		row = rm.getRowModel().addRow(group, -1);

		cell = rm.createReportCell(row, CUST_NO);
		cell.setColSpan(7);
		CellStyle footerStyle = tb.getStyles(null).deriveBorder(
				Border.LINE_TOP, new Border());
		cell.setStyleId(tb.addStyle(footerStyle));
		
		//Save to the template for use in the future
		tb.save(new File("GenerateReport.jdbr"));
		
		//Add iterable dataset
		tb.addReportDataSet(CUSTOMERS, customers);

		ReportEditor editor = new ReportEditor();
		ReportBook rb = tb.createReportBook(editor);
		editor.setReportBook(rb);
		editor.setVisible(true);
	}

	private static String toFieldExpr(String field) {
		return "${" + CUSTOMERS + "." + field + "}";
	}
}
