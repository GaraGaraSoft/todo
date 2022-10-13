package todo;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LoginController
 */
@WebServlet("/LoginController")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginController() {
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
			//フォームからのログイン情報を取得しModelへ配置
			LoginBean lbean = new LoginBean();
			lbean.setUserid(request.getParameter("userId"));
			lbean.setPassword(request.getParameter("password"));
			//ログイン判定（ユーザーID、パスワードがデータベースに存在するか）
			boolean chk = LoginBean.loginCheck(lbean);
			if(chk) {
				//ログイン成功時、セッションにログインデータを登録
				HttpSession session = request.getSession();
				session.setAttribute("loginbean", lbean);
				//ログインユーザのTODOリストデータ一覧を読み込む
				ToDoBean tdbean = new ToDoBean();
				//ユーザー用のデータテーブルを確認、無かったら作成
				String table = "todo_"+lbean.getUserid();
				SQLOperator.checkTable(table);
				
				ArrayList<PlanBean> bigArray = new ArrayList<>(); //データ一覧から大目標の配列
				ArrayList<PlanBean> middleArray = new ArrayList<>(); //データ一覧から中目標の配列
				ArrayList<PlanBean> smallArray = new ArrayList<>(); //データ一覧から小目標の配列
				ArrayList<PlanBean> todayArray = new ArrayList<>(); //データ一覧から当日のスケジュール配列
				ArrayList<PlanBean> scheArray = new ArrayList<>(); //データ一覧からスケジュール配列
				SQLOperator.getList(bigArray,middleArray,smallArray,todayArray,scheArray,tdbean,table); //テーブルから情報を取得して設定
				//セッションに各データを登録
				session.setAttribute("bigarray", bigArray);
				session.setAttribute("middlearray", middleArray);
				session.setAttribute("smallarray", smallArray);
				session.setAttribute("todayarray",todayArray);
				session.setAttribute("schearray", scheArray);
				session.setAttribute("tdbean", tdbean);

				System.out.println("111");
				
				//トップ画面にフォワード処理
				ServletContext application = getServletContext();
				RequestDispatcher rd = application.getRequestDispatcher("/jsp/top.jsp");
				rd.forward(request, response);
			}else {
				//ユーザー名、パスワードが一致しなかった時
				request.setAttribute("error", 1);
				RequestDispatcher rd = request.getRequestDispatcher("/jsp/index.jsp");
				rd.forward(request, response);
			}
		}catch(Exception e) {
			//エラー時
			request.setAttribute("error", 2);
			RequestDispatcher rd = request.getRequestDispatcher("/jsp/index.jsp");
			rd.forward(request, response);
		}
		
	}

}
