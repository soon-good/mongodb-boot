package io.study.boot_mongodb.employee;

import io.study.boot_mongodb.employee.mongo.Employee;
import io.study.boot_mongodb.employee.mongo.EmployeeRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {

	@Autowired
	private MongoTemplate mongoTemplate;

	private final EmployeeRepository employeeRepository;

	@Autowired
	public EmployeeController(EmployeeRepository employeeRepository){
		this.employeeRepository = employeeRepository;
	}

	@GetMapping("/employees")
	public List<Employee> getEmployees(){
		List<Employee> all = mongoTemplate.findAll(Employee.class);
		return all;
	}

	@GetMapping("/employee/insert/by-mongo-template")
	public EmployeeDto insertEmployee(){
		Employee e1 = Employee.builder().id("by-mongo-template").name("소방관#1").salary(1000D).build();
		mongoTemplate.insert(e1);

		EmployeeDto resultDto = EmployeeDto.of(e1);
		return resultDto;
	}

	@GetMapping("employee/insert/by-mongo-repository")
	public EmployeeDto insertEmployeeByMongoRepository(){
		Employee e2 = Employee.builder().id("by-mongo-repository").name("소방관#2").salary(1000D).build();
		employeeRepository.insert(e2);

		EmployeeDto resultDto = EmployeeDto.of(e2);
		return resultDto;
	}
}
