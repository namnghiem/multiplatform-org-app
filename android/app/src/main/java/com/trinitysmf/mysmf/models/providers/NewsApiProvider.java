package com.trinitysmf.mysmf.models.providers;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;
import com.trinitysmf.mysmf.models.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by namxn_000 on 21/10/2017.
 */

public class NewsApiProvider extends Request<ArrayList<Article>> {
    private final Response.Listener<ArrayList<Article>> listener;

    public NewsApiProvider(Response.Listener<ArrayList<Article>> listener, Response.ErrorListener errorListener) {
        super(Method.GET,
                "https://newsapi.org/v1/articles?source=bloomberg&sortBy=top&apiKey=beba570f76094a808fdb7bf78bf1dbbc",
                errorListener);
        this.listener = listener;
    }

    @Override
    protected Response<ArrayList<Article>> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            JSONArray result = new JSONObject(json).getJSONArray("articles");
            ArrayList<Article> articles = new ArrayList<>();
            for (int i = 0; i < result.length(); i++) {
                JSONObject current = result.getJSONObject(i);
                Article article = new Article();
                article.title = current.getString("title");
                article.imgUrl = current.getString("urlToImage");
                article.summary = current.getString("description");
                article.author = current.getString("author");
                article.date = current.getString("publishedAt");
                article.url = current.getString("url");
                articles.add(article);
            }

            return Response.success(
                    articles,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(ArrayList<Article> response) {
        listener.onResponse(response);
    }
}
