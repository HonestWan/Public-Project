package com.demo.jsoup.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.demo.model.City;




/**
 * 获取全国地区数据
 * @author pactera
 *
 */
public class JsoupTest5 {
	//爬取数据的地址
	private static String url="http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2016/";
	//存放地区数据
	private static Map<Integer,String> saveMap = new HashMap<>();
	//等待爬取的城市
	private static volatile Queue<City> cities = new LinkedBlockingQueue<City>();
	//记录存放所有地区个数
	private static int allCount=0;
	//级别
	private static int level = 1;
	//开始时间
	private static long startTime = 0;
	//存放数据的文件
	private static File file = new File("E:/hunan.txt");
	//获取文件位置
	private static PrintWriter pw;
	//写入文件
	private static BufferedWriter bw;
	
	static {
		saveMap.put(1, "provincetr");//省
		saveMap.put(2, "citytr");//市
		saveMap.put(3, "countytr");//区/县
		saveMap.put(4, "towntr");//街道/镇/乡
		saveMap.put(5, "villagetr");//社区/村
	}
	
	public static void main(String[] args) {
		startTime = System.currentTimeMillis();
		//初始化文件
		initFile();
		try {
			Document document = Jsoup.connect(url)
					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
					.get();
			Elements trs = document.select("tr." + saveMap.get(level));
			here://跳出多重循环
			for (Element tr : trs) {
				Elements tds = tr.select("td");
				for (Element td : tds) {
					//找到湖南省
					if("湖南省".equals(td.select("a[href]").text())) {
						//获取下一级数据
						parseLevelTwo(td.select("a").attr("href"),level+1);
						//获取城市下一级数据
						int count = cities.size();
						for(int i = 0;i < count;i++) {
							new JsoupTest5().new MyThread(cities.poll().getHref(), level+2).start();
						}
						break here;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取所有城市
	 */
	public static void parseLevelTwo(String nextUrl,int level) {
		try {
			Thread.sleep(500);//睡眠一下，否则可能出现各种错误状态码
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			Document document = Jsoup.connect(url+nextUrl)
					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
					.get();
			Elements trs = document.select("tr." + saveMap.get(level));
			for (Element tr : trs) {
				String code = tr.select("td").first().select("a").text();//统计用区划代码
				String name = tr.select("td").last().select("a").text();//名称
				String href = tr.select("td").last().select("a").attr("href");//地址
				City city = new City(code, name, href);
				cities.add(city);
				allCount++;//总条数+1
				//bw.write("级别:"+level+",名称:"+title+",代码:"+code+",地址:"+href+"\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取下一级所有的数据
	 * @param parentElement
	 * @param level
	 */
	public static void parseNextLevel(String nextUrl,int level) {
		try {
			Thread.sleep(500);//睡眠一下，否则可能出现各种错误状态码
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			if(allCount > 1000 && allCount % 1000 == 0){
				System.out.println("已爬取"+allCount+"条数据");
			}
			Document document = Jsoup.connect(url+nextUrl)
					.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0")
					.get();
			Elements trs = document.select("tr." + saveMap.get(level));
			for (Element tr : trs) {
				allCount++;//总条数+1
				String code = "";//统计用区划代码
				String title = "";//名称
				String href = "";//地址
				//村级以上输出地址
				if(level < 5){
					code = tr.select("td").first().select("a").text();
					title = tr.select("td").last().select("a").text();
					href = tr.select("td").last().select("a").attr("href");
					bw.write("级别:"+level+",名称:"+title+",代码:"+code+",地址:"+href+"\r\n");
				}else{
					code = tr.select("td").first().text();
					title = tr.select("td").last().text();
					bw.write("级别:"+level+",名称:"+title+",代码:"+code+"\r\n");
				}
				if(href.length() > 1) {
					if(level == 3) {
						String prefix = href.substring(3, 5);
						parseNextLevel(prefix+"/"+href, level+1);
					}else if(level == 4){
						String prefix1 = href.substring(3, 5);
						String prefix2 = href.substring(5,7);
						parseNextLevel(prefix1+"/"+prefix2+"/"+href, level+1);
					}else {
						if(cities.size() == 0) {
							//关闭流
							closeStream();
							//输出结果
							long endTime = System.currentTimeMillis();
							System.out.println("总共获取:"+allCount+"条数据");
							System.out.println("耗时:"+(endTime-startTime)/1000+"s");
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化文件
	 */
	public static void initFile(){
		try {
			pw = new PrintWriter(file);
			bw = new BufferedWriter(pw);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭IO流
	 */
	public static void closeStream(){
		try {
			bw.flush();
			bw.close();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public class MyThread extends Thread{
		String nextUrl;
		int lev;
		
		public MyThread(String nextUrl, int lev) {
			this.nextUrl = nextUrl;
			this.lev = lev;
		}
		
		@Override
		public void run() {
			parseNextLevel(nextUrl,lev);
		}
	}
	
}
