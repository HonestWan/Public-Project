package com.demo.jsoup.test;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * 爬取豆瓣小组租房信息
 * @author pactera
 *
 */
public class JsoupTest2 {
	
	private static Queue<String> saveUrl = new LinkedBlockingQueue<String>();//存放要爬取的url
	
	public static void main(String[] args) {
		int i = 0;//记录获取了多少个需要的租房信息
		String firstUrl = "https://www.douban.com/group/baoanzufang/discussion?start=";//宝安租房小组
		String secondUrl = "https://www.douban.com/group/106955/discussion?start=";//深圳租房小组
		String thirdUrl = "https://www.douban.com/group/nanshanzufang/discussion?start=";//南山租房小组
		saveUrl.add(firstUrl);
		saveUrl.add(secondUrl);
		saveUrl.add(thirdUrl);
		long startTime = System.currentTimeMillis();//开始时间
		try {
			while(saveUrl.size() > 0) {
				int index = 0;//页数
				String getUrl = saveUrl.poll();
				for(;index < 200;index=index+25) {
					Document document = Jsoup.connect(getUrl+index)
							.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
							.get();
					//#content > div > div.article > div:nth-child(2) > table > tbody > tr:nth-child(2) > td.title > a
					Element main = document.getElementById("content");
					Elements url = main.select("div")
							.select("div.article")
							.select("div:nth-child(2)")
							.select("table")
							.select("tr")
							.select("td.title")
							.select("a");
					for (Element information : url) {
						String Information = information.attr("title");//标题
						String Url = information.attr("href");//地址
						//判断字符串中是否含有坪洲
						String patternStr=".*(坪洲|宝体|翻身|灵芝|西丽|茶光).*";
						Pattern pattern = Pattern.compile(patternStr);
						Matcher matcher = pattern.matcher(Information);
						if(matcher.find()) {
							String regex=".*(合租|主卧|次卧|两室|两房|三室|三房).*";
							pattern = Pattern.compile(regex);
							matcher = pattern.matcher(Information);
							if(!matcher.find()) {
								i++;
								System.out.println("标题:"+Information+",地址:"+Url);
							}
						}
					}
				}
			}
			long endTime = System.currentTimeMillis();//结束时间
			System.out.println("获取了"+i+"条需要的租房信息");
			System.out.println("耗时:"+(endTime - startTime)/1000+"s");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
