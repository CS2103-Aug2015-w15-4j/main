package logic;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.*;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.google.gson.Gson;

import parser.ParsedCommand;
import parser.ParsedCommand.TaskType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Search {
	private static final TaskType TASK = TaskType.FLOATING_TASK;
	private static final TaskType DEADLINETASK = TaskType.DEADLINE_TASK;
	private static final TaskType EVENT = TaskType.EVENT;

	public List<Task> multiSearch(List<Task> taskList, ParsedCommand toSearch) throws IOException, ParseException{
		StandardAnalyzer analyzer = new StandardAnalyzer();
		Directory index = indexTaskList(taskList,analyzer);
		BooleanQuery bQuery = new BooleanQuery();

		if (toSearch.getKeywords() != null && !toSearch.getKeywords().isEmpty()) {
			String titleQuery = toSearch.getKeywords();

			Query title = createQuery(analyzer, titleQuery);
			bQuery.add(title, BooleanClause.Occur.SHOULD);

			try {
				String descriptionQuery = "details: " + toSearch.getKeywords();
				Query description = createQuery(analyzer, descriptionQuery);
				bQuery.add(description, BooleanClause.Occur.SHOULD);
			} catch (ParseException e) {
				// Search has a string value that parser does not accept, skip
			}

		}

		if (toSearch.getFirstDate() != null && toSearch.getSecondDate() != null) {
			TermRangeQuery dates = createDateQuery(analyzer,toSearch.getFirstDate(),toSearch.getSecondDate(),"end");
			bQuery.add(dates, BooleanClause.Occur.SHOULD);
			dates = createDateQuery(analyzer,toSearch.getFirstDate(),toSearch.getSecondDate(),"start");
			bQuery.add(dates, BooleanClause.Occur.SHOULD);
		}
		if (toSearch.getTaskId() >= 0) {
			String idQuery = "id: " + toSearch.getTaskId();
			Query isComplete = createQuery(analyzer, idQuery);
			bQuery.add(isComplete, BooleanClause.Occur.MUST);
		}
		if (toSearch.getTags() != null && toSearch.getTags().size() != 0) {
			String tagQuery = "tags: ";
			for (String tag : toSearch.getTags()) {
				tagQuery += tag + " ";
			}
			Query tags = createQuery(analyzer, tagQuery);
			bQuery.add(tags, BooleanClause.Occur.MUST);
		}
		if (toSearch.isCompleted() != null) {
			String isCompleteQuery = "isCompleted: " + toSearch.isCompleted();
			Query isComplete = createQuery(analyzer, isCompleteQuery);
			bQuery.add(isComplete, BooleanClause.Occur.MUST);
		}
		if (toSearch.getTaskType() != null) {
			String typeQuery = "taskType: " + toSearch.getTaskType();
			Query type = createQuery(analyzer, typeQuery);
			bQuery.add(type, BooleanClause.Occur.MUST);
		}


		return queryIndex(index, bQuery);
	}

	/*
	 * Search function. Uses the Lucene Library to search through all the fields of a task. Only
	 * searches through 1 task at a time so range searches are not within the scope of this function.
	 */
	public static List<Task> search(List<Task> taskList, String query) throws IOException, ParseException{
		StandardAnalyzer analyzer = new StandardAnalyzer();
		Directory index = indexTaskList(taskList,analyzer);
	    
	    String querystr = query;
	    Query q = new QueryParser("name", analyzer).parse(querystr);	// Searches through the 'name' field by default 
	    																// if no field explicitly specified.
	    ArrayList<Task> hitList = queryIndex(index, q);
	    
		return hitList;
	}

	private Query createQuery(StandardAnalyzer analyzer, String querystr) throws ParseException {
		Query q = new QueryParser("name", analyzer).parse(querystr);
		return q;
	}

	/*
	 * Searches the index list using the search query provided. Stores the results in an ArrayList and returns that list.
	 */
	private static ArrayList<Task> queryIndex(Directory index, Query q)
			throws IOException {
		// Search
	    int hitsPerPage = 1000;
	    IndexReader reader = DirectoryReader.open(index);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
	    searcher.search(q, collector);
	    ScoreDoc[] hits = collector.topDocs().scoreDocs;
	    
	    // Store results
	    ArrayList<Task> hitList = new ArrayList<Task>();
	    Gson gson = new Gson();
	    for(int i=0;i<hits.length;++i) {
	        int docId = hits[i].doc;
	        Document d = searcher.doc(docId);
	        
	        Task task = gson.fromJson(d.get("json"),Task.class);
	        if (task.getTaskType() == TASK) {
	        	hitList.add(gson.fromJson(d.get("json"),Task.class));
	        } else if (task.getTaskType() == DEADLINETASK) {
	        	hitList.add(gson.fromJson(d.get("json"),DeadlineTask.class));
	        } else if (task.getTaskType() == EVENT) {
	        	hitList.add(gson.fromJson(d.get("json"),Event.class));
	        }
	    }
		return hitList;
	}
	
	/*
	 * range query that can search through various fields
	 */
	public static List<Task> rangeSearch(List<Task> taskList, String field, String upper, String lower) throws IOException {
		
		StandardAnalyzer analyzer = new StandardAnalyzer();
	    Directory index = indexTaskList(taskList,analyzer);
	    
		TermRangeQuery rq = TermRangeQuery.newStringRange(field ,upper,lower,true,true);
		ArrayList<Task> hitList = queryIndex(index, rq);
		
		return hitList;
	}
	
	/*
	 * A search involving fields with dates
	 */
	public static List<Task> searchDate(List<Task> taskList, Calendar fromDate, Calendar toDate) throws IOException, ParseException{
		
		// Create indexes with taskList
		StandardAnalyzer analyzer = new StandardAnalyzer();
	    Directory index = indexTaskList(taskList,analyzer);
	    
	    // Set up Query
		String sFromDate = DateTools.dateToString(fromDate.getTime(),
		        Resolution.SECOND);
		String sToDate = DateTools.dateToString(toDate.getTime(),
		        Resolution.SECOND);
		
	    TermRangeQuery rq = TermRangeQuery.newStringRange("end" ,sFromDate,sToDate,true,true);
	    
	    ArrayList<Task> hitList = queryIndex(index, rq);

		return hitList;
	}

	private TermRangeQuery createDateQuery(StandardAnalyzer analyzer, Calendar fromDate, Calendar toDate,String field) {
		// Set up Query
		String sFromDate = DateTools.dateToString(fromDate.getTime(),
				Resolution.SECOND);

		String sToDate = DateTools.dateToString(toDate.getTime(),
				Resolution.SECOND);

		TermRangeQuery rq = TermRangeQuery.newStringRange(field ,sFromDate,sToDate,true,true);

		return rq;
	}

	/*
	 * Indexes the taskList specified for Lucene to do its search with
	 */
	private static Directory indexTaskList(List<Task> taskList,StandardAnalyzer analyzer)
			throws IOException {
	    Directory index = new RAMDirectory();
	    IndexWriterConfig config = new IndexWriterConfig(analyzer);
	    IndexWriter w = new IndexWriter(index, config);
	    
	    for (Task task : taskList) {
	    	addDoc(w,task);
	    }
	    w.close();
		return index;
	}
	
	/*
	 * Specific index Writer for tasks/DeadlineTasks/Events
	 */
	  private static void addDoc(IndexWriter w, Task task) throws IOException {
		    Document doc = new Document();
		    Gson gson = new Gson();
		    String jsonString = gson.toJson(task);

		  if(task.getTaskType().equals(ParsedCommand.TaskType.DEADLINE_TASK)) {
			  DeadlineTask dlTask = (DeadlineTask) task;
			  doc.add(new TextField("end", DateTools.dateToString(dlTask.getEnd().getTime(), DateTools.Resolution.SECOND), Field.Store.YES));
		  } else if(task.getTaskType().equals(ParsedCommand.TaskType.EVENT)) {
			  Event event = (Event) task;
			  doc.add(new TextField("start", DateTools.dateToString(event.getStart().getTime(), DateTools.Resolution.SECOND), Field.Store.YES));
			  doc.add(new TextField("end", DateTools.dateToString(event.getEnd().getTime(), DateTools.Resolution.SECOND), Field.Store.YES));
		  }
		    doc.add(new TextField("name", task.getName(), Field.Store.YES));
		    doc.add(new TextField("id",new Integer(task.getId()).toString(), Field.Store.YES));
		    doc.add(new TextField("isCompleted",new Boolean(task.getIsCompleted()).toString(), Field.Store.YES));
		    String tagString = "";
		    for (String tags : task.getTags()) {
		    	tagString = tagString + tags + " ";
		    }
		    doc.add(new TextField("tags",tagString, Field.Store.YES));
		    doc.add(new TextField("taskType",task.getTaskType().toString(), Field.Store.YES));
		    if (task.getDetails() != null) {
		    	doc.add(new TextField("details",task.getDetails(), Field.Store.YES));
		    }
		    
		    doc.add(new StringField("json",jsonString,Field.Store.YES));
		    w.addDocument(doc);
		  }
	
	
}