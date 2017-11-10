DROP TABLE Tsession CASCADE CONSTRAINTS;
DROP TABLE TUSER CASCADE CONSTRAINTS;
DROP TABLE Subject CASCADE CONSTRAINTS;
DROP TABLE Blocked CASCADE CONSTRAINTS;
DROP TABLE Rating CASCADE CONSTRAINTS;
DROP TABLE Entry CASCADE CONSTRAINTS;

DROP SEQUENCE seq_subject;
DROP SEQUENCE seq_entry;

CREATE SEQUENCE seq_entry;
CREATE SEQUENCE seq_subject;


Create Table Subject(
    id number,
    dename VARCHAR2(25),
	enname VARCHAR2(25),
	isActive CHAR(1),
    CONSTRAINT pk_tsubject PRIMARY KEY (id),
    CONSTRAINT u_subject_de_name UNIQUE (dename),
	CONSTRAINT u_subject_en_name UNIQUE (enname)
    );

CREATE TABLE TUSER(
    username VARCHAR(20),
    password VARCHAR2(64),
    role CHAR(1),
    email VARCHAR2(50),
    name VARCHAR2 (30),
    avatar BLOB,
    averageRating NUMBER(2,1),
    education VARCHAR2(50),
    gender char(1),
    CONSTRAINT pk_tuser PRIMARY KEY (username),
    CONSTRAINT u_user_email UNIQUE (email)
);

CREATE TABLE TSESSION(
	username VARCHAR(20),
	authkey VARCHAR(32),
	expirydate TIMESTAMP,
	CONSTRAINT pk_session PRIMARY KEY (authkey),
    CONSTRAINT fk_session_username FOREIGN KEY (username) references TUSER (username)
);

Create Table Blocked(
    username VARCHAR(20),
    reason VARCHAR2(100),
    dueDate DATE,
    CONSTRAINT pk_blocked PRIMARY KEY (username),
    CONSTRAINT fk_blocked_user FOREIGN KEY (username) references TUser (username)
);


Create Table Rating (
    text VARCHAR2(500),
    ratedUser VARCHAR2(20),
    ratingUser VARCHAR2 (20),
    stars number(1),
    writtenOn DATE,
    CONSTRAINT pk_rating PRIMARY KEY (ratedUser,ratingUser),
    CONSTRAINT fk_rated_user FOREIGN KEY (ratedUser) references TUser (username),
    CONSTRAINT fk_rating_user FOREIGN KEY (ratingUser) references TUser (username)
);


Create Table Entry (
    id NUMBER,
    postedOn Date,
    dueDate Date,
    Subject Number,
    isActive CHAR(1),
    description VARCHAR2(500),
    username VARCHAR2(20),
	flag CHAR(1),
	headline VARCHAR2(50)
    CONSTRAINT pk_entry PRIMARY KEY (id),
    CONSTRAINT fk_entry_user FOREIGN KEY (username) references TUser (username),
    CONSTRAINT fk_entry_subject FOREIGN KEY (subject) references Subject (id)
);



INSERT INTO TUser VALUES ('admin', '21232f297a57a5a743894a0e4a801fc3', 'A', 'admin@tutoringtrain.com', 'Admin', null, null, 'HTL', 'N');
commit;
