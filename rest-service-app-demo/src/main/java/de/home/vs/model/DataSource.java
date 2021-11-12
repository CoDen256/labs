package de.home.vs.model;

import java.util.ArrayList;
import java.util.List;

public class DataSource {

	private static DataSource instance = null;
	
	private DataSource() {
	}
	
	public static DataSource getInstance() {
		if (instance == null) {
			instance = new DataSource();
		}
		return instance;
	}
	
	public void prefillData() {
	}

}
