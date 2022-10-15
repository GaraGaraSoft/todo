package todo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
	
	public static void checkTable(String userid) { //ログイン時にテーブルの存在をチェック、無ければ新規作成
		//ユーザーのデータテーブル
		String sql = "create table if not exists "
				+ "todo_"+userid
				+ "(id int auto_increment primary key,\n"
				+ "title text,\n"
				+ "content text,\n"
				+ "level varchar(10),\n"
				+ "big int,\n"
				+ "middle int,\n"
				+ "date date,\n"
				+ "hold boolean,\n"
				+ "important tinyint unsigned\n"
				+ ");";
		
		System.out.println("SQL:"+sql);
		
		try(Connection conn = ResourceFinder.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql);){
			
			pst.executeUpdate();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		//ユーザーのログテーブル
		sql = "create table if not exists log_"+userid+"(\n"
				+ "log_id int auto_increment primary key,\n"
				+ "id int,\n"
				+ "hold boolean,\n"
				+ "ope varchar(10),\n"
				+ "before_title text,\n"
				+ "before_content text,\n"
				+ "before_level varchar(10),\n"
				+ "before_big int,\n"
				+ "before_middle int,\n"
				+ "before_date date,\n"
				+ "before_important tinyint unsigned,\n"
				+ "after_title text,\n"
				+ "after_content text,\n"
				+ "after_level varchar(10),\n"
				+ "after_big int,\n"
				+ "after_middle int,\n"
				+ "after_date date,\n"
				+ "after_important tinyint unsigned\n"
				+ ");";
		
		System.out.println("SQL2:"+sql);
		
		try(Connection conn = ResourceFinder.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql);){
			
			pst.executeUpdate();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return;
	}
	
	public static boolean getList(ArrayList<PlanBean> bigarray,ArrayList<PlanBean> middlearray,ArrayList<PlanBean> smallarray,ArrayList<PlanBean> todayarray,ArrayList<PlanBean> weekarray,ArrayList<PlanBean> schearray,ToDoBean tdbean,String userid) {
		//ユーザーのデータテーブルを取得して設定
		boolean result = false;
		String table = "todo_"+userid; //ユーザーテーブル名
		String  sql = "select * from "+table+" order by level,id";
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
				pbean.setDate(rs.getString("date"));
				pbean.setHold(rs.getBoolean("hold"));
				pbean.setImportant(rs.getInt("important"));
				
				//各データのセッション配列に登録
				if(rs.getString("level").equals("big")) {
					bigarray.add(pbean);
				}else if(rs.getString("level").equals("middle")) {
					//大目標のタイトルを取得してセッション配列に登録
					for(PlanBean b:bigarray) {
						if(pbean.getBig() == b.getId()) {
							pbean.setBig_title(b.getTitle());
						System.out.println("大"+b.getTitle());
						break;
						}
					}
					
					middlearray.add(pbean);
				}else if(rs.getString("level").equals("small")) {
					//大目標のタイトルを取得してセッション配列に登録
					for(PlanBean b:bigarray) {
						if(pbean.getBig() == b.getId()) {
							pbean.setBig_title(b.getTitle());
						System.out.println("大"+b.getTitle());
						break;
						}
					}

					//中目標のタイトルを取得してセッション配列に登録
					for(PlanBean m:middlearray) {
						if(pbean.getMiddle() == m.getId()) {
					System.out.println("中:"+m.getId());
							pbean.setMiddle_title(m.getTitle());
							System.out.println("中"+m.getTitle());
						break;
						}
					}
					smallarray.add(pbean);
				}else if(rs.getString("level").equals("sche")) {
					
					String year = pbean.getDate().substring(0,4);
					String month = pbean.getDate().substring(5,7);
					String day = pbean.getDate().substring(8,10);
					String date = year + "-" + month + "-" + day + " 00:00:00";
					
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							Calendar cal1 = Calendar.getInstance();
							Calendar cal2 = Calendar.getInstance();
							Calendar cal3 = Calendar.getInstance();
							
							cal1.set(Calendar.HOUR_OF_DAY, 0);
							cal1.set(Calendar.MINUTE, 0);
							cal1.set(Calendar.SECOND, 0);
							cal1.set(Calendar.MILLISECOND, 0);
							cal1.add(Calendar.DAY_OF_MONTH, 1);
							Date d1 = cal1.getTime(); //当日の1日後のDateオブジェクト
				
							cal2.set(Calendar.HOUR_OF_DAY, 0);
							cal2.set(Calendar.MINUTE, 0);
							cal2.set(Calendar.SECOND, 0);
							cal2.set(Calendar.MILLISECOND, 0);
							cal2.add(Calendar.DAY_OF_MONTH, 7);
							Date d2 = cal2.getTime(); //一週間後のDateオブジェクト
		        
					        Date d3 = null;
							try {
					        d3 = sdf.parse(date); //スケジュールの日付のDateオブジェクト
							}catch(ParseException e) {
								e.printStackTrace();
							}
					
					
					Calendar cal = Calendar.getInstance();
					if(
						    Integer.parseInt(pbean.getDate().substring(0,4))  == cal.get(Calendar.YEAR) &&
						    Integer.parseInt(pbean.getDate().substring(5,7)) == cal.get(Calendar.MONTH)+1 &&
						    Integer.parseInt(pbean.getDate().substring(8,10)) == cal.get(Calendar.DATE)) {
						todayarray.add(pbean);
						schearray.add(pbean);
					}else if(d1.equals(d3) || d2.equals(d3) || d3.after(d1) && d3.before(d2)){
						weekarray.add(pbean);
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
	
	public static boolean getLog(ArrayList<PlanBean> bigarray,ArrayList<PlanBean> middlearray,ArrayList<LogBean> logarray,String userid) {
		boolean result = false;
		String table = "log_"+userid; //編集ログテーブル名
		String  sql = "select * from "+table+" order by log_id";
		System.out.println(sql);
		try(Connection conn = ResourceFinder.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql);){
			ResultSet rs = pst.executeQuery(); //データテーブルからrsに取得
			while(rs.next()) {
//				System.out.println(rs.getInt("id"));
				LogBean lbean = new LogBean();
				lbean.setLogid(rs.getInt("log_id"));
				lbean.setId(rs.getInt("id"));
				lbean.setHold(rs.getBoolean("hold"));
				lbean.setOpe(rs.getString("ope"));
				lbean.setBefore_title(rs.getString("before_title"));
				lbean.setBefore_content(rs.getString("before_content"));
				lbean.setBefore_level(rs.getString("before_level"));
				lbean.setBefore_big(rs.getInt("before_big"));
				lbean.setBefore_middle(rs.getInt("before_middle"));
				lbean.setBefore_date(rs.getString("before_date"));
				lbean.setBefore_important(rs.getInt("before_important"));
				lbean.setAfter_title(rs.getString("after_title"));
				lbean.setAfter_content(rs.getString("after_content"));
				lbean.setAfter_level(rs.getString("after_level"));
				lbean.setAfter_big(rs.getInt("after_big"));
				lbean.setAfter_middle(rs.getInt("after_middle"));
				lbean.setAfter_date(rs.getString("after_date"));
				lbean.setAfter_important(rs.getInt("after_important"));
				
				System.out.println("aaaa");
				//大目標のタイトルを取得してセッション配列に登録
				if(lbean.getOpe().equals("insert")) {
					if(lbean.getAfter_level().equals("middle") || lbean.getAfter_level().equals("small")) {
						boolean afterbig = false;
						for(PlanBean b:bigarray) {
							if(lbean.getAfter_big()== b.getId()) {
								lbean.setAfter_big_title(b.getTitle());
								afterbig = true;
								break;
							}
						}
						if(afterbig == false) {
							lbean.setAfter_big_title("-");
						}
					}
				}else if(lbean.getOpe().equals("delete")) {
					if(lbean.getBefore_level().equals("middle") || lbean.getBefore_level().equals("small")) {
						boolean beforebig = false;
						for(PlanBean b:bigarray) {
							if(lbean.getBefore_big() == b.getId()) {
								lbean.setBefore_big_title(b.getTitle());
								beforebig = true;
								break;
							}
						}
						if(beforebig == false) {
							lbean.setBefore_big_title("-");
						}
					}
				}else if(lbean.getOpe().equals("update")) {
				if(lbean.getBefore_level().equals("middle") || lbean.getBefore_level().equals("small") || lbean.getAfter_level().equals("middle") || lbean.getAfter_level().equals("small")) {
					boolean beforebig = false;
					boolean afterbig = false;
					for(PlanBean b:bigarray) {
						if(lbean.getBefore_big() == b.getId()) {
							lbean.setBefore_big_title(b.getTitle());
							beforebig = true;
						}
						if(lbean.getAfter_big()== b.getId()) {
							lbean.setAfter_big_title(b.getTitle());
							afterbig = true;
						}
					}
					if(beforebig == false) {
						lbean.setBefore_big_title("-");
					}
					if(afterbig == false) {
						lbean.setAfter_big_title("-");
					}
				}
					
				}
				
				//中目標のタイトルを取得してセッション配列に登録
				if(lbean.getOpe().equals("delete")) {
					if(lbean.getBefore_level().equals("small")) {
						boolean beforemiddle = false;
						for(PlanBean m:middlearray) {
							if(lbean.getBefore_middle() == m.getId()) {
								lbean.setBefore_middle_title(m.getTitle());
								beforemiddle = true;
								break;
							}
						}
						if(beforemiddle == false) {
							lbean.setBefore_middle_title("-");
						}
					}
					
				}else if(lbean.getOpe().equals("update")){
					if(lbean.getBefore_level().equals("small") || lbean.getAfter_level().equals("small")) {
						boolean beforemiddle = false;
						boolean aftermiddle = false;
						for(PlanBean m:middlearray) {
							if(lbean.getBefore_middle() == m.getId()) {
								lbean.setBefore_middle_title(m.getTitle());
								beforemiddle = true;
							}
							if(lbean.getAfter_middle() == m.getId()) {
								lbean.setAfter_middle_title(m.getTitle());
								aftermiddle = true;
							}
						}
						if(beforemiddle == false) {
							lbean.setBefore_middle_title("-");
						}
						if(aftermiddle == false) {
							lbean.setAfter_middle_title("-");
						}
					}
					
				}else if(lbean.getOpe().equals("insert")) {
					if(lbean.getAfter_level().equals("small")) {
						boolean aftermiddle = false;
						for(PlanBean m:middlearray) {
							if(lbean.getAfter_middle() == m.getId()) {
								lbean.setAfter_middle_title(m.getTitle());
								aftermiddle = true;
								break;
							}
						}
						if(aftermiddle == false) {
							lbean.setAfter_middle_title("-");
						}
					}
					
				}
				logarray.add(lbean);
				
			}
			rs.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
		return result;
	}
	
	//新しい目標、予定を登録するメソッド
	public static boolean setNewData(PlanBean bean,LogBean logbean,String userid) {
		boolean result = false;
		
		if(bean.getLevel().equals("big")) { 
			//大目標入力時のデータを登録
			String sql = "insert into todo_"+userid+"(title,content,level) values(?,?,?)";
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
		         
		         rs.close();

					//大目標入力時のログを登録
		         sql = "insert into log_"+userid+"(id,ope,after_title,after_content,after_level) values(?,?,?,?,?)";
		         try(PreparedStatement pstl = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
		        	 pstl.setInt(1, bean.getId());
		        	 pstl.setString(2, logbean.getOpe());
		        	 pstl.setString(3, logbean.getAfter_title());
		        	 pstl.setString(4, logbean.getAfter_content());
		        	 pstl.setString(5, logbean.getAfter_level());
		        	 pstl.executeUpdate();
		        	 
			         // 新しく登録したデータのログidを取り出す
			         ResultSet lrs = pstl.getGeneratedKeys();
			         int newlogid=0;
			         
			         if(lrs.next()){
			             newlogid = lrs.getInt(1);
			         }

			         logbean.setLogid(newlogid);
			         System.out.println(newlogid);

			         lrs.close();
		        	 
				result = true;
		         }catch(Exception e) {
		        	 e.printStackTrace();
		         }
		         
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			
		}else if(bean.getLevel().equals("middle")) {
			//中目標入力時
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
		         
		         sql = "insert into log_"+userid+"(id,ope,after_title,after_content,after_level,after_big) values(?,?,?,?,?,?)";
		         try(PreparedStatement pstl = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
		        	 pstl.setInt(1, bean.getId());
		        	 pstl.setString(2, logbean.getOpe());
		        	 pstl.setString(3, logbean.getAfter_title());
		        	 pstl.setString(4, logbean.getAfter_content());
		        	 pstl.setString(5, logbean.getAfter_level());
		        	 pstl.setInt(6, logbean.getAfter_big());
		        	 pstl.executeUpdate();
		        	 
			         // 新しく登録したデータのログidを取り出す
			         ResultSet lrs = pstl.getGeneratedKeys();
			         int newlogid=0;
			         
			         if(lrs.next()){
			             newlogid = lrs.getInt(1);
			         }

			         logbean.setLogid(newlogid);
			         System.out.println(newlogid);

			         lrs.close();
		        	 
				result = true;
		         }catch(Exception e) {
		        	 e.printStackTrace();
		         }
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}else if(bean.getLevel().equals("small")) { 
			//小目標入力時
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

		         sql = "insert into log_"+userid+"(id,ope,after_title,after_content,after_level,after_big,after_middle) values(?,?,?,?,?,?,?)";
		         try(PreparedStatement pstl = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
		        	 pstl.setInt(1, bean.getId());
		        	 pstl.setString(2, logbean.getOpe());
		        	 pstl.setString(3, logbean.getAfter_title());
		        	 pstl.setString(4, logbean.getAfter_content());
		        	 pstl.setString(5, logbean.getAfter_level());
		        	 pstl.setInt(6, logbean.getAfter_big());
		        	 pstl.setInt(7, logbean.getAfter_middle());
		        	 pstl.executeUpdate();
		        	 

			         // 新しく登録したデータのログidを取り出す
			         ResultSet lrs = pstl.getGeneratedKeys();
			         int newlogid=0;
			         
			         if(lrs.next()){
			             newlogid = lrs.getInt(1);
			         }

			         logbean.setLogid(newlogid);
			         System.out.println(newlogid);

			         lrs.close();
		        	 
				result = true;
		         }catch(Exception e) {
		        	 e.printStackTrace();
		         }
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}else if(bean.getLevel().equals("sche")) { 
			//スケジュール入力時
			String sql = "insert into todo_"+userid+"(title,content,level,date) values(?,?,?,?)";
			try (Connection conn = ResourceFinder.getConnection();
					PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
				pst.setString(1, bean.getTitle());
				pst.setString(2, bean.getContent());
				pst.setString(3, bean.getLevel());
				pst.setString(4, bean.getDate());
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
				
		         
		         sql = "insert into log_"+userid+"(id,ope,after_title,after_content,after_level,after_date) values(?,?,?,?,?,?)";
		         try(PreparedStatement pstl = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
		        	 pstl.setInt(1, bean.getId());
		        	 pstl.setString(2, logbean.getOpe());
		        	 pstl.setString(3, logbean.getAfter_title());
		        	 pstl.setString(4, logbean.getAfter_content());
		        	 pstl.setString(5, logbean.getAfter_level());
		        	 pstl.setString(6, logbean.getAfter_date());
		        	 pstl.executeUpdate();

			         // 新しく登録したデータのログidを取り出す
			         ResultSet lrs = pstl.getGeneratedKeys();
			         int newlogid=0;
			         
			         if(lrs.next()){
			             newlogid = lrs.getInt(1);
			         }

			         logbean.setLogid(newlogid);
			         System.out.println(newlogid);

			         lrs.close();
				result = true;
		         }catch(Exception e) {
		        	 e.printStackTrace();
		         }
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			
		}
		
		try(Connection conn = ResourceFinder.getConnection();
					Statement st = conn.createStatement()){
			String sql = "select count(*) from log_"+userid; //ログの件数を取得
			ResultSet rs = st.executeQuery(sql);
			rs.next();
			int num = rs.getInt("count(*)");
			rs.close();
			
			if(num > 100) {//ログ件数が100件を超えたら古い方から削除
				
				sql = "delete from log_"+userid+" order by log_id limit "+(num-100);
				try(Statement dst = conn.createStatement();){
					dst.executeUpdate(sql);
				}catch(Exception e) {
					e.printStackTrace();
				}
				
				
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	//選択された目標を編集する
	public static boolean editData(String userid,PlanBean pbean,LogBean logbean) {
		boolean check = false;
		
		if(pbean.getLevel().equals("big")) {
			
			String sql = "update todo_"+userid+" set title=?,content=? where id=?";
			try(Connection conn = ResourceFinder.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql);){
				pst.setString(1, pbean.getTitle());
				pst.setString(2, pbean.getContent());
				pst.setInt(3, pbean.getId());
				pst.executeUpdate();
				

		         sql = "insert into log_"+userid+"(id,ope,before_title,before_content,before_level,after_title,after_content,after_level) values(?,?,?,?,?,?,?,?)";
		         try(PreparedStatement pstl = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
		        	 pstl.setInt(1, logbean.getId());
		        	 pstl.setString(2, logbean.getOpe());
		        	 pstl.setString(3, logbean.getBefore_title());
		        	 pstl.setString(4, logbean.getBefore_content());
		        	 pstl.setString(5, logbean.getBefore_level());
		        	 pstl.setString(6, logbean.getAfter_title());
		        	 pstl.setString(7, logbean.getAfter_content());
		        	 pstl.setString(8, logbean.getAfter_level());
		        	 pstl.executeUpdate();

			         // 新しく登録したデータのログidを取り出す
			         ResultSet lrs = pstl.getGeneratedKeys();
			         int newlogid=0;
			         
			         if(lrs.next()){
			             newlogid = lrs.getInt(1);
			         }

			         logbean.setLogid(newlogid);
			         System.out.println(newlogid);

			         lrs.close();
		        	 check = true;
		         }catch(Exception e) {
		        	 e.printStackTrace();
		         }
				
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

						//削除したデータをログ情報に入力
				         sql = "insert into log_"+userid+"(id,ope,before_title,before_content,before_level,before_big,after_title,after_content,after_level,after_big) values(?,?,?,?,?,?,?,?,?,?)";
				         try(PreparedStatement pstl = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
				        	 pstl.setInt(1, logbean.getId());
				        	 pstl.setString(2, logbean.getOpe());
				        	 pstl.setString(3, logbean.getBefore_title());
				        	 pstl.setString(4, logbean.getBefore_content());
				        	 pstl.setString(5, logbean.getBefore_level());
				        	 pstl.setInt(6, logbean.getBefore_big());
				        	 pstl.setString(7, logbean.getAfter_title());
				        	 pstl.setString(8, logbean.getAfter_content());
				        	 pstl.setString(9, logbean.getAfter_level());
				        	 pstl.setInt(10, logbean.getAfter_big());
				        	 pstl.executeUpdate();
				        	 
				        	// 新しく登録したデータのログidを取り出す
					         ResultSet lrs = pstl.getGeneratedKeys();
					         int newlogid=0;
					         
					         if(lrs.next()){
					             newlogid = lrs.getInt(1);
					         }

					         logbean.setLogid(newlogid);
					         System.out.println(newlogid);

					         lrs.close();
				        	 
				        	 check = true;
				         }catch(Exception e) {
				        	 e.printStackTrace();
				         }
						
						
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
					
					//削除したデータをログ情報に入力
			         sql = "insert into log_"+userid+"(id,ope,before_title,before_content,before_level,before_big,before_middle,after_title,after_content,after_level,after_big,after_middle) values(?,?,?,?,?,?,?,?,?,?,?,?)";
			         try(PreparedStatement pstl = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
			        	 pstl.setInt(1, logbean.getId());
			        	 pstl.setString(2, logbean.getOpe());
			        	 pstl.setString(3, logbean.getBefore_title());
			        	 pstl.setString(4, logbean.getBefore_content());
			        	 pstl.setString(5, logbean.getBefore_level());
			        	 pstl.setInt(6, logbean.getBefore_big());
			        	 pstl.setInt(7, logbean.getBefore_middle());
			        	 pstl.setString(8, logbean.getAfter_title());
			        	 pstl.setString(9, logbean.getAfter_content());
			        	 pstl.setString(10, logbean.getAfter_level());
			        	 pstl.setInt(11, logbean.getAfter_big());
			        	 pstl.setInt(12, logbean.getAfter_middle());
			        	 pstl.executeUpdate();
			        	 
			        	// 新しく登録したデータのログidを取り出す
				         ResultSet lrs = pstl.getGeneratedKeys();
				         int newlogid=0;
				         
				         if(lrs.next()){
				             newlogid = lrs.getInt(1);
				         }

				         logbean.setLogid(newlogid);
				         System.out.println(newlogid);

				         lrs.close();
			        	 
			        	 check = true;
			         }catch(Exception e) {
			        	 e.printStackTrace();
			         }
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			
		}else if(pbean.getLevel().equals("sche")) {

			String sql = "update todo_"+userid+" set title=?,content=?,date=? where id=?";
			try(Connection conn = ResourceFinder.getConnection();
					PreparedStatement pst = conn.prepareStatement(sql);){
					pst.setString(1, pbean.getTitle());
					pst.setString(2, pbean.getContent());
					pst.setString(3, pbean.getDate());
					pst.setInt(4, pbean.getId());
					pst.executeUpdate();
					
					//削除したデータをログ情報に入力
			         sql = "insert into log_"+userid+"(id,ope,before_title,before_content,before_level,before_date,after_title,after_content,after_level,after_date) values(?,?,?,?,?,?,?,?,?,?)";
			         try(PreparedStatement pstl = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
			        	 pstl.setInt(1, logbean.getId());
			        	 pstl.setString(2, logbean.getOpe());
			        	 pstl.setString(3, logbean.getBefore_title());
			        	 pstl.setString(4, logbean.getBefore_content());
			        	 pstl.setString(5, logbean.getBefore_level());
			        	 pstl.setString(6, logbean.getBefore_date());
			        	 pstl.setString(7, logbean.getAfter_title());
			        	 pstl.setString(8, logbean.getAfter_content());
			        	 pstl.setString(9, logbean.getAfter_level());
			        	 pstl.setString(10, logbean.getAfter_date());
			        	 pstl.executeUpdate();
			        	 

				         // 新しく登録したデータのログidを取り出す
				         ResultSet lrs = pstl.getGeneratedKeys();
				         int newlogid=0;
				         
				         if(lrs.next()){
				             newlogid = lrs.getInt(1);
				         }

				         logbean.setLogid(newlogid);
				         System.out.println(newlogid);

				         lrs.close();
			        	 
			        	 check = true;
			         }catch(Exception e) {
			        	 e.printStackTrace();
			         }
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			
		}
		
			try(Connection conn = ResourceFinder.getConnection();
					Statement st = conn.createStatement()){
			String sql = "select count(*) from log_"+userid; //ログの件数を取得
			ResultSet rs = st.executeQuery(sql);
			rs.next();
			int num = rs.getInt("count(*)");
			rs.close();
			
			if(num > 100) {//ログ件数が100件を超えたら古い方から削除
				
				sql = "delete from log_"+userid+" order by log_id limit "+(num-100);
				try(Statement dst = conn.createStatement();){
					dst.executeUpdate(sql);
				}catch(Exception e) {
					e.printStackTrace();
				}
				
				
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return check;
	}
	
	//選択された目標を削除する
	public static int deleteData(int id,String userid,String level,int big,LogBean logbean) { 
		//大目標入力時
		int result = 0;
		String sql = "delete from todo_"+userid+" where id = ?";
		try(Connection conn = ResourceFinder.getConnection();
				PreparedStatement pst = conn.prepareStatement(sql);){
			pst.setInt(1, id);
			pst.executeUpdate();
			
			
			try {
				if(level.equals("big")) {
					//下位目標から大目標を削除して行数を返す
					sql = "update todo_"+userid+" set big=null,middle=null where big = ?";
					PreparedStatement pstb = conn.prepareStatement(sql);
					pstb.setInt(1,id);
					System.out.println(sql);
					result = pstb.executeUpdate();
					pstb.close();
					if(result==0) {
						result = 99999;
					}
					
					//削除したデータをログ情報に入力
			         sql = "insert into log_"+userid+"(id,ope,before_title,before_content,before_level) values(?,?,?,?,?)";
			         try(PreparedStatement pstl = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
			        	 pstl.setInt(1, logbean.getId());
			        	 pstl.setString(2, logbean.getOpe());
			        	 pstl.setString(3, logbean.getBefore_title());
			        	 pstl.setString(4, logbean.getBefore_content());
			        	 pstl.setString(5, logbean.getBefore_level());
			        	 pstl.executeUpdate();
			        	 
				         // 新しく登録したデータのログidを取り出す
				         ResultSet lrs = pstl.getGeneratedKeys();
				         int newlogid=0;
				         
				         if(lrs.next()){
				             newlogid = lrs.getInt(1);
				         }

				         logbean.setLogid(newlogid);
				         System.out.println(newlogid);

				         lrs.close();
			        	 
			         }catch(Exception e) {
			        	 e.printStackTrace();
			         }
			         
					
				}else if(level.equals("middle")) {
					//下位目標から中目標を削除して行数を返す
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

					//削除したデータをログ情報に入力
			         sql = "insert into log_"+userid+"(id,ope,before_title,before_content,before_level,before_big) values(?,?,?,?,?,?)";
			         try(PreparedStatement pstl = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
			        	 pstl.setInt(1, logbean.getId());
			        	 pstl.setString(2, logbean.getOpe());
			        	 pstl.setString(3, logbean.getBefore_title());
			        	 pstl.setString(4, logbean.getBefore_content());
			        	 pstl.setString(5, logbean.getBefore_level());
			        	 pstl.setInt(6, logbean.getBefore_big());
			        	 pstl.executeUpdate();
			        	 
				         // 新しく登録したデータのログidを取り出す
				         ResultSet lrs = pstl.getGeneratedKeys();
				         int newlogid=0;
				         
				         if(lrs.next()){
				             newlogid = lrs.getInt(1);
				         }

				         logbean.setLogid(newlogid);
				         System.out.println(newlogid);

				         lrs.close();
			        	 
			         }catch(Exception e) {
			        	 e.printStackTrace();
			         }
					
				}else if(level.equals("small")) {
					result = 1;

					//削除したデータをログ情報に入力
			         sql = "insert into log_"+userid+"(id,ope,before_title,before_content,before_level,before_big,before_middle) values(?,?,?,?,?,?,?)";
			         try(PreparedStatement pstl = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
			        	 pstl.setInt(1, logbean.getId());
			        	 pstl.setString(2, logbean.getOpe());
			        	 pstl.setString(3, logbean.getBefore_title());
			        	 pstl.setString(4, logbean.getBefore_content());
			        	 pstl.setString(5, logbean.getBefore_level());
			        	 pstl.setInt(6, logbean.getBefore_big());
			        	 pstl.setInt(7, logbean.getBefore_middle());
			        	 pstl.executeUpdate();
			        	 
				         // 新しく登録したデータのログidを取り出す
				         ResultSet lrs = pstl.getGeneratedKeys();
				         int newlogid=0;
				         
				         if(lrs.next()){
				             newlogid = lrs.getInt(1);
				         }

				         logbean.setLogid(newlogid);
				         System.out.println(newlogid);

				         lrs.close();
			        	 
			         }catch(Exception e) {
			        	 e.printStackTrace();
			         }
					
				}else if(level.equals("sche")) {
					result = 1;
					
			         sql = "insert into log_"+userid+"(id,ope,before_title,before_content,before_level,before_date) values(?,?,?,?,?,?)";
			         try(PreparedStatement pstl = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);){
			        	 pstl.setInt(1, logbean.getId());
			        	 pstl.setString(2, logbean.getOpe());
			        	 pstl.setString(3, logbean.getBefore_title());
			        	 pstl.setString(4, logbean.getBefore_content());
			        	 pstl.setString(5, logbean.getBefore_level());
			        	 pstl.setString(6, logbean.getBefore_date());
			        	 pstl.executeUpdate();

				         // 新しく登録したデータのログidを取り出す
				         ResultSet lrs = pstl.getGeneratedKeys();
				         int newlogid=0;
				         
				         if(lrs.next()){
				             newlogid = lrs.getInt(1);
				         }

				         logbean.setLogid(newlogid);
				         System.out.println(newlogid);

				         lrs.close();
			        	 
			         }catch(Exception e) {
			        	 e.printStackTrace();
			         }
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		try(Connection conn = ResourceFinder.getConnection();
					Statement st = conn.createStatement()){
			sql = "select count(*) from log_"+userid; //ログの件数を取得
			ResultSet rs = st.executeQuery(sql);
			rs.next();
			int num = rs.getInt("count(*)");
			rs.close();
			
			if(num > 100) {//ログ件数が100件を超えたら古い方から削除
				
				sql = "delete from log_"+userid+" order by log_id limit "+(num-100);
				try(Statement dst = conn.createStatement();){
					dst.executeUpdate(sql);
				}catch(Exception e) {
					e.printStackTrace();
				}
				
				
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	
		
	}
	

}
