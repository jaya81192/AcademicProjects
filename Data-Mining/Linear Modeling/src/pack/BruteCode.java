package pack;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class BruteCode {
	public static void main(String args[]) throws IOException
	{
		try 
			{
				int columnNumber=0;
				int noRows=0;
				ArrayList<Float> data1 = new ArrayList<Float>();
				ArrayList<Float> data2 = new ArrayList<Float>();
				float middata,x,x1,y,y1,a,b,c,d,B1,B0,error,errorchk=0,difference=0;
				//Taking the file name with the data from the user
				System.out.println("Enter the file name");
				Scanner in = new Scanner(System.in);
				String s = in.nextLine();
				in.close();
				//Reading the data from the file and storing different columns in different arrays
				BufferedReader br = new BufferedReader(new FileReader(s));
				while((s = br.readLine()) != null && noRows<50)
				{
					StringTokenizer stk = new StringTokenizer(s, ",");
					 while(stk.hasMoreTokens())
		                {
		                        columnNumber++;
		                        middata=Float.parseFloat(stk.nextToken());
		                        if(columnNumber==1)
		                        	{
		                        	data1.add(middata);
		                        	}
		                        else 
		                        	{
		                        	data2.add(middata);
		                        	}
		                }
					 columnNumber=0;
					 noRows++;
				}	 
				//Finding the approximate value for the coefficients using the equation
				x1=data1.get(noRows-1);
				x=data1.get(0);
				y1=Math.abs(data2.get(noRows-1));
				y=Math.abs(data2.get(0));
				B1=(float) (Math.log10(y1)-Math.log10(y)/(x1-x));
				B0=(float) (Math.log10(y1)-B1*x1);
				//Setting the values upper and lower bounds for values to be searched for coefficients
				a=B1-(float)2.0;
				b=B1+(float)2.0;
				c=B0-(float)2.0;
				d=B0+(float)2.0;
				//Following code to search for B0 and B1 through brute force analysis
				for(float i=a;i<=b;i+=0.001)
				{
					for(float j=c;j<=d;j+=0.001)
					{
						error=0;
						for(int k=0;k<noRows;k++)
						{
							x=data1.get(k);
							y=data2.get(k);
							difference=(float) (y-Math.pow(10,(i*x+j)));
							//Cumulative error for the given data with a set of B0 and B1 value
							error=error+Math.abs(difference);
						}
						if(i==a&&j==c)
						{
							//assigns the minimum error as the value of error for first set of coefficients
							errorchk=error;
							//updates the initial optimum coefficients
							B1=i;
							B0=j;
						}
						//following checks if the new error is less than the minimum error
						else if(error<errorchk)
						{
							//if error is less than the minimum error, updates new error as the minimum error
							errorchk=error;
							//updates the new optimum coefficients
							B1=i;
							B0=j;
						}
					}
				}
				System.out.println("The coefficient B1 is " + B1);
				System.out.println("The coefficient B0 is " + B0);
				error=0;
				//The following code displays individual errors for coefficients found in R
				System.out.println("Individual error in each dataset for coefficients found in R");
				for(int k=0;k<noRows;k++)
				{
					x=data1.get(k);
					y=data2.get(k);
					difference=(float) (y-Math.pow(10,(float)0.4966*x+((float)-1.8850)));
					System.out.println(difference);
					error=error+Math.abs(difference);
				}
				System.out.println("The cumulative error with coefficients found in R: "+error);
				//The following code displays individual errors for coefficients found by brute force analysis
				System.out.println("Individual error in each dataset for coefficients found by brute force analysis");
				for(int k=0;k<noRows;k++)
				{
					x=data1.get(k);
					y=data2.get(k);
					difference=(float) (y-Math.pow(10,B1*x+((float)B0)));
					System.out.println(difference);
				}
				System.out.println("The cumulative error with coefficients found by brute force analysis: " + errorchk);
			}
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}	
}
