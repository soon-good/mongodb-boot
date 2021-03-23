package io.study.reactive_mongo_boot.employee;

import io.study.reactive_mongo_boot.employee.mongo.Employee;
import lombok.Builder;
import lombok.Data;

@Data
public class EmployeeDto {

	private String name;

	private Double salary;

	@Builder
	EmployeeDto(String name, Double salary){
		this.name = name;
		this.salary = salary;
	}

	public static EmployeeDto of(Employee employee){
		return EmployeeDto.builder()
			.name(employee.getName())
			.salary(employee.getSalary())
			.build();
	}
}
