import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;


public class Main {
	
	public static void main(String[] args) {

		// initialize a TabbedPane to display all the components
		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setVisible(true);
		
		// BOOK AVAILABILITY SEARCH
		tabbedPane.addTab("Book Search", new BookSearchGui());

		// CHECK-OUT
		tabbedPane.addTab("Check Out", new CheckOutGui());

		// CHECK-IN
		tabbedPane.addTab("Check In", new CheckInGui());

		// BORROWER MANAGEMENT
		tabbedPane.addTab("Add Borrower", new BorrowerGui());

		// FINES
		tabbedPane.addTab("Check Fines", new FinesGui());

		// add the tabbedPane to a JFrame
		final JFrame frame = new JFrame("Library Management System");
		frame.add(tabbedPane);
		frame.setVisible(true);
		frame.setSize(new Dimension(900, 600));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
	}
}