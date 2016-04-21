package com.divvyapp;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AssignTasks
 */
@WebServlet("/AssignTasks")
public class AssignTasks extends HttpServlet {
	
	static int assignStatus = 1;	
	
	private static final long serialVersionUID = 1L;
       
    /** @see HttpServlet#HttpServlet() */
    public AssignTasks() 
    {
        super();
    }

	/** @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response) */
	@SuppressWarnings("unused")
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		String temp_assignedtask = request.getParameter("tasktobeassigned1");
		String temp_taskassignee = request.getParameter("taskassignee1");
		String startdate = request.getParameter("startDate");
		String enddate = request.getParameter("endDate");

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy"); 
		Date temp_startdate = null;
		Date temp_enddate = null;

	    try{
	    		// If both start and end date are not selected by the user
			if (startdate != "" && enddate != ""){
				temp_startdate = (Date) df.parse(startdate);
				temp_enddate = (Date) df.parse(enddate);
			}
			// If only start date is selected by the user
			else if (startdate != ""){
				temp_startdate = (Date) df.parse(startdate);
				temp_enddate = null;
			}
			// If only end date is selected by the user
			else if (enddate != ""){
			    	temp_startdate = null;
				temp_enddate = (Date) df.parse(enddate);
			}

	    }
	    catch (ParseException e) {
	        e.printStackTrace();
	    }
	   
	    	String msg = "";
		int point = 0;
		String frequency = null;
		int addfreq=0;
		
