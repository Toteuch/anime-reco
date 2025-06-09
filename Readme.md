# Anime-Reco

#### Next

* Allow anonymous session
* Filters management
* Save a filter
* Delete a filter
* Sort filters
* Basic search anime page
    * Access the filter management

#### 0.4.0

* Profile page

    * Profile details
    * Job requests
    * Last jobs summary
* Basic Notification page

    * Notification list

        * Sorted from newest to oldest
    * Mark as read
* Fixes

    * Usage of the request interval (default set 2000ms to avoid 307 (api limit))
    * Issue on old anime refresh (orphan deletion)

#### 0.3.0

* Create a JobTask to plan an asynchronous task
* Abandon a JobTask that is not finished (in STARTED or QUEUED state)
* Process user's similarity for a Profile (and a required linked user)
* Process anime recommendation for a Profile (requires similarities for the profile)
* Modify some columns definition on existing tables (execute V0001__alter_tables_0.3.0.sql if previous version was
  installed)
* Automatically trigger once a day (4AM) a refresh job that
    * Clear old data for eligible profiles (delete the configured amount of similarities, for Profiles that already
      completed ProcessUserSimilarityJob at least once)
    * Call ProcessUserSimilarityJob for the profiles
    * Call ProcessAnimeRecommendationJob for the profiles
* Add a home page (required authent through Google OAuth2)
    * Display temporary page with basic actions to setup a Profile

#### 0.2.0

* Create a Profile from a Subject, email and Avatar URL: the Subject and the Email mustn't exist already
* Link a Profile to a MalUser: the MalUser mustn't be linked to a Profile already
* Create and Delete a favorite anime for a Profile
* Automatically create a favorite anime:
    * When retrieving a user's scores list from MyAnimeList.net, if a score is at least 7 and the score isn't already
      exisiting in DB, creates a favorite anime for the linked profile
        * Also, if the new favorite Anime got related anime (prequel/sequel), favorite them and their related animes
    * When refreshing anime details, if the anime got a new sequel, all profiles that have this anime in favorite got
      the sequel as favorite too
* Delete all favorite animes for a Profile
* Notify profile when an anime has been favorited
* Notify when a favorited anime got its status changed

#### 0.1.0

New features:

- Scrap last seen usernames from MyAnimeList.net (min freq is configurable, 10s default)
- Retrieve user's scores list from MyAnimeList.net's API for new users
- Retrieve user's scores list from MyAnimeList.net's API for user seen on MAL after their last update
- Retrieve user's scores list from MyAnimeList.net's API for user that has been updated long time ago (configurable,
  default is 30 days)
- Retrieve anime details from MyAnimeList.net's API for new anime
- Retrieve anime details from MyAnimeList.net's API for anime that has been updated long time ago (configurable, default
  is 14 days)

Those tasks are executed sequentially and looped over every 100ms through a ScheduledJob that runs indefinitely if
activated (configurable, default true).
