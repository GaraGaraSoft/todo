package todo;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginBean implements Serializable{
	private int id;
	private String name;
	private String userid;
	private String password;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
	public static boolean loginCheck(LoginBean lbean) {
		boolean check = false;
		String sql = "select * from user where userid = ? and password = ?";
		try(Connection conn = ResourceFinder.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql);){
			pst.setString(1, lbean.getUserid());
			pst.setString(2, lbean.getPassword());
			System.out.println(lbean.getUserid());
			System.out.println(lbean.getPassword());
			
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				check = true;
				if(rs.getString("name")!=null) {
					lbean.setName(rs.getString("name"));
				}else {
					lbean.setName(rs.getString("userId"));
				}
			}
			rs.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return check;
	}

}
