import { CalendarDays, ClipboardList, LayoutDashboard, LogOut, QrCode, ScanLine } from 'lucide-react'
import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { clearSession } from '../api/client'
import type { Employee } from '../api/client'

type Props = {
  user: Employee
  onLogout: () => void
}

const adminLinks = [
  { to: '/admin/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/admin/events', label: 'Events', icon: CalendarDays },
  { to: '/reports', label: 'Reports', icon: ClipboardList },
]

const scannerLinks = [
  { to: '/scanner', label: 'Scanner', icon: ScanLine },
]

export function AppLayout({ user, onLogout }: Props) {
  const navigate = useNavigate()
  const links = user.role === 'HR_ADMIN' ? adminLinks : scannerLinks

  function logout() {
    clearSession()
    onLogout()
    navigate('/login')
  }

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <QrCode size={26} />
          <div>
            <strong>Maverick</strong>
            <span>Event Control Hub</span>
          </div>
        </div>
        <nav>
          {links.map(({ to, label, icon: Icon }) => (
            <NavLink key={to} to={to} className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
              <Icon size={18} />
              {label}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-footer">
          <div className="user-chip">
            <strong>{user.name}</strong>
            <span>{user.role.replace('_', ' ')}</span>
          </div>
          <button className="icon-button" onClick={logout} title="Log out">
            <LogOut size={18} />
          </button>
        </div>
      </aside>
      <main className="content">
        <Outlet />
      </main>
    </div>
  )
}
