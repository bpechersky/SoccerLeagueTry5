# Soccer League Fullstack

- Backend: Spring Boot + H2 (port 8080)
- Frontend: Vite + React (port 5173)

## Quick Start
1) Backend
```bash
cd backend
mvn spring-boot:run
```

2) Frontend
```bash
cd ../frontend
npm install
npm run dev
```

## Features
- CRUD: Teams, Players, Matches
- Standings computed on the backend from all matches
- CORS enabled for localhost dev
- Seed data provided in `data.sql`
