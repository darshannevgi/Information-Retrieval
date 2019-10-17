import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;


public class MainClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File outputFile = new File(args[1]); 
		File inputFile = new File(args[2]); 
		String IndexPath = "index";
		File index = new File(IndexPath);
		HashMap<String, LinkedList<Integer>> dictionary = new HashMap<>();
		try {
			Directory indexDir = FSDirectory.open(index.toPath());
			IndexReader indexReader = DirectoryReader.open(indexDir);
			Fields fields = MultiFields.getFields(indexReader);
			for (String field : fields) {
				if(field.equals("id"))
					continue;
	             Terms terms = fields.terms(field);
	             TermsEnum termsEnum = terms.iterator();
	             PostingsEnum docsEnum =null ;
	             BytesRef term;
	         	while ((term = termsEnum.next()) != null) {
	         		
	         		docsEnum = termsEnum.postings(docsEnum, PostingsEnum.NONE);
	         		int docId;
	         		LinkedList<Integer> ll=new LinkedList<Integer>();
					
	                while ((docId = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
	                    ll.addLast(docId);
	                }
	                dictionary.put(term.utf8ToString(), ll);
	         	}
			}
		    BufferedReader br = null;
			  BufferedWriter wr = null;
			  String queryTerms[];
			try {
				br = new BufferedReader(new InputStreamReader(
					    new FileInputStream(inputFile), "UTF-8"));
				wr = new BufferedWriter(new OutputStreamWriter(
					    new FileOutputStream(outputFile,false), "UTF-8"));
				String line;
				int termCount;
				SortedMap<Integer, Integer> docScore =new TreeMap<Integer, Integer>();
				//Run through each line for each test case
				while(((line = br.readLine()) != null))
				{
					queryTerms = line.split("\\s");
					for (String string : queryTerms) {
						LinkedList<Integer> ii = dictionary.get(string);
						wr.write("GetPostings");
						wr.newLine();
						wr.write(string);
						wr.newLine();
						wr.write("Postings list: ");
						if(ii!=null)
						for (Integer integer : ii) {
							wr.write(integer + " ");
						}
						wr.newLine();
					}
					termCount = queryTerms.length;
					
				    //TAAT AND
				    //sort arrayList by increasing order of 
				    int counter = 0;
				    LinkedList< Integer> result = (LinkedList< Integer>)dictionary.get(queryTerms[counter]).clone();
				    LinkedList< Integer> resultUnion = (LinkedList< Integer>)dictionary.get(queryTerms[counter]).clone();
				    counter++;
				    LinkedList< Integer> termList ;
				    int taatORCompCount = 0;
				   while(result!=null && queryTerms.length > counter)
				   {
					   termList = (LinkedList< Integer>)dictionary.get(queryTerms[counter++]).clone();
					   Result res = intersect(result, termList);
					   result = res.getAnswer();
					   taatORCompCount+=res.getNoOfComparisons();
					   
				   }
				   counter = 1;
				   int taatAndCompCount = 0;
				   while(queryTerms.length > counter)
				   {
					   termList = (LinkedList< Integer>)dictionary.get(queryTerms[counter++]).clone();
					   Result res = union(resultUnion, termList);
					   resultUnion = res.getAnswer();
					   taatAndCompCount+=res.getNoOfComparisons();
					   
				   }
				   LinkedList< Integer> taatAndCount = result;
				   LinkedList< Integer> taatORCount = resultUnion;
				   //System.out.println("AND length " + result.size() );
				   for (Integer integer : result) {
					System.out.print(integer + "->");
				}
					

					//DAAT AND
					ArrayList<LinkedList<Integer>> postingLists = new ArrayList<LinkedList<Integer>>();
					counter =0;
					   while(queryTerms.length > counter)
					   {
						   LinkedList<Integer> newLL = (LinkedList<Integer>)dictionary.get(queryTerms[counter++]).clone();
						   postingLists.add(newLL);
					   }
					   Result res = andDAAT(postingLists);
					   LinkedList<Integer> daatAndCount = res.getAnswer();
					   int daatAndComCount = res.getNoOfComparisons();
					   
						ArrayList<LinkedList<Integer>> postingLists1 = new ArrayList<LinkedList<Integer>>();
						counter =0;
						   while(queryTerms.length > counter)
						   {
							   LinkedList<Integer> newLL = (LinkedList<Integer>)dictionary.get(queryTerms[counter++]).clone();
							   postingLists1.add(newLL);
						   }
						   res = orDAAT(postingLists1);
						   LinkedList<Integer> daatORCount = res.getAnswer();
						   int daatORComCount = res.getNoOfComparisons();
					
							
							//OUTPUT printing
							wr.write("TaatAnd");
						    wr.newLine();
						    wr.write(line);
						    wr.newLine();
						    if( taatAndCount.size()==0)
						    	wr.write("Results: empty");
						    else
						    	{
						    	wr.write("Results: ");
								    for (Integer integer : taatAndCount) {
								    	wr.write(integer + " ");
									}
						    	}
			
						    wr.newLine();
						    wr.write("Number of documents in results: " + taatAndCount.size());
						    wr.newLine();
						    wr.write("Number of comparisons: " + taatAndCompCount );
						    wr.newLine();
						    
						    //TAAT OR
						    wr.write("TaatOr");
						    wr.newLine();
						    wr.write(line);
						    wr.newLine();
						    if( taatORCount.size()==0)
						    	 wr.write("Results: empty");
						    else
						    	{
						    	 wr.write("Results: ");
								    for (Integer integer : taatORCount) {
								    	 wr.write(integer + " ");
									}
						    	}
			
						    wr.newLine();
						    wr.write("Number of documents in results: " + taatORCount.size());
						    wr.newLine();
						    wr.write("Number of comparisons: " + taatORCompCount);
						    wr.newLine();
						    
							wr.write("DaatAnd");
						    wr.newLine();
						    wr.write(line);
						    wr.newLine();
						    if( daatAndCount.size()==0)
						    	wr.write("Results: empty");
						    else
						    	{
						    	wr.write("Results: ");
								    for (Integer integer : daatAndCount) {
								    	wr.write(integer + " ");
									}
						    	}
			
						    wr.newLine();
						    wr.write("Number of documents in results: " + daatAndCount.size());
						    wr.newLine();
						    wr.write("Number of comparisons: " + daatAndComCount);
						    wr.newLine();
					
						    
							wr.write("DaatOr");
						    wr.newLine();
						    wr.write(line);
						    wr.newLine();
						    if( daatORCount.size()==0)
						    	wr.write("Results: empty");
						    else
						    	{
						    	wr.write("Results: ");
								    for (Integer integer : daatORCount) {
								    	wr.write(integer + " ");
									}
						    	}
			
						    wr.newLine();
						    wr.write("Number of documents in results: " + daatORCount.size());
						    wr.newLine();
						    wr.write("Number of comparisons: " + daatORComCount );
						    wr.newLine();
				}
				
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			  try {
				wr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

	}

	private static Result orDAAT(
			ArrayList<LinkedList<Integer>> postingLists1) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		int count = 0;
		ArrayList<Integer> minArray = new ArrayList<Integer>();
		boolean flag = true;
		LinkedList<Integer> answer = new LinkedList<Integer>();
		boolean isempty = false;
		while(!isempty)
		{
			minArray = new ArrayList<Integer>();
			for (LinkedList<Integer> iterator : postingLists1) {
				if(!iterator.isEmpty())
				{
				minArray.add(iterator.peek());
				}
			}
			if(minArray.size()==0)
				break;
			int min = Collections.min(minArray);
			//calculate min of all terms current pointer
			answer.add(min);
			
			int score =0;
			for (LinkedList<Integer> iterator : postingLists1) {
				if(iterator.isEmpty())
				{
					isempty = true;
					continue;
				}
				isempty = false;
				int currDocID;
					currDocID= iterator.peek()	;
					count++;
					if(currDocID == min)
					{
						iterator.pop();
					}

					
			}
			
				
		}
		System.out.println("DAAT or count" + count);
/*		for (Integer integer : answer) {
			System.out.print(integer + "->");	
		}*/
		Result result = new Result(answer,count);
		return result;
	
	}

	private static Result andDAAT(ArrayList<LinkedList<Integer>> postingLists) {
		// TODO Auto-generated method stub
		int count = 0;
		   ArrayList<Integer> maxArray = new ArrayList<Integer>();
		boolean flag = true;
		LinkedList<Integer> answer = new LinkedList<Integer>();
		while(flag)
		{
			maxArray = new ArrayList<Integer>();
			for (LinkedList<Integer> iterator : postingLists) {
				
				maxArray.add(iterator.peek());
			}
			int max = Collections.max(maxArray);
			//calculate max of all terms current pointer
			
			boolean andResult = true;
			int score =0;
			for (LinkedList<Integer> iterator : postingLists) {
				int currDocID;
				while(!iterator.isEmpty())
				{
					currDocID= iterator.peek()	;
					count++;
					if(currDocID > max)
					{
						andResult =false;
						break;
					}
					if(currDocID == max)
					{
						iterator.pop();
						score++;
						break;
					}
					iterator.pop();
				}
				if(iterator.isEmpty())
				{
					flag = false;
					break;
				}
				if(!andResult)
				{
					break;
				}
					
			}
			if(score == maxArray.size())
				answer.add(max);
			
				
		}
		System.out.println("DAAT And Count= "+ count);
		Result result = new Result(answer,count);
		return result;
	}

	private static Result union(LinkedList<Integer> resultUnion,
			LinkedList<Integer> termList) {
		// TODO Auto-generated method stub
		LinkedList<Integer> answer  = new LinkedList<Integer>();
		Iterator<Integer> resultIt = resultUnion.iterator();
		Iterator<Integer> termIt = termList.iterator();
		int counter=0;
		while(resultIt.hasNext() && termIt.hasNext())
		{
			int resultCurrDocID = resultIt.next();
			int termCurrDocID = termIt.next();
			boolean flag = true;
			while(resultCurrDocID != termCurrDocID)
			{
				counter++;
				if(resultCurrDocID > termCurrDocID)
				{
					answer.add(termCurrDocID);
					if(termIt.hasNext())
					{
						termCurrDocID = termIt.next();
					}
					
					else 
					{
						answer.add(resultCurrDocID);
						flag = false; 
						break;
					}
						
				}
				else
				{
					answer.add(resultCurrDocID);
					if(resultIt.hasNext())
					{
						
						resultCurrDocID = resultIt.next();
							
					}
						else 
					{
						answer.add(termCurrDocID);
						flag = false;
						break;
					}
						
				}
				
			}
			if(flag)
			{
				answer.add(resultCurrDocID);
			}
				
		}
		while(resultIt.hasNext())
			answer.add(resultIt.next());
		while(termIt.hasNext())
			answer.add(termIt.next());
		System.out.println(counter);
		Result result = new Result(answer,counter);
		return result;
	}

	private static Result intersect(LinkedList<Integer> result,
			LinkedList<Integer> termList) {
		// TODO Auto-generated method stub
		LinkedList<Integer> answer  = new LinkedList<Integer>();
		Iterator<Integer> resultIt = result.iterator();
		Iterator<Integer> termIt = termList.iterator();
		int counter=0;
		while(resultIt.hasNext() && termIt.hasNext())
		{
			int resultCurrDocID = resultIt.next();
			int termCurrDocID = termIt.next();
			boolean flag = true;
			while(resultCurrDocID != termCurrDocID)
			{
				counter++;
				if(resultCurrDocID > termCurrDocID)
				{
					if(termIt.hasNext())
					termCurrDocID = termIt.next();
					else 
					{
						flag = false; 
						break;
					}
						
				}
				else
				{
					if(resultIt.hasNext())
						resultCurrDocID = resultIt.next();
					else 
					{
						flag = false;
						break;
					}
						
				}
				
			}
			if(flag)
				answer.add(resultCurrDocID);
		}

		//System.out.println(counter);
		System.out.println(counter);
		Result result1 = new Result(answer,counter);
		return result1;
	}

}

class Result
{
  
	private LinkedList<Integer> answer;
	public Result(LinkedList<Integer> answer, int noOfComparisons) {
		super();
		this.answer = answer;
		this.noOfComparisons = noOfComparisons;
	}
	private int noOfComparisons ;
	public LinkedList<Integer> getAnswer() {
		return answer;
	}
	public void setAnswer(LinkedList<Integer> answer) {
		this.answer = answer;
	}
	public int getNoOfComparisons() {
		return noOfComparisons;
	}
	public void setNoOfComparisons(int noOfComparisons) {
		this.noOfComparisons = noOfComparisons;
	}
	
}

