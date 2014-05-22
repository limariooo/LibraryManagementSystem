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


public class CheckOut {
	static Connection conn = null;
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object[] Search(String bookID, String branchID) {
		
		try {
			// Create a connection to the local MySQL server, with the NO database selected.
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");

			// Create a SQL statement object and execute the query.
			Statement stmt1 = conn.createStatement();
			Statement stmt2 = conn.createStatement();

			// Set the current database, if not already set in the getConnection
			stmt1.execute("use Library;");
			stmt2.execute("use Library;");

			// SEARCH SQL query
			String query1 = new String(
					"SELECT BOOK.book_id, title, author_name, LIBRARY_BRANCH.branch_id, BOOK_COPIES.no_of_copies "
					+ "FROM BOOK, BOOK_AUTHORS, LIBRARY_BRANCH, BOOK_COPIES "
					+ "WHERE "
					// match the bookID
					+ "BOOK.book_id LIKE \"%" + bookID + "%\""
					+ " AND "
					// match the branchID
					+ "LIBRARY_BRANCH.branch_id LIKE \"%" + branchID + "%\""
					+ " AND "
					+ "BOOK.book_id = BOOK_AUTHORS.book_id"
					+ " AND "
					+ "BOOK.book_id = BOOK_COPIES.book_id"
					+ " AND "
					+ "BOOK_COPIES.branch_id = LIBRARY_BRANCH.branch_id;");
			
			// Execute SQL Query
			ResultSet rs1 = stmt1.executeQuery(query1);
			ResultSetMetaData md1 = rs1.getMetaData();
			int columns = md1.getColumnCount();
			
			Vector columnNames = new Vector();
			Vector data = new Vector();
			
			// Add the Column Names
			for(int i=1;i<=columns;i++) {
				columnNames.addElement(md1.getColumnName(i));
			}
			columnNames.add("Available Copies");
			
			// Add the data
			while (rs1.next()) {

				Vector row = new Vector();

				// Add data from query1
				for(int i=1;i<=columns;i++) {
					row.addElement(rs1.getObject(i));
				}

				// Populate field variables
				bookID = rs1.getString("book_id");
				branchID = rs1.getString("branch_id");
				int numCopies = rs1.getInt("no_of_copies");

				// Get the number of checked out copies
				String query2 = new String(
						"SELECT COUNT(*) "
						+ "FROM BOOK_LOANS, BOOK, LIBRARY_BRANCH "
						+ "WHERE "
						+ "BOOK.book_id = " + bookID
						+ " AND "
						+ "LIBRARY_BRANCH.branch_id = " + branchID
						+ " AND "
						+ "BOOK.book_id = BOOK_LOANS.book_id"
						+ " AND "
						+ "BOOK_LOANS.branch_id = LIBRARY_BRANCH.branch_id"
						+ " AND "
						+ "BOOK_LOANS.date_in IS NULL;");
				
				// Execute SQL Query
				ResultSet rs2 = stmt2.executeQuery(query2);
				int availableCopies = numCopies;
				while(rs2.next()) {
					availableCopies = numCopies - rs2.getInt("COUNT(*)");
				}
				rs2.close();
				
				// Add data from query2
				row.add(availableCopies);

				// Add row to data
				data.addElement(row);
			}

			// Always close the result set and connection
			rs1.close();
			conn.close();
			
			return new Object[]{data, columnNames};
		}
		catch(SQLException ex) {
			System.out.println(ex.getMessage());
			return null;
		}
	}

