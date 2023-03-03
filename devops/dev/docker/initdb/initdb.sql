-- Create schemas
CREATE SCHEMA IF NOT EXISTS elodata_sch;

-- Create user table
CREATE TABLE IF NOT EXISTS elodata.elodata_sch.user (
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
CREATE TABLE IF NOT EXISTS elodata.elodata_sch.token (
	id bigserial NOT NULL,
	user_id bigint NOT NULL,
	access_token text NOT NULL,
	expire_at timestamptz(3) NOT NULL,
	refresh_token text NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES elodata.elodata_sch.user,
	UNIQUE (user_id),
	UNIQUE (access_token),
	UNIQUE (refresh_token)
);

-- Create connection table
CREATE TABLE IF NOT EXISTS elodata.elodata_sch.conn (
	id bigserial NOT NULL,
	user_id bigint NOT NULL,
	kind text NOT NULL,
	conn_id bigint NOT NULL,
	"name" text NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (user_id) REFERENCES elodata.elodata_sch.user,
	UNIQUE (kind, conn_id)
);

-- Create postgres connection table
CREATE TABLE IF NOT EXISTS elodata.elodata_sch.conn_postgres (
	id bigserial NOT NULL,
	kind text NOT NULL,
	host text NOT NULL,
	port text NOT NULL,
	dbName text NOT NULL,
	"user" text NOT NULL,
	pwd text NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (kind, id) REFERENCES elodata.elodata_sch.conn (kind, conn_id),
	CHECK (kind = 'postgres')
);

-- Create mongodb connection table
CREATE TABLE IF NOT EXISTS elodata.elodata_sch.conn_mongodb (
	id bigserial NOT NULL,
	kind text NOT NULL,
	hostPort text[][2] NOT NULL,
	dbAuth text NOT NULL,
	"user" text NOT NULL,
	pwd text NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (kind, id) REFERENCES elodata.elodata_sch.conn (kind, conn_id),
	CHECK (kind = 'mongodb')
);