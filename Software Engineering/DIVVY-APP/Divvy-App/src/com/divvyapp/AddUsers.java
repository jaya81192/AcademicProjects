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

/** Servlet implementation class AddUsers */

@WebServlet("/AddUsers")
public class AddUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    // @see HttpServlet#HttpServlet()
    public AddUsers() {
        super();
    }

    
    	// Allows users to create a group and add other users
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


		//Get the user name and group name from the user
		String temp_user_name = request.getParameter("inputusername");
		String temp_group_name = request.getParameter("inputgroupname");
		String msg = "";
		DbConnectivity db = null ;
		Connection conn = null;

		try
		{			
			db = new DbConnectivity();
			conn = db.getConnection();
			
			// Insert groups ino database
			String insertQuery = "insert into usergroup(groupname,username) values (?,?)";
			PreparedStatement preparedStmt = conn.prepareStatement(insertQuery);
			preparedStmt.setString (1, temp_group_name);
			preparedStmt.setString (2, temp_user_name);
			preparedStmt.execute();
			response.sendRedirect("HomePage.jsp");		
			conn.close();
			////The program redirects to the homepage after inserting the group and closes the connection
		}
		
		catch(Exception e)
		{
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
