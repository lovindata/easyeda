-- Create schemas
CREATE SCHEMA IF NOT EXISTS elodata_sch;

-- Create user table
CREATE TABLE IF NOT EXISTS elodata_sch."user" (
	id bigserial NOT NULL,
	email text NOT NULL,
	username text NOT NULL,
	pwd text NOT NULL,
	pwd_salt text NOT NULL,
	day_birth smallint NOT NULL,
	month_birth smallint NOT NULL,
	year_birth smallint NOT NULL,
	profil_img bytea NULL,
	created_at timestamptz(3) NOT NULL,
	validated_at timestamptz(3) NULL,
	updated_at timestamptz(3) NOT NULL,
	active_at timestamptz(3) NOT NULL,
	PRIMARY KEY (id),
	UNIQUE (email)
);