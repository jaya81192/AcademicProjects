package ir.yelp.jsonparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class categoryListAndCount {

	public static void main(String[] args)
	{
	try {
		File fileName = new File("F:/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_business.json");
		BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));		
		File file = new File("F:/categoriesAndCount.txt");
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
		
		String jsonContent = null;
		HashMap<String, Integer> categoryCount = new HashMap<String, Integer>();
		
		//Reads each json object in businesses file, and gets unique categories. 
		//puts the category in hashMap with a count = 1 if not already there, if there it increments count
		while ((jsonContent = bufferedReader.readLine()) != null) {
			JSONObject jsonObject = ir.yelp.jsonparser.DatasetReader.JSONReader(jsonContent);
			JSONArray categories = (JSONArray) jsonObject.get("categories");
			for(int i = 0; i < categories.size(); i++){
				String category = (String) categories.get(i);
				if(categoryCount.containsKey(category))
				{
					categoryCount.put(category, categoryCount.get(category)+1);
				}
				else
				{
					categoryCount.put(category, 1);
				}
			}
		}
		
		//write the category count pair to a file
		for(String key: categoryCount.keySet()){
			bufferedWriter.write(key + "\t" + categoryCount.get(key) + "\n");
			System.out.println("Category: " + key + " Count: " + categoryCount.get(key));
		}
		bufferedWriter.close();
		bufferedReader.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
