package com.valle.deliveryboyfoodieapp.models;

public class FilterHistoryBean {

    private String name;
    private String time;

    public FilterHistoryBean(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }
}
