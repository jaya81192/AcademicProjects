<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css">

	<!-- Optional theme -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap-theme.min.css">

	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	</head>
	<title>
	Sign up
	</title>
	<body>
	<!-- Latest compiled and minified JavaScript -->
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"> </script>
		<!-- Navigation bar with title -->
		<nav class="navbar navbar-default navbar-fixed-top" role="navigation">   
	    	<div class="container">
	    		<form class="form-inline" role="form">
	    				<div class="nav navbar-nav navbar-left">
	    				<h3 style="color:steelblue">DIVVY-APP</h3>
	    				</div>
	    			<div class= "nav navbar-nav navbar-right">
	    			<h3><strong>Hello!</strong></h3>
	    			</div>
	    		</form>
	    	</div> <!-- /container -->
		</nav>  
		
		
		
		
		<!-- Form to register yourself to the application -->
		<div class="container" style="margin-top:75px">
			<div class="row  pad-top">
				<div class="col-md-4 col-md-offset-4 col-sm-6 col-sm-offset-3 col-xs-10 col-xs-offset-1">
					<div class="panel panel-default"  class="text-center">
						<div class="panel-heading">
							<strong>Register Yourself</strong>  
						</div>
						<div class="panel-body">
						<form role="form" action="SignUpInformation" method="post">
							<div class="form-group">
								<label for="InputUsername">Enter Username</label>
								<div class="input-group">
									<input type="text" class="form-control" name="InputUsername"  placeholder="Enter Preferred Username" required>
									<span class="input-group-addon"></span> 
								</div>
							</div>
							<div class="form-group">
								<label for="InputFirstName">Enter First Name</label>
								<div class="input-group">
									<input type="text" class="form-control" name="InputFirstName"  placeholder="Enter First Name" required>
									<span class="input-group-addon"></span> 
								</div>
							</div>
							<div class="form-group">
								<label for="InputLastName">Enter Last Name</label>
								<div class="input-group">
									<input type="text" class="form-control" name="InputLastName" placeholder="Enter Last name" required>
									<span class="input-group-addon"></span>
								</div>
							</div>
							<div class="form-group">
								<label for="InputEmail">Enter Email</label>
								<div class="input-group">
									<input type="email" class="form-control" name="InputEmail" placeholder="Enter Email" required>
									<span class="input-group-addon"></span>
								</div>
							</div>
							<div class="form-group">
								<label for="InputPassword">Enter password</label>
								<div class="input-group">
									<input type="password" class="form-control" name="InputPassword" placeholder="Enter a password" required>
									<span class="input-group-addon"></span>
								</div>
							</div>
							<input type="submit" name="submit" id="submit" value="Submit" class="btn btn-info pull-right">
							<input type="reset" name="reset" id="reset" value="Reset " class="btn btn-info pull-left">
						</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
