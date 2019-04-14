package ca.mcgill.ecse223.resto.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import ca.mcgill.ecse.btms.controller.BtmsController;
import ca.mcgill.ecse223.resto.controller.InvalidInputException;
import ca.mcgill.ecse223.resto.controller.RestoController;
/*	from jdatepicker.jar library
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;
*/
import ca.mcgill.ecse223.resto.model.Table;

public class RestoPage extends JFrame {
	

	//UI elements
	private JLabel errorMessage;
	//restaurant visualization
	private RestoVisualizer restoVisualizer;
	//create table
	private JTextField addTableNumberTextField;
	private JLabel addTableNumberLabel;
	private JTextField addTableXTextField;
	private JLabel addTableXLabel;
	private JTextField addTableYTextField;
	private JLabel addTableYLabel;
	private JTextField addTableWidthTextField;
	private JLabel addTableWidthLabel;
	private JTextField addTableLengthTextField;
	private JLabel addTableLengthLabel;
	private JTextField addTableNumberOfSeatsTextField;
	private JLabel addTableNumberOfSeatsLabel;
	private JButton addTableButton;
	//update or delete table
	private JComboBox<String> selectTableDropdown;
	private JLabel selectTableDropdownLabel;
	private JTextField updateTableXTextField;
	private JLabel updateTableXLabel;
	private JTextField updateTableYTextField;
	private JLabel updateTableYLabel;
	private JTextField updateTableNumberOfSeatsTextField;
	private JLabel updateTableNumberOfSeatsLabel;
	private JButton updateTableButton;
	private JButton deleteTableButton;
	private JButton moveTableButton;
	private JButton menuButton;
	
	//data elements
	
	// restaurant visualization
	private static final int WIDTH_RESTO_VISUALIZATION = 200;
	private static final int HEIGHT_RESTO_VISUALIZATION = 200;
	//error
	private String error = null;
	//Table
	private HashMap<Integer, Table> tables;
	private Integer selectedTable = -1;

	
	
	// Creates new form RestoPage
	public RestoPage() {
		initComponents();
		refreshData();
	}

	private void initComponents() {
		//elements for restaurant visualization
		restoVisualizer = new RestoVisualizer();
		restoVisualizer.setMinimumSize(new Dimension(WIDTH_RESTO_VISUALIZATION, HEIGHT_RESTO_VISUALIZATION));
		// elements for error message
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);
		// elements for creating table
		addTableNumberTextField = new JTextField();
		addTableNumberLabel = new JLabel();
		addTableXTextField = new JTextField();
		addTableXLabel = new JLabel();
		addTableYTextField = new JTextField();
		addTableYLabel = new JLabel();
		addTableLengthLabel = new JLabel();
		addTableLengthTextField = new JTextField();
		addTableWidthLabel = new JLabel();
		addTableWidthTextField = new JTextField();
		addTableNumberOfSeatsTextField = new JTextField();
		addTableNumberOfSeatsLabel = new JLabel();
		addTableButton = new JButton();
		

		
		selectTableDropdownLabel = new JLabel();
		updateTableXTextField = new JTextField();
		updateTableXLabel = new JLabel();
		updateTableYTextField = new JTextField();
		updateTableYLabel = new JLabel();
		updateTableNumberOfSeatsTextField = new JTextField();
		updateTableNumberOfSeatsLabel = new JLabel();
		updateTableButton = new JButton();
		deleteTableButton = new JButton();
		moveTableButton = new JButton();
		menuButton = new JButton();
		
		//global settings and listeners
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Resto App");
		
