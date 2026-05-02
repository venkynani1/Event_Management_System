# Maverick Event Control Hub

Internal HR event control system for public-link participant registration, QR-based entry validation, stage-wise food/goodies tracking, walk-ins, dashboards, and CSV reports.

## Project Structure

```text
backend/
  src/main/java/com/maverick/eventcontrolhub/
    auth/            development-only mock login for HR and scanner users
    common/          enums, errors, shared API behavior
    config/          CORS and seed data
    dashboard/       event summary aggregation
    email/           EmailService abstraction and console implementation
    employee/        mock platform users for HR/scanner access
    event/           event CRUD, public slugs, registration controls
    qr/              QR token generation and stage scan endpoints
    registration/    public normal and walk-in registration
    report/          summaries, recent activity, CSV export
frontend/
  src/
    api/             typed API client and mock session storage
    components/      shell, badges, metric cards, empty states
    pages/           HR, scanner, reports, and public registration screens
```

## PostgreSQL Local Setup

PostgreSQL is the active database for local development. Data persists after backend restarts.

Create the database:

```powershell
createdb -U postgres maverick_event_control
```

Or create it from `psql`:

```sql
CREATE DATABASE maverick_event_control;
```

The backend reads these environment variables:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/maverick_event_control"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
```

Defaults are already configured in `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/maverick_event_control}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
spring.jpa.hibernate.ddl-auto=update
```

H2 is kept only for automated tests under `backend/src/test/resources/application.properties`.

## Run Locally

Requirements:

- Java 17+
- Node.js 20+
- PostgreSQL running locally

Backend:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Frontend:

```powershell
cd frontend
npm install
npm run dev
```

Open `http://127.0.0.1:5173`. The API runs at `http://localhost:8080`.

Frontend API URL is read from `VITE_API_BASE_URL`. Copy `frontend/.env.example` to `frontend/.env` when pointing the UI at another backend:

```text
VITE_API_BASE_URL=http://localhost:8080
```

Backend CORS origins are configured with `app.cors.allowed-origins`.

## Deploy Frontend to Vercel

The frontend is a Vite React app and is ready for Vercel.

1. Import the repository in Vercel.
2. Set the project root to `frontend`.
3. Use these build settings:
   - Framework preset: `Vite`
   - Install command: `npm install`
   - Build command: `npm run build`
   - Output directory: `dist`
4. Add the environment variable:

```text
VITE_API_BASE_URL=https://YOUR_BACKEND_URL
```

5. Deploy.

`frontend/vercel.json` rewrites all routes to `index.html`, so React Router routes such as `/register/:slug`, `/walkin/:slug`, `/scanner`, and `/admin/events/:id` continue to work after a browser refresh.

After Vercel deploys, copy the deployed frontend URL and set it as `FRONTEND_ORIGIN` on the backend.

## Deploy Backend to Render, Azure, Railway, or Similar

The backend is a Spring Boot service and reads deployment configuration from environment variables.

Required environment variables:

```text
DB_URL=jdbc:postgresql://YOUR_DB_HOST:5432/YOUR_DB_NAME?sslmode=require
DB_USERNAME=YOUR_DB_USERNAME
DB_PASSWORD=YOUR_DB_PASSWORD
FRONTEND_ORIGIN=https://YOUR_VERCEL_APP.vercel.app
PORT=8080
```

Notes:

- Many hosts, including Render and Railway, inject `PORT` automatically. The app reads it through `server.port=${PORT:8080}`.
- `FRONTEND_ORIGIN` must be the exact deployed Vercel origin, without a trailing slash.
- `spring.jpa.hibernate.ddl-auto=update` is enabled for demo deployments so tables are created/updated automatically.
- Health check endpoint: `GET /api/health`.

Example Render setup:

1. Create a new Web Service.
2. Point it at the repository and set the root directory to `backend`.
3. Build command: `./mvnw package -DskipTests`
4. Start command: `java -jar target/event-control-hub-0.0.1-SNAPSHOT.jar`
5. Add the environment variables above.
6. Set the health check path to `/api/health`.

For Azure App Service or Railway, use the same build/start commands and environment variables.

## External PostgreSQL on Neon or Supabase

Create a PostgreSQL database in Neon, Supabase, or another managed PostgreSQL provider.

For Neon:

1. Create a project and database.
2. Open connection details.
3. Copy the host, database name, username, and password.
4. Build a JDBC URL like:

```text
jdbc:postgresql://HOSTNAME/DATABASE_NAME?sslmode=require
```

For Supabase:

1. Create a project.
2. Open Project Settings, then Database.
3. Copy the connection host, database name, username, password, and port.
4. Build a JDBC URL like:

```text
jdbc:postgresql://HOSTNAME:5432/postgres?sslmode=require
```

Use that JDBC URL as `DB_URL`, then set `DB_USERNAME` and `DB_PASSWORD` from the provider credentials. Keep `spring.jpa.hibernate.ddl-auto=update` for the internal demo deployment so the schema is created automatically on first boot.

## Mock Users

Mock auth is development-only and should be replaced before production SSO. Participants do not log in.

| Role | Employee code |
| --- | --- |
| HR_ADMIN | HR001 |
| SCANNER_OPERATOR | SCAN001 |

## Product Flow

