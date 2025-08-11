import React, { useEffect, useState, useCallback } from 'react';
import { getMatches, getTeams, createMatch, updateMatch, deleteMatch } from '../services/api';

export default function MatchesPage() {
  const [matches, setMatches] = useState([]);
  const [teams, setTeams] = useState([]);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState({
    id: null,
    homeTeamId: '',
    awayTeamId: '',
    homeScore: '',
    awayScore: '',
    matchDate: '',
  });

  const fetchAll = useCallback(async () => {
    try {
      const [m, t] = await Promise.all([getMatches(), getTeams()]);
      const mData = Array.isArray(m?.data) ? m.data : m?.data?.content ?? [];
      const tData = Array.isArray(t?.data) ? t.data : t?.data?.content ?? [];

      // Sort by date desc if present
      mData.sort((a, b) => (b.matchDate || '').localeCompare(a.matchDate || ''));

      setMatches(mData);
      setTeams(tData);
    } catch (err) {
      console.error('Failed to load matches/teams', err);
      setMatches([]);
      setTeams([]);
    }
  }, []);

  useEffect(() => {
    fetchAll();
  }, [fetchAll]);

  const resetForm = () =>
    setForm({ id: null, homeTeamId: '', awayTeamId: '', homeScore: '', awayScore: '', matchDate: '' });

  const onSubmit = async (e) => {
    e.preventDefault();
    if (!form.homeTeamId || !form.awayTeamId) return;

    // Build backend-friendly body
    const body = {
      homeTeam: { id: Number(form.homeTeamId) },
      awayTeam: { id: Number(form.awayTeamId) },
      homeScore: form.homeScore !== '' ? Number(form.homeScore) : null,
      awayScore: form.awayScore !== '' ? Number(form.awayScore) : null,
      matchDate: form.matchDate || null,
    };

    try {
      setSaving(true);
      if (form.id) {
        await updateMatch(form.id, body);
      } else {
        await createMatch(body);
      }
      resetForm();
      await fetchAll(); // ✅ force re-render from fresh server state
    } catch (err) {
      console.error('Create/Update match failed', err);
    } finally {
      setSaving(false);
    }
  };

  const onEdit = (m) => {
    setForm({
      id: m.id,
      homeTeamId: m.homeTeam?.id ?? '',
      awayTeamId: m.awayTeam?.id ?? '',
      homeScore: m.homeScore ?? '',
      awayScore: m.awayScore ?? '',
      matchDate: m.matchDate ?? '',
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const onDelete = async (id) => {
    try {
      await deleteMatch(id);
      await fetchAll(); // ✅ refresh after delete
    } catch (err) {
      console.error('Delete match failed', err);
    }
  };

  return (
    <div>
      <h2>Matches</h2>

      <form
        onSubmit={onSubmit}
        style={{ display: 'grid', gridTemplateColumns: 'repeat(6, minmax(120px, 1fr))', gap: 8, marginBottom: 12 }}
      >
        <select
          value={form.homeTeamId}
          onChange={(e) => setForm({ ...form, homeTeamId: e.target.value })}
        >
          <option value="">Home</option>
          {teams.map((t) => (
            <option key={t.id} value={t.id}>
              {t.name}
            </option>
          ))}
        </select>

        <select
          value={form.awayTeamId}
          onChange={(e) => setForm({ ...form, awayTeamId: e.target.value })}
        >
          <option value="">Away</option>
          {teams.map((t) => (
            <option key={t.id} value={t.id}>
              {t.name}
            </option>
          ))}
        </select>

        <input
          type="number"
          placeholder="Home Score"
          value={form.homeScore}
          onChange={(e) => setForm({ ...form, homeScore: e.target.value })}
        />
        <input
          type="number"
          placeholder="Away Score"
          value={form.awayScore}
          onChange={(e) => setForm({ ...form, awayScore: e.target.value })}
        />
        <input
          type="date"
          value={form.matchDate}
          onChange={(e) => setForm({ ...form, matchDate: e.target.value })}
        />

        <div style={{ display: 'flex', gap: 8 }}>
          <button type="submit" disabled={saving}>
            {form.id ? 'Update' : 'Add'}
          </button>
          {form.id && (
            <button type="button" onClick={resetForm} disabled={saving}>
              Cancel
            </button>
          )}
        </div>
      </form>

      <table border="1" cellPadding="6">
        <thead>
          <tr>
            <th>Date</th>
            <th>Home</th>
            <th>Score</th>
            <th>Away</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {matches.map((m) => (
            <tr key={m.id}>
              <td>{m.matchDate || '-'}</td>
              <td>{m.homeTeam?.name || '-'}</td>
              <td>
                {(m.homeScore ?? '-')}&nbsp;:&nbsp;{(m.awayScore ?? '-')}
              </td>
              <td>{m.awayTeam?.name || '-'}</td>
              <td>
                <button onClick={() => onEdit(m)}>Edit</button>
                <button onClick={() => onDelete(m.id)}>Delete</button>
              </td>
            </tr>
          ))}
          {!matches.length && (
            <tr>
              <td colSpan={5} style={{ textAlign: 'center' }}>
                No matches yet
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
