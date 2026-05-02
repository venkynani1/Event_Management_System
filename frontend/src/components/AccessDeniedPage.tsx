import { ShieldAlert } from 'lucide-react'
import { Link } from 'react-router-dom'
import type { Employee } from '../api/client'

export function AccessDeniedPage({ user }: { user: Employee }) {
  return (
    <div className="card access-denied">
      <ShieldAlert size={36} />
      <div>
        <h1>Access denied</h1>
        <p>Your mock account is signed in as {user.employeeCode}. This route is not available for that role.</p>
      </div>
      <Link className="button" to="/">Go to my home</Link>
    </div>
  )
}
