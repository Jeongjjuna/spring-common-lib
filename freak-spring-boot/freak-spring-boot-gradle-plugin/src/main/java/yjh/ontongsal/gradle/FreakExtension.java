package yjh.ontongsal.gradle;

public class FreakExtension {
    private boolean web;
    private boolean redis;

    public boolean getWeb() {
        return web;
    }

    public boolean getRedis() {
        return redis;
    }

    public void setWeb(boolean web) {
        this.web = web;
    }

    public void setRedis(boolean redis) {
        this.redis = redis;
    }
}
