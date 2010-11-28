CREATE TABLE user(
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`last_name` varchar(256) DEFAULT NULL,
`first_name` varchar(256) DEFAULT NULL,
`duty` varchar(256) DEFAULT NULL,
`cellphone` varchar(256) DEFAULT NULL,
`housephone` varchar(256) DEFAULT NULL,
`telephone` varchar(256) DEFAULT NULL,
`office_fax` varchar(256) DEFAULT NULL,
`home_address` varchar(256) DEFAULT NULL,
`office_address` varchar(256) DEFAULT NULL,
`remark` text, 
PRIMARY KEY (`id`),
KEY `NAME_INDEX` (`first_name`,`last_name`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

