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
import javax.swing.table.DefaultTableModel;


@SuppressWarnings("serial")
public class FinesGui extends JPanel {
	
	private JLabel cardLabel;
	private JTextField cardField;
	private JButton searchButton;
	private JButton updateButton;
	private JTable table;
	private DefaultTableModel tableModel = new DefaultTableModel();
	
	public FinesGui() throws HeadlessException {
			
		// create a label for CARD NUMBER
		cardLabel = new JLabel();
		cardLabel.setVisible(true);
		cardLabel.setText("Card No:");

		// create a field for CARD NUMBER
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
		
		// create a button to Check-In
		updateButton = new JButton("Update");
		updateButton.setVisible(true);
		// add an ActionListener to the button
		updateButton.addActionListener(new ActionListener() {
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
		
		inputPanel.add(cardLabel);
		inputPanel.add(cardField);
		
		inputPanel.add(searchButton);
		inputPanel.setVisible(true);

		JPanel tablePanel = new JPanel(new BorderLayout());
		table = new JTable(tableModel);
		tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

		// set layout to the panel
		setLayout(new BorderLayout());
		add(inputPanel, BorderLayout.PAGE_START);
		add(tablePanel, BorderLayout.CENTER);
		add(updateButton, BorderLayout.PAGE_END);
		setPreferredSize(new Dimension(900, 600));
	}
	
	@SuppressWarnings("rawtypes")
	private void loadData() {
		
		// parse the user input
		String cardNo;
		
		cardNo = cardField.getText();
		
		// get the search results
		Object[] obj = Fines.Display(cardNo);
		tableModel.setDataVector((Vector)obj[0], (Vector)obj[1]);
	}

	private void updateData() {
		
        String str = Fines.Update();
		JOptionPane.showMessageDialog(null, str);
		
		// refresh the data
		loadData();
	}
}