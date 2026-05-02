import { useEffect, useState } from 'react'
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
        <h1>Dashboard</h1>
        <Link className="button" to="/admin/events/create">Create Event</Link>
      </div>
      {error && <div className="message error">{error}</div>}
      {loading ? (
        <EmptyState title="Loading dashboard..." />
      ) : events.length === 0 ? (
        <EmptyState title="No events to summarize" detail="Create an event to see dashboard metrics." />
      ) : (
        <div className="card">
          <label>
            Event
            <select value={selectedId ?? ''} onChange={(event) => setSelectedId(Number(event.target.value))}>
              {events.map((item) => <option key={item.id} value={item.id}>{item.title}</option>)}
            </select>
          </label>
        </div>
      )}
      {summary && (
        <div className="grid metrics" style={{ marginTop: 16 }}>
          <MetricCard label="Total events" value={summary.totalEvents} />
          <MetricCard label="Registered" value={summary.registeredCount} />
          <MetricCard label="Normal registrations" value={summary.normalRegistrationCount} />
          <MetricCard label="Walk-ins" value={summary.walkInCount} />
          <MetricCard label="Checked in" value={summary.checkedInCount} />
          <MetricCard label="Pending check-ins" value={summary.pendingCheckInCount} />
          {summary.enableFood && <MetricCard label="Food claimed" value={summary.foodClaimedCount} />}
          {summary.enableGoodies && <MetricCard label="Goodies claimed" value={summary.goodiesClaimedCount} />}
        </div>
      )}
    </>
  )
}
