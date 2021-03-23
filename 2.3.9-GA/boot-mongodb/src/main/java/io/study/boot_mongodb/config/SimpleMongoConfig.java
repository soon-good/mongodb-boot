package io.study.boot_mongodb.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

//@Configuration
public class SimpleMongoConfig {

	@Value("${spring.data.mongodb.uri}")
	private String mongoUrl;

	private static final String DATABASE_NAME = "codingtest";

	@Bean
	public MongoClient mongo(){
		ConnectionString connectionString = new ConnectionString(mongoUrl);

		// Decorator 객체인 듯
		MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
			.applyConnectionString(connectionString)
			.build();

		return MongoClients.create(mongoClientSettings);
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongo(), DATABASE_NAME);
	}

}
