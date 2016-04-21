<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap-theme.min.css">
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="../../favicon.ico">
    <!-- Bootstrap core CSS -->
    <link href="../../dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Custom styles for this template -->
    <link href="signin.css" rel="stylesheet">
    <script src="../../assets/js/ie-emulation-modes-warning.js"></script>
  </head>
  <title>Welcome to Divvy-App</title>
  <body>
  <!-- Latest compiled and minified JavaScript -->
  	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"></script>
	<nav class="navbar navbar-default navbar-fixed-top" role="navigation">   
    	<div class="container">
    		<form class="form-inline" role="form" action="LoginValidation" method="post">
    			<div class="nav navbar-nav navbar-left">
    				<h3 style="color:steelblue">DIVVY-APP</h3>
    			</div>
    			<div class= "nav navbar-nav navbar-right" style="margin-top:20px;margin-bottom:5px">
    					<input type="text" name="username" class="form-control" placeholder="Username" required autofocus>
    					<input type="password" name="userpassword" class="form-control" placeholder="Password" required>
    					<button class="btn btn-info" type="submit">Log in</button>
    		
    			</div>
    		</form>
    	</div> <!-- /container -->
    </nav>
    <div class="row">
  		<div class="text-center" style="margin-top:150px">
  			<img src="http://homegenius.com.au/images/Cartoon-cartoon-people-family-clothes-cleaning-mop-broom-children-parents1.jpg">
		</div>
		<div class="text-center">
			<p style="margin-left:65">Don't Have an Account?</p>
			<form method="get" action="SignUpPage.jsp">
				<button type="submit" class="btn btn-info" style="margin-left:60px">Get Rolling</button>
			</form>
		</div>
	</div>
    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
  	<script src="../../assets/js/ie10-viewport-bug-workaround.js"></script>
  </body>
</html>