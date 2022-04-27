DROP TABLE IF EXISTS tasks;
CREATE TABLE tasks (
  id        VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
  text      VARCHAR      NOT NULL,
  day       DATETIME     NOT NULL,
  reminder  BOOLEAN      NOT NULL DEFAULT TRUE
);

DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id        VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
  name      VARCHAR(50)      NOT NULL,
  email     VARCHAR(50)      NOT NULL,
  password  VARCHAR(60)      NOT NULL,
  role      VARCHAR(50)      NOT NULL
);