		//elements settings and listener for moving table
		moveTableButton.setText("Move table");
		moveTableButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				moveTableButtonActionPerformed(evt);		   			
		}
		 });
	

		
		//elements settings and listener for moving table
		updateTableButton.setText("Update table");
		updateTableButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateTableButtonActionPerformed(evt);
			}
		});

		// settings and listener for createTable()
		addTableNumberLabel.setText("Table number:");
		addTableXLabel.setText("X position:");
		moveTableButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateTableXTextField(evt);
			}
		});
		addTableButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateTableXTextField(evt);
			}
		});
		
		addTableYLabel.setText("Y Position:");
		moveTableButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateTableYTextField(evt);
			}
		});
		addTableButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateTableYTextField(evt);
			}
		});
		
		addTableWidthLabel.setText("Table width:");
		addTableButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addTableWidthTextField(evt);
			}
		});
		
		
		addTableLengthTextField.setText("Table length:");
		addTableButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addTableLengthTextField(evt);
			}
		});
		
		addTableNumberOfSeatsTextField.setText("Number of seats:");
		addTableButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updateTableNumberOfSeatsTextField(evt);
			}
		});
		
		
		addTableButton.setText("Add Table");
		addTableButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addTableButtonActionPerformed(evt);
			}
		});	
		
		//elements for updating or deleting ta	ble
		selectTableDropdown = new JComboBox<String>(new String[0]);
		selectTableDropdown.addActionListener(new java.awt.event.ActionListener() {
			
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JComboBox<String> cb = (JComboBox<String>) evt.getSource();
				selectedTable = cb.getSelectedIndex();
			}
		});
		
		//settings and listener for MenuBLALBla()
		menuButton.setText("Menu");
		
		deleteTableButton.setText("Delete");
		deleteTableButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteTableButtonActionPerformed(evt);
			}
		});
		
		//layout
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
	///////////////////////////////////////////////////////////////////////////	

		RestoAppGUI gui = new RestoAppGUI();
		gui.setVisible(true);
	
	////////////////////////////////////////////////////////////////////////////
		layout.setHorizontalGroup(
			layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
					.addComponent(errorMessage)
					.addGroup(layout.createParallelGroup()
							.addComponent(addTableNumberLabel, 200, 200, 400)
							.addComponent(addTableXLabel)
							.addComponent(addTableYLabel)
							.addComponent(addTableWidthLabel)
							.addComponent(addTableLengthLabel)
							.addComponent(addTableNumberOfSeatsLabel)
							.addComponent(selectTableDropdownLabel)
							.addGroup(layout.createSequentialGroup()
									.addComponent(addTableButton)
									.addComponent(deleteTableButton))
							.addComponent(menuButton))
					.addGroup(layout.createParallelGroup()
							.addComponent(addTableNumberTextField, 200, 200, 400)
							.addComponent(addTableXTextField, 200, 200, 400)
							.addComponent(addTableYTextField, 200, 200, 400)
							.addComponent(addTableWidthTextField)
							.addComponent(addTableLengthTextField, 200, 200, 400)
							.addComponent(addTableNumberOfSeatsTextField)
							.addComponent(selectTableDropdown)
							.addGroup(layout.createSequentialGroup()
									.addComponent(updateTableButton)
									.addComponent(moveTableButton)))
					)
			.addComponent(restoVisualizer)
			);
		
		layout.setVerticalGroup(
			layout.createSequentialGroup()
			.addComponent(errorMessage)
			.addGroup(layout.createParallelGroup()
					.addComponent(addTableNumberLabel)
					.addComponent(addTableNumberTextField))
			.addGroup(layout.createParallelGroup()
					.addComponent(addTableXLabel)
					.addComponent(addTableXTextField))
			.addGroup(layout.createParallelGroup()
					.addComponent(addTableYLabel)
					.addComponent(addTableYTextField))
			.addGroup(layout.createParallelGroup()
					.addComponent(addTableWidthLabel)
					.addComponent(addTableWidthTextField))
			.addGroup(layout.createParallelGroup()
					.addComponent(addTableLengthLabel)
					.addComponent(addTableLengthTextField))
			.addGroup(layout.createParallelGroup()
					.addComponent(addTableNumberOfSeatsLabel)
					.addComponent(addTableNumberOfSeatsTextField))
			.addGroup(layout.createParallelGroup()
					.addComponent(selectTableDropdownLabel)
					.addComponent(selectTableDropdown))
			.addGroup(layout.createParallelGroup()
					.addComponent(addTableButton)
					.addComponent(deleteTableButton)
					.addComponent(updateTableButton)
					.addComponent(moveTableButton)
					)
			.addGroup(layout.createParallelGroup()
					.addComponent(menuButton)
					)
			.addComponent(restoVisualizer)
			);
		

		
		pack();
	}
	
	private void refreshData() {
		//error
		errorMessage.setText(error);
		if(error == null || error.length() == 0) {
			//empty the text fields
			addTableNumberTextField.setText("");
			addTableXTextField.setText("");
			addTableYTextField.setText("");
			addTableNumberOfSeatsTextField.setText("");
			addTableWidthTextField.setText("");
			addTableLengthTextField.setText("");
			updateTableXTextField.setText("");
			updateTableYTextField.setText("");
			updateTableNumberOfSeatsTextField.setText("");
			//update table Dropdown
			tables = new HashMap<Integer, Table>();
			selectTableDropdown.removeAllItems();
			Integer index = 0;
			for (Table table : RestoController.getTables()) {
				tables.put(index, table);
				selectTableDropdown.addItem("#" + table.getNumber());
				index++;
			}
			selectedTable = -1;
			selectTableDropdown.setSelectedIndex(selectedTable);
		}
		pack();
	}
	
	private void addTableButtonActionPerformed(java.awt.event.ActionEvent evt) {
		// clear error message
		error = null;
		
		// call the controller
		try {
			RestoController.createTable(Integer.parseInt(addTableNumberTextField.getText()),
					Integer.parseInt(addTableXTextField.getText()), 
					Integer.parseInt(addTableYTextField.getText()), 
					Integer.parseInt(addTableWidthTextField.getText()), 
					Integer.parseInt(addTableLengthTextField.getText()), 
					Integer.parseInt(addTableNumberOfSeatsTextField.getText()));
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}
		
		// update visuals
		refreshData();
	}
	
	private void deleteTableButtonActionPerformed(java.awt.event.ActionEvent evt) {
		error = "";
		if (selectedTable < 0) {
			error = "Table needs to be selected for deletion!";
		}
		if (error.length() == 0) {
			try {
				RestoController.removeTable(tables.get(selectedTable));
			} catch (InvalidInputException e) {
				error = e.getMessage();
			}
		}
		//update visuals	
		refreshData();
	}
	
	
	private void moveTableButtonActionPerformed(java.awt.event.ActionEvent evt) {
		error = "";
		if (selectedTable < 0) {
			error = "Table needs to be selected to move";
		}
		if (error.length() == 0) {
			try {
				RestoController.moveTable(tables.get(selectedTable));
				RestoController.moveTable(updateTableXTextField.getText());
				RestoController.moveTable(updateTableYTextField.getText());
			} 
			catch (InvalidInputException e) {
				error = e.getMessage();
			}
		}
		refreshData();
	}
		private void updateTableButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
		error = "";
		Table table;
		if (table == null) {		
				error = "Table not found.";				
			}
		
		int tableNumber = 0;
		try {
			tableNumber = Integer.parseInt(addTableNumberTextField.getText());
		}
		catch (NumberFormatException e) {
			error = "Table number needs to be a numerical value";
		}
		int nbOfSeats = 0;
		try {
			nbOfSeats = Integer.parseInt(addTableNumberOfSeatsTextField.getText());
		}
		catch (NumberFormatException e) {
			error = "Invalid number of seats.";
		}
		
		error.trim();
		if (error.length() == 0) {
			try {
				RestoController.updateTable(table, tableNumber, nbOfSeats);
			} catch (InvalidInputException e) {
				error = e.getMessage();
			}
		}

		refreshData();
	}
	
}

