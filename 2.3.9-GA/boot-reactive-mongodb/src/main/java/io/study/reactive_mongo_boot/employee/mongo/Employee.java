package io.study.reactive_mongo_boot.employee.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "employees")
public class Employee {

	@Id
	private String id;

	private String name;

	private Double salary;

	public Employee(){}

	public Employee(String name, Double salary){
		this.name = name;
		this.salary = salary;
	}

}
