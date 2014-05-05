# REST Service

Project contains:

* Jersey
* Jetty (as Maven plugin)
* Spring
* Spring Profiles
* Jedis (Spring Data)

## Usage

    mvn clean package -Dspring.profiles.active="local" jetty:run