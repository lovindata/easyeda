-- Create schemas
CREATE SCHEMA IF NOT EXISTS elodata_sch;

-- Create user table
CREATE TABLE IF NOT EXISTS elodata_sch.user (
	id bigserial NOT NULL,
	email text NOT NULL,
	username text NOT NULL,
	pwd text NOT NULL,
	pwd_salt text NOT NULL,
	birth_date date NOT NULL,
	img bytea NULL,
	created_at timestamptz(3) NOT NULL,
	validated_at timestamptz(3) NULL,
	updated_at timestamptz(3) NOT NULL,
	active_at timestamptz(3) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE (email)
);

-- Create token table
CREATE TABLE IF NOT EXISTS elodata_sch.token (
	id bigserial NOT NULL,
	user_id bigint NOT NULL,
	access_token text NOT NULL,
	expire_at timestamptz(3) NOT NULL,
	refresh_token text NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES elodata_sch.user,
	UNIQUE (user_id),
	UNIQUE (access_token),
	UNIQUE (refresh_token)
);

-- Create connection table
CREATE TABLE IF NOT EXISTS elodata_sch.conn (
	id bigserial NOT NULL,
	user_id bigint NOT NULL,
	kind text NOT NULL,
	"name" text NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES elodata_sch.user,
	CHECK (kind = 'postgres' OR kind = 'mongodb')
);

-- Create postgres connection table
CREATE TABLE IF NOT EXISTS elodata_sch.conn_postgres (
	id bigserial NOT NULL,
	conn_id bigint NOT NULL,
	host text NOT NULL,
	port text NOT NULL,
	db_name text NOT NULL,
	"user" text NOT NULL,
	pwd text NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (conn_id) REFERENCES elodata_sch.conn
);

-- Create mongodb connection table
CREATE TABLE IF NOT EXISTS elodata_sch.conn_mongodb (
	id bigserial NOT NULL,
	conn_id bigint NOT NULL,
	host_port text [] [2] NOT NULL,
	db_auth text NOT NULL,
	"user" text NOT NULL,
	pwd text NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (conn_id) REFERENCES elodata_sch.conn
);