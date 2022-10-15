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
 * Servlet implementation class ToDoController
 */
@WebServlet("/ToDoController")
public class ToDoController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ToDoController() {
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
		
		try {
			//フォームデータを取得してModelへ入力
			PlanBean pbean = new PlanBean();
			
			//更新データログをModelへ入力
			LogBean logbean = new LogBean();
			logbean.setOpe("insert");
			
			String title = request.getParameter("title");
			pbean.setTitle(title);
			logbean.setAfter_title(title);
			String level = request.getParameter("level");
			pbean.setLevel(level);
			logbean.setAfter_level(level);
			String content = request.getParameter("content");
			pbean.setContent(content);
			logbean.setAfter_content(content);
			
			//セッションを取得
			HttpSession session = request.getSession();
			LoginBean lbean = (LoginBean) session.getAttribute("loginbean");
			
			@SuppressWarnings("unchecked")
			ArrayList<PlanBean> bigArray = (ArrayList<PlanBean>) session.getAttribute("bigarray");
			@SuppressWarnings("unchecked")
			ArrayList<PlanBean> middleArray = (ArrayList<PlanBean>) session.getAttribute("middlearray");
			
			
			
			//中目標以下を入れるとき大目標を取得
			if(level.equals("middle")) {
				int big = Integer.parseInt(request.getParameter("big_ReadyMade"));
				pbean.setBig(big);
				logbean.setAfter_big(big);
				
				for(PlanBean b:bigArray) {
					System.out.println("大のID"+b.getId());
					if(pbean.getBig() == b.getId()) {
						pbean.setBig_title(b.getTitle());
						logbean.setAfter_big_title(b.getTitle());
					break;
					}
				}
				
			}
			//小目標を入れるとき中目標を取得
			if(level.equals("small")) {
				String big_middle = request.getParameter("middle_ReadyMade");
				String[] bm = big_middle.split(",");
				int middle = Integer.parseInt(bm[0]);
				int big = Integer.parseInt(bm[1]);
				pbean.setMiddle(middle);
				logbean.setAfter_middle(middle);
				pbean.setBig(big);
				logbean.setAfter_big(big);

				for(PlanBean b:bigArray) {
					System.out.println("大のID"+b.getId());
					if(pbean.getBig() == b.getId()) {
						pbean.setBig_title(b.getTitle());
						logbean.setAfter_big_title(b.getTitle());
					break;
					}
				}
				
				for(PlanBean m:middleArray) {
					System.out.println("中のID"+m.getId());
					if(pbean.getMiddle() == m.getId()) {
						pbean.setMiddle_title(m.getTitle());
						logbean.setAfter_middle_title(m.getTitle());
					break;
					}
				}
			}
			
			//予定を入れるとき年月日を取得
			if(level.equals("sche")) {
				String date = request.getParameter("year")+"-"+request.getParameter("month")+"-"+request.getParameter("day");
				pbean.setDate(date);
				logbean.setAfter_date(date);
			}
			
			
			//入力情報をデータベースに登録
			boolean result = SQLOperator.setNewData(pbean,logbean,lbean.getUserid());
			
			logbean.setId(pbean.getId());
			
			//sessionに情報を再セット
			if(result) {
				
				if(level.equals("big")) {
					bigArray.add(pbean);
					session.setAttribute("bigarray", bigArray);
					
				}else if(level.equals("middle")) {
					middleArray.add(pbean);
					session.setAttribute("middlearray", middleArray);
					
				}else if(level.equals("small")) {
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> smallArray = (ArrayList<PlanBean>) session.getAttribute("smallarray");
					smallArray.add(pbean);
					session.setAttribute("smallarray", smallArray);
					
				}else if(level.equals("sche")) {
					Calendar cal = Calendar.getInstance();
		
					if(
						Integer.parseInt(pbean.getDate().substring(0,4))  == cal.get(Calendar.YEAR) &&
						Integer.parseInt(pbean.getDate().substring(5,7)) == cal.get(Calendar.MONTH)+1 &&
						Integer.parseInt(pbean.getDate().substring(8,10)) == cal.get(Calendar.DATE)) {
						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> todayArray = (ArrayList<PlanBean>) session.getAttribute("todayarray");
						todayArray.add(pbean);
						session.setAttribute("todayarray", todayArray);
						@SuppressWarnings("unchecked")
						ArrayList<PlanBean> scheArray = (ArrayList<PlanBean>) session.getAttribute("schearray");
						scheArray.add(pbean);
						session.setAttribute("schearray", scheArray);
							
						
					}else {
					@SuppressWarnings("unchecked")
					ArrayList<PlanBean> scheArray = (ArrayList<PlanBean>) session.getAttribute("schearray");
					scheArray.add(pbean);
					session.setAttribute("schearray", scheArray);
						
					}
					
					
				}
				//ログデータをセッション情報に登録
				@SuppressWarnings("unchecked")
				ArrayList<LogBean> logArray = (ArrayList<LogBean>) session.getAttribute("logarray");
				Collections.reverse(logArray);
				logArray.add(logbean);
				session.setAttribute("logarray", logArray);
			}
			//トップ画面にフォワード処理
			ServletContext application = getServletContext();
			RequestDispatcher rd = application.getRequestDispatcher("/jsp/top.jsp");
			rd.forward(request, response);
		}catch(Exception e) {
			//エラー時
			request.setAttribute("error", 1);
			RequestDispatcher rd = request.getRequestDispatcher("/jsp/top.jsp");
			rd.forward(request, response);
		}
	}

}
