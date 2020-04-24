package controllers;

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
			
		}
	}
	
	private static void create(String[] Fields, String File, String Name, String Reg) {
		Schema schema = new Schema(Fields, Reg);
		Table table = new Table(schema, File, Name);
		Database.Save(table);
	}
	
	private static void select(String query) {
		
	}
	
	private static void where(String query) {
		
	}
}
