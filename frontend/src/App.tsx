import { useState } from 'react'
import type { ReactElement } from 'react'
import { Navigate, Route, Routes } from 'react-router-dom'
import { getStoredUser } from './api/client'
import type { Employee, Role } from './api/client'
import { AccessDeniedPage } from './components/AccessDeniedPage'
import { AppLayout } from './components/AppLayout'
import { AdminDashboardPage } from './pages/AdminDashboardPage'
import { AdminEventDetailPage } from './pages/AdminEventDetailPage'
import { CreateEventPage } from './pages/CreateEventPage'
import { EventsListPage } from './pages/EventsListPage'
import { LoginPage } from './pages/LoginPage'
import { PublicRegistrationPage } from './pages/PublicRegistrationPage'
import { ReportsPage } from './pages/ReportsPage'
import { ScannerPage } from './pages/ScannerPage'

function roleHome(user: Employee | null) {
  if (!user) return '/login'
  if (user.role === 'HR_ADMIN') return '/admin/dashboard'
  if (user.role === 'SCANNER_OPERATOR') return '/scanner'
  return '/login'
}

function canAccess(user: Employee, roles: Role[]) {
  if (!roles.includes(user.role)) return false
  if (user.role === 'HR_ADMIN') return user.employeeCode === 'HR001'
  if (user.role === 'SCANNER_OPERATOR') return user.employeeCode === 'SCAN001'
  if (user.role === 'EMPLOYEE') return user.employeeCode.startsWith('EMP')
  return false
}

function Protected({ user, onLogout }: { user: Employee | null; onLogout: () => void }) {
  if (!user) return <Navigate to="/login" replace />
  return <AppLayout user={user} onLogout={onLogout} />
}

function RequireRoute({ user, roles, children }: { user: Employee | null; roles: Role[]; children: ReactElement }) {
  if (!user) return <Navigate to="/login" replace />
  if (!canAccess(user, roles)) return <AccessDeniedPage user={user} />
  return children
}

export default function App() {
  const [user, setUser] = useState<Employee | null>(getStoredUser())

  return (
    <Routes>
      <Route path="/login" element={user && roleHome(user) !== '/login' ? <Navigate to={roleHome(user)} replace /> : <LoginPage onLogin={setUser} />} />
      <Route path="/register/:registrationSlug" element={<PublicRegistrationPage />} />
      <Route path="/walkin/:walkinSlug" element={<PublicRegistrationPage walkin />} />
      <Route element={<Protected user={user} onLogout={() => setUser(null)} />}>
        <Route path="/admin/dashboard" element={<RequireRoute user={user} roles={['HR_ADMIN']}><AdminDashboardPage /></RequireRoute>} />
        <Route path="/admin/events" element={<RequireRoute user={user} roles={['HR_ADMIN']}><EventsListPage /></RequireRoute>} />
        <Route path="/admin/events/create" element={<RequireRoute user={user} roles={['HR_ADMIN']}><CreateEventPage /></RequireRoute>} />
        <Route path="/admin/events/:id" element={<RequireRoute user={user} roles={['HR_ADMIN']}><AdminEventDetailPage /></RequireRoute>} />
        <Route path="/scanner" element={<RequireRoute user={user} roles={['SCANNER_OPERATOR']}><ScannerPage /></RequireRoute>} />
        <Route path="/reports" element={<RequireRoute user={user} roles={['HR_ADMIN']}><ReportsPage /></RequireRoute>} />
      </Route>
      <Route path="*" element={<Navigate to={roleHome(user)} replace />} />
    </Routes>
  )
}
