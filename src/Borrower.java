import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Borrower {
	static Connection conn = null;

	public static String AddMember(String fname, String lname, String address, String city, String state, String phone) {
		try {			
			// Create a connection to the local MySQL server, with the NO database selected.
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");

			// Create a SQL statement object and execute the query.
			Statement stmt1 = conn.createStatement();
			Statement stmt2 = conn.createStatement();
			Statement stmt3 = conn.createStatement();

			// Set the current database, if not already set in the getConnection
			stmt1.execute("use Library;");
			stmt2.execute("use Library;");
			stmt3.execute("use Library;");

			// Check duplicate entry SQL Query
			String query1 = new String(
					"SELECT COUNT(*) "
					+ "FROM BORROWER "
					+ "WHERE "
					+ "fname = '" + fname + "'"
					+ " AND "
					+ "lname = '" + lname + "'"
					+ " AND "
					+ "address = '" + address + "';");
			
			ResultSet rs1 = stmt1.executeQuery(query1);
			if(rs1.next()) {
				int count = rs1.getInt("COUNT(*)");
				if(count > 0) {
					rs1.close();
					return "Duplicate entry. User already exists";
				}
			}
			rs1.close();
			
			String query2 = new String(
					"SELECT MAX(card_no) "
					+ "FROM BORROWER"
					);
			
			ResultSet rs2 = stmt2.executeQuery(query2);
			int newCardID = 0;
			if(rs2.next()) {
				newCardID = rs2.getInt("MAX(card_no)") + 1;
			}
			
			// BORROWER UPDATE SQL query
			String query3 = new String(
					"INSERT INTO BORROWER VALUES"
					+ "(" + newCardID + ", "
					+ "'" + fname + "', '"
					+ lname + "', '"
					+ address + "', '"
					+ city + "', '"
					+ state + "', '"
					+ phone + "');");
			
			// Execute SQL Query
			stmt3.executeUpdate(query3);
			// Always close the connection.
			conn.close();
			return "User Succesfully added";
		}
		catch(SQLException ex) {
			System.out.println(ex.getMessage());
			return ex.getMessage();
		}
	}
}
