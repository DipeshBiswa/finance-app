# Deploy on Render

The repository's `render.yaml` creates a Docker web service and a PostgreSQL database in the same region. The Docker build compiles React, includes its assets in the Spring Boot JAR, and runs Java 21. The frontend and API share one HTTPS origin, so there is no separate static site or production frontend URL to configure.

## First deployment

1. Commit and push these changes to the branch you want to deploy. The Blueprint and Dockerfile must be present on that remote branch.
2. In [Render](https://dashboard.render.com/), choose **New → Blueprint**, connect this repository, and select that branch. Use `render.yaml` at the repository root. Leave the root directory unset: the Git repository already contains `pom.xml` and `Dockerfile` at its root.
3. Supply `PLAID_CLIENT_ID` and the matching sandbox `PLAID_SECRET` when prompted. The Blueprint sets `PLAID_ENV=sandbox`, connects the database automatically, and generates `JWT_SECRET`.
4. Review the resources and deploy. Open the web service's generated HTTPS URL after the health check passes. `/actuator/health` should return `{"status":"UP"}`. Register a user, sign in, and refresh `/dashboard` to verify routing.
5. To enable AI chat, add `GEMINI_API_KEY` in the web service's **Environment** tab and redeploy. Without it, the app starts and chat returns HTTP 503. `GEMINI_MODEL` defaults to `gemini-2.5-flash` and can be changed in the same tab.

The Blueprint uses free plans for an initial demo. **Free Render PostgreSQL expires after 30 days and has no backups**; select a paid database plan before keeping real financial data. The free web service sleeps after inactivity. Review [Render's free-plan limits](https://render.com/docs/free) before deployment.

Use newly issued credentials: the previous application configuration included secrets in Git. Revoke or rotate any real keys and passwords previously committed. Removing them from the current files does not remove them from Git history. Never put API keys, database passwords, or JWT secrets in `VITE_*` variables, which become public browser code.

## Environment variables

| Variable | Purpose |
| --- | --- |
| `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` | Filled automatically from the Blueprint's PostgreSQL instance. |
| `DB_URL` | Optional full **JDBC** URL override, such as `jdbc:postgresql://host:5432/database`. Render's `postgresql://user:password@host/database` URL is not a JDBC URL. Keep username/password in their separate variables. |
| `JWT_SECRET` | Generated automatically by Render. For manual/local setup, use a random secret of at least 32 bytes; `openssl rand -hex 32` creates a suitable value. |
| `PLAID_CLIENT_ID`, `PLAID_SECRET` | Your Plaid credentials; supplied during Blueprint creation. |
| `PLAID_ENV` | `sandbox` by default; use `production` only with approved production credentials. |
| `GEMINI_API_KEY` | Optional; enables AI chat. |
| `GEMINI_MODEL`, `GEMINI_TEMPERATURE` | Optional model settings; defaults are `gemini-2.5-flash` and `0.7`. |
| `PORT` | Provided by Render; Spring binds to `0.0.0.0` on this port. Defaults to `8080` locally. |
| `CORS_ALLOWED_ORIGINS` | Optional comma-separated origins if you host the frontend separately. Not needed for the bundled deployment. |
| `DB_POOL_SIZE` | Maximum database connections per app instance; defaults to `5`. |
| `DDL_AUTO` | Defaults to `update` so Hibernate creates tables in a new database. For established production data, adopt reviewed database migrations before changing the schema. |

When creating a service manually instead of using the Blueprint, select the **Docker** runtime with `./Dockerfile`, add the database settings and secrets above, and set the health check path to `/actuator/health`. Do not enter a separate build or start command; the Dockerfile supplies both.

## Local development and verification

Use Java 21, Node.js 22, and a local PostgreSQL database. Copy `.env.example` to `.env`, fill in your local database password and a fresh JWT secret, and optionally add integration keys. `.env` is ignored by Git and Docker; Spring Boot does not load it automatically.

```sh
set -a
source .env
set +a
./mvnw spring-boot:run
```

In another terminal, run `npm ci` and `npm run dev` from `frontend/`. Vite forwards `/api` to the local backend. An optional `VITE_API_BASE_URL` can override `/api` for a separately hosted frontend; the standard Render image uses `/api`.

Run `./mvnw test` for isolated backend tests using an in-memory test database, and `npm run build` from `frontend/` for the browser build. With Docker running, verify the actual image:

```sh
docker build -t finance-app .
docker run --rm --env-file .env -e DB_HOST=host.docker.internal -e PORT=8080 -p 8080:8080 finance-app
```

`host.docker.internal` addresses the host's PostgreSQL from Docker Desktop. On Linux, configure a reachable database host (or add a host-gateway mapping). Database settings must point to a database dedicated to this app: startup applies Hibernate schema updates.

References: [Render Blueprints](https://render.com/docs/blueprint-spec), [Docker on Render](https://render.com/docs/docker), [health checks](https://render.com/docs/health-checks).
