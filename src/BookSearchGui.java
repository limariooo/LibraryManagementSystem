import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;


@SuppressWarnings("serial")
public class BookSearchGui extends JPanel {
	
	private JLabel idLabel, titleLabel, nameLabel;
	private JTextField idField, titleField, nameField;
	private JButton searchButton;
	private JTable table;
	private DefaultTableModel tableModel = new DefaultTableModel();
	
	public BookSearchGui() throws HeadlessException {

		// create a label for BOOK ID
		idLabel = new JLabel();
		idLabel.setVisible(true);
		idLabel.setText("Book ID:");

		// create a field for BOOK ID
		idField = new JTextField();
		idField.setVisible(true);
		idField.setPreferredSize(new Dimension(150, 20));
		
		// create a label for BOOK TITLE
		titleLabel = new JLabel();
		titleLabel.setVisible(true);
		titleLabel.setText("Book Title:");

		// create a field for BOOK TITLE
		titleField = new JTextField();
		titleField.setVisible(true);
		titleField.setPreferredSize(new Dimension(150, 20));
		
		// create a label for AUTHOR NAME
		nameLabel = new JLabel();
		nameLabel.setVisible(true);
		nameLabel.setText("Author Name:");

		// create a field for AUTHOR NAME
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
		
		// add the above to a panel
		JPanel inputPanel = new JPanel();
		inputPanel.add(idLabel);
		inputPanel.add(idField);
		
		inputPanel.add(titleLabel);
		inputPanel.add(titleField);
		
		inputPanel.add(nameLabel);
		inputPanel.add(nameField);
		
		inputPanel.add(searchButton);
		inputPanel.setVisible(true);

		JPanel tablePanel = new JPanel(new BorderLayout());
		table = new JTable(tableModel);
		tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
		
		// set layout to the panel
		setLayout(new BorderLayout());
		add(inputPanel, BorderLayout.PAGE_START);
		add(tablePanel, BorderLayout.CENTER);
		setPreferredSize(new Dimension(900, 600));
	}
	
	@SuppressWarnings("rawtypes")
	private void loadData() {

		// parse the user input
		String bookID, title, authorName;
		
		bookID = idField.getText();
		title = titleField.getText();
		authorName = nameField.getText();
		
		// get the search results
		Object[] obj = BookSearch.Search(bookID, title, authorName);
		tableModel.setDataVector((Vector)obj[0], (Vector)obj[1]);
	}
}
