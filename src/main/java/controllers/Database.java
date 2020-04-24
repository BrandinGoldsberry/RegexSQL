package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.*;

import models.*;

public class Database {
	private static Hashtable<String, Table> tables = new Hashtable<String, Table>();
	
	public static void Save(Table table) {
		tables.put(table.getName(), table);
	}
	
	public static void LoadTables() {
		
	}
	
	public static Table GetTable(String name) {
		Table ret = tables.get(name);
		
		ArrayList<ArrayList<String>> rowData = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		
		for (int i = 0; i < ret.getSchema().getFields().length; i++) {
			data.add(new ArrayList<String>());
		}
		
		Scanner sc = null;
		try {
			File f = new File(ret.getFile());
			sc = new Scanner(f);
			int rowInc = 0;
			while(sc.hasNext()) {
				rowData.add(new ArrayList<String>());
				Pattern p = Pattern.compile(ret.getSchema().getLineFormat());
				String nL = sc.nextLine();
				Matcher m = p.matcher(nL);
				if(m.find()) {
					for (int i = 0; i < ret.getSchema().getFields().length; i++) {
						rowData.get(rowInc).add(m.group(i + 1));
						data.get(i).add(m.group(i + 1));
					}					
				}
				rowInc++;
			}
		} catch(IOException IOE) {
			IOE.printStackTrace();
		} finally {
			sc.close();
		}
		
		Hashtable<String, String[]> hashTable = new Hashtable<String, String[]>();
		
		for (int i = 0; i < ret.getSchema().getFields().length; i++) {
			hashTable.put(ret.getSchema().getFields()[i], data.get(i).toArray(new String[data.get(i).size()]));
		}
		
		String[][] RowData = new String[rowData.size()][];
		for (int i = 0; i < rowData.size(); i++) {
			RowData[i] = new String[rowData.get(i).size()];
			for (int j = 0; j < rowData.get(i).size(); j++) {
				RowData[i][j] = rowData.get(i).get(j);
			}
		}
		
		ret.setData(hashTable);
		ret.setRowData(RowData);
		return ret;
	}
}
