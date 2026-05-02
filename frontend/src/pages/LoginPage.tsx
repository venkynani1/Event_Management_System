import { useState } from 'react'
import type { FormEvent } from 'react'
import { LogIn } from 'lucide-react'
import { api, setSession } from '../api/client'
import type { Employee, Role } from '../api/client'

type Props = {
  onLogin: (user: Employee) => void
}

export function LoginPage({ onLogin }: Props) {
  const [role, setRole] = useState<Role>('HR_ADMIN')
  const [employeeCode, setEmployeeCode] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function submit(event: FormEvent) {
    event.preventDefault()
    setError('')
    setLoading(true)
    try {
      const response = await api.mockLogin(role, employeeCode)
      setSession(response.user, response.mockToken)
      onLogin(response.user)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-page">
      <form className="login-panel" onSubmit={submit}>
        <h1>Maverick Event Control Hub</h1>
        <p>Development-only mock access</p>
        <label>
          Role
          <select value={role} onChange={(event) => setRole(event.target.value as Role)}>
            <option value="HR_ADMIN">HR Admin</option>
            <option value="SCANNER_OPERATOR">Scanner Operator</option>
          </select>
        </label>
        <label>
          Employee code
          <input value={employeeCode} onChange={(event) => setEmployeeCode(event.target.value)} placeholder="Optional" />
        </label>
        {error && <div className="message error">{error}</div>}
        <div className="actions">
          <button className="button" type="submit" disabled={loading}>
            <LogIn size={18} />
            {loading ? 'Signing in...' : 'Login'}
          </button>
        </div>
      </form>
    </div>
  )
}
