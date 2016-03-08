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

public class UpdateSumEmerge {

	String newIDPath = "";
	String oldIDPath = "";
	String newSentPath = "";
	String wordPath = "";
	ArrayList<String> sourceSentence = new ArrayList<String>();
	
	HashMap<String,Integer> id2Map = new HashMap<String, Integer>();
	
	/*HashMap<Integer,Double> id2Weight = new HashMap<Integer,Double>();*/
	
	
	ArrayList<ArrayList<Integer>> sentIDArr = new ArrayList<ArrayList<Integer>>();
	
	double [][]sentsRepres; // representative all sentences of new documents using patterns mined from old and new documents
	
	double sentQueryScore[];
	
	String vectorPath = "C:/Users/jipeng/Desktop/Qiang/Word2Vec/glove.6B.300d.txt";
	
	HashMap<String, float[]> wordMap = new HashMap<String, float[]>();
	
	private Stopwords m_EnStopwords = new StopwordsEnglish();
	
	public UpdateSumEmerge()
	{
		
	}
	public UpdateSumEmerge(HashMap<String, float[]> wordMap)
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
			if(file.getName().contains("A_ID"))
				oldIDPath = file.getAbsolutePath();
			
			if(file.getName().contains("B_ID"))
				newIDPath = file.getAbsolutePath();
			
			
			if(file.getName().contains("B_Sent"))
				newSentPath = file.getAbsolutePath();
			
