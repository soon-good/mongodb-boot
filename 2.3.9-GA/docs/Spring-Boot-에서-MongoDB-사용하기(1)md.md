# Spring Boot 에서 MongoDB 사용하기(1)

> **Spring Boot Version : 2.3.9 GA** 

  

오늘 정리할 내용은 MongoDB Datasource를 연동해서 데이터를 insert 해보는 것이다. 단순히 현재 돌아가고 있는 MongoDB가 잘 동작하기 위한 테스트이다. MongoDB의 경우 스프링에서 사용할 때 임베디드 MongoDB로 테스트하는 방법 역시 존재한다. 임베디드 MongoDB로 테스트하는 방식은 제일 마지막 문서에서 정리하게 되지 않을까 싶다.

  

MongoDB 와 Spring Boot를 연동하는 과정에 대해 정리할 문서들의 작성 계획은 아래와 같다.

- Spring Boot 에서 MongoDB 사용하기 (1)
  - MongoDB 와 Spring Boot 인스턴스를 연동해서 INSERT 해보기
  - MongoRepository, MongoTemplate을 이용한 INSERT 연산
- Spring Boot 에서 MongoDB 사용하기 (2)
  - MongoDB와 Spring Boot 인스턴스를 연동해서 여러가지 CRUD 실행해보기
  - MongoRepository 를 활용한 다양한 종류의 INSERT 연산
- Spring Boot 에서 MongoDB 사용하기 (3)
  - 리액티브 MongoDB 사용해보기
- Spring Boot 에서 MongoDB 사용하기 (4)
  - 임베디드 MongoDB 로 테스트해보기

 

위의 계획중에서 오늘 작성할 내용은 **Spring Boot 에서 MongoDB 사용하기 (1)** 이다.  



# 참고자료

## 공식제공 자료

