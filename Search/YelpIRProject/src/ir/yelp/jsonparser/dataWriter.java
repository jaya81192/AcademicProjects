package ir.yelp.jsonparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class dataWriter {
	
	//gets the list of businessIds in the given city for the category Restaurants
	public static HashSet<String> businessList(String city)
	{
		HashSet<String> businesses = new HashSet<String>();
		File fileName = new File("/Users/bhavik/git/YelpIRProject/resources/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_business.json");
		try{
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
			String jsonContent = null;
			while ((jsonContent = bufferedReader.readLine()) != null) {
				JSONObject jsonObject = ir.yelp.jsonparser.DatasetReader.JSONReader(jsonContent);
				String bId = (String) jsonObject.get("business_id");
				JSONArray categories = (JSONArray) jsonObject.get("categories");
				for(int i=0; i < categories.size(); i++)
				{
					String category = (String) categories.get(i);
					String myCity = (String) jsonObject.get("city");
					if(category.equals("Restaurants") && myCity.equals(city)){
						businesses.add(bId);
					}
				}
			}
			bufferedReader.close();
		}
		catch(IOException io){
			io.printStackTrace();
		}
		return businesses;
	}
	
	
	//Adds reviews or concatenates review, increments count and adds rating to a hashmap for the 
	//businesses in a city given as parameter
	public static HashMap<String, HashMap<String, String>> addToMap(HashMap<String, HashMap<String, String>> cityFields, String bId, String review, Long rating)
	{
		if(cityFields.containsKey(bId)){
			HashMap<String, String> record = cityFields.get(bId);
			String oldReview = record.get("review");
			record.put("review",  oldReview + " " + review);	
			record.put("rating", Long.toString(Long.parseLong(record.get("rating")) + rating));
			record.put("count", Integer.toString(Integer.parseInt(record.get("count")) + 1)); 
			cityFields.put(bId, record);
		}
		else {
			HashMap<String, String> record = new HashMap<String, String>();
			record.put("review", review);
			record.put("rating", Long.toString(rating));
			record.put("count", Integer.toString(1));
			cityFields.put(bId, record);
		}
		return cityFields;
	}
	
	
	//writes the attributes, review, avgRating etc. to a file
	public static void writeToFile(HashMap<String, HashMap<String, String>> cityField, String city, Set<String> allAttributes){
		File file = new File("/Users/bhavik/git/YelpIRProject/resources/" + city + ".csv");
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
			for(String attr: allAttributes)
			{
				bufferedWriter.write(attr + ",");
			}
			bufferedWriter.write("\n");
			for(String key: cityField.keySet()){
				long avgRating = Long.parseLong(cityField.get(key).get("rating"))/Long.parseLong(cityField.get(key).get("count"));
				String review = cityField.get(key).get("review"); 
				if(review.equals("")){
					System.out.println("Empty review");
				}
				String attributes = cityField.get(key).get("attributes");
				bufferedWriter.write(attributes + "\t" + avgRating + "\n");
			}
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args){
		try {
			File reviewFile = new File("/Users/bhavik/git/YelpIRProject/resources/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_review.json");
			BufferedReader reviewReader = new BufferedReader(new FileReader(reviewFile));
			BufferedReader businessReader = new BufferedReader(new FileReader("/Users/bhavik/git/YelpIRProject/resources/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_business.json"));
			
			
			//Creates two hashmaps for the two cities
			String cityA = "Edinburgh";
			String cityB = "Montréal";
			HashMap<String, HashMap<String, String>> cityAFields = new HashMap<String, HashMap<String, String>>();
			HashMap<String, HashMap<String, String>> cityBFields = new HashMap<String, HashMap<String, String>>();
			
			//creates hashset of businesses for the two cities
			System.out.println("Creating Hashset of relevant business");
			HashSet<String> businessesCityA = businessList(cityA);
			HashSet<String> businessesCityB = businessList(cityB);
			businessesCityB.addAll(businessList("Montr\u00e9al"));
			String jsonContent = null;
			
			
			System.out.println("Adding only relevant reviews and ratings to hash");
			Set<String> allAttrColumnSet = new TreeSet<String>();
			Set<String> attrColumnSet;
			while ((jsonContent = businessReader.readLine()) != null) {
				JSONObject jsonObject = ir.yelp.jsonparser.DatasetReader.JSONReader(jsonContent);
				String attributes = jsonObject.get("attributes").toString();
				JSONObject attrJSON = ir.yelp.jsonparser.DatasetReader.JSONReader(attributes);
				attrColumnSet = attrJSON.keySet();
				allAttrColumnSet.addAll(attrColumnSet);

			}
			System.out.println(allAttrColumnSet);// 
			businessReader.close();
			
			
			//reads the given file. gets the rating and review and businessid
			//checks if one of the two cities have this businessId
			//if they do, this rating, review is added to the hashmap of the city
			while((jsonContent = reviewReader.readLine()) != null) {
				JSONObject jsonObject = ir.yelp.jsonparser.DatasetReader.JSONReader(jsonContent);
				Long rating = (Long) jsonObject.get("stars");
				String bId = (String) jsonObject.get("business_id");
				String review = (String) jsonObject.get("text");
				review = review.replaceAll("[^a-zA-Z ]", " ");
				review = review.trim();
				if(businessesCityA.contains(bId)){
					cityAFields = addToMap(cityAFields, bId, review, rating);
				}
				else if(businessesCityB.contains(bId)){
					cityBFields = addToMap(cityBFields, bId, review, rating);
				}
			}
			System.out.println("done");
			
			
			
			// reads the attributes of a business in the list of business in two cities and writes these attributes to a file
			businessReader = new BufferedReader(new FileReader("/Users/bhavik/git/YelpIRProject/resources/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_business.json"));
			//  re-read the business.json file
			while ((jsonContent = businessReader.readLine()) != null) {
				JSONObject jsonObject = ir.yelp.jsonparser.DatasetReader.JSONReader(jsonContent);
				String attributes = jsonObject.get("attributes").toString();//  get the attributes for a business
				System.out.println("====================================");
				System.out.println(attributes);
				
				String bId = (String) jsonObject.get("business_id");
				Iterator<String> attrIterator = allAttrColumnSet.iterator();//@ JAYA iterator for all attr
				String attr;
				while(attrIterator.hasNext())
				{
					JSONObject attrJSON = ir.yelp.jsonparser.DatasetReader.JSONReader(attributes);
					attr = (String) attrIterator.next();
					System.out.println("++++++Putting for column "+attr);
					//  now do the following
					
					HashMap<String, String> record = null;
					String recordVal;
					System.out.println("putting "+attrJSON.get(attr)+" in Map");
					if(attrJSON.get(attr) != null)
					System.out.println("instanceof "+attrJSON.get(attr)+ " is "+ attrJSON.get(attr).getClass().getCanonicalName());
					if(attrJSON.get(attr) == null)
					{
						System.out.println("Putting false in attr record");
						if((record = cityAFields.get(bId)) != null)
						{
							if((recordVal = record.get("attributes")) != null)
							{
								recordVal += ",false";
								record.put("attributes", recordVal);
							}
							else
							{
								record.put("attributes", "false");
							}
						}
						else if((record = cityBFields.get(bId)) !=null)
						{
							System.out.println("&&&&&&&&&& cityBrecord");
							if((recordVal = record.get("attributes")) != null)
							{
								recordVal += ",false";
								record.put("attributes", recordVal);
							}
							else
							{
								record.put("attributes", "false");
							}
						}
					}
					else
					{
						if(!(attrJSON.get(attr) instanceof org.json.simple.JSONObject)) // it is not JSON 
						{
							System.out.println("Not a JSON putting...");
							if((record = cityAFields.get(bId)) != null)
							{
								if((recordVal = record.get("attributes")) != null)
								{
									recordVal += ","+attrJSON.get(attr); // add value
									record.put("attributes", recordVal);
									System.out.println(recordVal);
								}
								else
								{
									record.put("attributes", attrJSON.get(attr).toString());
									System.out.println(attrJSON.get(attr).toString());
								}
							}
							else if((record = cityBFields.get(bId)) != null)
							{
								System.out.println("&&&&&&&&&& cityBrecord");
								if((recordVal = record.get("attributes")) != null)
								{
									recordVal += ","+attrJSON.get(attr); // add value
									record.put("attributes", recordVal);
									System.out.println(recordVal);
								}
								else
								{
									record.put("attributes", attrJSON.get(attr).toString());
									System.out.println(attrJSON.get(attr).toString());
								}
							}
						}
						else
						{// the attribute is a JSON
							Boolean retVal = parseInnerAttr(attrJSON.get(attr).toString());
							System.out.println("Putting false in attr record");
							if((record = cityAFields.get(bId)) != null)
							{
								if((recordVal = record.get("attributes")) != null)
								{
									recordVal += ","+retVal;
									record.put("attributes", recordVal);
								}
								else
								{
									record.put("attributes", retVal.toString());
								}
							}
							else if((record = cityBFields.get(bId)) !=null)
							{
								System.out.println("&&&&&&&&&& cityBrecord");
								if((recordVal = record.get("attributes")) != null)
								{
									recordVal += ","+retVal;
									record.put("attributes", recordVal);
								}
								else
								{
									record.put("attributes", retVal.toString());
								}
							}
						}
					}
					
				}
				
			}
			
			businessReader.close();
			reviewReader.close();
			
			System.out.println("Write to a file");
			writeToFile(cityAFields, cityA,allAttrColumnSet); //writeFileforcityA
			writeToFile(cityBFields, cityB,allAttrColumnSet); //writeFileforcityB
			
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	public static Boolean parseInnerAttr(String attributes)
	{
		System.out.println("parsing inner JSON" + attributes);
		JSONObject attrJSON =  ir.yelp.jsonparser.DatasetReader.JSONReader(attributes);
		@SuppressWarnings("unchecked")
		Set<String> keySet  = attrJSON.keySet();
		Iterator<String> keyIterator = keySet.iterator();
		while(keyIterator.hasNext())
		{
			String key = (String) keyIterator.next();
			if((attrJSON.get(key) instanceof java.lang.Boolean) && attrJSON.get(key).equals(true))
			{
				return true;
			}
			else
			{
				System.out.println("****#####*)@*)# parseInnerAttr : Not a boolean "+ attrJSON.get(key).getClass().getCanonicalName());
			}
		}
		return false;
	}
}
