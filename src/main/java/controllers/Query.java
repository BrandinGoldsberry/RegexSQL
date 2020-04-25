package controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.regex.*;

import models.*;

public class Query {
	public static void Execute(String query) {
		String createPatternStr = "CREATE[\\s\\r\\n]+TABLE[\\s\\r\\n]'(?<tablename>[A-Za-z](?>[A-Za-z]|\\d)*)'[\\s\\r\\n]\\([\\s\\r\\n]*?(?<args>([A-Za-z_]+(?>,[\\s\\r\\n]+)?)+)[\\s\\r\\n]*?\\)[\\s\\r\\n]*?:[\\s\\r\\n]*?line[\\s\\r\\n]+format[\\s\\r\\n]+\\/(?<lineformat>[\\s\\S]+?)\\/[\\s\\r\\n]+file[\\s\\r\\n]+'(?<filename>[A-Z-a-z]:(?>\\/[\\w\\d]+)+\\.[\\w\\d]+)'[\\s\\r\\n]*?;";
		
		Pattern createPattern = Pattern.compile(createPatternStr);
		Matcher match = createPattern.matcher(query);
		if(match.find()) {
			String[] Fields = match.group("args").split(", ");
			String File = match.group("filename");
			String Name = match.group("tablename");
			String Reg = match.group("lineformat");
			create(Fields, File, Name, Reg);
		} else {
			//select regex test and basic info
			String selectPatternStr = "SELECT\\s(?<fields>(?>\\w+(?>,\\s)?)*)[\\s\\r\\n]FROM\\s(?<tablename>[A-Za-z]+)(?>[\\s\\r\\n])?";
			
			Pattern selectPattern = Pattern.compile(selectPatternStr);
			match = selectPattern.matcher(query);
			
			if(match.find()) {
				select(query, match.group("fields"), match.group("tablename"));
			} else {
				throw new IllegalArgumentException("Query did not match create or select syntax");
			}
		}
	}
	
	private static void create(String[] Fields, String File, String Name, String Reg) {
		Schema schema = new Schema(Fields, Reg);
		Table table = new Table(schema, File, Name);
		Database.Save(table);
	}
	
	private static void select(String query, String fields, String tablename) {
		if(query.contains("WHERE")) {
			String limiterPatternString = "(?>(?<keyword>WHERE|AND|OR)\\s(?<field>\\w+(?>,\\s)?)\\s(?<operator>>=|=|<=|<|>)\\s'(?<argument>.*?)'[\\s\\r\\n]?)";
			
			Pattern limiterPattern = Pattern.compile(limiterPatternString);
			Matcher match = limiterPattern.matcher(query);
			
			ArrayList<SelectQuery> queries = new ArrayList<SelectQuery>();
			
			while(match.find()) {
				SelectQuery sq = new SelectQuery(match.group("keyword"), match.group("operator"), match.group("argument"), match.group("field"));
				queries.add(sq);
			}
			
			String[] splitFields = fields.split(", ");
			Table toSelect = Database.GetTable(tablename);
			
			for (String str : splitFields) {
				if(!Arrays.asList(toSelect.getSchema().getFields()).contains(str)) {
					throw new IllegalArgumentException("Column " + str + " not in table");
				}
			}
			
			where(queries.toArray(new SelectQuery[queries.size()]), splitFields, toSelect);
		} else {
			String[] splitFields = fields.split(", ");
			Table t = Database.GetTable(tablename);
			
			for (String str : splitFields) {
				if(!Arrays.asList(t.getSchema().getFields()).contains(str)) {
					throw new IllegalArgumentException("Column " + str + " not in table");
				}
			}
			
			String[] SchemaFields = t.getSchema().getFields();
			Hashtable<String, String[]> data = t.getData();
			ArrayList<String> selectedColumns = new ArrayList<String>();
			ArrayList<ArrayList<String>> rowData = new ArrayList<ArrayList<String>>();
			
			for (int i = 0; i < SchemaFields.length; i++) {
				for (int j = 0; j < splitFields.length; j++) {
					if(SchemaFields[i].equals(splitFields[j])) {
						selectedColumns.add(SchemaFields[i]);		
						rowData.add(new ArrayList<String>());
					}
				}
			}
			
			for (int i = 0; i < data.get(selectedColumns.get(0)).length; i++) {				
				for (int j = 0; j < selectedColumns.size(); j++) {
					String toAdd = data.get(selectedColumns.get(j))[i];
					rowData.get(i).add(toAdd);
				}
			}
			
			StringBuilder formatBuilder = new StringBuilder();
			for (String field : selectedColumns) {
				formatBuilder.append("%-20s ");
			}
			
			StringBuilder sb = new StringBuilder();
			
			sb.append(String.format(formatBuilder.toString(), selectedColumns.toArray()));
			sb.append("\r\n");
			
			for (ArrayList<String> dataList : rowData) {
				sb.append(String.format(formatBuilder.toString(), (Object[]) dataList.toArray()));
				sb.append("\r\n");
			}
			
			System.out.println(sb.toString());
		}
	}
	
