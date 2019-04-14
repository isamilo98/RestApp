package ca.mcgill.ecse223.resto.application;

import java.awt.EventQueue;

import ca.mcgill.ecse223.resto.model.RestoApp;
import ca.mcgill.ecse223.resto.persistence.PersistenceObjectStream;
import ca.mcgill.ecse223.resto.view.RestoAppGUI;
import ca.mcgill.ecse223.resto.view.RestoPage;

public class RestoApplication {
	
	private static RestoApp restoApp;
	private static String filename = "menu.resto";
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RestoAppGUI frame = new RestoAppGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static RestoApp getRestoApp() {
		if (restoApp == null) {
			//load model
			restoApp = load();
		}
		return restoApp;
	}
	/**
	 * Persistence
	 */
	public static void save() {
		PersistenceObjectStream.serialize(restoApp);
	}
	
	public static RestoApp load() {
		PersistenceObjectStream.setFilename(filename);
		restoApp = (RestoApp) PersistenceObjectStream.deserialize();
		// model cannot be loaded - create empty RestoApp
		if (restoApp == null) {
			System.out.println("RestoApp is null.");
			restoApp = new RestoApp();
		}
		else {
			restoApp.reinitialize();
		}
		return restoApp;
	}
	
	
	public static void setFilename(String newFilename) {
		filename = newFilename;
	}

}
