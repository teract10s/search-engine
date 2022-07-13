package com.example.SearchEngine.SiteParse;

import lombok.Getter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveTask;

@Getter
public class CreateListOfLinks extends RecursiveTask<Set<String>> {
    private static final Set<String> paths = new HashSet<>();
    private final String url;

    private final String rootURL;

    public CreateListOfLinks(String url, String rootURL) {
        this.url = url;
        this.rootURL = rootURL;
    }

    @Override
    protected Set<String> compute() {
        paths.add('/' + url.substring(rootURL.length()));

        List<CreateListOfLinks> taskList = new ArrayList<>();

        try {
           Set<String> currentPaths = getCurrentPaths(url);

           for (String str : currentPaths){
               CreateListOfLinks task = new CreateListOfLinks(str, rootURL);
               task.fork();
               taskList.add(task);
           }
        } catch (Exception e) {
            e.printStackTrace();
        }

        taskList.forEach(CreateListOfLinks::join);

        return paths;
    }

    private Set<String> getCurrentPaths(String url) throws IOException, InterruptedException {
        Set<String> currentPaths = new HashSet<>();
        Thread.sleep((int) (Math.random() * 50 + 100));

        Connection connection = Jsoup.connect(url).ignoreContentType(true)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com");

        Document doc = connection.get();

        DBWriter.write('/' + url.substring(rootURL.length()), connection.execute().statusCode(), doc.toString());
        Elements href = doc.select("a[href]");
        href.forEach(h -> {
            String currentUrl = h.absUrl("href");
            if (currentUrl.startsWith("/")){
                currentUrl = url + currentUrl.substring(1);
            }
            if (checkURL(currentUrl) && addNewURL(currentUrl)) {
                currentPaths.add(currentUrl);
            }
        });

        return currentPaths;
    }

    private synchronized boolean addNewURL(String url){
        return paths.add('/' + url.substring(rootURL.length()));
    }

    private boolean checkURL(String currentUrl){
        return currentUrl.startsWith(url);
    }
}
