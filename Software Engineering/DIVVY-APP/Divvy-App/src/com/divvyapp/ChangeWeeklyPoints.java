package com.divvyapp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ChangeWeeklyPoints
 */
@WebServlet("/ChangeWeeklyPoints")
public class ChangeWeeklyPoints extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /** * @see HttpServlet#HttpServlet()
     */
    public ChangeWeeklyPoints() {
        super();
        // TODO Auto-generated constructor stub
    }


	/*** @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)	 */
    
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DbConnectivity db = new DbConnectivity();
		Connection conn = db.getConnection();
		DbConnectivity db1 = new DbConnectivity();
		Connection conn1 = db.getConnection();
		
		String getWeeklyPtQuery = "select * from weeklypoints";
		Statement st;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(getWeeklyPtQuery);
			
			while(rs.next()){
				int weekptkey = rs.getInt(1);
				float tmpRequiredPoints = rs.getFloat(6);
			
				// Converting int to string
				StringBuilder sb = new StringBuilder();
				sb.append("");
				sb.append(weekptkey);
				String strweekpt = sb.toString();
			
				String str_weekptkey = request.getParameter(strweekpt);
				float float_weekptkey = Float.parseFloat(str_weekptkey);
				if (tmpRequiredPoints == 0)
				{
					tmpRequiredPoints = float_weekptkey;
				}
				String UpdatePoints = "update weeklypoints set pointsperweek=?, requiredpoints=? where weekptkey=?";
				PreparedStatement preparedStmt = conn.prepareStatement(UpdatePoints);
				preparedStmt.setFloat(1, float_weekptkey);
				preparedStmt.setFloat(2, tmpRequiredPoints);
				preparedStmt.setInt(3, weekptkey);
				preparedStmt.execute(); 
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.sendRedirect("HomePage.jsp");
		

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
