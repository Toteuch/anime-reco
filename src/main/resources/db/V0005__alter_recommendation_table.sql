ALTER TABLE `anime_reco`.`recommendation`
DROP FOREIGN KEY `FK1cc4qdpxwpja5n9vjmwhdxr1h`;
ALTER TABLE `anime_reco`.`recommendation`
DROP COLUMN `user_id`,
DROP INDEX `FK1cc4qdpxwpja5n9vjmwhdxr1h` ;
;