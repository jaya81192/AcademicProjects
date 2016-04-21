package com.divvyapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet implementation class CompleteTask */
@
WebServlet("/CompleteTask")
public class CompleteTask extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /** @see HttpServlet#HttpServlet()   */
    public CompleteTask() {
        super();
    }

    /*** @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
     * Marks the task completion   */
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	String temp_taskassignid = request.getParameter("taskassignid");
	
    	// Updates the database with the completionStatus as 1 if the user clicks the "Done" button

    	try {
			
    		// Changes the status of the task as completed in taskassignment table
    		DbConnectivity db = new DbConnectivity();
			Connection conn = db.getConnection();
            String updateTaskQuery = "update taskassignment set completionstatus=1 where taskassignid=? ";
            PreparedStatement preparedStmt = conn.prepareStatement(updateTaskQuery);
            preparedStmt.setString(1, temp_taskassignid);
            preparedStmt.execute();
            conn.close();
            
            // Get the points of the completed task from taskassignment table
        	DbConnectivity db1 = new DbConnectivity();
			Connection conn1 = db1.getConnection();
        	String selectTaskQuery = "select * from taskassignment where taskassignid="+temp_taskassignid+";";            
            Statement st1 = conn1.createStatement();
        	ResultSet rs1 = st1.executeQuery(selectTaskQuery);
        	float taskpoints=0;
        	String nameofuser= null;
        	
            while (rs1.next()){
            	taskpoints= rs1.getFloat(4);
            	nameofuser=rs1.getString(2);
            }
        	conn1.close();
        	
        	// Get the points of the completed task from taskassignment table

            DbConnectivity db2 = new DbConnectivity();
			Connection conn2 = db2.getConnection();
            String selectPointsQuery = "select * from weeklypoints where username='"+nameofuser+"';";
            Statement st2 = conn2.createStatement();
        	ResultSet rs2 = st2.executeQuery(selectPointsQuery);
        	float completedpoints=0;
        	float requiredpoints=0;
        	
            while (rs2.next()){            	
            	completedpoints= rs2.getFloat(5);
            	requiredpoints= rs2.getFloat(6);                
            }
        	conn2.close();
        	
            
        	// Add the points of the completed task to the total users point
        	// Subtract the points of the completed task from users required point

            DbConnectivity db3 = new DbConnectivity();
			Connection conn3 = db3.getConnection();
            String updatePointsQuery = "update weeklypoints set completedpoints=?,requiredpoints=? where username='"+nameofuser+"';";
            PreparedStatement preparedStmt2 = conn3.prepareStatement(updatePointsQuery);
            preparedStmt2.setFloat(1, completedpoints+taskpoints);
            preparedStmt2.setFloat(2, requiredpoints-taskpoints);
            preparedStmt2.execute();
            conn3.close();         
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.sendRedirect("HomePage.jsp");
    }
}
