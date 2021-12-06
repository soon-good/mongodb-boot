# Spring Data MongoDB 의 다양한 조회쿼리들

Spring Data MongoDB에는 다양한 조회쿼리들이 있다. 오늘 문서에서는 이것에 대해 찾아본 내용들을 정리해봐야지...<br>

<br>

## 참고자료

- [A Guide to Queries in Spring Data MongoDB](https://www.baeldung.com/queries-in-spring-data-mongodb)
- [MongoDB Manual - $regex](https://docs.mongodb.com/manual/reference/operator/query/regex/)
- [querydsl 공식 리포지터리](https://github.com/querydsl/querydsl/tree/master/querydsl-mongodb)
- [Spring Data JPA Tutorial : Creating Database Queries From Method Names](https://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-creating-database-queries-from-method-names/)
  - QueryMethod 의 Naming 규칙에 대해 간단하게 나마 요약을 해주고 있다.
- [docs.spring.io - repositories.query-methods](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods)
- [Spring Data MongoDB - Guide to the @Query Annotation](https://stackabuse.com/spring-data-mongodb-guide-to-the-query-annotation/)

<br>

## 요약

여러가지 방법들이 있다.

- Query, Criteria 클래스를 사용한 쿼리

  - ex) 

    > Query = new Query(); <br>
    > query.addCriteria(Criteria.where("name").is("Stacey"));<br>List\<User\> users = mongoTemplate.find(query, User.class);<br>

- 생성되는 Query Method 를 이용한 방식

  - ex)

    > List\<Book\> books = bookRepository.findByNameLikeOrderByPriceAsc("E")

- JSON 을 인자로 받는 QueryMethod 방식 (mongodb의 쿼리와 제일 유사하게 생겼다.)

  - ex)

    > // Query 정의 <br>
    > @Query("{ 'name': { $regex: ?0 } }")<br>
    > List\<Book\> findBooksByRegexpName (String regexp);<br>
    >
    > <br>
    > // 실제 사용구문<br>
    > List<Book> books = bookRepository.findBooksByRegexpName("^E");<br>

- QueryDSL 을 사용한 쿼리 방식 

  - `QueryDslPredicatorExecutorInterface` 를 extends 해 구현하면 조건식을 and, or 로 붙여가면서 조금 더 유연하게 사용할 수 있다.

  - ex)

    > QBook book = new QBook("book");<br>
    >
    > Predicate predicate = qBook.price.between(29000, 39001);<br>
    >
    > List\<Book\> books = (List\<Book\> ) bookRepository.findAll(predicate);<br>

<br>

## 1. Query, Creteria 를 이용한 쿼리

- is 쿼리
- Regex 
- lt and gt
- Sort
- Pageable

<br>

예제에 사용할 도큐먼트

```json
[
    {
        "_id" : ObjectId("55c0e5etetet0a164a581907"),
        "_class" : "io.study.mongo.book.mongo",
        "name" : "EffectiveJava",
        "price" : 30000
    },
    {
        "_id" : ObjectId("55c0e5etetet0a164a581908"),
        "_class" : "io.study.mongo.book.mongo",
        "name" : "TestDrivenDevelopment",
        "price" : 22000
    },
    {
        "_id" : ObjectId("55c0e5etetet0a164a581909"),
        "_class" : "io.study.mongo.book.mongo",
        "name" : "EffectiveCPP",
        "price" : 39000
    }
}
```

<br>

### is 쿼리

ex)

```java
Query query = new Query();
query.addCriteria(Criteria.where("name").is("EffectiveJava"));
List<Book> books = mongoTemplate.find(query, Book.class);
```

<br>

### Regex 

- Mongodb의 regex 표현식을 사용하는 방식. mongodb 에서 regex를 사용하는 방식에 대해서는 [공식문서](https://docs.mongodb.com/manual/reference/operator/query/regex/) 에서 찾아볼 수 있다.<br>

```java
Query query = new Query();
query.addCriteria(Criteria.where("name").regex("^E"));
List<Book> books = mongoTemplate.find(query, Book.class);
```

<br>

### lt, gt

ex)

```java
Query query = new Query();
query.addCriteria(Criteria.where("price").gt(29000).lt(39001));
List<Book> books = mongoTemplate.find(query, Book.class);
```

<br>

### Sort

ex)

```java
Query query = new Query();
query.with(Sort.by(Sort.Direction.ASC, "price"));
List<Book> books = mongoTemplate.find(query, Book.class);
```

<br>

### Pageable

ex)

- 페이징 사이즈 : 2
- 읽어올 페이지 번호 : 0

```java
final Pageable pageableRequest = PageRequest.of(0,2);
Query query = new Query();
query.with(pageRequest);
```

<br>

## 2. 쿼리 메서드 이용 (Generated Query Methods)

JPA에서는 JpaRepository 인터페이스를 상속하면, 쿼리 생성 기능을 리포지터리 인터페이스에서 사용할 수 있었다. Spring data MongoDB에서도 이런 기능을 사용할 수 있다. 이것을 흔히 QueryMethod 라고 부르는 편이다.(스프링 진영에서만...)<br>

