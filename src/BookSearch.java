import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;


public class BookSearch {
	static Connection conn = null;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object[] Search(String bookID, String title, String authorName) {
		
		try {
			// Create a connection to the local MySQL server, with the NO database selected.
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");

			// Create a SQL statement object and execute the query.
			Statement stmt1 = conn.createStatement();
			Statement stmt2 = conn.createStatement();

			// Set the current database, if not already set in the getConnection
			stmt1.execute("use Library;");
			stmt2.execute("use Library;");

			// BOOK AVAILABILITY SEARCH SQL query
			String query1 = new String(
					"SELECT BOOK.book_id, title, author_name, LIBRARY_BRANCH.branch_id, BOOK_COPIES.no_of_copies "
					+ "FROM BOOK, BOOK_AUTHORS, LIBRARY_BRANCH, BOOK_COPIES "
					+ "WHERE "
					// match the bookID
					+ "BOOK.book_id LIKE \"%" + bookID + "%\""
					+ " AND "
					// match the title
					+ "BOOK.title LIKE \"%" + title + "%\""
					+ " AND "
					// match the authorName
					+ "BOOK_AUTHORS.author_name LIKE \"%" + authorName + "%\""
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
				title = rs1.getString("title");
				authorName = rs1.getString("author_name");
				int branchID = rs1.getInt("branch_id");
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
}