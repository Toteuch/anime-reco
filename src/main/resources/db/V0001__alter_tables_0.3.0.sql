ALTER TABLE `anime_reco`.`mal_user`
CHANGE COLUMN `last_seen` `last_seen` DATETIME(3) NOT NULL ,
CHANGE COLUMN `last_update` `last_update` DATETIME(3) NULL DEFAULT NULL ;

ALTER TABLE `anime_reco`.`favorite`
CHANGE COLUMN `author` `author` ENUM('SYSTEM', 'USER') NOT NULL ,
CHANGE COLUMN `created_at` `created_at` DATETIME(3) NOT NULL ;

ALTER TABLE `anime_reco`.`profile`
CHANGE COLUMN `created_at` `created_at` DATETIME(3) NOT NULL ;