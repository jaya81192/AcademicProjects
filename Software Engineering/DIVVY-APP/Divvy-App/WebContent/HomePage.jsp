<%@page import="com.divvyapp.AdvanceDate"%>
<%@page import="com.divvyapp.AssignTasks"%>
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
		<script type="text/javascript" src="ValidatingAddTask.js"> </script>
		<script type="text/javascript" src="DateValidate.js"> </script>
		<script>
			function isNumberKey(event)
			{
			var charCode = (evt.which) ? evt.which : event.keyCode
			if (charCode > 31 && (charCode < 48 || charCode > 57))
				return false;
			return true;
			}
		</script>
		<link  rel="stylesheet" href="homePageStyle.css" type="text/css"/>
		<link rel="stylesheet" type="text/css" href="style.css" media="screen" />
		
		<!-------------------------------------------------------------------------------------------->
		<!--  	This part of code displays the calender dates to choose the start date and end date	-->
		<!--  	of the task assignment 																-->
		<!-------------------------------------------------------------------------------------------->

		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.9/jquery-ui.js"></script>

		<script>
			$(function() 
			{
		    	$( "#datepicker" ).datepicker();
			}
			);
			$(function() 
			{
		    	$( "#datepicker1" ).datepicker();
			}
			);
			$(function() 
			{
				$( "#datepicker2" ).datepicker();
			}
			);
		</script>
		
		<style> 
			.ui-datepicker th { background-color: #CCCCFF; }
		</style>
	
		<!-- ---------------------------------------------------------------------------------------- -->

		<title>DIVVY-APP</title>
	</head>
	
	
	
	<body>
	<%
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/divvy","root", "Shraddha1");
		Connection conn1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/divvy","root", "Shraddha1");
		Connection conn2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/divvy","root", "Shraddha1");
		Connection conn3 = DriverManager.getConnection("jdbc:mysql://localhost:3306/divvy","root", "Shraddha1");
		Connection conn4 = DriverManager.getConnection("jdbc:mysql://localhost:3306/divvy","root", "Shraddha1");
		Statement statement = conn.createStatement();
	    Statement statement1 = conn1.createStatement();
	    ResultSet resultset;
	%>

	<!-------------------------------------------------------------------------------------------->
	<!--  	Latest compiled and minified JavaScript												-->
	<!--  						 																-->
	<!-------------------------------------------------------------------------------------------->
	
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
		<div class="container-fluid" style="border:1px solid black;" >
			<nav class="navbar navbar-default navbar-fixed-top" role="navigation" style="background:#0B5E80">   
    			<div class="container" >
    				<form class="form-inline" role="form" action="LogIn.jsp" method="post">
    					<div class="nav navbar-nav navbar-left">
    						<h2 style="color:White;">DIVVY-APP</h2>
    					</div>
    					<div class= "nav navbar-nav navbar-right">
							<h3 style="color:White">Welcome <%= session.getAttribute( "theName" ) %> &nbsp; &nbsp;	&nbsp; &nbsp;	
    							<input id="logout" type="submit" class="btn btn-success" value="Logout" style="align:right; position:absolute; top:20px; left:1250px; background:linear-gradient(to bottom,#5bc0de 0,#2aabd2 100%);">
							</h3>
    					</div>
    				</form>
				</div>
			</nav>
			<div class="row" style="background: ; color:Black; margin-top:30px; "> 
				<br>
				<!-------------------------------------------------------------------------------------------->
				<!--  	This part displays two drop down boxes displaying name of tasks and					-->
				<!--  	names of users and start date and end date boxes to assign the task					-->
				<!--  	to the users users for particular duration											-->
				<!-------------------------------------------------------------------------------------------->
				<div class="col-xs-6 col-md-6" style="width:300px;">
				<div class="col-xs-4" style="width:300px; ">		
					<div class="assigntask" style="width:300px">
						<h4><br>Assign Task &nbsp; &nbsp; &nbsp; </h4>
						<h3></h3>
						<form class="form-horizontal" name="assigntaskfrm" action="AssignTasks" method="post" role="form" onSubmit="return validateDate()">
							<%
								statement = conn.createStatement();
								statement1 = conn1.createStatement();
								int rowcounts = 0;
								resultset = statement.executeQuery("select taskname from tasks where taskname not in (select taskname from taskAssignment) or taskname not in (select taskname from taskAssignment where enddate > curdate())") ;
								ResultSet resultset1 = statement1.executeQuery("select username from users") ; 
							%>
							<div class="form-group">
								<label for="taskPoints" class="col-sm-2 control-label">Tasks</label>							
								<div class="col-sm-6">
									<select name="tasktobeassigned1" style="border:1px solid black; border-radius:5px; height:30px; padding:4px 0;">
										<%while(resultset.next()){%>
										<option name="tasktobeassigned"  placeholder="Tasks"><%= resultset.getString(1) %></option>
										<% } %>
									</select>
								</div>
							</div>
							
											
							<div class="form-group">
								<label for="assignee" class="col-sm-2 control-label">Assignee</label>							
								<div class="col-sm-6">
									<select name="taskassignee1" style="border:1px solid black; border-radius:5px; height:30px; width:135px;">
										<%while(resultset1.next()){%>
											<option name="taskassignee" placeholder="User"><%= resultset1.getString(1) %></option>
										<% } %>
									</select>
								</div>
							</div>
							
							
							<div class="form-group">
								<label for="assignee" class="col-sm-2 control-label">Start</label>							
								<div class="col-sm-6">
									<input class=col-sm-6 name="startDate" type="text" style="border:1px solid black; border-radius:5px; width:135px; height:30px;" id="datepicker" placeholder="Start Date".ui-datepicker th { background-color: black; }">	
								</div>
							</div>
							
							<div class="form-group">
								<label for="assignee" class="col-sm-2 control-label">End </label>							
								<div class="col-sm-6">
									<input class=col-sm-6 name="endDate" type="text" style="border:1px solid black; border-radius:5px; width:135px; height:30px;" id="datepicker1" placeholder="End Date".ui-datepicker th { background-color: black; }">								</div>
								</div>
							</div>
							
							<% AssignTasks at = new AssignTasks();
						   	int v = at.getAssignStatus();
						   	if (v == 0){%>  
								<% at.resetAssignStatus(); %>
									<p style="color:red"> Task points are very low. Please assign another task. </p>
							<% }%>
							
							<div class="form-group">
							<input class="btn btn-info" type="submit" value="Assign" style="box-shadow: 1px 1px 5px black; width:100px; height:30px; text-align: center; line-height: 2px; vertical-align:top;"> 
								<% 	
									resultset.close();
									resultset1.close();
								%>
							</div>
							
						</form>
					</div>
					
					<br>
					<!-------------------------------------------------------------------------------------------->
					<!--  	This part displays the textbox for users and tasks and drop-down box				-->
					<!-- 	for associating frequency with the task.											-->
					<!--  	User can add tasks along with the points and task frequency							-->
					<!-------------------------------------------------------------------------------------------->		
					<br><br>
					<div name="AddTaskDiv" class="col-xs-4" style="width:350px;">
					<b>	
						<br><h4>Add Task &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</h4><br>	
					</b>
					<form class="form-horizontal" name="addtaskfrm" action="AddTasks" method="post" onSubmit="return validateTask()" role="form">
						<div class="form-group">
							<label for="taskName" class="col-sm-2 control-label">Task</label>
							<div class="col-sm-6" >
								<input type="text" class="form-control" name="taskName" id="taskName" placeholder="Task" style="border:1px solid black; border-radius:5px; height:30px;">
							</div>
						</div>
			
						<div class="form-group">
							<label for="taskPoints" class="col-sm-2 control-label">Points</label>							
							<div class="col-sm-6">
								<input type="text" class="form-control" name="taskPoints" id="taskPoints" onkeypress="return isNumberKey(event)" placeholder="Points" style="border:1px solid black; border-radius:5px; height:30px;">
							</div>
						</div>
						<div class="form-group">
							<label for="taskFrequency" class="col-sm-2 control-label" id="frequency">Frequency</label>
								<div class="col-sm-8">
									&nbsp; &nbsp;
									<select name="taskFrequency" style="border:1px solid black; border-radius:5px; height:30px; width:135px;">
										<option value="Daily">Daily</option>
										<option value="Weekly">Weekly</option>
										<option value="Once">Once</option>
										<option value="Monthly">Monthly</option>		    		
									</select>
								</div>
						</div>
			
						<div class="form-group">
							<div class="col-sm-10">
								<button class="btn btn-info" type="submit" style="box-shadow: 1px 1px 5px black; width:100px; height:30px; text-align: center; line-height: 2px; vertical-align:top;">Add</button>
							</div>
						</div>
					</form>
				</div>
				</div>		

				<!-------------------------------------------------------------------------------------------->
				<!--  	This part displays the assigned tasks in a table. The name of the task				-->
				<!--  	and assignee are displayed 	along with the number of days to complete the task		-->
				<!--  	Along with these option there is Done button clicking on which user can mark 		-->
				<!--	the task as completed																-->
				<!-------------------------------------------------------------------------------------------->

				<div class="col-xs-6 col-md-6" style="width:650px; background:#FFFFFF; box-shadow: 10px 0px 10px -10px #404040, -10px 0px 10px -10px #404040 ;">
					<div>
					<div>
						<h4><br>&nbsp; &nbsp; &nbsp; Assigned tasks
							<input type="button" value="Overdue Tasks"  onClick="window.open('OverdueTasks.jsp','mywindow','width=500,heightÃ¢ÂÂÃ¢ÂÂ=350,toolbar=no,resizable=yes,menubar=yes')" class="btn btn-info" style="box-shadow: 1px 1px 10px black; width:120px; float:right; height:30px; text-align: center; line-height: 2px; vertical-align:top;" />
						</h4>  
					</div>	
					<br>
					<!-- The headings in the table should remain unchanged
					The tuples in the table should be populated from the database using a loop -->
					<form name="showfrm" method="post" action="CompleteTask">
						<%		
							// advances the date
							DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
							java.util.Date curr_date = new java.util.Date();
							AdvanceDate ad = new AdvanceDate();
							curr_date  = ad.getDate();
							String  systemDate = dateFormat.format(curr_date);

							resultset = statement.executeQuery("select taskassignid,username,taskname,taskpoints,startdate,enddate,DATEDIFF(enddate,'"+systemDate+"') AS Difference from taskAssignment where completionstatus = 0 order by enddate ") ;
						%>
						<table class="table table-hover">
							<tr> 	
								<th>Assignee</th><th>Tasks</th><th>Points</th><th>Start Date</th><th>End Date</th><th>Due Days</th><th>Status</th>
							</tr>
							<%
								while(resultset.next()){
								int taskassignid1 = resultset.getInt(1);
							%>
							<tr>
								<% 
									for (int counter=2;counter<8;counter++ ) {
								%>
								<td>
									<%= resultset.getString(counter) %>
								</td>
							<%} %>	
							<% if(resultset.getInt(7) < 0){ %>
								<td>	
									<button class="btn btn-info" type="submit" style="border-top: 1px solid #f7979f; background: #de1013; background: -webkit-gradient(linear, left top, left bottom, from(#c41919), to(#de1013)); background: -webkit-linear-gradient(top, #c41919, #de1013); background: -moz-linear-gradient(top, #c41919, #de1013); background: -ms-linear-gradient(top, #c41919, #de1013); background: -o-linear-gradient(top, #c41919, #de1013);  width:40px; height:20px; text-align: center; line-height: 2px; vertical-align:top;" name="taskassignid" value=<%= taskassignid1 %>> Done </button>
								</td>
								<%} 
								else{%>
								<td>
									<button class="btn btn-info" type="submit" style="   border-top: 1px solid #96d1f8;   background: #6ac93a;   background: -webkit-gradient(linear, left top, left bottom, from(#6ac421), to(#6ac93a));   background: -webkit-linear-gradient(top, #6ac421, #6ac93a);   background: -moz-linear-gradient(top, #6ac421, #6ac93a);   background: -ms-linear-gradient(top, #6ac421, #6ac93a);   background: -o-linear-gradient(top, #6ac421, #6ac93a);  width:40px; height:20px; text-align: center; line-height: 2px; vertical-align:top;" name="taskassignid" value=<%= taskassignid1 %>> Done </button>
								</td>
								<%} %>
							</tr>	
							<% } %>
						</table>
						<% resultset.close();%>
					</form>
					</div>
					<br>
					
					<!-------------------------------------------------------------------------------------------->
					<!--  	This part displays list of unassigned tasks											-->
					<!-- 																						-->
					<!-------------------------------------------------------------------------------------------->		

				<div class="col-xs-4" style="width:625px; box-shadow:">
					<br><h4>Unassigned tasks</h4><br>
					<form name="showfrm" method="post">
						<%
							statement = conn.createStatement();
							statement1 = conn1.createStatement();
							resultset = statement.executeQuery("select taskname,taskpoints,frequency,day from tasks") ;
						%>
						<table class="table table-hover">
							<tr>  				
								<th>Task </th>
								<th>Points</th>
								<th>Frequency</th>  				
							</tr>
							<% 	java.util.Date current_date = new java.util.Date();	
							while(resultset.next())
							{%>
							<tr>
								<%  ResultSet resultset2 = statement1.executeQuery("select taskname,max(enddate) from taskAssignment group by taskname") ; 
									int flag = 0;
									while(resultset2.next())
									{ 
									 	if( resultset.getString(1).equals(resultset2.getString(1)) )
									 	{
											flag = 1;
											java.util.Date temp_date =  resultset2.getDate(2) ;
											if( temp_date.compareTo(current_date) < 0)
											{%>
												<td><%= resultset.getString(1) %></td>
												<td><%= resultset.getString(2) %></td>
												<td><%= resultset.getString(3) %></td>
											<%} %> 
									<% } %>
								<% } %>	
								<%resultset2.close();%>
								<% if(flag != 1)
									{ %>
										<td><%= resultset.getString(1) %></td>
										<td><%= resultset.getString(2) %></td>
										<td><%= resultset.getString(3) %></td>
									<%} %>
							</tr>	
							<%} %>
						</table>
						<% resultset.close();%>
					</form>
				<br><br><br><br><br>
				</div>
				
				</div>
	
				<!-------------------------------------------------------------------------------------------->
				<!--  	This part displays the table which displays the username and with the weekly		-->
				<!--  	status of points. Table displays how many points user needs and 					-->
				<!--  	how many points user has already completed.											-->
				<!-------------------------------------------------------------------------------------------->		
		
				<div class="col-xs-6 col-md-4" style="width:300px;">
					<form name="showfrm" action="ChangeWeeklyPoints" method="post" role="form">
						<%	Statement statement4= conn4.createStatement();
							ResultSet resultset4 = statement4.executeQuery("select * from weeklypoints ");
							resultset4.next();
						%>
						<h4><br>Weekly Points Summary</h4>
						<h5>(Due on <%= resultset4.getDate(3) %>)</h5>		
						<table class="table table-bordered" style="text-align:center;vertical-align:middle;">
							
							<tr>
								<th>Name</th><th>Required Points</th><th>Weekly Points</th>
							</tr>
							<% 
								resultset4.previous(); 
								while(resultset4.next()){
								if (resultset4.getString(2).equals(session.getAttribute( "theName" ))) { 
							%>
							<tr >
								<td style="text-align:center;vertical-align:middle;"><strong><%= resultset4.getString(2) %></strong></td>
								<td style="text-align:center;vertical-align:middle;"><strong><%= resultset4.getFloat(6) %></strong></td>
								<% 
									int pointsperweek =  resultset4.getInt(7); 
									int weekptkey = resultset4.getInt(1); 
								%>
								<td><strong><input style="width:40px" type="text" name=<%= weekptkey %> value=<%= pointsperweek %> placeholder=<%=resultset4.getInt(7) %>></strong></td>								
							</tr>
								<%} 
								if (!resultset4.getString(2).equals(session.getAttribute( "theName" ))) 
								{%>
								
							<tr>
								<td style="text-align:center;vertical-align:middle;"><%= resultset4.getString(2) %></td>
								<td style="text-align:center;vertical-align:middle;"><%= resultset4.getFloat(6) %></td>
								<% int pointsperweek =  resultset4.getInt(7); 
								int weekptkey = resultset4.getInt(1); %>
								<td><input style="width:40px" type="text" name=<%= weekptkey %> value=<%= pointsperweek %> placeholder=<%=resultset4.getInt(7) %>></td>								
							</tr>
							<%}%>
							<%}%>
							<% resultset4.close();%>
						
						</table>
						
						<div class="form-group">
							<div class="col-sm-offset-2 col-sm-10">
								<button class="btn btn-info" style="box-shadow: 1px 1px 5px black; width:150px; height:30px; text-align: center; line-height: 2px; vertical-align:top;">Change Weekly Points</button>
							</div>
						</div>
					</form>
					
				</div>		
			</div>	
			
			<div class="row" style="background:GhostWhite; color:Black; box-shadow: 5px 5px 20px #888888">	
				<br>
				
						
				 
				<!-------------------------------------------------------------------------------------------->
				<!--  	This part displays button. By clicking this button all overdue tasks 				-->
				<!-- 	of the users are displayed															-->
				<!-------------------------------------------------------------------------------------------->		
			
				<div class="col-xs-4" style="width:350px;">
				<!-- <b>Overdue tasks</b> -->
					<form class="form-horizontal" name="overduetaskfrm" action="OverDueTasks" method="post" role="form">
						<div class="form-group">
							
						</div>
					</form>
				</div>
						
				<!-------------------------------------------------------------------------------------------->
				
				<!-------------------------------------------------------------------------------------------->
				<!--  	User Strory 3.6														 				-->
				<!-- 																						-->
				<!-------------------------------------------------------------------------------------------->		
			
				<div class="col-xs-4" style="width:350px;">
					<form class="form-horizontal" name="datetest" action="AdvanceDate" method="post" role="form" onSubmit="return advanceDateValidate()">
						<div class="form-group">
							<div class="col-sm-offset-2 col-sm-10">
							<h4 text-align:left> For Testing </h4><br>
							
								<label class=col-sm-4>End Date</label>
								<table>
									<tr>
										<td>
											<input class=col-sm-6 name="pickdate" type="text" id="datepicker2" placeholder="Pick System Date" style="width:150px;.ui-datepicker th { background-color: black; }"></label>
										</td> 
									</tr>
									<tr><td>&nbsp</td></tr>
									<tr> 
										<td>
											<button class="btn btn-info" type="submit" name="testDate" value="testDate"> Advance System Date </button>
										</td> 
									</tr>
								</table>
							</div>
						</div>
					</form>
				</div>
			
				<!-------------------------------------------------------------------------------------------->	
			</div>	
		</div>	
	</body>
</html>
