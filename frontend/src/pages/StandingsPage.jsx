import React, { useEffect, useState } from 'react'
import { getStandings } from '../services/api'

export default function StandingsPage() {
  const [rows, setRows] = useState([])

  const load = async () => {
    const { data } = await getStandings()
    setRows(data)
  }
  useEffect(()=>{ load() }, [])

  return (
    <div>
      <h2>Standings</h2>
      <button onClick={load} style={{ marginBottom: 10 }}>Refresh</button>
      <table border="1" cellPadding="6">
        <thead>
          <tr>
            <th>#</th><th>Team</th><th>P</th><th>W</th><th>D</th><th>L</th>
            <th>GF</th><th>GA</th><th>GD</th><th>Pts</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((r, idx) => (
            <tr key={r.teamId}>
              <td>{idx+1}</td>
              <td>{r.teamName}</td>
              <td>{r.played}</td>
              <td>{r.wins}</td>
              <td>{r.draws}</td>
              <td>{r.losses}</td>
              <td>{r.goalsFor}</td>
              <td>{r.goalsAgainst}</td>
              <td>{r.goalDiff}</td>
              <td>{r.points}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
