package models;

public class Table {
	private Schema schema;
	private String[][] data;
	
	public Table(Schema schema, String data) {
		setSchema(schema);
		parseData(data);
	}

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public String[][] getData() {
		return data;
	}

	public void setData(String[][] data) {
		this.data = data;
	}
	
	private void parseData(String data) {
		
	}
}
