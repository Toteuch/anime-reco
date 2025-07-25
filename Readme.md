# Anime-Reco

#### 1.0.6

* Remove NEW_DATE notification type (too frequent changes on the source)

#### 1.0.0

* Set the pageSize in ajax call (and put page param in path params)
* Put notitifications on top of Home page
* Handle recommendations exclusion
* Display cached statistics information in navbar
* Refresh linked user lists each scheduled job
* Handle user list visibility's log and info for profile
* Change the displayed notification sorting
* Harmonize buttons' behavior
* Add useful information on how to use the app
* Refresh job status when running
* Add footer to the app

#### 0.10.0

* Change the pagination display (fixed size)
* Rationalize anime card generation
* Prevent notification readAt value to be overwritten
* Add tags on anime cards
* Restrict the watchlist on home page to completed animes
* Add data in the modal animeDetails
* Sort watched anime by user's update date
* Remove the interval for anime refresh
  * the scheduled job now refreshs the 20 last updated anime

#### 0.9.0

* Get more data from MAL
  * enable nsfw tag
  * anime details: created_at, updated_at, synopsis, background, nsfw
  * user score: updated_at

#### 0.8.0

* Recommendations page - default (logged in and anonymous) recommendations
* Delete user_id column in recommendation table
* Recommendations page - Handle profile's filters
* Recommendations page - Configuration
  * Sort filters
  * Ignore (or not) watchlisted animes

#### 0.7.1

* Raise new date notification only on non-completed anime

#### 0.7.0

* Small security configuration changes
* Open search filter
* Watchlist page - Watched
* Add/Remove from watchlist
* Add pagination on Home's notifications
* Watchlist page - Watchlist
* Use bootstrap icons css
* Home page - Watchlist
* Home page - Current season

#### 0.6.0

* Search filter save
* Home page : Notifications
* Fix
  * Scheduled task wasn't processing recommendations

#### 0.5.0

* Allow anonymous session
* Search anime page
  * Filter search
* Change Favorite to NotificationSetting
* Remove the auto NotificationSetting for anime score above the threshold
* Notification types :
  * New related anime
  * Status changed
  * Start date changed
* Anime details window

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
