package com.cl.utils;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;

@Component
public class NotificationUtil {

    private static final String SMS_API_URL = "https://api.example.com/sms/send";
    private static final String API_KEY = "your_api_key";
    private static final String API_SECRET = "your_api_secret";

    public boolean sendSMS(String phone, String content) {
        try {
            String encodedContent = URLEncoder.encode(content, "UTF-8");
            String requestUrl = SMS_API_URL + "?phone=" + phone + "&content=" + encodedContent + "&key=" + API_KEY;

            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                String responseStr = response.toString();
                return responseStr.contains("success") || responseStr.contains("ok");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean sendAppNotification(String userId, String content) {
        try {
            String requestUrl = "https://api.example.com/app/notification";
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            String postData = "user_id=" + userId + "&content=" + URLEncoder.encode(content, "UTF-8") + "&key=" + API_KEY;
            OutputStream os = connection.getOutputStream();
            os.write(postData.getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean sendNotification(String phone, String userId, String content) {
        boolean smsSent = sendSMS(phone, content);
        boolean appSent = sendAppNotification(userId, content);
        return smsSent || appSent;
    }

    public boolean isPhoneValid(String phone) {
        return phone != null && phone.matches("1[3-9]\\d{9}");
    }

    public String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
}
