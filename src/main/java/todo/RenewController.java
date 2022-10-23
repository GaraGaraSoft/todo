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
 * Servlet implementation class RenewController
 */
@WebServlet("/RenewController")
public class RenewController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RenewController() {
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
		
		//セッションからログイン情報を取得
		HttpSession session = request.getSession();
		LoginBean lbean = (LoginBean) session.getAttribute("loginbean");

		//セッションの各データ配列を削除
		session.removeAttribute("bigarray");
		session.removeAttribute("middlearray");
		session.removeAttribute("smallarray");
		session.removeAttribute("schearray");
		session.removeAttribute("todayarray");
		session.removeAttribute("weekarray");
		session.removeAttribute("logarray");
		
		//新しく各データ配列を生成
		ArrayList<PlanBean> bigArray = new ArrayList<>(); //データ一覧から大目標の配列
		ArrayList<PlanBean> middleArray = new ArrayList<>(); //データ一覧から中目標の配列
		ArrayList<PlanBean> smallArray = new ArrayList<>(); //データ一覧から小目標の配列
		ArrayList<PlanBean> todayArray = new ArrayList<>(); //データ一覧から当日のスケジュール配列
		ArrayList<PlanBean> weekArray = new ArrayList<>(); //データ一覧から一週間のスケジュール配列
		ArrayList<PlanBean> scheArray = new ArrayList<>(); //データ一覧からスケジュール配列
		ArrayList<LogBean> logArray = new ArrayList<>(); //データ一覧から変更ログ配列
		
		
		SQLOperator.getList(bigArray,middleArray,smallArray,todayArray,weekArray,scheArray,lbean.getUserid()); //テーブルから情報を取得して設定
		SQLOperator.getLog(bigArray,middleArray,logArray,lbean.getUserid());//ログから情報を取得して設定
		//セッションに各データを登録
		session.setAttribute("bigarray", bigArray);
		session.setAttribute("middlearray", middleArray);
		session.setAttribute("smallarray", smallArray);
		session.setAttribute("todayarray",todayArray);
		session.setAttribute("weekarray", weekArray);
		session.setAttribute("schearray", scheArray);
		session.setAttribute("logarray", logArray);
		
		//トップ画面にフォワード処理
		ServletContext application = getServletContext();
		RequestDispatcher rd = application.getRequestDispatcher("/jsp/top.jsp");
		rd.forward(request, response);
		
		
	}

}
