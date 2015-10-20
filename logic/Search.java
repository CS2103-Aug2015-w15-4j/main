package logic;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Search {
	
	
	/*
	 * Search function. Uses the Lucene Library to search through all the fields of a task. Only
	 * searches through 1 task at a time so range searches are not within the scope of this function.
	 */
	public static List<Task> search(List<Task> allTaskList, String query) throws IOException, ParseException{
		
		// Create the indexes with allTaskList
	    StandardAnalyzer analyzer = new StandardAnalyzer();
	    Directory index = new RAMDirectory();
	    IndexWriterConfig config = new IndexWriterConfig(analyzer);
	    IndexWriter w = new IndexWriter(index, config);
	    
	    for (Task task : allTaskList) {
	    	addDoc(w,task);
	    }
	    w.close();
	    
	    String querystr = query;
	    Query q = new QueryParser("name", analyzer).parse(querystr);	// Searches through the 'name' field by default 
	    																// if no field explicitly specified.
	    // Search
	    int hitsPerPage = 10;
	    IndexReader reader = DirectoryReader.open(index);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
	    searcher.search(q, collector);
	    ScoreDoc[] hits = collector.topDocs().scoreDocs;
	    
	    // Store results
	    ArrayList<Task> hitList = new ArrayList<Task>();
	    Gson gson = new Gson();
	    System.out.println("Found " + hits.length + " hits.");
	    for(int i=0;i<hits.length;++i) {
	        int docId = hits[i].doc;
	        Document d = searcher.doc(docId);
	        
	        hitList.add(gson.fromJson(d.get("json"),Task.class));
	    }
	    
		return hitList;
	}
	
	  private static void addDoc(IndexWriter w, Task task) throws IOException {
		    Document doc = new Document();
		    Gson gson = new Gson();
		    String jsonString = gson.toJson(task);
		    
		    doc.add(new TextField("name", task.getName(), Field.Store.YES));
		    doc.add(new TextField("id",new Integer(task.getId()).toString(), Field.Store.YES));
		    doc.add(new TextField("isCompleted",new Boolean(task.getIsCompleted()).toString(), Field.Store.YES));
		    String tagString = "";
		    for (String tags : task.getTags()) {
		    	tagString = tagString + tags + " ";
		    }
		    doc.add(new TextField("tags",tagString, Field.Store.YES));
		    doc.add(new TextField("taskType",new Integer(task.getTaskType()).toString(), Field.Store.YES));
		    doc.add(new TextField("details",task.getDetails(), Field.Store.YES));

		    doc.add(new StringField("json",jsonString,Field.Store.YES));
		    w.addDocument(doc);
		  }
	
	
}