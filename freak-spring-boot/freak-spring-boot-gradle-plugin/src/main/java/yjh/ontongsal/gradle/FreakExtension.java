package yjh.ontongsal.gradle;

public class FreakExtension {
    private boolean web;
    private boolean cache;
    private boolean httpClient;
    private boolean db;

    public boolean isWeb() {
        return web;
    }

    public void setWeb(boolean web) {
        this.web = web;
    }

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public boolean isHttpClient() {
        return httpClient;
    }

    public void setHttpClient(boolean httpClient) {
        this.httpClient = httpClient;
    }

    public boolean isDb() {
        return db;
    }

    public void setDb(boolean db) {
        this.db = db;
    }
}
