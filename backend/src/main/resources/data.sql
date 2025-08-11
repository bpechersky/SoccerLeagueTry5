-- Teams: omit id
INSERT INTO team (name, city) VALUES ('Arsenal', 'London');
INSERT INTO team (name, city) VALUES ('Manchester City', 'Manchester');
INSERT INTO team (name, city) VALUES ('Liverpool', 'Liverpool');
INSERT INTO team (name, city) VALUES ('Chelsea', 'London');

-- Players: omit id; keep team_id FKs (ids will match the order teams are inserted)
INSERT INTO player (name, position, team_id) VALUES ('Bukayo Saka', 'Winger', 1);
INSERT INTO player (name, position, team_id) VALUES ('Erling Haaland', 'Striker', 2);
INSERT INTO player (name, position, team_id) VALUES ('Virgil van Dijk', 'Defender', 3);
INSERT INTO player (name, position, team_id) VALUES ('Robert Sanchez', 'Goalie', 4);

-- Matches: omit id; reference team ids from above
INSERT INTO matches (home_team_id, away_team_id, home_score, away_score, match_date) VALUES
(1, 2, 2, 2, '2025-08-01'),
(3, 1, 1, 3, '2025-08-02');


