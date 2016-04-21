//Name: Jayalakshmi Sureshkumar
//Email: jsureshk@umail.iu.edu

package ir.yelp.indexcreator;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class statsOnIndex 
{
	
	//prints number of document indexed, vocabulary, size of vocabulary
	public static void main(String[] args) throws IOException
	{
		//stats for part 1 - total document count
		IndexReader readerpart1 = DirectoryReader.open(FSDirectory.open(Paths.get(("F:\\InformationRetrieval\\YelpIRProject\\index"))));
		System.out.println("Total number of documents in the corpus: "+ readerpart1.maxDoc());
		System.out.println("");
		Terms vocabulary2 = MultiFields.getTerms(readerpart1, "BusinessId");
		System.out.println(vocabulary2.size());
		
		TermsEnum iterator3 = vocabulary2.iterator();
		BytesRef byteRef3 = null;
		System.out.println("\n*******Vocabulary-Start**********");
		while ((byteRef3 = iterator3.next()) != null) {
			String term = byteRef3.utf8ToString();
			System.out.println(term);
		}
		
		
		System.out.println("Vocabulary end");
		
		IndexSearcher searcher = new IndexSearcher(readerpart1);
		List<LeafReaderContext> leafContexts = readerpart1.getContext().reader().leaves();

		
		for (int i = 0; i < leafContexts.size(); i++) {
			LeafReaderContext leafContext = leafContexts.get(i);
			int startDocNo = leafContext.docBase;
			int numberOfDoc = leafContext.reader().maxDoc();
			for (int docId = 0; docId < numberOfDoc; docId++) {
				System.out.println(searcher.doc(docId + startDocNo).get("BusinessId"));
				
			}
		}		
		readerpart1.close();
	}
}
