# Spring-Twitter-Stream 

Spring Boot - Spring Social Twitter
I have made the assumption that we fetch the most recent tweet text
from twitter, not List of recent tweets(It can also be done easily).
Data is fetched from Twitter every minute.
NOTE: Also if score of words is <=1 we will not be able to see tweets. Please
change the value(recommendation is >=0) to see it(for testing purpose).

#### Clone the Github repository
```sh
$ git clone https://github.com/ashwinthakre91/spring-twitter-stream.git
```

#### Twitter App and Configuration(Although I have provide demo keys,you can create and provide yours)
1. Login to https://apps.twitter.com
2. Create a New Application and note down the *Consumer Key, Consumer Secret, Access Token and Access Token Secret*. 
3. Edit the `/src/main/resources/application.properties` and add above noted keys.

#### Run the application
```sh
$ mvn spring-boot:run
```

## Tools and Tech

The following tools, technologies and libraries are used to create this project :

* [Spring Boot] - (Spring Social Twitter)
* [Git]

## License
----

The MIT License (MIT)

Copyright (c) 2017. Ashwin Thakre

[Spring Boot]: http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/
[Git]: https://git-scm.com/

=======
# spring-twitter-stream
spring-twitter-stream
