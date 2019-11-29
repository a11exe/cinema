# Cinema
Movie tickets online booking system

The task is:

Build a online booking system for cinema.

 * 2 types of users: admin and regular users
 * Admin can see all bookings and  cancel them.
 * Admin can creates the hall configuration in a hall.xml file.
 * Users can book seat in the hall.
 * After selecting a seat, the user must confirm the booking within 5 minutes.
 * After confirming the reservation, the user receives a booking code.
    
## Used technologies

+ Java 8
+ Maven
+ Servlet 4
+ Jetty
+ Liquibase
+ DBs: H2
+ log4j
+ Ajax JSON
+ XML DOM

## Install

    git clone https://github.com/a11exe/cinema
    
## Run

    mvn clean install jetty:run

## Credentials
DB connection
+ JDBC URL: jdbc:h2:file:~/cinema
+ User: sa, password: zD5z6Wx

+ Admin, pass: "admin" (set in properties)

## Properties
app.properties
reload.hall (true - clear database and reload hall from hall.xml, false - nothing to do)
booking.timeout.seconds (timeout of booking until confirmation)
admin.pass (password for admin panel)