import { useState } from 'react'
import type { FormEvent } from 'react'
import { BadgeCheck, LogIn, ShieldCheck } from 'lucide-react'
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
        <div className="login-brand">
          <span><ShieldCheck size={22} /></span>
          <div>
            <strong>Event Control Center</strong>
            <small>Secure event operations workspace</small>
          </div>
        </div>
        <h1>Sign in to Maverick events</h1>
        <p>Use the demo access profile for HR administration or on-site stage validation.</p>
        <div className="login-highlights">
          <span><BadgeCheck size={16} /> Public registration</span>
          <span><BadgeCheck size={16} /> Stage Validation</span>
          <span><BadgeCheck size={16} /> Live reports</span>
        </div>
        <div className="form-stack">
          <label>
            Role
            <select value={role} onChange={(event) => setRole(event.target.value as Role)}>
              <option value="HR_ADMIN">HR Admin</option>
              <option value="SCANNER_OPERATOR">Scanner Operator</option>
            </select>
          </label>
          <label>
            Employee code
            <input value={employeeCode} onChange={(event) => setEmployeeCode(event.target.value)} placeholder={role === 'HR_ADMIN' ? 'HR001' : 'SCAN001'} />
          </label>
        </div>
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
