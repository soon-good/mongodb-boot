package io.study.reactive_mongo_boot.employee;

import io.study.reactive_mongo_boot.employee.mongo.Employee;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class EmployeeTest {

	@Test
	void Mono_테스트해보기(){
		Employee e1 = new Employee("소방관 #1", 1000D);
		Mono<EmployeeDto> dtoResult = Mono.just(e1).map(data -> EmployeeDto.of(data));

		dtoResult.doOnNext(data -> System.out.println("doOnNext : " + data.getName()));
		System.out.println(dtoResult);
	}
}
