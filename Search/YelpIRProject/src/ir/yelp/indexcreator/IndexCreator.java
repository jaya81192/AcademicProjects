package ir.yelp.indexcreator;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONObject;

public class IndexCreator {
	
	//indexing a document into the index. If the document is already in index, it retrieves doc, modifies it and 
	//indexes it back 
	public static void addToIndex(String businessId, String text, String fieldType, boolean indexCreated) {
		try {
			// Creating a document for the field
			Document lDoc = new Document();
			lDoc.add(new StringField("BusinessId", businessId, Field.Store.YES));
			if (fieldType.equals("review")) {
				FieldType ft = new FieldType(TextField.TYPE_STORED);
				
				ft.setTokenized(true);
				ft.setStoreTermVectors(true);
				ft.setStored(true);
				Field field = new Field("REVIEWTEXT", text, ft);
				lDoc.add(field);
			} else if (fieldType.equals("tip")) {
				FieldType ft = new FieldType(TextField.TYPE_STORED);
				ft.setTokenized(true);
				ft.setStoreTermVectors(true);
				ft.setStored(true);
				Field field = new Field("TIPTEXT", text, ft);
				lDoc.add(field);
			}

			String index = "/Users/bhavik/git/YelpIRProject/resources/index";
			Document doc;

			// setting indexWriter
			Directory dir = FSDirectory.open(Paths.get(index));
			Analyzer analyzer = new EnglishAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			IndexWriter writer = new IndexWriter(dir, iwc);

			// if there already exists an index
			if (indexCreated) {
				// setting indexReader
				IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
				IndexSearcher searcher = new IndexSearcher(reader);
				List<LeafReaderContext> leafContexts = reader.getContext().reader().leaves();

				// checking if the document is already indexed
				String currentDocId = null;
				int currentDoc = 0;
				int currentStartDocNo = 0;
				for (int i = 0; i < leafContexts.size(); i++) {
					LeafReaderContext leafContext = leafContexts.get(i);
					int startDocNo = leafContext.docBase;
					int numberOfDoc = leafContext.reader().maxDoc();
					for (int docId = 0; docId < numberOfDoc; docId++) {
						if (searcher.doc(docId + startDocNo).get("BusinessId").equals(businessId)) {
							currentDocId = Integer.toString(docId);
							currentDoc = docId;
							currentStartDocNo = startDocNo;
						}
					}
				}

				// updating index depending on if the document is in index or
				// not
				if (currentDocId != null) {
					doc = searcher.doc(currentDoc + currentStartDocNo);
					List<IndexableField> replacementFields = lDoc.getFields();
					for (IndexableField field : replacementFields) {
						String fieldName = field.name();
						if (!fieldName.equals("BusinessId")) {
							String currentValue = doc.get(fieldName);
							if (currentValue != null) {
								String textField = currentValue.trim() + " " + lDoc.get(fieldName).trim();
								doc.removeFields(fieldName);
								// insert the replacement
								doc.add(new TextField(fieldName, textField, Field.Store.YES));
							} else {
								doc.add(field);
							}
						} else {
							doc.add(field);
						}
					}
					writer.updateDocument(new Term("BusinessId", businessId), doc);
				} else {
					doc = lDoc;
					writer.addDocument(doc);
				}
				reader.close();
			} else {
				doc = lDoc;
				writer.addDocument(doc);
			}
			writer.forceMerge(1);
			writer.commit();
			writer.close();
		} catch (IOException ioE) {
			ioE.printStackTrace();
		}
	}

	
	//parses JSONFile, extracts reviews/tip text and appends for each document till 
	//hashmap reaches a size limit, indexing is done in batches here (range = 10000)
	public static void parseJSONFile(File fileName, String type, boolean indexPresent) {
		String jsonContent = null;
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));

			HashMap<String, HashMap<String, String>> corpusList = new HashMap<String, HashMap<String, String>>();
			while ((jsonContent = bufferedReader.readLine()) != null) {
				JSONObject jsonObject = ir.yelp.jsonparser.DatasetReader.JSONReader(jsonContent);
				HashMap<String, String> document = new HashMap<String, String>();
				String businessId = (String) jsonObject.get("business_id");
				String text = (String) jsonObject.get("text");
				if (corpusList.containsKey(businessId)) {
					document = corpusList.get(businessId);
					if (type.equals("tip")) {
						document.put("TIPTEXT", document.get("TIPTEXT").trim() + " " + text.trim());
						corpusList.put(businessId, document);
					} else {
						document.put("REVIEWTEXT", document.get("REVIEWTEXT").trim() + " " + text.trim());
						corpusList.put(businessId, document);
					}
				} else {
					document.put("BusinessId", businessId);
					if (type.equals("tip")) {
						document.put("TIPTEXT", text.trim());
						corpusList.put(businessId, document);
					} else {
						document.put("REVIEWTEXT", text.trim());
						corpusList.put(businessId, document);
					}
				}

				
				//performs indexing in batches of 10000
				if (corpusList.size() >= 10000) {
					System.out.println(corpusList.size());
					for (String key : corpusList.keySet()) {
						HashMap<String, String> doc = corpusList.get(key);
						String bID = doc.get("BusinessId");
						if (type.equals("review")) {
							String txt = doc.get("REVIEWTEXT");
							addToIndex(bID, txt, type, indexPresent);
							indexPresent = true;
						} else {
							String txt = doc.get("TIPTEXT");
							addToIndex(bID, txt, type, indexPresent);
							indexPresent = true;
						}
					}
					corpusList = new HashMap<String, HashMap<String, String>>();
				}
			}

			//performs index on the last batch
			if (corpusList.size() != 0) {
				System.out.println(corpusList.size());
				for (String key : corpusList.keySet()) {
					HashMap<String, String> doc = corpusList.get(key);
					String bID = doc.get("BusinessId");
					if (type.equals("review")) {
						String txt = doc.get("REVIEWTEXT");
						addToIndex(bID, txt, type, indexPresent);
					} else {
						String txt = doc.get("TIPTEXT");
						addToIndex(bID, txt, type, indexPresent);
					}
				}
			}

			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	//Indexes the tips first and then reviews
	public static void main(String[] args) {
		File file = new File("/Users/bhavik/git/YelpIRProject/resources/yelp_dataset_challenge_academic_dataset/tip_5000.json");
		parseJSONFile(file, "tip", false);
		file = new File("/Users/bhavik/git/YelpIRProject/resources/yelp_dataset_challenge_academic_dataset/review_5000.json");
		parseJSONFile(file, "review", true);
	}
}
