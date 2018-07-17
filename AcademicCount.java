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


public class AcademicCount implements PageProcessor{
	private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
	
	@Override
	public void process(Page page) {
    	//String regex = "\\w+(\\.\\w+)*@\\w+((\\.||\\-)\\w+)*";
		String regex = "Page\\w+";
		String fieldString = null;
		page.addTargetRequests(page.getHtml().links().regex("http://www.academicjournals.org/journal/\\w+").all());
		page.addTargetRequests(page.getHtml().links().regex("http://www.academicjournals.org/journal/\\w+/articles").all());
		fieldString = page.getHtml().xpath("//div[@class='col-lg-9']/p/tidyText()").toString();
		if(fieldString != null)
		{
			page.putField("page", fieldString);
		}
		
/*    	fieldString = page.getHtml().regex(regex).toString();
    	//System.out.println(fieldString);
    	if(fieldString != null) {
    		page.putField("pageNum", fieldString);
    	}*/
    	

	}
	@Override
	public Site getSite() {
        return site;
	}
	
	
    public static void main(String[] args) {
    	/*create spider task*/
    	Spider.create(new AcademicCount()).addUrl("http://www.academicjournals.org/journal").addPipeline(new FilePipeline(System.getProperty("user.dir"), 1, 118)).addPipeline(new ConsolePipeline()).thread(4).run();
    }	
	
}
