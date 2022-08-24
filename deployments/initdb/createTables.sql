-- Database to use for the application
USE restapi;

-- Store the current running sessions
CREATE TABLE session (
    id CHAR(36) PRIMARY KEY NOT NULL, -- Primary key
    bearer_auth_token_sha1 CHAR(40) UNIQUE NOT NULL, -- Candidate key
    created_at TIMESTAMP(3) NOT NULL,
    updated_at TIMESTAMP(3) NOT NULL, -- Initialized at the same value as `created_at`
    terminated_at TIMESTAMP(3)
);