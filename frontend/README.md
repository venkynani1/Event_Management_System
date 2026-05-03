# Event Control Center Frontend

Vite React frontend for Maverick Event Control Center.

## Local Development

```powershell
npm install
npm run dev
```

Set the API base URL in `.env` when needed:

```text
VITE_API_BASE_URL=http://localhost:8080
```

## Vercel

Vercel builds the `dist` output. Keep `vercel.json` rewrites in place so React Router routes such as `/register/:slug`, `/walkin/:slug`, `/scanner`, and `/admin/events/:id` load directly after browser refresh.
