import { useEffect, useState } from 'react'
import { QRCodeCanvas } from 'qrcode.react'
import { CheckCircle2, Clipboard, Download, PauseCircle, PlayCircle } from 'lucide-react'
import { useParams } from 'react-router-dom'
import { api } from '../api/client'
import type { EventItem, Registration, ReportSummary } from '../api/client'
import { downloadEventReport } from '../api/download'
import { EmptyState } from '../components/EmptyState'
import { MetricCard } from '../components/MetricCard'
import { StatusBadge } from '../components/StatusBadge'

async function fetchEventDetail(id: number) {
  const [eventData, summaryData, registrationsData] = await Promise.all([
    api.event(id),
    api.reportSummary(id),
    api.registrations(id),
  ])
  return { eventData, summaryData, registrationsData }
}

function publicUrl(path: string) {
  return `${window.location.origin}${path}`
}

export function AdminEventDetailPage() {
  const id = Number(useParams().id)
  const [event, setEvent] = useState<EventItem | null>(null)
  const [summary, setSummary] = useState<ReportSummary | null>(null)
  const [registrations, setRegistrations] = useState<Registration[]>([])
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [busy, setBusy] = useState('')
  const [registrationCloseAt, setRegistrationCloseAt] = useState('')
  const [walkinCloseAt, setWalkinCloseAt] = useState('')

  async function load() {
    const { eventData, summaryData, registrationsData } = await fetchEventDetail(id)
    setEvent(eventData)
    setSummary(summaryData)
    setRegistrations(registrationsData)
    setRegistrationCloseAt(eventData.registrationCloseAt.slice(0, 16))
    setWalkinCloseAt(eventData.walkinCloseAt?.slice(0, 16) ?? '')
  }

  useEffect(() => {
    fetchEventDetail(id)
      .then(({ eventData, summaryData, registrationsData }) => {
        setEvent(eventData)
        setSummary(summaryData)
        setRegistrations(registrationsData)
        setRegistrationCloseAt(eventData.registrationCloseAt.slice(0, 16))
        setWalkinCloseAt(eventData.walkinCloseAt?.slice(0, 16) ?? '')
      })
      .catch((err) => setError(err.message))
  }, [id])

  async function run(action: string, work: () => Promise<unknown>, success: string) {
    setError('')
    setMessage('')
    setBusy(action)
    try {
      await work()
      await load()
      setMessage(success)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Action failed')
    } finally {
      setBusy('')
    }
  }

  async function copy(value: string, label: string) {
    try {
      await navigator.clipboard.writeText(value)
      setMessage(`${label} copied.`)
    } catch {
      setMessage('Copy is unavailable in this browser. Select and copy the link manually.')
    }
  }

  async function downloadReport() {
    if (!event) return
    await run('download', () => downloadEventReport(event), 'Report download started.')
  }

  function downloadWalkinQr() {
    if (!event?.walkinSlug) return
    const canvas = document.getElementById('walkin-qr') as HTMLCanvasElement | null
    if (!canvas) return
    const link = document.createElement('a')
    link.download = `${event.title.toLowerCase().replace(/[^a-z0-9]+/g, '-')}-walkin-qr.png`
    link.href = canvas.toDataURL('image/png')
    link.click()
  }

  if (!event) return error ? <div className="message error">{error}</div> : <EmptyState title="Loading event..." />

  const registrationLink = publicUrl(`/register/${event.registrationSlug}`)
  const walkinLink = event.walkinSlug ? publicUrl(`/walkin/${event.walkinSlug}`) : ''

  return (
    <>
      <div className="page-header">
        <div>
          <h1>{event.title}</h1>
          <div className="actions">
            <StatusBadge value={event.status} />
            <span className="badge">ENTRY{event.enableFood ? ' + FOOD' : ''}{event.enableGoodies ? ' + GOODIES' : ''}</span>
          </div>
        </div>
        <div className="actions">
          <button className="button" onClick={() => {
            if (window.confirm('Complete this event? This closes public registration and walk-ins.')) {
              void run('complete', () => api.completeEvent(id), 'Event completed.')
            }
          }} disabled={busy === 'complete'}>
            <CheckCircle2 size={18} />{busy === 'complete' ? 'Completing...' : 'Complete'}
          </button>
          <button className="ghost-button" onClick={downloadReport} disabled={busy === 'download'}>
            <Download size={18} />{busy === 'download' ? 'Preparing...' : 'Download Report'}
          </button>
        </div>
      </div>
      {error && <div className="message error">{error}</div>}
      {message && <div className="message">{message}</div>}

      <div className="card">
        <strong>{event.venue}</strong>
        <p>{event.description}</p>
        <div className="grid metrics">
          <MetricCard label="Capacity" value={event.maxCapacity} />
          <MetricCard label="Registration" value={event.registrationOpen ? 'Open' : 'Closed'} />
          <MetricCard label="Walk-ins" value={event.walkinAllowed ? (event.walkinOpen ? 'Open' : 'Closed') : 'Disabled'} />
          <MetricCard label="Start" value={new Date(event.startTime).toLocaleString()} />
        </div>
      </div>

      {summary && (
        <div className="grid metrics" style={{ marginTop: 16 }}>
          <MetricCard label="Registered" value={summary.registeredCount} />
          <MetricCard label="Normal registrations" value={summary.normalRegistrationCount} />
          <MetricCard label="Walk-ins" value={summary.walkInCount} />
          <MetricCard label="Checked in" value={summary.checkedInCount} />
          <MetricCard label="Pending check-ins" value={summary.pendingCheckInCount} />
          {event.enableFood && <MetricCard label="Food claimed" value={summary.foodClaimedCount} />}
          {event.enableGoodies && <MetricCard label="Goodies claimed" value={summary.goodiesClaimedCount} />}
        </div>
      )}

      <h2 className="section-title">Public Links</h2>
      <div className="grid link-grid">
        <div className="card">
          <strong>Registration link</strong>
          <div className="qr-token">{registrationLink}</div>
          <div className="actions">
            <button className="ghost-button" type="button" onClick={() => copy(registrationLink, 'Registration link')}><Clipboard size={18} />Copy Registration Link</button>
          </div>
        </div>
        {event.walkinAllowed && event.walkinSlug && (
          <div className="card">
            <strong>Walk-in link and QR</strong>
            <div className="qr-code-panel walkin-qr-panel">
              <QRCodeCanvas id="walkin-qr" value={walkinLink} size={180} includeMargin />
            </div>
            <div className="qr-token">{walkinLink}</div>
            <div className="actions">
              <button className="ghost-button" type="button" onClick={() => copy(walkinLink, 'Walk-in link')}><Clipboard size={18} />Copy Walk-in Link</button>
              <button className="button" type="button" onClick={downloadWalkinQr}><Download size={18} />Download Walk-in QR</button>
            </div>
          </div>
        )}
      </div>

      <h2 className="section-title">Registration Controls</h2>
      <div className="card form-grid">
        <label>Registration close time<input type="datetime-local" value={registrationCloseAt} onChange={(e) => setRegistrationCloseAt(e.target.value)} /></label>
        <div className="actions">
          <button className="ghost-button" type="button" onClick={() => run('reg-open', () => api.openRegistration(id), 'Registration opened.')} disabled={busy === 'reg-open'}><PlayCircle size={18} />Open Registration</button>
          <button className="danger-button" type="button" onClick={() => run('reg-close', () => api.closeRegistration(id), 'Registration closed.')} disabled={busy === 'reg-close'}><PauseCircle size={18} />Close Registration</button>
          <button className="button" type="button" onClick={() => run('reg-extend', () => api.extendRegistration(id, registrationCloseAt), 'Registration end time updated.')} disabled={busy === 'reg-extend'}>Extend Registration</button>
        </div>
        {event.walkinAllowed && (
          <>
            <label>Walk-in close time<input type="datetime-local" value={walkinCloseAt} onChange={(e) => setWalkinCloseAt(e.target.value)} /></label>
            <div className="actions">
              <button className="ghost-button" type="button" onClick={() => run('walkin-open', () => api.openWalkins(id), 'Walk-ins opened.')} disabled={busy === 'walkin-open'}><PlayCircle size={18} />Open Walk-ins</button>
              <button className="danger-button" type="button" onClick={() => run('walkin-close', () => api.closeWalkins(id), 'Walk-ins closed.')} disabled={busy === 'walkin-close'}><PauseCircle size={18} />Close Walk-ins</button>
              <button className="button" type="button" onClick={() => run('walkin-extend', () => api.extendWalkins(id, walkinCloseAt), 'Walk-in timer updated.')} disabled={busy === 'walkin-extend'}>Extend Walk-ins</button>
            </div>
          </>
        )}
      </div>

      <h2 className="section-title">Recent Activity</h2>
      {summary && summary.recentActivity.length > 0 ? (
        <div className="activity-list">
          {summary.recentActivity.map((activity, index) => (
            <div className="activity-item" key={`${activity.type}-${activity.occurredAt}-${index}`}>
              <strong>{activity.message}</strong>
              <span>{activity.type.replace('_', ' ')} by {activity.actor} at {new Date(activity.occurredAt).toLocaleString()}</span>
            </div>
          ))}
        </div>
      ) : (
        <EmptyState title="No activity yet" detail="Registrations and stage scans will appear here." />
      )}

      <h2 className="section-title">Registrations</h2>
      {registrations.length === 0 ? (
        <EmptyState title="No registrations yet" detail="Public registrations and walk-ins will appear here." />
      ) : (
        <table className="table">
          <thead><tr><th>Participant</th><th>Email</th><th>Source</th><th>Status</th><th>QR token</th></tr></thead>
          <tbody>
            {registrations.map((registration) => (
              <tr key={registration.id}>
                <td>{registration.employeeName}<br /><small>{registration.employeeId}</small></td>
                <td>{registration.employeeEmail}</td>
                <td>{registration.source}</td>
                <td><StatusBadge value={registration.status} /></td>
                <td>{registration.qrToken}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </>
  )
}
