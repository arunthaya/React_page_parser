package com.example.springbootwithreactjs.controller;

import com.example.springbootwithreactjs.model.MyJsoup;
import com.example.springbootwithreactjs.model.MyTika;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.regex.Pattern;

public class MyCrawler extends WebCrawler {

    private Date startTime;
    private Date endTime;
    private static String paragraphs = "";
    private static String schoolName = "";

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|vcf|ico" + "|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {

        endTime = new Date();

        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches()
                && (href.startsWith(MyCrawlController.SEED1));
    }

    @Override
    public void visit(Page page){
        startTime = new Date();
        int docid = page.getWebURL().getDocid();
        boolean aboutUsPage = false;
        URL url = null;
        try{
            url = new URL(page.getWebURL().getURL());
        } catch(MalformedURLException e1){
            e1.printStackTrace();
        }
        aboutUsPage = MyTika.getInstance().validAboutUsPage(url);
        String aboutPageToCompare = page.getWebURL().getURL();
        System.out.println("currently visiting: "+aboutPageToCompare);
        String absoluteAboutPage = MyCrawlController.SEED1;
        if(!aboutPageToCompare.endsWith(".pdf")){
            MyJsoup.getInstance().storeImgAltInfo(aboutPageToCompare);
        }
        if(page.getWebURL().getURL().endsWith("/")){
            aboutPageToCompare = aboutPageToCompare.substring(0, aboutPageToCompare.length() - 1);
        }
        if(absoluteAboutPage.endsWith("/")){
            absoluteAboutPage = absoluteAboutPage.substring(0,absoluteAboutPage.length() - 1);
        }
        absoluteAboutPage += "/about";
        if(MyCrawlController.SEED1.contains("utoronto")) {
            absoluteAboutPage += "-u-of-t";
        }
        if(aboutUsPage && (aboutPageToCompare.equals(absoluteAboutPage))){
            System.out.println("Found a valid about us page");
            paragraphs = MyJsoup.getInstance().getParagraphSelector(page.getWebURL().getURL());
        }
    }

    public static String getParagraphs(){
        return paragraphs;
    }

    public static String getTitle() { return schoolName; }

}


