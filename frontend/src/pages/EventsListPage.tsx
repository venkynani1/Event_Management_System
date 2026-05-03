import { useEffect, useState } from 'react'
import { CalendarPlus, MapPin } from 'lucide-react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import type { EventItem } from '../api/client'
import { EmptyState } from '../components/EmptyState'
import { StatusBadge } from '../components/StatusBadge'

export function EventsListPage() {
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
        <div>
          <span className="eyebrow">Operations</span>
          <h1>Events</h1>
          <p>Manage registration windows, walk-in links, and stage validation readiness.</p>
        </div>
        <Link className="button" to="/admin/events/create"><CalendarPlus size={18} />Create Event</Link>
      </div>
      {error && <div className="message error">{error}</div>}
      {loading ? (
        <EmptyState title="Loading events..." />
      ) : events.length === 0 ? (
        <EmptyState title="No events available" detail="Create the first event to begin registration." />
      ) : (
        <div className="event-list">
          {events.map((event) => (
            <Link className="event-row" key={event.id} to={`/admin/events/${event.id}`}>
              <div>
                <strong>{event.title}</strong>
                <span><MapPin size={15} />{event.venue}</span>
              </div>
              <div>
                <span>{new Date(event.startTime).toLocaleString()}</span>
                <small>Capacity {event.maxCapacity}</small>
              </div>
              <div className="row-status">
                <StatusBadge value={event.status} />
                <span className={event.registrationOpen ? 'state-dot state-on' : 'state-dot'}>{event.registrationOpen ? 'Registration open' : 'Registration closed'}</span>
              </div>
            </Link>
          ))}
        </div>
      )}
    </>
  )
}
