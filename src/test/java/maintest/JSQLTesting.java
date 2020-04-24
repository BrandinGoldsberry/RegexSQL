package maintest;

import org.junit.jupiter.api.Test;

import controllers.Query;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.awt.FileDialog;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;   

import lib.ConsoleIO;

public class JSQLTesting {
	
	@Test
	public void TableCreation() {
		String query = ConsoleIO.promptForInput("Enter Query", false);
	}
	
	@Test
	public void SelectRegex() {
		
	}
	
	@Test
	public void CreateRegex() {
		//arange
//		String query = "CREATE TABLE 'mylog' (date, time, log_level, src_ip, username, msg) :\r\n" + 
//				"    line format /([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) ([A-Z]+) \\[.*?\\] (...) (...) : (.*$)/\r\n" + 
//				"    file 'C:/users/access_log.txt'\r\n" + 
//				";";
		String query = ConsoleIO.promptForInput("Input Query", false);
		String[] correctFields = { "date", "time", "log_level", "src_ip", "username", "msg" };
		String correctFile = "C:/users/access_log.txt";
		String correctName = "mylog";
		String correctReg = "([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}:[0-9]{2}:[0-9]{2}) ([A-Z]+) \\[.*?\\] (...) (...) : (.*$)";
		String pattern = "CREATE[\\s\\r\\n]+TABLE[\\s\\r\\n]'(?<tablename>[A-Za-z](?>[A-Za-z]|\\d)*)'[\\s\\r\\n]\\([\\s\\r\\n]*?(?<args>([A-Za-z_]+(?>,[\\s\\r\\n]+)?)+)[\\s\\r\\n]*?\\)[\\s\\r\\n]*?:[\\s\\r\\n]*?line[\\s\\r\\n]+format[\\s\\r\\n]+\\/(?<lineformat>[\\s\\S]+?)\\/[\\s\\r\\n]+file[\\s\\r\\n]+'(?<filename>[A-Z-a-z]:(?>\\/[\\w\\d]+)+\\.[\\w\\d]+)'[\\s\\r\\n]*?;";
		
		String[] actualFields = null;
		String actualFile = null;
		String actualName = null;
		String actualReg = null;
		
		//act
		Pattern compPattern = Pattern.compile(pattern);
		Matcher match = compPattern.matcher(query);
		if(match.find()) {
			actualFields = match.group("args").split(", ");
			actualFile = match.group("filename");
			actualName = match.group("tablename");
			actualReg = match.group("lineformat");
		}
		
		//assert
		assertArrayEquals(correctFields, actualFields);
		assertEquals(correctFile, actualFile);
		assertEquals(correctName, actualName);
		assertEquals(correctReg, actualReg);
	}
	
	@Test
	public void FieldTest() {
		//arrange
		FileDialog dialog = new FileDialog((Frame)null, "Select File to Open");
		
		//act
		
		//assert
	}
}
