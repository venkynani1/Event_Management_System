import { useEffect, useState } from 'react'
import { Download, FileSpreadsheet } from 'lucide-react'
import { api } from '../api/client'
import type { EventItem, ReportSummary } from '../api/client'
import { downloadEventReport } from '../api/download'
import { EmptyState } from '../components/EmptyState'
import { MetricCard } from '../components/MetricCard'
import { StatusBadge } from '../components/StatusBadge'

export function ReportsPage() {
  const [events, setEvents] = useState<EventItem[]>([])
  const [selectedId, setSelectedId] = useState<number | null>(null)
  const [summary, setSummary] = useState<ReportSummary | null>(null)
  const [loadingEvents, setLoadingEvents] = useState(true)
  const [downloading, setDownloading] = useState(false)
  const [error, setError] = useState('')

  const selectedEvent = events.find((event) => event.id === selectedId) ?? null
  const loadingSummary = selectedId !== null && summary?.eventId !== selectedId && !error

  useEffect(() => {
    api.events()
      .then((items) => {
        setEvents(items)
        setSelectedId(items[0]?.id ?? null)
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoadingEvents(false))
  }, [])

  useEffect(() => {
    if (!selectedId) {
      return
    }
    api.reportSummary(selectedId)
      .then((data) => {
        setSummary(data)
        setError('')
      })
      .catch((err) => {
        setError(err.message)
      })
  }, [selectedId])

  async function downloadCsv() {
    if (!selectedEvent) return
    setDownloading(true)
    setError('')
    try {
      await downloadEventReport(selectedEvent)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Report download failed')
    } finally {
      setDownloading(false)
    }
  }

  return (
    <>
      <div className="page-header">
        <div>
          <span className="eyebrow">Exports</span>
          <h1>Reports</h1>
          <p>Review registration totals and export stage validation records for HR reporting.</p>
        </div>
        <button className="button" type="button" onClick={downloadCsv} disabled={!selectedEvent || downloading}>
          <Download size={18} />
          {downloading ? 'Preparing...' : 'Download CSV'}
        </button>
      </div>
      {error && <div className="message error">{error}</div>}
      {loadingEvents ? (
        <EmptyState title="Loading reports..." />
      ) : events.length === 0 ? (
        <EmptyState title="No events available" detail="Create an event before downloading reports." />
      ) : (
        <>
          <div className="card control-card">
            <label>
              Event
              <select value={selectedId ?? ''} onChange={(event) => setSelectedId(Number(event.target.value))}>
                {events.map((item) => (
                  <option key={item.id} value={item.id}>{item.title}</option>
                ))}
              </select>
            </label>
            {selectedEvent && (
              <div className="actions">
                <StatusBadge value={selectedEvent.status} />
                <span className="muted"><FileSpreadsheet size={16} />{selectedEvent.venue}</span>
              </div>
            )}
          </div>
          {loadingSummary ? (
            <EmptyState title="Loading report summary..." />
          ) : summary ? (
            <div className="grid metrics" style={{ marginTop: 16 }}>
              <MetricCard label="Total Registrations" value={summary.registeredCount} />
              <MetricCard label="Normal Registrations" value={summary.normalRegistrationCount} />
              <MetricCard label="Walk-ins" value={summary.walkInCount} />
              <MetricCard label="Entry Verified" value={summary.checkedInCount} />
              <MetricCard label="Pending Entry" value={summary.pendingCheckInCount} />
              {summary.enableFood && <MetricCard label="Food Claim Verified" value={summary.foodClaimedCount} />}
              {summary.enableGoodies && <MetricCard label="Goodies Claim Verified" value={summary.goodiesClaimedCount} />}
            </div>
          ) : (
            <EmptyState title="No summary available" detail="Choose another event or try again." />
          )}
        </>
      )}
    </>
  )
}