		try{
			DbConnectivity db = new DbConnectivity();
			Connection conn = db.getConnection();
			Connection conn1 = db.getConnection();
			Connection conn2 = db.getConnection();
			Connection conn3 = db.getConnection();
			Connection conn4 = db.getConnection();
			Connection conn5 = db.getConnection();

			String getPoints = "select taskpoints,frequency from tasks where taskname='"+temp_assignedtask+"';";
			Statement st1 = conn1.createStatement();
			ResultSet rs1 = st1.executeQuery(getPoints);
			String totpoints = "select sum(taskpoints) from tasks where taskname not in('"+temp_assignedtask+"') and taskname not in(select taskname from taskAssignment where enddate > curdate());";
			Statement st2 = conn2.createStatement();
			ResultSet rs2 = st2.executeQuery(totpoints);
			String tasklist = "select taskname, taskpoints from tasks where taskname not in('"+temp_assignedtask+"') and taskname not in(select taskname from taskAssignment where enddate > curdate());";
			Statement st3 = conn3.createStatement();
			ResultSet rs3 = st3.executeQuery(tasklist);
			try
			{
				while (rs1.next()){
					
					point = rs1.getInt(1);
					
					if (point < 1){
						assignStatus = 0;
						break;
					}
					else{
						assignStatus = 1;
					}
					frequency = rs1.getString(2);	

					if(frequency.equals("Once")){
						addfreq = 0;
					}
					else if(frequency.equals("Daily")){
						addfreq = 1;
					}else if(frequency.equals("Weekly")){
						addfreq = 7;
					}else if(frequency.equals("Monthly")){
						addfreq = 30;
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}		

		if(assignStatus == 1)
		{
			String strQuery = "select * from taskAssignment where taskname='"+temp_assignedtask+"';";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(strQuery);

			
				if (temp_assignedtask == null || temp_taskassignee== null){
					msg = "Please select the Task and Assignee !!";
					response.sendRedirect("HomePage.jsp");
				}
				else{
					if (temp_startdate != null && temp_enddate != null)
					{
						while (temp_startdate.before(temp_enddate))
						{
							java.sql.Date tmp_startdate = new java.sql.Date(temp_startdate.getTime());

							String insertQuery = "insert into taskAssignment(taskname,username,taskpoints,startdate,enddate,completionstatus) values (?,?,?,?,?,?)";
							PreparedStatement preparedStmt = conn.prepareStatement(insertQuery);
							preparedStmt.setString (1, temp_assignedtask);
							preparedStmt.setString (2, temp_taskassignee);
							preparedStmt.setFloat(3, point);
							preparedStmt.setDate(4, tmp_startdate);
							Calendar c = Calendar.getInstance();
							c.setTime(temp_startdate);
							c.add(Calendar.DATE,addfreq);
							temp_startdate = c.getTime();
							java.sql.Date tp_enddate = new java.sql.Date(temp_startdate.getTime());
							preparedStmt.setDate (5, (java.sql.Date) tp_enddate);
							preparedStmt.setInt(6, 0);
							preparedStmt.execute(); 
						}
					}
					else if (temp_startdate == null && temp_enddate == null)
					{
						Date date = new Date();
						Calendar c = Calendar.getInstance();
						c.setTime(date);
						temp_startdate = c.getTime();
						c.add(Calendar.DATE,addfreq);
						temp_enddate = c.getTime();
						java.sql.Date tp_startdate = new java.sql.Date(temp_startdate.getTime());
						java.sql.Date tp_enddate = new java.sql.Date(temp_enddate.getTime());

						String insertQuery = "insert into taskAssignment(taskname,username,taskpoints,startdate,enddate,completionstatus) values (?,?,?,?,?,?)";
						PreparedStatement preparedStmt = conn.prepareStatement(insertQuery);

						preparedStmt.setString (1, temp_assignedtask);
						preparedStmt.setString (2, temp_taskassignee);
						preparedStmt.setFloat (3, point);
						preparedStmt.setDate(4,tp_startdate);
						preparedStmt.setDate(5,tp_enddate);
						preparedStmt.setInt(6, 0);
						preparedStmt.execute(); 
					}
					else if (temp_startdate == null){
						// If start date is null put the start date as system start date
						Date date = new Date();
						Calendar c = Calendar.getInstance();
						c.setTime(date);
						temp_startdate = c.getTime();

						while (temp_startdate.before(temp_enddate))
						{
							String insertQuery = "insert into taskAssignment(taskname,username,taskpoints,startdate,enddate,completionstatus) values (?,?,?,?,?,?)";
							PreparedStatement preparedStmt = conn.prepareStatement(insertQuery);
						
							preparedStmt.setString (1, temp_assignedtask);
							preparedStmt.setString (2, temp_taskassignee);
							preparedStmt.setFloat (3, point);
							
							// If start date is null put the start date as system start date
							java.sql.Date tmp_startdate = new java.sql.Date(temp_startdate.getTime());
							preparedStmt.setDate(4, tmp_startdate);

							// If start date is null put the end date by adding frequency to it
							Calendar c1 = Calendar.getInstance();
							c1.setTime(temp_startdate);
							c1.add(Calendar.DATE,addfreq);
							temp_startdate = c1.getTime();
							java.sql.Date tp_enddate = new java.sql.Date(temp_startdate.getTime());
							preparedStmt.setDate (5, (java.sql.Date) tp_enddate);
							
							preparedStmt.setInt(6, 0);
							preparedStmt.execute();
						}
					}
					else if (temp_enddate == null){
							String insertQuery = "insert into taskAssignment(taskname,username,taskpoints,startdate,enddate,completionstatus) values (?,?,?,?,?,?)";
							PreparedStatement preparedStmt = conn.prepareStatement(insertQuery);
						
							preparedStmt.setString (1, temp_assignedtask);
							preparedStmt.setString (2, temp_taskassignee);
							preparedStmt.setFloat (3, point);
							
							// If start date is null put the start date as system start date
							java.sql.Date tmp_startdate = new java.sql.Date(temp_startdate.getTime());
							preparedStmt.setDate(4, tmp_startdate);

							// If start date is null put the end date by adding frequency to it
							Calendar c = Calendar.getInstance();
							c.setTime(temp_startdate);
							c.add(Calendar.DATE,addfreq);
							temp_startdate = c.getTime();
							java.sql.Date tp_enddate = new java.sql.Date(temp_startdate.getTime());
							preparedStmt.setDate (5, (java.sql.Date) tp_enddate);							
							preparedStmt.setInt(6, 0);
							preparedStmt.execute();	
					}
					
					// Calculating the new points 
					float d;
					rs1.first();
					d= (float) (0.2*rs1.getFloat(1));
					float p=rs1.getFloat(1)-d;
					rs2.first();
					while(rs3.next())
					{
						float x = rs3.getFloat(2);
						x= x+(x/(float)(rs2.getFloat(1)))*d;
						String updquery="update tasks set taskpoints = " + x + " where taskname = '"+rs3.getString(1)+"';";
						Statement st4= conn4.createStatement();
						st4.executeUpdate(updquery);
					}
					String updquery1="update tasks set taskpoints = " + p + " where taskname = '" + temp_assignedtask + "';";
					Statement st5= conn5.createStatement();
					st5.executeUpdate(updquery1);
				}
		}
			    conn.close();	
			    conn1.close();
			    conn2.close();
			    conn4.close();
			    conn5.close();
				response.sendRedirect("HomePage.jsp");
				conn3.close();
			}		
			catch(Exception e){
				e.printStackTrace();
			}		
		response.setContentType("Text/html");
		PrintWriter out = response.getWriter();
		out.println(msg);
	}
	
	
	public int getAssignStatus(){
		return assignStatus;
	}
	
	public void resetAssignStatus(){
		assignStatus = 1;
	}
	
}
