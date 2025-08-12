import { render, screen, within, fireEvent, waitFor } from '@testing-library/react'
import React from 'react'

// Mock the API used by MatchesPage
vi.mock('../../services/api', () => ({
  getMatches: vi.fn(),
  getTeams: vi.fn(),
  createMatch: vi.fn(),
  updateMatch: vi.fn(),
  deleteMatch: vi.fn(),
}))

import { getMatches, getTeams, createMatch } from '../../services/api'
import MatchesPage from '../MatchesPage'

const TEAMS = [
  { id: 1, name: 'Arsenal', city: 'London' },
  { id: 2, name: 'Liverpool', city: 'Liverpool' },
  { id: 3, name: 'Manchester City', city: 'Manchester' },
  { id: 4, name: 'Chelsea', city: 'London' },
]

const M1 = { id: 10, matchDate: '2025-08-01', homeTeam: TEAMS[0], awayTeam: TEAMS[1], homeScore: 2, awayScore: 1 }
const M2 = { id: 11, matchDate: '2025-08-02', homeTeam: TEAMS[2], awayTeam: TEAMS[0], homeScore: 0, awayScore: 3 }

function primeInitialLoads(matches = []) {
  getTeams.mockResolvedValue({ data: TEAMS })
  getMatches.mockResolvedValue({ data: matches })
}

beforeEach(() => {
  vi.clearAllMocks()
})

test('lists matches (shows home/away names even when a team repeats)', async () => {
  primeInitialLoads([M1, M2])

  render(<MatchesPage />)

  // Wait for rows to appear
  const rows = await screen.findAllByRole('row')
  // Find a row containing Arsenal vs Liverpool
  const rowAL = rows.find(r => /arsenal/i.test(r.textContent) && /liverpool/i.test(r.textContent))
  expect(rowAL).toBeTruthy()

  // Find a row where Arsenal appears again (as away)
  const rowMCA = rows.find(r => /manchester city/i.test(r.textContent) && /arsenal/i.test(r.textContent))
  expect(rowMCA).toBeTruthy()
})

test('creates a match and refreshes table with populated names', async () => {
  // First load – 1 existing match
  primeInitialLoads([M1])

  render(<MatchesPage />)

  // Ensure existing is shown
  await screen.findByText('Arsenal')
  await screen.findByText('Liverpool')

  // Next calls after create: API returns list including the new one
  const NEW = { id: 12, matchDate: '2025-08-03', homeTeam: TEAMS[1], awayTeam: TEAMS[0], homeScore: 1, awayScore: 1 }
  createMatch.mockResolvedValue({ data: NEW })

  // After create, component calls getMatches again — return previous + NEW
  getMatches.mockResolvedValueOnce({ data: [M1, NEW] })

  // Fill form
  const selects = screen.getAllByRole('combobox')
  const homeSel = selects[0]
  const awaySel = selects[1]

  // Home: Liverpool (id 2)
  fireEvent.change(homeSel, { target: { value: '2' } })
  // Away: Arsenal (id 1)
  fireEvent.change(awaySel, { target: { value: '1' } })

  fireEvent.change(screen.getByPlaceholderText(/home score/i), { target: { value: '1' } })
  fireEvent.change(screen.getByPlaceholderText(/away score/i), { target: { value: '1' } })
  fireEvent.change(screen.getByDisplayValue('') /* date input */, { target: { value: '2025-08-03' } })

  fireEvent.click(screen.getByRole('button', { name: /add/i }))

  await waitFor(() => {
    expect(createMatch).toHaveBeenCalled()
  })

  // New row with Liverpool and Arsenal should be visible
  await screen.findByText(/liverpool/i)
  await screen.findAllByText(/arsenal/i) // appears in at least two rows now
})
