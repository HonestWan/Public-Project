package com.demo.client.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * HttpClient测试3
 * @author pactera
 *
 */
public class HttpClientTest3 {
	
	//要爬取数据的url
	private static String url = "http://data.mofcom.gov.cn/datamofcom/front/jdmonth/query";
	//开始日期
	private static String startDate = "201709";
	//结束日期
	private static String endDate = "201809";
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws UnsupportedEncodingException {
		//1.使用默认的配置的httpclient
        CloseableHttpClient client = HttpClients.createDefault();
        //2.使用POST方法
        HttpPost httpPost = new HttpPost(url);
        //设置参数
        Map<String,Object> map =new HashMap<String,Object>();
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>(); 
        for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String value = String.valueOf(map.get(name));
			nvps.add(new BasicNameValuePair(name, value));
		}
        httpPost.setEntity(new UrlEncodedFormEntity(nvps,HTTP.UTF_8));
        InputStream inputStream = null;
        CloseableHttpResponse response = null;
        //3.执行请求获取响应
        try {
			response = client.execute(httpPost);
			//打印状态码
			System.out.println("状态码:"+response.getStatusLine().getStatusCode());
			//4.获取响应的实体内容，就是我们所要抓取得网页内容
            HttpEntity entity = response.getEntity();
            //5.将其打印到控制台上面
            //使用EntityUtils
            if (entity != null) {
                System.out.println(EntityUtils.toString(entity, "UTF-8").replace("[", "").replace("]", "").replace("},", "}\r\n"));
            }
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
		}
	}
}
