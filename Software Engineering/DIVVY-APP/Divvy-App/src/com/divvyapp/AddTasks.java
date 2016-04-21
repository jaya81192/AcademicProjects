package com.divvyapp;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Servlet implementation class AddTasks

@WebServlet("/AddTasks")

public class AddTasks extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // @see HttpServlet#HttpServlet()
     
    public AddTasks() {
        super();
    }


    /* @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)  */
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	//Get the task name, points and frequency from the user
        String temp_task_name = request.getParameter("taskName");
        String tmp_task_points = request.getParameter("taskPoints");
        String temp_frequency = request.getParameter("taskFrequency");
        String message = "";
        
        float temp_task_points = Float.parseFloat(tmp_task_points);
        	
        	//Establish database connection and insert the task name, points and frequency into the database	
        	DbConnectivity db = new DbConnectivity();
		Connection conn = db.getConnection();
		
		String msg = null;
		try{
			String strQuery = "select * from tasks where taskname='"+temp_task_name+"';";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strQuery);
			//Check for duplicates, insert onle the new ones
			if(rs.next())
			{
				msg = temp_task_name +", is already added!!";
				response.sendRedirect("HomePage.jsp");
			}
			else
			{
				for (int i=0 ; i < 1 ;i++)
				{
					String insertQuery = "insert into tasks(taskname,taskpoints,frequency) values (?,?,?)";
					PreparedStatement preparedStmt = conn.prepareStatement(insertQuery);
					preparedStmt.setString (1, temp_task_name);
			    		preparedStmt.setFloat (2, temp_task_points);
			    		preparedStmt.setString (3,temp_frequency);
			    		preparedStmt.execute();
				}
			    	conn.close();
				response.sendRedirect("HomePage.jsp");		//The new task details are inserted and the connection is closed
			}	
			rs.close();
			st.close();
			conn.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}		
		finally{
		      try{
		         if(conn!=null)
		            conn.close();
		      }
		      catch(SQLException se){
		         se.printStackTrace();
		      }
		}
		response.setContentType("Text/html");
		PrintWriter out = response.getWriter();
		out.println(msg);
    }
}
