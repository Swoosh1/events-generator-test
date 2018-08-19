# Order event generator

the program creates json events for orders placed and orders accepted/cancelled in batches and writes to file.

## program arguments

> number_of_events

total number of events to create

> batch_size

how many events per file

> interval

time in seconds between files

> output_directory

the directory on the system to write the event files

## build

use the following command to build a jar with the dependencies in the target folder

> mvn assembly:single

## Example

> java -jar generator-1.0-jar-with-dependencies.jar -interval 30 -batch_size 10 -number_of_events 100 -output_directory /Users/sorooshavazkhani/test-data/

