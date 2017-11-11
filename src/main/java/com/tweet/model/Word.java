package com.tweet.model;

public class Word {
    private Integer count;
    private Long expireAfter;
    private Long insertTime;

    public Word(int count, long expireAfter){
        this.count = count;
        this.expireAfter = expireAfter;
        this.insertTime = System.currentTimeMillis();
    }

    public boolean isExpired(){
        return ((insertTime+expireAfter)<System.currentTimeMillis());
    }


    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Long getExpireAfter() {
        return expireAfter;
    }

    public void setExpireAfter(Long expireAfter) {
        this.expireAfter = expireAfter;
    }

    public Long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Long insertTime) {
        this.insertTime = insertTime;
    }
}
