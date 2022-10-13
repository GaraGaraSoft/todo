package todo;

import java.io.Serializable;
import java.util.ArrayList;

public class ToDoBean implements Serializable{
	private int id;
	private String title;
	private String level;
	private String content;

	private int big;
	private int middle;
	private int year;
	private int month;
	private int day;
	
	
	public int getId() {
		return id;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	
	
	//各データを重要度で分類
	private ArrayList<Integer> bigNum = new ArrayList<>();
	private ArrayList<Integer> middleNum = new ArrayList<>();
	private ArrayList<Integer> smallNum = new ArrayList<>();
	private ArrayList<Integer> scheNum = new ArrayList<>();


	public ArrayList<Integer> getBigNum() {
		return bigNum;
	}
	public void setBigNum(int bigNum) {
		this.bigNum.add(bigNum);
	}
	public ArrayList<Integer> getMiddleNum() {
		return middleNum;
	}
	public void setMiddleNum(int middleNum) {
		this.middleNum.add(middleNum);
	}
	public ArrayList<Integer> getSmallNum() {
		return smallNum;
	}
	public void setSmallNum(int smallNum) {
		this.smallNum.add(smallNum);
	}
	public ArrayList<Integer> getScheNum() {
		return scheNum;
	}
	public void setScheNum(int scheNum) {
		this.scheNum.add(scheNum);
	}
	
	
}
