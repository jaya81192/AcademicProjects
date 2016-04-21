package ir.yelp.filemanipulations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class AccumulateCategories {
	
	
	public static HashMap<String, String> businessList()
	{
		HashMap<String, String> businesses = new HashMap<String, String>();
		File fileName = new File("F:/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_business.json");
		try{
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
			String jsonContent = null;
			while ((jsonContent = bufferedReader.readLine()) != null) {
				JSONObject jsonObject = ir.yelp.jsonparser.DatasetReader.JSONReader(jsonContent);
				String bId = (String) jsonObject.get("business_id");
				JSONArray categories = (JSONArray) jsonObject.get("categories");
				String category = "";
				for(int i=0; i < categories.size(); i++){
					if(i==0){
						category = (String) categories.get(i);	
					}
					else{
						category += "|" + (String) categories.get(i);	
					}
				}
				businesses.put(bId, category);	
			}
			bufferedReader.close();
		}
		catch(IOException io){
			io.printStackTrace();
		}
		return businesses;
	}
	
	public static void putInFile(HashMap<String, String> businessCategoryHash)
	{
		File file = new File("F:/InformationRetrieval/YelpIRProject/outputFiles/partOneOutput.txt");
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
			HashMap<String, String> businesses = businessList();
			String businessId;
			Set<String> businessIdSet = businessCategoryHash.keySet();
			Iterator<String> businessIdIterator = businessIdSet.iterator();
			bufferedWriter.write("BusinessId" + "\t" + "PredictedCategories" + "\t" + "ActualCategories" + "\n");
			while(businessIdIterator.hasNext())
			{
				businessId = businessIdIterator.next();
				bufferedWriter.write(businessId + "\t" + businessCategoryHash.get(businessId) + "\t" + businesses.get(businessId) + "\n");
			}
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		String line;
		String categories;
		String[] tokens;
		HashMap<String, String> businessCategoryHash = new HashMap<String, String>(); 
		try {
			BufferedReader bFileReader = new BufferedReader(new FileReader("/Users/bhavik/predictionOutput.txt"));
			while((line = bFileReader.readLine()) != null)
			{
				tokens = line.split("\t");
				if((categories = businessCategoryHash.get(tokens[0])) != null)
				{
					categories += "|"+tokens[1];
					businessCategoryHash.put(tokens[0], categories);
				}
				else
					businessCategoryHash.put(tokens[0], tokens[1]);
			}
			bFileReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		putInFile(businessCategoryHash);
	}
}
