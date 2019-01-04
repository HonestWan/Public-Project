package com.demo.jsoup.test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * 多线程爬取邮箱
 * @author pactera
 *
 */
public class JsoupTest4 {
	/**
	 * Queue:先入先出（FIFO）的数据结构
	 * LinkedBlockingQueue:一个由链接节点支持的可选有界队列
	 */
	//等待爬取的url
	private static volatile Queue<String> queue = new LinkedBlockingQueue<String>();
	//爬取过的url
	private static volatile Set<String> allOverUrl=new HashSet<>();
	//总线程5条
	private static int MAX_THREAD=5;
	//开始时间
	private static long startTime=0;
	//记录获取的邮箱个数
	private static int emailCount=0;
	//爬取帖子的页数（10页）
	private static AtomicInteger pageCount=new AtomicInteger(10);
	
	public static void main(String[] args) {
		for(int i =1;i <= pageCount.get();i++) {
			String url = "https://tieba.baidu.com/p/3349997454?pn="+i;
			//将获取的url放入等待队列中
			queue.add(url);
		}
		//记录开始时间
		startTime = System.currentTimeMillis();
		for(int i = 0;i < MAX_THREAD;i++) {
			new JsoupTest4().new MyThread().start();
		}
	}
	
	/**
	 * 网页数据爬取
	 */
	public static void work() {
		String url = queue.poll();
		//检测线程是否执行
		System.out.println("当前执行："+Thread.currentThread().getName()+" 爬取线程处理爬取："+url);
		try {
			Document document = Jsoup.connect(url)
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
				//如果含有邮箱，获取到的邮箱个数emailCount+1,并输出
				if(matcher.find()) {
					emailCount++;
					//输出邮箱
					//System.out.println(matcher.group());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//将当前url归列到alloverurl中
		allOverUrl.add(url);
		System.out.println(url+"网页爬取完成，已爬取数量："+allOverUrl.size());
	
		if(allOverUrl.size() == pageCount.get()) {
			long endTime = System.currentTimeMillis();
			System.out.println("爬取结束,耗时:"+(endTime - startTime)/1000+"s");
			System.out.println("获取了"+emailCount+"个邮箱");
		}
	}
	
	/**
	 * 线程分配任务
	 * @author pactera
	 *
	 */
	public class MyThread extends Thread{
		@Override
		public void run() {
			do{
				work();
			}while(queue.size() > 0);
		}
	}
}
