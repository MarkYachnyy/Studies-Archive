-- Logical/Relational Model DDL
CREATE TABLE Display (
  id BIGINT PRIMARY KEY,
  currentTime TIMESTAMP
);

CREATE TABLE Schedule (
  id BIGINT PRIMARY KEY,
  lastUpdate TIMESTAMP
);

CREATE TABLE TrainSchedule (
  id BIGINT PRIMARY KEY,
  trainId BIGINT NOT NULL,
  destinationStation VARCHAR(100) NOT NULL,
  plannedArrival TIMESTAMP NOT NULL,
  plannedDeparture TIMESTAMP NOT NULL
);

CREATE TABLE Advertisement (
  id BIGINT PRIMARY KEY,
  text VARCHAR(500) NOT NULL,
  displayOrder INTEGER NOT NULL
);

CREATE TABLE Sensor (
  id BIGINT PRIMARY KEY
);

CREATE TABLE SensorEvent (
  id BIGINT PRIMARY KEY,
  sensorId BIGINT NOT NULL REFERENCES Sensor(id),
  eventTime TIMESTAMP NOT NULL,
  eventType VARCHAR(20) NOT NULL
);

CREATE TABLE Violation (
  id BIGINT PRIMARY KEY,
  type VARCHAR(20) NOT NULL,
  actualTime TIMESTAMP NOT NULL,
  plannedTime TIMESTAMP NOT NULL,
  sensorEventId BIGINT NOT NULL REFERENCES SensorEvent(id),
  trainScheduleId BIGINT NOT NULL REFERENCES TrainSchedule(id)
);

-- Связи 1:1, 1:N
ALTER TABLE Display ADD CONSTRAINT FK_Display_Schedule FOREIGN KEY (id) REFERENCES Schedule(id);
ALTER TABLE Schedule ADD CONSTRAINT FK_Schedule_TrainSchedule FOREIGN KEY (id) REFERENCES TrainSchedule(id); -- Пример
