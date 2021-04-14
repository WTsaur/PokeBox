# PokeBox
![PokeBox App Icon](https://github.com/WTsaur/PokeBox/blob/master/github-Assets/PokeBox-icon-app.png)
## Created by William Tsaur and Tommy Chong
## Progress Report 1
### Contributions
- William Tsaur
    - Basic Layout using ViewPager2, TabLayout, and fragments
    - Fixed and modified basic camera functionality for camera fragment
    - Created Image Assets and Vector assets for the app logo, tabLayout, and camera fragment background
    - Created markdown document
    - Implemented image recognition and text extraction using Google's ML Kit text recognizer
- Tommy Chong
    - Implemented basic camera functionality for camera fragment
    - Began implementation of user login and registration with FireBase
### Current State of the Application
> We have established a basic layout for the application consisting of four fragments that can be swiped through using a ViewPager2.
> We have implemented the camera page to the point where pictures can be taken and the image can be received. Furthermore, the image
> can then be sent to the on-device image processor provided by Google's ML kit library. The processor extracts the text from the
> image, which we, for now, display onto the camera fragment after the image processing is complete. We have also begun working on
> the login and registration of user accounts which has been commented out in the code because it has yet to reach a point of
> completion where it can be displayed. Below you will find images of the current states of each page as well as a short GIF
> that shows a demo of the application running.
#### Camera Page
![camera](https://github.com/WTsaur/PokeBox/blob/master/github-Assets/pokebox-camera.jpg)
![scanner example](https://github.com/WTsaur/PokeBox/blob/master/github-Assets/pokebox-scanner-example.jpg)
#### Cards Page
![cards](https://github.com/WTsaur/PokeBox/blob/master/github-Assets/pokebox-cards.jpg)
#### Watchlist Page
![watchlist](https://github.com/WTsaur/PokeBox/blob/master/github-Assets/pokebox-watchlist.jpg)
#### Settings Page
![settings](https://github.com/WTsaur/PokeBox/blob/master/github-Assets/pokebox-settings.jpg)
#### Demo
![Progress 1 Demo](https://github.com/WTsaur/PokeBox/blob/master/github-Assets/app_demo_progress1.gif)

## Progress Report 2
### Contributions
- William Tsaur
  - Implemented API request after card scan to retrieve and display a list of cards that match the card that was scanned
  - Implemented Firebase database allowing users to save scanned cards to the database and to retrieve from any device as long as they log onto their account
  - Added background, buttons, and a recycler view to the Card Viewer Fragment so that a user can view their card collection
  - Created PokeCard.java, a custom java object for ease of writing pokemon card data to the Firebase database
  - Created item_card.xml to contain Pokemon card image data when used in recycler view
  - Created CardsAdapter, an adapter required when using recycler view
- Tommy Chong
  - Implemented firebase authentication.
  - Created register.java, register.xml, login.xml.
  - Implemented the login and registration screen for firebase authentication.
  - Implemented logout button for logging user out in settings fragment.
  - Implemented change user password in settings fragment. Settings fragment also displays user email address.
  - Created CardView.java, this activity is started whenever a card is clicked in CardsViewer Fragment. It retrieves the card information and displays all the information.
### Current State of the Application
> We have established a user login and registration system, now users can create accounts and login to their accounts. Users will be logged in when they exit the application so 
> that they don't need to login again. Card scan is now fully functional and now displays a list of cards that match the card that was scanned. User can select the card that
> matches the card they have. Once the card is selected, the card is saved to the database and user can now view their saved cards in the cardsviewer fragment. Selecting a card 
> now starts a new activity, which it displays all the information from the pokemon card (Card number, HP, Stage, Attacks, Weaknessess, Resistances, card type). The activity
> also displays the pokemon card image. Logout button is also functional now.

## Progress Report 3
### Contributions
- William Tsaur
    - Created and designed layout files for Watchlist fragment and watchlist item
    - Implemented adding cards to watchlist and removing cards from watchlist
    - Implemented swipe left to delete on watchlist item and swipe down to refresh prices on watchlist
    - Implemented options menu button to delete card from collection (which subsequently deletes card from watchlist)
    - Created error ghost to let users know if the watchlist is empty or if their card collection is empty
    - Implemented Filter by Pokemon Type
    - Improved Search by Pokemon Name
    - Performance improvements so that the app pulls data from the database and downloads bitmaps far less often than it needed to; loading expanded card views and adding and removing cards from watchlist and card collection is faster
    - Re-designed theme and colors across entire application
    - Re-designed card that displays Pokemon information to utilize CardView
    - Updated Firebase Database rules to accommodate Watchlists
    - Created powerpoint presentation
    - Created Progress report
- Tommy Chong
    - Implemented Search by Pokemon name
    - Wrote basic framework for implementing watchlist
    - Implemented detailed card view that displays Pokemon information
    - Implemented detailed image view that displays the entire card image in a bigger size
    - Fixed Scroll view error in Card Viewer fragment
    - Implemented Options Menu and Toolbar for detailed card view
    - Created powerpoint presentation
### Current State of the Application
> We have fully implemented all pages of the application (Card Scanning, Card Collection, Watchlist, User Registration/Login, User password change). Users can take pictures of cards and save them to their card collection. Users can search their card collection by the name of a Pokemon or they can filter their collection by Pokemon type. They can also view detailed information about each card in their collection and choose to delete the card from their collection or add them to their watchlist. In their watchlist, users can view the prices of their cards (Normal, Holofoil, Reverse Holofoil, and 1st Edition Holofoil). Users can swipe down to refresh the prices on the watchlist or swipe left on a watchlist item to remove it from the watchlist. Users can also change their password, log out of their account and log in to a different account or register for a new account.
### Remaining Issues with the Application and Improvements that can be made
> There are many different card variations of Pokemon cards and we currently do not account for every variation. Our text processing logic is not broad enough to cover all variations. To solve this, we could either improve the text processing logic or replace Google's Firebase ML Kit SDK Text Recognizer with Google's Image Search API to broaden our processing range across all variations and improve accuracy. The application also pulls from the database every time the application is opened is not ideal performance-wise. To improve this we would use SharedPreferences to temporarily store a user's card collection onto their device for easy, fast retrieval at a later time.
> A feature that can be added in the future is sorting. This would allow users to sort their cards in alphabetical order by name. We could also implement sorting by region and sorting by type.
