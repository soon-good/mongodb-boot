package io.study.reactive_mongo_boot.employee;

import io.study.reactive_mongo_boot.employee.mongo.Employee;
import io.study.reactive_mongo_boot.employee.mongo.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class EmployeeController {

	private final EmployeeRepository employeeRepository;

	@Autowired
	public EmployeeController(EmployeeRepository employeeRepository){
		this.employeeRepository = employeeRepository;
	}

	@GetMapping("/employees/insert")
	public Mono<EmployeeDto> insertEmployeeByGet(){
		Employee e1 = new Employee("소방관#3", 1000D);

		// 데이터 insert
		Mono<Employee> monoData = employeeRepository.insert(e1);

		// Dto 로 타입변환
		Mono<EmployeeDto> resultDto = monoData.map(data -> EmployeeDto.of(data));
		return resultDto;
	}

}
