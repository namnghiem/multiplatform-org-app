package com.trinitysmf.mysmf.models.providers;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.trinitysmf.mysmf.models.Share;
import com.trinitysmf.mysmf.models.ShareDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by namxn_000 on 03/11/2017.
 */

public class AlphaVantageShareProvider extends Request<Share> {
    private final ShareDetails ticker;
    private Response.Listener<Share> listener;
    private final Response.ErrorListener errorListener;

    public AlphaVantageShareProvider(ShareDetails ticker, Response.Listener<Share> listener, Response.ErrorListener errorListener) {
        super(Method.GET, "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol="+ticker.ticker+"&interval=15min&outputsize=compact&apikey=63SO9QGJRKSX7WTD", errorListener);
        this.ticker = ticker;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    @Override
    protected Response<Share> parseNetworkResponse(NetworkResponse response) {

        String json = null;
        try {
            json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            JSONObject result = new JSONObject(json).getJSONObject("Time Series (15min)");
            HashMap<Date, Share> dataSet = new HashMap<>();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            Iterator x = result.keys();
            JSONArray jsonArray = new JSONArray();

            while (x.hasNext()){
                String key = (String) x.next();
                jsonArray.put(result.get(key));
            }

            JSONObject recent = jsonArray.getJSONObject(0);
            Share share =  new Share();
            share.change = "";
            share.currency = "";
            share.title = ticker.title;
            share.quote = recent.getString("1. open");
            share.ticker= new JSONObject(json).getJSONObject("Meta Data").getString("2. Symbol");
            share.buyPrice = ticker.buyPrice;
            share.number = ticker.number;


            return Response.success(
                    share,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void deliverResponse(Share response) {
        listener.onResponse(response);
    }
}
