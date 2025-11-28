package com.editor.app.api.models;

import java.util.List;

public class SearchResponse {
    private int total;
    private int total_pages;
    private List<Media> results;

    public SearchResponse(int total, int total_pages, List<Media> results) {
        this.total = total;
        this.total_pages = total_pages;
        this.results = results;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public List<Media> getResults() {
        return results;
    }

    public void setResults(List<Media> results) {
        this.results = results;
    }
}