			if(file.getName().contains("word"))
				wordPath = file.getAbsolutePath();
		}
		
		/*System.out.println(oldIDPath);
		System.out.println(newIDPath);
		System.out.println(newSentPath);
		System.out.println(wordPath);*/
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
				id2Map.put(line, id);
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
				//System.out.println(line);
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
			System.out.println(newIDPath);
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
	
	public void positiveRepres(HashMap<ArrayList, ArrayList> patSentIndexHash)
	{
		sentsRepres = new double[sentIDArr.size()][id2Map.size()];
		
		Iterator iter = patSentIndexHash.entrySet().iterator();
		
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			
			//ArrayList<Integer> it = (ArrayList)entry.getKey();
			ArrayList<Integer> wordId = (ArrayList)entry.getKey();
			//if(wordId.size()<  2)
				//if(sup<min1)
					//continue;
			
			ArrayList<Integer> value = (ArrayList)entry.getValue();
			
			int supPatt = value.size();
			
			//if(sup<2)
				//continue;
			
			for(int ii=0; ii<supPatt; ii++)
			{
				int sentId = value.get(ii);
				
				for(int jj=0; jj<wordId.size(); jj++)
				{
					sentsRepres[sentId][wordId.get(jj)] += supPatt*wordId.size();
				}
				
			}
		}
	}
		
		public void positiveRepres2(HashMap<ArrayList, ArrayList> patSentIndexHash)
		{
			sentsRepres = new double[sentIDArr.size()][id2Map.size()];
			
			Iterator iter = patSentIndexHash.entrySet().iterator();
			
			while (iter.hasNext()) 
			{
				Map.Entry entry = (Map.Entry) iter.next();
				
				//ArrayList<Integer> it = (ArrayList)entry.getKey();
				ArrayList<Integer> wordId = (ArrayList)entry.getKey();
				//if(wordId.size()<  2)
					//if(sup<min1)
					//	continue;
				int sup = ((ArrayList<Integer>)entry.getValue()).size();
				
				for(int sentInd = 0; sentInd<sentIDArr.size(); sentInd++)
				{
					ArrayList<Integer> comArr = containArr(sentIDArr.get(sentInd),wordId);
					
					
					for(int t=0; t<comArr.size(); t++)
					{
						sentsRepres[sentInd][comArr.get(t)] += sup*comArr.size();
					}
					
				}
			}
		
		}
	
	
	
	public void negativeRepres(HashMap<ArrayList, ArrayList> patSentIndexHash)
	{
		
		Iterator iter = patSentIndexHash.entrySet().iterator();
		
		while (iter.hasNext()) 
		{
			Map.Entry entry = (Map.Entry) iter.next();
			
			//ArrayList<Integer> it = (ArrayList)entry.getKey();
			ArrayList<Integer> wordId = (ArrayList)entry.getKey();
			//if(wordId.size()<  2)
				//if(sup<min1)
				//	continue;
			int sup = ((ArrayList<Integer>)entry.getValue()).size();
			
			for(int sentInd = 0; sentInd<sentIDArr.size(); sentInd++)
			{
				ArrayList<Integer> comArr = containArr(sentIDArr.get(sentInd),wordId);
				
				
				for(int t=0; t<comArr.size(); t++)
				{
					if (sentsRepres[sentInd][comArr.get(t)] < sup)
						sentsRepres[sentInd][comArr.get(t)] = 0;
					else
						sentsRepres[sentInd][comArr.get(t)] -= sup;//*comArr.size();
				}
				
			}
		}
		
	}
	
	
	public HashMap<ArrayList, ArrayList> getPatterns(String path, int sup)
	{
		INSGrow ins = new INSGrow();
		ins.input(path,sup);
		
		ins.search();
		System.out.println(" total Pattern: " + ins.tot);
       
		return ins.getPatSentIndexHash();
	}
	
	public void sentRepresUsingEmergePatterns(int sup_A, int sup_B)
	{
		
		//System.out.println(sentIDArr.size());
		//System.out.println(id2Map.size());
		
		positiveRepres2(getPatterns(newIDPath, sup_B));
		
		//negativeRepres(getPatterns(oldIDPath, sup_A));
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
	
	/* the distance between two words */
	public float wordDist(float w1[], float w2[])
	{
		return 1-wordSisCosine(w1,w2);
	}

	public float wordSisCosine(float w1[], float w2[])
	{
		float dis = 0;
		
		float d1 = 0;
		
		float d2 = 0;
		for(int i=0; i<w1.length; i++)
		{
			//dis += w1[i]*w2[i];//*(w1[i]-w2[i]);
			d1 += w1[i]*w1[i];
			d2 += w2[i]*w2[i];
		}
		
		d1 = (float)Math.sqrt(d1);
		
		d2 = (float)Math.sqrt(d2);
		
		for(int i=0; i<w1.length; i++)
		{
			//dis += w1[i]*w2[i];//*(w1[i]-w2[i]);
			w1[i] /= d1;
			w2[i] /= d2;
		}
		d1 = 0;
		d2 = 0;
		for(int i=0; i<w1.length; i++)
		{
			dis += w1[i]*w2[i];//*(w1[i]-w2[i]);
			d1 += w1[i]*w1[i];
			d2 += w2[i]*w2[i];
		}
		return dis/((float)Math.sqrt(d1)*(float)Math.sqrt(d2));
	}
	
	public void computQuerySent(String query)
	{
		sentQueryScore = new double[sentsRepres.length];
		
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
		
		
		
		for(int i=0; i<sourceSentence.size(); i++)
		{
			String sent = sourceSentence.get(i);
			
			String words[] = sent.split(" ");
			
			ArrayList<String> sentWordArr = new ArrayList<String>();
			
			ArrayList<Double> dist = new ArrayList<Double>();
			
			int lengByVec = 0;
			
			for(int j=0; j<words.length; j++)
			{
				if(id2Map.containsKey(words[j]) && wordMap.containsKey(words[j]))
				{
					sentWordArr.add(words[j]);
					
					for(int q=0; q<queryArr.size(); q++)
					{
						dist.add((double)wordDist(wordMap.get(words[j]),wordMap.get(queryArr.get(q))));
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
			sentQueryScore[i] = 1- sis;
			//System.out.println(sentQueryScore[i] + sourceSentence.get(i));
		}
	}
	
	/*public void computWeight()
	{
		id2Weight.clear();
		for(int i=0; i<sentsRepres.length; i++)
		{
			for(int j=0; j<sentsRepres[i].length; j++)
			{
				if(sentsRepres[i][j]>0)
				{
					if(id2Weight.containsKey(j))
						id2Weight.put(j, id2Weight.get(j)+sentsRepres[i][j]);
					else
						id2Weight.put(j, sentsRepres[i][j]);
				}
			}
		}
	}*/
	
	/*public double updateScore(ArrayList<Integer> sentId)
	{
		double score = 0;
		for(int i=0; i<sentId.size(); i++)
		{
			if(id2Weight.containsKey(sentId.get(i)))
				score += id2Weight.get(sentId.get(i));
		}
		
		return score;
	}*/
	
	public ArrayList<Integer> containArr(ArrayList<Integer> sourArr, ArrayList<Integer> aimArr) 
	{
		ArrayList<Integer> comArr = new ArrayList<Integer>();
		
		for(int i=0; i<aimArr.size(); i++)
		{
			if(sourArr.contains(aimArr.get(i)))
				comArr.add(aimArr.get(i));
		}
		return comArr;
	}
	
	public void updateRepre(ArrayList<Integer> negID)
	{
		for(int sentInd = 0; sentInd<sentIDArr.size(); sentInd++)
		{
			ArrayList<Integer> comArr = containArr(sentIDArr.get(sentInd),negID);
			
			
			for(int t=0; t<comArr.size(); t++)
			{
				int id = comArr.get(t);
				
				if (sentsRepres[sentInd][id] < 3*comArr.size())
					sentsRepres[sentInd][id] = 0;
				else
					sentsRepres[sentInd][id] -= 3*comArr.size();
			}
		}
	}
	
	public int getScore(int ind)
	{
		int score = 0;
		for(int i=0; i< sentsRepres[ind].length; i++)
			score += sentsRepres[ind][i];
		
		return score;
	}
	
	public String generateSummary()
	{
		
		ArrayList<Integer> summaryArr = new ArrayList<Integer>();
		
		double maxScore = -999999;
		int indMax = -1;
		
		int len = 0;
		
		while(len<2000)
		{
			for(int i=0; i<sentIDArr.size(); i++)
			{
				if(summaryArr.contains(i))
					continue;
				
				double score = getScore(i);
				
				if (score<0)
					score /= sentQueryScore[i];
				//double score = sentQueryScore[i] * getScore(i);
				
				if(score>maxScore)
				{
					maxScore = score;
					indMax = i;
				}
				
				//System.out.println(getScore(i));
			}
			
			len += sourceSentence.get(indMax).length();
			summaryArr.add(indMax);
			updateRepre(sentIDArr.get(indMax));
			//computWeight();
			maxScore = -999999;
			indMax = -1;
		}
		
		String summary = "";
		for(int i=0; i<summaryArr.size(); i++)
		{
			summary += sourceSentence.get(summaryArr.get(i))+ " ";
		}
		return summary;
		
	}
	
	public void mainFun(int sup_A, int sup_B, String filePath, String query)
	{
		
		try
		{
			extractPath(filePath);
			
			extractSourceSentence();
			extractWords();
			extractSentId();
			
			sentRepresUsingEmergePatterns(sup_A, sup_B);
			
			//computWeight();
			
			readVector();
			
			computQuerySent(query);
			String sum = generateSummary();
			System.out.println(sum);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	
	public String callFun(int sup_A, int sup_B, String filePath, String query)
	{
		
		extractPath(filePath);
		
		extractSourceSentence();
		extractWords();
		extractSentId();
		
		sentRepresUsingEmergePatterns(sup_A, sup_B);
		
		//computWeight();
		
		computQuerySent(query);
		String sum = generateSummary();
		return sum;
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		UpdateSumEmerge use = new UpdateSumEmerge();
		
		String path = "C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/Sentences/D0801A/";
		String query = "Airbus A380";
		
		int sub_A = 2;
		
		int sub_B = 3;
		use.mainFun(sub_A,sub_B, path, query);
	}

}
