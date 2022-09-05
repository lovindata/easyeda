-- Database to use for the application

USE restapi;


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
  `applied_schema` json DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- restapi.failure_result definition

CREATE TABLE `failure_result` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `exception` tinytext NOT NULL,
  `message` tinytext NOT NULL,
  `stack_trace` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- restapi.json_params definition

CREATE TABLE `json_params` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `infer_schema` tinyint(1) NOT NULL,
  `dateFormat` tinytext,
  `timestampFormat` tinytext,
  `applied_schema` json DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- restapi.`session` definition

CREATE TABLE `session` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `bearer_auth_token_sha1` char(40) NOT NULL,
  `created_at` timestamp(3) NOT NULL,
  `updated_at` timestamp(3) NOT NULL,
  `terminated_at` timestamp(3) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `bearer_auth_token_sha1` (`bearer_auth_token_sha1`),
  CONSTRAINT `created_updated_terminated_at_inferiors` CHECK (((`created_at` <= `updated_at`) and ((`terminated_at` is null) or (`updated_at` <= `terminated_at`))))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- restapi.success_result definition

CREATE TABLE `success_result` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `schema` json NOT NULL,
  `data` json NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- restapi.job_params definition

CREATE TABLE `job_params` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `type` enum('csv','json') NOT NULL,
  `csv_params_id` bigint unsigned DEFAULT NULL,
  `json_params_id` bigint unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `csv_params_id` (`csv_params_id`),
  KEY `json_params_id` (`json_params_id`),
  CONSTRAINT `csv_params_id` FOREIGN KEY (`csv_params_id`) REFERENCES `csv_params` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `json_params_id` FOREIGN KEY (`json_params_id`) REFERENCES `json_params` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `just_one_csv_or_json` CHECK ((((_utf8mb4'type' = _utf8mb4'csv') and (_utf8mb4'csv_params_id' is not null)) or ((_utf8mb4'type' = _utf8mb4'json') and (_utf8mb4'json_params_id' is not null))))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- restapi.job_result definition

CREATE TABLE `job_result` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `status` enum('success','failure') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `success_result_id` bigint unsigned DEFAULT NULL,
  `failure_result_id` bigint unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `success_result_id` (`success_result_id`),
  KEY `failed_result_id` (`failure_result_id`),
  CONSTRAINT `failure_result_id` FOREIGN KEY (`failure_result_id`) REFERENCES `failure_result` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `success_result_id` FOREIGN KEY (`success_result_id`) REFERENCES `success_result` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `success_or_failure_just_one` CHECK ((((_utf8mb4'status' = _utf8mb4'success') and (_utf8mb4'success_result_id' is not null)) or ((_utf8mb4'status' = _utf8mb4'failure') and (_utf8mb4'failure_result_id' is not null))))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- restapi.job definition

CREATE TABLE `job` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `session_id` bigint unsigned NOT NULL,
  `type` enum('preview','analyze') NOT NULL,
  `status` enum('running','terminated') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `job_params_id` bigint unsigned NOT NULL,
  `job_result_id` bigint unsigned NOT NULL,
  `created_at` timestamp(3) NOT NULL,
  `terminated_at` timestamp(3) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `session_id` (`session_id`),
  KEY `job_params_id` (`job_params_id`),
  KEY `job_result_id` (`job_result_id`),
  CONSTRAINT `job_params_id` FOREIGN KEY (`job_params_id`) REFERENCES `job_params` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `job_result_id` FOREIGN KEY (`job_result_id`) REFERENCES `job_result` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `session_id` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`),
  CONSTRAINT `created_terminated_at_inferiors` CHECK (((`terminated_at` is null) or (`created_at` <= `terminated_at`))),
  CONSTRAINT `status_terminated_at_coherence` CHECK ((((`terminated_at` is null) and (`status` = _utf8mb4'running')) or ((`terminated_at` is not null) and (`status` = _utf8mb4'terminated'))))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;