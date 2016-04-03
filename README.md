# College-MobileAppDev-CA1
College-MobileAppDev-CA1

Mobile Application Design and Development – CA1

OrderMyDrinks Android Application

Jamie Darcy - X00130016 - 3-29-2016

Application proposal architecture overview - 
OrderMyDrinks application will allow users to reserve a table in a bar and have their drinks at the table upon arrival. The application will also try to prove the effects of alcohol by monitoring the during to and from the bar. 
Architecture The proposed Android application will perform a nearby search request against the Google Places API Web Service which returns information about the nearest places based on the user’s current location using GPS. The nearby search HTTP request will add parameter for Place Type=Bar to return all the bars nearby. Once a location is selected the user can select a transit_mode (Bus, Bicycle, walking) which will be used to query Google Maps Directions API service to calculate the travel time between current location and the bar selected by the user using an HTTP request. Using the phones sensors to try to predict the weather the application will suggest a travel mode to the user. The proposed application will implement a cloud-based backend service using Azure Mobile Services and Microsoft Azure SQL Database instance and server to store data for the application. 

Testing the effects of alcohol 
The application will use the phones motion sensors to monitor the accelerometer calculating the raw data returned to give a figure of the journey to the bar and also calculate the journey time. Using the GPS coordinates of the bar it will stop calculation once it arrives. When the GPS coordinates change the application will start to track accelerometer again and compare total time and total accelerometer readings to and from the bar 

Design 
The application design will follow the guidelines defined in the Android material design specification. The application uses floating action buttons for navigation and a menu for common application tasks (setting, Exit, search). All screens will use a Navigation menu that will appear onclick or slide right. 

Application Screens step by step

Welcome Screen
Screen uses a collapsing toolbar with the image section collapsing when space is needed for keyboard. Screen takes user input Name, email and Number of guests. Screen contains a floating action button which verifies entries before submitting to next screen

List Of Bars
The List Of Bars activity will contain a list. The list is created by performing a nearby search request using current GPS location against the Google Places API Web Service to return a list of selectable bars nearby. The list allows the user to select a bar of interest. When selected the user will be taken to the bar details screen.

Bar Details Activity
The Bar Details screen contains details about bar such as address and name. It will also contain hidden location details longitude and latitude of the bar. Button Click ‘Call Bar’ will take the user the to the Call Bar screen Button Click ‘Order Drinks’ will take the user to the Order Drinks Screen.

Call bar Activity
The Call Bar activity allows the user to make a phone call to the bar. 

Reserve Table Activity
Travel method activity contains a map with the current location and bar location pinned. Map can be zoomed in and out. User option to select a method of transport, once selected the directions will be drawn on the map and the distance to bar and estimated arrival time will be calculated depending on the method of transport. The transport method selected will be used to query Google Maps Directions API service to calculate the travel time between current location and the bar selected by the user using an HTTP request. This travel time will be used to calculate the time to reserve the table.

Choose Drinks Activity
Choose Drinks activity will contain a list of drinks that the user can pre-order. The application will use the phones motion sensors to monitor the accelerometer calculating the raw data returned to give a figure of the journey to the bar and also calculate the journey time. Using the GPS coordinates of the bar it will stop calculation once it arrives. When the GPS coordinates change the application will start to track accelerometer again and compare total time and total accelerometer readings to and from the bar 

Summary Activity
Summary activity will contain a summary of the details of the user’s reservation. Details selected from the ‘Reserve a table form’, calculated time of arrival to the bar and the list of drinks ordered from the ‘Order Drinks’ form. All data will be stored using Azure Mobile Services to store the data in a Microsoft Azure SQL Database instance.

Test effects of alcohol
Activity Test level of alcohol activity is a demo purpose screen. The activity on start click uses the phones motion sensors to monitor the accelerometer calculating the raw data returned to give a figure of the journey to the bar and also calculate the journey time. OnStop click these totals are used as the journey to the bar figure. On start click again the application does the same as above and on stop click the data is used for the journey home from bar figure. The activity uses animation where a ball bounces around the screen and the current accelerometer value displayed in the center
