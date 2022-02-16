CREATE TABLE IF NOT EXISTS `activities` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `start_time` DATETIME DEFAULT NULL,
  `activity_desc` VARCHAR(127)  COLLATE utf8mb3_hungarian_ci DEFAULT NULL,
  `activity_type` VARCHAR(15)  COLLATE utf8mb3_hungarian_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3  COLLATE utf8mb3_hungarian_ci;