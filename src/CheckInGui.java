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
public class CheckInGui extends JPanel {
	
	private String loanID = new String();
	private String bookID = new String();
	private String branchID = new String();
	private String cardNo = new String();
	private JLabel idLabel, cardLabel, nameLabel;
	private JTextField idField, cardField, nameField;
	private JButton searchButton;
	private JButton checkInButton;
	private JTable table;
	private DefaultTableModel tableModel = new DefaultTableModel();
	
	public CheckInGui() throws HeadlessException {
			
		// create a label for BOOK ID
		idLabel = new JLabel();
		idLabel.setVisible(true);
		idLabel.setText("Book ID:");

		// create a field for BOOK ID
		idField = new JTextField();
		idField.setVisible(true);
		idField.setPreferredSize(new Dimension(150, 20));
		
		// create a label for CARD NUMBER
		cardLabel = new JLabel();
		cardLabel.setVisible(true);
		cardLabel.setText("Card No:");

		// create a field for CARD NUMBER
		cardField = new JTextField();
		cardField.setVisible(true);
		cardField.setPreferredSize(new Dimension(150, 20));
		
		// create a label for BORROWER NAME
		nameLabel = new JLabel();
		nameLabel.setVisible(true);
		nameLabel.setText("Borrower Name:");

		// create a field for BORROWER NAME
		nameField = new JTextField();
		nameField.setVisible(true);
		nameField.setPreferredSize(new Dimension(150, 20));
		
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
		
		// create a button to Check-In
		checkInButton = new JButton("Check In");
		checkInButton.setVisible(true);
		// add an ActionListener to the button
		checkInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
        	            
                    	// update the BOOK_LOANS table after clicking Check-In
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
		
		inputPanel.add(idLabel);
		inputPanel.add(idField);
		
		inputPanel.add(cardLabel);
		inputPanel.add(cardField);
		
		inputPanel.add(nameLabel);
		inputPanel.add(nameField);
		
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
	            int loanIDColumn = table.convertColumnIndexToModel(0);
	            int bookIDColumn = table.convertColumnIndexToModel(1);
	            int branchIDColumn = table.convertColumnIndexToModel(2);
	            int cardNoColumn = table.convertColumnIndexToModel(3);

	            // get the values from the selected tuple
	            loanID = tableModel.getValueAt(selectedRow, loanIDColumn).toString();
	            bookID = tableModel.getValueAt(selectedRow, bookIDColumn).toString();
	            branchID = tableModel.getValueAt(selectedRow, branchIDColumn).toString();
	            cardNo = tableModel.getValueAt(selectedRow, cardNoColumn).toString();
	        }
	    });
		
		// set layout to the panel
		setLayout(new BorderLayout());
		add(inputPanel, BorderLayout.PAGE_START);
		add(tablePanel, BorderLayout.CENTER);
		add(checkInButton, BorderLayout.PAGE_END);
		setPreferredSize(new Dimension(900, 600));
	}
	
	@SuppressWarnings("rawtypes")
	private void loadData() {

		// parse the user input
		String bookID, cardNo, name;
		
		bookID = idField.getText();
		cardNo = cardField.getText();
		name = nameField.getText();
		
		// get the search results
		Object[] obj = CheckIn.Check(bookID, cardNo, name);
		tableModel.setDataVector((Vector)obj[0], (Vector)obj[1]);
	}

	private void updateData() {

		if(loanID.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please select a book to Check In from the results");
			return;
		}

		if(bookID.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please select a book to Check In from the results");
			return;
		}

		if(branchID.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please select a book to Check In from the results");
			return;
		}

		if(cardNo.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please select a book to Check In from the results");
			return;
		}

        String str = CheckIn.Update(loanID, bookID, branchID, cardNo);
		JOptionPane.showMessageDialog(null, str);
		
		// refresh the data
		loadData();
	}
}
