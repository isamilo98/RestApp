package ca.mcgill.ecse223.resto.controller;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import ca.mcgill.ecse223.resto.controller.InvalidInputException;
import ca.mcgill.ecse223.resto.application.RestoApplication;
import ca.mcgill.ecse223.resto.model.Bill;
import ca.mcgill.ecse223.resto.model.Event;
import ca.mcgill.ecse223.resto.model.Menu;
import ca.mcgill.ecse223.resto.model.MenuItem;
import ca.mcgill.ecse223.resto.model.MenuItem.ItemCategory;
import ca.mcgill.ecse223.resto.model.Order;
import ca.mcgill.ecse223.resto.model.OrderItem;
import ca.mcgill.ecse223.resto.model.PricedMenuItem;
import ca.mcgill.ecse223.resto.model.Reservation;
import ca.mcgill.ecse223.resto.model.RestoApp;
import ca.mcgill.ecse223.resto.model.Seat;
import ca.mcgill.ecse223.resto.model.Table;
import ca.mcgill.ecse223.resto.model.Table.Status;

public class RestoController {
	public RestoController() {

	}

	public static List<Event> getEvents() {
		RestoApp r = RestoApplication.getRestoApp();
		try {
			return r.getEvents();
		} catch (Exception e) {
			return null;
		}
	}

	public static void createEvent(String nameOfEvent, String description, Date startDate, Date endDate) throws InvalidInputException {
		String error = "";
		if(nameOfEvent == null || description == null || startDate == null || endDate == null) {
			error = "Missing event information";
			throw new InvalidInputException(error.trim());
		} else if (endDate.getTime() < startDate.getTime()) {
			error = "End date must be after start date.";
			throw new InvalidInputException(error.trim());
		}
		RestoApp r = RestoApplication.getRestoApp();
		new Event(nameOfEvent, description, startDate, endDate, r);
		//r.addEvent(newEvent);
		RestoApplication.save();
	}

	public static void removeEvent(Event event) throws InvalidInputException {
		String error = "";
		if(event == null) {
			error = "Must select event in the table";
			throw new InvalidInputException(error.trim());
		}
		event.delete();
		RestoApplication.save();
	}

	public static void moveTable(Table table, int x, int y) throws InvalidInputException {

		String error = "";
		int length;
		int width;
		if (table == null) {
			error = "Table not found.";
			throw new InvalidInputException(error);
		}

		RestoApp restoApp = RestoApplication.getRestoApp();
		width = table.getWidth();
		length = table.getLength();
		List<Table> currentTables = restoApp.getCurrentTables();
		boolean overlaps;
		for (Table currentTable : currentTables) {
			if (currentTable.doesOverlaps(x, y, width, length) && table.equals(currentTable)) {
				error = "There is already a table in this location";
				throw new InvalidInputException(error.trim());
			}
		}
				table.setX(x);
				table.setY(y);        
				
		RestoApplication.save();
	}

