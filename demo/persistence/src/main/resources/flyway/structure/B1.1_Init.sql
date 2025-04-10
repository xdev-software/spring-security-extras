CREATE TABLE product (
   id BIGINT AUTO_INCREMENT NOT NULL,
   name VARCHAR(255) NOT NULL,
   PRIMARY KEY (id)
);

ALTER TABLE product ADD CONSTRAINT uc_product_name UNIQUE (name);

CREATE TABLE `userdetail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email_address` VARCHAR(255) NOT NULL,
  `fullname` VARCHAR(255) NOT NULL DEFAULT '',
  `created_at` datetime NOT NULL,
  `disabled_at` datetime DEFAULT NULL,
  `last_login_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_address` (`email_address`),
  KEY `idx_userdetail_created_at` (`created_at`),
  KEY `idx_userdetail_disabled_at` (`disabled_at`),
  KEY `idx_userdetail_last_login_at` (`last_login_at`)
);

CREATE TABLE `auth_remember_me_secret` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `identifier` VARCHAR(255) NOT NULL,
  `secret` blob NOT NULL,
  `created_at` datetime NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `crypto_algorithm` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uc_auth_remember_me_secret_identifier` (`identifier`),
  KEY `FK_AUTH_REMEMBER_ME_SECRET_ON_USER` (`user_id`),
  CONSTRAINT `FK_AUTH_REMEMBER_ME_SECRET_ON_USER` FOREIGN KEY (`user_id`) REFERENCES `userdetail` (`id`)
);
