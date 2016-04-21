package ir.yelp.indexcreator;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONObject;

public class indexCreatorNonBatch {
	
	//creates a 
	public static HashMap<String, HashMap<String, String>> createHashMap(File fileName, String type,
			HashMap<String, HashMap<String, String>> corpusList) {
		String jsonContent = null;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
			while ((jsonContent = bufferedReader.readLine()) != null) {
				JSONObject jsonObject = ir.yelp.jsonparser.DatasetReader.JSONReader(jsonContent);
				HashMap<String, String> document = new HashMap<String, String>();
				String businessId = (String) jsonObject.get("business_id");
				String text = (String) jsonObject.get("text");
				if (corpusList.containsKey(businessId)) {
					document = corpusList.get(businessId);
					if (type.equals("tip")) {
						document.put("TIPTEXT", document.get("TIPTEXT") + " " + text.trim());
						corpusList.put(businessId, document);
					} else {
						document.put("REVIEWTEXT", document.get("REVIEWTEXT") + " " + text.trim());
						corpusList.put(businessId, document);
					}
				} else {
					if (type.equals("tip")) {
						document.put("TIPTEXT", text.trim());
						corpusList.put(businessId, document);
					} else {
						document.put("REVIEWTEXT", text.trim());
						corpusList.put(businessId, document);
					}
				}
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return corpusList;
	}

	
	
	//reads review and tip file, and concatenates all tip and review for each file by calling create hashmap function, 
	//and then indexes each business text into index
	public static void main(String[] args) {
		HashMap<String, HashMap<String, String>> corpusList = new HashMap<String, HashMap<String, String>>();
		System.out.println("tipfile="+args[0]);
		System.out.println("reviewfile="+args[1]);
		File file = new File(args[0]);
		corpusList = createHashMap(file, "tip", corpusList);
		file = new File(args[1]);
		corpusList = createHashMap(file, "review", corpusList);

		try {
			if (corpusList.size() != 0) {
				System.out.println(corpusList.size());
				String index = "F:/InformationRetrieval/YelpIRProject/index";
				Directory dir = FSDirectory.open(Paths.get(index));
				Analyzer analyzer = new EnglishAnalyzer();
				IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
				IndexWriter writer = new IndexWriter(dir, iwc);

				for (String key : corpusList.keySet()) {
					HashMap<String, String> doc = corpusList.get(key);
					String bID = key;
					String reviewText = "";
					String tipText = "";
					if (doc.containsKey("REVIEWTEXT")) {
						reviewText = doc.get("REVIEWTEXT");
					}
					if (doc.containsKey("TIPTEXT")) {
						tipText = doc.get("TIPTEXT");
					}
					Document lDoc = new Document();
					lDoc.add(new StringField("BusinessId", bID, Field.Store.YES));
					if(!reviewText.equals("")){
						FieldType ft = new FieldType(TextField.TYPE_STORED);
						
						ft.setTokenized(true);
						ft.setStoreTermVectors(true);
						ft.setStored(true);
						Field field = new Field("REVIEWTEXT", reviewText, ft);
						lDoc.add(field);
					}
					if(!tipText.equals("")){
						FieldType ft = new FieldType(TextField.TYPE_STORED);
						ft.setTokenized(true);
						ft.setStoreTermVectors(true);
						ft.setStored(true);
						Field field = new Field("TIPTEXT", tipText, ft);
						
						lDoc.add(field);
					}
					writer.addDocument(lDoc);
				}
				writer.forceMerge(1);
				writer.commit();
				writer.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
