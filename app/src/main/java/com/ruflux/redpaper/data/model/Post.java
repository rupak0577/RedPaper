package com.ruflux.redpaper.data.model;

import java.util.List;

public class Post {
    private String domain;
    private String id;
    private String title;
    private Preview preview;
    private String url;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Preview getPreview() {
        return preview;
    }

    public void setPreview(Preview preview) {
        this.preview = preview;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static class Preview {
        private List<Image> images = null;

        public List<Image> getImages() {
            return images;
        }

        public void setImages(List<Image> images) {
            this.images = images;
        }

        public static class Image {
            private Source source;
            private List<Resolution> resolutions = null;

            public Source getSource() {
                return source;
            }

            public void setSource(Source source) {
                this.source = source;
            }

            public List<Resolution> getResolutions() {
                return resolutions;
            }

            public void setResolutions(List<Resolution> resolutions) {
                this.resolutions = resolutions;
            }
        }

        public static class Resolution {
            private String url;
            private Integer width;
            private Integer height;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public Integer getWidth() {
                return width;
            }

            public void setWidth(Integer width) {
                this.width = width;
            }

            public Integer getHeight() {
                return height;
            }

            public void setHeight(Integer height) {
                this.height = height;
            }
        }

        public static class Source {
            private String url;
            private Integer width;
            private Integer height;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public Integer getWidth() {
                return width;
            }

            public void setWidth(Integer width) {
                this.width = width;
            }

            public Integer getHeight() {
                return height;
            }

            public void setHeight(Integer height) {
                this.height = height;
            }
        }
    }
}
