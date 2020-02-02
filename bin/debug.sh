#!/bin/bash
# Debugs applet from local host
appletviewer -J-agentlib:jdwp=transport=dt_socket,address=5000,server=y,suspend=n "http://localhost:8080/VolanoChat.html"
