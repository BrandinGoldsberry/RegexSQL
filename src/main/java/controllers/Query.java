package controllers;

import java.util.ArrayList;
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
			String selectPatternStr = "SELECT\\s(?<fields>(?>\\w+(?>,\\s)?)*)[\\s\\r\\n]FROM\\s(?<tablename>[A-Za-z]+)(?>[\\s\\r\\n])";
			
			Pattern selectPattern = Pattern.compile(selectPatternStr);
			match = selectPattern.matcher(query);
			
			if(match.find()) {
				select(query, match.group("fields"), match.group("tablename"));
			}
		}
	}
	
	private static void create(String[] Fields, String File, String Name, String Reg) {
		Schema schema = new Schema(Fields, Reg);
		Table table = new Table(schema, File, Name);
		Database.Save(table);
	}
	
	private static void select(String query, String fields, String tablename) {
		String limiterPatternString = "(?>(?<keyword>WHERE|AND|OR)\\s(?<feild>\\w+(?>,\\s)?)\\s(?<operator>>=|=|<=)\\s'(?<argument>.*)'[\\s\\r\\n]?)";
		
		Pattern limiterPattern = Pattern.compile(limiterPatternString);
		Matcher match = limiterPattern.matcher(query);
		
		ArrayList<SelectQuery> queries = new ArrayList<SelectQuery>();
		
		while(match.find()) {
			SelectQuery sq = new SelectQuery(match.group("keyword"), match.group("operator"), match.group("argument"), match.group("field"));
			queries.add(sq);
		}
		
		String[] splitFields = fields.split(", ");
		Table toSelect = Database.GetTable(tablename);
		where((SelectQuery[])queries.toArray(), splitFields, toSelect);
	}
	
	private static void where(SelectQuery[] queries, String[] fields, Table table) {
		
	}
}
