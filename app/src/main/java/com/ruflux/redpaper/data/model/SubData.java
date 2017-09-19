package com.ruflux.redpaper.data.model;

import java.util.List;

public class SubData {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        private List<Child> children = null;
        private String after;
        private String before = null;

        public List<Child> getChildren() {
            return children;
        }

        public String getAfter() {
            return after;
        }

        public void setAfter(String after) {
            this.after = after;
        }

        public Object getBefore() {
            return before;
        }

        public void setBefore(String before) {
            this.before = before;
        }
    }

    public static class Child {
        private Post data;

        public Post getData() {
            return data;
        }
    }
}