create database divvy;

use divvy;


CREATE TABLE `taskassignment` (
  `taskassignid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(10) DEFAULT NULL,
  `taskname` varchar(25) DEFAULT NULL,
  `taskpoints` float(10,3) DEFAULT NULL,
  `startdate` date DEFAULT NULL,
  `enddate` date DEFAULT NULL,
  `frequency` varchar(10) DEFAULT NULL,
  `completionstatus` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`taskassignid`)
) ENGINE=InnoDB AUTO_INCREMENT=11094 DEFAULT CHARSET=utf8;



CREATE TABLE `tasks` (
  `taskid` mediumint(9) NOT NULL AUTO_INCREMENT,
  `taskname` varchar(25) DEFAULT NULL,
  `taskpoints` float(10,3) DEFAULT NULL,
  `frequency` varchar(10) DEFAULT NULL,
  `day` varchar(10) DEFAULT NULL,
  `taskdescription` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`taskid`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8;



CREATE TABLE `users` (
  `username` varchar(10) NOT NULL,
  `firstname` varchar(25) DEFAULT NULL,
  `lastname` varchar(25) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `password` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


create table weeklyupdate(
wkey int Primary Key,
nextdate Date,
points float(10,3));

insert into weeklyupdate values(1,'2014-12-01',20);


CREATE TABLE `weeklypoints` (
  `weekptkey` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(10) NOT NULL,
  `nextdate` date DEFAULT NULL,
  `targetpoints` float(10,3) DEFAULT NULL,
  `completedpoints` float(10,3) DEFAULT NULL,
  `requiredpoints` float(10,3) DEFAULT NULL,
  `pointsperweek` float(10,3) DEFAULT NULL,
  PRIMARY KEY (`weekptkey`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8;

insert into weeklypoints values(1,'harini',STR_TO_DATE('2014-11-03', '%Y-%m-%d'),0,0,0,10);
insert into weeklypoints values(2,'jaya',STR_TO_DATE('2014-11-03', '%Y-%m-%d'),0,0,0,20);
insert into weeklypoints values(3,'pranav',STR_TO_DATE('2014-11-03', '%Y-%m-%d'),0,0,0,5);
insert into weeklypoints values(4,'sayali',STR_TO_DATE('2014-11-03', '%Y-%m-%d'),0,0,0,0);


CREATE TABLE `advancetestdate` (
  `testkey` int(11) NOT NULL AUTO_INCREMENT,
  `datediff` int(10) DEFAULT NULL,
  PRIMARY KEY (`testkey`)
);


insert into advancetestdate values(1,0);
