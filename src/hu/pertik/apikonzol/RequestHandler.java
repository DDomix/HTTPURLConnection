package hu.pertik.apikonzol;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public final class RequestHandler {
    private RequestHandler(){}
    public static Response get(String url) throws IOException {
        HttpURLConnection connection = setupConnection(url);
        connection.setRequestMethod("GET");
        return getResponse(connection);
    }

    private static HttpURLConnection setupConnection(String url) throws IOException {
        URL urlobj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlobj.openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setRequestProperty("Accept", "application/json");
        return connection;
    }

    public static Response post(String url, String data) throws IOException {
        HttpURLConnection connection = setupConnection(url);
        connection.setRequestMethod("POST");
        addRequestBody(data, connection);
        return getResponse(connection);
    }

    private static void addRequestBody(String data, HttpURLConnection connection) throws IOException {
        connection.setRequestProperty("Content-Type","application/json");
        connection.setDoOutput(true);
        OutputStream outputStream= connection.getOutputStream();
        BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write(data);
        writer.flush();
        writer.close();
        outputStream.close();
    }

    private static Response getResponse(HttpURLConnection connection) throws IOException {
        int responsecode = connection.getResponseCode();
        InputStream is = null;
        if (responsecode >= 400) {
            is = connection.getErrorStream();
        } else {
            is = connection.getInputStream();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = br.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        while (line != null) {
            stringBuilder.append(line).append(System.lineSeparator());
            line = br.readLine();
        }
        br.close();
        is.close();
        String content=stringBuilder.toString().trim();
        return new Response(responsecode,content);
    }
}
