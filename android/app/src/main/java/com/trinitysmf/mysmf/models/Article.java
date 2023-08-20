package com.trinitysmf.mysmf.models;

import org.jsoup.nodes.Element;

/**
 * Created by namxn_000 on 20/10/2017.
 */

public class Article {
    public String title;
    public String url;
    public String summary;
    public String author;
    public String date;
    public String imgUrl;

    public static Article parseHtml(Element e){
        Article result = new Article();
        result.author = e.select(".meta-author").text();
        result.date = e.select(".meta-date").text();
        result.summary = e.select(".post-excerpt p").get(0).text();
        result.url = e.select("a[itemprop=\"url\"]").attr("href");
        result.imgUrl = e.select("img[itemprop='image']").attr("src");
        result.title = e.select("h2[itemprop='headline']").text();
        return result;
    }
}
