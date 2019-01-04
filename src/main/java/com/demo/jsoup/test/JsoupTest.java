package com.demo.jsoup.test;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class JsoupTest {
	public static void main(String[] args) {
		 try {
			 //从URL加载文档，使用Jsoup.connect()方法从URL加载HTML
//			 Document document = Jsoup.connect("http://www.baidu.com").get();
//			 System.out.println(document.title());
			 
			 //使用Jsoup.parse()方法从文件加载HTML
//			 Document document = Jsoup.parse( new File( "E:/webapp/demo/index.html" ) , "utf-8" );
//			 System.out.println(document.title());
			 
			 //使用Jsoup.parse()方法从字符串加载HTML
//			 String html = "<html><head><title>First parse</title></head>"
//	                    + "<body><p>Parsed HTML into a doc.</p></body></html>";
//			 Document document = Jsoup.parse(html);
//			 System.out.println(document.title());
			 
			 //获取网页中的所有链接
//			 Document document = Jsoup.connect("http://www.baidu.com").get();
//		     Elements links = document.select("a[href]");  
//		     for (Element link : links) 
//		     {
//		         System.out.println("link : " + link.attr("href"));  
//		         System.out.println("text : " + link.text());  
//		     }
		     
			 //获取网页中显示的所有图像
//		     Document document = Jsoup.connect("http://www.baidu.com").get();
//		     Elements images = document.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
//		     for (Element image : images) 
//		     {
//		         System.out.println("src : " + image.attr("src"));
//		         System.out.println("height : " + image.attr("height"));
//		         System.out.println("width : " + image.attr("width"));
//		         System.out.println("alt : " + image.attr("alt"));
//		     }
			 
			 //消除不信任的HTML(以防止XSS)
			 String dirtyHTML = "<p><a href='http://www.yiibai.com/' onclick='sendCookiesToMe()'>Link</a></p>";
			 String cleanHTML = Jsoup.clean(dirtyHTML, Whitelist.basic());
			 System.out.println(cleanHTML);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
