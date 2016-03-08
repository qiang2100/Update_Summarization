package TAC;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class TAC_Model {

	
	
	public String readTextFromFile(String fileName)
	{
		StringBuffer txtStr = new StringBuffer();
		//String text="";
		try 
		{
			File txt = new File(fileName);
			InputStreamReader is = new InputStreamReader(new FileInputStream(txt),"UTF-8");
			int c;
			while ((c = is.read()) != -1) 
			{
				txtStr.append((char)c);
			}
			is.close();
			
			//String[] sentences = txtStr.toString().split("[><]");
			
			
			//text = sentences[2].replaceAll("\r|\n", "");; 
			//System.out.println(text);
		
		
		} catch (Exception e) 
		{
			System.err.println("Can't find document.");
		}
		return txtStr.toString();

	}
	
	
	public void mainFun(String inputDir, String outputDir) throws Exception
	{
		if (inputDir==null)
		{
			System.out.println("you have not specify the file name.");
			return ;
		}
		
		File srcFile = new File(inputDir);		
		String []dirList = srcFile.list();
	
		//String writeRoot = "experimentResult\\DUC2002";
		
		
		int flag = 1;
		for(int i=0; i<dirList.length; i++)
		{
			File readFile = new File(inputDir + "\\" + dirList[i]);
			
			
			
			String[] part = dirList[i].split("[.]");
			
			
			
			if(part[0].contains("-B"))
			{
				String text = readTextFromFile(readFile.getPath());
				
				String newName = dirList[i]+".spl";
				
				File writeFile = new File(outputDir+"\\"+part[0].substring(0, part[0].length()-2)+"\\"+"models\\");		
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
				writer.write(text);
				writer.close();
				//System.out.println(newName);
			}
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try
		{
			String modelPath = "C:/Users/jipeng/Desktop/Qiang/updateSum/TAC2008_Update_Summarization_Documents/UpdateSumm08_eval/ROUGE/models";
		
			String outputPath = "C:/Users/jipeng/Desktop/Qiang/updateSum/RELEASE-1.5.5/TAC2008/";
			TAC_Model tac = new TAC_Model();
		
		tac.mainFun(modelPath, outputPath);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
