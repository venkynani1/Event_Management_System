import { useEffect, useRef, useState } from 'react'
import type { FormEvent } from 'react'
import { Camera, CameraOff, CheckCircle2, ScanLine, ShieldAlert, UserRound } from 'lucide-react'
import type { Html5Qrcode } from 'html5-qrcode'
import { api } from '../api/client'
import type { DashboardSummary, ScanValidation, Stage } from '../api/client'
import { MetricCard } from '../components/MetricCard'
import { StatusBadge } from '../components/StatusBadge'

const CAMERA_READER_ID = 'maverick-camera-reader'

const stageLabels: Record<Stage, string> = {
  ENTRY: 'Entry',
  FOOD: 'Food',
  GOODIES: 'Goodies',
}

function successMessage(stage: Stage) {
  if (stage === 'ENTRY') return 'Entry Verified'
  if (stage === 'FOOD') return 'Food Claim Verified'
  return 'Goodies Claim Verified'
}

export function ScannerPage() {
  const scannerRef = useRef<Html5Qrcode | null>(null)
  const scanLockedRef = useRef(false)
  const [qrToken, setQrToken] = useState('')
  const [scanType, setScanType] = useState<Stage>('ENTRY')
  const [stationName, setStationName] = useState('Main Gate')
  const [validation, setValidation] = useState<ScanValidation | null>(null)
  const [summary, setSummary] = useState<DashboardSummary | null>(null)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [cameraError, setCameraError] = useState('')
  const [lastScannedToken, setLastScannedToken] = useState('')
  const [lastActionResult, setLastActionResult] = useState('')
  const [lastSuccessfulScan, setLastSuccessfulScan] = useState('')
  const [validating, setValidating] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [cameraStarting, setCameraStarting] = useState(false)
  const [cameraActive, setCameraActive] = useState(false)

  useEffect(() => {
    return () => {
      const scanner = scannerRef.current
      if (scanner?.isScanning) {
        void scanner.stop().then(() => scanner.clear()).catch(() => undefined)
      }
    }
  }, [])

  async function refreshDashboard(eventId?: number) {
    const targetEventId = eventId ?? validation?.eventId
    if (!targetEventId) return
    setSummary(await api.dashboard(targetEventId))
  }

  async function validate() {
    setError('')
    setMessage('')
    setLastActionResult('')
    setValidating(true)
    try {
      const result = await api.validateScan(qrToken)
      setValidation(result)
      await refreshDashboard(result.eventId)
      const resultMessage = 'QR token is valid and ready for scanning.'
      setMessage(resultMessage)
      setLastActionResult(resultMessage)
    } catch (err) {
      setValidation(null)
      setSummary(null)
      const resultMessage = err instanceof Error ? err.message : 'Validation failed'
      setError(resultMessage)
      setLastActionResult(resultMessage)
    } finally {
      setValidating(false)
    }
  }

  async function submit(event: FormEvent) {
    event.preventDefault()
    setError('')
    setMessage('')
    setLastActionResult('')
    const scanSignature = `${qrToken}:${scanType}`
    if (lastSuccessfulScan === scanSignature) {
      const resultMessage = 'Duplicate scan blocked. This QR was already submitted for the selected stage in this session.'
      setError(resultMessage)
      setLastActionResult(resultMessage)
      return
    }
    setSubmitting(true)
    try {
      await api.scanStage(qrToken, scanType, stationName)
      const resultMessage = successMessage(scanType)
      setMessage(resultMessage)
      setLastActionResult(resultMessage)
      setLastSuccessfulScan(scanSignature)
      const result = await api.validateScan(qrToken)
      setValidation(result)
      await refreshDashboard(result.eventId)
    } catch (err) {
      const resultMessage = err instanceof Error ? err.message : 'Scan failed'
      setError(resultMessage)
      setLastActionResult(resultMessage)
    } finally {
      setSubmitting(false)
    }
  }

  async function startCameraScan() {
    setCameraError('')
    setMessage('')
    setCameraStarting(true)
    scanLockedRef.current = false
    try {
      if (!window.isSecureContext && window.location.hostname !== 'localhost' && window.location.hostname !== '127.0.0.1') {
        throw new Error('Camera scanning requires HTTPS or localhost.')
      }

      const { Html5Qrcode } = await import('html5-qrcode')
      const scanner = scannerRef.current ?? new Html5Qrcode(CAMERA_READER_ID)
      scannerRef.current = scanner

      await scanner.start(
        { facingMode: 'environment' },
        { fps: 10, qrbox: { width: 250, height: 250 } },
        (decodedText) => {
          if (scanLockedRef.current) return
          scanLockedRef.current = true
          const scannedValue = decodedText.trim()
          setQrToken(scannedValue)
          setLastScannedToken(scannedValue)
          setMessage('QR detected. Review the token, then validate or submit Stage Validation.')
          void stopCameraScan(false)
        },
        () => undefined,
      )
      setCameraActive(true)
    } catch (err) {
      const detail = err instanceof Error ? err.message : 'Camera is unavailable.'
      setCameraError(`Unable to start camera scanning. Check browser camera permission and device availability. ${detail}`)
      setCameraActive(false)
    } finally {
      setCameraStarting(false)
    }
  }

  async function stopCameraScan(resetLock = true) {
    const scanner = scannerRef.current
    if (!scanner) return
    try {
      if (scanner.isScanning) {
        await scanner.stop()
      }
      scanner.clear()
    } catch (err) {
      setCameraError(err instanceof Error ? err.message : 'Unable to stop camera scanning.')
    } finally {
      if (resetLock) scanLockedRef.current = false
      setCameraActive(false)
    }
  }

  return (
    <>
      <div className="page-header scanner-title">
        <div>
          <span className="eyebrow">On-site operations</span>
          <h1>Scanner</h1>
          <p>Validate QR passes quickly for entry, food, and goodies checkpoints.</p>
        </div>
      </div>
      <div className="scanner-stage-bar">
        {(['ENTRY', 'FOOD', 'GOODIES'] as Stage[]).map((stage) => (
          <button
            className={scanType === stage ? 'stage-button active' : 'stage-button'}
            key={stage}
            type="button"
            onClick={() => setScanType(stage)}
          >
            {stageLabels[stage]}
          </button>
        ))}
      </div>
      <div className="card scanner-camera">
        <div className="scanner-camera-header">
          <div>
            <h2 className="section-title" style={{ margin: 0 }}>Camera Scan</h2>
            <p>Point the camera at the participant QR pass.</p>
          </div>
          <div className="actions" style={{ marginTop: 0 }}>
            <button className="ghost-button" type="button" onClick={startCameraScan} disabled={cameraStarting || cameraActive}>
              <Camera size={18} />
              {cameraStarting ? 'Starting...' : 'Start Camera Scan'}
            </button>
            <button className="danger-button" type="button" onClick={() => stopCameraScan()} disabled={!cameraActive}>
              <CameraOff size={18} />
              Stop Camera Scan
            </button>
          </div>
        </div>
        <div id={CAMERA_READER_ID} className="camera-reader">
          {!cameraActive && !cameraStarting && <span>Camera preview will appear here.</span>}
        </div>
        {cameraError && <div className="message error">{cameraError}</div>}
        <div className="last-scan">
          <span>Last scanned token</span>
          <code>{lastScannedToken || 'No camera scan yet'}</code>
        </div>
      </div>
      <div className="grid scanner-grid">
        <form className="card" onSubmit={submit}>
          <label>QR token<textarea required value={qrToken} onChange={(event) => setQrToken(event.target.value.trim())} /></label>
          <label>Scan type<select value={scanType} onChange={(event) => setScanType(event.target.value as Stage)}><option value="ENTRY">Entry</option><option value="FOOD">Food</option><option value="GOODIES">Goodies</option></select></label>
          <label>Station<input value={stationName} onChange={(event) => setStationName(event.target.value)} /></label>
          <div className="actions">
            <button className="ghost-button" type="button" onClick={validate} disabled={validating || !qrToken}>
              {validating ? 'Validating...' : 'Validate'}
            </button>
            <button className="button" type="submit" disabled={submitting || !qrToken}>
              <ScanLine size={18} />
              {submitting ? 'Submitting...' : 'Submit Scan'}
            </button>
          </div>
          {message && <div className="result-panel success"><CheckCircle2 size={24} /><strong>{message}</strong></div>}
          {error && <div className="result-panel error"><ShieldAlert size={24} /><strong>{error}</strong></div>}
          {lastActionResult && (
            <div className="last-scan">
              <span>Last action result</span>
              <code>{lastActionResult}</code>
            </div>
          )}
        </form>
        <div className="card validation-card">
          <h2 className="section-title" style={{ marginTop: 0 }}>Validation</h2>
          {validation ? (
            <div className="grid">
              <div className="participant-card">
                <UserRound size={26} />
                <div>
                  <strong>{validation.employeeName}</strong>
                  <span>{validation.employeeId}</span>
                </div>
              </div>
              <span>{validation.eventTitle}</span>
              <StatusBadge value={validation.registrationStatus} />
              <div className="scan-state-grid">
                <span>Entry: {validation.entryScanned ? 'Entry Verified' : 'Open'}</span>
                <span>Food: {validation.foodEnabled ? (validation.foodScanned ? 'Food Claim Verified' : 'Open') : 'Disabled'}</span>
                <span>Goodies: {validation.goodiesEnabled ? (validation.goodiesScanned ? 'Goodies Claim Verified' : 'Open') : 'Disabled'}</span>
              </div>
            </div>
          ) : (
            <span>No token validated</span>
          )}
        </div>
      </div>
      {summary && (
        <>
          <h2 className="section-title">Live Event Counts</h2>
          <div className="grid metrics">
            <MetricCard label="Total Registrations" value={summary.registeredCount} />
            <MetricCard label="Entry Verified" value={summary.checkedInCount} />
            {summary.enableFood && <MetricCard label="Food Claim Verified" value={summary.foodClaimedCount} />}
            {summary.enableGoodies && <MetricCard label="Goodies Claim Verified" value={summary.goodiesClaimedCount} />}
            <MetricCard label="Walk-ins" value={summary.walkInCount} />
          </div>
        </>
      )}
    </>
  )
}
