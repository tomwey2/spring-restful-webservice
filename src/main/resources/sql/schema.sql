DROP TABLE tasks;
CREATE TABLE tasks (
  id        VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
  text      VARCHAR      NOT NULL,
  day       DATETIME     NOT NULL,
  reminder  BOOLEAN      NOT NULL DEFAULT TRUE
);

DROP TABLE users;
CREATE TABLE users (
  id         VARCHAR(60)  DEFAULT RANDOM_UUID() PRIMARY KEY,
  first_name  VARCHAR(50)      NOT NULL,
  last_name   VARCHAR(50)      NOT NULL,
  email      VARCHAR(50)      NOT NULL
);