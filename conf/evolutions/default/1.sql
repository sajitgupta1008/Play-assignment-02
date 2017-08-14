

# --- !Ups

create table if not exists userdatatable (
id serial NOT NULL,
firstname VARCHAR(20) NOT NULL,
middlename VARCHAR(20) ,
lastname VARCHAR(20) NOT NULL,
username VARCHAR(30) NOT NULL,
password VARCHAR NOT NULL,
mobileno bigint NOT NULL,
gender VARCHAR(10) NOT NULL,
age int NOT NULL,
isadmin boolean NOT NULL,
isenabled boolean NOT NULL,
PRIMARY KEY(id)
);

# --- !Downs

drop table userdatatable;
