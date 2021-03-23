package io.study.reactive_mongo_boot.employee.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface EmployeeRepository extends ReactiveMongoRepository<Employee, String> {

}
