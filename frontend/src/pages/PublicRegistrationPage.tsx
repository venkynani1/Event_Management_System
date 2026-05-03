import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { QRCodeCanvas } from 'qrcode.react'
import { Clipboard, Download, MapPin, ShieldCheck, Ticket, UserRound } from 'lucide-react'
import { useParams } from 'react-router-dom'
import { api } from '../api/client'
import type { PublicEvent, Registration } from '../api/client'

type Props = {
  walkin?: boolean
}

function getDeviceToken() {
  const key = 'mech.public.deviceToken'
  const existing = localStorage.getItem(key)
  if (existing) return existing
  const token = crypto.randomUUID()
  localStorage.setItem(key, token)
  return token
}

function storedRegistrationKey(slug: string, walkin: boolean) {
  return `mech.public.registration.${walkin ? 'walkin' : 'normal'}.${slug}`
}

export function PublicRegistrationPage({ walkin = false }: Props) {
  const params = useParams()
  const slug = walkin ? params.walkinSlug : params.registrationSlug
  const [event, setEvent] = useState<PublicEvent | null>(null)
  const [registration, setRegistration] = useState<Registration | null>(() => {
    if (!slug) return null
    const saved = localStorage.getItem(storedRegistrationKey(slug, walkin))
    return saved ? JSON.parse(saved) as Registration : null
  })
  const [form, setForm] = useState({ employeeId: '', employeeName: '', employeeEmail: '' })
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  const storageKey = useMemo(() => slug ? storedRegistrationKey(slug, walkin) : '', [slug, walkin])
  const accessOpen = event ? (walkin ? event.walkinOpen : event.registrationOpen) : false

  useEffect(() => {
    if (!slug) return
    const load = walkin ? api.publicWalkinEvent(slug) : api.publicEvent(slug)
    load
      .then(setEvent)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [slug, walkin])

  async function submit(eventSubmit: FormEvent) {
    eventSubmit.preventDefault()
    if (!slug) return
    setError('')
    setMessage('')
    setSubmitting(true)
    try {
      const payload = { ...form, deviceToken: getDeviceToken() }
      const result = walkin
        ? await api.registerWalkinPublic(slug, payload)
        : await api.registerPublic(slug, payload)
      setRegistration(result)
      localStorage.setItem(storageKey, JSON.stringify(result))
      setMessage('Registration complete. Your QR pass is ready.')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Registration failed')
    } finally {
      setSubmitting(false)
    }
  }

  async function copyToken() {
    if (!registration) return
    try {
      await navigator.clipboard.writeText(registration.qrToken)
      setMessage('QR token copied.')
    } catch {
      setMessage('Copy is unavailable in this browser. Select the token manually.')
    }
  }

  function downloadQr() {
    const canvas = document.getElementById('participant-qr') as HTMLCanvasElement | null
    if (!canvas || !registration) return
    const link = document.createElement('a')
    link.download = `${registration.employeeId}-${walkin ? 'walkin' : 'event'}-qr.png`
    link.href = canvas.toDataURL('image/png')
    link.click()
  }

  if (loading) return <main className="public-page"><div className="empty-state"><strong>Loading registration...</strong></div></main>

  return (
    <main className="public-page">
      <section className="public-panel">
        {error && <div className="message error">{error}</div>}
        {message && <div className="message">{message}</div>}
        {event && (
          <div className="public-event-summary">
            <span>{walkin ? 'Walk-in Access Link' : 'Registration Link'}</span>
            <h1>{event.title}</h1>
            <p>{event.description}</p>
            <div className="qr-pass-details">
              <div><MapPin size={18} /><span>{event.venue}</span></div>
              <div><Ticket size={18} /><span>{new Date(event.startTime).toLocaleString()}</span></div>
            </div>
            <div className="actions">
              <span className={`badge ${accessOpen ? 'badge-open' : 'badge-completed'}`}>
                {accessOpen ? (walkin ? 'Walk-in open' : 'Registration open') : (walkin ? 'Walk-in closed' : 'Registration closed')}
              </span>
              <span className="badge">Stage Validation: ENTRY{event.enableFood ? ' + FOOD' : ''}{event.enableGoodies ? ' + GOODIES' : ''}</span>
            </div>
          </div>
        )}

        {registration && event ? (
          <div className="qr-pass public-pass">
            <div className="qr-pass-header">
              <div>
                <span>Participant QR pass</span>
                <h2>{event.title}</h2>
              </div>
              <strong>{registration.source}</strong>
            </div>
            <div className="qr-pass-body">
              <div className="qr-pass-details">
                <div><UserRound size={18} /><span>{registration.employeeName}</span></div>
                <div><Clipboard size={18} /><span>{registration.employeeId}</span></div>
                <div><MapPin size={18} /><span>{event.venue}</span></div>
                <div><Ticket size={18} /><span>{new Date(event.startTime).toLocaleString()}</span></div>
              </div>
              <div className="qr-code-panel">
                <QRCodeCanvas id="participant-qr" value={registration.qrToken} size={260} level="M" includeMargin />
              </div>
            </div>
            <div className="qr-token">{registration.qrToken}</div>
            <p className="pass-instruction"><ShieldCheck size={18} />Save this QR pass. You need it at the event.</p>
            <div className="actions">
              <button className="ghost-button" type="button" onClick={copyToken}><Clipboard size={18} />Copy Token</button>
              <button className="button" type="button" onClick={downloadQr}><Download size={18} />Download QR</button>
            </div>
          </div>
        ) : (
          <form className="card form-grid public-form" onSubmit={submit}>
            {event && !accessOpen && (
              <div className="message error span-2">{walkin ? 'Walk-in access is closed for this event.' : 'Registration closed for this event.'}</div>
            )}
            <label>Employee ID<input required value={form.employeeId} onChange={(e) => setForm({ ...form, employeeId: e.target.value })} /></label>
            <label>Employee name<input required value={form.employeeName} onChange={(e) => setForm({ ...form, employeeName: e.target.value })} /></label>
            <label className="span-2">Employee email<input required type="email" value={form.employeeEmail} onChange={(e) => setForm({ ...form, employeeEmail: e.target.value })} /></label>
            <div className="actions span-2">
              <button className="button" type="submit" disabled={submitting || !event || !accessOpen}>
                {submitting ? 'Submitting...' : walkin ? 'Register Walk-in' : 'Register'}
              </button>
            </div>
          </form>
        )}
      </section>
    </main>
  )
}
