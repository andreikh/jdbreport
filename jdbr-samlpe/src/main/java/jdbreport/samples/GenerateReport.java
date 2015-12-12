package jdbreport.samples;

import java.util.ArrayList;
import java.util.List;

import jdbreport.model.Border;
import jdbreport.model.Cell;
import jdbreport.model.CellStyle;
import jdbreport.model.ReportBook;
import jdbreport.model.ReportModel;
import jdbreport.model.TableRow;
import jdbreport.model.print.ReportPage;
import jdbreport.view.ReportEditor;

public class GenerateReport {

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
		 * @param custNo the custNo to set
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
		 * @param customer the customer to set
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
		 * @param contactFirst the contactFirst to set
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
		 * @param contactLast the contactLast to set
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
		 * @param phone the phone to set
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
		 * @param address the address to set
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
		 * @param city the city to set
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
		 * @param postCode the postCode to set
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
		 * @param country the country to set
		 */
		public void setCountry(String country) {
			this.country = country;
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
	
	private static List<Customer>  getData() {
		List<Customer> customers = new ArrayList<>();
		customers.add(new Customer(1001, "Signature Design", "Dale J.", "Little", "(619) 530-2710", "15500 Pacific Heights Blvd.", "San Diego", "USA", "92121"));
		customers.add(new Customer(1002, "Dallas Technologies", "Glen", "Brown", "(214) 960-2233", "P. O. Box 47000", "Dallas", "USA", "75205"));
		customers.add(new Customer(1003, "Buttle, Griffith and Co.", "James", "Buttle", "(617) 488-1864", "2300 Newbury Street Suite 101", "Boston", "USA", "02115"));
		customers.add(new Customer(1004, "Central Bank", "Elizabeth", "Brocket", "61 211 99 88", "66 Lloyd Street", "Manchester", "England", "M2 3LA"));
		customers.add(new Customer(1005, "DT Systems, LTD.", "Tai", "Wu", "(852) 850 43 98", "400 Connaught Road", "Central Hong Kong", "Hong Kong", null));
		customers.add(new Customer(1006, "DataServe International", "Tomas", "Bright", "(613) 229 3323", "2000 Carling Avenue Suite 150", "Ottawa", "Canada", "K1V 9G1"));
		customers.add(new Customer(1007, "Mrs. Beauvais", "", "Mrs. Beauvais", "", "P.O. Box 22743", "Pebble Beach", "USA", "93953"));
		customers.add(new Customer(1008, "Anini Vacation Rentals", "Leilani", "Briggs", "(808) 835-7605", "3320 Lawai Road", "Lihue", "USA", "96766"));
		customers.add(new Customer(1009, "Max", "Max", null, "22 01 23", "1 Emerald Cove", "Turtle Island", "Fiji", null));
		customers.add(new Customer(1010, "MPM Corporation", "Miwako", "Miyamoto", "3 880 77 19", "2-64-7 Sasazuka", "Tokyo", "Japan", "150"));
		customers.add(new Customer(1011, "Dynamic Intelligence Corp", "Victor", "Granges", "01 221 16 50", "Florhofgasse 10", "Zurich", "Switzerland", "8005"));
		customers.add(new Customer(1012, "3D-Pad Corp.", "Michelle", "Roche", "1 43 60 61", "22 Place de la Concorde", "Paris", "France", "75008"));
		customers.add(new Customer(1013, "Lorenzi Export, Ltd.", "Andreas", "Lorenzi", "02 404 6284", "Via Eugenia, 15", "Milan", "Italy", "20124"));
		customers.add(new Customer(1014, "Dyno Consulting", "Greta", "Hessels", "02 500 5940", "Rue Royale 350", "Brussels", "Belgium", "1210"));
		customers.add(new Customer(1015, "GeoTech Inc.", "K.M.", "Neppelenbroek", "(070) 44 91 18", "P.0.Box 702", "Den Haag", "Netherlands", "2514"));
		return customers;
	}
	
	private static final int[] WIDTHS = {50, 130, 90, 100, 140, 75, 80, 60 };

	public static void main(String[] args) {
		List<Customer> customers = getData();
		
		ReportBook rb = new ReportBook();
		rb.setShowGrid(false);
		ReportModel rm = rb.getReportModel(0);
		rm.getReportPage().setOrientation(ReportPage.LANDSCAPE);
		rm.setColumnCount(8);
		rm.setColumnWidths(WIDTHS);
		
		rm.unionCells(0, 0, 0, 7);
		Cell cell = rm.createReportCell(0, 0);
		cell.setValue("Customers");
		CellStyle titleStyle = rb.getStyles(null);
		titleStyle = titleStyle.deriveAutoHeight(true);
		titleStyle = titleStyle.deriveFont(18f);
		titleStyle = titleStyle.deriveFont(CellStyle.BOLD);
		titleStyle = titleStyle.deriveHAlign(CellStyle.CENTER);
		Object idTitleStyle = rb.addStyle(titleStyle);
		cell.setStyleId(idTitleStyle);
		

		CellStyle tableTitleStyle = rb.getStyles(null);
		tableTitleStyle = tableTitleStyle.deriveAutoHeight(true);
		tableTitleStyle =tableTitleStyle.deriveFont(12f);
		tableTitleStyle = tableTitleStyle.deriveFont(CellStyle.BOLD);
		tableTitleStyle = tableTitleStyle.deriveHAlign(CellStyle.CENTER);
		tableTitleStyle = tableTitleStyle.deriveBorder(Border.LINE_TOP, new Border());
		tableTitleStyle = tableTitleStyle.deriveBorder(Border.LINE_RIGHT, new Border());
		Object idTableTitleStyle = rb.addStyle(tableTitleStyle);
		
		CellStyle leftTitleStyle = tableTitleStyle.deriveBorder(Border.LINE_LEFT, new Border());
		Object idLeftTitleStyle = rb.addStyle(leftTitleStyle);

		rm.getRowModel().addRow();
		
		TableRow tableRow = rm.getRowModel().addRow();
		
		cell = tableRow.createCellItem(CUST_NO);
		cell.setValue("Cust.No");
		cell.setStyleId(idLeftTitleStyle);
		
		cell = tableRow.createCellItem(CUSTOMER);
		cell.setValue("Customer");
		cell.setStyleId(idTableTitleStyle);

		cell = tableRow.createCellItem(CONTACT);
		cell.setValue("Contact");
		cell.setStyleId(idTableTitleStyle);
		
		cell = tableRow.createCellItem(PHONE);
		cell.setValue("Phone");
		cell.setStyleId(idTableTitleStyle);
		
		cell = tableRow.createCellItem(ADDRESS);
		cell.setValue("Address");
		cell.setStyleId(idTableTitleStyle);

		cell = tableRow.createCellItem(CITY);
		cell.setValue("City");
		cell.setStyleId(idTableTitleStyle);
		
		cell = tableRow.createCellItem(COUNTRY);
		cell.setValue("Country");
		cell.setStyleId(idTableTitleStyle);
		
		cell = tableRow.createCellItem(POSTAL_CODE);
		cell.setValue("Postal Code");
		cell.setStyleId(idTableTitleStyle);
		

		
		CellStyle cellStyle = tableTitleStyle.deriveFont(CellStyle.PLAIN); 
		cellStyle = cellStyle.deriveFont(10f);
		cellStyle = cellStyle.deriveHAlign(CellStyle.LEFT);
		Object idCellStyle = rb.addStyle(cellStyle);
		
		CellStyle leftCellStyle = cellStyle.deriveBorder(Border.LINE_LEFT, new Border());
		leftCellStyle = leftCellStyle.deriveHAlign(CellStyle.CENTER);
		Object idLeftCellStyle = rb.addStyle(leftCellStyle);
		
		
		for (Customer cust : customers) {
			tableRow = rm.getRowModel().addRow();
			
			cell = tableRow.createCellItem(CUST_NO);
			cell.setValue(cust.getCustNo());
			cell.setStyleId(idLeftCellStyle);
			
			cell = tableRow.createCellItem(CUSTOMER);
			cell.setValue(cust.getCustomer());
			cell.setStyleId(idCellStyle);

			cell = tableRow.createCellItem(CONTACT);
			cell.setValue(
					(cust.getContactFirst() == null ? "" : (cust.getContactFirst() + " "))
					+ (cust.getContactLast() == null ? "" : cust.getContactLast())
			);
			cell.setStyleId(idCellStyle);
			
			cell = tableRow.createCellItem(PHONE);
			cell.setValue(cust.getPhone());
			cell.setStyleId(idCellStyle);
			
			cell = tableRow.createCellItem(ADDRESS);
			cell.setValue(cust.getAddress());
			cell.setStyleId(idCellStyle);

			cell = tableRow.createCellItem(CITY);
			cell.setValue(cust.getCity());
			cell.setStyleId(idCellStyle);
			
			cell = tableRow.createCellItem(COUNTRY);
			cell.setValue(cust.getCountry());
			cell.setStyleId(idCellStyle);
			
			cell = tableRow.createCellItem(POSTAL_CODE);
			cell.setValue(cust.getPostCode());
			cell.setStyleId(idCellStyle);
			
		}
		
		tableRow = rm.getRowModel().addRow();
		cell = tableRow.createCellItem(CUST_NO);
		cell.setColSpan(7);
		CellStyle footerStyle = rb.getStyles(null).deriveBorder(Border.LINE_TOP, new Border());
		cell.setStyleId(rb.addStyle(footerStyle));
		
		ReportEditor editor = new ReportEditor();
		for (int r = 0; r < rm.getRowCount(); r ++) {
			for (int c = 0; c < rm.getColumnCount(); c++) {
				rm.updateRowHeight(editor, r, c);
			}
		}
		editor.setReportBook(rb);
		editor.setVisible(true);
	}

}
