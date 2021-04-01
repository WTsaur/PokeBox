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
  - Created CardView.java, this activity is started whenever a card is clicked in CardsViewer Fragment. It displays the card image and all other information of the Pokemon card.
### Current State of the Application
> 
