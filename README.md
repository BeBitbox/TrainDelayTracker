# TrainDelayTracker
This application will track and store train departure events. The goal of the project is to 
have the possibilities to query that data and obtain useful statistics from it and display 
it using a webpage.

## Core module
This is the most important module that contains all the specific models of the application and
most of the businessrules applied on them. It has no external dependencies, except some technical ones.

**Station** : a station represents a departure and arrival point for trains. It has a physical
 location and GPS coordinates.  

**Board** : a board is the schedule of the departure of the trains for a certain station.

**Train departure** : A board consists of scheduled train departures in the nearby future. 
When a train is removed from the board, it is departed and this results
into a TrainDepartureEvent. 

**BoardHarvester** : This will follow changes in boards and make train departure events when 
needed.

## boardrequesters
This module contains some boardrequester implementations : 
* NMBS data scraper : fastest
* iRail REST API consumer : slower, but cleaner.

For now the NMBS one has highest priority and iRail is being used as a fallback mechanism.

## persistance
This module contains the persistance layer of the application. 
For now it is specific Amazon DynamoDB specific. If other database are being chosen, 
just recreate a new module that implements the DAO-interfaces.

## ui
This module contains the webpages of the frontend. For now this is written in Vaadin

## application
This module bundles all the modules together in one executable JAR
