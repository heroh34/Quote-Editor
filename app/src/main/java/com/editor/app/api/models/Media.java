package com.editor.app.api.models;

import java.io.Serializable;
import java.util.List;

public class Media implements Serializable {
    private String id;
    private String slug;
    private String color;
    private Urls urls;

    public static class Urls implements Serializable {
        private String raw;
        private String full;
        private String regular;
        private String small;
        private String thumb;
        private String small_s3;

        // Getters

        public String getRaw() {
            return raw;
        }

        public String getFull() {
            return full;
        }

        public String getRegular() {
            return regular;
        }

        public String getSmall() {
            return small;
        }

        public String getThumb() {
            return thumb;
        }

        public String getSmall_s3() {
            return small_s3;
        }
    }

    // Getters

    public String getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getColor() {
        return color;
    }

    public Urls getUrls() {
        return urls;
    }
}