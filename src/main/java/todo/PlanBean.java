package todo;

import java.io.Serializable;

public class PlanBean implements Serializable{
	//テーブルの各行データ
	private int id;
	private String title;
	private String content;
	private String level;
	private int big;
	private int middle;
	private String date;
	private boolean hold;
	private int important;
	private String big_title;
	private String middle_title;
	
	
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public boolean isHold() {
		return hold;
	}
	public void setHold(boolean hold) {
		this.hold = hold;
	}
	public int getImportant() {
		return important;
	}
	public void setImportant(int important) {
		this.important = important;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public int getBig() {
		return big;
	}
	public void setBig(int big) {
		this.big = big;
	}
	public int getMiddle() {
		return middle;
	}
	public void setMiddle(int middle) {
		this.middle = middle;
	}
	public int getId() {
		return id;
	}
	
	public String getBig_title() {
		return big_title;
	}
	public void setBig_title(String big_title) {
		this.big_title = big_title;
	}
	public String getMiddle_title() {
		return middle_title;
	}
	public void setMiddle_title(String middle_title) {
		this.middle_title = middle_title;
	}
	
	

}
