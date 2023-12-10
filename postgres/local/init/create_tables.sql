CREATE TABLE customer (
    id VARCHAR(50) PRIMARY KEY,
    type VARCHAR(50),
    gender VARCHAR(50),
    fname VARCHAR(50),
    mname VARCHAR(50),
    lname VARCHAR(50),
    fullname VARCHAR(50),
    suffix VARCHAR(50),
    title VARCHAR(50),
    birthdt VARCHAR(50),
    joindt VARCHAR(50)
);

CREATE TABLE address (
    id VARCHAR(50) PRIMARY KEY,
    customerid VARCHAR(50),
    formatcode VARCHAR(50),
    type VARCHAR(50),
    line1 VARCHAR(50),
    line2 VARCHAR(50),
    citynm VARCHAR(50),
    state VARCHAR(50),
    zip5 VARCHAR(50),
    zip4 VARCHAR(50),
    countrycd VARCHAR(50),

    FOREIGN KEY (customerId) REFERENCES customer (id)
);

CREATE TABLE email (
    id VARCHAR(50) PRIMARY KEY,
    customerid VARCHAR(50),
    email VARCHAR(50),

    FOREIGN KEY (customerId) REFERENCES customer (id)
);

CREATE TABLE phone (
    id VARCHAR(50) PRIMARY KEY,
    customerid VARCHAR(50),
    phonetypecd VARCHAR(50),
    primaryind VARCHAR(50),
    timezone VARCHAR(50),
    extnbr VARCHAR(50),
    number VARCHAR(50),

    FOREIGN KEY (customerId) REFERENCES customer (id)
);

CREATE TABLE artist (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(50),
    genre VARCHAR(50)
);

CREATE TABLE venue (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(50),
    street VARCHAR(50),
    city VARCHAR(50),
    state VARCHAR(2),
    zip VARCHAR(10),
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6),
    maxcapacity INTEGER
);

CREATE TABLE event (
    id VARCHAR(50) PRIMARY KEY,
    artistid VARCHAR(50),
    venueid VARCHAR(50),
    capacity INTEGER,
    eventdate VARCHAR(50),

    FOREIGN KEY (artistid) REFERENCES artist (id),
    FOREIGN KEY (venueid) REFERENCES venue (id)
);

CREATE TABLE ticket (
    id VARCHAR(50) PRIMARY KEY,
    customerid VARCHAR(50),
    eventid VARCHAR(50),
    price DECIMAL,

    FOREIGN KEY (customerid) REFERENCES customer (id),
    FOREIGN KEY (eventid) REFERENCES event (id)
);

CREATE TABLE stream (
    id VARCHAR(50) PRIMARY KEY,
    customerid VARCHAR(50),
    artistid VARCHAR(50),
    streamtime VARCHAR(50),

    FOREIGN KEY (customerid) REFERENCES customer (id),
    FOREIGN KEY (artistid) REFERENCES artist (id)
);
