-- public."session" definition

-- Drop table

-- DROP TABLE public."session";

CREATE TABLE public."session" (
	id bigserial NOT NULL,
	bearer_auth_token_sha1 bpchar(40) NOT NULL,
	created_at timestamp(3) NOT NULL,
	updated_at timestamp(3) NOT NULL,
	terminated_at timestamp(3) NULL DEFAULT NULL::timestamp without time zone,
	CONSTRAINT session_pk PRIMARY KEY (id),
	CONSTRAINT session_un UNIQUE (bearer_auth_token_sha1),
	CONSTRAINT timestamps_coherence CHECK (((created_at <= updated_at) AND ((terminated_at IS NULL) OR (updated_at <= terminated_at))))
);


-- public.job definition

-- Drop table

-- DROP TABLE public.job;

CREATE TABLE public.job (
	id bigserial NOT NULL,
	session_id bigserial NOT NULL,
	is_preview bool NOT NULL,
	is_running bool NOT NULL,
	created_at timestamp(3) NOT NULL,
	terminated_at timestamp(3) NULL DEFAULT NULL::timestamp without time zone,
	CONSTRAINT is_not_running_chk CHECK (((terminated_at IS NOT NULL) AND (NOT is_running))),
	CONSTRAINT is_running_chk CHECK (((terminated_at IS NULL) AND is_running)),
	CONSTRAINT job_pk PRIMARY KEY (id),
	CONSTRAINT timestamps_chk CHECK (((terminated_at IS NULL) OR (created_at <= terminated_at))),
	CONSTRAINT session_id_fk FOREIGN KEY (session_id) REFERENCES public."session"(id)
);