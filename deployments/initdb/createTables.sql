-- Database to use for the application
USE restapi;

-- Store the current running sessions
CREATE TABLE session (
    id CHAR(36) PRIMARY KEY NOT NULL,
    auth_token_sha1 CHAR(40) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);