- Getting Started
  - [spring.io - getting started, Accessing Data with Mongo DB](https://spring.io/guides/gs/accessing-data-mongodb/)
- Spring Data MongoDB 홈
  - [Spring Data MongoDB 홈](https://spring.io/projects/spring-data-mongodb#learn)
- MongoDB Documentation
  - [3.0.8 GA](https://docs.spring.io/spring-data/mongodb/docs/3.0.8.RELEASE/reference/html/)
  - [3.1.6 GA](https://docs.spring.io/spring-data/mongodb/docs/3.1.6/reference/html/#reference)



## 메인 참고자료

- [www.devglan.com - Spring Boot MongoDB configuration](https://www.devglan.com/spring-boot/spring-boot-mongodb-configuration)
  - application.yml, properties 에 connection string을 어떻게 주는지 등에 대해 언급된 자료가 없어서 조금 헤매다가 찾은 자료
- [baeldung.com - Intrduction to Spring Data MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)
  - MongoClient 객체를 Bean으로 생성하는 방식에 대한 설명을 찾을 수 있는 곳이 없었는데, 여기에서 참고해서 예제를 작성했다.
- [everyflower.tistory.com](https://everyflower.tistory.com/222)
  - @EnableMongoRepositories에 scan 할 repository 를 `**` 과 같은 표현식으로 지정하는 방식이 확신이 가질 않아서 찾아본 자료.



# build.gradle (의존성 추가)

```groovy
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-data-mongodb'
  // ... 
}
```



# Configuration

Configuration 코드를 설정하는 방식은 두가지가 있다. 이번 예제에서 사용한 방식은 AbstractMongoClientConfiguration클래스를 상속하는 방식이다.

- AbstractMongoClientConfiguration 클래스를 상속(확장 - extends)받아서 필요한 함수들을 override 하는방식
  - AbstractMongoClientConfiguration 클래스는 MongoClient 설정코드들을 Abstract 화, 즉 추상화 한 클래스이다.
  - 중복된 코드들이 공통화 되어 있는 듯하다.
- SimpleMongoConfig
  - 직접 필요한 MongoClient, MongoTemplate 타입의 빈을 직접 생성하는 방식이다.



## 1) AbstractMongoClientConfiguration 을 활용하는 방식

```java
package io.study.boot_mongodb.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories({"io.study.boot_mongodb.**.mongo"})
public class MongoConfig extends AbstractMongoClientConfiguration {

	@Value("${spring.data.mongodb.uri}")
	private String mongoUrl;

	private static final String DATABASE_NAME = "codingtest";

	@Override
	public MongoClient mongoClient() {
		ConnectionString connectionString = new ConnectionString(mongoUrl);

		MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
			.applyConnectionString(connectionString)
			.build();

		return MongoClients.create(mongoClientSettings);
	}

	@Override
	protected Collection<String> getMappingBasePackages() {
		return Collections.singleton("employee.mongo");
	}

	@Override
	protected String getDatabaseName() {
		return DATABASE_NAME;
	}
}
```



## 2) MongoTemplate, MongoClient 를 Bean으로 직접 생산하는 방식

```java
package io.study.boot_mongodb.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

//@Configuration
//@EnableMongoRepositories({"io.study.boot_mongodb.**.mongo"})
public class SimpleMongoConfig {

	@Value("${spring.data.mongodb.uri}")
	private String mongoUrl;

	private static final String DATABASE_NAME = "codingtest";

	@Bean
	public MongoClient mongo(){
		ConnectionString connectionString = new ConnectionString(mongoUrl);

		// Decorator 객체인 듯
		MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
			.applyConnectionString(connectionString)
			.build();

		return MongoClients.create(mongoClientSettings);
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongo(), DATABASE_NAME);
	}

}
```



# application.yml

> application.yml 의 주요부분들을 작성하는데에 참고한 자료는 [devglan.com/spring-boot-mongodb-configuration](https://www.devglan.com/spring-boot/spring-boot-mongodb-configuration) 이다.



```yaml
server:
  port: 80

spring:
  profiles:
    active: testdocker
---
spring:
  profiles: testdocker
  jpa:
    hibernate:
      ddl-auto: create-drop
  data:
    mongodb:
      uri: mongodb://mongoadmin:1111@localhost:27039
      authentication-database: admin
      username: mongoadmin
      password: 1111
      host: localhost
      port: 27039
```



# Entity, Repository

## Employee.java

```java
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
```



## EmployeeRepository.java

```java
package io.study.boot_mongodb.employee.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository extends MongoRepository<Employee, String> {

}
```



# EmployeeController, EmployeeDto

## EmployeeController.java

EmployeeController 클래스 내에는 데이터를 insert 하기위한 API를 두개 만들었다.

- /employee/insert/by-mongo-template
  - MongoTemplate 을 이용한 insert 연산을 테스트하기 위한 용도
- /employee/insert/by-mongo-repository
  - MongoRepository 를 이용한 insert 연산을 테스트하기 위한 용도



```java
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

	@GetMapping("/employee/insert/by-mongo-repository")
	public EmployeeDto insertEmployeeByMongoRepository(){
		Employee e2 = Employee.builder().id("by-mongo-repository").name("소방관#2").salary(1000D).build();
		employeeRepository.insert(e2);

		EmployeeDto resultDto = EmployeeDto.of(e2);
		return resultDto;
	}
}
```



## EmployeeDto.java

- 아래  Dto 클래스 내의 메서드 중에 of(Employee employee) 메서드는 아래의 역할을 한다.
  - Employee 객체를 받아서 EmployeeDto 객체를 리턴하는 역할



```java
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
```



# 출력결과

## /employee/insert/by-mongo-template

![이미지](./img/INSERT_SAMPLE_RESULT_1.png)

  

## /employee/insert/by-mongo-repository

![이미지](./img/INSERT_SAMPLE_RESULT_2.png)

  

## DB 저장결과 확인

![이미지](./img/INSERT_SAMPLE_DATABASE_RESULT.png)

