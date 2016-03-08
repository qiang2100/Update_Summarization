package Preprocess_Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import kex.stopwords.Stopwords;
import kex.stopwords.StopwordsEnglish;
import kex.stemmers.MartinPorterStemmer;

public class ExtractText {

	int id = 0;
	
	private MartinPorterStemmer m_Stemmer = new MartinPorterStemmer();
	MaxentTagger tagger = new MaxentTagger("C:/Users/jipeng/Desktop/TopicModel/stanford-postagger-2014-01-04/models/english-left3words-distsim.tagger");
	private Stopwords m_EnStopwords = new StopwordsEnglish();
	
	private HashMap<String, Integer> word2IdHash = new HashMap<String, Integer>(); //<word, word's id>
	
	ArrayList<String> allWordsArr = new ArrayList<String>();
	
	public String readText(String path) throws Exception
	{
		BufferedReader br1 = new BufferedReader(new FileReader(path));
		String text = "";
		String line = "";
		boolean isHead = false;
		boolean isText = false;
		String head = "";
		while ((line = br1.readLine()) != null) 
		{
			//System.out.println(line);
			if(line.equals("<P>") || line.equals("</P>"))
				continue;
			if(line.equals("<HEADLINE>"))
			{
				isHead = true;
				continue;
			}
			
			if(line.equals("<TEXT>"))
			{
				isText = true;
				continue;
			}
			
			if(line.equals("</HEADLINE>") )
			{
				head += ". ";
				text += head;
				isHead = false;
				continue;
			}
			if(line.equals("</TEXT>"))
			{
				isText = false;
				continue;
			}
			
			if(isHead)
				head += line + " ";
			
			if(isText)
				text += line + " ";
		}
		
		if( isHead || isText)
			System.out.println(path + " The text has false formulation");
		br1.close();
			
		return text;
	 }
	
	public void readFile(String path) throws Exception
	{
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
	
		//boolean isHistory =  true;
		
		FileWriter fwText;
		BufferedWriter bwText;
		
		 
	      
	      File file2 = new File("C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/Process/"+ folder.getName());
	  	if (!file2.exists()) {
	  		if (file2.mkdir()) {
	  			System.out.println("Directory is created!");
	  		} else {
	  			System.out.println("Failed to create directory!");
	  		}
	  	}
	      
		//System.out.println(folder.getName());
		
		for (File file : listOfFiles) {
		  
			File[] nameList = file.listFiles();
			//System.out.println(file.getPath());
			
			File file3 = new File("C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/Process/"+ folder.getName()+ "/"+file.getName());
		  	if (!file3.exists()) {
		  		if (file3.mkdir()) {
		  			System.out.println("Directory is created!");
		  		} else {
		  			System.out.println("Failed to create directory!");
		  		}
		  	}
			
			
			for(File subFile:nameList)
			{
				//System.out.println(subFile.getName());
				
				String text = readText(subFile.getAbsolutePath());
				
				fwText = new FileWriter("C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/Process/"+ folder.getName()+ "/"+file.getName()+"/" + subFile.getName()+  ".txt");
				bwText = new BufferedWriter(fwText);
				
				//System.out.println(text);
				bwText.write(text);
				bwText.close();
				fwText.close();
			}
			
			
		}
				
	}
	
	
	
