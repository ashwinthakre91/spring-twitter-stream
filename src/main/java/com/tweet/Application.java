package com.tweet;


import com.tweet.service.TwitterCacheService;
import com.tweet.serviceImpl.StreamService;
import com.tweet.serviceImpl.StreamService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

import java.util.Scanner;

@SpringBootApplication
public class Application extends SpringBootServletInitializer implements CommandLineRunner{
	
	@Autowired
    private Environment environment;

    @Autowired
    TwitterCacheService twitterCacheService;

    @Autowired
    StreamService streamService;

    @Autowired
    StreamService2 streamService2;
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    TwitterTemplate getTwtTemplate(){
        return new TwitterTemplate(environment.getProperty("consumerKey"), environment.getProperty("consumerSecret"), environment.getProperty("accessToken"), environment.getProperty("accessTokenSecret"));
    }

    @Override
    public void run(String... args) throws Exception {
        //clear console screen
        System.out.print("\033[H\033[2J");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Search something : ");
        String keyword = scanner.nextLine();
        scanner.close();
        //streamService.streamApi(keyword);
        streamService2.streamApi(keyword);
    //twitterCacheService.minuteScheduler(keyword);
    }

} 