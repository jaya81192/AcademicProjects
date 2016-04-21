package com.divvyapp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Servlet implementation class AdvanceDate
 */
@WebServlet("/AdvanceDate")
public class AdvanceDate extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdvanceDate() {
        super();
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
    
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int diff;
		
		//Read the new date from the tester
		try {
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 
			String strtestdate = request.getParameter("pickdate");
			Date testdate = null;
			testdate = (Date) df.parse(strtestdate);
			
			//Calculate the difference between the system date and the date picked by the tester
			Date date = new Date();
			diff = (int) ((testdate.getTime() - date.getTime()) / (1000 * 60 * 60 * 24))+1;
			

	        java.util.Date temp_date = new java.util.Date();
			temp_date=getDate();
			
			if(temp_date.compareTo(testdate) < 0)
			{    
				//Insert the calculated difference into the database
				//This will be used for future calculations
				DbConnectivity dbc=new DbConnectivity();
				Connection con = dbc.getConnection();
				String insertQuery = "update advancetestdate set datediff=?;";
				PreparedStatement preparedStmt = con.prepareStatement(insertQuery);
				preparedStmt.setInt(1,diff );
				preparedStmt.execute();
				con.close();
			}
			
			//Update the new target points based on the amount of time advanced
			LoginValidation lv = new LoginValidation();
			lv.setTargetPoints();			
			response.sendRedirect("HomePage.jsp");
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public java.util.Date getDate() throws IOException, SQLException, ParseException 
    	{
		DbConnectivity dbc1=new DbConnectivity();
		Connection con1 = dbc1.getConnection();
		String strQuery = "select datediff from advancetestdate;";
		Statement st1 = con1.createStatement();
		ResultSet rs1 = st1.executeQuery(strQuery);
		rs1.next();
        //java.util.Date curr_date1 = new java.util.Date();
        java.util.Date curr_date1 = new java.util.Date();

		int diffDays = rs1.getInt(1);
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(curr_date1);
		cal1.add(Calendar.DATE,diffDays);
  		curr_date1 = cal1.getTime();
		con1.close();
		return curr_date1;
    	}
}	
