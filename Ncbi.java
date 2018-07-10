package spider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.pipeline.*;


public class Ncbi implements PageProcessor{
	public static int begin_index, end_index;
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
    //int initial_num = 29886236; 

    @Override
    public void process(Page page) {
    	String regex = "\\w+((\\.|\\-)\\w+)*@\\w+((\\.|\\-)\\w+)*";
    	//page.addTargetRequests(page.getHtml().links().regex("https://www.biorxiv.org/content/early/\\d+/\\d+/\\d+/\\d+").all());
    	//page.addTargetRequests(page.getHtml().links().regex("https://www.ncbi.nlm.nih.gov/pubmed/\\d+").all());
    	//while()
    	for(int i = begin_index; i <= end_index; i++)
    	{
    		page.addTargetRequest("https://www.ncbi.nlm.nih.gov/pubmed/" + i);
    	}
    	//page.addTargetRequest("https://www.ncbi.nlm.nih.gov/pubmed/" + initial_num);
    	page.putField("authormail", page.getHtml().regex(regex).all());

        if (page.getResultItems().get("authormail")==null){
            //skip this page
            page.setSkip(true);
        }
        
/*        if(initial_num < 29890000)
        {
        	initial_num = initial_num+1;
        	page.addTargetRequest("https://www.ncbi.nlm.nih.gov/pubmed/" + initial_num);
        }*/
        
        
    }

    @Override
    public Site getSite() {
        return site;
    }

    /** 

     * ����Ϊ��λ��ȡ�ļ��������ڶ������еĸ�ʽ���ļ� 

     */  

    public static void readFileByLines(String fileName) {  
        File file = new File(fileName);  
        BufferedReader reader = null;  
        try {  
            System.out.println("����Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���У�");  
            reader = new BufferedReader(new FileReader(file));  
            String tempString = null;  
            int line = 1;  
            // һ�ζ���һ�У�ֱ������nullΪ�ļ�����  
            while ((tempString = reader.readLine()) != null) {  
                // ��ʾ�к�  
                System.out.println("line " + line + ": " + tempString); 
                if (line == 1)
                {
                	begin_index = Integer.parseInt(tempString);
                }
                else if (line == 2)
                {
                	end_index = Integer.parseInt(tempString);
                }
                line++;  
            }  
            reader.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (reader != null) {  
                try {  
                    reader.close();  
                } catch (IOException e1) {  

                }  
            }  
        }  
    }      
        
    
    
    public static void main(String[] args) {
    	//https://www.ncbi.nlm.nih.gov/pubmed/29886345
        //Spider.create(new Ncbi()).addUrl("https://www.ncbi.nlm.nih.gov/pubmed/29880925").addPipeline(new FilePipeline("D:\\webmagic\\")).run();
    	String fileName = System.getProperty("user.dir") + "/page_index.txt" ;
    	readFileByLines(fileName);
    	/*create spider task*/
    	Spider.create(new Ncbi()).addUrl("https://www.ncbi.nlm.nih.gov/pubmed/").addPipeline(new FilePipeline(System.getProperty("user.dir"), begin_index, end_index)).addPipeline(new ConsolePipeline()).thread(4).run();
        
    }	
	
}
