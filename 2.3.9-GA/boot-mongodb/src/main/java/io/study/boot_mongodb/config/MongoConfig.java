package io.study.boot_mongodb.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories({"io.study.boot_mongodb.**.mongo"})
public class MongoConfig extends AbstractMongoClientConfiguration {

	@Value("${spring.data.mongodb.uri}")
	private String mongoUrl;

	private static final String DATABASE_NAME = "codingtest";

	@Override
	public MongoClient mongoClient() {
		ConnectionString connectionString = new ConnectionString(mongoUrl);

		MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
			.applyConnectionString(connectionString)
			.build();

		return MongoClients.create(mongoClientSettings);
	}

	@Override
	protected Collection<String> getMappingBasePackages() {
		return Collections.singleton("employee.mongo");
	}

	@Override
	protected String getDatabaseName() {
		return DATABASE_NAME;
	}
}
