package com.tweet.serviceImpl;

import com.tweet.model.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.social.twitter.api.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StreamService2 {
    @Autowired
    private Twitter twitter;

    private final Logger log = LoggerFactory.getLogger(StreamService.class);
    Map<String, Word> cacheWords;
    private final int  maxCacheWordsCapacity= 20;

    public StreamService2(){
        this.cacheWords = new ConcurrentHashMap<String, Word>(maxCacheWordsCapacity);
    }

    @Async
    public void streamApi(String keyword) throws InterruptedException{
        List<StreamListener> listeners = new ArrayList<StreamListener>();

        //Streaming api code here
        StreamListener streamListener = new StreamListener() {
            @Override
            public void onWarning(StreamWarningEvent warningEvent) {
            }

            @Override
            public void onLimit(int numberOfLimitedTweets) {

            }

            @Override
            public void onDelete(StreamDeleteEvent deleteEvent) {
            }

            @Override
            public void onTweet(Tweet tweet) {
                //log.info("{}",tweet.getText());
                String realTimeTweet = tweet.getText();
                String[]  realTimeTweetWords= realTimeTweet.split(" ");
                for(String word: realTimeTweetWords) {
                    /*if maxCacheWordsCapacity is reached remove all words that
                    has count(score)==0*/
                    removeZeroScoreEntries();
                    //make all words character to lowercase
                    word=word.toLowerCase();
                    //increment word count if exists in cache else add new entry
                    if(cacheWords.containsKey(word)) {
                        cacheWords.put(word,
                                new Word((cacheWords.get(word).getCount().intValue()+1),60000));
                    }else{
                        cacheWords.put(word,new Word(1,60000));
                    }
                }
            }

        };
        scheduler();
        listeners.add(streamListener);
        FilterStreamParameters filterStreamParameters = new FilterStreamParameters();
        filterStreamParameters.track(keyword);
        Stream userStream = twitter.streamingOperations().filter(filterStreamParameters, listeners);
        Thread.sleep(Calendar.getInstance().getTimeInMillis());
        userStream.close();
    }

    private void scheduler(){
        /*after every 30 seconds check for expired words
        and decrement its count by 1*/
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        for(Iterator<Map.Entry<String, Word>> it = cacheWords.entrySet().iterator(); it.hasNext(); ) {
                            Map.Entry<String, Word> entry = it.next();
                            if(entry.getValue().isExpired()){
                                /*if cacheWord count is greater than zero then decrement it
                                else i.e. 0 then remove it from the cache*/
                                if(entry.getValue().getCount()>0){
                                    entry.getValue().setCount(entry.getValue().getCount()-1);
                                }else{
                                    it.remove();
                                }
                            }
                        }
                    }
                },
                30000,1000*30);

        /*after starting every 60 seconds print the cacheWords with count(score)
        * greater than 1*/
        System.out.println(Calendar.getInstance().getTime());
        System.out.println("Printing cache words after 1 min.");
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    boolean isAnyScoreGreaterthanOne = false;
                    @Override
                    public void run() {
                        isAnyScoreGreaterthanOne = false;
                        System.out.print("\033[H\033[2J");
                        System.out.println(Calendar.getInstance().getTime());
                        for(Iterator<Map.Entry<String, Word>> it = cacheWords.entrySet().iterator(); it.hasNext(); ) {
                            Map.Entry<String, Word> entry = it.next();
                            if(entry.getValue().getCount()>1){
                                System.out.println(entry.getKey());
                                isAnyScoreGreaterthanOne = true;
                            }
                        }
                        if(!isAnyScoreGreaterthanOne){
                            System.out.println("No cache word has score greater than 1.");
                        }

                    }
                },
                60000,1000 * 60);
    }

    private void removeZeroScoreEntries(){
        if(cacheWords.size()==maxCacheWordsCapacity){
            for(Iterator<Map.Entry<String, Word>> it = cacheWords.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Word> entry = it.next();
                if(entry.getValue().getCount()==0){
                    it.remove();
                }
            }
        }
    }
}
