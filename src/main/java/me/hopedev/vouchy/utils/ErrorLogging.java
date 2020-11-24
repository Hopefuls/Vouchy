package me.hopedev.vouchy.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ErrorLogging {


    public static String createHasteLog(Exception e, String causingCommand, String serverID, String userID) {

        // Parse stacktrace

        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("Vouchy Error Log\n");
        sb.append("==================================================\n\n");
        sb.append("Caused at timestamp = "+System.currentTimeMillis()+"\n");
        sb.append("Caused in Server ID = "+serverID+"\n");
        sb.append("Caused by User ID = "+userID+"\n\n");
        sb.append("Caused by Command = "+causingCommand+"\n\n");
        sb.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
        sb.append("Exception Message:\n"+e.getMessage()+"\n\n");
        sb.append("StackTrace______\n");

        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("in Class "+element.getClassName()+" [File: "+element.getFileName()+"]\n");
            sb.append("at Line Number "+element.getLineNumber()+" caused by method "+element.getMethodName()+"\n\n");
        }


            HttpURLConnection connection = null;

            try {
                //Create connection
                URL url = new URL("https://paste.hopefuls.de/documents");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "text/plain");
                connection.setRequestProperty("User-Agent", "ErrorReporter Vouchy");
                connection.setRequestProperty("Content-Length",
                        Integer.toString(sb.toString().length()));

                connection.setUseCaches(false);
                connection.setDoOutput(true);
                connection.setDoInput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream (
                        connection.getOutputStream());
                wr.writeBytes(sb.toString());
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();

                // Parse paste.hopefuls.de key

                JSONObject object = new JSONObject(response.toString());

                return object.getString("key");


            } catch (Exception error) {
                error.printStackTrace();
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }


}
