<%@page import="com.divvyapp.AdvanceDate"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.sql.*,java.text.*,java.util.*,java.util.Date"%>
<% Class.forName("com.mysql.jdbc.Driver"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<!-- Latest compiled and minified CSS -->
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css">
		<!-- Optional theme -->
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap-theme.min.css">
		<link rel="stylesheet" type="text/css" href="C:\Users\Pranav\Downloads\bootstrap-3.3.0-dist\dist\css\custom.css">
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<script type="text/javascript" src="addUserTaskValidation.js"> </script>
		<link  rel="stylesheet" href="homePageStyle.css" type="text/css"/>
		<link rel="stylesheet" type="text/css" href="style.css" media="screen" />
		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.9/jquery-ui.js"></script>
		<title>Overdue tasks</title>
	</head>
	
	
	<body>
		<% 
			Connection conn = null;      
			String driver = "com.mysql.jdbc.Driver";	
		%>
		<div class="container">
			<nav class="navbar navbar-default navbar-fixed-top" role="navigation">   
    			<div class="container">
    				<form class="form-inline" role="form" action="LoginValidation" method="post">
    					
    					<div class="nav navbar-nav navbar-left">
    						<h2 style="color:steelblue">DIVVY-APP</h2>
    					</div>
    		
    					<div class= "nav navbar-nav navbar-right" style="margin-right:50px">
    						<h3 style="color:steelblue">Welcome <%= session.getAttribute( "theName" ) %></h3>
    					</div>
    					
    				</form>
				</div>
			</nav>
			
			<br><br><br><br><br>
			
			<div class="text-center" style="background:GhostWhite; color:Black;margin-top:30px">
				<div class="table-responsive">
					<form name="showfrm" method="post">
			 			<%  
			 				
			 				Class.forName(driver).newInstance();
            				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + "divvy", "root", "sayali123");
            				Statement statement = conn.createStatement();
            				ResultSet rs;
			        		/*DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    		Date date = new Date();
                    		Calendar c = Calendar.getInstance();
                     		String  temp_dueDate = dateFormat.format(c.getTime());*/
                     		
                            // advances the date
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            java.util.Date curr_date = new java.util.Date();
                            AdvanceDate ad = new AdvanceDate();
                            curr_date  = ad.getDate();
                            String  temp_dueDate = dateFormat.format(curr_date);

							rs = statement.executeQuery( "select username,taskname,endDate from taskAssignment where completionStatus =0 and endDate < '" +temp_dueDate +"' ;");
                     	%>	
    					<table class="table table-hover">
      		      			<tr>
            	    			<th>Assignee</th>
                				<th>Taskname</th>
                				<th>Overdue Date</th>
            				</tr>  
 		       				<%	while(rs.next())
        					{%>
            					<tr>
                					<td><%= rs.getString(1) %></td>
                					<td><%= rs.getString(2) %></td>
                					<td><%= rs.getString(3) %></td>
            					</tr>
           					<%} %>
    					</table>
    				</form>
				</div>
			</div>
		</div>
	</body>
</html>
