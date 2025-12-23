INSERT INTO addiction (name, category)
VALUES ('weed', 'weed'),
       ('badLanguage', 'badLanguage'),
       ('lying', 'lying'),
       ('porn', 'porn'),
       ('coffee', 'coffee'),
       ('fastFood', 'fastFood'),
       ('smoking', 'smoking'),
       ('soda', 'soda'),
       ('tv', 'tv'),
       ('overfeeding', 'overfeeding'),
       ('sugar', 'sugar'),
       ('youtube', 'youtube'),
       ('reddit', 'reddit'),
       ('instagram', 'instagram'),
       ('x', 'x'),
       ('facebook', 'facebook'),
       ('drugs', 'drugs'),
       ('gambling', 'gambling'),
       ('pills', 'pills'),
       ('alcohol', 'alcohol'),
       ('videoGames', 'videoGames'),
       ('shopping', 'shopping');

INSERT INTO sobriety_achievement (id, duration, duration_type, minute_duration, next_achievement_id)
VALUES ('14196655-dff7-4b82-bde0-9775b561fcf2', 1, 'MINUTE', 1, '5872f0ff-dee0-4552-adaa-1d94b43424a4'),
       ('5872f0ff-dee0-4552-adaa-1d94b43424a4', 1, 'DAY', 1440, 'd7cc62bd-0ac2-47a6-878d-b46dcb097316'),
       ('d7cc62bd-0ac2-47a6-878d-b46dcb097316', 3, 'DAY', 4320, 'bc79b3eb-1b4d-4034-a3f9-8443e20255c3'),
       ('bc79b3eb-1b4d-4034-a3f9-8443e20255c3', 1, 'YEAR', 525600, '0b398a40-0041-4aaa-ae7b-f0a61d080c86'),
       ('0b398a40-0041-4aaa-ae7b-f0a61d080c86', 10, 'YEAR', 5256000, NULL);

INSERT INTO app_config (id, reset_timer_message_count)
VALUES (0, 0);

INSERT INTO localization (locale, localization_key)
VALUES ('TURKISH', 'resetTimer-0', 'Hala mücadeledeyiz'),
       ('ENGLISH', 'resetTimer-0', 'Still fighting!'),