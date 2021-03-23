package io.study.reactive_mongo_boot.department.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "departments")
public class Department {

	@Id @Field("dept_no")
	private String id;

	@Field("dept_name")
	private String name;

	public static enum Type {
		FIRE_FIGHT, EMERGENCY, DEVELOPMENT
	}
}
