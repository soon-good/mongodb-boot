package io.study.boot_mongodb.runner;

import io.study.boot_mongodb.employee.mongo.Employee;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Order(1)
@Component
public class EmpDataCommandLineRunner implements CommandLineRunner {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void run(String... args) throws Exception {
		System.out.println("CommandLineRunner Args: " + Arrays.toString(args));
//		Employee e1 = Employee.builder().id("a1").name("소방관 #1").salary(1000D).build();
//		Employee e2 = Employee.builder().id("a2").name("소방관 #2").salary(2000D).build();
//		Employee e3 = Employee.builder().id("a3").name("소방관 #3").salary(3000D).build();
//
//		mongoTemplate.insert(e1);
//		mongoTemplate.insert(e2);
//		mongoTemplate.insert(e3);
//

	}
}
