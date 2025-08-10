import React, { useEffect, useState } from 'react'
import { getMatches, getTeams, createMatch, updateMatch, deleteMatch } from '../services/api'

export default function MatchesPage() {
  const [matches, setMatches] = useState([])
  const [teams, setTeams] = useState([])
  const [form, setForm] = useState({ id:null, homeTeamId:'', awayTeamId:'', homeScore:'', awayScore:'', matchDate:'' })

  const load = async () => {
    const [m, t] = await Promise.all([getMatches(), getTeams()])
    setMatches(m.data); setTeams(t.data)
  }
  useEffect(()=>{ load() }, [])

  const onSubmit = async (e) => {
    e.preventDefault()
    const body = {
      homeTeam: form.homeTeamId ? { id: Number(form.homeTeamId) } : null,
      awayTeam: form.awayTeamId ? { id: Number(form.awayTeamId) } : null,
      homeScore: form.homeScore!=='' ? Number(form.homeScore) : null,
      awayScore: form.awayScore!=='' ? Number(form.awayScore) : null,
      matchDate: form.matchDate || null
    }
    if (form.id) await updateMatch(form.id, body)
    else await createMatch(body)
    setForm({ id:null, homeTeamId:'', awayTeamId:'', homeScore:'', awayScore:'', matchDate:'' })
    load()
  }

  const onEdit = (m) => {
    setForm({
      id: m.id,
      homeTeamId: m.homeTeam?.id || '',
      awayTeamId: m.awayTeam?.id || '',
      homeScore: m.homeScore ?? '',
      awayScore: m.awayScore ?? '',
      matchDate: m.matchDate || ''
    })
  }

  const onDelete = async (id) => { await deleteMatch(id); load() }

  return (
    <div>
      <h2>Matches</h2>
      <form onSubmit={onSubmit} style={{ display:'grid', gridTemplateColumns:'repeat(6, 1fr)', gap:8, marginBottom:12 }}>
        <select value={form.homeTeamId} onChange={e=>setForm({...form, homeTeamId:e.target.value})}>
          <option value="">Home</option>
          {teams.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
        </select>
        <select value={form.awayTeamId} onChange={e=>setForm({...form, awayTeamId:e.target.value})}>
          <option value="">Away</option>
          {teams.map(t => <option key={t.id} value={t.id}>{t.name}</option>)}
        </select>
        <input type="number" placeholder="Home Score" value={form.homeScore} onChange={e=>setForm({...form, homeScore:e.target.value})} />
        <input type="number" placeholder="Away Score" value={form.awayScore} onChange={e=>setForm({...form, awayScore:e.target.value})} />
        <input type="date" value={form.matchDate} onChange={e=>setForm({...form, matchDate:e.target.value})} />
        <button type="submit">{form.id ? 'Update' : 'Add'}</button>
      </form>

      <table border="1" cellPadding="6">
        <thead>
          <tr><th>Date</th><th>Home</th><th>Score</th><th>Away</th><th>Actions</th></tr>
        </thead>
        <tbody>
          {matches.map(m => (
            <tr key={m.id}>
              <td>{m.matchDate || '-'}</td>
              <td>{m.homeTeam ? m.homeTeam.name : '-'}</td>
              <td>{m.homeScore!=null && m.awayScore!=null ? `${m.homeScore} : ${m.awayScore}` : '-'}</td>
              <td>{m.awayTeam ? m.awayTeam.name : '-'}</td>
              <td>
                <button onClick={()=>onEdit(m)}>Edit</button>
                <button onClick={()=>onDelete(m.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
