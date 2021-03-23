package io.study.boot_mongodb.employee;

import io.study.boot_mongodb.employee.mongo.Employee;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EmployeeDto {

	private String name;

	private Double salary;

	@Builder
	public EmployeeDto(String name, Double salary){
		this.name = name;
		this.salary = salary;
	}

	public static EmployeeDto of (Employee employee){
		return EmployeeDto.builder()
			.name(employee.getName())
			.salary(employee.getSalary())
			.build();
	}
}
