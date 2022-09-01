-- Database to use for the application
USE restapi;

-- restapi.`session` definition
CREATE TABLE `session` (
  `id` char(36) NOT NULL,
  `bearer_auth_token_sha1` char(40) NOT NULL,
  `created_at` timestamp(3) NOT NULL,
  `updated_at` timestamp(3) NOT NULL,
  `terminated_at` timestamp(3) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `bearer_auth_token_sha1` (`bearer_auth_token_sha1`),
  CONSTRAINT `created_updated_terminated_at_inferiors` CHECK (((`created_at` <= `updated_at`) and ((`terminated_at` is null) or (`updated_at` <= `terminated_at`))))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- restapi.job definition
CREATE TABLE `job` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` char(36) NOT NULL,
  `type` enum('preview','analyze') NOT NULL,
  `status` enum('running','succeeded','failed') DEFAULT NULL,
  `created_at` timestamp(3) NOT NULL,
  `terminated_at` timestamp(3) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `session_id` (`session_id`),
  CONSTRAINT `session_id` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`),
  CONSTRAINT `status_terminated_at_coherence` CHECK ((((`terminated_at` is null) and (`status` = _utf8mb4'running')) or ((`terminated_at` is not null) and (`status` in (_utf8mb4'succeeded',_utf8mb4'failed'))))),
  CONSTRAINT `created_terminated_at_inferiors` CHECK (((`terminated_at` is null) or (`created_at` <= `terminated_at`)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;