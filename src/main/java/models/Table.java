package models;

public class Table {
	private Schema schema;
	private String[][] data;
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

	public String[][] getData() {
		return data;
	}

	public void setData(String[][] data) {
		this.data = data;
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

	public void LoadTable() {
		
	}
}
