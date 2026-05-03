import type { ReactNode } from 'react'

export function MetricCard({ label, value, detail, icon }: { label: string; value: number | string; detail?: string; icon?: ReactNode }) {
  return (
    <div className="metric-card">
      <div className="metric-card-top">
        <span>{label}</span>
        {icon}
      </div>
      <strong>{value}</strong>
      {detail && <small>{detail}</small>}
    </div>
  )
}
