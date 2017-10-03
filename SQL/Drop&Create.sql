DROP TABLE TUSER CASCADE CONSTRAINTS;
DROP TABLE GENDER CASCADE CONSTRAINTS;
DROP TABLE Subject CASCADE CONSTRAINTS;
DROP TABLE Blocked CASCADE CONSTRAINTS;
DROP TABLE Rating CASCADE CONSTRAINTS;
DROP TABLE Offer CASCADE CONSTRAINTS;
DROP TABLE Request CASCADE CONSTRAINTS;

DROP SEQUENCE seq_gender;
DROP SEQUENCE seq_subject;
DROP SEQUENCE seq_request;
DROP SEQUENCE seq_offer;

CREATE SEQUENCE seq_gender;
CREATE SEQUENCE seq_subject;
CREATE SEQUENCE seq_request;
CREATE SEQUENCE seq_offer;


CREATE TABLE GENDER(
    id Number,
    name VARCHAR2(20),
    CONSTRAINT pk_gender PRIMARY KEY (id)
);

Create Table Subject(
    id number,
    name VARCHAR2(25),
    CONSTRAINT pk_subject PRIMARY KEY (id)
    );

CREATE TABLE TUSER(
    username VARCHAR(20),
    password VARCHAR2(32),
    role CHAR(1),
    email VARCHAR2(30),
    name VARCHAR2 (30),
    avatar BLOB,
    averageRating NUMBER(2,1),
    education VARCHAR2(20),
    gender NUMBER,
	authkey VARCHAR(32),
	authexpirydate TIMESTAMP,
    CONSTRAINT pk_tuser PRIMARY KEY (username),
    CONSTRAINT fk_user_gender FOREIGN KEY (gender) references GENDER (id)
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

Create Table Offer (
    id NUMBER,
    postedOn TIMESTAMP,
    dueDate Date,
    Subject Number,
    isActive CHAR(1),
    description VARCHAR2(500),
    username VARCHAR2(20),
    CONSTRAINT pk_offer PRIMARY KEY (id),
    CONSTRAINT fk_offer_user FOREIGN KEY (username) references TUser (username),
    CONSTRAINT fk_offer_subject FOREIGN KEY (subject) references Subject (id)
);

Create Table Request (
    id NUMBER,
    postedOn Date,
    dueDate Date,
    Subject Number,
    isActive CHAR(1),
    description VARCHAR2(500),
    username VARCHAR2(20),
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_request_user FOREIGN KEY (username) references TUser (username),
    CONSTRAINT fk_request_subject FOREIGN KEY (subject) references Subject (id)
);
