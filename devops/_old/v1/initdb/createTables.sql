-- Database to use for the application

USE restapi;


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


-- restapi.job definition

CREATE TABLE `job` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `session_id` bigint unsigned NOT NULL,
  `job_type` enum('preview','analyze') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` enum('starting','running','terminated') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp(3) NOT NULL,
  `terminated_at` timestamp(3) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `session_id` (`session_id`),
  CONSTRAINT `session_id_FK` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`),
  CONSTRAINT `created_terminated_at_inferiors` CHECK (((`terminated_at` is null) or (`created_at` <= `terminated_at`))),
  CONSTRAINT `status_terminated_at_coherence` CHECK (((_utf8mb4'status' <> _utf8mb4'terminated') xor (_utf8mb4'terminated_at' is null)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- restapi.json_params definition

CREATE TABLE `json_params` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `job_id` bigint unsigned NOT NULL,
  `infer_schema` tinyint(1) NOT NULL,
  `dateFormat` tinytext,
  `timestampFormat` tinytext,
  `custom_schema` json DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `job_id_json_params_FK` (`job_id`),
  CONSTRAINT `job_id_json_params_FK` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- restapi.success_result definition

CREATE TABLE `success_result` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `job_id` bigint unsigned NOT NULL,
  `schema` json NOT NULL,
  `data` json NOT NULL,
  PRIMARY KEY (`id`),
  KEY `job_id_success_result_FK` (`job_id`),
  CONSTRAINT `job_id_success_result_FK` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- restapi.csv_params definition

CREATE TABLE `csv_params` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `job_id` bigint unsigned NOT NULL,
  `sep` char(1) NOT NULL,
  `quote` char(1) NOT NULL,
  `escape` char(1) NOT NULL,
  `header` tinyint(1) NOT NULL,
  `infer_schema` tinyint(1) NOT NULL,
  `dateFormat` tinytext,
  `timestampFormat` tinytext,
  `custom_schema` json DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `job_id_csv_params_FK` (`job_id`),
  CONSTRAINT `job_id_csv_params_FK` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- restapi.failure_result definition

CREATE TABLE `failure_result` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `job_id` bigint unsigned NOT NULL,
  `exception` tinytext NOT NULL,
  `message` tinytext NOT NULL,
  `stack_trace` longtext NOT NULL,
  PRIMARY KEY (`id`),
  KEY `job_id_failure_result_FK` (`job_id`),
  CONSTRAINT `job_id_failure_result_FK` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;