-- -----------------------------------------------------
-- Table Users
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Users (
  userId INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(40) NOT NULL,
  email VARCHAR(250) NOT NULL,
  passwd CHAR(128) NULL,
  thirdAuth VARCHAR(250) NULL,
  PRIMARY KEY (userId))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table Schools
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Schools (
  schoolId INT NOT NULL AUTO_INCREMENT,
  schoolName VARCHAR(40) NOT NULL,
  location VARCHAR(250) NULL,
  PRIMARY KEY (schoolId))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table Schools
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Schools (
  schoolId INT NOT NULL AUTO_INCREMENT,
  schoolName VARCHAR(40) NOT NULL,
  location VARCHAR(250) NULL,
  PRIMARY KEY (schoolId))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table Professors
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Professors (
  userId INT NOT NULL,
  work INT NOT NULL,
  PRIMARY KEY (userId),
  CONSTRAINT fk_Professors_Schools
    FOREIGN KEY (work)
    REFERENCES Schools (schoolId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_Professors_Users1
    FOREIGN KEY (userId)
    REFERENCES Users (userId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_Professors_Schools_idx ON Professors(work ASC);


-- -----------------------------------------------------
-- Table Majors
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Majors (
  majorId INT NOT NULL AUTO_INCREMENT,
  schoolId INT NOT NULL,
  majorName VARCHAR(40) NOT NULL,
  PRIMARY KEY (majorId),
  CONSTRAINT fk_Majors_Schools1
    FOREIGN KEY (schoolId)
    REFERENCES Schools (schoolId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_Majors_Schools1_idx ON Majors(schoolId ASC);

-- -----------------------------------------------------
-- Table Students
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Students (
  userId INT NOT NULL,
  defaultIdentity VARCHAR(40) NULL,
  majorId INT NOT NULL,
  PRIMARY KEY (userId),
  CONSTRAINT fk_Students_Users2
    FOREIGN KEY (userId)
    REFERENCES Users (userId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_Students_Majors1
    FOREIGN KEY (majorId)
    REFERENCES Majors (majorId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX k_Students_Users2_idx ON Students(userId ASC);
CREATE INDEX fk_Students_Majors1_idx ON Students(majorId ASC);

-- -----------------------------------------------------
-- Table Classes
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Classes (
  classId INT NOT NULL AUTO_INCREMENT,
  className VARCHAR(250) NOT NULL,
  professorId INT NOT NULL,
  startDate DATE NULL,
  endDate DATE NULL,
  createDatetime DATETIME NOT NULL,
  schoolId INT NOT NULL,
  PRIMARY KEY (classId),
  CONSTRAINT fk_Classes_Professors1
    FOREIGN KEY (professorId)
    REFERENCES Professors (userId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_Classes_Schools1
    FOREIGN KEY (schoolId)
    REFERENCES Schools (schoolId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_Classes_Professors1_idx ON Classes(professorId ASC);
CREATE INDEX fk_Classes_Schools1_idx ON Classes(schoolId ASC);

-- -----------------------------------------------------
-- Table TimelineItems
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS TimelineItems (
  itemId INT NOT NULL AUTO_INCREMENT,
  classId INT NOT NULL,
  type INT NOT NULL,
  publishDatetime DATETIME NOT NULL,
  PRIMARY KEY (itemId),
  CONSTRAINT fk_TimelineItems_Classes1
    FOREIGN KEY (classId)
    REFERENCES Classes (classId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_TimelineItems_Classes1_idx ON TimelineItems(classId ASC);

-- -----------------------------------------------------
-- Table ClassRegistration
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS ClassRegistrations (
  userId INT NOT NULL,
  classId INT NOT NULL,
  identity VARCHAR(40) NOT NULL,
  major VARCHAR(40) NOT NULL,
  joinedDatetime DATETIME NOT NULL,
  accepted INT NOT NULL,
  PRIMARY KEY (userId, classId),
  CONSTRAINT fk_Students_has_Classes_Students1
    FOREIGN KEY (userId)
    REFERENCES Students (userId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_Students_has_Classes_Classes1
    FOREIGN KEY (classId)
    REFERENCES Classes (classId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_Students_has_Classes_Students1_idx ON ClassRegistrations(userId ASC);
CREATE INDEX fk_Students_has_Classes_Classes1_idx ON ClassRegistrations(classId ASC);

-- -----------------------------------------------------
-- Table Alerts
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Alerts (
  itemId INT NOT NULL,
  text VARCHAR(250) NOT NULL,
  PRIMARY KEY (itemId),
  CONSTRAINT fk_Alerts_TimelineItems1
    FOREIGN KEY (itemId)
    REFERENCES TimelineItems (itemId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table Assignments
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Assignments (
  itemId INT NOT NULL,
  title VARCHAR(40) NOT NULL,
  description VARCHAR(250) NOT NULL,
  dueDatetime DATETIME NOT NULL,
  PRIMARY KEY (itemId),
  CONSTRAINT fk_Assignments_TimelineItems1
    FOREIGN KEY (itemId)
    REFERENCES TimelineItems (itemId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table Lectures
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Lectures (
  itemId INT NOT NULL,
  title VARCHAR(40) NOT NULL,
  PRIMARY KEY (itemId),
  CONSTRAINT fk_Lectures_TimelineItems1
    FOREIGN KEY (itemId)
    REFERENCES TimelineItems (itemId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table Quizzes
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Quizzes (
  itemId INT NOT NULL,
  PRIMARY KEY (itemId),
  CONSTRAINT fk_Quizzes_TimelineItems1
    FOREIGN KEY (itemId)
    REFERENCES TimelineItems (itemId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table Attachments
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Attachments (
  attachmentId CHAR(128) NOT NULL AUTO_INCREMENT,
  owner INT NOT NULL,
  directory VARCHAR(250) NOT NULL,
  filename VARCHAR(250) NOT NULL,
  PRIMARY KEY (attachmentId, owner),
  CONSTRAINT fk_Attachments_Users1
    FOREIGN KEY (owner)
    REFERENCES Users (userId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_Attachments_Users1_idx ON Attachments(owner ASC);

-- -----------------------------------------------------
-- Table AssignmentAttachments
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS AssignmentAttachments (
  attachmentId CHAR(128) NOT NULL,
  owner INT NOT NULL,
  itemId INT NOT NULL,
  PRIMARY KEY (attachmentId, owner),
  CONSTRAINT fk_AssignmentAttachments_Attachments1
    FOREIGN KEY (attachmentId , owner)
    REFERENCES Attachments (attachmentId , owner)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_AssignmentAttachments_Assignments1
    FOREIGN KEY (itemId)
    REFERENCES Assignments (itemId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_AssignmentAttachments_Attachments1_idx ON AssignmentAttachments(attachmentId ASC, owner ASC);
CREATE INDEX fk_AssignmentAttachments_Assignments1_idx ON AssignmentAttachments(itemId ASC);


-- -----------------------------------------------------
-- Table Submissions
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS Submissions (
  submissionId INT NOT NULL AUTO_INCREMENT,
  assignmentId INT NOT NULL,
  userId INT NOT NULL,
  description VARCHAR(250) NOT NULL,
  createDatetime DATETIME NOT NULL,
  PRIMARY KEY (submissionId),
  CONSTRAINT fk_Submissions_Assignments1
    FOREIGN KEY (assignmentId)
    REFERENCES Assignments (itemId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_Submissions_Students1
    FOREIGN KEY (userId)
    REFERENCES Students (userId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


CREATE INDEX fk_Submissions_Assignments1_idx ON Submissions(assignmentId ASC);
CREATE INDEX fk_Submissions_Students1_idx ON Submissions(userId ASC);

-- -----------------------------------------------------
-- Table SubmissionAttachments
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS SubmissionAttachments (
  attachmentId CHAR(128) NOT NULL,
  owner INT NOT NULL,
  submissionId INT NOT NULL,
  PRIMARY KEY (attachmentId, owner),
  CONSTRAINT fk_SubmissionAttachments_Attachments1
    FOREIGN KEY (attachmentId , owner)
    REFERENCES Attachments (attachmentId , owner)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_SubmissionAttachments_Submissions1
    FOREIGN KEY (submissionId)
    REFERENCES Submissions (submissionId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_SubmissionAttachments_Submissions1_idx ON SubmissionAttachments(submissionId ASC);

-- -----------------------------------------------------
-- Table LectureAttachments
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS LectureAttachments (
  attachmentId CHAR(128) NOT NULL,
  owner INT NOT NULL,
  itemId INT NOT NULL,
  PRIMARY KEY (attachmentId, owner),
  CONSTRAINT fk_LectureAttachments_Attachments1
    FOREIGN KEY (attachmentId , owner)
    REFERENCES Attachments (attachmentId , owner)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_LectureAttachments_Lectures1
    FOREIGN KEY (itemId)
    REFERENCES Lectures (itemId)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_LectureAttachments_Lectures1_idx ON LectureAttachments(itemId ASC);