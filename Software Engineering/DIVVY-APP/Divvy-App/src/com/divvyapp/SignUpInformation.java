package com.divvyapp;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
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
 * Servlet implementation class SignUp/Registration
 */
@
WebServlet(description = "This servlet will update the user details into the database", urlPatterns = {
    "/SignUpInformation"
})
public class SignUpInformation extends HttpServlet {
    //private static final long serialVersionUID = 1L;
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignUpInformation() {
        super();
        // TODO Auto-generated constructor stub
    }


    /**
     * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
     * Allows users to signUp with the application
     */
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        DbConnectivity db = new DbConnectivity();
    	Connection conn = db.getConnection();
    	
    	Connection conn1 = db.getConnection();
    	
    	Connection conn2 = db.getConnection();
        
    	String temp_username = request.getParameter("InputUsername");
        String temp_firstname = request.getParameter("InputFirstName");
        String temp_lastname = request.getParameter("InputLastName");
        String temp_email = request.getParameter("InputEmail");
        String temp_password = request.getParameter("InputPassword");
        String message = "";
        try {
        	
        	String strQuery = "select * from users where username='" + temp_username + "';";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(strQuery);
	
            // Duplicate usernames are checked for in the databases. If the username is already present, redirects to the SignUpPage and requests to sign up with all the details once again.
 
            if (rs.first()) {
                message = temp_username + " is already added!!";
                response.sendRedirect("SignUpPage.jsp");
            }
            
            // Inserts the user information to the database
            else{
                String insertUserQuery = "insert into users(username,firstname,lastname,email,password) values (?,?,?,?,?)";
                PreparedStatement preparedStmt = conn.prepareStatement(insertUserQuery);
                preparedStmt.setString(1, temp_username);
                preparedStmt.setString(2, temp_firstname);
                preparedStmt.setString(3, temp_lastname);
                preparedStmt.setString(4, temp_email);
                preparedStmt.setString(5, temp_password);
                preparedStmt.execute();
                
                // Getting the value of last day of week
                
                String getDateQuery = "select max(weekptkey),nextdate from weeklypoints;";
                Statement st1 = conn1.createStatement();
                ResultSet rs1 = st1.executeQuery(getDateQuery);
                rs1.next();
   
                
                // Inserting user information into weeklypoint table
                // Setting the default recurring weekly points value to 20 
                
                String insertWeekDataQuery = "insert into weeklypoints(weekptkey,username,nextdate,targetpoints,completedpoints,requiredpoints,pointsperweek) values (?,?,?,0,0,0,0)";
                PreparedStatement preparedStmt2 = conn2.prepareStatement(insertWeekDataQuery);
                preparedStmt2.setInt(1, rs1.getInt(1)+1);
                preparedStmt2.setString(2, temp_username);
                preparedStmt2.setDate(3, rs1.getDate(2));
                
                preparedStmt2.execute();
                
                conn.close();
                conn1.close();
                conn2.close();
                response.sendRedirect("LogIn.jsp");
            }
            rs.close();
        } catch (SQLException ex) {
            message = "ERROR: " + ex.getMessage();
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        response.setContentType("Text/html");
        PrintWriter out = response.getWriter();
        out.println(message);
    }
}
