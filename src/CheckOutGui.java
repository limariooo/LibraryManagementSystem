import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;


@SuppressWarnings("serial")
public class CheckOutGui extends JPanel {
	
	private String bookID = new String();
	private String branchID = new String();
	private String cardNo = new String();
	private JLabel bookIDLabel, branchIDLabel, cardLabel;
	private JTextField bookIDField, branchIDField, cardField;
	private JButton checkOutButton;
	private JButton searchButton;
	private JTable table;
	private DefaultTableModel tableModel = new DefaultTableModel();

	
	public CheckOutGui() throws HeadlessException {
			
		// create a label for BOOK ID
		bookIDLabel = new JLabel();
		bookIDLabel.setVisible(true);
		bookIDLabel.setText("Book ID:");

		// create a field for BOOK ID
		bookIDField = new JTextField();
		bookIDField.setVisible(true);
		bookIDField.setPreferredSize(new Dimension(150, 20));
		
		// create a label for BRANCH ID
		branchIDLabel = new JLabel();
		branchIDLabel.setVisible(true);
		branchIDLabel.setText("Branch ID:");

		// create a field for BRANCH ID
		branchIDField = new JTextField();
		branchIDField.setVisible(true);
		branchIDField.setPreferredSize(new Dimension(150, 20));
		
		// create a label for CARD NO
		cardLabel = new JLabel();
		cardLabel.setVisible(true);
		cardLabel.setText("Card No:");

		// create a field for AUTHOR NAME
		cardField = new JTextField();
		cardField.setVisible(true);
		cardField.setPreferredSize(new Dimension(150, 20));
		
		// create a button to Search
		searchButton = new JButton("Search");
		searchButton.setVisible(true);
		// add an ActionListener to the button
		searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                    	
                    	// display the search results
                        loadData();
                        
                		repaint();
                		validate();
                        return null;
                    }
                }.execute();
            }
        });

		// create a button to Check-Out
		checkOutButton = new JButton("Check-Out");
		checkOutButton.setVisible(true);
		// add an ActionListener to the button
		checkOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                    	
                    	// update BOOK_LOANS table after clicking Check-Out
                        updateData();
                        
                		repaint();
                		validate();
                        return null;
                    }
                }.execute();
            }
        });
		
		// add the above to a panel
		JPanel inputPanel = new JPanel();
		
		inputPanel.add(bookIDLabel);
		inputPanel.add(bookIDField);
		
		inputPanel.add(branchIDLabel);
		inputPanel.add(branchIDField);
		
		inputPanel.add(cardLabel);
		inputPanel.add(cardField);
		
		inputPanel.add(searchButton);
		inputPanel.setVisible(true);

		JPanel tablePanel = new JPanel(new BorderLayout());
		table = new JTable(tableModel);
		tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

		// add a ListSelectionListener to the table
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
	        public void valueChanged(ListSelectionEvent e) {
	            if (e.getValueIsAdjusting()) return;
	            
	            int selectedRow = table.convertRowIndexToModel(table.getSelectedRow());
	            int bookIDColumn = table.convertColumnIndexToModel(0);
	            int branchIDColumn = table.convertColumnIndexToModel(3);

	            // get the values from the selected tuple
	            bookID = tableModel.getValueAt(selectedRow, bookIDColumn).toString();
	            branchID = tableModel.getValueAt(selectedRow, branchIDColumn).toString();
	        }
	    });
		
		// set layout to the panel
		setLayout(new BorderLayout());
		add(inputPanel, BorderLayout.PAGE_START);
		add(tablePanel, BorderLayout.CENTER);
		add(checkOutButton, BorderLayout.PAGE_END);
		setPreferredSize(new Dimension(900, 600));
	}
	
	@SuppressWarnings("rawtypes")
	private void loadData() {

		// parse the user input
		String _bookID, _branchID;
		
		_bookID = bookIDField.getText();
		
		_branchID = branchIDField.getText();
		// get the response
		Object[] obj = CheckOut.Search(_bookID, _branchID);
		tableModel.setDataVector((Vector)obj[0], (Vector)obj[1]);
	}
	
	private void updateData() {
		
		if(bookID.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please select a book to Check Out from the results");
			return;
		}

		if(branchID.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please select a book to Check Out from the results");
			return;
		}

		cardNo = cardField.getText();
		if(cardNo.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please enter a value for Card No");
			return;
		}
		
        String str = CheckOut.Update(bookID, branchID, cardNo);
		JOptionPane.showMessageDialog(null, str);
		
		// refresh the data
		loadData();
	}
}
