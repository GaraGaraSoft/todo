package todo;

import java.io.Serializable;

public class LogBean implements Serializable{
	private int logid;
	private int id;
	private Boolean beforehold;
	private Boolean hold;
	private String ope;
	private String before_title;
	private String before_content;
	private String before_level;
	private int before_big;
	private int before_middle;
	private String before_date;
	private int before_important;
	private String after_title;
	private String after_content;
	private String after_level;
	private int after_big;
	private int after_middle;
	private String after_date;
	private int after_important;
	private String before_big_title;
	private String before_middle_title;
	private String after_big_title;
	private String after_middle_title;
	
	
	
	public Boolean getBeforehold() {
		return beforehold;
	}
	public void setBeforehold(Boolean beforehold) {
		this.beforehold = beforehold;
	}
	public Boolean getHold() {
		return hold;
	}
	public void setHold(Boolean hold) {
		this.hold = hold;
	}
	public String getBefore_date() {
		return before_date;
	}
	public void setBefore_date(String before_date) {
		this.before_date = before_date;
	}
	public int getBefore_important() {
		return before_important;
	}
	public void setBefore_important(int before_important) {
		this.before_important = before_important;
	}
	public String getAfter_date() {
		return after_date;
	}
	public void setAfter_date(String after_date) {
		this.after_date = after_date;
	}
	public int getAfter_important() {
		return after_important;
	}
	public void setAfter_important(int after_important) {
		this.after_important = after_important;
	}
	public int getLogid() {
		return logid;
	}
	public void setLogid(int logid) {
		this.logid = logid;
	}
	public String getBefore_big_title() {
		return before_big_title;
	}
	public void setBefore_big_title(String before_big_title) {
		this.before_big_title = before_big_title;
	}
	public String getBefore_middle_title() {
		return before_middle_title;
	}
	public void setBefore_middle_title(String before_middle_title) {
		this.before_middle_title = before_middle_title;
	}
	public String getAfter_big_title() {
		return after_big_title;
	}
	public void setAfter_big_title(String after_big_title) {
		this.after_big_title = after_big_title;
	}
	public String getAfter_middle_title() {
		return after_middle_title;
	}
	public void setAfter_middle_title(String after_middle_title) {
		this.after_middle_title = after_middle_title;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOpe() {
		return ope;
	}
	public void setOpe(String ope) {
		this.ope = ope;
	}
	public String getBefore_title() {
		return before_title;
	}
	public void setBefore_title(String before_title) {
		this.before_title = before_title;
	}
	public String getBefore_content() {
		return before_content;
	}
	public void setBefore_content(String before_content) {
		this.before_content = before_content;
	}
	public String getBefore_level() {
		return before_level;
	}
	public void setBefore_level(String before_level) {
		this.before_level = before_level;
	}
	public int getBefore_big() {
		return before_big;
	}
	public void setBefore_big(int before_big) {
		this.before_big = before_big;
	}
	public int getBefore_middle() {
		return before_middle;
	}
	public void setBefore_middle(int before_middle) {
		this.before_middle = before_middle;
	}
	public String getAfter_title() {
		return after_title;
	}
	public void setAfter_title(String after_title) {
		this.after_title = after_title;
	}
	public String getAfter_content() {
		return after_content;
	}
	public void setAfter_content(String after_content) {
		this.after_content = after_content;
	}
	public String getAfter_level() {
		return after_level;
	}
	public void setAfter_level(String after_level) {
		this.after_level = after_level;
	}
	public int getAfter_big() {
		return after_big;
	}
	public void setAfter_big(int after_big) {
		this.after_big = after_big;
	}
	public int getAfter_middle() {
		return after_middle;
	}
	public void setAfter_middle(int after_middle) {
		this.after_middle = after_middle;
	}
	

}
