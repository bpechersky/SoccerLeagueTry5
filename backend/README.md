# Soccer League Backend

**Tech**: Spring Boot 3, Java 17, H2, JPA, Validation

### Run
```bash
cd backend
mvn spring-boot:run
```
H2 console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:soccerdb`)

### API
- `GET/POST/PUT/DELETE /api/teams`
- `GET/POST/PUT/DELETE /api/players`
- `GET /api/players/team/{teamId}`
- `GET/POST/PUT/DELETE /api/matches`
- `GET /api/standings`
