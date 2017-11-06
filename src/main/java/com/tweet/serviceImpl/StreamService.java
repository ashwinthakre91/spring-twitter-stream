package com.tweet.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.social.twitter.api.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StreamService {
	@Autowired
	private Twitter twitter;

	private final Logger log = LoggerFactory.getLogger(StreamService.class);
	Boolean l1;
	Boolean l2;
	Map<String, String> listener1;
	Map<String, String> listener2;
	Map<String, String> recentWords;
	Map<String, Integer> cacheWords;
	Timer timer;

	public StreamService(){
		this.l1=true;
		this.l2=false;
		this.listener1 = new ConcurrentHashMap<String, String>(1000);
		this.listener2 = new ConcurrentHashMap<String, String>(1000);
		this.recentWords = new ConcurrentHashMap<String, String>(1000);
		this.cacheWords = new ConcurrentHashMap<String, Integer>(500);
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
				if(l1){
					for(String word: realTimeTweetWords)
					listener1.put(UUID.randomUUID().toString(),word);
				}
				if(l2){
					for(String word: realTimeTweetWords)
						listener2.put(UUID.randomUUID().toString(),word);
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
		int MINUTES = 1;
		timer = new Timer();
		//The timer is set to run every 1 min.
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				l2=true;
				l1=false;

				removeCacheWords();

				//Assign listener1 data to cacheWords and recentWords
				for(Iterator<Map.Entry<String, String>> it = listener1.entrySet().iterator(); it.hasNext(); ) {
					Map.Entry<String, String> entry = it.next();
					if(cacheWords.containsValue(entry.getValue())){
						cacheWords.put(entry.getValue(),cacheWords.get(entry.getValue())+1);
					}else{
						cacheWords.put(entry.getValue(),1);
					}
					recentWords.put(entry.getKey(),entry.getValue());
				}

				printCacheWords();

				listener1.clear();
				l1=true;
				l2=false;

				//Assign temporary running listener2 data to permanent listener1
				for(Iterator<Map.Entry<String, String>> it = listener2.entrySet().iterator(); it.hasNext(); ) {
					Map.Entry<String, String> entry = it.next();
					listener1.put(entry.getKey(),entry.getValue());
				}

				listener2.clear();


				// after 30 seconds perform decrement operation
				new java.util.Timer().schedule(
						new java.util.TimerTask() {
							@Override
							public void run() {
								decrementCacheWordsValue();
							}
						},
						30000);
			}
		}, 1, 1000 * 60 * MINUTES);
	}

	//perform decrement operation
	private void decrementCacheWordsValue(){
		for(Iterator<Map.Entry<String, Integer>> it = cacheWords.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, Integer> entry = it.next();
			if(!recentWords.containsKey(entry.getKey())) {
				cacheWords.put(entry.getKey(),cacheWords.get(entry.getKey())-1);
			}
		}
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

	//print words from cache
	private void printCacheWords(){
		//clear console screen
		System.out.print("\033[H\033[2J");
		//print cacheWords whose value is greater than 1
		if(cacheWords.size()>0){
			for (Map.Entry<String,Integer> entry : cacheWords.entrySet()){
				if (entry.getValue()>1){
					System.out.println(entry.getKey());
				}else{
					System.out.print("\033[H\033[2J");
					System.out.println("Right now no cache word has score greater than 1..checking after 1 min.");
				}
			}
		}else{
			System.out.println("Cache is empty!!");
		}
	}
}
