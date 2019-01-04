package com.demo.client.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * HttpClient测试2
 * @author Honest
 *
 */
public class HttpClientTest2 {
	
	//获取数据的请求
	private static String url = "http://www.tradesns.com.cn/china_experience/get_data10.php";
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws UnsupportedEncodingException {
		//1.使用默认的配置的httpclient
        CloseableHttpClient client = HttpClients.createDefault();
        //2.使用POST方法
        HttpPost httpPost = new HttpPost(url);
        //设置参数
        Map<String,Object> map =new HashMap<String,Object>();
        map.put("pageSize", 50);
        map.put("pageStart", 0);
        map.put("entrance", 1);
        map.put("deal_company_name", "中粮集团有限公司");
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
			//看请求是否成功，这儿打印的是http状态码
            System.out.println("状态码:"+response.getStatusLine().getStatusCode());
            //4.获取响应的实体内容，就是我们所要抓取得网页内容
            HttpEntity entity = response.getEntity();
            //5.将其打印到控制台上面
            //使用EntityUtils
            if (entity != null) {
                System.out.println(unicodeToString(EntityUtils.toString(entity, "UTF-8")));
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
	
	/**
	 * unicode转中文
	 * @param str
	 * @return
	 */
	public static String unicodeToString(String str) {
	    Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");    
	    Matcher matcher = pattern.matcher(str);
	    char ch;
	    while (matcher.find()) {
	        ch = (char) Integer.parseInt(matcher.group(2), 16);
	        str = str.replace(matcher.group(1), ch + "");    
	    }
	    //处理数据，使得更直观
	    str = str.replace("[", "").replace("]", "").replace("},", "}\r\n");
	    return str;
	}
	
}
