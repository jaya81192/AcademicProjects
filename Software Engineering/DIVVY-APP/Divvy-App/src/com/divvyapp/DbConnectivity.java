package com.divvyapp;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnectivity {

	public DbConnectivity() {
		// TODO Auto-generated constructor stub
	}
	
	public Connection getConnection(){
	
	
	Connection conn = null;
	String url = "jdbc:mysql://localhost:3306/";
	String dbName="divvy";
	String driver = "com.mysql.jdbc.Driver";
	String dbusername="root";
	String dbpassword="sayali123";
	
	try {
		Class.forName(driver).newInstance();
		conn = DriverManager.getConnection(url+dbName,dbusername,dbpassword);
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return conn;
	}

}
