package models;

public class Schema {
	private String[] fields;
	private String lineFormat;
	private String name;
	
	public Schema(String[] fields, String lineFormat, String name) {
		setFields(fields);
		setLineFormat(lineFormat);
		setName(name);
	}
	
	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public String getLineFormat() {
		return lineFormat;
	}

	public void setLineFormat(String lineFormat) {
		this.lineFormat = lineFormat;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