	public static String Update(String bookID, String branchID, String cardNO) {
		
		try {			
			// Create a connection to the local MySQL server, with the NO database selected.
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");

			// Create a SQL statement object and execute the query.
			Statement stmt1 = conn.createStatement();
			Statement stmt2 = conn.createStatement();
			Statement stmt3 = conn.createStatement();
			Statement stmt4 = conn.createStatement();
			Statement stmt5 = conn.createStatement();
			Statement stmt6 = conn.createStatement();

			// Set the current database, if not already set in the getConnection
			stmt1.execute("use Library;");
			stmt2.execute("use Library;");
			stmt3.execute("use Library;");
			stmt4.execute("use Library;");
			stmt5.execute("use Library;");
			stmt6.execute("use Library;");

			// Get dateOut and dueDate
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			String dateOut = sdf.format(cal.getTime());
			cal.add(Calendar.DATE, 14);
			String dueDate = sdf.format(cal.getTime());
			
			// Execute SQL Query to restrict a maximum of 3 book loans
			String query1 = new String("SELECT COUNT(*) "
					+ "FROM BOOK_LOANS "
					+ "WHERE card_no = " + cardNO + ";");

			ResultSet rs1 = stmt1.executeQuery(query1);
			int numBorrowed = 0;
			if(rs1.next()) {
				numBorrowed = rs1.getInt("COUNT(*)");
			}
			if(numBorrowed == 3) {
				rs1.close();
				return "Cannot borrow more than 3 books at a time";
			}
			rs1.close();
			
			// Execute SQL Query to ensure books are available
			String query2 = new String(
					"SELECT no_of_copies "
					+ "FROM BOOK_COPIES "
					+ "WHERE "
					// match the bookID
					+ "BOOK_COPIES.book_id LIKE '%" + bookID + "%'"
					+ " AND "
					// match the branchID
					+ "BOOK_COPIES.branch_id LIKE '%" + branchID + "%';");
			ResultSet rs2 = stmt2.executeQuery(query2);
			int numCopies = 0;
			if(rs2.next()) {
				numCopies = rs2.getInt("no_of_copies");
			}
			rs2.close();
			
			// Get the number of checked out copies
			String query3 = new String(
					"SELECT COUNT(*) "
					+ "FROM BOOK_LOANS, BOOK, LIBRARY_BRANCH "
					+ "WHERE "
					+ "BOOK.book_id LIKE '%" + bookID + "%'"
					+ " AND "
					+ "LIBRARY_BRANCH.branch_id LIKE '%" + branchID + "%'"
					+ " AND "
					+ "BOOK.book_id = BOOK_LOANS.book_id"
					+ " AND "
					+ "BOOK_LOANS.branch_id = LIBRARY_BRANCH.branch_id"
					+ " AND "
					+ "BOOK_LOANS.date_in IS NULL;");
			
			// Execute SQL Query
			ResultSet rs3 = stmt3.executeQuery(query3);
			int availableCopies = numCopies;
			while(rs3.next()) {
				availableCopies = numCopies - rs3.getInt("COUNT(*)");
			}
			rs3.close();
			
			if(availableCopies == 0) {
				return "The book with ID " + bookID + " is not available in branch ID " + branchID;
			}

			String query4 = new String(
					"SELECT MAX(loan_id) "
					+ "FROM BOOK_LOANS"
					);
			
			ResultSet rs4 = stmt4.executeQuery(query4);
			int loanID = 0;
			if(rs4.next()) {
				loanID = rs4.getInt("MAX(loan_id)") + 1;
			}
			
			// BOOK CHECKOUT SQL query
			String query5 = new String(
					"INSERT INTO BOOK_LOANS VALUES"
					+ "(" + loanID + ", "
					+ bookID + ", "
					+ branchID + ", "
					+ cardNO + ", '"
					+ dateOut + "', '"
					+ dueDate + "', "
					+ "NULL);");
			
			// Execute SQL Query
			stmt5.executeUpdate(query5);

			// BOOK CHECKOUT SQL query
			String query6 = new String(
					"INSERT INTO FINES(loan_id) VALUES(" + loanID + ")");
			
			// Execute SQL Query
			stmt6.executeUpdate(query6);
						
			// Always close the connection.
			conn.close();
			return "Success. Please return the book by " + dueDate;
		}
		catch(SQLException ex) {
			return ex.getMessage();
		}
	}
}