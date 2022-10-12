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
    customerId VARCHAR(50),
    formatCode VARCHAR(50),
    type VARCHAR(50),
    line1 VARCHAR(50),
    line2 VARCHAR(50),
    citynm VARCHAR(50),
    state VARCHAR(50),
    zip5 VARCHAR(50),
    zip4 VARCHAR(50),
    countryCd VARCHAR(50),
    FOREIGN KEY (customerId) REFERENCES customer (id)
);

CREATE TABLE email (
    id VARCHAR(50) PRIMARY KEY,
    customerId VARCHAR(50),
    email VARCHAR(50),
    FOREIGN KEY (customerId) REFERENCES customer (id)
);

CREATE TABLE phone (
    id VARCHAR(50) PRIMARY KEY,
    customerId VARCHAR(50),
    phoneTypeCD VARCHAR(50),
    primaryInd VARCHAR(50),
    timezone VARCHAR(50),
    extNbr VARCHAR(50),
    number VARCHAR(50),
    FOREIGN KEY (customerId) REFERENCES customer (id)
);
