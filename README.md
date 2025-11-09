# HealthAlertNotification
How to compile: 
	1. navigate to the directory that include the .java files using cd command
	2. run the command: javac *.java
How to run:
	1. make sure that "watchers" and "health" .txt files are in same directory with .java files
	   watchers.txt and health.txt files are uploaded with .java files for more convenience
	2. run the program using 2 commands below (one with --all and one without):
		a) without --all: java HealthAlertNotification watchers.txt health.txt
		b) with --all: java HealthAlertNotification --all watchers.txt health.txt
		NOTE: make sure that your .txt file names are matching with the command
Known Bugs and Limitations: 
	program works as it should without any known bugs.
	outputs are checked with sample files and are fully correct.
