export type Role = 'HR_ADMIN' | 'EMPLOYEE' | 'SCANNER_OPERATOR'
export type EventStatus = 'DRAFT' | 'OPEN' | 'LIVE' | 'COMPLETED' | 'CANCELLED'
export type Stage = 'ENTRY' | 'FOOD' | 'GOODIES'

export type Employee = {
  id: number
  employeeCode: string
  name: string
  email: string
  department: string
  role: Role
}

export type EventItem = {
  id: number
  title: string
  description: string
  venue: string
  startTime: string
  endTime: string
  registrationOpenAt: string
  registrationCloseAt: string
  registrationOpen: boolean
  maxCapacity: number
  walkinAllowed: boolean
  walkinOpen: boolean
  walkinCloseAt: string | null
  enableEntry: boolean
  enableFood: boolean
  enableGoodies: boolean
  registrationSlug: string
  walkinSlug: string | null
  status: EventStatus
}

export type Registration = {
  id: number
  eventId: number
  employeeId: string
  employeeName: string
  employeeEmail: string
  status: 'REGISTERED' | 'CANCELLED' | 'WAITLISTED' | 'WALKIN'
  source: 'NORMAL' | 'WALKIN'
  qrToken: string
  deviceToken: string
  registeredAt: string
}

export type DashboardSummary = {
  totalEvents: number
  eventId: number
  eventTitle: string
  registeredCount: number
  normalRegistrationCount: number
  checkedInCount: number
  foodClaimedCount: number
  goodiesClaimedCount: number
  walkInCount: number
  pendingCheckInCount: number
  registrationOpen: boolean
  walkinOpen: boolean
  enableEntry: boolean
  enableFood: boolean
  enableGoodies: boolean
}

export type RecentActivity = {
  type: string
  message: string
  actor: string
  occurredAt: string
}

export type ReportSummary = DashboardSummary & {
  recentActivity: RecentActivity[]
}

export type ScanValidation = {
  valid: boolean
  eventId: number
  eventTitle: string
  registrationId: number
  employeeName: string
  employeeId: string
  registrationStatus: string
  entryScanned: boolean
  foodScanned: boolean
  goodiesScanned: boolean
  foodEnabled: boolean
  goodiesEnabled: boolean
  message: string
}

export type PublicEvent = {
  id: number
  title: string
  description: string
  venue: string
  startTime: string
  endTime: string
  registrationOpen: boolean
  walkinOpen: boolean
  walkinAllowed: boolean
  enableEntry: boolean
  enableFood: boolean
  enableGoodies: boolean
}

export type PublicRegistrationPayload = {
  employeeId: string
  employeeName: string
  employeeEmail: string
  deviceToken: string
}

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'
const AUTH_KEY = 'mech.mockToken'
const USER_KEY = 'mech.user'

function friendlyError(message: string) {
  if (/capacity/i.test(message)) return 'Capacity reached. Registration is no longer available for this event.'
  if (/registration.*closed|not open|outside/i.test(message)) return 'Registration closed for this event.'
  if (/already used|duplicate/i.test(message)) return 'Duplicate scan blocked.'
  return message
}

export function getMockToken() {
  return localStorage.getItem(AUTH_KEY)
}

export function setSession(user: Employee, mockToken: string) {
  localStorage.setItem(AUTH_KEY, mockToken)
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function clearSession() {
  localStorage.removeItem(AUTH_KEY)
  localStorage.removeItem(USER_KEY)
}

export function getStoredUser(): Employee | null {
  const raw = localStorage.getItem(USER_KEY)
  return raw ? JSON.parse(raw) as Employee : null
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers)
  headers.set('Content-Type', 'application/json')
  const mockToken = getMockToken()
  if (mockToken) headers.set('X-Employee-Code', mockToken)

  const response = await fetch(`${API_BASE}${path}`, { ...options, headers })
  if (!response.ok) {
    const body = await response.json().catch(() => ({ message: response.statusText }))
    if (response.status === 401) {
      throw new Error(body.message ?? 'Your session is missing or expired. Please log in again.')
    }
    if (response.status === 403) {
      throw new Error(body.message ?? 'You do not have permission to perform this action.')
    }
    throw new Error(friendlyError(body.message ?? 'Request failed'))
  }
  return response.json() as Promise<T>
}

