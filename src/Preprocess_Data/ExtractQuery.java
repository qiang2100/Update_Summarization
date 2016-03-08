package Preprocess_Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class ExtractQuery {

	
	public void printQuery(String path)
	{
		try
		{
			String outputPath = "C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008/query.txt";
			
			BufferedReader br1 = new BufferedReader(new FileReader(path));
		
			String line = "";
			FileWriter fwText = new FileWriter(outputPath);
			BufferedWriter bwText = new BufferedWriter(fwText);
			while ((line = br1.readLine()) != null) 
			{
				if(line.contains("<title>"))
				{
					String sub[] = line.split(">");
					System.out.println(line + " :" + line.substring(9, line.length()-9) + ".");
					
					
					bwText.write(line.substring(9, line.length()-9));
					bwText.newLine();
				}
			
			}
			br1.close();
			
			bwText.close();
			fwText.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ExtractQuery eq = new ExtractQuery();
		
		String path = "C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008_Update_Summarization_Documents/UpdateSumm08_test_topics.xml.txt";
		
		eq.printQuery(path);
	}

}
