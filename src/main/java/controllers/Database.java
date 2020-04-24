package controllers;

import java.util.ArrayList;
import java.util.Dictionary;

import org.json.simple.*;

import models.*;

public class Database {
	private static Dictionary<String, Table> tables;
	
	public static void Save(Table table) {
		tables.put(table.getName(), table);
	}
	
	public static void LoadTables() {
		
	}
	
	public static Table GetTable(String name) {
		return tables.get(name);
	}
}
