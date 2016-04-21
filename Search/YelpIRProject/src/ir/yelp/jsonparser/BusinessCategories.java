package ir.yelp.jsonparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.*;


public class BusinessCategories {
	public FileWriter fw;
	public BufferedWriter bw;
	public File output1 = new File("/Users/Shruti/Git/YelpIRProject/bin/Resources/categories.txt");
	HashMap<String,String> businessCategory = new HashMap<String,String>();

	
	public static Object[] extractCategory(File fileName, String businessID)
	{
		String jsonContent = null;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
			while ((jsonContent = bufferedReader.readLine()) != null) {
				JSONObject jsonObject = ir.yelp.jsonparser.DatasetReader.JSONReader(jsonContent);
				String businessId = (String) jsonObject.get("business_id");
				if(businessId.equalsIgnoreCase(businessID))
				{
					JSONArray categories = (JSONArray) jsonObject.get("categories");
					bufferedReader.close();
					return categories.toArray();
				}
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void parseJSONFile(File fileName, String type) throws IOException {
		String jsonContent = null;
		fw=new FileWriter(output1,true);
		bw = new BufferedWriter(new BufferedWriter(fw));
		
		
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));

			while ((jsonContent = bufferedReader.readLine()) != null) {
				JSONObject jsonObject = ir.yelp.jsonparser.DatasetReader.JSONReader(jsonContent);
				String businessId = (String) jsonObject.get("business_id");
				JSONArray categories = (JSONArray) jsonObject.get("categories");			
				@SuppressWarnings("unchecked")
				Iterator<Object> j = categories.iterator(); 
					while (j.hasNext()) 
					{ 
						businessCategory.put((String) j.next(), businessId);
					}		
			}
			for(java.util.Map.Entry<String, String> business : businessCategory.entrySet())
			{
				bw.write(business.getKey());
				System.out.println(business.getKey());
				bw.newLine();
			}
			bw.close();
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
