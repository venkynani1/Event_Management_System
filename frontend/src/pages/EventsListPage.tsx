import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import type { EventItem } from '../api/client'
import { EmptyState } from '../components/EmptyState'
import { StatusBadge } from '../components/StatusBadge'

export function EventsListPage({ employee = false }: { employee?: boolean }) {
  const [events, setEvents] = useState<EventItem[]>([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.events()
      .then(setEvents)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  return (
    <>
      <div className="page-header">
        <h1>Events</h1>
        {!employee && <Link className="button" to="/admin/events/create">Create Event</Link>}
      </div>
      {error && <div className="message error">{error}</div>}
      {loading ? (
        <EmptyState title="Loading events..." />
      ) : events.length === 0 ? (
        <EmptyState title="No events available" detail={employee ? 'HR has not opened any events yet.' : 'Create the first event to begin registration.'} />
      ) : (
        <table className="table">
          <thead>
            <tr>
              <th>Event</th>
              <th>Venue</th>
              <th>Start</th>
              <th>Capacity</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {events.map((event) => (
              <tr key={event.id}>
                <td><Link to={employee ? `/employee/events/${event.id}` : `/admin/events/${event.id}`}>{event.title}</Link></td>
                <td>{event.venue}</td>
                <td>{new Date(event.startTime).toLocaleString()}</td>
                <td>{event.maxCapacity}</td>
                <td><StatusBadge value={event.status} /></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </>
  )
}
