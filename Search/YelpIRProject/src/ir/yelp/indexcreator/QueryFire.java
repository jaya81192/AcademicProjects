package ir.yelp.indexcreator;

import ir.yelp.jsonparser.BusinessCategories;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class QueryFire {
	static int LIMIT_WORD_QUERY = 10;
	static float IDF_THRESH = 2.0f;
	static int WORD_LENGTH_THRES = 2;
	static BufferedWriter outputWriter;
	static File businessJSONFile = null;
	public static void runSearch(Similarity similarity, String queryString, int LIMIT_SEARCH, Integer totalDocsinCategory, String index)
	{
		int cutoff1, cutoff2;
		IndexReader reader;
		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new EnglishAnalyzer();
			searcher.setSimilarity(similarity);
			QueryParser parser = new QueryParser("REVIEWTEXT", analyzer);
			Query query = parser.parse(QueryParser.escape(queryString));

			TopDocs results = searcher.search(query, LIMIT_SEARCH);
			// Print number of hits
			int numTotalHits = results.totalHits;
			System.out.println(numTotalHits);
			// Print retrieved results
			ScoreDoc[] hits1 = results.scoreDocs;
			
			ArrayList<Double> returnStatistics1 = new ArrayList<Double>();
			cutoff1 = calculatePrecision(hits1,searcher, queryString, returnStatistics1, totalDocsinCategory);
			System.out.println("Precision="+returnStatistics1.get(0));
			System.out.println("Recall="+returnStatistics1.get(1));
			// =============== If initial query has no hits return ======================
			if(hits1.length == 0)
				return;
			//====================== Trying to improve the query ==========================
			 // access term vector fields for a first document to improve the search result
			 Fields fields = reader.getTermVectors(hits1[0].doc);

			 ArrayList<String> termList = new ArrayList<String>();
			 for (String field : fields) {
			   // access the terms for this field
			   Terms terms = fields.terms(field);
			   TermsEnum tE = terms.iterator();
			   BytesRef t;
			   
			   while((t = tE.next()) != null)
			   {
				   termList.add(t.utf8ToString());
			   }
			}
			//======================Append terms in query===========================
			int df;
			double IDF = 0;
			
			StringBuffer queryfinal = new StringBuffer("\""+queryString+"\"^100 " );// weight of previous query is 100
			int totalNumDocs = reader.numDocs();
			//======================== Calculate IDF ===========================
			for(int i = 0; i < termList.size() ; i++)
			{
				df = reader.docFreq(new Term("REVIEWTEXT", termList.get(i)));
				if((termList.get(i).length() > WORD_LENGTH_THRES) && (df != 0))
				{
					IDF = Math.log10(1 + (totalNumDocs/df));
					System.out.println(i+")Term: '"+termList.get(i) + "' IDF: "+ IDF);
					if(IDF > IDF_THRESH)
					queryfinal.append("\""+termList.get(i) + "\"^" + (20 * IDF ) + " ");// use IDF as weights
				}
				IDF = 0;
			}
			System.out.println(queryfinal);
			//==========================Fire the Final Query and get results============
			
			query = parser.parse(QueryParser.escape(queryfinal.toString()));
			TopDocs resultsFinal = searcher.search(query, LIMIT_SEARCH);
			ScoreDoc[] hitsFinal = resultsFinal.scoreDocs;

			ArrayList<Double> returnStatistics2 = new ArrayList<Double>();
			cutoff2 = calculatePrecision(hitsFinal,searcher, queryString, returnStatistics2, totalDocsinCategory);
			System.out.println("Precision="+returnStatistics2.get(0));
			System.out.println("Recall="+returnStatistics2.get(1));
			
			//=========== Check precision of both queries and print the results with higher precision ======
			if(returnStatistics1.get(0) > returnStatistics2.get(0))
			{
				for (int i = 0; i < cutoff1; i++) {
					Document doc = searcher.doc(hits1[i].doc);
					System.out.println( i + ") " + doc.get("BusinessId")  + " "+ hits1[i].score);
					outputWriter.write(doc.get("BusinessId")+"\t"+queryString+"\n");
					System.out.println();
				}
			}
			else
			{
				for (int i = 0; i < cutoff2; i++) {
					Document doc = searcher.doc(hitsFinal[i].doc);
					System.out.println( i + ") " + doc.get("BusinessId")  + " "+ hitsFinal[i].score);
					outputWriter.write(doc.get("BusinessId")+"\t"+queryString+"\n");
					System.out.println();
				}
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static int calculatePrecision(ScoreDoc[] hits, IndexSearcher searcher, String category, ArrayList<Double> returnStatistics,int totalDocs)
	{
		Object[] categoryArray;
		int truePositive = 0;
		int cutoff = 0;
		for (int i = 0; i < hits.length; i++) {
			Document doc;
			try {
				doc = searcher.doc(hits[i].doc);
				categoryArray =  BusinessCategories.extractCategory(businessJSONFile, doc.get("BusinessId"));
				
				for(int j=0; j < categoryArray.length ; j++)
				{
					if(((String)categoryArray[j]).equalsIgnoreCase(category))
					{
						truePositive++;
						cutoff = i;
						break;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		System.out.println("True positive="+truePositive);
		System.out.println("Total Docs Retv="+hits.length);
		returnStatistics.add( ((double)truePositive/(double)(cutoff+1)));
		returnStatistics.add(((double)truePositive/(double)totalDocs));
		return cutoff + 1;
	}

	public static void fireQ(String categoryFile, String indexLocation)
	{
		String[] query = null;
		String line = null;
		int totalDocs = 0;
		Similarity bm25 = new BM25Similarity();
		
		try {
			outputWriter = new BufferedWriter(new FileWriter("predictionOutput.txt"));
			BufferedReader bfr = new BufferedReader(new FileReader(categoryFile));
			while((line = bfr.readLine()) != null)
			{
				System.out.println(line);
				query = line.split("\t");
				totalDocs = Integer.parseInt(query[1]);
				System.out.println("Initial Q:"+query[0]+" totoal docs:"+totalDocs);
				runSearch(bm25, query[0], 2 * totalDocs, totalDocs, indexLocation);
			}
			bfr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		businessJSONFile = new File("/Users/bhavik/git/YelpIRProject/resources/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_business.json");
		fireQ("/Users/bhavik/git/YelpIRProject/resources/categoriesAndCount5000.txt","aaa");
	}
}
