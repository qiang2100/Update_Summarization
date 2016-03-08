package Experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import Algorithm.UpdateSumEmerge;
//import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import Algorithm.UpdateSumPara;


public class PTMExp {

	//MaxentTagger tagger = new MaxentTagger("E:/UMASS/stanford-postagger-2014-01-04/models/wsj-0-18-bidirectional-nodistsim.tagger");
	String inputDir = "C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/Sentences/";
	//String inputDir = "C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/Parag/";
	String outputDir = "C:/Users/jipeng/Desktop/Qiang/updateSum/RELEASE-1.5.5/TAC2008/";
	
	ArrayList<String> query = new ArrayList<String>();
	
	String vectorPath = "C:/Users/jipeng/Desktop/Qiang/Word2Vec/glove.6B.300d.txt";
	
	HashMap<String, float[]> wordMap = new HashMap<String, float[]>();
	
	public void readVector()
	{
		try
		{
			BufferedReader br1 = new BufferedReader(new FileReader(vectorPath));
			String line = "";
			
			float vector = 0;
			while ((line = br1.readLine()) != null) {
			
				String word[] = line.split(" ");
				
				String word1 = word[0];
				float []vec = new float[word.length-1];
				
				for(int i=1; i<word.length; i++)
				{
					vector = Float.parseFloat(word[i]);///(word.length-1);
					
					vec[i-1] = vector;
				}
				wordMap.put(word1, vec);
			}
			br1.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void readQuery(String path)
	{
		try
		{
			BufferedReader br1 = new BufferedReader(new FileReader(path));
			String line = "";
			
			while ((line = br1.readLine()) != null) 
			{
				
				query.add(line);
			}
			br1.close();	
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	public void mainFun(int sup) throws Exception 
	{
		
		if (inputDir==null)
		{
			System.out.println("you have not specify the file name.");
			return ;
		}
		
		File srcFile = new File(inputDir);
		String []dirList = srcFile.list();
		
		for(int i=0; i<dirList.length; i++)
		{
			String path = inputDir + dirList[i];
			
	
			UpdateSumEmerge ts = new UpdateSumEmerge(wordMap);
			//UpdateSumPara ts = new UpdateSumPara(wordMap);
			System.out.println(dirList[i]);
			//ts.clm(path,tagger);
			String sum = ts.callFun(sup,3, path, query.get(i));
			//ts.clear();
			//ts = null;
			String newName = dirList[i]+".M.100.T.PTMSum"+"sup"+sup+".spl";
			
			String writeRoot = outputDir + "/"+dirList[i].substring(0, dirList[i].length()-1)+ "/peers/";
			File writeFile = new File(writeRoot);		
			if(!writeFile.exists())
			{		 
				writeFile.mkdirs();		
			} 
			
			File newFile = new File(writeFile,newName);
			if(!newFile.exists())
			{
				newFile.createNewFile();
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));  
			writer.write(sum);
			writer.close();
			//System.out.println(sum);
			
			//new Thread().sleep(1000);
		}
		
	}
	
	
	public void mainBasedSup()
	{
		try
		{
			int sup = 2;
		
		String queryPath = "C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/query.txt";
		
		readQuery(queryPath);
		
		readVector();
		
		mainFun(sup);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	public static void main(String[] args) {
		
		
		
		
		PTMExp rd = new PTMExp();
		
		try
		{
			//rd.mainFun(0.3,5);
			//rd.repetRe();
			//rd.repetRe_Evolu();
			//rd.repetAll();
			rd.mainBasedSup();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
}
