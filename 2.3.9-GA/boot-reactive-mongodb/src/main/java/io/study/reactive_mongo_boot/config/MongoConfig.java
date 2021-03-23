package io.study.reactive_mongo_boot.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories({"io.study.reactive_mongo_boot.**.mongo"})
public class MongoConfig extends AbstractReactiveMongoConfiguration {

	@Value("${spring.data.mongodb.uri}")
	private String mongoUri;

//	@Bean
//	public MongoClient mongoClient(){
//		ConnectionString connectionString = new ConnectionString(mongoUri);
//
//		MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
//			.applyConnectionString(connectionString)
//			.build();
//
//		return MongoClients.create(mongoClientSettings);
//	}

	@Override
	public MongoClient reactiveMongoClient() {

		ConnectionString connectionString = new ConnectionString(mongoUri);

		MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
			.applyConnectionString(connectionString)
			.build();

		return MongoClients.create(mongoClientSettings);
	}

	@Override
	protected String getDatabaseName() {
		return "codingtest";
	}
}
