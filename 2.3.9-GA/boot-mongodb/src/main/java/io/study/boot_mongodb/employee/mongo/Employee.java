package io.study.boot_mongodb.employee.mongo;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "employees")
public class Employee {

	@Id
	private String id;

	private String name;

	private Double salary;

	@Builder
	public Employee(String id, String name, Double salary){
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

}
