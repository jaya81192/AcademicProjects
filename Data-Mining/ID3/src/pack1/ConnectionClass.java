package pack1;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionClass {
	//Class for establishing connection with SQL 
	public Connection getConnection(){
	Connection conn = null;
	String dbname = "assignment3";
	String url = "jdbc:mysql://localhost:3306/";
	String dbName=dbname;
	String driver = "com.mysql.jdbc.Driver";
	String dbusername="root";
	String dbpassword="Shraddha1";
	try 
	{
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
