import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Vector;


public class Fines {

	static Connection conn = null;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object[] Display(String cardNo) {
		
		try {
			// Create a connection to the local MySQL server, with the NO database selected.
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");

			// Create a SQL statement object and execute the query.
			Statement stmt = conn.createStatement();

			// Set the current database, if not already set in the getConnection
			stmt.execute("use Library;");

			// SEARCH SQL query
			String query = new String(
					"SELECT BORROWER.card_no, SUM(FINES.fine_amt) AS `total_fine(in $)`, "
					+ "SUM(CASE WHEN FINES.paid = 0 THEN FINES.fine_amt ELSE 0 END) AS `total_due(in $)`, "
					+ "SUM(CASE WHEN FINES.paid = 1 THEN FINES.fine_amt ELSE 0 END) AS `total_paid(in $)` "
					+ "FROM BORROWER, FINES, BOOK_LOANS "
					+ "WHERE "
					// match the cardNo
					+ "BORROWER.card_no LIKE \"%" + cardNo + "%\""
					+ " AND "
					+ "BOOK_LOANS.card_no = BORROWER.card_no"
					+ " AND "
					+ "BOOK_LOANS.loan_id = FINES.loan_id "
					+ "GROUP BY BORROWER.card_no;");
						
			// Execute SQL Query
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
			
			// Always close the result set and connection
			rs.close();
			conn.close();
			
			return new Object[]{data, columnNames};
		}
		catch(SQLException ex) {
			System.out.println(ex.getMessage());
			return null;
		}
	}

	public static String Update() {
		
		try {			
			// Create a connection to the local MySQL server, with the NO database selected.
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");

			// Create a SQL statement object and execute the query.
			Statement stmt1 = conn.createStatement();
			Statement stmt2 = conn.createStatement();

			// Set the current database, if not already set in the getConnection
			stmt1.execute("use Library;");
			stmt2.execute("use Library;");

			// SQL query to update fine amount for checked in books
			String query1 = new String("update FINES, BOOK_LOANS "
					+ "set fine_amt = (DATEDIFF(DATE(date_in), DATE(due_date)) * 0.25) "
					+ "where DATE(date_in) > DATE(due_date)"
					+ " AND "
					+ "date_in IS NOT NULL"
					+ " AND "
					+ "FINES.loan_id = BOOK_LOANS.loan_id");
			
			stmt1.executeUpdate(query1);

			// SQL query to update fine amount for books still on loan
			String query2 = new String("update FINES, BOOK_LOANS "
					+ "set fine_amt = (DATEDIFF(DATE(NOW()), DATE(due_date)) * 0.25) "
					+ "where DATE(NOW()) > DATE(due_date)"
					+ " AND "
					+ "date_in IS NULL"
					+ " AND "
					+ "FINES.loan_id = BOOK_LOANS.loan_id");
			
			stmt2.executeUpdate(query2);

			// Always close the connection.
			conn.close();
			return "Success";
		}
		catch(SQLException ex) {
			return ex.getMessage();
		}
	}

}
