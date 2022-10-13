package todo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

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
		response.getWriter().append("Served at: ").append(request.getContextPath());
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
			
			
			//編集データをpbeanデータ化
			PlanBean pbean = new PlanBean();
			pbean.setId(id);
			
			//編集するデータの目標レベル
			String level = request.getParameter("level");
			pbean.setLevel(level);
			
			//編集する各データの取得
			String title = request.getParameter("title"); //タイトル取得
			pbean.setTitle(title);
			String content = request.getParameter("content"); //詳細取得
			pbean.setContent(content);
			if(level.equals("middle")) { //中目標選択時の大目標データ取得
				int big = Integer.parseInt(request.getParameter("big_ReadyMade"));
				pbean.setBig(big);
			}else if(level.equals("small")) {
				String big_middle = request.getParameter("middle_ReadyMade");
				System.out.println("bm:"+big_middle);
				String[] bm = big_middle.split(",");
				int middle = Integer.parseInt(bm[0]);
				pbean.setMiddle(middle);
				int big = Integer.parseInt(bm[1]);
				pbean.setBig(big);
				
			}else if(level.equals("sche")) {
				int year = Integer.parseInt(request.getParameter("year"));
				pbean.setYear(year);
				int month = Integer.parseInt(request.getParameter("month"));
				pbean.setMonth(month);
				int day = Integer.parseInt(request.getParameter("day"));
				pbean.setDay(day);
			}
			
			
			
			
			//idの登録データを編集
			boolean result = SQLOperator.editData(lbean.getUserid(),pbean);
			
			//データ編集成功時、セッション情報を書き換える
			if(result) {
				
				if(level.equals("big")) {
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> bigArray = (ArrayList<PlanBean>) session.getAttribute("bigarray");
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> middleArray = (ArrayList<PlanBean>) session.getAttribute("middlearray");
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> smallArray = (ArrayList<PlanBean>) session.getAttribute("smallarray");
					//セッションの大目標のデータを編集
					for(int i=0;i<bigArray.size();i++) {
						if(id == bigArray.get(i).getId()) {
							bigArray.get(i).setTitle(title);
							bigArray.get(i).setContent(content);
							break;
						}
					}
					//セッション中、小目標の上位データを編集
					
					for(int i=0;i<middleArray.size();i++) {
						if(id == middleArray.get(i).getBig()) {
							middleArray.get(i).setBig_title(title);
							
						}
					}
					for(int i=0;i<smallArray.size();i++) {
						if(id == smallArray.get(i).getBig()) {
							smallArray.get(i).setBig_title(title);
						}
					}

					session.setAttribute("smallarray", smallArray);
					session.setAttribute("middlearray", middleArray);
					session.setAttribute("bigarray", bigArray);
					
				}else if(level.equals("middle")) {

					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> bigArray = (ArrayList<PlanBean>) session.getAttribute("bigarray");
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> middleArray = (ArrayList<PlanBean>) session.getAttribute("middlearray");
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> smallArray = (ArrayList<PlanBean>) session.getAttribute("smallarray");
					
					//セッションの大目標からタイトル取得
					for(int i=0;i<bigArray.size();i++) {
						if(pbean.getBig() == bigArray.get(i).getId()) {
							pbean.setBig_title(bigArray.get(i).getTitle());
							break;
						}
					}
					
					//セッションの中目標の上位データ、上位タイトル、タイトル、内容を編集
					for(int i=0;i<middleArray.size();i++) {
						if(id == middleArray.get(i).getId()) {
							middleArray.get(i).setTitle(title);
							middleArray.get(i).setContent(content);
							middleArray.get(i).setBig(pbean.getBig());
							middleArray.get(i).setBig_title(pbean.getBig_title());
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
						}
					}
					session.setAttribute("smallarray", smallArray);
					session.setAttribute("middlearray", middleArray);
					
				
				}else if(level.equals("small")) {
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> bigArray = (ArrayList<PlanBean>) session.getAttribute("bigarray");
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> middleArray = (ArrayList<PlanBean>) session.getAttribute("middlearray");
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> smallArray = (ArrayList<PlanBean>) session.getAttribute("smallarray");

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
							smallArray.get(i).setTitle(title);
							smallArray.get(i).setContent(content);
							smallArray.get(i).setBig(pbean.getBig());
							smallArray.get(i).setBig_title(pbean.getBig_title());
							smallArray.get(i).setMiddle(pbean.getMiddle());
							smallArray.get(i).setMiddle_title(pbean.getMiddle_title());
							System.out.println("b:"+smallArray.get(i).getBig());
							System.out.println("m:"+smallArray.get(i).getMiddle());
							break;
						}
					}
					
					session.setAttribute("smallarray", smallArray);
					
				}else if(level.equals("sche")) {
					Calendar cal = Calendar.getInstance();
					if(
							pbean.getYear() == cal.get(Calendar.YEAR) &&
							pbean.getMonth() == cal.get(Calendar.MONTH)+1 &&
							pbean.getDay() == cal.get(Calendar.DATE)) { //当日のスケジュールのセッション情報を編集
						
						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> todayArray = (ArrayList<PlanBean>) session.getAttribute("todayarray");
						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> scheArray = (ArrayList<PlanBean>) session.getAttribute("schearray");
						
						boolean todaycheck = false;
						
						for(int i=0;i<todayArray.size();i++) { //当日の情報変更
							if(id == todayArray.get(i).getId()) {
								todayArray.get(i).setTitle(title);
								todayArray.get(i).setContent(content);
								todayArray.get(i).setYear(pbean.getYear());
								todayArray.get(i).setMonth(pbean.getMonth());
								todayArray.get(i).setDay(pbean.getDay());
								session.setAttribute("todayarray", todayArray);
								todaycheck = true;
								
								break;
							}
						}
						
						for(int i=0;i<scheArray.size();i++) {
							if(id == scheArray.get(i).getId()) {
								
								scheArray.get(i).setTitle(title);
								scheArray.get(i).setContent(content);
								scheArray.get(i).setYear(pbean.getYear());
								scheArray.get(i).setMonth(pbean.getMonth());
								scheArray.get(i).setDay(pbean.getDay());
								session.setAttribute("schearray", scheArray);
								if(!todaycheck){
									PlanBean tbean = new PlanBean();
									tbean.setTitle(title);
									tbean.setContent(content);
									tbean.setYear(pbean.getYear());
									tbean.setMonth(pbean.getMonth());
									tbean.setDay(pbean.getDay());
									todayArray.add(tbean);
									session.setAttribute("todayarray", todayArray);
								}
								break;
							}
						}
						
					}else { //当日以降のスケジュールのセッション情報
						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> todayArray = (ArrayList<PlanBean>) session.getAttribute("todayarray");
						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> scheArray = (ArrayList<PlanBean>) session.getAttribute("schearray");
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
								scheArray.get(i).setYear(pbean.getYear());
								scheArray.get(i).setMonth(pbean.getMonth());
								scheArray.get(i).setDay(pbean.getDay());
								session.setAttribute("schearray", scheArray);
								break;
							}
						}
						
					}
					
				}
				
			}

			//トップ画面にフォワード処理
			ServletContext application = getServletContext();
			RequestDispatcher rd = application.getRequestDispatcher("/jsp/top.jsp");
			rd.forward(request, response);
		}else if(request.getParameter("action").equals("del")) {

			//削除するデータの目標レベル
			String level = request.getParameter("level");
			//削除するデータの大目標データ
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
				
			}
			System.out.println("よっしゃ！"+big);
			
			
			//idの登録データを削除
			int result = SQLOperator.deleteData(id,lbean.getUserid(),level,big);
			
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
							smallArray.get(i).setMiddle_title("");
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
							smallArray.get(i).setMiddle(0);
							smallArray.get(i).setBig_title("");
							smallArray.get(i).setMiddle_title("");
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
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> scheArray = (ArrayList<PlanBean>) session.getAttribute("schearray");
					for(int i=0;i<scheArray.size();i++) {
						if(id == scheArray.get(i).getId()) {
							scheArray.remove(i);
							break;
						}
					}
					session.setAttribute("schearray", scheArray);
					
				}
				
				
			}
			

			//トップ画面にフォワード処理
			ServletContext application = getServletContext();
			RequestDispatcher rd = application.getRequestDispatcher("/jsp/top.jsp");
			rd.forward(request, response);
			
		}
		
	}

}
