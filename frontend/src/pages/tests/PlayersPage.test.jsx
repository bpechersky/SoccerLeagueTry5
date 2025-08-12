import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import React from 'react'

vi.mock('../../services/api', () => ({
  getPlayers: vi.fn(),
  getTeams: vi.fn(),
  createPlayer: vi.fn(),
  updatePlayer: vi.fn(),
  deletePlayer: vi.fn(),
}))

import { getPlayers, getTeams, createPlayer } from '../../services/api'
import PlayersPage from '../PlayersPage'

const TEAMS = [{ id: 1, name: 'Arsenal', city: 'London' }]
const PLAYERS = [{ id: 9, name: 'Saka', position: 'Winger', team: TEAMS[0] }]

beforeEach(() => vi.clearAllMocks())

test('renders players and allows creating with a team', async () => {
  getTeams.mockResolvedValue({ data: TEAMS })
  getPlayers.mockResolvedValue({ data: PLAYERS })

  render(<PlayersPage />)

  // shows existing player
  expect(await screen.findByText(/saka/i)).toBeInTheDocument()

  // create another
  createPlayer.mockResolvedValue({ data: { id: 10, name: 'Odegaard', position: 'Midfielder', team: TEAMS[0] } })
  // After create, page reloads
  getPlayers.mockResolvedValueOnce({
    data: [...PLAYERS, { id: 10, name: 'Odegaard', position: 'Midfielder', team: TEAMS[0] }],
  })

  fireEvent.change(screen.getByPlaceholderText(/name/i), { target: { value: 'Odegaard' } })
  fireEvent.change(screen.getByPlaceholderText(/position/i), { target: { value: 'Midfielder' } })
  fireEvent.change(screen.getByRole('combobox'), { target: { value: '1' } }) // team select

  fireEvent.click(screen.getByRole('button', { name: /add/i }))

  await waitFor(() => expect(createPlayer).toHaveBeenCalled())
  await screen.findByText(/odegaard/i)
  await screen.findAllByText(/arsenal/i)
})
