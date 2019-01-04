package com.demo.jsoup.test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 爬取贴吧邮箱
 * @author Honest
 *
 */
public class JsoupTest3 {
	public static void main(String[] args) {
		int index = 1;//页数
		String url = "https://tieba.baidu.com/p/3349997454?pn=";//地址
		long startTime = System.currentTimeMillis();//开始时间
		int i = 0;//记录获取邮箱个数
		try {
			//获取10页的内容
			for(;index <= 10;index++) {
				Document document = Jsoup.connect(url+index)
						//伪装成浏览器进行抓取
						.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
						.get(); 
				Element main = document.getElementById("j_p_postlist");
				Elements content = main.select("div.l_post_bright")
						.select("div.d_post_content_main")
						.select("div.p_content")
						.select("cc")
						.select("div.d_post_content");
				
				//遍历每一帖的内容
				for (Element element : content) {
					String Content = element.text();
					//正则表达式判断邮箱
					String patternStr = "[\\w[.-]]+@[\\w[.-]]+\\.[\\w]+";
					Pattern pattern = Pattern.compile(patternStr);
					Matcher matcher = pattern.matcher(Content);
					//如果含有邮箱，获取到的邮箱个数i+1，并输出该邮箱
					if(matcher.find()) {
						i++;
					}
				}
			}
			long endTime = System.currentTimeMillis();//结束时间
			System.out.println("获取了"+i+"个邮箱");
			System.out.println("耗时:"+(endTime - startTime)/1000+"s");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}