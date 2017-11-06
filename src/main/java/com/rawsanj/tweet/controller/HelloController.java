package com.rawsanj.tweet.controller;

import com.rawsanj.tweet.serviceImpl.StreamService;
import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.util.List;

@Controller
@Component
@RequestMapping("/")
public class HelloController {
	
	private TwitterTemplate twitterTemplate;
    
    private StreamService streamService;

    @Inject
    public HelloController(StreamService streamService, TwitterTemplate twitterTemplate) {
        
        this.streamService = streamService;
        this.twitterTemplate=twitterTemplate;
    }

    @RequestMapping(value = "tweet",method=RequestMethod.GET)
    public String searchTwitter(Model model, @RequestParam String search) {
    	
    	int count = 1;
    	
        SearchResults results = twitterTemplate.searchOperations().search(
        	    new SearchParameters(search)
        	        .resultType(SearchParameters.ResultType.RECENT)
                .count(count));
        
        List<Tweet> tweets = results.getTweets();        
        model.addAttribute("tweets", tweets);
        model.addAttribute("count", count);
        model.addAttribute("search", search);
        
//        System.out.println("+++++++++++++++++++DEBUGGING++++++++++++++++++");
//        int i =0;
//        for (Tweet tweet : tweets) {
//        	i++;
//			System.out.println(i + " - "+tweet.getUser().getName() + " Tweeted : "+tweet.getText() + " from " + tweet.getUser().getLocation() 
//					+ " @ " + tweet.getCreatedAt() + tweet.getUser().getLocation()  );
//			
//		}
//        System.out.println("+++++++++++++++++++++++++++++++++GeoTemnplatePlz Work++++++++++++++++++++++++");
//        RestTemplate restTemplate = twitterTemplate.getRestTemplate();
//        GeoTemplate geoTemplate = new GeoTemplate(restTemplate, true, true);
//        List<Place> place = geoTemplate.search(37.423021, -122.083739);
//        for (Place p : place) {
//			System.out.println(p.getName() + " " + p.getCountry() );
//		} 
//        System.out.println("+++++++++++++++++++++++++++++++++GeoTemnplatePlz Work++++++++++++++++++++++++");
//        
        
        return "search";
    }
    
    @RequestMapping("/stream")
    public String streamTweet(Model model) throws InterruptedException{
        
        /*Model returnedmodel = streamService.streamApi(model);
        model = returnedmodel;*/

        return "stream";
    }
    

//    @RequestMapping("/streamfire/{time}")
//    public String streamFireTweet(@PathVariable int time) throws InterruptedException{
//
//        streamService.streamFireApi(time, twitterTemplate);
//
//        return "stream";
//    }
       

}