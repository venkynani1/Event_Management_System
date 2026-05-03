import { expect, test } from '@playwright/test'

const appUrl = process.env.E2E_BASE_URL ?? 'http://127.0.0.1:5173'

function localInput(minutesFromNow: number) {
  const date = new Date(Date.now() + minutesFromNow * 60_000)
  const pad = (value: number) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`
}

async function login(page: import('@playwright/test').Page, role: string, employeeCode: string) {
  await page.goto(`${appUrl}/login`)
  await page.getByLabel('Role').selectOption(role)
  await page.getByLabel('Employee code').fill(employeeCode)
  await page.getByRole('button', { name: 'Login' }).click()
}

async function logout(page: import('@playwright/test').Page) {
  await page.getByRole('button', { name: /Log out/ }).click()
  await expect(page).toHaveURL(/\/login$/)
}

test('public registration, scanner stages, dashboard, walk-in, and reports', async ({ page }) => {
  const unique = Date.now()
  const title = `Public Flow Demo Event ${unique}`

  await login(page, 'HR_ADMIN', 'HR001')
  await expect(page.getByRole('heading', { name: 'Dashboard' })).toBeVisible()
  await page.getByRole('link', { name: 'Create Event' }).click()
  await page.getByLabel('Title').fill(title)
  await page.getByLabel('Venue').fill('Demo Hall A')
  await page.getByLabel('Description').fill('Public registration and stage scan demo.')
  await page.getByLabel('Start').fill(localInput(180))
  await page.getByRole('textbox', { name: 'End', exact: true }).fill(localInput(300))
  await page.getByLabel('Registration opens').fill(localInput(-10))
  await page.getByLabel('Registration closes').fill(localInput(120))
  await page.getByLabel('Capacity').fill('5')
  await page.getByLabel('Walk-ins').selectOption('true')
  await page.getByLabel('Registration state').selectOption('true')
  await page.getByLabel('Walk-in state').selectOption('true')
  await page.getByLabel('Walk-in closes').fill(localInput(240))
  await page.getByLabel('Validation stages').selectOption('ENTRY_FOOD_GOODIES')
  await page.getByRole('button', { name: /Save/ }).click()
  await expect(page.getByRole('heading', { name: title })).toBeVisible()
  await expect(page.getByText('ENTRY + FOOD + GOODIES')).toBeVisible()

  const registrationLink = await page.locator('.qr-token').nth(0).innerText()
  const walkinLink = await page.locator('.qr-token').nth(1).innerText()
  expect(registrationLink).toContain('/register/')
  expect(walkinLink).toContain('/walkin/')
  await logout(page)

  await page.goto(registrationLink)
  await expect(page.getByRole('heading', { name: title })).toBeVisible()
  await page.getByLabel('Employee ID').fill(`EMP001-${unique}`)
  await page.getByLabel('Employee name').fill('Aarav Mehta')
  await page.getByLabel('Employee email').fill(`aarav.${unique}@maverick.local`)
  await page.getByRole('button', { name: 'Register' }).click()
  await expect(page.getByText('Registration complete')).toBeVisible()
  const qrToken = await page.locator('.public-pass .qr-token').innerText()
  expect(qrToken).toContain('MECH-')

  await login(page, 'SCANNER_OPERATOR', 'SCAN001')
  await expect(page.getByRole('heading', { name: 'Scanner' })).toBeVisible()
  await page.getByLabel('QR token').fill(qrToken)
  await page.getByLabel('Scan type').selectOption('ENTRY')
  await page.getByRole('button', { name: /Submit Scan/ }).click()
  await expect(page.locator('.result-panel').filter({ hasText: 'Entry Verified' })).toBeVisible()
  await page.getByRole('button', { name: /Submit Scan/ }).click()
  await expect(page.locator('.result-panel.error').filter({ hasText: 'Duplicate scan blocked' })).toBeVisible()

  await page.getByLabel('Scan type').selectOption('FOOD')
  await page.getByRole('button', { name: /Submit Scan/ }).click()
  await expect(page.locator('.result-panel').filter({ hasText: 'Food Claim Verified' })).toBeVisible()
  await page.getByRole('button', { name: /Submit Scan/ }).click()
  await expect(page.locator('.result-panel.error').filter({ hasText: 'Duplicate scan blocked' })).toBeVisible()

  await page.getByLabel('Scan type').selectOption('GOODIES')
  await page.getByRole('button', { name: /Submit Scan/ }).click()
  await expect(page.locator('.result-panel').filter({ hasText: 'Goodies Claim Verified' })).toBeVisible()
  await logout(page)

  await login(page, 'HR_ADMIN', 'HR001')
  await page.getByRole('link', { name: 'Events' }).click()
  await page.getByRole('link', { name: title }).click()
  await expect(page.locator('.metric-card').filter({ hasText: 'Total Registrations' })).toContainText('1')
  await expect(page.locator('.metric-card').filter({ hasText: 'Entry Verified' })).toContainText('1')
  await expect(page.locator('.metric-card').filter({ hasText: 'Food Claim Verified' })).toContainText('1')
  await expect(page.locator('.metric-card').filter({ hasText: 'Goodies Claim Verified' })).toContainText('1')
  await expect(page.getByRole('heading', { name: 'Recent Activity' })).toBeVisible()

  const reportDownload = page.waitForEvent('download')
  await page.getByRole('button', { name: /Download Report/ }).click()
  expect((await reportDownload).suggestedFilename()).toContain('report.csv')
  await page.getByRole('link', { name: 'Reports' }).click()
  await page.getByLabel('Event').selectOption({ label: title })
  const reportsDownload = page.waitForEvent('download')
  await page.getByRole('button', { name: /Download CSV/ }).click()
  expect((await reportsDownload).suggestedFilename()).toContain('report.csv')
  await logout(page)

  await page.goto(walkinLink)
  await page.evaluate(() => localStorage.removeItem('mech.public.deviceToken'))
  await page.reload()
  await expect(page.getByRole('heading', { name: title })).toBeVisible()
  await page.getByLabel('Employee ID').fill(`WALK-${unique}`)
  await page.getByLabel('Employee name').fill('Walk In Demo')
  await page.getByLabel('Employee email').fill(`walkin.${unique}@maverick.local`)
  await page.getByRole('button', { name: 'Register Walk-in' }).click()
  await expect(page.getByText('Registration complete')).toBeVisible()
  const walkinQrToken = await page.locator('.public-pass .qr-token').innerText()
  expect(walkinQrToken).toContain('MECH-')

  await login(page, 'SCANNER_OPERATOR', 'SCAN001')
  await page.getByLabel('QR token').fill(walkinQrToken)
  await page.getByLabel('Scan type').selectOption('ENTRY')
  await page.getByRole('button', { name: /Submit Scan/ }).click()
  await expect(page.locator('.result-panel').filter({ hasText: 'Entry Verified' })).toBeVisible()
  await logout(page)

  await login(page, 'HR_ADMIN', 'HR001')
  await page.getByRole('link', { name: 'Events' }).click()
  await page.getByRole('link', { name: title }).click()
  await expect(page.locator('.metric-card').filter({ hasText: 'Walk-ins' }).last()).toContainText('1')
})
