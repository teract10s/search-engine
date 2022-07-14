package com.example.SearchEngine.Indexing;

import com.example.SearchEngine.SiteParse.DBWriter;

public class Indexing {
    public Indexing(){
        accountingLemm();
    }

    private void accountingLemm() {
        DBWriter.getAllPage();
    }
}
