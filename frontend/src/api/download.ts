import { api } from './client'
import type { EventItem } from './client'

export async function downloadEventReport(event: EventItem) {
  const blob = await api.exportReport(event.id)
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  const safeTitle = event.title.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '') || 'event'
  link.href = url
  link.download = `${safeTitle}-report.csv`
  document.body.appendChild(link)
  link.click()
  link.remove()
  URL.revokeObjectURL(url)
}
