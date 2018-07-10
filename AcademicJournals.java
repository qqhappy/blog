package spider;

import java.lang.String;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.pipeline.*;


public class AcademicJournals implements PageProcessor{

	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
	@Override
	public void process(Page page){
    	//String regex = "\\w+(\\.\\w+)*@\\w+((\\.||\\-)\\w+)*";
		String regex = "protection#\\w+";
		String fieldString = null;
				
    	//page.addTargetRequests(page.getHtml().links().regex("http://www.academicjournals.org/journal/AJAR/article-authors/6FF1F0456279").all());
		page.addTargetRequests(page.getHtml().links().regex("http://www.academicjournals.org/articles/j_articles/AJAR/\\w+").all());
		page.addTargetRequests(page.getHtml().links().regex("http://www.academicjournals.org/journal/AJAR/article-abstract/\\w+").all());
    	page.addTargetRequests(page.getHtml().links().regex("http://www.academicjournals.org/journal/AJAR/article-authors/\\w+").all());
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
	
	
    public static void main(String[] args) {
    	/*create spider task*/
    	//Spider.create(new AcademicJournals()).addUrl("http://www.academicjournals.org/journal/AJAR/article-authors/6FF1F0456279").addPipeline(new FilePipeline("C:\\webmagic\\", 1, 2)).addPipeline(new ConsolePipeline()).thread(4).run();
    	Spider.create(new AcademicJournals()).addUrl("http://www.academicjournals.org/articles/j_articles/AJAR").addPipeline(new FilePipeline(System.getProperty("user.dir"), 1, 2)).addPipeline(new ConsolePipeline()).thread(4).run();
    }	
	
}
