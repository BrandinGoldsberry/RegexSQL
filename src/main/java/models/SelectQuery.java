package models;

public class SelectQuery {
	private String keyword;
	private String operator;
	private String argument;
	private String field;
	
	public SelectQuery(String keyword, String operator, String argument, String field) {
		super();
		this.keyword = keyword;
		this.operator = operator;
		this.argument = argument;
		this.field = field;
	}
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getArgument() {
		return argument;
	}
	public void setArgument(String argument) {
		this.argument = argument;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
}
