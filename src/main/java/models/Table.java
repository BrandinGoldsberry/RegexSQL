package models;

import java.util.Hashtable;

public class Table {
	private Schema schema;
	private Hashtable<String, String[]> data;
	private String[][] rowData;
	private String file;
	private String name;
	
	public Table(Schema schema, String file, String name) {
		setSchema(schema);
		setFile(file);
		setName(name);
	}

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public Hashtable<String, String[]> getData() {
		return data;
	}

	public void setData(Hashtable<String, String[]> data) {
		this.data = data;
	}
	
	public String[][] getRowData() {
		return rowData;
	}

	public void setRowData(String[][] rowData) {
		this.rowData = rowData;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder formatBuilder = new StringBuilder();
		for (String field : getSchema().getFields()) {
			formatBuilder.append("%-20s ");
		}
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format(formatBuilder.toString(), (Object[]) getSchema().getFields()));
		sb.append("\r\n");
		
		for (String[] dataList : rowData) {
			sb.append(String.format(formatBuilder.toString(), (Object[]) dataList));
			sb.append("\r\n");
		}
		return sb.toString();
	}
}
