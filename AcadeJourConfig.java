package biospider;

import java.lang.String;
import java.net.URLDecoder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.pipeline.*;

public class AcadeJourConfig implements PageProcessor{
	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
	public static String journal = "";
	public int pageMax = 300;
	
	@Override
	public void process(Page page){
    	//String regex = "\\w+(\\.\\w+)*@\\w+((\\.||\\-)\\w+)*";
		String regex = "protection#\\w+";
		String fieldString = null;

		for(int x=1; x < (pageMax+1) ; x++)
		{
			page.addTargetRequests(page.getHtml().links().regex("http://www.academicjournals.org/articles/j_articles/"+ journal +"/page:" + x).all());
		}
		//page.addTargetRequests(page.getHtml().links().regex("http://www.academicjournals.org/articles/j_articles/AJAR/page:\\w+").all());
		page.addTargetRequests(page.getHtml().links().regex("http://www.academicjournals.org/journal/" + journal + "/article-abstract/\\w+").all());
    	page.addTargetRequests(page.getHtml().links().regex("http://www.academicjournals.org/journal/" + journal + "/article-authors/\\w+").all());
    	fieldString = page.getHtml().regex(regex).toString();
    	//System.out.println(fieldString);
    	if(fieldString != null) {
    		page.putField("authormail", intArr2Name(arrayXor(string2Array(fieldString.replaceFirst("protection#","")))));
    	}
    	

	}
	@Override
	public Site getSite() {
        return site;
	}

	public String[] string2Array(String str) {
		int m=str.length()/2;
		if(m*2<str.length()){
		m++;
		}
		
		String[] arr = new String[m];

		for(int i = 0; i < m; i++){
			arr[i] = str.substring(2*i, 2*i+2);
			//System.out.println(arr[i]);
			//System.out.println("parseInt:" + Integer.parseInt(arr[i], 16));
			}
		return arr;
	}
	
	public int[] arrayXor(String[] array) {
		// array[0] is mask, so length reduce one
		int length = array.length - 1;
		int[] resultArr = new int[length];
		for(int i = 0; i < length; i++) {
			resultArr[i] = (Integer.parseInt(array[0], 16) ^ Integer.parseInt(array[i + 1], 16)) ;
		}
		
		return resultArr;
	}
	
	public String intArr2Name(int[] intArr) {
		String authorName = "";
		
		for(int i=0; i < intArr.length; i++) {
			try {
				String sName = URLDecoder.decode("%" + String.format("%02x", intArr[i]), "UTF-8");
				authorName = authorName + sName;
			} catch (IOException e1) {

			}
		}

		return authorName;
		
	}

	   /** 

     * 以行为单位读取文件，常用于读面向行的格式化文件 

     */  

    public static void readFileByLines(String fileName) {  
        File file = new File(fileName);  
        BufferedReader reader = null;  
        try {  
            System.out.println("Read Config File：");  
            reader = new BufferedReader(new FileReader(file));  
            String tempString = null;  
            int line = 1;  
            // 一次读入一行，直到读入null为文件结束  
            while ((tempString = reader.readLine()) != null) {  
                // 显示行号  
                
                if (line == 1)
                {
                	System.out.println("line " + line + ": " + tempString); 
                	journal = tempString;
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

    public static void deleteXthLine(int x) throws IOException {
    	int lineDel=x;
    	BufferedReader br=new BufferedReader(new FileReader(System.getProperty("user.dir") + "/name.txt"));
    	StringBuffer sb=new StringBuffer(4096);
    	String temp=null;
    	int line=0;
    	while((temp=br.readLine())!=null){
    	  line++;
    	  if(line==lineDel) continue;
    	  sb.append(temp).append( "\r\n");
    	}
    	br.close();
    	BufferedWriter bw=new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/name.txt"));
    	bw.write(sb.toString());
    	bw.close();
    }
    
    
	
    public static void main(String[] args) throws IOException {
    	/*create spider task*/
    	String fileName = System.getProperty("user.dir") + "/name.txt" ;
    	readFileByLines(fileName);
    	deleteXthLine(1);
    	Spider.create(new AcadeJourConfig()).addUrl("http://www.academicjournals.org/articles/j_articles/" + journal).addPipeline(new FilePipeline(System.getProperty("user.dir"), 2, 0)).addPipeline(new ConsolePipeline()).thread(5).run();
    }	
	
}
