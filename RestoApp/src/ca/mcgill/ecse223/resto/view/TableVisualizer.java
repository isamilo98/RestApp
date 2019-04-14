package ca.mcgill.ecse223.resto.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

import javax.swing.JPanel;

import ca.mcgill.ecse223.resto.model.Table;

public class TableVisualizer extends JPanel {
	private static final long serialVersionUID = 8625130496966285981L;
	HashMap<Integer, Table> tables;
	HashMap<Rectangle2D, Table> guiTables;
	Table selectedTable;
	
	public void addTables(HashMap<Integer, Table> tables) {
		this.tables = tables;
		guiTables = new HashMap<Rectangle2D, Table>();
		selectedTable = null;
		repaint();
	}
	
	private void doDrawing(Graphics g) {
		if (tables != null) {
			int numberOfTables = tables.size();
			Graphics2D g2d = (Graphics2D) g.create();
			guiTables.clear();
			for(int i = 0; i < numberOfTables; i++) {
				Table table = tables.get(i);
				int tableX = table.getX() - table.getWidth()/2;
				int tableY = table.getY() - table.getLength()/2;
				int tableWidth = table.getWidth() + table.getWidth()/2;
				int tableHeight = table.getLength() + table.getLength()/2;
				if (table.hasReservations()) {
					g2d.setColor(Color.RED);
				} else {
					g2d.setColor(Color.BLACK);
				}
				g2d.drawRect(tableX, tableY, tableWidth, tableHeight);
				g2d.drawString(Integer.toString(table.getNumber()) + ": " + table.getStatusFullName(), tableX, tableY-1);
				g2d.drawString("Seats: " + table.numberOfCurrentSeats(), tableX, tableY+tableHeight+11);
			}
			
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);
	}
}
