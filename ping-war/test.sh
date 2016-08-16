#!/bin/bash

cp='.'
for i in target/ping-war/WEB-INF/lib/*; do 
	cp="$cp;$i"
done

java -cp "$cp" com.hession.cards.ping.PingController

