# adbeit スキーマ

# --- !Ups
CREATE TABLE user (
  id bigint auto_increment primary key,
  active int NOT NULL DEFAULT 1,
  name varchar(255) NOT NULL,
  email varchar(255) DEFAULT null,
  password varchar(255) DEFAULT null,
  gender int NOT NULL,
  rank int DEFAULT 0 NOT NULL,
  exp int DEFAULT 0 NOT NULL,
  create_date timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  modified_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE facebook (
  id bigint NOT NULL primary key,
  active int NOT NULL DEFAULT 1,
  user_id bigint unique NOT NULL,
  accesstoken varchar(255) NOT NULL,
  expiration_date datetime NOT NULL,
  foreign key(user_id) references user(id) on delete cascade
);

CREATE TABLE twitter (
  id bigint primary key,
  active int NOT NULL DEFAULT 1,
  user_id bigint unique NOT NULL,
  accesstoken varchar(255) NOT NULL,
  expiration_date datetime NOT NULL,
  foreign key(user_id) references user(id) on delete cascade
);

CREATE TABLE company (
  id bigint auto_increment primary key,
  active int NOT NULL DEFAULT 1,
  name varchar(255) NOT NULL,
  address varchar(255) DEFAULT null,
  url varchar(255) DEFAULT null,
  create_date timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  modified_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE advertisement (
  id bigint auto_increment primary key,
  active int NOT NULL DEFAULT 1,
  company_id bigint unique NOT NULL,
  name varchar(255) NOT NULL,
  description varchar(255) DEFAULT null,
  url varchar(255) NOT NULL,
  point int NOT NULL,
  create_date timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  modified_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  expiration_date datetime NOT NULL,
  foreign key(company_id) references company(id) on delete cascade
);

CREATE TABLE user_advertisement (
  u_id bigint unique NOT NULL,
  ad_id bigint unique NOT NULL,
  foreign key(u_id) references user(id) on delete cascade,
  foreign key(ad_id) references advertisement(id) on delete cascade
);

CREATE TABLE likes (
  id bigint auto_increment primary key,
  active int NOT NULL DEFAULT 1,
  user_id bigint unique NOT NULL,
  ad_id bigint unique NOT NULL,
  url varchar(255) NOT NULL,
  create_date timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  foreign key(user_id) references user(id) on delete cascade,
  foreign key(ad_id) references advertisement(id) on delete cascade
);

CREATE TABLE session (
  id varchar(255) primary key,
  user_id bigint unique NOT NULL,
  hostname varchar(255) NOT NULL,
  create_date timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  expiration_date datetime NOT NULL,
  foreign key(user_id) references user(id) on delete cascade
);
# --- !Downs

DROP TABLE if exists user_advertisement;
DROP TABLE if exists advertisement;
DROP TABLE if exists session;
DROP TABLE if exists likes;
DROP TABLE if exists twitter;
DROP TABLE if exists facebook;
DROP TABLE if exists company;
DROP TABLE if exists user;

