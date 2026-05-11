package com.example.aircraftwar.application;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    private final String account;
    private final String password;
    private final String requestGoal;
    private final Context context;

    public Login(String account, String password, String requestGoal, Context context){
        this.account = account;
        this.password = password;
        this.requestGoal = requestGoal;
        this.context = context;
    }

    public boolean Enter(){
        final Map<String, String> paramsmap = new HashMap<>();
        paramsmap.put("username", account);
        paramsmap.put("password", password);
        paramsmap.put("requestGoal", requestGoal);
        String LOGIN_URL = "http://10.0.2.2:8080/LoginInfo";
        String result = LoginByGet(LOGIN_URL,paramsmap);

        threadRunToToast(result);
        return (result.equals("登录成功") || result.equals("注册成功"));
    }

    private String LoginByGet(String urlStr, Map<String,String> map) {

        StringBuilder result = new StringBuilder();
        StringBuilder pathString = new StringBuilder(urlStr);
        pathString.append("?");
        pathString.append(getStringFromEntry(map));

        try{
            URL url = new URL(pathString.toString());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            int responseCode = httpURLConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String temp;
                while((temp = reader.readLine()) != null) {
                    result.append(temp);
                }
            }else{
                return "failed";
            }
            httpURLConnection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            threadRunToToast("登录失败，请检查网络！");
        } catch (IOException e) {
            e.printStackTrace();
            threadRunToToast("IO发生异常");
        }
        return result.toString();
    }

    private String LoginByPost(String urlStr, Map<String,String> map) {

        StringBuilder result = new StringBuilder();
        String paramsString = getStringFromEntry(map);

        try{
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(true);
            OutputStream outputStream = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            writer.write(paramsString);
            writer.flush();
            writer.close();
            outputStream.close();
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String temp;
                while((temp = reader.readLine()) != null) {
                    result.append(temp);
                }
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            threadRunToToast("登录失败，请检查网络！");
        } catch (IOException e) {
            e.printStackTrace();
            threadRunToToast("IO发生异常");
        }
        return result.toString();
    }

    private String getStringFromEntry(Map<String, String> map) {

        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (isFirst)
                    isFirst = false;
                else
                    sb.append("&");
                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void threadRunToToast(final String text) {
        runOnUiThread(() -> Toast.makeText(context, text, Toast.LENGTH_LONG).show());
    }
}
