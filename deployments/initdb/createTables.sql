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

-- restapi.json_params definition
CREATE TABLE `json_params` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `infer_schema` tinyint(1) NOT NULL,
  `dateFormat` tinytext,
  `timestampFormat` tinytext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- restapi.csv_params definition
CREATE TABLE `csv_params` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `sep` char(1) NOT NULL,
  `quote` char(1) NOT NULL,
  `escape` char(1) NOT NULL,
  `header` tinyint(1) NOT NULL,
  `infer_schema` tinyint(1) NOT NULL,
  `dateFormat` tinytext,
  `timestampFormat` tinytext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- restapi.job_params definition
CREATE TABLE `job_params` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `csv_params_id` bigint unsigned DEFAULT NULL,
  `json_params_id` bigint unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `csv_params_id` (`csv_params_id`),
  KEY `json_params_id` (`json_params_id`),
  CONSTRAINT `csv_params_id` FOREIGN KEY (`csv_params_id`) REFERENCES `csv_params` (`id`),
  CONSTRAINT `json_params_id` FOREIGN KEY (`json_params_id`) REFERENCES `json_params` (`id`),
  CONSTRAINT `just_one_not_null` CHECK ((((`csv_params_id` is null) + (`json_params_id` is null)) = 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- restapi.job definition
CREATE TABLE `job` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `session_id` char(36) NOT NULL,
  `type` enum('preview','analyze') NOT NULL,
  `status` enum('running','succeeded','failed') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `job_params_id` bigint unsigned NOT NULL,
  `created_at` timestamp(3) NOT NULL,
  `terminated_at` timestamp(3) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `session_id` (`session_id`),
  KEY `job_params_id` (`job_params_id`),
  CONSTRAINT `session_id` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`),
  CONSTRAINT `job_params_id` FOREIGN KEY (`job_params_id`) REFERENCES `job_params` (`id`),
  CONSTRAINT `created_terminated_at_inferiors` CHECK (((`terminated_at` is null) or (`created_at` <= `terminated_at`))),
  CONSTRAINT `status_terminated_at_coherence` CHECK ((((`terminated_at` is null) and (`status` = _utf8mb4'running')) or ((`terminated_at` is not null) and (`status` in (_utf8mb4'succeeded',_utf8mb4'failed')))))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;