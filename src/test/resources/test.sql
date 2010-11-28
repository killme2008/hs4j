drop table test_user;
CREATE TABLE `test_user` (  
   `user_id` int(10) unsigned NOT NULL AUTO_INCREMENT,  
   `user_name` varchar(50) NOT NULL,  
   `user_email` varchar(255) NOT NULL,  
   `age`   int(10) unsigned DEFAULT NULL,
   `created` datetime NOT NULL,  
   PRIMARY KEY (`user_id`),  
   KEY `NAME_MAIL_INDEX` (`user_name`,`user_email`),
   KEY `AGE_INDEX` (`age`)
) ENGINE=InnoDB;
