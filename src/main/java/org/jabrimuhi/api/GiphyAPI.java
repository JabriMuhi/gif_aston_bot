package org.jabrimuhi.api;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class GiphyAPI {
    private static final String API_KEY = "ev8HBg4KoAAAYboKadrwAVh9dv8W1UtG";
    private static final String BASE_URL = "https://api.giphy.com/v1/gifs/";

    public static String searchGifs(String searchQuery) throws Exception {
        HttpGet request = new HttpGet(BASE_URL + "search?api_key=" + API_KEY + "&q=" + searchQuery + "&limit=1");

        var httpClient = HttpClientBuilder.create().build();
        CloseableHttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String responseString = EntityUtils.toString(entity);
        response.close();

        return responseString;
    }
}
