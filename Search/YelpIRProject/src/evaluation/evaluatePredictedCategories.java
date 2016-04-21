package evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class evaluatePredictedCategories {

	
	//creates a hashmap with all the categories and businesses with these categories as actual categories
	public static HashMap<String, HashMap<String, Integer>> categoryHashMapCreator(){
		HashMap<String, HashMap<String, Integer>> categories = new HashMap<String, HashMap<String, Integer>>();
		File file = new File("F:/InformationRetrieval/YelpIRProject/New folder/categoriesAndCount.txt");
		try{
			BufferedReader readCatFile = new BufferedReader(new FileReader(file));
			String line = null;
			int i = 0;
			while((line = readCatFile.readLine())!=null && i < 151){
				i++;
				HashMap<String, Integer> category = new HashMap<String, Integer>();
				String[] lineParsed = line.split("\t");
				category.put("count", Integer.parseInt(lineParsed[1]));
				categories.put(lineParsed[0], category);
			}
			readCatFile.close();
		}
		catch(IOException io){
			io.printStackTrace();
		}
		return categories;
	}
	
	
	//checks true category of business and predicted category of business and finds true positives and add it to
	//hashmap
	public static HashMap<String, HashMap<String, Integer>> getTruePositives(){
		File file = new File("F:/InformationRetrieval/YelpIRProject/New folder/partOneOutput.txt");
		HashMap<String, HashMap<String, Integer>> categories = categoryHashMapCreator(); 
		try{
			BufferedReader readPredFile = new BufferedReader(new FileReader(file));
			String line = null;
			readPredFile.readLine();
			while((line = readPredFile.readLine())!=null){
				String[] parsedLine = line.split("\t");
				if(parsedLine.length == 3){
					String predCategory = parsedLine[1];
					String actCategory = parsedLine[2];
					ArrayList<String> predCategoryList = new ArrayList<String>();
					if(predCategory.contains("|")){
						String[] temp = predCategory.split("\\|");
						for(int k = 0; k<temp.length;k++){
							predCategoryList.add(temp[k]);
						}
					}
					else{
						predCategoryList.add(predCategory); 
					}
					for(int p = 0; p < predCategoryList.size(); p++){
						if(actCategory.contains(predCategoryList.get(p))){
							HashMap<String, Integer> category = categories.get(predCategoryList.get(p));
							if(category != null)
							{
								if(category.containsKey("truePos")){
									category.put("truePos", category.get("truePos")+1);
								}else{
									category.put("truePos", 1);
								}
								if(category.containsKey("totalPredicted")){
									category.put("totalPredicted", category.get("totalPredicted")+1);
								}else{
									category.put("totalPredicted", 1);
								}
							}
							categories.put(predCategoryList.get(p), category);
						}
					}
				}
			}
			readPredFile.close();
		}
		catch(IOException io){
			io.printStackTrace();
		}
		return categories;
	}
	
	
	// goes over the returned hashmaps containing true positives of category, actual count of businesses with each categories
	//and total businesses predicted as a category
	//uses hashmap to compute precision and recall
	public static void main(String[] args){
		HashMap<String, HashMap<String, Integer>> categories = getTruePositives();
		float sumPrec = 0;
		float sumRecall = 0;
		for(String categoryKey: categories.keySet()){
			HashMap<String, Integer> category = categories.get(categoryKey);
			if(category.containsKey("truePos") && category.containsKey("totalPredicted") && category.containsKey("count")){
				sumPrec = sumPrec + (float)category.get("truePos")/(float)category.get("totalPredicted");
				sumRecall = sumRecall + (float)category.get("truePos")/(float)category.get("count");
			}
		}
		float avgPrec = (float)sumPrec/(float)categories.size();
		float avgRecall = (float)sumRecall/(float)categories.size();
		System.out.println("Average Precision: " + avgPrec + " Average Recall: " + avgRecall);
	}
}
