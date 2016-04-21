package com.divvyapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JOptionPane;

/**
 * Servlet implementation class LoginValidation
 */
@
WebServlet(description = "This servlet will validate the user credentials", urlPatterns = {
    "/LoginValidation"
})
public class LoginValidation extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginValidation() {
        super();
        // TODO Auto-generated constructor stub
    }

 
    /**
     * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
    * Checks if the user is already a SignedUp user or a new user. Lets SignedUp users to login after verifying their credentials and does not allow new users who have not signed up to login directly 
     */
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        String temp_username = request.getParameter("username");
        String temp_password = request.getParameter("userpassword");
        String message = "";
        request.getSession().setAttribute("theName", temp_username);
   
        try 
        {
        	DbConnectivity db = new DbConnectivity();
        	Connection conn = db.getConnection();
    		String strQuery = "select * from users where username='" + temp_username + "' and password='" + temp_password + "';";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(strQuery);

            // Checks the database with the given credentials. If it is present, moves to Homepage.
            if (rs.first()){
                message = "Hello " + temp_username + ", your login is successful!!";

                // Add the weekly points to target points
                
                setTargetPoints();
                
                response.sendRedirect("HomePage.jsp");
            } 
            
            // If credentials are not available in database, returns to the same page.
            else {
                message = "Hello " + temp_username + ", your login failed !!";
                response.sendRedirect("LogIn.jsp");
            }
            conn.close();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setContentType("Text/html");
        PrintWriter out = response.getWriter();
        out.println(message);
    }
    
    public void setTargetPoints() throws IOException, SQLException, ParseException, InterruptedException 
    {
    	DbConnectivity db1 = new DbConnectivity();
    	DbConnectivity db2 = new DbConnectivity();
    	DbConnectivity db3 = new DbConnectivity();

        // Checks the current date and and insert the 
        
        // Get the maximum date from weeklypoints table
        // and get the current date from java
    
        Connection conn1 = db1.getConnection();
        String getMaxDateQuery = "select nextdate from weeklypoints group by nextdate";
        Statement st1 = conn1.createStatement();
        ResultSet rs1 = st1.executeQuery(getMaxDateQuery);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

        // advances the date
        java.util.Date curr_date = new java.util.Date();
        AdvanceDate ad = new AdvanceDate();
        curr_date  = ad.getDate();
    	
    	rs1.next();
        String p = df.format(rs1.getDate(1));
        Date temp = (Date) df.parse(p);
        conn1.close();
        rs1.close();
        java.sql.Date newDate = null;

        // Get username and latest date from weeklypoints table 
    
        Connection conn2 = db2.getConnection();
        String getUsersQuery = "select * from weeklypoints";
        Statement st2 = conn2.createStatement();
    	Calendar cal = Calendar.getInstance();
    	float pointCounter = 0;
        // Checks if current date is greater than the latest date in table
        while(curr_date.compareTo(temp) > 0)
        {
        	cal.setTime(temp);
        	cal.add(Calendar.DATE,7);
        	temp = cal.getTime();
        	newDate = new java.sql.Date(temp.getTime());
        	pointCounter++;
        }

        if(newDate != null)
        {
        	Connection conn3 = db3.getConnection();
    		String insertWeeklyPoints="update weeklypoints set nextdate=?,targetpoints=?,requiredpoints=? where username = ?";
    		String user = null;
    		float targetpoints = 0, completedpoints = 0,requiredpoints;

        	ResultSet rs2 = st2.executeQuery(getUsersQuery);
    		while(rs2.next())
        	{
        		user = rs2.getString(2);
        		targetpoints=rs2.getFloat(4)+rs2.getFloat(7)*pointCounter;
        		completedpoints=rs2.getFloat(5);
        		requiredpoints=rs2.getFloat(6);
      
        		PreparedStatement preparedStmt = conn3.prepareStatement(insertWeeklyPoints);
				preparedStmt.setDate (1, newDate);
				preparedStmt.setFloat (2, targetpoints);
				preparedStmt.setFloat (3, targetpoints-completedpoints);
				preparedStmt.setString (4, user);
				preparedStmt.execute();
        	}
        conn3.close();
        }
    }
}
