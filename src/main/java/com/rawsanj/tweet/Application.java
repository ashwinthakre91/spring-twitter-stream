package com.rawsanj.tweet;


import com.rawsanj.tweet.service.TwitterCacheService;
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


    // Put your logic here.
    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter something : ");
        String keyword = scanner.nextLine();
        scanner.close();
    twitterCacheService.minuteScheduler(keyword);
    }

} 