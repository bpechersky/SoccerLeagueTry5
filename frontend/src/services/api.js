import axios from 'axios'

export const api = axios.create({
  baseURL: 'http://localhost:8080/api'
})

// Teams
export const getTeams = () => api.get('/teams')
export const getTeam = (id) => api.get(`/teams/${id}`)
export const createTeam = (body) => api.post('/teams', body)
export const updateTeam = (id, body) => api.put(`/teams/${id}`, body)
export const deleteTeam = (id) => api.delete(`/teams/${id}`)

// Players
export const getPlayers = () => api.get('/players')
export const getPlayersByTeam = (teamId) => api.get(`/players/team/${teamId}`)
export const createPlayer = (body) => api.post('/players', body)
export const updatePlayer = (id, body) => api.put(`/players/${id}`, body)
export const deletePlayer = (id) => api.delete(`/players/${id}`)

// Matches
export const getMatches = () => api.get('/matches')
export const createMatch = (body) => api.post('/matches', body)
export const updateMatch = (id, body) => api.put(`/matches/${id}`, body)
export const deleteMatch = (id) => api.delete(`/matches/${id}`)

// Standings
export const getStandings = () => api.get('/standings')
