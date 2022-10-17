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
 * Servlet implementation class resetController
 */
@WebServlet("/resetController")
public class resetController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public resetController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		//リセットする更新データのログIDを取得
		int logid = Integer.parseInt(request.getParameter("logid"));
		
		//ログのデータ配列のセッション情報を取得
		HttpSession session = request.getSession();
		LoginBean lbean = (LoginBean) session.getAttribute("loginbean");
		@SuppressWarnings("unchecked")
		ArrayList<LogBean> logArray = (ArrayList<LogBean>) session.getAttribute("logarray");
		//logArray.add(logbean);
		//session.setAttribute("logarray", logArray);
		
		//リセット前、リセット後のlogbean
		LogBean blogbean = new LogBean();
		LogBean alogbean = new LogBean();
		
		//リセット後のPlanBean
		PlanBean rabean = new PlanBean();
		
		for(int i=0;i<logArray.size();i++) {
			if(logid == logArray.get(i).getLogid()) {
				blogbean = logArray.get(i); //ログセッションから元に戻すためのログ情報をコピー
				break;
			}
		}
		
		
		if(blogbean.getOpe().equals("insert")) { //過去に挿入したデータを削除する
			
			//リセット後のセッションログ情報
			alogbean.setOpe("delete");
			alogbean.setId(blogbean.getId());
			alogbean.setBefore_level(blogbean.getAfter_level());
			alogbean.setBefore_content(blogbean.getAfter_content());
			alogbean.setBefore_title(blogbean.getAfter_title());
			alogbean.setHold(blogbean.getHold());
			
			if(blogbean.getAfter_level().equals("big")) {
				
			}else if(blogbean.getAfter_level().equals("middle")) {
				alogbean.setBefore_big(blogbean.getAfter_big());
			}else if(blogbean.getAfter_level().equals("small")) {
				alogbean.setBefore_big(blogbean.getAfter_big());
				alogbean.setBefore_middle(blogbean.getAfter_middle());
			}else if(blogbean.getAfter_level().equals("sche")) {
				alogbean.setBefore_date(blogbean.getAfter_date());
			}
			
			int result = SQLOperator.deleteData(alogbean.getId(),lbean.getUserid(),alogbean.getBefore_level(),alogbean);
			

			//削除前のログをセッション情報に設定
			Collections.reverse(logArray);
			logArray.add(alogbean);
			session.setAttribute("logarray", logArray);
			
			//データベース削除成功時、セッション情報からもデータを削除する
			if(result>0) {
				
				if(alogbean.getBefore_level().equals("big")) {
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> bigArray = (ArrayList<PlanBean>) session.getAttribute("bigarray");
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> middleArray = (ArrayList<PlanBean>) session.getAttribute("middlearray");
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> smallArray = (ArrayList<PlanBean>) session.getAttribute("smallarray");
					//セッションの大目標からデータを削除
					for(int i=0;i<bigArray.size();i++) {
						if(alogbean.getId() == bigArray.get(i).getId()) {
							bigArray.remove(i);
							break;
						}
					}
					//セッション中、小目標から上位データを削除
					
					for(int i=0;i<middleArray.size();i++) {
						if(alogbean.getId() == middleArray.get(i).getBig()) {
							middleArray.get(i).setBig(0);
							middleArray.get(i).setBig_title("");
							middleArray.get(i).setHold(false);
							result--;
							
						}
						if(result==0 || result==99999) {
							break;
						}
					}
					for(int i=0;i<smallArray.size();i++) {
						if(alogbean.getId() == smallArray.get(i).getBig()) {
							smallArray.get(i).setBig(0);
							smallArray.get(i).setBig_title("");
							smallArray.get(i).setHold(false);
							result--;
						}
						if(result==0 || result==99999) {
							break;
						}
					}

					session.setAttribute("smallarray", smallArray);
					session.setAttribute("middlearray", middleArray);
					session.setAttribute("bigarray", bigArray);
					
				}else if(alogbean.getBefore_level().equals("middle")) {
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> middleArray = (ArrayList<PlanBean>) session.getAttribute("middlearray");
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> smallArray = (ArrayList<PlanBean>) session.getAttribute("smallarray");
					
					for(int i=0;i<middleArray.size();i++) {
						if(alogbean.getId() == middleArray.get(i).getId()) {
							middleArray.remove(i);
							break;
						}
					}

					for(int i=0;i<smallArray.size();i++) {
						if(alogbean.getId() == smallArray.get(i).getMiddle()) {
							smallArray.get(i).setBig(0);
							smallArray.get(i).setMiddle(0);
							smallArray.get(i).setBig_title("");
							smallArray.get(i).setMiddle_title("");
							smallArray.get(i).setHold(false);
							result--;
						}
						if(result==0 || result==99999) {
							break;
						}
					}
					session.setAttribute("smallarray", smallArray);
					session.setAttribute("middlearray", middleArray);
					
				}else if(alogbean.getBefore_level().equals("small")) {
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> smallArray = (ArrayList<PlanBean>) session.getAttribute("smallarray");
					for(int i=0;i<smallArray.size();i++) {
						if(alogbean.getId() == smallArray.get(i).getId()) {
							smallArray.remove(i);
							break;
						}
					}
					session.setAttribute("smallarray", smallArray);
					
				}else if(alogbean.getBefore_level().equals("sche")) {
					
					

					//削除するスケジュールのセッション情報
					PlanBean sbean = null;
						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> scheArray = (ArrayList<PlanBean>) session.getAttribute("schearray");
						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> weekArray = (ArrayList<PlanBean>) session.getAttribute("weekarray");
						for(int i=0;i<scheArray.size();i++) {
							if(alogbean.getId() == scheArray.get(i).getId()) {
								sbean = scheArray.get(i);
								scheArray.remove(i); //スケジュールのセッション削除
								break;
							}
						}
						for(int i=0;i<weekArray.size();i++) {
							if(alogbean.getId() == weekArray.get(i).getId()) {
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
								if(alogbean.getId() == todayArray.get(i).getId()) {
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
			
			
		}else if(blogbean.getOpe().equals("delete")) { //過去に削除したデータを挿入し直す
			
				
			//リセット後のセッションログ情報
			alogbean.setOpe("insert");
			alogbean.setId(blogbean.getId());
			alogbean.setHold(blogbean.getHold());
			alogbean.setAfter_level(blogbean.getBefore_level());
			alogbean.setAfter_content(blogbean.getBefore_content());
			alogbean.setAfter_title(blogbean.getBefore_title());
			
			rabean.setId(blogbean.getId());
			rabean.setHold(blogbean.getHold());
			rabean.setLevel(blogbean.getBefore_level());
			rabean.setContent(blogbean.getBefore_content());
			rabean.setTitle(blogbean.getBefore_title());
			
			if(blogbean.getBefore_level().equals("big")) {
				
			}else if(blogbean.getBefore_level().equals("middle")) {
				alogbean.setAfter_big(blogbean.getBefore_big());
			}else if(blogbean.getBefore_level().equals("small")) {
				alogbean.setAfter_big(blogbean.getBefore_big());
				alogbean.setAfter_middle(blogbean.getBefore_middle());
			}else if(blogbean.getBefore_level().equals("sche")) {
				alogbean.setAfter_date(blogbean.getBefore_date());
			}
			

			@SuppressWarnings("unchecked")
			ArrayList<PlanBean> bigArray = (ArrayList<PlanBean>) session.getAttribute("bigarray");
			@SuppressWarnings("unchecked")
			ArrayList<PlanBean> middleArray = (ArrayList<PlanBean>) session.getAttribute("middlearray");
			
			
			
			//中目標以下を入れるとき大目標を取得
			if(alogbean.getAfter_level().equals("middle")) {
				int big = alogbean.getAfter_big();
				rabean.setBig(big);
				
				for(PlanBean b:bigArray) {
					if(rabean.getBig() == b.getId()) {
						rabean.setBig_title(b.getTitle());
						alogbean.setAfter_big_title(b.getTitle());
					break;
					}
				}
				
			}
			//小目標を入れるとき中目標を取得
			if(alogbean.getAfter_level().equals("small")) {
				int middle = alogbean.getAfter_middle();
				int big = alogbean.getAfter_big();
				rabean.setMiddle(middle);
				rabean.setBig(big);

				for(PlanBean b:bigArray) {
					System.out.println("大のID"+b.getId());
					if(rabean.getBig() == b.getId()) {
						rabean.setBig_title(b.getTitle());
						alogbean.setAfter_big_title(b.getTitle());
					break;
					}
				}
				
				for(PlanBean m:middleArray) {
					System.out.println("中のID"+m.getId());
					if(rabean.getMiddle() == m.getId()) {
						rabean.setMiddle_title(m.getTitle());
						alogbean.setAfter_middle_title(m.getTitle());
					break;
					}
				}
			}
			
			//予定を入れるとき年月日を取得
			if(alogbean.getAfter_level().equals("sche")) {
				String date = alogbean.getAfter_date();
				rabean.setDate(date);
			}
			
			//スケジュール変更時、weekarrayをセットし直す
			if(alogbean.getAfter_level().equals("sche")) {
				session.removeAttribute("weekarray");
			}
			ArrayList<PlanBean> weekArray = new ArrayList<>(); 

			//削除したデータをデータベースに再度登録
			boolean result = SQLOperator.setDeleteData(rabean,alogbean,weekArray,lbean.getUserid());

			//sessionに情報を再セット
			if(result) {
				
				if(alogbean.getAfter_level().equals("big")) {
					bigArray.add(rabean);
					session.setAttribute("bigarray", bigArray);
					
				}else if(alogbean.getAfter_level().equals("middle")) {
					middleArray.add(rabean);
					session.setAttribute("middlearray", middleArray);
					
				}else if(alogbean.getAfter_level().equals("small")) {
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> smallArray = (ArrayList<PlanBean>) session.getAttribute("smallarray");
					smallArray.add(rabean);
					session.setAttribute("smallarray", smallArray);
					
				}else if(alogbean.getAfter_level().equals("sche")) {
					Calendar cal = Calendar.getInstance();
		
					if(
						Integer.parseInt(rabean.getDate().substring(0,4))  == cal.get(Calendar.YEAR) &&
						Integer.parseInt(rabean.getDate().substring(5,7)) == cal.get(Calendar.MONTH)+1 &&
						Integer.parseInt(rabean.getDate().substring(8,10)) == cal.get(Calendar.DATE)) {
						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> todayArray = (ArrayList<PlanBean>) session.getAttribute("todayarray");
						todayArray.add(rabean);
						session.setAttribute("todayarray", todayArray);
						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> scheArray = (ArrayList<PlanBean>) session.getAttribute("schearray");
						scheArray.add(rabean);
						session.setAttribute("schearray", scheArray);
							
						
					}else {
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> scheArray = (ArrayList<PlanBean>) session.getAttribute("schearray");
					scheArray.add(rabean);
					session.setAttribute("schearray", scheArray);
						
					}
					
					session.setAttribute("weekarray", weekArray);
				}
				//ログデータをセッション情報に登録
				Collections.reverse(logArray);
				logArray.add(alogbean);
				session.setAttribute("logarray", logArray);
			}
			//トップ画面にフォワード処理
			ServletContext application = getServletContext();
			RequestDispatcher rd = application.getRequestDispatcher("/jsp/top.jsp");
			rd.forward(request, response);
		
			
			
		}else if(blogbean.getOpe().equals("update")) { //過去に変更したデータを元に戻す
			

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
			
			//更新後のログ情報、planbeanを設定
			alogbean.setId(blogbean.getId());
			alogbean.setOpe("update");
			alogbean.setHold(blogbean.getHold());
			alogbean.setAfter_level(blogbean.getBefore_level());
			alogbean.setBefore_level(blogbean.getAfter_level());
			alogbean.setAfter_title(blogbean.getBefore_title());
			alogbean.setBefore_title(blogbean.getAfter_title());
			alogbean.setAfter_content(blogbean.getBefore_content());
			alogbean.setBefore_content(blogbean.getAfter_content());
			
			rabean.setId(blogbean.getId());
			rabean.setHold(blogbean.getHold());
			rabean.setLevel(blogbean.getBefore_level());
			rabean.setTitle(blogbean.getBefore_title());
			rabean.setContent(blogbean.getBefore_content());

			if(blogbean.getBefore_level().equals("big")) {
				
			}else if(blogbean.getBefore_level().equals("middle")) {
				alogbean.setAfter_big(blogbean.getBefore_big());
				alogbean.setBefore_big(blogbean.getAfter_big());
			}else if(blogbean.getBefore_level().equals("small")) {
				alogbean.setAfter_big(blogbean.getBefore_big());
				alogbean.setAfter_middle(blogbean.getBefore_middle());
				alogbean.setBefore_big(blogbean.getAfter_big());
				alogbean.setBefore_middle(blogbean.getAfter_middle());
			}else if(blogbean.getBefore_level().equals("sche")) {
				alogbean.setAfter_date(blogbean.getBefore_date());
				alogbean.setBefore_date(blogbean.getAfter_date());
			}
			
			if(alogbean.getAfter_level().equals("middle")) { //中目標選択時の大目標データ取得
				int bbig = alogbean.getBefore_big();
				int abig = alogbean.getAfter_big();
				rabean.setBig(abig);
				for(int i=0;i<bigArray.size();i++) {
					if(abig == bigArray.get(i).getId()) {
						alogbean.setAfter_big_title(bigArray.get(i).getTitle());
						rabean.setBig_title(bigArray.get(i).getTitle());
					}
					if(bbig == bigArray.get(i).getId()) {
						alogbean.setBefore_big_title(bigArray.get(i).getTitle());
					}
				}
			}else if(alogbean.getAfter_level().equals("small")) {
				int bmiddle = alogbean.getBefore_middle();
				int amiddle = alogbean.getAfter_middle();
				rabean.setMiddle(amiddle);
				for(int i=0;i<middleArray.size();i++) {
					if(amiddle == middleArray.get(i).getId()) {
						alogbean.setAfter_middle_title(middleArray.get(i).getTitle());
						rabean.setMiddle_title(middleArray.get(i).getTitle());
					}
					if(bmiddle == middleArray.get(i).getId()) {
						alogbean.setBefore_middle_title(middleArray.get(i).getTitle());
					}
				}
				int bbig = alogbean.getBefore_big();
				int abig = alogbean.getAfter_big();
				rabean.setBig(abig);
				for(int i=0;i<bigArray.size();i++) {
					if(abig == bigArray.get(i).getId()) {
						alogbean.setAfter_big_title(bigArray.get(i).getTitle());
						rabean.setBig_title(bigArray.get(i).getTitle());
					}
					if(bbig == bigArray.get(i).getId()) {
						alogbean.setBefore_big_title(bigArray.get(i).getTitle());
					}
				}
				
			}else if(alogbean.getAfter_level().equals("sche")) {
				rabean.setDate(alogbean.getAfter_date());
				//スケジュール編集時週のデータも取得し直す
				session.removeAttribute("weekarray");
				weekArray = new ArrayList<>(); 
			}

			
			//idの登録データを編集
			boolean result = SQLOperator.editData(lbean.getUserid(),rabean,weekArray,alogbean);
			

			//データ編集成功時、セッション情報を書き換える
			if(result) {
				
				if(alogbean.getAfter_level().equals("big")) {
					
					for(int i=0;i<bigArray.size();i++) {
						if(alogbean.getId() == bigArray.get(i).getId()) {
							//セッションの大目標のデータを編集
							bigArray.get(i).setTitle(alogbean.getAfter_title());
							bigArray.get(i).setContent(alogbean.getAfter_content());
							bigArray.get(i).setHold(alogbean.getHold());
							break;
						}
					}
					//セッション中、小目標の上位データを編集
					
					for(int i=0;i<middleArray.size();i++) {
						if(alogbean.getId() == middleArray.get(i).getBig()) {
							middleArray.get(i).setBig_title(alogbean.getAfter_big_title());
							middleArray.get(i).setHold(alogbean.getHold());
							
						}
					}
					for(int i=0;i<smallArray.size();i++) {
						if(alogbean.getId() == smallArray.get(i).getBig()) {
							smallArray.get(i).setBig_title(alogbean.getAfter_big_title());
							smallArray.get(i).setHold(alogbean.getHold());
						}
					}
					
				}else if(alogbean.getAfter_level().equals("middle")) {
					
					
					for(int i=0;i<middleArray.size();i++) {
						if(alogbean.getId() == middleArray.get(i).getId()) {
							//セッションの中目標の上位データ、上位タイトル、タイトル、内容を編集
							middleArray.get(i).setTitle(alogbean.getAfter_title());
							middleArray.get(i).setContent(alogbean.getAfter_content());
							middleArray.get(i).setBig(alogbean.getAfter_big());
							middleArray.get(i).setBig_title(alogbean.getAfter_big_title());
							middleArray.get(i).setHold(alogbean.getHold());
							break;
						}
					}
					//セッションの小目標の上位データ、上位タイトル、中位データ、中位タイトルを編集
					for(int i=0;i<smallArray.size();i++) {
						if(alogbean.getId() == smallArray.get(i).getMiddle()) {
							smallArray.get(i).setMiddle(alogbean.getId());
							smallArray.get(i).setMiddle_title(alogbean.getAfter_middle_title());
							smallArray.get(i).setBig_title(alogbean.getAfter_big_title());
							smallArray.get(i).setBig(alogbean.getAfter_big());
							smallArray.get(i).setHold(alogbean.getHold());
						}
					}
					
				
				}else if(alogbean.getAfter_level().equals("small")) {
					
					for(int i=0;i<smallArray.size();i++) {
						if(alogbean.getId() == smallArray.get(i).getId()) {
							//セッションの小目標データを編集
							smallArray.get(i).setTitle(alogbean.getAfter_title());
							smallArray.get(i).setContent(alogbean.getAfter_content());
							smallArray.get(i).setBig(rabean.getBig());
							smallArray.get(i).setBig_title(rabean.getBig_title());
							smallArray.get(i).setMiddle(rabean.getMiddle());
							smallArray.get(i).setMiddle_title(rabean.getMiddle_title());
							smallArray.get(i).setHold(rabean.isHold());
							break;
						}
					}
					
					
				}else if(alogbean.getAfter_level().equals("sche")) {
					Calendar cal = Calendar.getInstance();
					if(
						Integer.parseInt(rabean.getDate().substring(0,4))  == cal.get(Calendar.YEAR) &&
						Integer.parseInt(rabean.getDate().substring(5,7)) == cal.get(Calendar.MONTH)+1 &&
						Integer.parseInt(rabean.getDate().substring(8,10)) == cal.get(Calendar.DATE)) { 
						//当日のスケジュールのセッション情報を編集
						
						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> todayArray = (ArrayList<PlanBean>) session.getAttribute("todayarray");
						
						boolean todaycheck = false;
						
						for(int i=0;i<todayArray.size();i++) { //当日の情報変更
							if(alogbean.getId() == todayArray.get(i).getId()) {
								todayArray.get(i).setTitle(alogbean.getAfter_title());
								todayArray.get(i).setContent(alogbean.getAfter_content());
								todayArray.get(i).setDate(rabean.getDate());
								todayArray.get(i).setHold(alogbean.getHold());
								session.setAttribute("todayarray", todayArray);
								todaycheck = true;
								
								break;
							}
						}
						
						for(int i=0;i<scheArray.size();i++) {
							if(alogbean.getId() == scheArray.get(i).getId()) {
								//セッションにスケジュール情報を設定
								scheArray.get(i).setTitle(alogbean.getAfter_title());
								scheArray.get(i).setContent(alogbean.getAfter_content());
								scheArray.get(i).setDate(rabean.getDate());
								scheArray.get(i).setHold(alogbean.getHold());
								if(!todaycheck){
									PlanBean tbean = new PlanBean();
									tbean.setTitle(alogbean.getAfter_title());
									tbean.setContent(alogbean.getAfter_content());
									tbean.setDate(rabean.getDate());
									tbean.setHold(alogbean.getHold());
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
							if(alogbean.getId() == todayArray.get(i).getId()) {
								todayArray.remove(i);
								break;
							}
						}
						
						for(int i=0;i<scheArray.size();i++) {
							if(alogbean.getId() == scheArray.get(i).getId()) {
								scheArray.get(i).setTitle(alogbean.getAfter_title());
								scheArray.get(i).setContent(alogbean.getAfter_content());
								scheArray.get(i).setDate(rabean.getDate());
								scheArray.get(i).setHold(alogbean.getHold());
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
			
			Collections.reverse(logArray);
			logArray.add(alogbean);
			session.setAttribute("logarray", logArray);

			//トップ画面にフォワード処理
			ServletContext application = getServletContext();
			RequestDispatcher rd = application.getRequestDispatcher("/jsp/top.jsp");
			rd.forward(request, response);
			
		}
		
	}

}
