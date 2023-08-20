package com.trinitysmf.mysmf.models.providers;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;
import com.trinitysmf.mysmf.models.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by namxn_000 on 01/11/2017.
 */

public class WordpressEventsProvider extends Request<ArrayList<Event>> {
    public Response.Listener<ArrayList<Event>> listener;

    public WordpressEventsProvider(Response.Listener<ArrayList<Event>> listener, Response.ErrorListener errorListener, int limit) {
        super(Method.GET, "http://trinitysmf.com/wp-json/wp/v2/posts?_embed&per_page="+ limit + "&categories=7", errorListener);
        this.listener = listener;
        setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    protected Response<ArrayList<Event>> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            JSONArray result = new JSONArray(json);
            
            ArrayList<Event> events = new ArrayList<>();
            
            for (int i = 0; i < result.length(); i++) {
                JSONObject current = result.getJSONObject(i);
                Event event = new Event();
                event.title = current.getJSONObject("title").getString("rendered");
                //sometimes there might not be an image
                if(current.getJSONObject("_embedded").has("wp:featuredmedia")) {
                    //sometimes the API will return forbidden
                    JSONObject featured = current.getJSONObject("_embedded").getJSONArray("wp:featuredmedia").getJSONObject(0);
                    if (featured.has("media_details")) {
                        event.imgUrl = featured.getJSONObject("media_details").getJSONObject("sizes").getJSONObject("full").getString("source_url");
                    }
                }
                event.summary= current.getJSONObject("excerpt").getString("rendered");
                event.author = current.getJSONObject("_embedded").getJSONArray("author").getJSONObject(0).getString("name");
                event.date = current.getString("date");
                event.url = current.getString("link");
                
                //event - specific
                if(current.has("acf")) {
                    JSONObject eventSpecific = current.getJSONObject("acf");
                    event.facebookLink = eventSpecific.getString("facebook_link");
                    event.eventTime = eventSpecific.getString("time");
                    event.venue = eventSpecific.getString("venue");
                }
                
                events.add(event);
            }

            return Response.success(
                    events,
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
    protected void deliverResponse(ArrayList<Event> response) {
        listener.onResponse(response);
    }
}
