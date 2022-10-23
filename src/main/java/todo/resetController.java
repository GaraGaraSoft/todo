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
		request.setCharacterEncoding("UTF-8");
		
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
		
		//再挿入、更新時に前の上中位目標があるか判定
		boolean beforeBig = false;
		boolean beforeMiddle = false;
		
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
				alogbean.setBefore_big_title(blogbean.getAfter_big_title());
			}else if(blogbean.getAfter_level().equals("small")) {
				alogbean.setBefore_big(blogbean.getAfter_big());
				alogbean.setBefore_big_title(blogbean.getAfter_big_title());
				alogbean.setBefore_middle(blogbean.getAfter_middle());
				alogbean.setBefore_middle_title(blogbean.getAfter_middle_title());
			}else if(blogbean.getAfter_level().equals("sche")) {
				alogbean.setBefore_date(blogbean.getAfter_date());
			}
			
			int result = SQLOperator.deleteData(alogbean.getId(),lbean.getUserid(),alogbean.getBefore_level(),alogbean);
			

			//削除処理のログをセッション情報に設定
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
					//セッション中、小目標から上位データを削除し保留へ
					
					for(int i=0;i<middleArray.size();i++) {
						if(alogbean.getId() == middleArray.get(i).getBig()) {
							middleArray.get(i).setBig(0);
							middleArray.get(i).setBig_title("");
							middleArray.get(i).setHold(true);
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
							smallArray.get(i).setHold(true);
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
					
					//セッションの中目標から削除
					for(int i=0;i<middleArray.size();i++) {
						if(alogbean.getId() == middleArray.get(i).getId()) {
							middleArray.remove(i);
							break;
						}
					}
					
					//セッションの小目標から中目標以上を削除し保留
					for(int i=0;i<smallArray.size();i++) {
						if(alogbean.getId() == smallArray.get(i).getMiddle()) {
							smallArray.get(i).setBig(0);
							smallArray.get(i).setMiddle(0);
							smallArray.get(i).setBig_title("");
							smallArray.get(i).setMiddle_title("");
							smallArray.get(i).setHold(true);
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

			@SuppressWarnings("unchecked")
			ArrayList<PlanBean> bigArray = (ArrayList<PlanBean>) session.getAttribute("bigarray");
			@SuppressWarnings("unchecked")
			ArrayList<PlanBean> middleArray = (ArrayList<PlanBean>) session.getAttribute("middlearray");
				
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
			
			if(blogbean.getBefore_level().equals("big")) 
			{
				
			}else if(blogbean.getBefore_level().equals("middle")) 
			{ //中目標を削除前に戻すとき
				for(int i=0;i<bigArray.size();i++) {
					if(blogbean.getBefore_big() == bigArray.get(i).getId()) {
						//元の上目標が残っていれば設定
						alogbean.setAfter_big(bigArray.get(i).getId());
						alogbean.setAfter_big_title(bigArray.get(i).getTitle());
						rabean.setBig(bigArray.get(i).getId());
						rabean.setBig_title(bigArray.get(i).getTitle());
						//元保留なしデータは元の中目標に合わせて保留状態を設定
						if(alogbean.getHold()==false) {
							alogbean.setHold(bigArray.get(i).isHold());
							rabean.setHold(bigArray.get(i).isHold());
						}
						beforeBig = true;

					}
				}

				if(beforeBig == false) 
				{ //上中位目標が見つからないときは保留状態に
					alogbean.setHold(true);
					rabean.setHold(true);
				}
			}else if(blogbean.getBefore_level().equals("small")) 
			{ //小目標を削除前に戻すとき
				
				for(int i=0;i<bigArray.size();i++) 
				{
					if(blogbean.getBefore_big() == bigArray.get(i).getId())
					{
						
						for(int j=0;j<middleArray.size();j++)
						{
							if(blogbean.getBefore_middle() == middleArray.get(j).getId())
							{ 
								//元の上目標が残っていれば設定
								alogbean.setAfter_big(bigArray.get(i).getId());
								alogbean.setAfter_big_title(bigArray.get(i).getTitle());
								rabean.setBig(bigArray.get(i).getId());
								rabean.setBig_title(bigArray.get(i).getTitle());
								beforeBig = true;
								//元の中位目標が残っていれば設定
								alogbean.setAfter_middle(middleArray.get(j).getId());
								alogbean.setAfter_middle_title(middleArray.get(j).getTitle());
								rabean.setMiddle(middleArray.get(j).getId());
								rabean.setMiddle_title(middleArray.get(j).getTitle());
								//元保留なしデータは元の中目標に合わせて保留状態を設定
								if(alogbean.getHold()==false) {
									alogbean.setHold(middleArray.get(j).isHold());
									rabean.setHold(middleArray.get(j).isHold());
								}
								beforeMiddle = true;
								
							}
						}
						
					}
						
				}
				
				if(beforeBig == false) 
				{ //上中位目標が見つからないときは保留状態に
					alogbean.setHold(true);
					rabean.setHold(true);
				}
				
				System.out.println(beforeBig+"どうなっとんねん"+rabean.isHold());
				
			}else if(blogbean.getBefore_level().equals("sche")) 
			{ //スケジュールを削除前に戻すとき
				alogbean.setAfter_date(blogbean.getBefore_date());
				rabean.setDate(blogbean.getBefore_date());
			}
			
			
			//スケジュール変更時、weekarrayをセットし直す
			if(alogbean.getAfter_level().equals("sche")) {
				session.removeAttribute("weekarray");
			}
			ArrayList<PlanBean> weekArray = new ArrayList<>(); 

			//削除したデータをデータベースに再度登録
			boolean result = SQLOperator.setDeleteData(rabean,alogbean,weekArray,lbean.getUserid(),beforeBig,beforeMiddle);

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
					
				}else if(alogbean.getAfter_level().equals("sche")) 
				{
					Calendar cal = Calendar.getInstance();
		
					if(
						Integer.parseInt(rabean.getDate().substring(0,4))  == cal.get(Calendar.YEAR) &&
						Integer.parseInt(rabean.getDate().substring(5,7)) == cal.get(Calendar.MONTH)+1 &&
						Integer.parseInt(rabean.getDate().substring(8,10)) == cal.get(Calendar.DATE)) 
					{ //当日のスケジュールは当日のデータ配列に追加
						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> todayArray = (ArrayList<PlanBean>) session.getAttribute("todayarray");
						todayArray.add(rabean);
						session.setAttribute("todayarray", todayArray);
						
					}
					
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> scheArray = (ArrayList<PlanBean>) session.getAttribute("schearray");
					scheArray.add(rabean);
					session.setAttribute("schearray", scheArray);
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
			alogbean.setBeforehold(blogbean.getHold());
			alogbean.setHold(blogbean.getBeforehold());
			alogbean.setAfter_level(blogbean.getBefore_level());
			alogbean.setBefore_level(blogbean.getAfter_level());
			alogbean.setAfter_title(blogbean.getBefore_title());
			alogbean.setBefore_title(blogbean.getAfter_title());
			alogbean.setAfter_content(blogbean.getBefore_content());
			alogbean.setBefore_content(blogbean.getAfter_content());
			
			rabean.setId(blogbean.getId());
			rabean.setHold(blogbean.getBeforehold());
			rabean.setLevel(blogbean.getBefore_level());
			rabean.setTitle(blogbean.getBefore_title());
			rabean.setContent(blogbean.getBefore_content());
			
			if(blogbean.getBefore_level().equals("big")) { 
				if(blogbean.getAfter_level().equals("big")) { //大目標から大目標へリセット
					
				}else if(blogbean.getAfter_level().equals("middle")) {//中目標から大目標へリセット
					alogbean.setBefore_big(blogbean.getAfter_big());
					
					//変更前の上目標タイトルを取得
					for(int i=0;i<bigArray.size();i++) {
						if(alogbean.getBefore_big() == bigArray.get(i).getId()) {
							alogbean.setBefore_big_title(bigArray.get(i).getTitle());
						}
					}
					
				}else if(blogbean.getAfter_level().equals("small")) {//小目標から大目標へリセット
					alogbean.setBefore_big(blogbean.getAfter_big());
					alogbean.setBefore_middle(blogbean.getAfter_middle());
					
					//変更前の上中目標タイトルを取得
					for(int i=0;i<middleArray.size();i++) {
						if(alogbean.getBefore_middle() == middleArray.get(i).getId()) {
							alogbean.setBefore_middle_title(middleArray.get(i).getTitle());
						}
					}
					for(int i=0;i<bigArray.size();i++) {
						if(alogbean.getBefore_big() == bigArray.get(i).getId()) {
							alogbean.setBefore_big_title(bigArray.get(i).getTitle());
						}
					}
					
					
				}else if(blogbean.getAfter_level().equals("sche")) {//スケジュールから大目標へリセット
					alogbean.setBefore_date(blogbean.getAfter_date());
				}
				
			}else if(blogbean.getBefore_level().equals("middle")) {
				if(blogbean.getAfter_level().equals("big")) { //大目標から中目標へリセット
					alogbean.setAfter_big(blogbean.getBefore_big());
					
					//変更後の上目標タイトルを取得
					rabean.setBig(alogbean.getAfter_big());
					for(int i=0;i<bigArray.size();i++) {
						if(alogbean.getAfter_big() == bigArray.get(i).getId()) {
							alogbean.setAfter_big_title(bigArray.get(i).getTitle());
							rabean.setBig_title(bigArray.get(i).getTitle());
						}
					}
					
				}else if(blogbean.getAfter_level().equals("middle")) {//中目標から中目標へリセット
					alogbean.setAfter_big(blogbean.getBefore_big());
					alogbean.setBefore_big(blogbean.getAfter_big());
					
					//変更前、変更後の上目標タイトルを取得
					rabean.setBig(alogbean.getAfter_big());
					for(int i=0;i<bigArray.size();i++) {
						if(alogbean.getAfter_big() == bigArray.get(i).getId()) {
							alogbean.setAfter_big_title(bigArray.get(i).getTitle());
							rabean.setBig_title(bigArray.get(i).getTitle());
						}
						if(alogbean.getBefore_big() == bigArray.get(i).getId()) {
							alogbean.setBefore_big_title(bigArray.get(i).getTitle());
						}
					}
					
				}else if(blogbean.getAfter_level().equals("small")){//小目標から中目標へリセット
					alogbean.setAfter_big(blogbean.getBefore_big());
					alogbean.setBefore_big(blogbean.getAfter_big());
					alogbean.setBefore_middle(blogbean.getAfter_middle());
					
					//変更前、変更後の上中目標タイトルを取得
					for(int i=0;i<middleArray.size();i++) {
						if(alogbean.getBefore_middle() == middleArray.get(i).getId()) {
							alogbean.setBefore_middle_title(middleArray.get(i).getTitle());
						}
					}
					rabean.setBig(alogbean.getAfter_big());
					for(int i=0;i<bigArray.size();i++) {
						if(alogbean.getAfter_big() == bigArray.get(i).getId()) {
							alogbean.setAfter_big_title(bigArray.get(i).getTitle());
							rabean.setBig_title(bigArray.get(i).getTitle());
						}
						if(alogbean.getBefore_big() == bigArray.get(i).getId()) {
							alogbean.setBefore_big_title(bigArray.get(i).getTitle());
						}
					}
					
					
				}else if(blogbean.getAfter_level().equals("sche")) {//スケジュールから中目標へリセット
					alogbean.setAfter_big(blogbean.getBefore_big());
					alogbean.setBefore_date(blogbean.getAfter_date());
					
					//変更後の上目標タイトルを取得
					rabean.setBig(alogbean.getAfter_big());
					for(int i=0;i<bigArray.size();i++) {
						if(alogbean.getAfter_big() == bigArray.get(i).getId()) {
							alogbean.setAfter_big_title(bigArray.get(i).getTitle());
							rabean.setBig_title(bigArray.get(i).getTitle());
						}
					}
					
				}
			}else if(blogbean.getBefore_level().equals("small")) {
				if(blogbean.getAfter_level().equals("big")) {//大目標から小目標へリセット
					alogbean.setAfter_big(blogbean.getBefore_big());
					alogbean.setAfter_middle(blogbean.getBefore_middle());
					
					//変更後の上中目標タイトルを取得
					rabean.setMiddle(alogbean.getAfter_middle());
					for(int i=0;i<middleArray.size();i++) {
						if(alogbean.getAfter_middle() == middleArray.get(i).getId()) {
							alogbean.setAfter_middle_title(middleArray.get(i).getTitle());
							rabean.setMiddle_title(middleArray.get(i).getTitle());
						}
					}
					rabean.setBig(alogbean.getAfter_big());
					for(int i=0;i<bigArray.size();i++) {
						if(alogbean.getAfter_big() == bigArray.get(i).getId()) {
							alogbean.setAfter_big_title(bigArray.get(i).getTitle());
							rabean.setBig_title(bigArray.get(i).getTitle());
						}
					}
					
					
				}else if(blogbean.getAfter_level().equals("middle")) {//中目標から小目標へリセット
					alogbean.setAfter_big(blogbean.getBefore_big());
					alogbean.setAfter_middle(blogbean.getBefore_middle());
					alogbean.setBefore_big(blogbean.getAfter_big());
					
					//変更前、変更後の上中目標タイトルを取得
					rabean.setMiddle(alogbean.getAfter_middle());
					for(int i=0;i<middleArray.size();i++) {
						if(alogbean.getAfter_middle() == middleArray.get(i).getId()) {
							alogbean.setAfter_middle_title(middleArray.get(i).getTitle());
							rabean.setMiddle_title(middleArray.get(i).getTitle());
						}
					}
					rabean.setBig(alogbean.getAfter_big());
					for(int i=0;i<bigArray.size();i++) {
						if(alogbean.getAfter_big() == bigArray.get(i).getId()) {
							alogbean.setAfter_big_title(bigArray.get(i).getTitle());
							rabean.setBig_title(bigArray.get(i).getTitle());
						}
						if(alogbean.getBefore_big() == bigArray.get(i).getId()) {
							alogbean.setBefore_big_title(bigArray.get(i).getTitle());
						}
					}
					
					
				}else if(blogbean.getAfter_level().equals("small")){//小目標から小目標へリセット
						
					alogbean.setAfter_big(blogbean.getBefore_big());
					alogbean.setAfter_middle(blogbean.getBefore_middle());
					alogbean.setBefore_big(blogbean.getAfter_big());
					alogbean.setBefore_middle(blogbean.getAfter_middle());
					
					//変更前、変更後の上中目標タイトルを取得
					rabean.setMiddle(alogbean.getAfter_middle());
					for(int i=0;i<middleArray.size();i++) {
						if(alogbean.getAfter_middle() == middleArray.get(i).getId()) {
							alogbean.setAfter_middle_title(middleArray.get(i).getTitle());
							rabean.setMiddle_title(middleArray.get(i).getTitle());
						}
						if(alogbean.getBefore_middle() == middleArray.get(i).getId()) {
							alogbean.setBefore_middle_title(middleArray.get(i).getTitle());
						}
					}
					rabean.setBig(alogbean.getAfter_big());
					for(int i=0;i<bigArray.size();i++) {
						if(alogbean.getAfter_big() == bigArray.get(i).getId()) {
							alogbean.setAfter_big_title(bigArray.get(i).getTitle());
							rabean.setBig_title(bigArray.get(i).getTitle());
						}
						if(alogbean.getBefore_big() == bigArray.get(i).getId()) {
							alogbean.setBefore_big_title(bigArray.get(i).getTitle());
						}
					}
					
					
				}else if(blogbean.getAfter_level().equals("sche")) {//スケジュールから小目標へリセット
					alogbean.setAfter_big(blogbean.getBefore_big());
					alogbean.setAfter_middle(blogbean.getBefore_middle());
					alogbean.setBefore_date(blogbean.getAfter_date());
					
					//変更後の上中目標タイトルを取得
					rabean.setMiddle(alogbean.getAfter_middle());
					for(int i=0;i<middleArray.size();i++) {
						if(alogbean.getAfter_middle() == middleArray.get(i).getId()) {
							alogbean.setAfter_middle_title(middleArray.get(i).getTitle());
							rabean.setMiddle_title(middleArray.get(i).getTitle());
						}
					}
					rabean.setBig(alogbean.getAfter_big());
					for(int i=0;i<bigArray.size();i++) {
						if(alogbean.getAfter_big() == bigArray.get(i).getId()) {
							alogbean.setAfter_big_title(bigArray.get(i).getTitle());
							rabean.setBig_title(bigArray.get(i).getTitle());
						}
					}
					
				}
			}else if(blogbean.getBefore_level().equals("sche")) {
				if(blogbean.getAfter_level().equals("big")) {//大目標からスケジュールへリセット
					alogbean.setAfter_date(blogbean.getBefore_date());
					rabean.setDate(alogbean.getAfter_date());
					
				}else if(blogbean.getAfter_level().equals("middle")) {//中目標からスケジュールへリセット
					alogbean.setAfter_date(blogbean.getBefore_date());
					rabean.setDate(alogbean.getAfter_date());
					alogbean.setBefore_big(blogbean.getAfter_big());
					
					//変更前の上目標タイトルを取得
					for(int i=0;i<bigArray.size();i++) {
						if(alogbean.getBefore_big() == bigArray.get(i).getId()) {
							alogbean.setBefore_big_title(bigArray.get(i).getTitle());
						}
					}
					
					
				}else if(blogbean.getAfter_level().equals("small")){//小目標からスケジュールへリセット
					alogbean.setAfter_date(blogbean.getBefore_date());
					rabean.setDate(alogbean.getAfter_date());
					alogbean.setBefore_big(blogbean.getAfter_big());
					alogbean.setBefore_middle(blogbean.getAfter_middle());
					
					//変更前の上中目標タイトルを取得
					for(int i=0;i<middleArray.size();i++) {
						if(alogbean.getBefore_middle() == middleArray.get(i).getId()) {
							alogbean.setBefore_middle_title(middleArray.get(i).getTitle());
						}
					}
					for(int i=0;i<bigArray.size();i++) {
						if(alogbean.getBefore_big() == bigArray.get(i).getId()) {
							alogbean.setBefore_big_title(bigArray.get(i).getTitle());
						}
					}
					
					
				}else if(blogbean.getAfter_level().equals("sche")) {//スケジュールからスケジュールへリセット
					alogbean.setAfter_date(blogbean.getBefore_date());
					alogbean.setBefore_date(blogbean.getAfter_date());
					rabean.setDate(alogbean.getAfter_date());
				}
				
			}
			
			if(alogbean.getAfter_level().equals("sche") || alogbean.getBefore_level().equals("sche")) {
				
				//スケジュール編集時週のデータも取得し直す
				session.removeAttribute("weekarray");
				weekArray = new ArrayList<>(); 
			}

			
			//idの登録データを編集
			boolean result = SQLOperator.editData(lbean.getUserid(),rabean,weekArray,alogbean);
			

			//データ編集成功時、セッション情報を書き換える
			if(result) {
				
				if(alogbean.getAfter_level().equals("big")) {
					if(alogbean.getBefore_level().equals("big")) { //大目標から大目標への移動時
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
								middleArray.get(i).setBig_title(alogbean.getAfter_title());
								if(alogbean.getHold()==true) //保留時のみ同時に保留
									middleArray.get(i).setHold(alogbean.getHold());
							}
						}
						for(int i=0;i<smallArray.size();i++) {
							if(alogbean.getId() == smallArray.get(i).getBig()) {
								smallArray.get(i).setBig_title(alogbean.getAfter_title());
								if(alogbean.getHold()==true) //保留時のみ同時に保留
									smallArray.get(i).setHold(alogbean.getHold());
							}
						}
							
					}else if(alogbean.getBefore_level().equals("middle")) {//中目標から大目標への移動時
						bigArray.add(rabean); //大目標に追加

						//中目標から削除

						for(int i=0;i<middleArray.size();i++) {
							if(alogbean.getId() == middleArray.get(i).getId()) {
								middleArray.remove(i);
								break;
							}
						}
						for(int i=0;i<smallArray.size();i++) { //中目標下の小目標を保留に
							if(alogbean.getId() == smallArray.get(i).getMiddle()) {
								smallArray.get(i).setBig(0);
								smallArray.get(i).setBig_title("");
								smallArray.get(i).setMiddle(0);
								smallArray.get(i).setMiddle_title("");
								smallArray.get(i).setHold(true);
							}
						}
					}else if(alogbean.getBefore_level().equals("small")) {//小目標から大目標への移動時
						bigArray.add(rabean); //大目標に追加

						for(int i=0;i<smallArray.size();i++) { //小目標から削除
							if(alogbean.getId() == smallArray.get(i).getId()) {
								smallArray.remove(i);
								break;
							}
						}
					}else if(alogbean.getBefore_level().equals("sche")) {//スケジュールから大目標への移動時
						bigArray.add(rabean); //大目標に追加


						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> todayArray = (ArrayList<PlanBean>) session.getAttribute("todayarray");
						
						
						for(int i=0;i<todayArray.size();i++) { //当日のスケジュールから削除
							if(alogbean.getId() == todayArray.get(i).getId()) {
								todayArray.remove(i);
								break;
							}
						}
						session.setAttribute("todayarray", todayArray);
						
						for(int i=0;i<scheArray.size();i++) { 
							if(alogbean.getId() == scheArray.get(i).getId()) {
								scheArray.remove(i); //スケジュールから削除
								break;
							}
						}
					}
					
					
					
				}else if(alogbean.getAfter_level().equals("middle")) {
					if(alogbean.getBefore_level().equals("big")) { //大目標から中目標への移動時
						for(int i=0;i<bigArray.size();i++) { //大目標から削除
							if(alogbean.getId() == bigArray.get(i).getId()) {
								bigArray.remove(i);
								break;
							}
						}
						
						middleArray.add(rabean); //中目標へ追加
						
						for(int i=0;i<middleArray.size();i++) { //大目標下の中目標を保留に
							if(alogbean.getId() == middleArray.get(i).getBig()) {
								middleArray.get(i).setBig(0);
								middleArray.get(i).setBig_title("");
								middleArray.get(i).setHold(true);
							}
						}
						
						for(int i=0;i<smallArray.size();i++) { //大目標下の小目標を保留に
							if(alogbean.getId() == smallArray.get(i).getBig()) {
								smallArray.get(i).setBig(0);
								smallArray.get(i).setBig_title("");
								smallArray.get(i).setMiddle(0);
								smallArray.get(i).setMiddle_title("");
								smallArray.get(i).setHold(true);
							}
						}
						
					
						
					}else if(alogbean.getBefore_level().equals("middle")) { //中目標から中目標へ移動時
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
								smallArray.get(i).setMiddle_title(alogbean.getAfter_title());
								smallArray.get(i).setBig_title(alogbean.getAfter_big_title());
								smallArray.get(i).setBig(alogbean.getAfter_big());
								if(alogbean.getHold()==true) //保留時のみ同時に保留
									smallArray.get(i).setHold(alogbean.getHold());
							}
						}
						
						
						
					}else if(alogbean.getBefore_level().equals("small")) { //小目標から中目標への移動時
						middleArray.add(rabean); //中目標へ追加
						
						for(int i=0;i<smallArray.size();i++) { //小目標から削除
							if(alogbean.getId() == smallArray.get(i).getId()) {
								smallArray.remove(i);
								break;
							}
						}
						
					
						
					}else if(alogbean.getBefore_level().equals("sche")) { //スケジュールから中目標への移動時
						middleArray.add(rabean); //中目標に追加


						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> todayArray = (ArrayList<PlanBean>) session.getAttribute("todayarray");
						
						
						for(int i=0;i<todayArray.size();i++) { //当日のスケジュールから削除
							if(alogbean.getId() == todayArray.get(i).getId()) {
								todayArray.remove(i);
								break;
							}
						}
						session.setAttribute("todayarray", todayArray);
						
						for(int i=0;i<scheArray.size();i++) { 
							if(alogbean.getId() == scheArray.get(i).getId()) {
								scheArray.remove(i); //スケジュールから削除
								break;
							}
						}
					
						
					}
					
					
				
				}else if(alogbean.getAfter_level().equals("small")) {
					if(alogbean.getBefore_level().equals("big")) { //大目標から小目標へ移動
						for(int i=0;i<bigArray.size();i++) { //大目標から削除
							if(alogbean.getId() == bigArray.get(i).getId()) {
								bigArray.remove(i);
							}
						}
						
						for(int i=0;i<middleArray.size();i++) { //大目標下の中目標を保留に
							if(alogbean.getId() == middleArray.get(i).getBig()) {
								middleArray.get(i).setBig(0);
								middleArray.get(i).setBig_title("");
								middleArray.get(i).setHold(true);
							}
						}
						
						for(int i=0;i<smallArray.size();i++) { //大目標下の小目標を保留に
							if(alogbean.getId() == smallArray.get(i).getBig()) {
								smallArray.get(i).setBig(0);
								smallArray.get(i).setBig_title("");
								smallArray.get(i).setMiddle(0);
								smallArray.get(i).setMiddle_title("");
								smallArray.get(i).setHold(true);
							}
						}
						
						smallArray.add(rabean); //小目標へ追加
						
					
						
					}else if(alogbean.getBefore_level().equals("middle")) { //中目標から小目標へ移動
						for(int i=0;i<middleArray.size();i++) {
							if(alogbean.getId() == middleArray.get(i).getId()) { //中目標から削除
								middleArray.remove(i);
								break;
							}
							
						}
						
						for(int i=0;i<smallArray.size();i++) { //中目標下の小目標を保留に
							if(alogbean.getId() == smallArray.get(i).getMiddle()) {
								smallArray.get(i).setBig(0);
								smallArray.get(i).setBig_title("");
								smallArray.get(i).setMiddle(0);
								smallArray.get(i).setMiddle_title("");
								smallArray.get(i).setHold(true);
							}
						}
						smallArray.add(rabean); //小目標へ追加
						
					
						
					}else if(alogbean.getBefore_level().equals("small")) { //小目標から小目標への移動時

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
					}else if(alogbean.getBefore_level().equals("sche")) { //スケジュールから小目標へ移動
						
						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> todayArray = (ArrayList<PlanBean>) session.getAttribute("todayarray");
						
						
						for(int i=0;i<todayArray.size();i++) { //当日のスケジュールから削除
							if(alogbean.getId() == todayArray.get(i).getId()) {
								todayArray.remove(i);
								break;
							}
						}
						session.setAttribute("todayarray", todayArray);
						
						for(int i=0;i<scheArray.size();i++) { 
							if(alogbean.getId() == scheArray.get(i).getId()) {
								scheArray.remove(i); //スケジュールから削除
								break;
							}
						}
						smallArray.add(rabean); //小目標に追加
					
						
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
						if(!todaycheck){//別のスケジュールから当日に移動するときは追加
							PlanBean tbean = new PlanBean();
							tbean.setId(alogbean.getId());
							tbean.setLevel("sche");
							tbean.setTitle(alogbean.getAfter_title());
							tbean.setContent(alogbean.getAfter_content());
							tbean.setDate(rabean.getDate());
							tbean.setHold(alogbean.getHold());
							todayArray.add(tbean);
							session.setAttribute("todayarray", todayArray);
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
						
					}
					
					if(alogbean.getBefore_level().equals("big")) { //大目標からスケジュールへ移動
						for(int i=0;i<bigArray.size();i++) {
							if(alogbean.getId() == bigArray.get(i).getId()) {
								bigArray.remove(i); //大目標から削除
								break;
							}
						}
						scheArray.add(rabean); //スケジュールに追加
						

						for(int i=0;i<middleArray.size();i++) { //大目標下の中目標を保留に
							if(alogbean.getId() == middleArray.get(i).getBig()) {
								middleArray.get(i).setBig(0);
								middleArray.get(i).setBig_title("");
								middleArray.get(i).setHold(true);
							}
						}
						
						for(int i=0;i<smallArray.size();i++) { //大目標下の小目標を保留に
							if(alogbean.getId() == smallArray.get(i).getBig()) {
								smallArray.get(i).setBig(0);
								smallArray.get(i).setBig_title("");
								smallArray.get(i).setMiddle(0);
								smallArray.get(i).setMiddle_title("");
								smallArray.get(i).setHold(true);
							}
						}
						
					
						
					}else if(alogbean.getBefore_level().equals("middle")) { //中目標からスケジュールへ移動
						for(int i=0;i<middleArray.size();i++) {
							if(alogbean.getId() == middleArray.get(i).getId()) {
								middleArray.remove(i); //中目標から削除
							}
						}
						scheArray.add(rabean); //スケジュールに追加
						

						for(int i=0;i<smallArray.size();i++) { //中目標下の小目標を保留に
							if(alogbean.getId() == smallArray.get(i).getMiddle()) {
								smallArray.get(i).setBig(0);
								smallArray.get(i).setBig_title("");
								smallArray.get(i).setMiddle(0);
								smallArray.get(i).setMiddle_title("");
								smallArray.get(i).setHold(true);
							}
						}
					
						
					}else if(alogbean.getBefore_level().equals("small")) { //小目標からスケジュールへ移動
						for(int i=0;i<smallArray.size();i++) {
							if(alogbean.getId() == smallArray.get(i).getId()) {
								smallArray.remove(i); //小目標から削除
							}
						}
						scheArray.add(rabean); //スケジュールに追加
					
						
					}else if(alogbean.getBefore_level().equals("sche")) {
						for(int i=0;i<scheArray.size();i++) {
							if(alogbean.getId() == scheArray.get(i).getId()) {
								//セッションにスケジュール情報を設定
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
