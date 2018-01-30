#!/usr/bin/env bash

java -classpath /opt/stellar-notifier/lib/*:$(ls -1 /opt/stellar-notifier/stellar-notifier-*.jar | head -n1) com.sputnik.stellar.Launcher