package com.example.SearchEngine.SiteParse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ForkJoinPool;

@Component
public class Parser implements CommandLineRunner {
    @Value("${spring.application.url}")
    private String url;

    @Override
    public void run(String... args) throws Exception {
        DBWriter.connect();
        CreateListOfLinks listOfLinks = new CreateListOfLinks(url, url);
        Set<String> paths = new ForkJoinPool().invoke(listOfLinks);
        DBWriter.multiInsert();
    }
}
