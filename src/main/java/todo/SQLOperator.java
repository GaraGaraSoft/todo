package todo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

public class SQLOperator {
	
	/*	public static boolean checkUser(String id,String password) { //既存のユーザーIDと重複していないかチェック
			boolean set = true;
			String sql = "select * from user where userid=?";
			try(Connection conn = ResourceFinder.getConnection();
					PreparedStatement pst = conn.prepareStatement(sql);){
				pst.setString(1, id);
				ResultSet rs = pst.executeQuery();
				while(rs.next()) {
					set = false; //重複してたらfalse
				}
				rs.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			return set; //trueなら新規登録可能
		}*/
	
	public static boolean setNewUser(String name,String id,String password) { //新規ユーザー登録
		boolean set = false;
		String sql = "insert into user(userId,password,name) values(?,?,?)";
		try(Connection conn = ResourceFinder.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql);){
			pst.setString(1, id);
			pst.setString(2, password);
			if(name.equals("")) { //ユーザー名入力なしならユーザーIDを入力
				pst.setString(3, id);
			}else {
				pst.setString(3, name);
			}
			pst.executeUpdate();
			set = true;
		}catch(SQLIntegrityConstraintViolationException se) { //ユーザーID 一意制約チェック
			se.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return set;
	}
	
	public static void checkTable(String table) { //ログイン時にテーブルの存在をチェック、無ければ新規作成
		String sql = "create table if not exists "
				+ table
				+ "(\n"
				+ "id int auto_increment primary key,\n"
				+ "title text,\n"
				+ "content text,\n"
				+ "level varchar(10),\n"
				+ "big int,\n"
				+ "middle int,\n"
				+ "year int,\n"
				+ "month int,\n"
				+ "day int\n"
				+ ");";
		
		try(Connection conn = ResourceFinder.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql);){
			
			System.out.println(sql);
			
			pst.executeUpdate();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return;
	}
	
