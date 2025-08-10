import React, { useEffect, useState } from 'react'
import { getTeams, createTeam, updateTeam, deleteTeam } from '../services/api'

export default function TeamsPage() {
  const [teams, setTeams] = useState([])
  const [form, setForm] = useState({ name: '', city: '' })
  const [editingId, setEditingId] = useState(null)
  const [error, setError] = useState('')

  const load = async () => {
    try {
      const { data } = await getTeams()
      if (Array.isArray(data)) {
        setTeams(data)
        setError('')
      } else {
        console.error('Unexpected /api/teams payload:', data)
        setTeams([])
        setError('Unexpected response from server. Check console/Network.')
      }
    } catch (e) {
      console.error('GET /api/teams failed:', e)
      setTeams([])
      setError('Failed to load teams.')
    }
  }

  useEffect(() => { load() }, [])

  const onSubmit = async (e) => {
    e.preventDefault()
    if (!form.name || !form.city) return
    if (editingId) await updateTeam(editingId, form)
    else await createTeam(form)
    setEditingId(null)
    setForm({ name: '', city: '' })
    load()
  }

  const onEdit = (t) => { setEditingId(t.id); setForm({ name: t.name, city: t.city }) }
  const onDelete = async (id) => { await deleteTeam(id); load() }

  return (
    <div>
      <h2>Teams</h2>
      {error && <div style={{ color: 'red', marginBottom: 8 }}>{error}</div>}

      <form onSubmit={onSubmit} style={{ display:'flex', gap:8, marginBottom:12 }}>
        <input placeholder="Name" value={form.name} onChange={e=>setForm({...form, name:e.target.value})} />
        <input placeholder="City" value={form.city} onChange={e=>setForm({...form, city:e.target.value})} />
        <button type="submit">{editingId ? 'Update' : 'Add'}</button>
        {editingId && <button type="button" onClick={()=>{ setEditingId(null); setForm({name:'', city:''}) }}>Cancel</button>}
      </form>

      <table border="1" cellPadding="6">
        <thead>
          <tr><th>ID</th><th>Name</th><th>City</th><th>Actions</th></tr>
        </thead>
        <tbody>
          {(teams || []).map(t => (
            <tr key={t.id}>
              <td>{t.id}</td><td>{t.name}</td><td>{t.city}</td>
              <td>
                <button onClick={()=>onEdit(t)}>Edit</button>
                <button onClick={()=>onDelete(t.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
