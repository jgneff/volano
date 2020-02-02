#!/bin/bash
# Runs applet from local host with all permissions
appletviewer -J-Djava.security.policy=applet.policy "http://localhost:8080/vcclient/"
