package com.trinitysmf.mysmf.models.providers;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;
import com.trinitysmf.mysmf.models.Share;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by namxn_000 on 20/10/2017.
 */

public class YahooShareProvider extends Request<Share>{
    private final Response.Listener<Share> listener;

    public YahooShareProvider(String ticker, Response.Listener<Share> listener, Response.ErrorListener errorListener) {
        super(Method.GET, "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22"+ticker+"%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=", errorListener);
        this.listener = listener;
    }

    @Override
    protected Response<Share> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            JSONObject result = new JSONObject(json).getJSONObject("query").getJSONObject("results").getJSONObject("quote");
            Share share = new Share();
            share.title = result.getString("Name");
            share.quote = result.getString("Ask");
            share.change = result.getString("Change_PercentChange");
            share.currency = result.getString("Currency");
            share.ticker = result.getString("Symbol");

            return Response.success(
                    share,
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
    protected void deliverResponse(Share response) {
        listener.onResponse(response);
    }
}
