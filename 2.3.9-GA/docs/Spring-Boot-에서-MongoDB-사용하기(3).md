

# Spring Boot 에서 MongoDB 사용하기(3)

> **Spring Boot Version : 2.3.9 GA** 

  

오늘 정리할 내용은 MongoDB 를 Reactive 방식으로 연동하는 방식에 대한 예제이다. Reactive Stream으로 MongoDB를 연동해서 insert 하는 것까지가 오늘 정리하는 예제의 목표이다. [스프링 인 액션](https://ridibooks.com/books/852000830) 을 주요하게 참고했다. 이 외에 여러가지 웹 자료를 참고했는데, 대부분의 자료들이 bean 설정에 대한 자료는 많지 않아 주로 [baeldung.com - Spring Data Reactive Repositories with MongoDB](https://www.baeldung.com/spring-data-mongodb-reactive) 과 같은 사이트의 문서를 많이 참고했다.  

  

실무에서 스프링 애플리케이션에서 몽고DB를 단순히 싱글 인스턴스로 27017 로만 띄우는 경우는 단순 예제 구동인 경우 외에는 없을 것 같다는 생각이 들었었다. 이런 이유로 오늘 예제에서는 몽고DB의 포트를 27039 포트로 임의 변경한 후에 연동하는 예제를 띄워봤다.  

  

> 참고) 이번 리포지터리에서 정리하고 있는 문서들은 아래와 같다.
>
> - Spring Boot 에서 MongoDB 사용하기 (1)
>   - MongoDB 와 Spring Boot 인스턴스를 연동해서 INSERT 해보기
>   - MongoRepository, MongoTemplate을 이용한 INSERT 연산
> - Spring Boot 에서 MongoDB 사용하기 (2)
>   - MongoDB와 Spring Boot 인스턴스를 연동해서 여러가지 CRUD 실행해보기
>   - MongoRepository 를 활용한 다양한 종류의 INSERT 연산
> - Spring Boot 에서 MongoDB 사용하기 (3)
>   - 리액티브 MongoDB 사용해보기
> - Spring Boot 에서 MongoDB 사용하기 (4)
>   - 임베디드 MongoDB 로 테스트해보기

  

# 참고자료

## 메인 참고자료

