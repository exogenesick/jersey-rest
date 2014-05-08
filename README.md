# Java REST Service application

Project contains:

* [Jersey][2] ([JAX-RS][9])
* [Jetty][3] (as [Maven plugin][10])
* [Spring][4] (IoC, Profiles)
* Redis integration with [Jedis][8] ([Spring Data][5])
* [Swagger for Jersey][6] API documentation with [Swagger UI][7]
* Integration tests with [Jersey Test Framework][11] support ([Grizzly][12] as HTTP container)

## Test (unit & integration)

    mvn test

## Usage

    mvn clean package -Dspring.profiles.active="local" jetty:run

## Demo

 * [Swagger REST API documentation][1]

[1]: http://localhost:8080
[2]: https://jersey.java.net
[3]: http://www.eclipse.org/jetty
[4]: http://spring.io/
[5]: http://projects.spring.io/spring-data-redis/
[6]: https://github.com/wordnik/swagger-core/wiki/Java-JAXRS-Quickstart
[7]: http://swagger.wordnik.com/
[8]: https://github.com/xetorthio/jedis
[9]: https://jax-rs-spec.java.net/
[10]: http://docs.codehaus.org/display/JETTY/Maven+Jetty+Plugin
[11]: https://jersey.java.net/documentation/latest/test-framework.html
[12]: https://grizzly.java.net/