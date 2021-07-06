CREATE DATABASE test_db;
\c test_db

CREATE TABLE dept (
  "deptno" INTEGER NOT NULL,
  "dname" TEXT NOT NULL,
  "loc" TEXT NOT NULL
);

INSERT INTO dept VALUES(1, 'A支店', '北海道');
INSERT INTO dept VALUES(2, 'B支店', '東京都');
INSERT INTO dept VALUES(3, 'C支店', '大阪府');