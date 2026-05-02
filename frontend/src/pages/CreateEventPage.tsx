import { useState } from 'react'
import type { FormEvent } from 'react'
import { Save } from 'lucide-react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api/client'

const toLocalInput = (date: Date) => date.toISOString().slice(0, 16)
type StageMode = 'ENTRY' | 'ENTRY_FOOD' | 'ENTRY_FOOD_GOODIES'

export function CreateEventPage() {
  const navigate = useNavigate()
  const now = new Date()
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)
  const [form, setForm] = useState({
    title: '',
    description: '',
    venue: '',
    startTime: toLocalInput(new Date(now.getTime() + 7 * 86400000)),
    endTime: toLocalInput(new Date(now.getTime() + 7 * 86400000 + 2 * 3600000)),
    registrationOpenAt: toLocalInput(now),
    registrationCloseAt: toLocalInput(new Date(now.getTime() + 6 * 86400000)),
    registrationOpen: true,
    maxCapacity: 100,
    walkinAllowed: true,
    walkinOpen: true,
    walkinCloseAt: toLocalInput(new Date(now.getTime() + 7 * 86400000)),
  })
  const [stageMode, setStageMode] = useState<StageMode>('ENTRY_FOOD_GOODIES')

  async function submit(event: FormEvent) {
    event.preventDefault()
    setError('')
    setSaving(true)
    try {
      const created = await api.createEvent({
        ...form,
        walkinCloseAt: form.walkinAllowed && form.walkinCloseAt ? form.walkinCloseAt : null,
        walkinOpen: form.walkinAllowed && form.walkinOpen,
        enableEntry: true,
        enableFood: stageMode === 'ENTRY_FOOD' || stageMode === 'ENTRY_FOOD_GOODIES',
        enableGoodies: stageMode === 'ENTRY_FOOD_GOODIES',
      })
      navigate(`/admin/events/${created.id}`)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Event creation failed')
    } finally {
      setSaving(false)
    }
  }

  return (
    <>
      <div className="page-header"><h1>Create Event</h1></div>
      {error && <div className="message error">{error}</div>}
      <form className="card form-grid" onSubmit={submit}>
        <label>Title<input required value={form.title} onChange={(event) => setForm({ ...form, title: event.target.value })} /></label>
        <label>Venue<input required value={form.venue} onChange={(event) => setForm({ ...form, venue: event.target.value })} /></label>
        <label className="span-2">Description<textarea value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} /></label>
        <label>Start<input type="datetime-local" value={form.startTime} onChange={(event) => setForm({ ...form, startTime: event.target.value })} /></label>
        <label>End<input type="datetime-local" value={form.endTime} onChange={(event) => setForm({ ...form, endTime: event.target.value })} /></label>
        <label>Registration opens<input type="datetime-local" value={form.registrationOpenAt} onChange={(event) => setForm({ ...form, registrationOpenAt: event.target.value })} /></label>
        <label>Registration closes<input type="datetime-local" value={form.registrationCloseAt} onChange={(event) => setForm({ ...form, registrationCloseAt: event.target.value })} /></label>
        <label>Capacity<input type="number" min="1" value={form.maxCapacity} onChange={(event) => setForm({ ...form, maxCapacity: Number(event.target.value) })} /></label>
        <label>Walk-ins<select value={String(form.walkinAllowed)} onChange={(event) => setForm({ ...form, walkinAllowed: event.target.value === 'true' })}><option value="true">Allowed</option><option value="false">Closed</option></select></label>
        <label>Registration state<select value={String(form.registrationOpen)} onChange={(event) => setForm({ ...form, registrationOpen: event.target.value === 'true' })}><option value="true">Open</option><option value="false">Closed</option></select></label>
        <label>Walk-in state<select value={String(form.walkinOpen)} onChange={(event) => setForm({ ...form, walkinOpen: event.target.value === 'true' })}><option value="true">Open</option><option value="false">Closed</option></select></label>
        <label className="span-2">Walk-in closes<input type="datetime-local" value={form.walkinCloseAt} onChange={(event) => setForm({ ...form, walkinCloseAt: event.target.value })} disabled={!form.walkinAllowed} /></label>
        <label className="span-2">Validation stages<select value={stageMode} onChange={(event) => setStageMode(event.target.value as StageMode)}><option value="ENTRY">ENTRY only</option><option value="ENTRY_FOOD">ENTRY + FOOD</option><option value="ENTRY_FOOD_GOODIES">ENTRY + FOOD + GOODIES</option></select></label>
        <div className="actions span-2">
          <button className="button" type="submit" disabled={saving}><Save size={18} />{saving ? 'Saving...' : 'Save'}</button>
        </div>
      </form>
    </>
  )
}
