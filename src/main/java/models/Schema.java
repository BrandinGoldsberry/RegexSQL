package models;

public class Schema {
	private String[] fields;
	private String lineFormat;

	
	public Schema(String[] fields, String lineFormat) {
		setFields(fields);
		setLineFormat(lineFormat);
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
}
