const labels: Record<string, string> = {
  OPEN: 'Open',
  LIVE: 'Live',
  DRAFT: 'Draft',
  COMPLETED: 'Completed',
  CANCELLED: 'Cancelled',
  REGISTERED: 'Registered',
  WALKIN: 'Walk-in',
  WAITLISTED: 'Waitlisted',
}

export function StatusBadge({ value }: { value: string }) {
  const normalized = value.toLowerCase().replaceAll('_', '-')
  return <span className={`badge badge-${normalized}`}>{labels[value] ?? value.replaceAll('_', ' ')}</span>
}
