package com.example.SearchEngine.SiteParse;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

@ConfigurationProperties(prefix = "parser")
public class Parser extends RecursiveTask<String> {
    private static String url;

    @Override
    protected String compute() {
        List<Parser> taskList = new ArrayList<>();
        return null;
    }
}
