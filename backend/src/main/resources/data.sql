INSERT INTO team (id, name, city) VALUES (1, 'Arsenal', 'London');
INSERT INTO team (id, name, city) VALUES (2, 'Manchester City', 'Manchester');
INSERT INTO team (id, name, city) VALUES (3, 'Liverpool', 'Liverpool');

INSERT INTO player (id, name, position, team_id) VALUES (1, 'Bukayo Saka', 'Winger', 1);
INSERT INTO player (id, name, position, team_id) VALUES (2, 'Erling Haaland', 'Striker', 2);
INSERT INTO player (id, name, position, team_id) VALUES (3, 'Virgil van Dijk', 'Defender', 3);

INSERT INTO matches (id, home_team_id, away_team_id, home_score, away_score, match_date) VALUES
(1, 1, 2, 2, 2, '2025-08-01'),
(2, 3, 1, 1, 3, '2025-08-02');