- [스프링 인 액션](https://ridibooks.com/books/852000830)
  - reactive mongo db 를 연동하는 방식에 대해서 설명해주고 있다.
  - ReactiveMongoRepository 와 ReactiveCrudRepository를 사용하는 방식의 차이점에 대해서도 설명해주고 있다.
  - 리액티브 몽고디비를 구성하는 방법을 읽어보다가 몽고디비를 스프링에 연동하는것을 이 책을 보고서 먼저시작했다면... 하는 생각역시 들게 한 책
- [baeldung.com - Spring Data Reactive Repositories with MongoDB](https://www.baeldung.com/spring-data-mongodb-reactive)
  - Java config 또는 xml config 방식 역시 다루고 있다.
  - 실무에서 사용시 인스턴스 하나에 여러개의 몽고를 띄우는 경우 역시 존재할 수 있기 때문에 항상 27017 포트를 사용할 수 없는 경우 역시 존재한다. 이런 이유로 커스텀한 설정을 세팅하는 방법 역시 알아야 하는데 설정방식에 대해 설명해주고 있다. 

- [www.devglan.com - Spring Boot MongoDB configuration](https://www.devglan.com/spring-boot/spring-boot-mongodb-configuration)
  - application.yml, properties 에 connection string을 어떻게 주는지 등에 대해 언급된 자료가 없어서 조금 헤매다가 찾은 자료
- [baeldung.com - Intrduction to Spring Data MongoDB](https://www.baeldung.com/spring-data-mongodb-tutorial)
  - MongoClient 객체를 Bean으로 생성하는 방식에 대한 설명을 찾을 수 있는 곳이 없었는데, 여기에서 참고해서 예제를 작성했다.
- [everyflower.tistory.com](https://everyflower.tistory.com/222)
  - @EnableMongoRepositories에 scan 할 repository 를 `**` 과 같은 표현식으로 지정하는 방식이 확신이 가질 않아서 찾아본 자료



# build.gradle

spring-boot-starter-data-mongodb-reactive 라이브러리를 추가해주었다.

```groovy
dependencies {
  // ...
	implementation 'org.springframework.boot:spring-boot-starter-data-mongodb-reactive'
}
```



# Configuration

아래와 같이 특정 리포지터리들을 스캔하도록 하는 구문이 있다.

> @EnableMongoRepositories({"io.study.reactive_mongo_boot.**.mongo"}) 

`**`  과 같은 표현식에 대해서는 [everyflower.tistory.com](https://everyflower.tistory.com/222) 를 참고해서 적용했다.  



**MongoConfig.java**

```java
package io.study.reactive_mongo_boot.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories({"io.study.reactive_mongo_boot.**.mongo"})
public class MongoConfig extends AbstractReactiveMongoConfiguration {

	@Value("{spring.data.mongodb.uri}")
	private String mongoUri;

	@Bean
	public MongoClient mongoClient(){
		ConnectionString connectionString = new ConnectionString(mongoUri);

		MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
			.applyConnectionString(connectionString)
			.build();

		return MongoClients.create(mongoClientSettings);
	}

//	@Override
//	public MongoClient reactiveMongoClient() {
//
//		ConnectionString connectionString = new ConnectionString(mongoUri);
//
//		MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
//			.applyConnectionString(connectionString)
//			.build();
//
//		return MongoClients.create(mongoClientSettings);
//	}

	@Override
	protected String getDatabaseName() {
		return "codingtest";
	}
}
```





# application.yml

> 참고자료
>
> - [devglan.com/spring-boot-mongodb-configuration](https://www.devglan.com/spring-boot/spring-boot-mongodb-configuration)

  

프로필 `testdocker` 를  하나 새로 만들어서 적용했다.

주의할 점으로는 두 가지가 있다.

- `authentication-database` : `admin`  을 꼭 지정해주어야 Authentication 관련 익셉션이 나지 않는다는 점
- `spring.data.mongodb.uri` 를 지정했다면 `spring.data.mongodb.username|password|host|port` 는 생략해주어야 한다는 점
  - `spring.data.mongodb.uri` 를 사용한 방식과 `spring.data.mongodb.username|password|host|port` 를 사용한 방식 두 가지 중 한 가지를 선택해서 설정해야 한다는 점이다.  
  - 개인적인 의견이지만, `spring.data.mongodb.uri` 의 경우 나중에 몽고DB를 샤드클러스터 로 전환할때 uri에 여러개의 서버의 주소를 `,`로 구분지어 나열하게 된다. 여러가지 방식에서 호환되는 방식이기에 두 가지 방식 중 한가지를 선택한다면  `spring.data.mongodb.uri` 를 사용하는 방식을 선택할 것 같다.



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
#      username: mongoadmin
#      password: 1111
#      host: localhost
#      port: 27039
```



# Entity, Repository

## Employee.java

```java
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

}
```



## EmployeeRepository.java

```java
package io.study.reactive_mongo_boot.employee.mongo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface EmployeeRepository extends ReactiveMongoRepository<Employee, String> {

}
```



# Controller, Dto

## EmployeeController.java

별 내용은 없다. 코딩을 줄이기 위해 Service 계층은 생략하고, Controller내의 "/employees/insert" 요청에 대한 처리함수에서 직접 몽고 DB에 insert 를 수행하고 있다.

```java
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
```



## EmployeeDto.java

아래  Dto 클래스 내의 메서드 중에 of(Employee employee) 메서드는 아래의 역할을 한다.

- Employee 객체를 받아서 EmployeeDto 객체를 리턴하는 역할

```java
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
```



# 출력결과

## Web 출력결과

localhost/employees/insert 로 접속해보면 아래와 같은 출력결과가 나타난다.

![이미지](./img/REACTIVE_MONGODB_WEB_RESULT.png)



Database 에 직접 INSERT 된 결과를 확인해보면 아래와 같다.

![이미지](./img/REACTIVE_MONGODB_DB_RESULT.png)







