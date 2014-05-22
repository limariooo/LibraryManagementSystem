import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;


public class CheckIn {
	static Connection conn = null;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object[] Check(String bookID, String cardNo, String name) {
		try {			
			// Create a connection to the local MySQL server, with the NO database selected.
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");

			// Create a SQL statement object and execute the query.
			Statement stmt = conn.createStatement();

			// Set the current database, if not already set in the getConnection
			stmt.execute("use Library;");

			// Execute SQL Query to display books borrowed
			String query = new String("SELECT loan_id, book_id, branch_id, BOOK_LOANS.card_no, date_out, due_date, date_in, fname, lname "
					+ "FROM BOOK_LOANS, BORROWER "
					+ "WHERE "
					+ "book_id LIKE '%" + bookID + "%'"
					+ " AND "
					+ "BOOK_LOANS.card_no LIKE '%" + cardNo + "%'"
					+ " AND "
					+ "BOOK_LOANS.card_no = BORROWER.card_no"
					+ " AND "
					+ "lname LIKE '%" + name + "%';");

			ResultSet rs = stmt.executeQuery(query);
			
			ResultSetMetaData md = rs.getMetaData();
			int columns = md.getColumnCount();
			
			Vector columnNames = new Vector();
			Vector data = new Vector();
			
			// Add the Column Names
			for(int i=1;i<=columns;i++) {
				columnNames.addElement(md.getColumnName(i));
			}

			// Add the data
			while (rs.next()) {
				Vector row = new Vector();

				// Add data from query1
				for(int i=1;i<=columns;i++) {
					row.addElement(rs.getObject(i));
				}
				
				// Add row to data
				data.addElement(row);
			}

			// Always close the connection.
			conn.close();
			rs.close();
			return new Object[]{data, columnNames};
		}
		catch(SQLException ex) {
			System.out.println(ex.getMessage());
			return null;
		}
	}
	
	public static String Update(String loanID, String bookID, String branchID, String cardNo) {
		try {
			// Create a connection to the local MySQL server, with the NO database selected.
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");

			// Create a SQL statement object and execute the query.
			Statement stmt1 = conn.createStatement();
			Statement stmt2 = conn.createStatement();
			
			// Set the current database, if not already set in the getConnection
			stmt1.execute("use Library;");
			stmt2.execute("use Library;");

			// Get dateIn
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			String dateIn = sdf.format(cal.getTime());

			// Execute SQL Query to update date checked in
			String query1 = new String("UPDATE BOOK_LOANS "
					+ "SET date_in = '" + dateIn + "' "
					+ "WHERE "
					+ "loan_id = " + loanID
					+ " AND "
					+ "book_id = " + bookID
					+ " AND "
					+ "branch_id = " + branchID
					+ " AND "
					+ "BOOK_LOANS.card_no = " + cardNo
					+ " AND "
					+ "BOOK_LOANS.date_in IS null;");
			stmt1.executeUpdate(query1);
			
			// assuming fine amount is paid during Check-In
			String query2 = new String("UPDATE FINES "
					+ "SET paid = 1 "
					+ "WHERE "
					+ "loan_id = " + loanID);
			stmt2.executeUpdate(query2);
			
			// Always close the connection.
			conn.close();
			return "Check In Successful";
		}
		catch(SQLException ex) {
			return ex.getMessage();
		}
	}
}