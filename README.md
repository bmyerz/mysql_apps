Example MySQL applications
--------------------------

This repository contains example applications for MySQL, written in Python and Java.


## Java

Requirements:

* Java JDK, probably version 1.7 or higher
* MySQL JDBC jar file

Recommended other requirements:

* IntelliJ IDE or Eclipse

### Try it in the IDE

1. Create a new Java project
2. copy the src/ directory into your project
3. Add the MySQL jar file (mysql-connector-java-VERSION-bin.jar ) to your project

In Eclipse, Right click on the project, choose Build Path -> Configure Build Path..., then Add Jars...
In IntelliJ, File > Project structure > Modules > Dependencies tab > + button > Jars or directories...

4. Set the environment variable DBPASSWORD to your MySQL password
5. Edit file AirlineCustomerClient.java to fill in your hawkid where indicated
6. Run

## Python

Requirements:

* Python 2.7 (tested on this version, but you might try others)
* Python mysql connector

### Try it

1. Set the environment variable DBPASSWORD to your MySQL password
5. Edit file AirlineCustomerClient.py to fill in your hawkid where indicated
2. Run the application

e.g.

`python AirlineCustomerClient.py`