async function requestBlob(path: string): Promise<Blob> {
  const headers = new Headers()
  const mockToken = getMockToken()
  if (mockToken) headers.set('X-Employee-Code', mockToken)

  const response = await fetch(`${API_BASE}${path}`, { headers })
  if (!response.ok) {
    const body = await response.json().catch(() => ({ message: response.statusText }))
    if (response.status === 401) {
      throw new Error(body.message ?? 'Your session is missing or expired. Please log in again.')
    }
    if (response.status === 403) {
      throw new Error(body.message ?? 'You do not have permission to download this report.')
    }
    throw new Error(friendlyError(body.message ?? 'Request failed'))
  }
  return response.blob()
}

export const api = {
  mockLogin: (role: Role, employeeCode?: string) =>
    request<{ user: Employee; mockToken: string; headerName: string }>('/api/auth/mock-login', {
      method: 'POST',
      body: JSON.stringify({ role, employeeCode: employeeCode || null }),
    }),
  me: () => request<Employee>('/api/auth/me'),
  events: () => request<EventItem[]>('/api/events'),
  event: (id: number) => request<EventItem>(`/api/events/${id}`),
  createEvent: (payload: Omit<EventItem, 'id' | 'status' | 'registrationSlug' | 'walkinSlug'>) =>
    request<EventItem>('/api/events', { method: 'POST', body: JSON.stringify(payload) }),
  openEvent: (id: number) => request<EventItem>(`/api/events/${id}/open`, { method: 'POST' }),
  completeEvent: (id: number) => request<EventItem>(`/api/events/${id}/complete`, { method: 'POST' }),
  registrations: (eventId: number) => request<Registration[]>(`/api/events/${eventId}/registrations`),
  openRegistration: (eventId: number) => request<EventItem>(`/api/events/${eventId}/registration/open`, { method: 'POST' }),
  closeRegistration: (eventId: number) => request<EventItem>(`/api/events/${eventId}/registration/close`, { method: 'POST' }),
  extendRegistration: (eventId: number, closeAt: string) =>
    request<EventItem>(`/api/events/${eventId}/registration/extend`, { method: 'POST', body: JSON.stringify({ closeAt }) }),
  openWalkins: (eventId: number) => request<EventItem>(`/api/events/${eventId}/walkins/open`, { method: 'POST' }),
  closeWalkins: (eventId: number) => request<EventItem>(`/api/events/${eventId}/walkins/close`, { method: 'POST' }),
  extendWalkins: (eventId: number, closeAt: string) =>
    request<EventItem>(`/api/events/${eventId}/walkins/extend`, { method: 'POST', body: JSON.stringify({ closeAt }) }),
  publicEvent: (slug: string) => request<PublicEvent>(`/api/public/events/${slug}`),
  registerPublic: (slug: string, payload: PublicRegistrationPayload) =>
    request<Registration>(`/api/public/events/${slug}/register`, { method: 'POST', body: JSON.stringify(payload) }),
  publicWalkinEvent: (slug: string) => request<PublicEvent>(`/api/public/walkins/${slug}`),
  registerWalkinPublic: (slug: string, payload: PublicRegistrationPayload) =>
    request<Registration>(`/api/public/walkins/${slug}/register`, { method: 'POST', body: JSON.stringify(payload) }),
  dashboard: (eventId: number) => request<DashboardSummary>(`/api/events/${eventId}/dashboard`),
  reportSummary: (eventId: number) => request<ReportSummary>(`/api/events/${eventId}/reports/summary`),
  exportReport: (eventId: number) => requestBlob(`/api/events/${eventId}/reports/export`),
  validateScan: (qrToken: string) =>
    request<ScanValidation>('/api/scan/validate', { method: 'POST', body: JSON.stringify({ qrToken }) }),
  scanStage: (qrToken: string, stage: Stage, locationName: string) =>
    request('/api/scan/stage', { method: 'POST', body: JSON.stringify({ qrToken, stage, locationName }) }),
}
