package ca.mcgill.ecse223.resto.controller;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import ca.mcgill.ecse223.resto.application.RestoApplication;
import ca.mcgill.ecse223.resto.model.RestoApp;

public class RestoControllerTest {

	@BeforeClass
	public static void setUpOnce() {
		String filename = "testdata.resto";
		RestoApplication.setFilename(filename);
		File f = new File(filename);
		f.delete();
	}
	
	
	@Before
	public void setUp() {
		// clear all data
		RestoApp r = RestoApplication.getRestoApp();
		r.delete();
	}

	@Test
	public void testCreateTableSuccess() {
		RestoApp r = RestoApplication.getRestoApp();
		int number = 1;
		int x = 200;
		int y = 200;
		int width = 20;
		int length = 20;
		int numberOfSeats = 1;

		try {
			RestoController.createTable(number, x, y, width, length, numberOfSeats);
		} catch (InvalidInputException e) {
			// Check that no error occurred
			fail();	
		}
		
		// Check model in memory
		checkResultTable(number, x, y, width, length, numberOfSeats, r, 1);

	}
	
	@Test
	public void testCreateTableXNegative() {
		RestoApp r = RestoApplication.getRestoApp();
		int number = 1;
		int x = -200;
		int y = 200;
		int width = 20;
		int length = 20;
		int numberOfSeats = 1;

		String error = null;
		try {
			RestoController.createTable(number, x, y, width, length, numberOfSeats);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}
		
		// check error
		assertEquals("The position of te table cannot be chararterised by negative x and y variables.", error);
		// Check model in memory
		checkResultTable(number, x, y, width, length, numberOfSeats, r, 0);

	}
	
	@Test
	public void testCreateTableYNegative() {
		RestoApp r = RestoApplication.getRestoApp();
		int number = 1;
		int x = 200;
		int y = -200;
		int width = 20;
		int length = 20;
		int numberOfSeats = 1;

		String error = null;
		try {
			RestoController.createTable(number, x, y, width, length, numberOfSeats);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}
		
		// check error
		assertEquals("The position of te table cannot be chararterised by negative x and y variables.", error);
		// Check model in memory
		checkResultTable(number, x, y, width, length, numberOfSeats, r, 0);

	}
	
	@Test
	public void testCreateTableTableNumberNotPositive() {
		RestoApp r = RestoApplication.getRestoApp();
		int number = 0;
		int x = 200;
		int y = 200;
		int width = 20;
		int length = 20;
		int numberOfSeats = 1;

		String error = null;
		try {
			RestoController.createTable(number, x, y, width, length, numberOfSeats);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}
		
		// check error
		assertEquals("The number of a table has to be positive.", error);
		// Check model in memory
		checkResultTable(number, x, y, width, length, numberOfSeats, r, 0);

	}
	
	@Test
	public void testCreateTableWidthNotPositive() {
		RestoApp r = RestoApplication.getRestoApp();
		int number = 1;
		int x = 200;
		int y = 200;
		int width = 0;
		int length = 20;
		int numberOfSeats = 1;

		String error = null;
		try {
			RestoController.createTable(number, x, y, width, length, numberOfSeats);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}
		
		// check error
		assertEquals("The width and the length has to be positive.", error);
		// Check model in memory
		checkResultTable(number, x, y, width, length, numberOfSeats, r, 0);

	}
	
	@Test
	public void testCreateTableLengthNotPositive() {
		RestoApp r = RestoApplication.getRestoApp();
		int number = 1;
		int x = 200;
		int y = 200;
		int width = 20;
		int length = 0;
		int numberOfSeats = 1;

		String error = null;
		try {
			RestoController.createTable(number, x, y, width, length, numberOfSeats);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}
		
		// check error
		assertEquals("The width and the length has to be positive.", error);
		// Check model in memory
		checkResultTable(number, x, y, width, length, numberOfSeats, r, 0);

	}

	@Test
	public void testCreateTableNumberOfSeatsNotPositive() {
		RestoApp r = RestoApplication.getRestoApp();
		int number = 1;
		int x = 200;
		int y = 200;
		int width = 0;
		int length = 20;
		int numberOfSeats = 0;

		String error = null;
		try {
			RestoController.createTable(number, x, y, width, length, numberOfSeats);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}
		
		// check error
		assertEquals("The number of seats needs to be positive.", error);
		// Check model in memory
		checkResultTable(number, x, y, width, length, numberOfSeats, r, 0);

	}
	
	@Test
	public void testCreateTableDuplicateNumber() {
		RestoApp r = RestoApplication.getRestoApp();
		int number = 1;
		int x = 200;
		int y = 200;
		int width = 20;
		int length = 20;
		int numberOfSeats = 1;
		
		int number1 = 1;
		int x1 = 100;
		int y1 = 100;
		int width1 = 20;
		int length1 = 20;
		int numberOfSeats1 = 1;

		String error = null;
		try {
			RestoController.createTable(number, x, y, width, length, numberOfSeats);
			RestoController.createTable(number1, x1, y1, width1, length1, numberOfSeats1);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}
		
		// check error
		assertEquals("A table with this number already exists. Please use a different number.", error);
		// Check model in memory
		checkResultTable(number, x, y, width, length, numberOfSeats, r, 1);

	}
	
	@Test
	public void testCreateTableOverlaps() {
		RestoApp r = RestoApplication.getRestoApp();
		int number = 1;
		int x = 200;
		int y = 200;
		int width = 20;
		int length = 20;
		int numberOfSeats = 1;
		
		int number2 = 2;
		int x2 = 195;
		int y2 = 195;
		int width2 = 20;
		int length2 = 20;
		int numberOfSeats2 = 1;

		String error = null;
		try {
			RestoController.createTable(number, x, y, width, length, numberOfSeats);
			RestoController.createTable(number2, x2, y2, width2, length2, numberOfSeats2);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}
		
		// check error
		assertEquals("Cannot create table since it overlaps with other, please change the location or the dimensions", error);
		// Check model in memory
		checkResultTable(number, x, y, width, length, numberOfSeats, r, 1);

	}
	
	private void checkResultTable(int number, int x, int y, int width, int length, int numberOfSeats, RestoApp r, int numberTables) {
		assertEquals(numberTables, r.getTables().size());
		if (numberTables > 0) {
			assertEquals(number, r.getTable(0).getNumber());
			assertEquals(x, r.getTable(0).getX());
			assertEquals(y, r.getTable(0).getY());
			assertEquals(width, r.getTable(0).getWidth());
			assertEquals(length, r.getTable(0).getLength());
			assertEquals(numberOfSeats, r.getTable(0).getSeats().size());
			assertEquals(r, r.getTable(0).getRestoApp());
			assertEquals(0, r.getTable(0).getReservations().size());
			assertEquals(0, r.getTable(0).getOrders().size());
		}
		assertEquals(null, r.getMenu());
		assertEquals(0, r.getPricedMenuItems().size());
		assertEquals(0, r.getBills().size());
	}

}
