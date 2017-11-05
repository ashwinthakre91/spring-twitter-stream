package com.tweet.serviceImpl;

import com.tweet.service.TwitterCacheService;
import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TwitterCacheServiceImpl implements TwitterCacheService {
    private TwitterTemplate twitterTemplate;
    Map<String, Integer> cacheWords;
    String mostRecentWord;
    Timer timer;

    @Inject
    public TwitterCacheServiceImpl(TwitterTemplate twitterTemplate) {

        this.twitterTemplate=twitterTemplate;
        this.mostRecentWord=null;
        this.cacheWords = new ConcurrentHashMap<String, Integer>(10);
    }

    //remove words from cache
    private void removeCacheWords(){
        //Remove all the cache words whose value is 0
        for(Iterator<Map.Entry<String, Integer>> it = cacheWords.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            if(entry.getValue().equals(0)) {
                it.remove();
            }
        }
    }

    private void insertIncrementCacheWordsValue(){
        //Everytime mostRecentWord is seen its score goes up by 1.
        //if word is already present increment its value else insert new entry with value 1
        if(cacheWords.containsValue(mostRecentWord)){
            cacheWords.put(mostRecentWord,cacheWords.get(mostRecentWord)+1);
        }else{
            cacheWords.put(mostRecentWord,1);
        }
    }

    private void printCacheWords(){
        //print cacheWords whose value is greater than 1
        if(cacheWords.size()>0){
            //clear console screen
            System.out.print("\033[H\033[2J");
            for (Map.Entry<String,Integer> entry : cacheWords.entrySet()){
                if (entry.getValue()>1){
                    System.out.println(entry.getKey());
                    System.out.println("value: "+entry.getValue());
                }else{
                    System.out.println("Right now no cache word has score greater than 1");
                }
            }
        }else{
            System.out.println("Cache is empty!!");
        }
    }

    private void decrementCacheWordsValue(){
        for(Iterator<Map.Entry<String, Integer>> it = cacheWords.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            if(!entry.getKey().equals(mostRecentWord)) {
                cacheWords.put(entry.getKey(),cacheWords.get(mostRecentWord)-1);
            }
        }
    }

    private String getMostRecentWord(String keyword){
        int count =1;
        //Get the recent most tweet(words).
        SearchResults results = twitterTemplate.searchOperations().search(
                new SearchParameters(keyword)
                        .resultType(SearchParameters.ResultType.RECENT)
                        .count(count));
        List<Tweet> tweets = results.getTweets();
        if(tweets.size()>0){
            return tweets.get(0).getText();
        }
        return null;
    }

    public void minuteScheduler(String keyword){
        int MINUTES = 1;
        timer = new Timer();
        //The timer is set to run every 1 min.
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                    mostRecentWord = getMostRecentWord(keyword);
                    if(mostRecentWord!=null){
                        removeCacheWords();
                        insertIncrementCacheWordsValue();
                        /*Everytime other words(other than mostRecentWord) is not seen after
                        30 sec its score goes down by 1.*/
                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        //decrement the value of key other than mostRecentWord by 1
                                        decrementCacheWordsValue();
                                    }
                                },
                                30000);
                    }else{
                        System.out.println("No most recent tweets found..Will look again after 1 min");
                    }
                printCacheWords();
                }
        }, 0, 1000 * 60 * MINUTES);
    }
}
