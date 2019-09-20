# Redis Cache Fallback Example
This Spring Boot project solves the problem of caching while Redis is not highly available.

## Initial Situation
Spring Boot supports the concept of caching by annotating a method with `@Cacheable`. The following
code is a snippet from `ItemCacheService.java` that handles caching by Spring. 

```
@Cacheable(value = "itemCache", key = "#id")
public Item getItemById(int id) {
  log.info("getItem by Id [{}]", id);
  Item item = null;
  try {
    item = testDataService.getItem(id);
  } catch (Exception e) {
    log.error("Error while fetching item [{}], {}", id, e);
  }
  return item;
}
``` 

If a request is made the result gets automatically stored in the Redis Cache `itemCache`. The 
entry is a Key-Value pair consisting of the returned object as value and a hashed key which is the
`#id` value in the upper example (the hash can also be made of the whole request object).

For future calls the request hash gets compared with Redis Cache returning the cached value in 
case of a match. This behaviour can be reproduced by checking the log because the code in line 
`log.info("getItem by Id [{}]", id);` does not get executed for cached values.

## Challenge
With the implementation of the initial situation a problem came up. Redis was not configured to be 
high available which led to `RedisConnectionFailureExceptions`. 

Since the real database was still available the application should load data from the database 
instead of insisting to return cached data. 

## Solution
This project provides a fallback solution for returning cached data with a missing Redis connection.

A second service layer catches the `RedisConnectionFailureException` and calls the backend directly
(`testDataService.getItem(id)`) instead of letting Spring handle the Caching itself (see code 
snippet above).

```
Item getItemById(int id) {
  Item item;
  try {
    item = itemCacheService.getItemById(id);
  } catch (RedisConnectionFailureException e) {
    log.warn("No Redis Connection while fetching item [{}]", id);
    item = testDataService.getItem(id);
  }
  return item;
}
```

## Reproduce Behaviour

Run Application:
```shell
$ docker run -d -p 6379:6379 --name redis redis
$ mvn clean install
$ mvn spring-boot:run 
```

Perform REST calls:
```shell
# Request Hardcoded Item
$ curl -X GET http://localhost:8082/item/8          
# INFO: de.adorsys.swi.example.ItemCacheService : getItem by Id [8]
{"id":8,"name":"Kalte Asche","category":"Books"}

$ curl -X GET http://localhost:8082/item/8          
{"id":8,"name":"Kalte Asche","category":"Books"}

# Kill Database Connection
$ docker stop redis && docker rm redis

# Request Cached Item Without Connection
$ curl -X GET http://localhost:8082/item/8          
# WARN:  de.adorsys.swi.example.ItemService  : No Redis Connection while fetching item [8]
```

## Prometheus Configuration

Prometheus is used to request metrics of this application. To keep its responses as light as 
possible all `tomcat` metrics will be suppressed.

Furthermore, it is possible to filter specific metrics by adding their names in 
`application.properties` files using the key `management.custom.exclude`.

IMPORTANT: Prometheus returns key value pairs in Snake case (e.g. `system_cpu_usage 8.0`).
To suppress a value the application needs keys separated with `.` instead of `_` (e.g. 
`system.cpu.usage`).

Example:
```
management.custom.exclude=process.files.open.files,system.cpu.count,system.cpu.usage
``` 