DROP TABLE tsession CASCADE CONSTRAINTS;

DROP TABLE tuser CASCADE CONSTRAINTS;

DROP TABLE subject CASCADE CONSTRAINTS;

DROP TABLE blocked CASCADE CONSTRAINTS;

DROP TABLE rating CASCADE CONSTRAINTS;

DROP TABLE entry CASCADE CONSTRAINTS;

DROP TABLE tcomment CASCADE CONSTRAINTS;

DROP SEQUENCE seq_subject;

DROP SEQUENCE seq_comment;

DROP SEQUENCE seq_entry;

DROP INDEX spatial_index_entry_location;

CREATE SEQUENCE seq_entry;

CREATE SEQUENCE seq_subject;

CREATE SEQUENCE seq_comment;

CREATE TABLE subject (
    id         NUMBER,
    dename     VARCHAR2(25),
    enname     VARCHAR2(25),
    isactive   CHAR(1),
    CONSTRAINT pk_tsubject PRIMARY KEY ( id ),
    CONSTRAINT u_subject_de_name UNIQUE ( dename ),
    CONSTRAINT u_subject_en_name UNIQUE ( enname )
);

CREATE TABLE tuser (
    username        VARCHAR(20),
    password        VARCHAR2(64),
    role            CHAR(1),
    email           VARCHAR2(50),
    name            VARCHAR2(30),
    avatar          BLOB,
    averagerating   NUMBER(2,1),
    education       VARCHAR2(50),
    gender          CHAR(1),
    CONSTRAINT pk_tuser PRIMARY KEY ( username ),
    CONSTRAINT u_user_email UNIQUE ( email )
);

CREATE TABLE tsession (
    username     VARCHAR(20),
    authkey      VARCHAR(32),
    expirydate   TIMESTAMP,
    CONSTRAINT pk_session PRIMARY KEY ( authkey ),
    CONSTRAINT fk_session_username FOREIGN KEY ( username )
        REFERENCES tuser ( username )
);

CREATE TABLE blocked (
    username   VARCHAR(20),
    reason     VARCHAR2(100),
    duedate    DATE,
    CONSTRAINT pk_blocked PRIMARY KEY ( username ),
    CONSTRAINT fk_blocked_user FOREIGN KEY ( username )
        REFERENCES tuser ( username )
);

CREATE TABLE rating (
    text         VARCHAR2(500),
    rateduser    VARCHAR2(20),
    ratinguser   VARCHAR2(20),
    stars        NUMBER(1),
    writtenon    DATE,
    CONSTRAINT pk_rating PRIMARY KEY ( rateduser,ratinguser ),
    CONSTRAINT fk_rated_user FOREIGN KEY ( rateduser )
        REFERENCES tuser ( username ),
    CONSTRAINT fk_rating_user FOREIGN KEY ( ratinguser )
        REFERENCES tuser ( username )
);

CREATE TABLE entry (
    id            NUMBER,
    postedon      DATE,
    duedate       DATE,
    subject       NUMBER,
    isactive      CHAR(1),
    description   VARCHAR2(500),
    username      VARCHAR2(20),
    flag          CHAR(1),
    headline      VARCHAR2(50),
    location      mdsys.sdo_geometry,
    CONSTRAINT pk_entry PRIMARY KEY ( id ),
    CONSTRAINT fk_entry_user FOREIGN KEY ( username )
        REFERENCES tuser ( username ),
    CONSTRAINT fk_entry_subject FOREIGN KEY ( subject )
        REFERENCES subject ( id )
);

CREATE TABLE tcomment (
    id         NUMBER,
    username   VARCHAR2(20),
    postedon   TIMESTAMP,
    text       VARCHAR2(300),
    entryid    NUMBER,
    CONSTRAINT pk_tcomment PRIMARY KEY ( id ),
    CONSTRAINT fk_tcomment_user FOREIGN KEY ( username )
        REFERENCES tuser ( username ),
    CONSTRAINT fk_tcomment_entry FOREIGN KEY ( entryid )
        REFERENCES entry ( id )
);

DELETE FROM user_sdo_geom_metadata WHERE
    table_name like upper('entry');

INSERT INTO user_sdo_geom_metadata (
    table_name,
    column_name,
    diminfo,
    srid
) VALUES (
    'entry',
    'lOCATION',
    sdo_dim_array(
        sdo_dim_element(
            'LONGITUDE',
            -180,
            180,
            0.5
        ),
        sdo_dim_element(
            'LATITUDE',
            -90,
            90,
            0.5
        )
    ),
    8307
);

CREATE INDEX spatial_index_entry_location ON
    entry ( location )
        INDEXTYPE IS mdsys.spatial_index;

INSERT INTO tuser VALUES (
    'admin',
    '21232f297a57a5a743894a0e4a801fc3',
    'A',
    'admin@tutoringtrain.com',
    'Admin',
    NULL,
    NULL,
    'HTL',
    'N'
);

COMMIT;
