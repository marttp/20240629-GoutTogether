# GoutTogether Backend

Example of code for Java Backend Developer Bootcamp 2024

## Related command

### Build jar and get OpenTelemetry Agent
```shell
./gradlew clean build
```

### Example curl just for generate traffic

อย่าลืมเปลี่ยน Tour ID กันด้วยนะครับ

Create Tour
```shell
curl --location 'http://localhost:8080/tours' \
--header 'Content-Type: application/json' \
--data '{
    "title": "Osaka 5 days",
    "maxPeople": 30
}'
```

Get all tour
```shell
curl --location 'http://localhost:8080/tours'
```

Get tour by id
```shell
curl --location 'http://localhost:8080/tours/1'
```

Update tour
```shell
curl --location --request PUT 'http://localhost:8080/tours/1' \
--header 'Content-Type: application/json' \
--data '{
    "title": "Play Elden Ring Party",
    "maxPeople": 4
}'
```

Delete tour
```shell
curl --location --request DELETE 'http://localhost:8080/tours/1'
```