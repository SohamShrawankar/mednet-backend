# Mednet Deployment (Public URL + SQL Persistence)

This project is ready to deploy as one backend service that serves both:
- the website (`/`)
- the API (`/api/...`)

## 1) Prepare repo

Push this code to GitHub.

## 2) Deploy on Railway (global public URL)

1. Create a Railway project.
2. Add a **MySQL** service in Railway.
3. Add a **GitHub service** and point to this repository.
4. Railway will detect the `Dockerfile` and build automatically.
5. In the app service variables, set:
   - `SPRING_DATASOURCE_URL=jdbc:mysql://<MYSQL_HOST>:<MYSQL_PORT>/<MYSQL_DB>?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
   - `SPRING_DATASOURCE_USERNAME=<MYSQL_USER>`
   - `SPRING_DATASOURCE_PASSWORD=<MYSQL_PASSWORD>`
   - `SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver`
6. Deploy. Railway gives you a public URL like:
   - `https://your-app-name.up.railway.app`

## 3) Verify DB updates from website

After deployment:
1. Open the public URL.
2. Add a patient from **Patient List -> New Patient**.
3. Refresh page and confirm row still exists.
4. Check MySQL `patients` and `prefix` tables to confirm inserts/deletes.

## 4) Auto schema updates

`spring.jpa.hibernate.ddl-auto=update` is enabled, so tables are created/updated automatically on startup.