	public static boolean getList(ArrayList<PlanBean> bigarray,ArrayList<PlanBean> middlearray,ArrayList<PlanBean> smallarray,ArrayList<PlanBean> todayarray,ArrayList<PlanBean> schearray,ToDoBean tdbean,String table) {
		//ユーザーのデータテーブルを取得して設定
		boolean result = false;
		String  sql = "select * from "+table+" order by id";
		System.out.println(sql);
		try(Connection conn = ResourceFinder.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql);){
			ResultSet rs = pst.executeQuery(); //データテーブルからrsに取得
			while(rs.next()) {
//				System.out.println(rs.getInt("id"));
				PlanBean pbean = new PlanBean();
				pbean.setId(rs.getInt("id"));
				pbean.setTitle(rs.getString("title"));
				pbean.setContent(rs.getString("content"));
				pbean.setLevel(rs.getString("level"));
				pbean.setBig(rs.getInt("big"));
				pbean.setMiddle(rs.getInt("middle"));
				pbean.setYear(rs.getInt("year"));
				pbean.setMonth(rs.getInt("month"));
				pbean.setDay(rs.getInt("day"));
				
				//大目標のタイトルを取得してセッション配列に登録
				if(pbean.getLevel().equals("middle") || pbean.getLevel().equals("small")) {
					for(PlanBean b:bigarray) {
						System.out.println("大のID"+b.getId());
						if(pbean.getBig() == b.getId()) {
							pbean.setBig_title(b.getTitle());
						break;
						}
					}
				}
				
				//中目標のタイトルを取得してセッション配列に登録
				if(pbean.getLevel().equals("small")) {
					for(PlanBean m:middlearray) {
						System.out.println("中のID"+m.getId());
						if(pbean.getMiddle() == m.getId()) {
							pbean.setMiddle_title(m.getTitle());
						break;
						}
					}
				}
				/*
				System.out.println("bigid"+pbean.getBig()+",bigtitle"+pbean.getBig_title()+",bigsize"+bigarray.size()+",middleid"+pbean.getMiddle()+",middletitle"+pbean.getMiddle_title()+",middlesize"+middlearray.size());*/
				
				if(rs.getString("level").equals("big")) {
					bigarray.add(pbean);
				}else if(rs.getString("level").equals("middle")) {
					middlearray.add(pbean);
				}else if(rs.getString("level").equals("small")) {
					smallarray.add(pbean);
				}else if(rs.getString("level").equals("sche")) {
							Calendar cal = Calendar.getInstance();
					if(
						    pbean.getYear() == cal.get(Calendar.YEAR) &&
						    pbean.getMonth() == cal.get(Calendar.MONTH)+1 &&
						    pbean.getDay() == cal.get(Calendar.DATE)) {
						todayarray.add(pbean);
						schearray.add(pbean);
					}else {
						schearray.add(pbean);
					}
				}
				
			}
			rs.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	//新しい目標、予定を登録するメソッド
	public static boolean setNewData(PlanBean bean,String userid) {
		
		if(bean.getLevel().equals("big")) { 
			//大目標入力時
			boolean result = false;
			String sql = "insert into todo_"+userid+"(title,content,level) values(?,?,?)";
			System.out.println(sql);
			System.out.println(bean.getTitle()+","+bean.getContent()+","+bean.getLevel()+"2");
			try(Connection conn = ResourceFinder.getConnection();
					PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
				pst.setString(1, bean.getTitle());
				pst.setString(2, bean.getContent());
				pst.setString(3, bean.getLevel());
				pst.executeUpdate();
				
		         // 新しく登録したデータのidを取り出す
		         ResultSet rs = pst.getGeneratedKeys();
		         int newid=0;
		         
		         if(rs.next()){
		             newid = rs.getInt(1);
		         }
		         
		         bean.setId(newid);
		         System.out.println(newid);
		         
		         rs.close();
				
				result = true;
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			return result;
			
		}else if(bean.getLevel().equals("middle")) {
			//中目標入力時
			boolean result = false;
			String sql = "insert into todo_"+userid+"(title,content,level,big) values(?,?,?,?)";
			try (Connection conn = ResourceFinder.getConnection();
					PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
				pst.setString(1, bean.getTitle());
				pst.setString(2, bean.getContent());
				pst.setString(3, bean.getLevel());
				pst.setInt(4, bean.getBig());
				pst.executeUpdate();
				

		         // 新しく登録したデータのidを取り出す
		         ResultSet rs = pst.getGeneratedKeys();
		         int newid=0;
		         
		         if(rs.next()){
		             newid = rs.getInt(1);
		         }

		         bean.setId(newid);
		         System.out.println(newid);

		         rs.close();
				
				result = true;
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			return result;
			
		}else if(bean.getLevel().equals("small")) { 
			//小目標入力時
			boolean result = false;
			String sql = "insert into todo_"+userid+"(title,content,level,big,middle) values(?,?,?,?,?)";
			try (Connection conn = ResourceFinder.getConnection();
					PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
				pst.setString(1, bean.getTitle());
				pst.setString(2, bean.getContent());
				pst.setString(3, bean.getLevel());
				pst.setInt(4, bean.getBig());
				pst.setInt(5, bean.getMiddle());
				pst.executeUpdate();
				

		         // 新しく登録したデータのidを取り出す
		         ResultSet rs = pst.getGeneratedKeys();
		         int newid=0;
		         
		         if(rs.next()){
		             newid = rs.getInt(1);
		         }

		         bean.setId(newid);
		         System.out.println(newid);

		         rs.close();
				
				result = true;
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			return result;
			
		}else if(bean.getLevel().equals("sche")) { 
			//スケジュール入力時
			boolean result = false;
			String sql = "insert into todo_"+userid+"(title,content,level,year,month,day) values(?,?,?,?,?,?)";
			try (Connection conn = ResourceFinder.getConnection();
					PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
				pst.setString(1, bean.getTitle());
				pst.setString(2, bean.getContent());
				pst.setString(3, bean.getLevel());
				pst.setInt(4, bean.getYear());
				pst.setInt(5, bean.getMonth());
				pst.setInt(6, bean.getDay());
				pst.executeUpdate();
				

		         // 新しく登録したデータのidを取り出す
		         ResultSet rs = pst.getGeneratedKeys();
		         int newid=0;
		         
		         if(rs.next()){
		             newid = rs.getInt(1);
		         }

		         bean.setId(newid);
		         System.out.println(newid);

		         rs.close();
				
				result = true;
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			return result;
			
			
		}else {
			return false;
		}
		
	}
	
	//選択された目標を編集する
	public static boolean editData(String userid,PlanBean pbean) {
		boolean check = false;
		
		if(pbean.getLevel().equals("big")) {
			
			String sql = "update todo_"+userid+" set title=?,content=? where id=?";
			try(Connection conn = ResourceFinder.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql);){
				pst.setString(1, pbean.getTitle());
				pst.setString(2, pbean.getContent());
				pst.setInt(3, pbean.getId());
				pst.executeUpdate();
				check = true;
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}else if(pbean.getLevel().equals("middle")) {

			//中目標のデータベース書き換え
			String sql = "update todo_"+userid+" set title=?,content=?,big=? where id=?";
			try(Connection conn = ResourceFinder.getConnection();
					PreparedStatement pst = conn.prepareStatement(sql);){
					pst.setString(1, pbean.getTitle());
					pst.setString(2, pbean.getContent());
					pst.setInt(3, pbean.getBig());
					pst.setInt(4, pbean.getId());
					pst.executeUpdate();
					
					try { //小目標の上目標を変更
						String sql2 = "update todo_"+userid+" set big=? where middle=?";
						PreparedStatement pstm = conn.prepareStatement(sql2);
						pstm.setInt(1, pbean.getBig());
						pstm.setInt(2, pbean.getId());
						pstm.executeUpdate();
						pstm.close();
						check = true;
						
					}catch(Exception e) {
						e.printStackTrace();
					}
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			
		}else if(pbean.getLevel().equals("small")) {

			String sql = "update todo_"+userid+" set title=?,content=?,big=?,middle=? where id=?";
			try(Connection conn = ResourceFinder.getConnection();
					PreparedStatement pst = conn.prepareStatement(sql);){
					pst.setString(1, pbean.getTitle());
					pst.setString(2, pbean.getContent());
					pst.setInt(3, pbean.getBig());
					pst.setInt(4, pbean.getMiddle());
					pst.setInt(5, pbean.getId());
					pst.executeUpdate();
					check = true;
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			
		}else if(pbean.getLevel().equals("sche")) {

			String sql = "update todo_"+userid+" set title=?,content=?,year=?,month=?,day=? where id=?";
			try(Connection conn = ResourceFinder.getConnection();
					PreparedStatement pst = conn.prepareStatement(sql);){
					pst.setString(1, pbean.getTitle());
					pst.setString(2, pbean.getContent());
					pst.setInt(3, pbean.getYear());
					pst.setInt(4, pbean.getMonth());
					pst.setInt(5, pbean.getDay());
					pst.setInt(6, pbean.getId());
					pst.executeUpdate();
					check = true;
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			
		}
		
		return check;
	}
	
	//選択された目標を削除する
	public static int deleteData(int id,String userid,String level,int big) { 
		//大目標入力時
		int result = 0;
		String sql = "delete from todo_"+userid+" where id = ?";
		try(Connection conn = ResourceFinder.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql);){
			pst.setInt(1, id);
			pst.executeUpdate();
			
			
			//上の目標の情報を削除してその行数を返す
			try {
				if(level.equals("big")) {
					sql = "update todo_"+userid+" set big=null,middle=null where big = ?";
					PreparedStatement pstb = conn.prepareStatement(sql);
					pstb.setInt(1,id);
					System.out.println(sql);
					result = pstb.executeUpdate();
					pstb.close();
					if(result==0) {
						result = 99999;
					}
					
				}else if(level.equals("middle")) {
					sql = "update todo_"+userid+" set big=null where big = ? and middle = ?";
					PreparedStatement pstm = conn.prepareStatement(sql);
					pstm.setInt(1, big);
					pstm.setInt(2,id);
					System.out.println(pstm);
					result = pstm.executeUpdate();
					pstm.close();
					if(result==0) {
						result=99999;
					}
					
				}else if(level.equals("small")) {
					result = 1;
				}else if(level.equals("sche")) {
					result = 1;
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	
		
	}
	

}
