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

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import kex.stopwords.Stopwords;
import kex.stopwords.StopwordsEnglish;

public class ExtractPrag {

	int id = 0;
	
	MaxentTagger tagger = new MaxentTagger("C:/Users/jipeng/Desktop/TopicModel/stanford-postagger-2014-01-04/models/english-left3words-distsim.tagger");
	private Stopwords m_EnStopwords = new StopwordsEnglish();
	
	private HashMap<String, Integer> word2IdHash = new HashMap<String, Integer>(); //<word, word's id>
	
	ArrayList<String> allWordsArr = new ArrayList<String>();
	
	
	public void extractWords(String wordPath)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(wordPath));
			
			String line = "";
			int id = 0;
			while((line=br.readLine())!=null)
			{
				word2IdHash.put(line,id);
				id++;
				//System.out.println(line);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
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
	
	public void writeDocumentForA(String filename, String path) throws Exception
	{
		//System.out.println("A: " + path);
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
	      
	     
	  	FileWriter fwText;
		BufferedWriter bwText;
		
		fwText = new FileWriter("C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/Parag/"+ filename+"/"  +"A.txt");
		bwText = new BufferedWriter(fwText);
		
	  	for ( int i=0; i<listOfFiles.length; i++) {
	  		//String name = listOfFiles[i].getName();
			
			
			String text = readText(listOfFiles[i].getAbsolutePath());
			
			FileWriter fwWI = new FileWriter("C:/pun.txt");
			BufferedWriter bwWI = new BufferedWriter(fwWI);
			
			bwWI.write(text);
			
			bwWI.close();
			fwWI.close();
			
			
			List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(new FileReader("C:/pun.txt")));
			
			//System.out.println(text);
			ArrayList<Integer> textList = new ArrayList<Integer>();
			
			for (List<HasWord> sentence : sentences)
			 {
			      ArrayList<TaggedWord> tSentence = tagger.tagSentence(sentence);
			     
			      for(int j=0; j<tSentence.size(); j++)
			      {
			    	  	String word = tSentence.get(j).value();
			    	  	
			    	  	String token = word.toLowerCase();
			    	  
			    	  	if(token.length()>2 )
				    	{
			    			  if (!m_EnStopwords.isStopword(token)) 
			    			  {
			    				
								  if (word2IdHash.get(token)==null)
								  {
									  textList.add(id);
									 // bwText.write(String.valueOf(id)+ " ");
									
									  word2IdHash.put(token, id);
									  
									  allWordsArr.add(token);
									   
									   id++;
								    } else
								    {
								    	int wid=(Integer)word2IdHash.get(token);
								    	if(!textList.contains(wid))
								    		textList.add(wid);
								    	//bwText.write(String.valueOf(wid)+ " ");
								     }
								    	
								 }
								 
					    	}
			    	  }
			    }
			Collections.sort(textList);
		     
			String text2 = valueFromList(textList);
		     bwText.write(text2);
		     //System.out.println(text2);
		     bwText.newLine();
		     textList.clear();
		     
	  	}
	  	bwText.close();
	    fwText.close();
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
	
	public void writeParaForB(String filename, String path) throws Exception
	{
		System.out.println("B: " + path);
		
		String foldName = "C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/Parag/"+ filename;
		
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
	      
	      File file2 = new File(foldName+"/B");
	  	if (!file2.exists()) {
	  		if (file2.mkdir()) {
	  			System.out.println("Directory is created!");
	  		} else {
	  			System.out.println("Failed to create directory!");
	  		}
	  	}
	    
	  	FileWriter fwText;
		BufferedWriter bwText;
		String oneText =  "";
		
	  	for ( int i=0; i<listOfFiles.length; i++) {
	  		String name = listOfFiles[i].getName();
			fwText = new FileWriter(foldName+"/" + "B/"+ name +".txt");
			bwText = new BufferedWriter(fwText);
			
			BufferedReader br1 = new BufferedReader(new FileReader(listOfFiles[i].getAbsolutePath()));
			String para = "";
			String line = "";
		
			boolean isPara = false;
		
			
			
			boolean isHead = false;
			ArrayList<Integer> paraArr = new ArrayList<Integer>();
			
			while ((line = br1.readLine()) != null) 
			{
				if(line.equals("<P>") || line.equals("<HEADLINE>"))
				{
					isPara = true;
					
					if(line.equals("<HEADLINE>"))
						isHead = true;
					continue;
				}
				
				if(line.equals("</P>") || line.equals("</HEADLINE>"))
				{
					isPara = false;
					
					if(paraArr.size()>0)
					{
						Collections.sort(paraArr);
					     
						String para2 = valueFromList(paraArr);
					     bwText.write(para2);
					     //System.out.println(para);
					     bwText.newLine();
					     paraArr.clear();
					}
				     
				     if(isHead)
							oneText += para + ". ";
				     else
				    	 oneText += para;
				     
				     isHead = false;
				     para = "";
				    
					continue;
				}
				
				if(isPara)
				{
					para +=  line + " ";
					String tagged = tagger.tagString(line);
					
					String words[] = tagged.split("[_ ]");
					
					for(int j=0; j<words.length; j=j+2)
					{
						String token = words[j].toLowerCase();
			    	  	//Matcher m = p.matcher(token); // only save these strings only contains characters
			    	  //	if( !m.find()  && token.length()>3 && token.length()<25 )
			    	  	if(token.length()>2 )
				    	{
			    			  if (!m_EnStopwords.isStopword(token)) 
			    			  {
			    				
								  if (word2IdHash.get(token)==null)
								  {
									  paraArr.add(id);
									 // bwText.write(String.valueOf(id)+ " ");
									
									  word2IdHash.put(token, id);
									  
									  allWordsArr.add(token);
									   
									   id++;
								    } else
								    {
								    	int wid=(Integer)word2IdHash.get(token);
								    	if(!paraArr.contains(wid))
								    		paraArr.add(wid);
								    	//bwText.write(String.valueOf(wid)+ " ");
								     }
								    	
								 }
								 
					    	}
					}
				}
				
				
			}
			
			//if(oneText.length()<100)
				//System.out.println(name + " " + oneText);
			
			bwText.close();
			fwText.close();
	  	}
	  	printSentB(foldName, oneText);
	  	
	}
	
	
	public void printSentB(String path, String text) throws Exception
	{
		FileWriter fwWI = new FileWriter("C:/pun.txt");
		BufferedWriter bwWI = new BufferedWriter(fwWI);
		
		bwWI.write(text);
		
		bwWI.close();
		fwWI.close();
		
		
		
		
		//System.out.println(text);
		
		FileWriter fwText;
		BufferedWriter bwText;
		FileWriter fwSentText;
		BufferedWriter bwSentText;
		
		
		fwText = new FileWriter(path+ "/B_ID.txt");
		bwText = new BufferedWriter(fwText);
		
		fwSentText = new FileWriter(path+ "/B_Sent.txt");;
		bwSentText = new BufferedWriter(fwSentText);
		
		
			
		List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(new FileReader("C:/pun.txt")));
			
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
			    	  	
			    String token = word.toLowerCase();
			    	  	
			    
				if (word2IdHash.get(token)!=null)
				{
					int wid=(Integer)word2IdHash.get(token);
					if(!idList.contains(wid))
						idList.add(wid);
								    
				}
			}
								 
					  
			if(idList.size()>=1)
			{
			   Collections.sort(idList);
			   bwText.write(valueFromList(idList));
			   bwText.newLine();
			     
			   bwSentText.write(oneSentece);
			   bwSentText.newLine();
			      
			 }
			
		}
		
		bwText.close();
		fwText.close();
		
		bwSentText.close();;
		fwSentText.close();
		
	}
	
	public void writeContents(String singleCollectionPath) throws Exception
	{
		File folder = new File(singleCollectionPath);
		File[] listOfFiles = folder.listFiles();
	
		File file2 = new File("C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/Parag/"+ folder.getName());
	  	if (!file2.exists()) {
	  		if (file2.mkdir()) {
	  			System.out.println("Directory is created!");
	  		} else {
	  			System.out.println("Failed to create directory!");
	  		}
	  	}
	  	
		writeDocumentForA(folder.getName(), listOfFiles[0].getAbsolutePath());
		writeParaForB(folder.getName(), listOfFiles[1].getAbsolutePath());
		
		id = 0;
		
		FileWriter fwWI = new FileWriter("C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/Parag/" + folder.getName()+ "/word.txt");
		BufferedWriter bwWI = new BufferedWriter(fwWI);
		
		for(int i=0; i<allWordsArr.size(); i++)
		{
			bwWI.write(allWordsArr.get(i));
			bwWI.newLine();
		}
		bwWI.close();
		fwWI.close();
		
		word2IdHash.clear();
		allWordsArr.clear();
	
	}
	
	public void allDataset(String path) throws Exception
	{
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
	
		
		
		for (File file : listOfFiles) {
			writeContents(file.getAbsolutePath());
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try{
			String tac2008Path = "C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008_Update_Summarization_Documents/UpdateSumm08_test_docs_files";
			ExtractPrag ep = new ExtractPrag();
			
			//String wordPath = "";
			//ep.extractWords(wordPath)
			ep.allDataset(tac2008Path);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
