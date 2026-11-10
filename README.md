# Purdue-Dining-Options

## Problem
Whenever I want to find a place to eat on campus, I have to open an app and check 5-6 different places to see what is open/what foods they have. It is annoying to not have all the options in front of me at once.

## Solution
I am going to make a script that will output all of the dining options available at the moment you run it. It will list the food options and where they are located. This should only output food from dining courts and 1Bowl.

## MVP Plan
The MVP will include receiving data from the API, parsing it, and printing it to the terminal.

## Future Plans
My future plans are tracking favorites, making it searchable, sorting the options into categories, and searching for future meals.

## Tech Stack
- **Language:** Java
- **Libraries:** HttpClient, Gson, java.time
- **External tools:** Purdue Items API (https://apis.io/apis/purdue/purdue-menus-api/)

## Setup
Run these commands in your terminal at the desired location:
```bash
git clone https://github.com/Astern27/Purdue-Dining-Options.git
cd Purdue-Dining-Options
javac -cp "lib/gson-2.15.0.jar" src/Main.java
```
## Usage
Run this command in your terminal at the root of the project:
Windows:
```bash
java -cp "lib/gson-2.15.0.jar;src" Main
```
Mac/Linux:
```bash
java -cp "lib/gson-2.15.0.jar:src" Main
```
