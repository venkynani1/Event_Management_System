import { useEffect, useState } from 'react'
import { ArrowRight, CalendarPlus, CheckCircle2, ClipboardList, TicketCheck, UsersRound } from 'lucide-react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import type { DashboardSummary, EventItem } from '../api/client'
import { EmptyState } from '../components/EmptyState'
import { MetricCard } from '../components/MetricCard'

export function AdminDashboardPage() {
  const [events, setEvents] = useState<EventItem[]>([])
  const [selectedId, setSelectedId] = useState<number | null>(null)
  const [summary, setSummary] = useState<DashboardSummary | null>(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.events()
      .then((items) => {
        setEvents(items)
        setSelectedId(items[0]?.id ?? null)
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    if (!selectedId) return
    api.dashboard(selectedId)
      .then(setSummary)
      .catch((err) => setError(err.message))
  }, [selectedId])

  return (
    <>
      <div className="page-header">
        <div>
          <span className="eyebrow">Event Control Center</span>
          <h1>Dashboard</h1>
          <p>Monitor registration demand, walk-in access, and stage validation across active events.</p>
        </div>
        <Link className="button" to="/admin/events/create"><CalendarPlus size={18} />Create Event</Link>
      </div>
      {error && <div className="message error">{error}</div>}
      {loading ? (
        <EmptyState title="Loading dashboard..." />
      ) : events.length === 0 ? (
        <EmptyState title="No events to summarize" detail="Create an event to see dashboard metrics." />
      ) : (
        <div className="card control-card">
          <label>
            Event
            <select value={selectedId ?? ''} onChange={(event) => setSelectedId(Number(event.target.value))}>
              {events.map((item) => <option key={item.id} value={item.id}>{item.title}</option>)}
            </select>
          </label>
          {selectedId && <Link className="ghost-button" to={`/admin/events/${selectedId}`}>Open event <ArrowRight size={18} /></Link>}
        </div>
      )}
      {summary && (
        <div className="grid metrics" style={{ marginTop: 16 }}>
          <MetricCard label="Total Events" value={summary.totalEvents} icon={<ClipboardList size={18} />} />
          <MetricCard label="Total Registrations" value={summary.registeredCount} icon={<UsersRound size={18} />} />
          <MetricCard label="Normal Registrations" value={summary.normalRegistrationCount} />
          <MetricCard label="Walk-ins" value={summary.walkInCount} />
          <MetricCard label="Entry Verified" value={summary.checkedInCount} icon={<TicketCheck size={18} />} />
          <MetricCard label="Pending Entry" value={summary.pendingCheckInCount} />
          {summary.enableFood && <MetricCard label="Food Claim Verified" value={summary.foodClaimedCount} icon={<CheckCircle2 size={18} />} />}
          {summary.enableGoodies && <MetricCard label="Goodies Claim Verified" value={summary.goodiesClaimedCount} icon={<CheckCircle2 size={18} />} />}
        </div>
      )}
    </>
  )
}