	public void writeSentences(String path) throws Exception
	{
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
	
		//boolean isHistory =  true;
		
		FileWriter fwText;
		BufferedWriter bwText;
		FileWriter fwSentText;
		BufferedWriter bwSentText;
		//System.out.println(folder.getName());
		
		id = 0;
		word2IdHash.clear();
		allWordsArr.clear();
		
		File file2 = new File("C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/Sentences/"+ folder.getName());
	  	if (!file2.exists()) {
	  		if (file2.mkdir()) {
	  			System.out.println("Directory is created!");
	  		} else {
	  			System.out.println("Failed to create directory!");
	  		}
	  	}
	  	
		
		for (File file : listOfFiles) {
		  
			File[] nameList = file.listFiles();
			//System.out.println(file.getPath());
			
			
			fwText = new FileWriter("C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/Sentences/"+ folder.getName()+ "/"+file.getName()+ "_ID.txt");
			bwText = new BufferedWriter(fwText);
			
			fwSentText = new FileWriter("C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/Sentences/"+ folder.getName()+ "/"+file.getName()+ "_Sent.txt");;
			bwSentText = new BufferedWriter(fwSentText);
			
			for(File subFile:nameList)
			{
				//System.out.println(subFile.getName());
				
				//String text = readText(subFile.getAbsolutePath());
				
				List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(new FileReader(subFile.getAbsolutePath())));
				
				//System.out.println(text);
				
				for (List<HasWord> sentence : sentences)
				 {
					
						String oneSentece = "";
						//System.out.println(oneSentece);
						for(int i=0; i<sentence.size(); i++)
						{
							oneSentece += sentence.get(i).toString() + " ";
						}
					
						
				      
				      ArrayList<TaggedWord> tSentence = tagger.tagSentence(sentence);
				     
				     
				      ArrayList<Integer> idList = new ArrayList<Integer>();
				      for(int j=0; j<tSentence.size(); j++)
				      {
				    	  	String word = tSentence.get(j).value();
				    	  	
				    	  	//sentence += word + "";
				    	  	String token = word.toLowerCase();
				    	  	token = m_Stemmer.stemString(token);
				    	  	
				    	  	//Matcher m = p.matcher(token); // only save these strings only contains characters
				    	  //	if( !m.find()  && token.length()>3 && token.length()<25 )
				    	  	if(token.length()>2 )
					    	{
				    			  if (!m_EnStopwords.isStopword(token)) 
				    			  {
				    				
									  if (word2IdHash.get(token)==null)
									  {
										  idList.add(id);
										 // bwText.write(String.valueOf(id)+ " ");
										
										  word2IdHash.put(token, id);
										  
										  allWordsArr.add(token);
										   
										   id++;
									    } else
									    {
									    	int wid=(Integer)word2IdHash.get(token);
									    	if(!idList.contains(wid))
									    		idList.add(wid);
									    	//bwText.write(String.valueOf(wid)+ " ");
									     }
									    	
									 }
									 
						    	}
				    	  }
				      if(idList.size()>=3)
				      {
				    	  Collections.sort(idList);
				      //	
				    	  bwText.write(valueFromList(idList));
				    	  bwText.newLine();
				     
				    	  bwSentText.write(oneSentece);
				    	  bwSentText.newLine();
				      }
				    }
				
			}
			
			bwText.close();
			fwText.close();
			
			bwSentText.close();;
			fwSentText.close();
		}
		
		FileWriter fwWI = new FileWriter("C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/Sentences/" + folder.getName()+ "/word.txt");
		BufferedWriter bwWI = new BufferedWriter(fwWI);
		
		for(int i=0; i<allWordsArr.size(); i++)
		{
			bwWI.write(allWordsArr.get(i));
			bwWI.newLine();
		}
		bwWI.close();
		fwWI.close();
		
	}
	
	public void readAllDataset(String path) throws Exception
	{
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
	
		
		
		for (File file : listOfFiles) {
			readFile(file.getAbsolutePath());
		}
	}
	
	public void AllDataset(String path) throws Exception
	{
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
	
		
		
		for (File file : listOfFiles) {
			writeSentences(file.getAbsolutePath());
		}
	}
	
	public String valueFromList(ArrayList<Integer> list)
	{
		String res = "";
		for(int i=0; i<list.size(); i++)
		{
			res += list.get(i) + " ";
		}
		return res;
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			ExtractText et = new ExtractText();
			
			//String sourcePath = "C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008_Update_Summarization_Documents/UpdateSumm08_test_docs_files/";
			//et.readAllDataset(sourcePath);
			//et.readFile("C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008_Update_Summarization_Documents/UpdateSumm08_test_docs_files/D0801A");
			
			//et.writeSentences("C:/Users/jipeng/Desktop/Qiang/updateSum/Process/D0801A");
			String processPath = "C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/Process/";
			et.AllDataset(processPath);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
