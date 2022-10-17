package todo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class EditController
 */
@WebServlet("/EditController")
public class EditController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		//セッションからログイン情報を取得
		HttpSession session = request.getSession();
		LoginBean lbean = (LoginBean) session.getAttribute("loginbean");
		
		//編集,削除するデータのid
		int id = Integer.parseInt(request.getParameter("id"));
		
		if(request.getParameter("action").equals("del")) {

			//削除するデータの目標レベル
			String level = request.getParameter("level");
			/*			//削除するデータの大目標データ
						int big = 0;
						if(level.equals("middle")) {
							@SuppressWarnings("unchecked")
							ArrayList<PlanBean> middleArray = (ArrayList<PlanBean>) session.getAttribute("middlearray");
							for(PlanBean pbean:middleArray) {
								if(id == pbean.getId()) {
									big = pbean.getBig();
									break;
								}
							}
							
							
						}else if(level.equals("small")) {
							@SuppressWarnings("unchecked")
							ArrayList<PlanBean> smallArray = (ArrayList<PlanBean>) session.getAttribute("smallarray");
							for(PlanBean pbean:smallArray) {
								if(id == pbean.getId()) {
									big = pbean.getBig();
									break;
								}
							}
							
						}*/
			
			//削除前のデータを取得してLogBeanへ設定
			LogBean logbean = new LogBean();
			if(level.equals("big")) {
				@SuppressWarnings("unchecked")
				ArrayList<PlanBean> bigArray = (ArrayList<PlanBean>) session.getAttribute("bigarray");
				for(PlanBean pbean:bigArray) {
					if(id == pbean.getId()) {
					logbean.setId(id);
					logbean.setOpe("delete");
					logbean.setBefore_title(pbean.getTitle());
					logbean.setBefore_content(pbean.getContent());
					logbean.setBefore_level(level);
					logbean.setHold(pbean.isHold());
					break;
					}
				}
				
			}else if(level.equals("middle")) {
				@SuppressWarnings("unchecked")
				ArrayList<PlanBean> middleArray = (ArrayList<PlanBean>) session.getAttribute("middlearray");
				for(PlanBean pbean:middleArray) {
					if(id == pbean.getId()) {
					logbean.setId(id);
					logbean.setOpe("delete");
					logbean.setBefore_title(pbean.getTitle());
					logbean.setBefore_content(pbean.getContent());
					logbean.setBefore_level(level);
					logbean.setBefore_big(pbean.getBig());
					logbean.setBefore_big_title(pbean.getBig_title());
					logbean.setHold(pbean.isHold());
					break;
					}
				}
				
			}else if(level.equals("small")) {
				@SuppressWarnings("unchecked")
				ArrayList<PlanBean> smallArray = (ArrayList<PlanBean>) session.getAttribute("smallarray");
				for(PlanBean pbean:smallArray) {
					if(id == pbean.getId()) {
					logbean.setId(id);
					logbean.setOpe("delete");
					logbean.setBefore_title(pbean.getTitle());
					logbean.setBefore_content(pbean.getContent());
					logbean.setBefore_level(level);
					logbean.setBefore_big(pbean.getBig());
					logbean.setBefore_big_title(pbean.getBig_title());
					logbean.setBefore_middle(pbean.getMiddle());
					logbean.setBefore_middle_title(pbean.getMiddle_title());
					logbean.setHold(pbean.isHold());
						break;
					}
				}
				
			}else if(level.equals("sche")) {
				@SuppressWarnings("unchecked")
				ArrayList<PlanBean> scheArray = (ArrayList<PlanBean>) session.getAttribute("schearray");
				for(PlanBean pbean:scheArray) {
					if(id == pbean.getId()) {
					logbean.setId(id);
					logbean.setOpe("delete");
					logbean.setBefore_title(pbean.getTitle());
					logbean.setBefore_content(pbean.getContent());
					logbean.setBefore_level(level);
					logbean.setBefore_date(pbean.getDate());
					logbean.setHold(pbean.isHold());
						break;
					}
				}
			}
			
			
			//idの登録データを削除
			int result = SQLOperator.deleteData(id,lbean.getUserid(),level,logbean);
			
			
			//削除前のログをセッション情報に設定
			@SuppressWarnings("unchecked")
			ArrayList<LogBean> logArray = (ArrayList<LogBean>) session.getAttribute("logarray");
			Collections.reverse(logArray);
			logArray.add(logbean);
			session.setAttribute("logarray", logArray);
			
			//データベース削除成功時、セッション情報からもデータを削除する
			if(result>0) {
				
				if(level.equals("big")) {
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> bigArray = (ArrayList<PlanBean>) session.getAttribute("bigarray");
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> middleArray = (ArrayList<PlanBean>) session.getAttribute("middlearray");
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> smallArray = (ArrayList<PlanBean>) session.getAttribute("smallarray");
					//セッションの大目標からデータを削除
					for(int i=0;i<bigArray.size();i++) {
						if(id == bigArray.get(i).getId()) {
							bigArray.remove(i);
							break;
						}
					}
					//セッション中、小目標から上位データを削除
					
					for(int i=0;i<middleArray.size();i++) {
						if(id == middleArray.get(i).getBig()) {
							middleArray.get(i).setBig(0);
							middleArray.get(i).setBig_title("");
							middleArray.get(i).setHold(true); //上位データ削除時、中位は保留に
							result--;
							
						}
						if(result==0 || result==99999) {
							break;
						}
					}
					for(int i=0;i<smallArray.size();i++) {
						if(id == smallArray.get(i).getBig()) {
							smallArray.get(i).setBig(0);
							smallArray.get(i).setBig_title("");
							smallArray.get(i).setHold(true); //上位データ削除時、下位も保留に
							result--;
						}
						if(result==0 || result==99999) {
							break;
						}
					}

					session.setAttribute("smallarray", smallArray);
					session.setAttribute("middlearray", middleArray);
					session.setAttribute("bigarray", bigArray);
					
				}else if(level.equals("middle")) {
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> middleArray = (ArrayList<PlanBean>) session.getAttribute("middlearray");
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> smallArray = (ArrayList<PlanBean>) session.getAttribute("smallarray");
					
					for(int i=0;i<middleArray.size();i++) {
						if(id == middleArray.get(i).getId()) {
							middleArray.remove(i);
							break;
						}
					}

					for(int i=0;i<smallArray.size();i++) {
						if(id == smallArray.get(i).getMiddle()) {
							smallArray.get(i).setBig(0);
							smallArray.get(i).setMiddle(0);
							smallArray.get(i).setBig_title("");
							smallArray.get(i).setMiddle_title("");
							smallArray.get(i).setHold(true); //上位データ削除時、下位を保留に
							result--;
						}
						if(result==0 || result==99999) {
							break;
						}
					}
					session.setAttribute("smallarray", smallArray);
					session.setAttribute("middlearray", middleArray);
					
				}else if(level.equals("small")) {
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> smallArray = (ArrayList<PlanBean>) session.getAttribute("smallarray");
					for(int i=0;i<smallArray.size();i++) {
						if(id == smallArray.get(i).getId()) {
							smallArray.remove(i);
							break;
						}
					}
					session.setAttribute("smallarray", smallArray);
					
				}else if(level.equals("sche")) {
					
					

					//削除するスケジュールのセッション情報
					PlanBean sbean = null;
						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> scheArray = (ArrayList<PlanBean>) session.getAttribute("schearray");
						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> weekArray = (ArrayList<PlanBean>) session.getAttribute("weekarray");
						for(int i=0;i<scheArray.size();i++) {
							if(id == scheArray.get(i).getId()) {
								sbean = scheArray.get(i);
								scheArray.remove(i); //スケジュールのセッション削除
								break;
							}
						}
						for(int i=0;i<weekArray.size();i++) {
							if(id == weekArray.get(i).getId()) {
								weekArray.remove(i);
								break;
							}
						}
						session.setAttribute("weekarray", weekArray);
						session.setAttribute("schearray", scheArray);
						
						//削除するスケジュールが当日なら当日分も取得
						Calendar cal = Calendar.getInstance();
						if(
							Integer.parseInt(sbean.getDate().substring(0,4))  == cal.get(Calendar.YEAR) &&
							Integer.parseInt(sbean.getDate().substring(5,7)) == cal.get(Calendar.MONTH)+1 &&
							Integer.parseInt(sbean.getDate().substring(8,10)) == cal.get(Calendar.DATE)) { 
							//当日のセッション削除
							@SuppressWarnings("unchecked")
							ArrayList<PlanBean> todayArray = (ArrayList<PlanBean>) session.getAttribute("todayarray");
							
							for(int i=0;i<todayArray.size();i++) {
								if(id == todayArray.get(i).getId()) {
									todayArray.remove(i);
									break;
								}
							}
							session.setAttribute("todayarray", todayArray);
							
						}
						
						
					
				}
				
				
			}
			

			//トップ画面にフォワード処理
			ServletContext application = getServletContext();
			RequestDispatcher rd = application.getRequestDispatcher("/jsp/top.jsp");
			rd.forward(request, response);
			
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		//セッションからログイン情報を取得
		HttpSession session = request.getSession();
		LoginBean lbean = (LoginBean) session.getAttribute("loginbean");
		
		//編集,削除するデータのid
		int id = Integer.parseInt(request.getParameter("id"));
		
		
		if(request.getParameter("action").equals("edi")) {
		
			@SuppressWarnings("unchecked")
			ArrayList<PlanBean> bigArray = (ArrayList<PlanBean>) session.getAttribute("bigarray");
			@SuppressWarnings("unchecked")
			ArrayList<PlanBean> middleArray = (ArrayList<PlanBean>) session.getAttribute("middlearray");
			@SuppressWarnings("unchecked")
			ArrayList<PlanBean> smallArray = (ArrayList<PlanBean>) session.getAttribute("smallarray");
			@SuppressWarnings("unchecked")
			ArrayList<PlanBean> scheArray = (ArrayList<PlanBean>) session.getAttribute("schearray");
			@SuppressWarnings("unchecked")
			ArrayList<PlanBean> weekArray = (ArrayList<PlanBean>) session.getAttribute("weekarray");
			
			//編集データをpbeanデータ化
			PlanBean pbean = new PlanBean();
			pbean.setId(id);
			
			//編集前、編集後のログデータを設定するLogBeanオブジェクト作成
			LogBean logbean = new LogBean();
			
			//編集するデータの目標レベル
			String level = request.getParameter("level");
			pbean.setLevel(level);
			
			boolean hold = false; //編集データの保留状態
			//保留状態にチェックが入っていたらholdをtrueに
			if(request.getParameterValues("hold").length==1) {
				if(request.getParameterValues("hold")[0].equals("on")) {
					hold = true;
				}
					
			}else {
				hold = false;
			}
			pbean.setHold(hold);
			logbean.setHold(hold);
			
			//編集する各データの取得
			String title = request.getParameter("title"); //タイトル取得
			pbean.setTitle(title);
			String content = request.getParameter("content"); //詳細取得
			pbean.setContent(content);
			
			if(level.equals("middle")) { //中目標選択時の大目標データ取得
				int big = Integer.parseInt(request.getParameter("big_ReadyMade"));
				pbean.setBig(big);
				for(int i=0;i<bigArray.size();i++) {
					if(big == bigArray.get(i).getId()) {
						logbean.setAfter_big_title(bigArray.get(i).getTitle());
					}
				}
			}else if(level.equals("small")) {
				String big_middle = request.getParameter("middle_ReadyMade");
				System.out.println("bm:"+big_middle);
				String[] bm = big_middle.split(",");
				int middle = Integer.parseInt(bm[0]);
				pbean.setMiddle(middle);
				for(int i=0;i<middleArray.size();i++) {
					if(middle == middleArray.get(i).getId()) {
						logbean.setAfter_middle_title(middleArray.get(i).getTitle());
					}
				}
				int big = Integer.parseInt(bm[1]);
				pbean.setBig(big);
				for(int i=0;i<bigArray.size();i++) {
					if(big == bigArray.get(i).getId()) {
						logbean.setAfter_big_title(bigArray.get(i).getTitle());
					}
				}
				
			}else if(level.equals("sche")) {
				String date = request.getParameter("year")+"-"+request.getParameter("month")+"-"+request.getParameter("day");
				pbean.setDate(date);
				//スケジュール編集時週のデータも取得し直す
				session.removeAttribute("weekarray");
				weekArray = new ArrayList<>(); 
			}
			
			if(level.equals("big")) {
				for(PlanBean bean:bigArray) {
					if(id == bean.getId()) {
							//ログ情報をLogBeanに設定
							logbean.setId(id);
							logbean.setOpe("update");
							logbean.setBefore_title(bean.getTitle());
							logbean.setBefore_content(bean.getContent());
							logbean.setBefore_level(level);
							logbean.setAfter_title(title);
							logbean.setAfter_content(content);
							logbean.setAfter_level(level);
							break;
					}
				}
			}else if(level.equals("middle")) {
				for(PlanBean bean:middleArray) {
					if(id == bean.getId()) {
							//ログ情報をLogBeanに設定
							logbean.setId(id);
							logbean.setOpe("update");
							logbean.setBefore_title(bean.getTitle());
							logbean.setBefore_content(bean.getContent());
							logbean.setBefore_level(level);
							logbean.setBefore_big(bean.getBig());
							logbean.setBefore_big_title(bean.getBig_title());
							logbean.setAfter_title(title);
							logbean.setAfter_content(content);
							logbean.setAfter_level(level);
							logbean.setAfter_big(pbean.getBig());
							
							break;
					}
					
				}
				
			}else if(level.equals("small")) {
				for(PlanBean bean:smallArray) {
					if(id == bean.getId()) {
						//ログ情報をLogBeanに設定
						logbean.setId(id);
						logbean.setOpe("update");
						logbean.setBefore_title(bean.getTitle());
						logbean.setBefore_content(bean.getContent());
						logbean.setBefore_level(level);
						logbean.setBefore_big(bean.getBig());
						logbean.setBefore_big_title(bean.getBig_title());
						logbean.setBefore_middle(bean.getMiddle());
						logbean.setBefore_middle_title(bean.getMiddle_title());
						logbean.setAfter_title(title);
						logbean.setAfter_content(content);
						logbean.setAfter_level(level);
						logbean.setAfter_big(pbean.getBig());
						logbean.setAfter_middle(pbean.getMiddle());
						break;
					}
				}
				
			}else if(level.equals("sche")) {
				for(PlanBean bean:scheArray) {
					if(id == bean.getId()) {
						//ログ情報をLogBeanに設定
						logbean.setId(id);
						logbean.setOpe("update");
						logbean.setBefore_title(bean.getTitle());
						logbean.setBefore_content(bean.getContent());
						logbean.setBefore_level(level);
						logbean.setBefore_date(bean.getDate());
						logbean.setAfter_title(title);
						logbean.setAfter_content(content);
						logbean.setAfter_level(level);
						logbean.setAfter_date(pbean.getDate());
						break;
					}
				}
			}
			
			
			//idの登録データを編集
			boolean result = SQLOperator.editData(lbean.getUserid(),pbean,weekArray,logbean);
			
			//データ編集成功時、セッション情報を書き換える
			if(result) {
				
				if(level.equals("big")) {
					
					for(int i=0;i<bigArray.size();i++) {
						if(id == bigArray.get(i).getId()) {
							//セッションの大目標のデータを編集
							bigArray.get(i).setTitle(title);
							bigArray.get(i).setContent(content);
							bigArray.get(i).setHold(hold);
							break;
						}
					}
					//セッション中、小目標の上位データを編集
					
					for(int i=0;i<middleArray.size();i++) {
						if(id == middleArray.get(i).getBig()) {
							middleArray.get(i).setBig_title(title);
							middleArray.get(i).setHold(hold);
							
						}
					}
					for(int i=0;i<smallArray.size();i++) {
						if(id == smallArray.get(i).getBig()) {
							smallArray.get(i).setBig_title(title);
							smallArray.get(i).setHold(hold);
						}
					}
					
				}else if(level.equals("middle")) {
					
					//セッションの大目標からタイトル取得
					for(int i=0;i<bigArray.size();i++) {
						if(pbean.getBig() == bigArray.get(i).getId()) {
							pbean.setBig_title(bigArray.get(i).getTitle());
							break;
						}
					}
					
					for(int i=0;i<middleArray.size();i++) {
						if(id == middleArray.get(i).getId()) {
							//セッションの中目標の上位データ、上位タイトル、タイトル、内容を編集
							middleArray.get(i).setTitle(title);
							middleArray.get(i).setContent(content);
							middleArray.get(i).setBig(pbean.getBig());
							middleArray.get(i).setBig_title(pbean.getBig_title());
							middleArray.get(i).setHold(hold);
							break;
						}
					}
					//セッションの小目標の上位データ、上位タイトル、中位データ、中位タイトルを編集
					for(int i=0;i<smallArray.size();i++) {
						if(id == smallArray.get(i).getMiddle()) {
							smallArray.get(i).setMiddle(id);
							smallArray.get(i).setMiddle_title(title);
							smallArray.get(i).setBig_title(pbean.getBig_title());
							smallArray.get(i).setBig(pbean.getBig());
							smallArray.get(i).setHold(hold);
						}
					}
					
				
				}else if(level.equals("small")) {

					//セッションの大目標からタイトル取得
					for(int i=0;i<bigArray.size();i++) {
						if(pbean.getBig() == bigArray.get(i).getId()) {
							pbean.setBig_title(bigArray.get(i).getTitle());
							break;
						}
					}
					
					//セッションの中目標のタイトル取得
					for(int i=0;i<middleArray.size();i++) {
						if(pbean.getMiddle() == middleArray.get(i).getId()) {
							pbean.setMiddle_title(middleArray.get(i).getTitle());
							break;
						}
					}
					
					for(int i=0;i<smallArray.size();i++) {
						if(id == smallArray.get(i).getId()) {
							//セッションの小目標データを編集
							smallArray.get(i).setTitle(title);
							smallArray.get(i).setContent(content);
							smallArray.get(i).setBig(pbean.getBig());
							smallArray.get(i).setBig_title(pbean.getBig_title());
							smallArray.get(i).setMiddle(pbean.getMiddle());
							smallArray.get(i).setMiddle_title(pbean.getMiddle_title());
							smallArray.get(i).setHold(hold);
							break;
						}
					}
					
					
				}else if(level.equals("sche")) {
					Calendar cal = Calendar.getInstance();
					if(
						Integer.parseInt(pbean.getDate().substring(0,4))  == cal.get(Calendar.YEAR) &&
						Integer.parseInt(pbean.getDate().substring(5,7)) == cal.get(Calendar.MONTH)+1 &&
						Integer.parseInt(pbean.getDate().substring(8,10)) == cal.get(Calendar.DATE)) { 
						//当日のスケジュールのセッション情報を編集
						
						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> todayArray = (ArrayList<PlanBean>) session.getAttribute("todayarray");
						
						boolean todaycheck = false;
						
						for(int i=0;i<todayArray.size();i++) { //当日の情報変更
							if(id == todayArray.get(i).getId()) {
								todayArray.get(i).setTitle(title);
								todayArray.get(i).setContent(content);
								todayArray.get(i).setDate(pbean.getDate());
								todayArray.get(i).setHold(hold);
								session.setAttribute("todayarray", todayArray);
								todaycheck = true;
								
								break;
							}
						}
						
						for(int i=0;i<scheArray.size();i++) {
							if(id == scheArray.get(i).getId()) {
								//セッションにスケジュール情報を設定
								scheArray.get(i).setTitle(title);
								scheArray.get(i).setContent(content);
								scheArray.get(i).setDate(pbean.getDate());
								scheArray.get(i).setHold(hold);
								if(!todaycheck){
									PlanBean tbean = new PlanBean();
									tbean.setTitle(title);
									tbean.setContent(content);
									tbean.setDate(pbean.getDate());
									tbean.setHold(hold);
									todayArray.add(tbean);
									session.setAttribute("todayarray", todayArray);
								}
								break;
							}
						}
						
					}else { //当日以降のスケジュールのセッション情報
						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> todayArray = (ArrayList<PlanBean>) session.getAttribute("todayarray");
						for(int i=0;i<todayArray.size();i++) {
							if(id == todayArray.get(i).getId()) {
								todayArray.remove(i);
								break;
							}
						}
						
						for(int i=0;i<scheArray.size();i++) {
							if(id == scheArray.get(i).getId()) {
								scheArray.get(i).setTitle(title);
								scheArray.get(i).setContent(content);
								scheArray.get(i).setDate(pbean.getDate());
								scheArray.get(i).setHold(hold);
								break;
							}
						}
						
					}
					
					
				}
				
			}
			
			session.setAttribute("smallarray", smallArray);
			session.setAttribute("middlearray", middleArray);
			session.setAttribute("bigarray", bigArray);
			session.setAttribute("schearray", scheArray);
			session.setAttribute("weekarray", weekArray);
			
			//ログのセッション情報を再設定
			@SuppressWarnings("unchecked")
			ArrayList<LogBean> logArray = (ArrayList<LogBean>) session.getAttribute("logarray");
			Collections.reverse(logArray);
			logArray.add(logbean);
			session.setAttribute("logarray", logArray);

			//トップ画面にフォワード処理
			ServletContext application = getServletContext();
			RequestDispatcher rd = application.getRequestDispatcher("/jsp/top.jsp");
			rd.forward(request, response);
		}
		
	}

}
