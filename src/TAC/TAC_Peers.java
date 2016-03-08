package TAC;




import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

// extract human summarization from DUC2002 which only require its length equal 200
public class TAC_Peers {

	
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
	
	public static boolean isNum(String str)
	{		
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");	
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
			
			String text = readTextFromFile(readFile.getPath());
			
			String[] part = dirList[i].split("[.]");
			
			String newName = dirList[i]+".spl";
			
			if(!isNum(part[4]))
			{
				
				File writeFile = new File(outputDir+"\\"+part[0]+"\\"+"models\\");		
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
			}else
			{
				File writeFile = new File(outputDir+"\\"+part[0]+"\\"+"peers\\");		
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
			}
			
			
			//System.out.println(text);
			
			//String []twoDirList = twoDir.list();
			
			//System.out.println(dirList[i]);
			
			/*for(int j=0; j<twoDirList.length; j++)
			{
				if(twoDirList[j].equals("200"))
				{
					//System.out.println(twoDirList[j]);
					File readFile = new File(inputDir + "\\" + dirList[i]+ "\\"+twoDirList[j]);
					
					
				
					
					String newName = dirList[i].substring(0,dirList[i].length()-1)+"_model_" + flag+"_.spl";
					
					if(flag == 1)
						flag = 2;
					else
						flag = 1;
					File writeFile = new File(outputDir+"\\"+dirList[i].substring(0,dirList[i].length()-1)+"\\"+"models\\");		
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
				}
			}*/
		}
	}
	
	
	public static void main(String[] args) {
		
		
		try
		{
			String dirName = "D:/myperl/DUC2004/eval/models/2";
			//String dirName = "D:/myperl/DUC2004/eval/peers/2";
			
			
			TAC_Peers tac = new TAC_Peers();
			
			tac.mainFun(dirName, "D:/myperl/ROUGE-1.5.5/RELEASE-1.5.5/DUC2004");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
	
	
	
}
}

