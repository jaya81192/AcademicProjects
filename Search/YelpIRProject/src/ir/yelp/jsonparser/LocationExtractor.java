
package ir.yelp.jsonparser;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.simple.*;


public class LocationExtractor {
	
	public FileWriter fwc;
	public BufferedWriter bwc;
	public File output2 = new File("/Users/Shruti/Git/YelpIRProject/bin/Resources/cityMap.txt");

	HashMap<String,ArrayList<String>> cityMap = new HashMap<String, ArrayList<String>>();
	public LocationExtractor() {
		//Constructor
		
		
	}
	
	@SuppressWarnings("unchecked")

	public void parseJSONFile(File fileName, String type) throws IOException {
		
		String jsonContent = null;
		fwc=new FileWriter(output2, true);
		bwc = new BufferedWriter(new BufferedWriter(fwc));

		try {
			
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
			while ((jsonContent = bufferedReader.readLine()) != null) {
				JSONObject jsonObject = ir.yelp.jsonparser.DatasetReader.JSONReader(jsonContent);
				
				
				String businessId = (String) jsonObject.get("business_id");
				String full_address = (String) jsonObject.get("full_address");
				
				JSONObject hours = (JSONObject) jsonObject.get("hours");
				JSONObject Tuesday = (JSONObject) hours.get("Tuesday");
				JSONObject Monday = (JSONObject) hours.get("Monday");
				JSONObject Wednesday = (JSONObject) hours.get("Wednesday");
				JSONObject Thursday = (JSONObject) hours.get("Thursday");
				JSONObject Friday = (JSONObject) hours.get("Friday");
				JSONObject Saturday = (JSONObject) hours.get("Saturday");
				hours.get("Sunday");
				String Topen = null;
				if(Tuesday!=null)
				{
					Topen = (String) Tuesday.get("open");
					Tuesday.get("close");
				}
				if(Monday!=null)
				{
					Monday.get("open");
				}
				if(Wednesday!=null)
				{
					Wednesday.get("open");
				}
				if(Thursday!=null)
				{
					Thursday.get("open");
				}
				if(Friday!=null)
				{
					Friday.get("open");
				}
				if(Saturday!=null)
				{
					Saturday.get("open");
				}
				JSONArray categories = (JSONArray) jsonObject.get("categories");
				String cityInfo = (String) jsonObject.get("city");
				jsonObject.get("review_count");
				String name = (String) jsonObject.get("name");
				String state = (String) jsonObject.get("state");
				
				ArrayList<String> Cinfo = new ArrayList<String>();
				
				Cinfo.add(businessId);
				Cinfo.add(full_address);
				Cinfo.add(Topen);
				Cinfo.addAll(categories);
				Cinfo.add(cityInfo);
				Cinfo.add(name);
				Cinfo.add(state);
				cityMap.put(cityInfo,Cinfo);
					
			}
			
			for(Entry<String, ArrayList<String>> cityMap :cityMap.entrySet())
			{
				bwc.write(cityMap.getKey());
				bwc.newLine();
			}
			bwc.close();
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
