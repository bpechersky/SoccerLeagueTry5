import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import React from 'react'

vi.mock('../../services/api', () => ({
  getTeams: vi.fn(),
  createTeam: vi.fn(),
  updateTeam: vi.fn(),
  deleteTeam: vi.fn(),
}))

import { getTeams, createTeam } from '../../services/api'
import TeamsPage from '../TeamsPage'

const TEAMS = [
  { id: 1, name: 'Arsenal', city: 'London' },
  { id: 2, name: 'Liverpool', city: 'Liverpool' },
]

beforeEach(() => vi.clearAllMocks())

test('renders teams list', async () => {
  getTeams.mockResolvedValue({ data: TEAMS })
  render(<TeamsPage />)

  expect(await screen.findByText(/arsenal/i)).toBeInTheDocument()
  expect(screen.getByText(/liverpool/i)).toBeInTheDocument()
})

test('creates a team and refreshes list', async () => {
  getTeams.mockResolvedValueOnce({ data: TEAMS })

  render(<TeamsPage />)
  await screen.findByText(/arsenal/i)

  createTeam.mockResolvedValue({ data: { id: 3, name: 'Chelsea', city: 'London' } })
  // After create, TeamsPage reloads teams
  getTeams.mockResolvedValueOnce({ data: [...TEAMS, { id: 3, name: 'Chelsea', city: 'London' }] })

  fireEvent.change(screen.getByPlaceholderText(/name/i), { target: { value: 'Chelsea' } })
  fireEvent.change(screen.getByPlaceholderText(/city/i), { target: { value: 'London' } })
  fireEvent.click(screen.getByRole('button', { name: /add/i }))

  await waitFor(() => expect(createTeam).toHaveBeenCalled())
  await screen.findByText(/chelsea/i)
})
