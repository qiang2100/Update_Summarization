package TAC;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


// Giving the result of multi-document summarization, this program produce a .xml file using the test.
public class TAC_spl {
	
	String id_B = "<EVAL ID=\"";
	String E = "\">";
	String id_E = "</EVAL>";
	String model_Root_B = "  <MODEL-ROOT>";
	String model_Root_E = "  </MODEL-ROOT>";
	String peel_Root_B = "  <PEER-ROOT>";
	String peel_Root_E = "  </PEER-ROOT>";
	String input_Format_B = "  <INPUT-FORMAT TYPE=\"SPL\">";
	String input_Format_E = "  </INPUT-FORMAT>";
	String peers_B = "  <PEERS>";
	String peers_E = "  </PEERS>";
	String models_B = "  <MODELS>";
	String models_E = "  </MODELS>";
	String m_B = "    <M ID=\"";
	String m_E = "</M>";
	String p_B = "    <P ID=\"";
	String p_E = "</P>";
	  
	  

	String dataName = "  TAC2008/";
	
	public void mainFun(String inputDir, String outputDir) throws Exception
	{
		if (inputDir==null)
		{
			System.out.println("you have not specify the file name.");
			return ;
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputDir)));  
		writer.write("<ROUGE_EVAL version=\"1.0\">");
		writer.newLine();
		
		File srcFile = new File(inputDir);		
		String []dirList = srcFile.list();
		
		for(int i=0; i<dirList.length; i++)
		{
			File twoDir = new File(inputDir + "\\" + dirList[i]);
			
			String []twoDirList = twoDir.list();
			
			System.out.println(dirList[i]);
			writer.write(id_B+i+E);
			writer.newLine();
			writer.write(model_Root_B);
			writer.newLine();
			writer.write(dataName+ dirList[i]+"/models");
			writer.newLine();
			writer.write(model_Root_E);
			writer.newLine();
			writer.write(peel_Root_B);
			writer.newLine();
			writer.write(dataName+ dirList[i]+"/peers");
			writer.newLine();
			writer.write(peel_Root_E);
			writer.newLine();
			writer.write(input_Format_B);
			writer.newLine();
			writer.write(input_Format_E);
			writer.newLine();
			for(int j=0; j<twoDirList.length; j++)
			{
				//System.out.println("  "+ twoDirList[j]);
				String threePath = inputDir + "\\" + dirList[i]+ "\\"+twoDirList[j];
				File threeDir = new File(threePath);
				
				String []threeDirList = threeDir.list();
				
				if(twoDirList[j].equals("models"))
				{
					writer.write(models_B);
					writer.newLine();
					for(int u=0; u<threeDirList.length; u++)
					{
						String []split = threeDirList[u].split("[.]");
						writer.write(m_B+split[4]+E+threeDirList[u]+m_E);
						writer.newLine();
					}
					writer.write(models_E);
					writer.newLine();
				}else
				{
					writer.write(peers_B);
					writer.newLine();
					for(int u=0; u<threeDirList.length; u++)
					{
						String []split = threeDirList[u].split("[.]");
						writer.write(p_B+split[4]+E+threeDirList[u]+p_E);
						writer.newLine();
					}
					writer.write(peers_E);
					writer.newLine();
				}
				
			}
			writer.write(id_E);
			writer.newLine();
		}
		writer.write("</ROUGE_EVAL>");
		
		writer.close();	
	}
	
	public static void main(String[] args) {
		try
		{
			String dirName = "C:/Users/jipeng/Desktop/Qiang/updateSum/RELEASE-1.5.5/TAC2008/";
			
			TAC_spl tac = new TAC_spl();
			tac.mainFun(dirName, "C:/Users/jipeng/Desktop/Qiang/updateSum/RELEASE-1.5.5/experiment/TAC2008.spl.xml");
		}catch(Exception e)
		{
			e.printStackTrace();
		}

}

}