1. HR logs in and creates an event.
2. HR selects validation stages: `ENTRY`, `ENTRY + FOOD`, or `ENTRY + FOOD + GOODIES`.
3. The app generates a public registration link and, when enabled, a public walk-in link/QR.
4. Participants open the public link, enter employee ID/name/email, and receive a QR pass immediately.
5. Scanner operators log in, scan or paste the QR token, choose a stage, and submit the scan.
6. The backend prevents duplicate scans for the same QR and stage.
7. HR reviews dashboard counts, recent activity, walk-ins, and CSV exports.

## API Documentation

Auth:

- `POST /api/auth/mock-login`
- `GET /api/auth/me`

Health:

- `GET /api/health`

Public registration:

- `GET /api/public/events/{registrationSlug}`
- `POST /api/public/events/{registrationSlug}/register`
- `GET /api/public/walkins/{walkinSlug}`
- `POST /api/public/walkins/{walkinSlug}/register`

Events and controls:

- `POST /api/events`
- `GET /api/events`
- `GET /api/events/{id}`
- `PUT /api/events/{id}`
- `POST /api/events/{id}/open`
- `POST /api/events/{id}/complete`
- `POST /api/events/{id}/registration/open`
- `POST /api/events/{id}/registration/close`
- `POST /api/events/{id}/registration/extend`
- `POST /api/events/{id}/walkins/open`
- `POST /api/events/{id}/walkins/close`
- `POST /api/events/{id}/walkins/extend`

Scanner:

- `POST /api/scan/validate`
- `POST /api/scan/stage`

Dashboard and reports:

- `GET /api/events/{eventId}/dashboard`
- `GET /api/events/{eventId}/reports/summary`
- `GET /api/events/{eventId}/reports/export`

## Validation Rules

- One registration per employee ID per event.
- One registration per email per event.
- One registration per browser/device token per event.
- Registration is blocked when public registration is closed or outside the registration window.
- Walk-ins are blocked when walk-ins are disabled, manually closed, or past the walk-in expiry.
- Capacity is enforced across normal registrations and walk-ins.
- QR tokens are unique and tied to one event registration.
- Scans require an active event and valid registration.
- Disabled stages cannot be scanned.
- Duplicate `ENTRY`, `FOOD`, or `GOODIES` scans are blocked per QR.

## QR Pass Usage

Participants open `/register/{registrationSlug}` or `/walkin/{walkinSlug}`, submit the form, and receive a pass with:

- event name, venue, and date/time
- participant name and employee ID
- QR image
- QR token text
- copy token and download QR buttons

The confirmation email is a placeholder: `DevelopmentEmailService` logs the email content to the backend console.

## Scanner Flow

Scanner operators open `/scanner`, use `Start Camera Scan`, or paste the QR token manually. Camera scanning works on localhost during development and HTTPS in deployed environments. If permission is denied, allow camera access in the browser site settings and start scanning again.

The scanner submits:

```json
{
  "qrToken": "token",
  "stage": "ENTRY",
  "locationName": "Gate A"
}
```

## Walk-in Flow

When walk-ins are enabled, HR can copy the walk-in link or download the walk-in QR from event details. Participants use that link, fill the same public form, and receive a participant QR. Walk-ins remain open until HR closes them or the optional walk-in close time expires.

## Reporting and Export

HR can download reports from event details or the Reports page. The CSV includes:

- employeeId, employeeName, employeeEmail
- registration source `NORMAL` or `WALKIN`
- registeredAt
- QR token
- entry status/time
- food status/time only when food is enabled
- goodies status/time only when goodies are enabled

Counts match the same dashboard data used by event details and scanner live counts.

## Future SSO and Microsoft Graph Notes

- Replace `MockAuthService` with a production principal resolver backed by company SSO.
- Keep controllers dependent on a small current-user abstraction rather than Microsoft Graph directly.
- Map SSO claims or groups to `HR_ADMIN` and `SCANNER_OPERATOR`.
- Participants can remain public-link based unless HR later requires identity verification.
- Keep `EmailService` as the email boundary; add `MicrosoftGraphEmailService` later without changing registration logic.
- Add a Graph-backed employee/profile synchronization service behind the `employee` package if HR wants validation against company directory data.

## Verification Commands

```powershell
cd backend
.\mvnw.cmd test
.\mvnw.cmd package -DskipTests

cd ..\frontend
npm run lint
npm run build
npx playwright test e2e/demo-flow.spec.ts
```

## Final Demo Script

1. Login as `HR001`.
2. Create an event with `ENTRY + FOOD + GOODIES`, capacity, registration window, and walk-ins enabled.
3. Copy the public registration link from event details.
4. Open the public registration link without logging in.
5. Register participant details: employee ID, employee name, employee email.
6. Confirm the QR pass appears and download/copy the QR token if needed.
7. Login as `SCAN001`.
8. Submit `ENTRY`; confirm duplicate `ENTRY` is blocked.
9. Submit `FOOD`; confirm duplicate `FOOD` is blocked.
10. Submit `GOODIES`.
11. Login as `HR001`.
12. Verify registered, checked-in, food, goodies, pending, and walk-in counts.
13. Open the walk-in link/QR, register a walk-in participant, and scan `ENTRY`.
14. Confirm the walk-in count updates.
15. Download the CSV from event details and from Reports.
