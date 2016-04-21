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
<title>Sign up</title>
</head>
<body>
<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
<div class="container">
    <div class="row  pad-top">
    <div class="col-md-4 col-md-offset-4 col-sm-6 col-sm-offset-3 col-xs-10 col-xs-offset-1">
                        <div class="panel panel-default"  class="text-center">
                            <div class="panel-heading">
                        <strong>Register Yourself </strong>  
                            </div>
                            <div class="panel-body">
        <form role="form">
                <div class="form-group">
                    <label for="InputFirstName">Enter First Name</label>
                    <div class="input-group">
                        <input type="text" class="form-control" id="InputFirstName" name="InputFirstName"  placeholder="Enter First Name" required>
                        <span class="input-group-addon"></span> 
                    </div>
                </div>
				<div class="form-group">
                    <label for="InputLastName">Enter Last Name</label>
                    <div class="input-group">
                        <input type="text" class="form-control" id="InputLastName" name="InputLastName" placeholder="Enter Last name" required>
                        <span class="input-group-addon"></span>
                    </div>
                </div>
                <div class="form-group">
                    <label for="InputEmail">Enter Email</label>
                    <div class="input-group">
                        <input type="email" class="form-control" id="InputEmailFirst" name="InputEmail" placeholder="Enter Email" required>
                        <span class="input-group-addon"></span>
						</div>
                    </div>
				<div class="form-group">
                    <label for="InputConfirmEmail">Confirm Email</label>
                    <div class="input-group">
                        <input type="email" class="form-control" id="InputConfirmEmail" name="InputConfirmEmail" placeholder="enter a password" required>
                        <span class="input-group-addon"></span>
                    </div>
                </div>
				<div class="form-group">
                    <label for="InputPassword">Enter password</label>
                    <div class="input-group">
                        <input type="password" class="form-control" id="InputPassword" name="InputPassword" placeholder="enter a password" required>
                        <span class="input-group-addon"></span>
                    </div>
                </div>
                <input type="submit" name="submit" id="submit" value="Submit" class="btn btn-info pull-right">
                <input type="reset" name="reset" id="reset" value="Reset " class="btn btn-info pull-left">
            </div>
			</div>
        </form>
    </div>
</div>
</body>
</html>
