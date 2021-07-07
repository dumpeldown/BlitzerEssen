package de.dumpeldown.blitzer.request;

import org.brotli.dec.BrotliInputStream;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class LocationRequestManager {

    private static HttpURLConnection conn;

    public LocationRequestManager(String address) {
        URL url = null;
        try {
            url = new URL("https://eu1.locationiq.com/v1/search.php" +
                    "?key=" + System.getenv("locationiq_api_key") +
                    "&format=json" +
                    "&q=" + address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        initCon(url);
    }

    private static void initCon(URL url) {
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try {
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            conn.setDoOutput(true);
            conn.setReadTimeout(1000);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        try {
            conn.connect();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public JSONObject makeRequest() {
        int responsecode = 0;
        try {
            responsecode = conn.getResponseCode();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        if (responsecode != 200) {
            System.out.println("Code: " + responsecode + " -> Fehler, kann nicht mit dem Response " +
                    "Code arbeiten.");
            return null;
        }
        InputStream stream;
        InputStreamReader reader;
        BrotliInputStream brotliInputStream;
        BufferedReader br = null;
        try {
            stream = conn.getInputStream();
            brotliInputStream = new BrotliInputStream(stream);
            reader = new InputStreamReader(brotliInputStream);

            if (conn.getContentEncoding().equals("br")) {
                br = new BufferedReader(reader);
            } else {
                br = new BufferedReader(new InputStreamReader(stream));
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        StringBuilder result = new StringBuilder();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        JSONArray jsonArray = new JSONArray(result.toString());
        return jsonArray.getJSONObject(0);
    }

}
