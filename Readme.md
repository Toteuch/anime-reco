# Anime-Reco

#### Next Version

* Create a Profile from a Subject, email and Avatar URL: the Subject and the Email mustn't exist already
* Link a Profile to a MalUser: the MalUser mustn't be linked to a Profile already

#### Version 0.1.0

New features:

- Scrap last seen usernames from MyAnimeList.net (min freq is configurable, 10s default)
- Retrieve user's scores list from MyAnimeList.net's API for new users
- Retrieve user's scores list from MyAnimeList.net's API for user seen on MAL after their last update
- Retrieve user's scores list from MyAnimeList.net's API for user that has been updated long time ago (configurable, default is 30 days)
- Retrieve anime details from MyAnimeList.net's API for new anime
- Retrieve anime details from MyAnimeList.net's API for anime that has been updated long time ago (configurable, default is 14 days)

Those tasks are executed sequentially and looped over every 100ms through a ScheduledJob that runs indefinitely if activated (configurable, default true).
