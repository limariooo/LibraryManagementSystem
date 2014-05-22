import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;


@SuppressWarnings("serial")
public class BorrowerGui extends JPanel {
	
	private JButton addButton;
	private JLabel fnameLabel, lnameLabel, addLabel, cityLabel, stateLabel, phLabel;
	private JTextField fnameField, lnameField, addField, cityField, stateField, phField;
	
	public BorrowerGui() throws HeadlessException {

		// create a button to Add Member
		addButton = new JButton("Add Member");
		addButton.setVisible(true);
		// add an ActionListener to the button
		addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                    	// update borrower table
                        updateTable();
                		repaint();
                		validate();
                        return null;
                    }
                }.execute();
            }
        });
		
		// create a label for FIRST NAME
		fnameLabel = new JLabel();
		fnameLabel.setVisible(true);
		fnameLabel.setText("First Name:");

		// create a field for FIRST NAME
		fnameField = new JTextField();
		fnameField.setVisible(true);
		fnameField.setPreferredSize(new Dimension(250, 30));

		// create a label for LAST NAME
		lnameLabel = new JLabel();
		lnameLabel.setVisible(true);
		lnameLabel.setText("Last Name:");

		// create a field for LAST NAME
		lnameField = new JTextField();
		lnameField.setVisible(true);
		lnameField.setPreferredSize(new Dimension(250, 30));

		// create a label for ADDRESS
		addLabel = new JLabel();
		addLabel.setVisible(true);
		addLabel.setText("Address:");

		// create a field for ADDRESS
		addField = new JTextField();
		addField.setVisible(true);
		addField.setPreferredSize(new Dimension(250, 30));

		// create a label for CITY
		cityLabel = new JLabel();
		cityLabel.setVisible(true);
		cityLabel.setText("City:");

		// create a field for CITY
		cityField = new JTextField();
		cityField.setVisible(true);
		cityField.setPreferredSize(new Dimension(250, 30));

		// create a label for STATE
		stateLabel = new JLabel();
		stateLabel.setVisible(true);
		stateLabel.setText("State:");

		// create a field for STATE
		stateField = new JTextField();
		stateField.setVisible(true);
		stateField.setPreferredSize(new Dimension(250, 30));

		// create a label for PHONE
		phLabel = new JLabel();
		phLabel.setVisible(true);
		phLabel.setText("Phone:");

		// create a field for PHONE
		phField = new JTextField();
		phField.setVisible(true);
		phField.setPreferredSize(new Dimension(250, 30));

		// add the above to a panel
		JPanel inputPanel = new JPanel(new SpringLayout());
		
		inputPanel.add(fnameLabel);
		inputPanel.add(fnameField);
		
		inputPanel.add(lnameLabel);
		inputPanel.add(lnameField);
		
		inputPanel.add(addLabel);
		inputPanel.add(addField);
		
		inputPanel.add(cityLabel);
		inputPanel.add(cityField);
		
		inputPanel.add(stateLabel);
		inputPanel.add(stateField);
		
		inputPanel.add(phLabel);
		inputPanel.add(phField);
		
		// lay out the panel.
		SpringUtilities.makeCompactGrid(inputPanel,
		                                6, 2, //rows, cols
		                                6, 6,        //initX, initY
		                                6, 6);       //xPad, yPad
		inputPanel.setVisible(true);
		inputPanel.setOpaque(true);

		// layout the components
		setLayout(new BorderLayout());
		add(inputPanel, BorderLayout.PAGE_START);
		add(addButton, BorderLayout.CENTER);
		setPreferredSize(new Dimension(400, 300));
	}
	
	private void updateTable() {
		
		String fname = fnameField.getText();
		if(fname.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please enter First Name");
			return;
		}
		String lname = lnameField.getText();
		if(lname.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please enter Last Name");
			return;
		}
		String address = addField.getText();
		if(address.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please enter Address");
			return;
		}
		String city = cityField.getText();
		if(city.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please enter City");
			return;
		}
		String state = stateField.getText();
		if(state.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please enter State");
			return;
		}
		String phone = phField.getText();
		if(phone.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please enter Phone Number");
			return;
		}
		
		// add the new member to BORROWER table
		String str = Borrower.AddMember(fname, lname, address, city, state, phone);
		JOptionPane.showMessageDialog(null, str);
	}
}