```java
public interface BookRepository extends MongoRepository<Book, String>, QueryDslPredicateExecutor<Book>{
  // ...
}
```

<br>

- findByAAA()
- startingWith, endingWith
- between 
- like, orderBy

### findByAAA()

ex)

```java
public interface BookRepository extends MongoRepository<Book, String>, QueryDslPredicateExecutor<Book>{
  List<Book> findByName(String name);
}
```

이렇게 정의한 쿼리 메서드는 아래와 같이 사용가능하다.

```java
List<Book> books = bookRepository.findByName("EffectiveJava");
```

<br>

### startingWith, endingWith

ex)

```java
public interface BookRepository extends MongoRepository<Book, String>, QueryDslPredicateExecutor<Book>{
  // ...
  List<Book> findByNameStartingWith(String regexp);
  List<Book> findByNameEndingWith(String regexp);
}
```

이렇게 정의한 쿼리 메서드는 아래와 같이 사용가능하다.

```java
List<Book> books1 = bookRepository.findByNameStartingWith("E");
List<Book> books2 = bookRepository.findByNameEndingWith("A");
```

<br>

### between

ex)

```java
public interface BookRepository extends MongoRepository<Book, String>, QueryDslPredicateExecutor<Book>{
  // ...
  List<Book> findByPriceBetween(int priceGt, int priceLt);
}
```

쿼리메서드를 사용해보자.

```java
List<Book> books = bookRepository.findByPriceBetween(20001, 39001);
```

<br>

### like, orderBy

```java
public interface BookRepository extends MongoRepository<Book, String>, QueryDslPredicateExecutor<Book>{
  // ...
  List<Book> findByNameLikeOrderByPriceAsc("E");
}
```

이렇게 정의한 쿼리를 사용해보면 아래와 같다.

```java
List<Book> books = bookRepository.findByNameLikeOrderByPriceAsc("E");
```

<br>

## 3. JSON Query Method

- findBy
- `$regex` 
- `$lt`  , `$gt` 

### findBy

쿼리 정의

```java
public interface BookRepository extends MongoRepository<Book, String>, QueryDslPredicateExecutor<Book>{
	// ...
  @Query("{ 'name' : ?0 }")
  List<Book> findBooksByName(String name);
}
```

쿼리 사용

```java
List<Book> books = bookRepository.findBooksByName("EffectiveJava");
```

<br>

### $regex

쿼리 정의

```java
public interface BookRepository extends MongoRepository<Book, String>, QueryDslPredicateExecutor<Book>{
  // ...
  @Query("{ 'name' : {$regex: ?0}}")
  List<Book> findBooksByRegexpName(String regexp);
}
```

쿼리 사용

```java
List<Book> books1 = bookRepository.findBooksByRegexpName("^E");
List<Book> books2 = bookRepository.findBooksByRegexpName("$P");
```

<br>

### $lt, $gt

쿼리 정의

```java
@Query("{ 'price': { $gt: ?0, $lt: ?1 }}")
List<Book> findBooksByPriceBetween(int ageGt, int ageLt);
```

쿼리 사용

```java
List<Book> books = bookRepository.findBooksByPriceBetween(29000, 39001);
```

<br>

## 4. QueryDsl Query

spring data mongodb 에서 까지 굳이 querydsl 을?  하는 생각도 들기는 한다. 그냥 일단 정리해봤다.

### maven 의존성

```xml
<dependency>
    <groupId>com.mysema.querydsl</groupId>
    <artifactId>querydsl-mongodb</artifactId>
    <version>4.3.1</version>
</dependency>
<dependency>
    <groupId>com.mysema.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
    <version>4.3.1</version>
</dependency>
```

<br>

### @QueryEntity

```java
@QueryEntity
@Document
public class Book{
  @Id
  private String id;
  private String name;
  private Double price;
  
  // getter, setter
  
}
```

<br>

### Repository 생성

```java
public interface BookRepository extends MongoRepository<Book, String> QuerydslPredicateExecutor<Book>{
}
```

<br>

오늘 정리할 메서드는 아래의 3가지다.

- eq
- startingWith, endingWith
- between

<br>

### eq

ex) 

```java
QBook qBook = new QBook("book");
Predicate predicate = qBook.name.eq("EffetiveJava");
List<Book> books = (List<Book>) bookRepository.findAll(predicate);
```

<br>

### startingWith, endingWith

ex 1) startingWith

```java
QBook qBook = new QBook("book");
Predicate predicate = qBook.name.startsWith("E");
List<Book> books = (List<Book>) bookRepository.findAll(predicate);
```

<br>

ex 2) endingWith

```java
QBook qBook = new QBook("book");
Predicate predicate = qBook.name.endWith("a");
List<Book> books = (List<Book>) bookRepository.findAll(predicate);
```

<br>

### between

```java
QBook qBook = new QBook("book");
Predicate predicate = qBook.price.between(29000, 39001);
List<Book> books = (List<Book>) bookRepository.findAll(predicate);
```

<br>

## QueryMethod 의 일반적인 네이밍 규칙

https://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-creating-database-queries-from-method-names/













