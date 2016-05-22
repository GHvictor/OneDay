package com.android.oneday.Network;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dell on 5/19/2016.
 */
public class WebTools {
    /**
     * 当前的Context上下文对象
     */
    private Context context;

    /**
     * 构造一个网站访问工具类
     *
     * @param context 记录当前Activity中的Context上下文对象
     */
    public WebTools(Context context) {
        this.context = context;
    }

    /**
     * 根据给定的url地址访问网络，得到响应内容(这里为GET方式访问)
     *
     * @param httpUrl httpAg 指定的url地址
     * @return web服务器响应的内容，为<code>String</code>类型，当访问失败时，返回为null
     */
    public String getWebContent(String httpUrl) {
        //创建一个http请求对象
        URL url = null;

        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();

        try {
            url = new URL(httpUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            //urlConnection.setRequestProperty("apikey", "6a3b195cd2e939d45dcd75c7233c1766");
            //urlConnection.setRequestProperty("apikey", "6a3b195cd2e939d45dcd75c7233c1766");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                InputStream is = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String strRead = null;
                while ((strRead = reader.readLine()) != null) {
                    sbf.append(strRead);
                    sbf.append("\r\n");
                }

                reader.close();
                result = sbf.toString();
                return result;
            } else {
                Toast.makeText(context, "网络访问失败，请检查您机器的联网设备!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
