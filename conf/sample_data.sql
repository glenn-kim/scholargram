DELETE FROM Professors;
DELETE FROM Students;
DELETE FROM Users;
DELETE FROM Majors;
DELETE FROM Schools;

INSERT INTO "Schools" VALUES 
  (1,'NHN NEXT','경기도 성남시'),
  (2,'한국 디지털 미디어 고등학교','경기도 안산시'),
  (3,'이우학교','경기도 성남시'),
  (4,'서울대학교','서울 관악구'),
  (5,'경북대학교','대구 북구');

INSERT INTO "Majors" VALUES 
  (1,1,'웹 서버 전공'),
  (2,1,'UI 전공'),
  (3,1,'모바일 전공'),
  (4,1,'게임서버 전공'),
  (5,1,'게임클라이언트 전공'),
  (6,2,'e비지니스 과'),
  (7,2,'디지털 컨텐츠 과'),
  (8,2,'웹 프로그래밍 과'),
  (9,2,'해킹 방어과'),
  (10,4,'죽을수도 있는 해병대과'),
  (11,4,'펔-유과(feat. Doho Lee)'),
  (12,4,'컴퓨터 공학과'),
  (13,4,'사회복지학과'),
  (14,3,'뽀삐 복지학과'),
  (15,3,'뽀삐 심리학과'),
  (16,3,'뽀삐 공학과');

INSERT INTO "Users" VALUES 
  (1,'김창규','ck@a.a','ck',NULL),
  (2,'김창갑','gap@a.a','gap',NULL),
  (3,'이경민','lee@a.a','lee',NULL),
  (4,'뽀삐','poppy@a.a','poppy',NULL),
  (5,'Ricky Lee','ricky@a.a','ricky',NULL),
  (6,'Doho Lee','doho@a.a','dogo',NULL),
  (7,'박태준','tj@a.a','tj',NULL),
  (8,'김교수','prof.kim@a.a','prof.kim',NULL),
  (9,'박교수','prof.park@a.a','prof.park',NULL),
  (10,'이교수','prof.lee@a.a','prof.lee',NULL),
  (11,'남교수','prof.boy@a.a','prof.boy',NULL),
  (12,'여교수','prof.girl@a.a','prof.girl',NULL);

INSERT INTO "Students" VALUES
  (1,'13김창규',1),
  (2,'13김창갑',9),
  (3,'13이경민',2),
  (4,'13뽀삐',3),
  (5,'14Ricky Lee',10),
  (6,'14Doho Lee',13),
  (7,'14박태준',3);

INSERT INTO "Professors" VALUES
  (8,1),
  (9,2),
  (10,3),
  (11,4),
  (12,5);