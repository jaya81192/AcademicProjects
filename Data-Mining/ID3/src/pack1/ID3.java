package pack1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import pack1.TreeNode;
import pack1.ConnectionClass;

public class ID3 
{
	public static void main(String[] args) throws IOException, SQLException
	{
		ConnectionClass db = new ConnectionClass();
		Connection conn = db.getConnection();
		String tablename = null,classifiers,label = null,Query, fileName;
		Scanner in = new Scanner(System.in);
		Statement st;
		ResultSet data;
		int colNum, i;
		ResultSetMetaData md;
		TreeNode node = new TreeNode();
		BufferedReader br;
		String s = null;
		String[] classifyList = null;
		ArrayList<String> colNames = new ArrayList<String>(), classAtts = new ArrayList<String>();
		ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
		try 
		{
			//Getting the file which has the data to be classified, classifying attributes and the label
			System.out.println("Enter the tree specification file name");
			fileName = in.nextLine();
			br = new BufferedReader(new FileReader(fileName));
			i=0;
			while((s = br.readLine()) != null)
			{
				if(i==0)
				{
					tablename=s;
				}
				else if(i==1)
				{
					label=s;
				}
				else if(i==2) 
				{
					classifiers = s;
					classifyList = classifiers.split(" ");
				}
				i++;
			}	
			if(tablename!=null && label!=null && classifyList!=null)
			{
				//Selects all the tuples of the table given in the specification file.
				Query = "Select * from " + (String) tablename + ";";
				st = conn.createStatement();
				data = st.executeQuery(Query);
				md = data.getMetaData();
				colNum=md.getColumnCount();
				
				//Getting the names of the columns of the table having the data
				for(i=1;i<=colNum;i++)
				{
					colNames.add(md.getColumnName(i));
				}
				Statement st1=conn.createStatement();
				
				//A hashmap that keeps track of the distribution of labels in the data. 
				HashMap<String,Integer> labelHash = new HashMap<String,Integer>();
				String Query2="Select distinct (" + label + ") from " + tablename + ";";
				ResultSet labelTypes=st1.executeQuery(Query2);
				
				//Putting distinct labels into the hashmap with an initial count of 0
				while(labelTypes.next())
				{
					classAtts.add(labelTypes.getString(1));	
					labelHash.put(labelTypes.getString(1), 0);
				}
				
				//Traversing through the table having the data and looking at each tuple to create a list of tuples and determining the distribution of labels in the data
				while(data.next())
				{
					ArrayList<String> tupleList = new ArrayList<String>();
					for(i=1;i<=colNum;i++)
					{
						tupleList.add(data.getString(i));
					}
					
					//dataList is a list of tuples. The tuples are a list of attributes
					dataList.add(tupleList);
					for(int g=0;g<classAtts.size();g++)
					{
						//Determining the current tuple's label and incrementing the corresponding count in the distribution hashmap.
						if(tupleList.get(colNames.indexOf(label)).contains(classAtts.get(g)))
						{
							labelHash.put(classAtts.get(g), labelHash.get(classAtts.get(g)) + 1);
						}
					}
				}
				
				//building the tree with all tuples. 
				node = BuildClassificationTree(classifyList, tablename, label, dataList, colNames, 0, labelHash, classAtts);	
				
				//Takes the input file, which has a new input set for which label is to be classified. 
				System.out.println("Enter the input file name:");
				String fileName1 = in.nextLine();
				br = new BufferedReader(new FileReader(fileName1));
				String[] inputAtts = null;
				String input;
				while((s = br.readLine()) != null)
				{
					input=s;
					inputAtts= input.split("\t");
					//predicts the label for the input using the tree built in "node" 
					System.out.println("The label predicted with tree built with all the tuples: " + labelProbability(node, colNames, classAtts, inputAtts, classifyList, tablename, label, dataList, labelHash));	
				}

				
				
				//The following is the start of 3 fold validation
				ArrayList<ArrayList<String>> buildData1= new ArrayList<ArrayList<String>>();
				HashMap<String,Integer> buildHash1 = new HashMap<String,Integer>();
				ArrayList<ArrayList<String>> buildData2= new ArrayList<ArrayList<String>>();
				HashMap<String,Integer> buildHash2 = new HashMap<String,Integer>();
				ArrayList<ArrayList<String>> buildData3= new ArrayList<ArrayList<String>>();
				HashMap<String,Integer> buildHash3 = new HashMap<String,Integer>();
				
				TreeNode node1 = new TreeNode();
				TreeNode node2 = new TreeNode();
				TreeNode node3 = new TreeNode();
				float accuracy1 = 0;
				float accuracy2 = 0;
				float accuracy3 = 0;
				
				System.out.println("Three Fold Cross Validation");
				
				for(int y=0;y<3;y++)
				{
					//Tuples 1 to 6 to build data and 7 to 9 to test
					if(y==0)
					{
						System.out.println("Build 1: ");
						buildData1 = new ArrayList<ArrayList<String>>(); 
						for(int g=0;g<classAtts.size();g++)
						{
							buildHash1.put(classAtts.get(g), 0);
						}
						for(int p=0;p<dataList.size();p++)
						{
							if(p==0||p==1||p==2||p==3||p==4||p==5)
							{
								//creating a list of tuples with tuples 1 to 6 
								buildData1.add(dataList.get(p));
								for(int g=0;g<classAtts.size();g++)
								{
									if(dataList.get(p).get(colNames.indexOf(label)).contains(classAtts.get(g)))
									{
										//computing and incrementing label distribution in the tuples used for building the tree
										buildHash1.put(classAtts.get(g), buildHash1.get(classAtts.get(g)) + 1);
									}
								}
							}
						}
						
						//building tree with tuples 1 to 6
						node1 = BuildClassificationTree(classifyList, tablename, label, buildData1, colNames, 0, buildHash1, classAtts);	
						ArrayList<String> actualLabel = new ArrayList<String>();
						ArrayList<String> predictedLabel = new ArrayList<String>();
						for(int p=0;p<dataList.size();p++)
						{
							if(p==6||p==7||p==8)
							{
								//keeping track of the actual labels of tuple 7 to 9
								actualLabel.add(dataList.get(p).get(colNames.indexOf(label)));
								for(int k=0;k<classifyList.length;k++)
								{
								inputAtts[k]=dataList.get(p).get(colNames.indexOf(classifyList[k]));
								}
								
								//getting the label prediction using tree built in node1
								predictedLabel.add(labelProbability(node1, colNames, classAtts, inputAtts, classifyList, tablename, label, buildData1, buildHash1));
							}
						}
						
						//To find the accuracy of the tree, it is seen what percentage of the predicted label matches the actual label 
						int matchHit=0;
						for(int p = 0;p<actualLabel.size();p++)
						{
							if(predictedLabel.get(p).equals(actualLabel.get(p)))
							{
								matchHit=matchHit+1;
							}
						}
						accuracy1=((float)matchHit/(float)actualLabel.size())*100;
					}					
					
					
					//The same building process, but with tuples 1,2,3,7,8,9 and computing accuracy with 4,5,6
					if(y==1)
					{
						System.out.println("Build 2: ");
						buildData2 = new ArrayList<ArrayList<String>>(); 
						for(int g=0;g<classAtts.size();g++)
						{
							buildHash2.put(classAtts.get(g), 0);
						}
						for(int p=0;p<dataList.size();p++)
						{
							if(p==0||p==1||p==2||p==6||p==7||p==8)
							{
								buildData2.add(dataList.get(p));
								for(int g=0;g<classAtts.size();g++)
								{
									if(dataList.get(p).get(colNames.indexOf(label)).contains(classAtts.get(g)))
									{
										buildHash2.put(classAtts.get(g), buildHash2.get(classAtts.get(g)) + 1);
									}
								}
							}
						}
						node2 = BuildClassificationTree(classifyList, tablename, label, buildData2, colNames, 0, buildHash2, classAtts);	
						ArrayList<String> actualLabel = new ArrayList<String>();
						ArrayList<String> predictedLabel = new ArrayList<String>();
						for(int p=0;p<dataList.size();p++)
						{
							if(p==3||p==4||p==5)
							{
								actualLabel.add(dataList.get(p).get(colNames.indexOf(label)));
								for(int k=0;k<classifyList.length;k++)
								{
								inputAtts[k]=dataList.get(p).get(colNames.indexOf(classifyList[k]));
								}
								predictedLabel.add(labelProbability(node2, colNames, classAtts, inputAtts, classifyList, tablename, label, buildData2, buildHash2));
							}
						}
						int matchHit=0;
						for(int p = 0;p<actualLabel.size();p++)
						{
							if(predictedLabel.get(p).equals(actualLabel.get(p)))
							{
								matchHit++;
							}
						}
						accuracy2=((float)matchHit/(float)actualLabel.size())*100;	
					}
					
					//The same building process, but with tuples 4,5,6,7,8,9 and computing accuracy with 1,2,3
					if(y==2)
					{
						System.out.println("Build 3: ");
						buildData3 = new ArrayList<ArrayList<String>>(); 
						for(int g=0;g<classAtts.size();g++)
						{
							buildHash3.put(classAtts.get(g), 0);
						}
						for(int p=0;p<dataList.size();p++)
						{
							if(p==6||p==7||p==8||p==3||p==4||p==5)
							{
								buildData3.add(dataList.get(p));
								for(int g=0;g<classAtts.size();g++)
								{
									if(dataList.get(p).get(colNames.indexOf(label)).contains(classAtts.get(g)))
									{
										buildHash3.put(classAtts.get(g), buildHash3.get(classAtts.get(g)) + 1);
									}
								}
							}
						}
						node3 = BuildClassificationTree(classifyList, tablename, label, buildData3, colNames, 0, buildHash3, classAtts);	
						ArrayList<String> actualLabel = new ArrayList<String>();
						ArrayList<String> predictedLabel = new ArrayList<String>();
						for(int p=0;p<dataList.size();p++)
						{
							if(p==0||p==1||p==2)
							{
								actualLabel.add(dataList.get(p).get(colNames.indexOf(label)));
								for(int k=0;k<classifyList.length;k++)
								{
								inputAtts[k]=dataList.get(p).get(colNames.indexOf(classifyList[k]));
								}
								predictedLabel.add(labelProbability(node3, colNames, classAtts, inputAtts, classifyList, tablename, label, buildData3, buildHash3));
							}
						}
						int matchHit=0;
						for(int p = 0;p<actualLabel.size();p++)
						{
							if(predictedLabel.get(p).equals(actualLabel.get(p)))
							{
								matchHit++;
							}
						}
						accuracy3=((float)matchHit/(float)actualLabel.size())*100;
					}
				}
				
				
				//Start of the code to see which of the three built trees is the most accurate
				TreeNode nodeBest = node1;
				dataList = buildData1;
				labelHash = buildHash1;
				int c=1;
				float accuracy = accuracy1;
				if(accuracy2 > accuracy1)
				{
					nodeBest = node2;
					dataList = buildData2;
					labelHash = buildHash2;
					c=2;
					accuracy=accuracy2;
				}
				if(accuracy3 > accuracy1)
				{
					nodeBest = node3;
					dataList = buildData3;
					labelHash = buildHash3;
					c=3;
					accuracy=accuracy3;
				}
				if(accuracy2 > accuracy3)
				{
					nodeBest = node2;
					dataList = buildData2;
					labelHash = buildHash2;
					c=2;
					accuracy=accuracy2;
				}
				if(accuracy3 > accuracy2)
				{
					nodeBest = node3;
					dataList = buildData3;
					labelHash = buildHash3;
					c=3;
					accuracy=accuracy3;
				}

				//End of the code to see which of the three built trees is the most accurate
				
				System.out.println("Prediction using the most accurate tree ");
				br = new BufferedReader(new FileReader(fileName1));
				while((s = br.readLine()) != null)
				{
					input=s;
					inputAtts= input.split("\t");
					
					//Using the most accurate tree, the label for the input in input file is predicted.
					System.out.println("The label predicted for input using the tree " + c + " with maximum accuracy " + accuracy  + ": " + labelProbability(nodeBest, colNames, classAtts, inputAtts, classifyList, tablename, label, dataList, labelHash));
				
				}
			}
			else
			{
				System.out.println("No table specified");
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			conn.close();	
		}
	}
	
	
	
	
	public static TreeNode BuildClassificationTree(String[] classifyList, String tablename, String label, ArrayList<ArrayList<String>> dataList, ArrayList<String> colNames, int level, HashMap<String,Integer> labelHash, ArrayList<String> classAtts) throws SQLException
	{
		TreeNode node = new TreeNode(); 
		ConnectionClass db = new ConnectionClass();
		Connection conn = db.getConnection();
		Statement st;
		String Query1;
		ResultSet classifyAtt;
		try
		{
			if(classifyList.length>=level+1)
			{
				st = conn.createStatement();
				
				//To find the distinct values of the classifying attribute
				Query1 = "Select distinct(" + classifyList[level] +  ") from " + tablename + ";";
				classifyAtt = st.executeQuery(Query1);
				int colInd = colNames.indexOf(classifyList[level]);	
				int j = dataList.size();
				if(level==0)
				{
					node.classHash=labelHash;
				}
				int i = 0;
				//for each distinct value of classifying attribute a node is constructed 
				while(classifyAtt.next())
				{
					ArrayList<ArrayList<String>> dataListChild = new ArrayList<ArrayList<String>>();
					HashMap<String,Integer> labelHashChild = new HashMap<String,Integer>();
					
					//initialing the hashmap having distribution of labels in the child node. The child node is on classification of the current node 
					for(int l=0; l<classAtts.size();l++)
					{
						labelHashChild.put(classAtts.get(l), 0);
					}
					
					//going over the tuples in the tuple list
					for(int k=0;k<j;k++)
					{
						//putting the tuples with the classifying attribute value of the child of current node into the child node's list
						if(dataList.get(k).get(colInd).contains(classifyAtt.getString(1)))
						{
							dataListChild.add(dataList.get(k));
							for(int g=0;g<classAtts.size();g++)
							{
								if(dataList.get(k).get(colNames.indexOf(label)).contains(classAtts.get(g)))
								{
									//incrementing the corresponding label in the label distribution hashmap.
									labelHashChild.put(classAtts.get(g), labelHashChild.get(classAtts.get(g)) + 1);
								}
							}		
						}
					}
					//recursively creating the child nodes and assigning it as a child to current node
					//level+1 keeps track of which level of classifying attribute the recursive call is at
					node.childNodes.add(BuildClassificationTree(classifyList, tablename, label, dataListChild, colNames, level+1, labelHashChild, classAtts));
					
					//assigning other attributes values of the class
					node.childNodes.get(i).parentNode=node;
					node.childNodes.get(i).classAttribute=classifyList[level];
					node.childNodes.get(i).classAttributeValue=classifyAtt.getString(1);
					node.childNodes.get(i).classHash=labelHashChild;
					i++;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
		conn.close();	
		}
		return node;
	}
	
	
	
	
	
	
	public static String labelProbability(TreeNode rootNode,  ArrayList<String> colNames, ArrayList<String> classAtts, String[] oldInputAtts, String[] classifyList,String tablename, String label, ArrayList<ArrayList<String>> dataList,HashMap<String,Integer> labelHash)
	{
		ArrayList<Float> probabilities=new ArrayList<Float>();
		String returnLabel = "";
		try
		{
			TreeNode node = rootNode;
			ArrayList<Integer> classCount = new ArrayList<Integer>();
			int total=0;
			int flag=0;
			int flag1=0;
			String newClassifyListString = "";
			String inputAttsString = "";
			int b=0;
			System.out.print("Tuple: ");
			int count = 0;
			for(int a=0;a<oldInputAtts.length;a++)
			{
				System.out.print(oldInputAtts[a] + "\t");
				//Removes the classifying Attributes for which input is given as * 
				if(oldInputAtts[a].contains("*"))
				{
					count++;
					flag1=1;
				}
				 else
				 {
					if(b==0)
					{
						newClassifyListString = classifyList[a];
						inputAttsString = oldInputAtts[a];
						b++;	
					}
					else
					{	
						newClassifyListString = newClassifyListString + " " + classifyList[a];
						inputAttsString = inputAttsString + " " + oldInputAtts[a];
						b++;
					}
				}
			}
			System.out.println();
			String[] inputAtts = inputAttsString.split(" ");
			String[] newClassifyList = newClassifyListString.split(" ");
			if(count==oldInputAtts.length)
			{
			flag1=0;
			}
			if(flag1==1)
			{
				//creates a node neglecting the attributes for which input is given as *. Only if a * is encountered in the input list.
				node=BuildClassificationTree(newClassifyList, tablename, label, dataList, colNames, 0, labelHash, classAtts);
			}
			
			//for the length of input attributes, it traverses down till the corresponding leaf node
			for(int i = 0; i<inputAtts.length; i++)
			{
				int x=-1;
				
				for(int j = 0; j < node.childNodes.size(); j++)
				{	
					if(node.childNodes.get(j).classAttributeValue.contains(inputAtts[i]))
					{
						x=j;
						
					}
				}
				if(x!=-1)
				{
				node=node.childNodes.get(x);
				}
			}	
			
			//if the leaf node of the input set of attributes is null, it traverses up till it finds a node that is not null 
			while(node!=null && flag==0)
			{
				classCount= new ArrayList<Integer>(); 
				for(int k = 0; k < classAtts.size(); k++)
				{
					classCount.add(node.classHash.get(classAtts.get(k)));
					total=total + node.classHash.get(classAtts.get(k));	
				}
				if(total!=0)
				{
					flag=1;
				}
				if(flag==0)
				{
					total=0;
					node=node.parentNode;
				}
			}
			
			
			//calculates the probability of the labels at the node from the counts in the label distribution hashmap
			System.out.print("Class: ");
			for(int k=0;k<classCount.size();k++)
			{
				if(classCount.get(k)==0)
				{
					System.out.print(" " + classAtts.get(k) + " : " + 0 + " ");
					probabilities.add((float) 0);
				}
				else
				{
					probabilities.add((float)classCount.get(k)/(float)total);
					System.out.print(" " + classAtts.get(k) + " : " + (float)classCount.get(k)/(float)total);
				}		
			}
			System.out.println();
			System.out.println();
			
			
			
			//Picks a label value from all the possible labels based on the probability.
			//It is done by picking a random number, assigning ranges based on probability for the labels , and checking which label's range the random number falls in
			float randomNum = (float) Math.random();
			float min = -1;
			float max = (float)classCount.get(0)/(float)total;
			for(int k=1;k<=classCount.size();k++)
			{
				if(randomNum>min&&randomNum<=max)
				{
					returnLabel = classAtts.get(k-1);
				}
				if(k!=classCount.size())
				{
					min=max;
					max=max+(float)classCount.get(k)/(float)total;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return returnLabel;
	}
}
