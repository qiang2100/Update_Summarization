

package Algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import kex.pattern.INSGrow;
import kex.stopwords.Stopwords;
import kex.stopwords.StopwordsEnglish;


// 12/3/2015 by Jipeng Qiang

public class UpdateSumPara {

	String newIDPath = "";
	String oldIDPath = "";
	String newSentPath = "";
	String fileBPath = "";
	String wordPath = "";
	ArrayList<String> sourceSentence = new ArrayList<String>();
	
	HashMap<Integer,String> id2Map = new HashMap<Integer,String>();
	
	HashMap<Integer,Double> id2Weight = new HashMap<Integer,Double>();
	
	
	ArrayList<ArrayList<Integer>> sentIDArr = new ArrayList<ArrayList<Integer>>();
	
	ArrayList<Integer> sentLenArr = new ArrayList<Integer>();
	
	double [][]textsRepres; // representative all sentences of new documents using patterns mined from old and new documents
	
	double sentQueryScore[];
	
	String vectorPath = "C:/Users/jipeng/Desktop/Qiang/Word2Vec/glove.6B.300d.txt";
	
	HashMap<String, float[]> wordMap = new HashMap<String, float[]>();
	
	private Stopwords m_EnStopwords = new StopwordsEnglish();
	
	int numBFile = 0;

	double [][]sentToSent;
	
	
	public UpdateSumPara()
	{
		
	}
	public UpdateSumPara(HashMap<String, float[]> wordMap)
	{
		this.wordMap = wordMap;
	}
	
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
	
	public void extractPath(String filePath)
	{
		File folder = new File(filePath);
		File[] listOfFiles = folder.listFiles();
	
		for (File file : listOfFiles)
		{
			if(file.getName().equals("A.txt"))
				oldIDPath = file.getAbsolutePath();
			
			if(file.getName().contains("B_ID"))
				newIDPath = file.getAbsolutePath();
			
			
			if(file.getName().contains("B_Sent"))
				newSentPath = file.getAbsolutePath();
			
			if(file.getName().contains("word"))
				wordPath = file.getAbsolutePath();
			
			if(file.getName().equals("B"))
				fileBPath = file.getAbsolutePath();
		}
		
		/*System.out.println(oldIDPath);
		System.out.println(newIDPath);
		System.out.println(newSentPath);
		System.out.println(wordPath);
		System.out.println(fileBPath);*/
	}
	