	private static void where(SelectQuery[] queries, String[] splitFields, Table t) {
		String[] SchemaFields = t.getSchema().getFields();
		Hashtable<String, String[]> data = t.getData();
		ArrayList<String> selectedColumns = new ArrayList<String>();
		ArrayList<ArrayList<String>> rowData = new ArrayList<ArrayList<String>>();
		
		for (int i = 0; i < SchemaFields.length; i++) {
			for (int j = 0; j < splitFields.length; j++) {
				if(SchemaFields[i].equals(splitFields[j])) {
					selectedColumns.add(SchemaFields[i]);		
				}
			}
		}
		
		for (int i = 0; i < data.get(selectedColumns.get(0)).length; i++) {		
			rowData.add(new ArrayList<String>());
			for (int j = 0; j < selectedColumns.size(); j++) {
				String toAdd = data.get(selectedColumns.get(j))[i];
				rowData.get(i).add(toAdd);
			}
		}
		
		StringBuilder formatBuilder = new StringBuilder();
		for (String field : selectedColumns) {
			formatBuilder.append("%-20s ");
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format(formatBuilder.toString(), selectedColumns.toArray()));
		sb.append("\r\n");

		ArrayList<Integer> remove = new ArrayList<Integer>();
		for (int i = 0; i < rowData.size(); i++) {
			boolean keep = true;
			for (int j = 0; j < queries.length; j++) {
				int queryColumn = selectedColumns.indexOf(queries[j].getField());
				if(queryColumn == -1) {
					throw new IllegalArgumentException("Clause did not contain selected column");
				}
				String toComp = rowData.get(i).get(queryColumn);
				String switchCase = queries[j].getKeyword();
				switch(switchCase) {
				case "WHERE":
					keep = removeData(queries[j], toComp);
					break;
				case "AND":
					keep = keep & removeData(queries[j], toComp);
					break;
				case "OR":
					keep = keep | removeData(queries[j], toComp);
					break;
				}
			}
			if(!keep) {
				remove.add(i);
			}
		}
		
		for (int i = 0; i < remove.size(); i++) {
			int toRemove = remove.get(i) - i;
			rowData.remove(toRemove);			
		}
		
		for (ArrayList<String> dataList : rowData) {
			sb.append(String.format(formatBuilder.toString(), (Object[]) dataList.toArray()));
			sb.append("\r\n");
		}
		
		System.out.println(sb.toString());
	}
	
	private static boolean removeData(SelectQuery sq, String data) {
		Object check = resolveType(data);
		Object checkAgainst = resolveType(sq.getArgument());
		
		if(!(checkAgainst.getClass() == check.getClass())) {
			throw new IllegalArgumentException("Argument mismatched with column");
		}
		
		if(check instanceof String) {
			if(!sq.getOperator().equals("=")) {
				throw new IllegalArgumentException("Strings can only be compared against equality");
			} else {
				return sq.getArgument().equals(check);
			}
		} else if(check instanceof Date) {
			switch(sq.getOperator()) {
			case "=":
				if(!(checkAgainst instanceof Date)) { throw new IllegalArgumentException("Type Comparison MisMatch"); }
				return ((Date) check).compareTo((Date) checkAgainst) == 0;
			case "<=":
				if(!(checkAgainst instanceof Date)) { throw new IllegalArgumentException("Type Comparison MisMatch"); }
				return ((Date) check).compareTo((Date) checkAgainst) <= 0;
			case ">=":
				if(!(checkAgainst instanceof Date)) { throw new IllegalArgumentException("Type Comparison MisMatch"); }
				return ((Date) check).compareTo((Date) checkAgainst) >= 0;
			case ">":
				if(!(checkAgainst instanceof Date)) { throw new IllegalArgumentException("Type Comparison MisMatch"); }
				return ((Date) check).compareTo((Date) checkAgainst) > 0;
			case "<":
				if(!(checkAgainst instanceof Date)) { throw new IllegalArgumentException("Type Comparison MisMatch"); }
				return ((Date) check).compareTo((Date) checkAgainst) < 0;
			}
		} else if(check instanceof Integer) {
			switch(sq.getOperator()) {
			case "=":
				if(!(checkAgainst instanceof Integer)) { throw new IllegalArgumentException("Type Comparison MisMatch"); }
				return ((Integer)check).intValue() == ((Integer)checkAgainst).intValue();
			case "<=":
				if(!(checkAgainst instanceof Integer)) { throw new IllegalArgumentException("Type Comparison MisMatch"); }
				return ((Integer)check).intValue() <= ((Integer)checkAgainst).intValue();
			case ">=":
				if(!(checkAgainst instanceof Integer)) { throw new IllegalArgumentException("Type Comparison MisMatch"); }
				return ((Integer)check).intValue() >= ((Integer)checkAgainst).intValue();
			case ">":
				if(!(checkAgainst instanceof Integer)) { throw new IllegalArgumentException("Type Comparison MisMatch"); }
				return ((Integer)check).intValue() > ((Integer)checkAgainst).intValue();
			case "<":
				if(!(checkAgainst instanceof Integer)) { throw new IllegalArgumentException("Type Comparison MisMatch"); }
				return ((Integer)check).intValue() < ((Integer)checkAgainst).intValue();
			}
		}
		throw new IllegalArgumentException("Unsupported type detected");
	}
	
	private static Object resolveType(String input) {
		Pattern dateReg = Pattern.compile("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$");
		Pattern intReg = Pattern.compile("^[^\\D]+$");

		Matcher dateMatch = dateReg.matcher(input);
		Matcher intMatch = intReg.matcher(input);
		
		if(dateMatch.find()) {
			DateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
			try {
				Date newDate = dateFormat.parse(input);
				return newDate;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(intMatch.find()) {
			Integer ret = Integer.parseInt(input);
			return ret;
		} else {
			return input;
		}
		return input;
	}
}