	public static void createTable (int number, int x, int y, int width, int length, int numberOfSeats) throws InvalidInputException {
		String error = "";
		if (x < 0 || y < 0) {
			error = "The position of te table cannot be chararterised by negative x and y variables.";
		}
		if (number <= 0) {
			error = "The number of a table has to be positive.";
		}
		if (width <= 0 || length <= 0) {
			error = "The width and the length has to be positive.";
		}
		if (numberOfSeats <= 0) {
			error = "The number of seats needs to be positive.";
		}

		RestoApp r = RestoApplication.getRestoApp();
		List<Table> currentTables = r.getCurrentTables();

		for (Table currentTable : currentTables) {
			if (currentTable.doesOverlaps(x, y, width, length)) {
				error = "Cannot create table since it overlaps with other, please change the location or the dimensions";
			}
		}

		if (error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}


		Table table;
		try {
			table = new Table(number, x, y, width, length, r);
		}
		catch (RuntimeException e) {
			error = e.getMessage();
			if (error.equals("Cannot create due to duplicate number")) {
				error = "A table with this number already exists. Please use a different number.";
			}
			throw new InvalidInputException(error);
		}

		r.addCurrentTable(table);
		r.addTable(table);

		for (int i = 1 ; i <= numberOfSeats; i++) {
			Seat seat = table.addSeat();
			table.addCurrentSeat(seat);
		}

		try {
			RestoApplication.save();
		}
		catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}


	}

	public static List<Table> getTables() {
		return RestoApplication.getRestoApp().getCurrentTables();
	}

	public static void removeTable(int number) throws InvalidInputException {
		Table foundTable = Table.getWithNumber(number);
		removeTable(foundTable);
	}
	
	public static void removeTable(Table table) throws InvalidInputException {
		String error = "";
		if (table == null) {
			error = "Table not found.";
			throw new InvalidInputException(error.trim());
		}
		RestoApp restoApp = RestoApplication.getRestoApp();
		List<Order> currentOrders = restoApp.getCurrentOrders();
		for (Order order : currentOrders) {
			List<Table> tables = order.getTables();
			boolean inUse = tables.contains(table);
			if (inUse == true) {
				error = "Table in use.";
				throw new InvalidInputException(error.trim());
			}
		}
		restoApp.removeCurrentTable(table);
		RestoApplication.save();
	}

	public static void updateTable(int number, int newNumber, int numberOfSeats) throws InvalidInputException {
		Table foundTable = Table.getWithNumber(number);
		updateTable(foundTable, newNumber, numberOfSeats);
	}

	public static void updateTable(Table table, int newNumber, int numberOfSeats) throws InvalidInputException {
		String error = "";
		if (table == null) {
			error = "Table not found.";
			throw new InvalidInputException(error.trim()); 
		}
		else if (numberOfSeats <= 0) {
			error = "Invalid number of seats.";
			throw new InvalidInputException(error.trim());
		}
		else if (newNumber <= 0) {
			error = "Invalid table number.";
			throw new InvalidInputException(error.trim());
		}
		
		boolean reserved = table.hasReservations();
		if (reserved) {
			error = "This table is currently reserved! Please, remove all reservations to that table and try again.";
			throw new InvalidInputException(error.trim());
		}

		RestoApp restoApp = RestoApplication.getRestoApp();
		List<Order> currentOrders = restoApp.getCurrentOrders();
		for (Order order : currentOrders) {
			List<Table> tables = order.getTables();
			boolean inUse = tables.contains(table);
			if (inUse) {
				error = "Table in use.";
				throw new InvalidInputException(error.trim());
			}
		}

		try {
			table.setNumber(newNumber);
		}
		catch (RuntimeException e) {
			error = e.getMessage();
			if (error.equals("Cannot create due to duplicate number")) {
				error = "A table with this number already exists. Please use a different number.";
			}
			throw new InvalidInputException(error);
		}	


		int n = table.numberOfCurrentSeats();
		for (int i = 0; i < (numberOfSeats - n); i++) {
			Seat seat = table.addSeat();
			table.addCurrentSeat(seat);
		}


		for (int j = 1; j < (n - numberOfSeats); j++) {
			Seat seat = table.getCurrentSeat(0);
			table.removeCurrentSeat(seat);
		}

		RestoApplication.save();

	}

	public static List<ItemCategory> getItemCategories(){

		List <ItemCategory> categorieList = new ArrayList<ItemCategory>();


		categorieList.add(ItemCategory.Appetizer);
		categorieList.add(ItemCategory.Main);
		categorieList.add(ItemCategory.Dessert);
		categorieList.add(ItemCategory.AlcoholicBeverage);		
		categorieList.add(ItemCategory.NonAlcoholicBeverage);


		return categorieList;

	}

	public static List<MenuItem> getMenuItems(ItemCategory itemCategory){

		List<MenuItem> itemsElements = new ArrayList<MenuItem>();//create my list of item desired
		for(MenuItem menu_item: RestoApplication.getRestoApp().getMenu().getMenuItems()) //getting each element in our menu item List
			if(menu_item.getItemCategory().equals(itemCategory) && menu_item.hasCurrentPricedMenuItem())//if its the same element type and its price exist then we add it to the list
				itemsElements.add(menu_item);

		return itemsElements;
	}
	//The following reserve method is the one that should be called from the GUI, it takes in a List<String> of table numbers
	//which is will use to get the table objects from the restoapp.
	public static void reserve(List <String> tables, Date date, Time time, int numberInParty, String contactName, 
			String contactEmailAddress, String contactPhoneNumber) throws InvalidInputException{
		List<Table> tableList = new ArrayList<Table>();
		for(String tableNumberStr : tables) {
			int tableNumberInt = Integer.parseInt(tableNumberStr);
			tableList.add(Table.getWithNumber(tableNumberInt));
		}
		//System.out.println("Table list: "+ tableList.toString());
		reserve(date, time, numberInParty, contactName, contactEmailAddress, contactPhoneNumber, tableList);
	}

	public static void reserve(Date date, Time time, int numberInParty, String contactName, 
			String contactEmailAddress, String contactPhoneNumber, List <Table> tables) throws InvalidInputException{

		String error = "";
		for(Table table : tables) {
			if(table == null) {
				error = "One of these tables does not exist";
				throw new InvalidInputException(error.trim());
			}
		}
		if (date == null || time == null || contactName == null || contactEmailAddress == null || contactPhoneNumber == null) {
			error = "Missing reservation input.";
			throw new InvalidInputException(error.trim());
		}
		if (numberInParty <= 0) {
			error = "Number of people in party must be positive.";
			throw new InvalidInputException(error.trim());
		}
		RestoApp r = RestoApplication.getRestoApp();
		List <Table> currentTables = r.getCurrentTables();
		int seatCapacity = 0;
		for (Table table : tables) {
			boolean current = currentTables.contains(table);
			if (current == false) {
				error = "Table: " + table.getNumber() + " does not exist";
				throw new InvalidInputException(error.trim());
			}
			seatCapacity += table.numberOfCurrentSeats();
			List <Reservation> reservations = table.getReservations();
			for (Reservation reservation : reservations) {
				boolean overlaps = reservation.doesOverlap(date, time);
				if (overlaps) {
					error = "Reservation overlaps with table: " + table.getNumber();
					throw new InvalidInputException(error.trim());
				}
			}
		}
		if (seatCapacity < numberInParty) {
			error = "Not enough seats for party.";
			throw new InvalidInputException(error.trim());
		}
		Table[] tablesToReserve = new Table[tables.size()];
		tables.toArray(tablesToReserve);
		new Reservation(date, time, numberInParty, contactName, contactEmailAddress, contactPhoneNumber, r, tablesToReserve);
		System.out.println("Added reservation for tables: ");
		for (Table table : tablesToReserve) {
			System.out.print(table.getNumber()+" ");
		}
		RestoApplication.save();
	}
	
	public static void removeReservation(Reservation reservation) throws InvalidInputException {
		String error = "";
		if (reservation == null) {
			error = "Must select reservation to remove.";
			throw new InvalidInputException(error.trim());
		}
		//RestoApp restoApp = RestoApplication.getRestoApp();
		//restoApp.removeReservation(reservation);
		reservation.delete();
		RestoApplication.save();
	}

	public static void startOrder(List<Table> tables) throws InvalidInputException {
		String error = "";
		if(tables == null) {
			error = "Need to select tables";
			throw new InvalidInputException(error.trim());
		}
		RestoApp r = RestoApplication.getRestoApp();
		List<Table> currentTables = r.getCurrentTables();
		for (Table table : tables) {
			boolean current = currentTables.contains(table);
			if (current == false) {
				error = "Table: "+table.getNumber()+" does not exist.";
				throw new InvalidInputException(error.trim());
			}
		}
		boolean orderCreated = false;
		Order newOrder = null;
		for(Table table : tables) {
			if(orderCreated) {
				table.addToOrder(newOrder);
			}
			else {
				Order lastOrder = null;
				if(table.numberOfOrders() > 0) {
					lastOrder = table.getOrder(table.numberOfOrders()-1);
				}
				table.startOrder();
				if (table.numberOfOrders() > 0 && !table.getOrder(table.numberOfOrders()-1).equals(lastOrder)) {
					orderCreated = true;
					newOrder = table.getOrder(table.numberOfOrders()-1);
				}
			}
		}
		r.addCurrentOrder(newOrder);
		RestoApplication.save();
	}

	public static void endOrder(Order order) throws InvalidInputException {
		String error = "";
		if (order == null) {
			error = "Must specify order to end.";
			throw new InvalidInputException(error.trim());
		}
		RestoApp r = RestoApplication.getRestoApp();
		List<Order> currentOrders = r.getCurrentOrders();
		boolean current = currentOrders.contains(order);
		if(current == false) {
			error = "Order does not exist.";
			throw new InvalidInputException(error.trim());
		}
		List<Table> tables = order.getTables();
		for(Table table : tables) {
			if (table.numberOfOrders() > 0 && table.getOrder(table.numberOfOrders()-1).equals(order)) {
				table.endOrder(order);
			}
		}
		if(allTablesAvailableOrDifferentCurrentOrder(tables,order)) {
			r.removeCurrentOrder(order);
		}
		RestoApplication.save();
	}

	private static boolean allTablesAvailableOrDifferentCurrentOrder(List<Table> tables, Order order) {
		boolean result = true;
		for (Table table : tables) {
			if (table.getStatus() != Table.Status.Available || table.getOrder(table.numberOfOrders()-1).equals(order)) {
				result = false;
				break;
			}
		}
		return result;
	}

	public static void addMenuItem(String name, ItemCategory category, double price) throws InvalidInputException {
		String error = "";
		if (name == null || name.isEmpty() || name.trim().isEmpty()) {
			error = "The name is empty!";
			throw new InvalidInputException(error.trim());
		}
		if (category == null) {
			error = "No menu category was chosen!";
			throw new InvalidInputException(error.trim());
		}
		if (price <= 0) {
			error = "The price entered is not positive!";
			throw new InvalidInputException(error.trim());
		}

		RestoApp r = RestoApplication.getRestoApp();
		Menu menu = r.getMenu();

		MenuItem menuItem;
		try {
			menuItem = new MenuItem(name, menu);
		}
		catch (RuntimeException e) {
			error = e.getMessage();
			if (error.equals("Cannot create due to duplicate name")) {
				error = "A menu item with this name already exists. Please use a different name.";
			}
			throw new InvalidInputException(error);
		}

		menuItem.setItemCategory(category);
		PricedMenuItem pmi = menuItem.addPricedMenuItem(price, r);
		menuItem.setCurrentPricedMenuItem(pmi);

		try {
			RestoApplication.save();
		}
		catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	public static void removeMenuItem(MenuItem menuItem) throws InvalidInputException {
		String error = "";
		if (menuItem == null ) {
			error = "No menu item was selected!";
			throw new InvalidInputException(error.trim());
		}

		/*boolean current = menuItem.hasCurrentPricedMenuItem();
		if (current) {
			error = "this menu item is not current!";
		}*/

		menuItem.setCurrentPricedMenuItem(null);

		/*if (error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}*/

		try {
			RestoApplication.save();
		}
		catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}

	}

	public static List<OrderItem> getOrderItems(Table table) throws InvalidInputException{
		Order lastOrder;
		String error = "";
		if (table == null) {
			error = "error: Table not found.";
			throw new InvalidInputException(error.trim());
		}
		RestoApp restoApp = RestoApplication.getRestoApp();
		List<Table> currentTables = restoApp.getCurrentTables();

		boolean current = currentTables.contains(table);

		if(current == false) {
			error = "error: The table is available";
			throw new InvalidInputException(error.trim());
		}
		Status status = table.getStatus();

		if(status == Status.Available) {
			error = "error: The table is available";
			throw new InvalidInputException(error.trim());
		}

		lastOrder = null;

		if(table.numberOfOrders() > 0) {
			lastOrder = table.getOrder(table.numberOfOrders()-1);
		}

		else {
			error = "The table has no order";
			throw new InvalidInputException(error.trim());
		}

		List<Seat> currentSeats = table.getCurrentSeats();

		List<OrderItem> result = new ArrayList<OrderItem>();

		for(Seat seat : currentSeats ) {
			List<OrderItem> orderitems = seat.getOrderItems();

			for(OrderItem orderitem : orderitems ) {
				Order order = orderitem.getOrder();

				if(lastOrder.equals(order) && !result.contains(orderitem)) {
					result.add(orderitem);
				}

			}
		}

		return result;
	}

	public static void cancelOrderItem(OrderItem orderItem) throws InvalidInputException{

		String error = "";
		if (orderItem == null) {
			error = "no ordered Item selected";
			throw new InvalidInputException(error.trim());
		}



		List<Seat> seats= orderItem.getSeats();
		Order order = orderItem.getOrder();
		List<Table> tables = new ArrayList<Table>();

		for (Seat seat : seats) {
			Table table = seat.getTable();

			Order lastOrder=null;

			if(table.numberOfOrders()>0) {
				lastOrder= table.getOrder(table.numberOfOrders()-1);
			} else {
				error=" Order doesn't exist";
				throw new InvalidInputException(error.trim());
			}

			if (lastOrder.equals(order) && !tables.contains(table)) {
				tables.add(table);
			}

		}
		for( Table table :tables) {
			table.cancelOrderItem(orderItem);
		}
		RestoApplication.save();
	}

	public static void cancelOrder(Table table) throws InvalidInputException {

		String error = "";
		if (table == null) {
			error = "no table selected";
			throw new InvalidInputException(error.trim());
		}	
		RestoApp r = RestoApplication.getRestoApp();
		List<Table> currentTables= r.getCurrentTables();

		boolean current = currentTables.contains(table);
		if(current == false) {
			error = "table does not exist.";
			throw new InvalidInputException(error.trim());
		}else {

			table.cancelOrder();
		}
		RestoApplication.save();


	}
	
       public static void orderMenuItem (MenuItem menuItem, int quantity, List <Seat> seats) throws InvalidInputException {
	
	RestoApp r = RestoApplication.getRestoApp();
	String error = "";
	if (menuItem == null && seats == null && quantity < 0) {
		error = "Please select menu Item, seat and set quantity to a positive value";
				throw new InvalidInputException(error.trim());
	}
	boolean current = menuItem.hasPricedMenuItems();
	if (current == false) {
		error = "Item does not exist.";
		throw new InvalidInputException(error.trim());
	}
	
	List<Table> currentTables = r.getCurrentTables();
	
	Order lastOrder=null;
	
	
	for (Seat seat : seats) {
		
		Table table = seat.getTable();
		boolean currentTable = currentTables.contains(table);
		if (currentTable == false) {
			error = "Table: " + table.getNumber()+" does not exist.";
			throw new InvalidInputException(error.trim());
		}
		
		List<Seat> currentSeat = table.getSeats();
		boolean currentSeats = currentSeat.contains(seat);
		if (currentSeats == false) {
			error = "Seat does not exist.";
			throw new InvalidInputException(error.trim());
		}
		
		if (lastOrder == null) {
			
			if (table.numberOfOrders() > 0) {
			lastOrder = table.getOrder(table.numberOfOrders()-1);
			
			}
			
			else {
			error = "Table: "+table.getNumber()+" does not contain any orders";
			throw new InvalidInputException(error.trim());
			}
		}
		
		else {
			Order comparedOrder = null;
			
			if (table.numberOfOrders() > 0) {
			comparedOrder = table.getOrder(table.numberOfOrders()-1);
		}
			
			else {
			error = "Table: "+table.getNumber()+" does not contain any orders";
			throw new InvalidInputException(error.trim());
		}
			
			if (!(comparedOrder.equals(lastOrder))) {
			throw new InvalidInputException(error);
		}
	}
	}
	if (lastOrder == null ) {
		error = "Table: does not contain any orders";
		throw new InvalidInputException(error.trim());
	}
	
	PricedMenuItem pmi = menuItem.getCurrentPricedMenuItem();
	
	
	boolean itemCreated = false;
	OrderItem newItem = null;
	OrderItem lastItem = null;
	
	for (Seat seat : seats) {
		
		Table table = seat.getTable();
		
		if (itemCreated == true) {
			table.addToOrderItem(newItem, seat);
		}
		
		else {
			lastItem = null;
			if (lastOrder.numberOfOrderItems() > 0) {
				lastItem = lastOrder.getOrderItem(lastOrder.numberOfOrderItems()-1);
			}
			
			table.orderItem(quantity, lastOrder, seat, pmi);
			
			if (lastOrder.numberOfOrderItems() > 0 && !(lastOrder.getOrderItem(lastOrder.numberOfOrderItems()-1).equals(lastItem))) {
				itemCreated = true;
				newItem = lastOrder.getOrderItem(lastOrder.numberOfOrderItems()-1);
			}
		}

	}
	if (itemCreated == false) {
		error = "No item was created";
		throw new InvalidInputException(error.trim());
	}
	
	RestoApplication.save();
}
    
	public static void issueBill(List<Seat> seats) throws InvalidInputException {
		String error = "";
		if (seats == null || seats.isEmpty()) {
			error = "No seat was selected!";
			throw new InvalidInputException(error.trim());
		}

		RestoApp r = RestoApplication.getRestoApp();
		List<Table> currentTables = r.getCurrentTables(); 
		Order comparedOrder = null;

		Order lastOrder = null;
		for (Seat seat : seats) {
			Table table = seat.getTable();
			boolean current = currentTables.contains(table);
			if (!current) {
				error = "There is no current table for the selected seat(s)!";
				throw new InvalidInputException(error.trim());
			}
			List<Seat> currentSeats = table.getCurrentSeats();
			current = currentSeats.contains(seat);
			if (!current) {
				error = "There is no current table for the selected seat(s)!";
				throw new InvalidInputException(error.trim());
			}

			if (lastOrder == null) {
				if(table.numberOfOrders() > 0) {
					lastOrder = table.getOrder(table.numberOfOrders()-1);
				} 
				else {
					error = "One of the tables for the associated seats does not have an order!";
					throw new InvalidInputException(error.trim());
				}
			}
			else {

				if (table.numberOfOrders() > 0) {
					comparedOrder = table.getOrder(table.numberOfOrders()-1);
				}
				else {
					error = "One of the tables for the associated seats does not have an order!";
					throw new InvalidInputException(error.trim());
				}
			}
			if (!comparedOrder.equals(lastOrder)) {
				error = "One of the tables for the associated seats has a duplicated order! please fix the issue.";
				throw new InvalidInputException(error.trim());
			}

			if (lastOrder == null) {
				error = "One of the tables for the associated seats does not have an order!";
				throw new InvalidInputException(error.trim());
			}

			boolean billCreated = false;
			Bill newBill = null;

			for (Seat seat1 : seats) {
				Table table1 = seat.getTable();

				if (billCreated) {
					table1.addToBill(newBill, seat1);
				}
				else {
					Bill lastBill = null;
					if (lastOrder.numberOfBills() > 0) {
						lastBill = lastOrder.getBill(lastOrder.numberOfBills()-1);
					}
					if (lastOrder.numberOfBills() > 0 && !lastOrder.getBill(lastOrder.numberOfBills()-1).equals(lastBill)) {
						billCreated = true;
						newBill = lastOrder.getBill(lastOrder.numberOfBills()-1);
					}
				}
			}

			if (!billCreated) {
				error = "Oops something went wrong! Please try again";
				throw new InvalidInputException(error.trim());
			}

			try {
				RestoApplication.save();
			}
			catch (RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}

		}
	}


}
