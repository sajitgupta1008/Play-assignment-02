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

insert into userdatatable(firstname,lastname,username,password,mobileno,gender,age,isadmin
,isenabled) values('sajit','gupta','sajit@gmail.com','$2a$10$OheicC7rvlhpRSUDS12G8.FLaCe.AEQkZT/8ld/asUQlUlCDYISPG',
8743922586,'male',23,true,true);

create table if not exists hobbytable(
id serial NOT NULL,
hobby VARCHAR(20) NOT NULL
);

insert into hobbytable(hobby) values ('reading'),('Listening music'),('Cricket'),('Swimming');

create table if not exists userhobbytable(
username VARCHAR(30) NOT NULL,
hobbyname VARCHAR(20) NOT NULL
);

create table if not exists assignmenttable(
id serial NOT NULL,
title VARCHAR(30) NOT NULL,
description VARCHAR NOT NULL,
PRIMARY KEY(id)
);

# --- !Downs

drop table userdatatable;
drop table hobbytable;
drop table userhobbytable;
drop table assignmenttable;