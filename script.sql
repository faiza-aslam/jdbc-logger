CREATE DATABASE test;

use test;

CREATE TABLE `user_event_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_timestamp` datetime NOT NULL,
  `log_level` varchar(45) NOT NULL,
  `event_type` varchar(45) NOT NULL,
  `principal_name` varchar(45) NOT NULL,
  `event_detail` varchar(1000) NOT NULL,
  `is_active` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
