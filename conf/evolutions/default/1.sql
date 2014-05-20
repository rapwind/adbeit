# adbeit スキーマ

# --- !Ups
CREATE TABLE areas (
  id int auto_increment primary key,
  active int NOT NULL DEFAULT 0,
  name varchar(255) NOT NULL,
  create_date timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
);

CREATE TABLE categories (
  id int auto_increment primary key,
  active int NOT NULL DEFAULT 0,
  name varchar(255) NOT NULL,
  create_date timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
);

CREATE TABLE users (
  id bigint auto_increment primary key,
  active int NOT NULL DEFAULT 1,
  name varchar(255) NOT NULL,
  email varchar(255) DEFAULT NULL,
  password varchar(255) DEFAULT NULL,
  gender int NOT NULL,
  area_id int DEFAULT NULL,
  category_id int DEFAULT NULL,
  rank int DEFAULT 0 NOT NULL,
  exp int DEFAULT 0 NOT NULL,
  create_date timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  modified_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  foreign key(area_id) references areas(id) on delete set null,
  foreign key(category_id) references categories(id) on delete set null
);

CREATE TABLE facebook (
  id bigint NOT NULL primary key,
  active int NOT NULL DEFAULT 1,
  user_id bigint unique DEFAULT NULL,
  accesstoken varchar(255) NOT NULL,
  expiration_date datetime NOT NULL,
  foreign key(user_id) references users(id) on delete cascade
);

CREATE TABLE twitter (
  id bigint primary key,
  active int NOT NULL DEFAULT 1,
  user_id bigint unique DEFAULT NULL,
  accesstoken varchar(255) NOT NULL,
  expiration_date datetime NOT NULL,
  foreign key(user_id) references users(id) on delete cascade
);

CREATE TABLE companies (
  id bigint auto_increment primary key,
  active int NOT NULL DEFAULT 1,
  name varchar(255) NOT NULL,
  address varchar(255) DEFAULT null,
  url varchar(255) DEFAULT null,
  create_date timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  modified_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE advertisements (
  id bigint auto_increment primary key,
  active int NOT NULL DEFAULT 1,
  company_id bigint unique NOT NULL,
  name varchar(255) NOT NULL,
  description varchar(255) DEFAULT NULL,
  url varchar(255) NOT NULL,
  point int NOT NULL,
  create_date timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  modified_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  expiration_date datetime NOT NULL,
  foreign key(company_id) references companies(id) on delete cascade
);

CREATE TABLE users_advertisements (
  u_id bigint unique NOT NULL,
  ad_id bigint unique NOT NULL,
  foreign key(u_id) references users(id) on delete cascade,
  foreign key(ad_id) references advertisements(id) on delete cascade
);

CREATE TABLE likes (
  id bigint auto_increment primary key,
  active int NOT NULL DEFAULT 1,
  user_id bigint unique NOT NULL,
  ad_id bigint unique NOT NULL,
  url varchar(255) NOT NULL,
  create_date timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  foreign key(user_id) references users(id) on delete cascade,
  foreign key(ad_id) references advertisements(id) on delete cascade
);

CREATE TABLE sessions (
  id varchar(255) primary key,
  u_id bigint unique DEFAULT NULL,
  fb_id bigint unique DEFAULT NULL,
  tw_id bigint unique DEFAULT NULL,
  hostname varchar(255) NOT NULL,
  permission int NOT NULL DEFAULT 1,
  create_date timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  expiration_date datetime NOT NULL,
  foreign key(u_id) references users(id) on delete cascade,
  foreign key(fb_id) references facebook(id) on delete cascade,
  foreign key(tw_id) references twitter(id) on delete cascade
);

# --- !Downs

DROP TABLE if exists users_advertisements;
DROP TABLE if exists likes;
DROP TABLE if exists twitter;
DROP TABLE if exists facebook;
DROP TABLE if exists sessions;
DROP TABLE if exists advertisements;
DROP TABLE if exists companies;
DROP TABLE if exists users;
DROP TABLE if exists areas;
DROP TABLE if exists categories;

