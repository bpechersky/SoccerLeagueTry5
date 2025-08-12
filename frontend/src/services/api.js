// src/services/api.js
import axios from 'axios';

// Read once and normalize (remove trailing slash)
const API_BASE = (import.meta.env.VITE_API_BASE || '').replace(/\/$/, '');

// If you set VITE_API_BASE, we hit that host; otherwise we rely on Vite proxy and call /api
const baseURL = API_BASE ? `${API_BASE}/api` : '/api';

export const api = axios.create({ baseURL });

// Teams
export const getTeams = () => api.get('/teams');
export const getTeam = (id) => api.get(`/teams/${id}`);
export const createTeam = (body) => api.post('/teams', body);
export const updateTeam = (id, body) => api.put(`/teams/${id}`, body);
export const deleteTeam = (id) => api.delete(`/teams/${id}`);

// Players
export const getPlayers = () => api.get('/players');
export const getPlayersByTeam = (teamId) => api.get(`/players/team/${teamId}`);
export const createPlayer = (body) => api.post('/players', body);
export const updatePlayer = (id, body) => api.put(`/players/${id}`, body);
export const deletePlayer = (id) => api.delete(`/players/${id}`);

// Matches
export const getMatches = () => api.get('/matches');
export const createMatch = (body) => api.post('/matches', body);
export const updateMatch = (id, body) => api.put(`/matches/${id}`, body);
export const deleteMatch = (id) => api.delete(`/matches/${id}`);

// Standings
export const getStandings = () => api.get('/standings');
