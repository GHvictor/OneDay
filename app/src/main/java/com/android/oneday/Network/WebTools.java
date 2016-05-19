package com.android.oneday.Network;

import android.content.Context;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
     * @param requesturl 指定的url地址
     * @return web服务器响应的内容，为<code>String</code>类型，当访问失败时，返回为null
     */
    public String getWebContent(String requesturl) {
        //创建一个http请求对象
        URL url = null;
        try {
            url = new URL(requesturl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            //设置get方法
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);

            //urlConnection.setRequestProperty();
            if (urlConnection.getResponseCode() == 200) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                int len = 0;
                byte buffer[] = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                is.close();
                os.close();
                String result = new String(os.toByteArray());
                return result;
            } else {
                Toast.makeText(context, "网络访问失败，请检查您机器的联网设备!", Toast.LENGTH_LONG).show();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
