package de.dumpeldown.blitzer.request;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TwitterRequestManager {
    private static int numberOfLastTweets;
    private static String userID;
    private static final ArrayList<String> detectWords = new ArrayList<>(List.of("blitz"));

    public TwitterRequestManager(String _userID, int _numberOfLastTweets) {
        numberOfLastTweets = _numberOfLastTweets;
        userID = _userID;
    }

    public static void main(String[] args) {
    }


    public ArrayList<URL> getURLs() {
        Request request = initRequest();
        Response response = makeRequest(request);

        JSONObject fullObj = getJSONObject(response);
        ArrayList<String> foundMediaKeys = findMediaKeys(fullObj);
        ArrayList<String> foundURLs = findURLforMediaKeys(fullObj, foundMediaKeys);
        ArrayList<URL> urls = new ArrayList<>();
        for (String s : foundURLs) {
            try {
                urls.add(new URL(s));
            } catch (MalformedURLException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
        return urls;
    }

    private static Request initRequest() {
        Request request = null;
        String bearerToken = System.getenv("twitter_bearer_key");
        try {
            request = new Request.Builder()
                    .url("https://api.twitter.com/2/users/" + userID + "/tweets" +
                            "?exclude=replies,retweets" +
                            "&expansions=attachments.media_keys" +
                            "&media.fields=type,url" +
                            "&tweet.fields=created_at" +
                            "&max_results=" + numberOfLastTweets)
                    .addHeader("Authorization", String.format("Bearer %s", bearerToken))
                    .build();
        } catch (IllegalArgumentException e) {
            System.out.println("Your twitter key is probably not set correctly.");
        }
        return request;
    }

    private static ArrayList<String> findURLforMediaKeys(JSONObject fullObj, ArrayList<String> foundMediaKeys) {
        ArrayList<String> urls = new ArrayList<>();
        for (Object jsonObject : fullObj.getJSONObject("includes").getJSONArray("media")) {
            JSONObject object = (JSONObject) jsonObject;
            if (foundMediaKeys.contains(object.getString("media_key"))) {
                urls.add(object.getString("url"));
            }
        }
        return urls;
    }

    private static ArrayList<String> findMediaKeys(JSONObject fullObj) {
        ArrayList<String> foundMediaKeys = new ArrayList<>();
        JSONArray jsonArray = fullObj.getJSONArray("data");
        System.out.println("Found " + jsonArray.length() + " tweets with media.");
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            if (jsonObject.has("attachments")) {
                String full = jsonObject.getString("text");
                for (String token : full.split(" ")) {
                    token = token.replaceAll("[^a-zA-Z0-9]", "");
                    for (String dword : detectWords) {
                        if (token.toLowerCase().contains(dword)) {
                            System.out.println("Detected keyword: " + token);
                            String media_key = jsonObject.getJSONObject("attachments").getJSONArray("media_keys").get(0).toString();
                            if (!(media_key == null)) foundMediaKeys.add(media_key);
                        }
                    }
                }
            }
        }
        return foundMediaKeys;
    }

    private static JSONObject getJSONObject(Response response) {
        String jsonData = null;
        try {
            jsonData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(jsonData);
    }

    private static Response makeRequest(Request request) {
        System.out.println("Making request: " + request.toString());
        OkHttpClient client = new OkHttpClient();
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