	public void extractWords()
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(wordPath));
			
			String line = "";
			int id = 0;
			while((line=br.readLine())!=null)
			{
				id2Map.put(id, line);
				id++;
				//System.out.println(line);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void extractSourceSentence()
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(newSentPath));
			
			String line = "";
			while((line=br.readLine())!=null)
			{
				sourceSentence.add(line);
				sentLenArr.add(line.split(" ").length);
				//System.out.println(line.split(" ").length);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void extractSentId()
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(newIDPath));
			//System.out.println(newIDPath);
			String line = "";
			int ind = 0;
			while((line=br.readLine())!=null)
			{
				String subId[] = line.split(" ");
				
				ArrayList<Integer> idArr = new ArrayList<Integer>();
				//System.out.println(line);
				for(int i=0; i<subId.length; i++)
				{
					//System.out.println(subId[i]);
					idArr.add(Integer.parseInt(subId[i]));
				}
				
				sentIDArr.add(idArr);
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public double contain(double []text, ArrayList<Integer> wordArr)
	{
		double offering=0;
		
		for(int i=0; i<wordArr.size(); i++)
		{
			offering +=  text[wordArr.get(i)];
		}
		
		return offering;
	}
	
	public void negativeRepres() throws Exception
	{

		ArrayList<ArrayList<Integer>> AIDArr = new ArrayList<ArrayList<Integer>>();
		
		BufferedReader br = new BufferedReader(new FileReader(oldIDPath));
		String line = "";
		while((line=br.readLine())!=null)
		{
			String subId[] = line.split(" ");
			
			ArrayList<Integer> idArr = new ArrayList<Integer>();
			//System.out.println(line);
			for(int i=0; i<subId.length; i++)
			{
				//System.out.println(subId[i]);
				idArr.add(Integer.parseInt(subId[i]));
			}
			
			AIDArr.add(idArr);
		}
		
		for(int i=0; i<AIDArr.size(); i++)
			updateRepre(AIDArr.get(i));
		
	}
	
	
	public HashMap<ArrayList, ArrayList> getPatterns(String path, int sup)
	{
		INSGrow ins = new INSGrow();
		ins.input(path,sup);
		
		ins.search();
		System.out.println(" total Pattern: " + ins.tot);
       
		return ins.getPatSentIndexHash();
	}
	
	public void sentRepresUsingEmergePatterns(int sup) 
	{
		
		//System.out.println(sentIDArr.size());
		//System.out.println(id2Map.size());
		try
		{
			fileRepres(fileBPath, sup);
		
			negativeRepres();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void fileRepres(String path, int sup) throws Exception 
	{
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		
		numBFile = listOfFiles.length;
		textsRepres = new double[numBFile][id2Map.size()];
		
		for (int i=0; i<numBFile; i++) {
			//System.out.println(listOfFiles[i].getAbsolutePath());
			HashMap<ArrayList, ArrayList> patMap = getPatterns(listOfFiles[i].getAbsolutePath(),sup);
			
			Iterator iter = patMap.entrySet().iterator();
			
			double allSup  = 0;
			while (iter.hasNext()) 
			{
				Map.Entry entry = (Map.Entry) iter.next();
				
				ArrayList<Integer> wordId = (ArrayList)entry.getKey();
			
				//System.out.println(wordId.toString());
				int supPatt = ((ArrayList)entry.getValue()).size();
				
				for(int jj=0; jj<wordId.size(); jj++)
				{
					textsRepres[i][wordId.get(jj)] += supPatt;
					allSup += supPatt;
				}
					
			}
			
			for(int j=0; j<textsRepres[i].length; j++)
				textsRepres[i][j] /=  allSup;
			
		}
		
		
	}
	
	public double docDist(double []d1, double []d2, ArrayList<Double> dist)
	{
		double sis = 0.0;
		
		
		
		ArrayList<Integer> indArr = new ArrayList<Integer>();
		
		
		for(int i=0; i<dist.size(); i++)
		{
			indArr.add(i);
		}
		
		for(int i=0; i<dist.size(); i++)
		{
			double cur = dist.get(i);
			
			int curInd = indArr.get(i);
			
			double minV = dist.get(i);
			int minInd = i;
			int ind = -1;
			for(int j=i+1; j<dist.size(); j++)
			{
				if(dist.get(j)<minV)
				{
					minV = dist.get(j);
					minInd = j;
					ind = indArr.get(j);
				}
			}
			if(i!=minInd)
			{
				
				dist.set(i, minV);
				dist.set(minInd, cur);
				
				indArr.set(i, ind);
				indArr.set(minInd, curInd);
			
			}
			//indArr.add(minInd);
		}
		
		for(int i=0; i<dist.size(); i++)
		{
			int index = indArr.get(i);
			
			int doc1Ind = index/d2.length;
			int doc2Ind = index%d2.length;
			
			double wei1 = d1[doc1Ind];
			double wei2 = d2[doc2Ind];
			
			if(wei1<1e-5 || wei2<1e-5)
				continue;
			
			double minWei = 0.0;
			if(wei1>wei2)
				minWei = wei2;
			else
				minWei = wei1;
			
			sis += minWei*dist.get(i);
			d1[doc1Ind] = wei1-minWei;
			d2[doc2Ind] = wei2-minWei;	
		}
		
		return sis;
	}
	
	public  float ComputeCosineSimilarity(float[] vector1, float[] vector2)
    {
        if (vector1.length != vector2.length)
			try {
				throw new Exception("DIFER LENGTH");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


        float denom = (VectorLength(vector1) * VectorLength(vector2));
        if (denom == 0f)
            return 0f;
        else
            return (InnerProduct(vector1, vector2) / denom);

    }
    
    public  float computDist(float[] vector1, float[] vector2)
    {
       
           // return (float)Math.exp((1-ComputeCosineSimilarity(vector1,vector2))/0.3f);
    	return 1-ComputeCosineSimilarity(vector1,vector2);

    }

    public static float VectorLength(float[] vector)
    {
        float sum = 0.0f;
        for (int i = 0; i < vector.length; i++)
            sum = sum + (vector[i] * vector[i]);

        return (float)Math.sqrt(sum);
    }
    
    public  float InnerProduct(float[] vector1, float[] vector2)
    {

        if (vector1.length != vector2.length)
			try {
				throw new Exception("DIFFER LENGTH ARE NOT ALLOWED");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


        float result = 0f;
        for (int i = 0; i < vector1.length; i++)
            result += vector1[i] * vector2[i];

        return result;
    }
	
	public void computQuerySent(String query)
	{
		sentQueryScore = new double[sentIDArr.size()];
		
		String subQuery[] = query.split(" ");
		
		ArrayList<String> queryArr = new ArrayList<String>();
		
		for(int i=0; i<subQuery.length; i++)
		{
			String token = subQuery[i].toLowerCase();
			
			 if (!m_EnStopwords.isStopword(token))
			 {
				 if (wordMap.containsKey(token))
				 	queryArr.add(token);
			 }
		}
		
		if(queryArr.size()<1)
		{
			System.out.println("all the words in the query don't exist in word2vector");
			for(int i=0; i<sentQueryScore.length; i++)
				sentQueryScore[i] = 1;
			return;
		}
		
		double []queryWei = new double[queryArr.size()];
		
		
		
		for(int i=0; i<sentIDArr.size(); i++)
		{
			ArrayList<String> sentWordArr = new ArrayList<String>();
			
			ArrayList<Double> dist = new ArrayList<Double>();
			
			int lengByVec = 0;
			for(int j=0; j<sentIDArr.get(i).size(); j++)
			{
				String word = id2Map.get(sentIDArr.get(i).get(j));
				
				if(wordMap.containsKey(word))
				{
					sentWordArr.add(word);
					
					for(int q=0; q<queryArr.size(); q++)
					{
						dist.add((double)computDist(wordMap.get(word),wordMap.get(queryArr.get(q))));
					}
					
					lengByVec++;
				}
			}
			
			double []sentWei = new double[lengByVec];
			for(int j=0; j<sentWei.length; j++)
				sentWei[j] = 1.0/lengByVec;
			
			for(int q=0; q<queryWei.length; q++)
				queryWei[q] = 1.0/queryWei.length;
			
			double sis = docDist(sentWei,queryWei,dist);
			if(sis>=1)
				sis = 1;
			sentQueryScore[i] = 1- sis;
			//System.out.println(sentQueryScore[i] + sourceSentence.get(i));
		}
	}
	
	public void computWeight()
	{
		id2Weight.clear();
		for(int i=0; i<textsRepres.length; i++)
		{
			for(int j=0; j<textsRepres[i].length; j++)
			{
				if(textsRepres[i][j]>0)
				{
					if(id2Weight.containsKey(j))
						id2Weight.put(j, id2Weight.get(j)+textsRepres[i][j]);
					else
						id2Weight.put(j, textsRepres[i][j]);
				}
			}
		}
	}
	
	public double updateScore(ArrayList<Integer> sentId)
	{
		double score = 0;
		for(int i=0; i<sentId.size(); i++)
		{
			if(id2Weight.containsKey(sentId.get(i)))
				score += id2Weight.get(sentId.get(i));
		}
		
		return score;
	}
	
	public void updateRepre(ArrayList<Integer> negID)
	{
		
			//System.out.println(AIDArr.get(i).toString());
			
			for(int j=0; j<numBFile; j++)
			{
				double offerning = contain(textsRepres[j],negID);
				if(offerning == 0)
					continue;
				
			
				double base = 1-offerning;
				
				double alpha = 2;
				offerning *= (1-1./alpha);
				
				for(int t=0; t<textsRepres[j].length; t++)
				{
					if(textsRepres[j][t]>0)
					{
						if(negID.contains(t))
						{
							textsRepres[j][t] *= 1.0/alpha;
						}else
						{
							textsRepres[j][t] *= (1+ offerning/base);
						}
					}
					
				}
			}
		
		
	}
	
	public void sentToSentSis()
	{
		sentToSent = new double[sentIDArr.size()][sentIDArr.size()];
		
		ArrayList<ArrayList<String>> sentByVect = new ArrayList<ArrayList<String>>();
		
		for(int i=0; i<sentIDArr.size(); i++)
		{
			ArrayList<String> sentWordArr = new ArrayList<String>();
		
			for(int j=0; j<sentIDArr.get(i).size(); j++)
			{
				String word = id2Map.get(sentIDArr.get(i).get(j));
				
				if(wordMap.containsKey(word))
				{
					sentWordArr.add(word);
				}
			}
			sentByVect.add(sentWordArr);
		}
		
		for(int i=0; i<sentByVect.size()-1; i++)
		{
		
			for(int j=i+1; j<sentByVect.size(); j++)
			{		
				double sis = computSentToSent(sentByVect.get(i),sentByVect.get(j));
				if(sis<0)
					sis = 0;
				sentToSent[i][j] = sis;
				sentToSent[j][i] = sis;
			}
		}
	
		
	}
	
	public double computSentToSent(ArrayList<String>sent1, ArrayList<String> sent2)
	{
		//double sis = 0;
		
		if((sent1.size()<1) || sent2.size()<1)
			return 0;
		ArrayList<Double> dist = new ArrayList<Double>();
		
		for(int i=0; i<sent1.size(); i++)
		{
			for(int j=0; j<sent2.size(); j++)
			{
				dist.add((double)computDist(wordMap.get(sent1.get(i)),wordMap.get(sent2.get(j))));
			}
		}
		
		double []sent1Wei = new double[sent1.size()];
		for(int j=0; j<sent1Wei.length; j++)
			sent1Wei[j] = 1.0/sent1Wei.length;
		
		double []sent2Wei = new double[sent2.size()];
		for(int j=0; j<sent2Wei.length; j++)
			sent2Wei[j] = 1.0/sent2Wei.length;
		
		double dist2 = docDist(sent1Wei,sent2Wei,dist);
		return 1-dist2;
	}
	
	public double existSis(int ind,ArrayList<Integer> summaryArr)
	{
		double max = 0;
		for(int i=0; i<summaryArr.size(); i++)
		{
			double sis = sentToSent[ind][summaryArr.get(i)];
			if(sis>max)
				max = sis;
		}
		return max;
	}
	
	public String generateSummary()
	{
		
		ArrayList<Integer> summaryArr = new ArrayList<Integer>();
		
		double maxScore = 0;
		int indMax = -1;
		
		int len = 0;
		
		while(len<2000)
		{
			for(int i=0; i<sentIDArr.size(); i++)
			{
				if(summaryArr.contains(i))
					continue;
				
				double score = sentQueryScore[i] * updateScore(sentIDArr.get(i))*(1-existSis(i,summaryArr))/sentLenArr.get(i);
				//System.out.println(score + " "+ (1-existSis(i,summaryArr)) + " "+sentQueryScore[i]);
				if(score>maxScore)
				{
					maxScore = score;
					indMax = i;
				}
			}
			
			System.out.println("sum: " + indMax + " " + sourceSentence.get(indMax));
			len += sourceSentence.get(indMax).length();
			summaryArr.add(indMax);
			updateRepre(sentIDArr.get(indMax));
			computWeight();
			maxScore = 0;
			indMax = -1;
		}
		
		String summary = "";
		for(int i=0; i<summaryArr.size(); i++)
		{
			summary += sourceSentence.get(summaryArr.get(i))+ " ";
		}
		return summary;
		
	}
	
	public void mainFun(int sup, String filePath, String query)
	{
		
		try
		{
			extractPath(filePath);
			
			extractSourceSentence();
			extractWords();
			extractSentId();
			
			sentRepresUsingEmergePatterns(sup);
			
			computWeight();
			
			readVector();
			
			computQuerySent(query);
			sentToSentSis();
			String sum = generateSummary();
			System.out.println(sum);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	public String callFun(int sup, String filePath, String query)
	{
		
		extractPath(filePath);
		
		extractSourceSentence();
		extractWords();
		extractSentId();
		
		sentRepresUsingEmergePatterns(sup);
		
		computWeight();
		
		computQuerySent(query);
		sentToSentSis();
		String sum = generateSummary();
		return sum;
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		UpdateSumPara use = new UpdateSumPara();
		
		String path = "C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/Parag/D0813B/";
		String query = "Airbus A380";
		use.mainFun(2, path, query);
	}

}
