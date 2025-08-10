import React, { useEffect, useState } from 'react'
import { getPlayers, getTeams, createPlayer, updatePlayer, deletePlayer } from '../services/api'

export default function PlayersPage() {
  const [players, setPlayers] = useState([])
  const [teams, setTeams] = useState([])
  const [form, setForm] = useState({ name: '', position: '', teamId: '' })
  const [editingId, setEditingId] = useState(null)

  const load = async () => {
    try {
      const [p, t] = await Promise.all([getPlayers(), getTeams()])
      setPlayers(Array.isArray(p?.data) ? p.data : p?.data?.content ?? [])
      setTeams(Array.isArray(t?.data) ? t.data : t?.data?.content ?? [])
    } catch (e) {
      console.error('Failed to load players/teams', e)
      setPlayers([])
      setTeams([])
    }
  }

  useEffect(() => {
    load()                    // ✅ call the function you actually defined
  }, [])

  const onSubmit = async (e) => {
    e.preventDefault()
    const body = {
      name: form.name.trim(),
      position: form.position.trim(),
      team: form.teamId ? { id: Number(form.teamId) } : null,  // ✅ ensure number
    }
    try {
      if (editingId) {
        await updatePlayer(editingId, body)
      } else {
        await createPlayer(body)
      }
      setForm({ name: '', position: '', teamId: '' })
      setEditingId(null)
      await load()
    } catch (err) {
      console.error('Create/Update player failed', err)
    }
  }

  const onEdit = (p) => {
    setEditingId(p.id)
    setForm({
      name: p.name ?? '',
      position: p.position ?? '',
      teamId: p.team?.id ?? '',
    })
  }

  const onDelete = async (id) => {
    try {
      await deletePlayer(id)
      await load()
    } catch (e) {
      console.error('Delete failed', e)
    }
  }

  return (
    <div>
      <h2>Players</h2>

      <form onSubmit={onSubmit} style={{ display: 'flex', gap: 8, marginBottom: 12, flexWrap: 'wrap' }}>
        <input
          placeholder="Name"
          value={form.name}
          onChange={(e) => setForm({ ...form, name: e.target.value })}
        />
        <input
          placeholder="Position"
          value={form.position}
          onChange={(e) => setForm({ ...form, position: e.target.value })}
        />

        {/* ✅ Team dropdown writes a number (or '') */}
        <select
          value={form.teamId}
          onChange={(e) =>
            setForm({ ...form, teamId: e.target.value ? Number(e.target.value) : '' })
          }
        >
          <option value="">No Team</option>
          {teams.map((t) => (
            <option key={t.id} value={t.id}>
              {t.name}
            </option>
          ))}
        </select>

        <button type="submit">{editingId ? 'Update' : 'Add'}</button>
        {editingId && (
          <button
            type="button"
            onClick={() => {
              setEditingId(null)
              setForm({ name: '', position: '', teamId: '' })
            }}
          >
            Cancel
          </button>
        )}
      </form>

      <table border="1" cellPadding="6">
        <thead>
          <tr>
            <th>ID</th><th>Name</th><th>Position</th><th>Team</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {players.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.name}</td>
              <td>{p.position}</td>
              <td>{p.team ? p.team.name : '-'}</td>
              <td>
                <button onClick={() => onEdit(p)}>Edit</button>
                <button onClick={() => onDelete(p.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
