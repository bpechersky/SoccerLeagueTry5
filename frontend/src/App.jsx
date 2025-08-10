import React from 'react'
import { Link, Routes, Route, Navigate } from 'react-router-dom'
import TeamsPage from './pages/TeamsPage'
import PlayersPage from './pages/PlayersPage'
import MatchesPage from './pages/MatchesPage'
import StandingsPage from './pages/StandingsPage'

export default function App() {
  return (
    <div style={{ fontFamily: 'system-ui, Arial', margin: 20 }}>
      <h1>⚽ Soccer League</h1>
      <nav style={{ display: 'flex', gap: 12, marginBottom: 20 }}>
        <Link to="/teams">Teams</Link>
        <Link to="/players">Players</Link>
        <Link to="/matches">Matches</Link>
        <Link to="/standings">Standings</Link>
      </nav>
      <Routes>
        <Route path="/" element={<Navigate to="/teams" />} />
        <Route path="/teams" element={<TeamsPage />} />
        <Route path="/players" element={<PlayersPage />} />
        <Route path="/matches" element={<MatchesPage />} />
        <Route path="/standings" element={<StandingsPage />} />
      </Routes>
    </div>
  )
}
