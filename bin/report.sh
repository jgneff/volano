#!/bin/bash
# Prints the sizes and number of files in the applet Java archive files
dirlist="tmp lib ria"
jarlist="VolanoChat.jar MyVolanoChat.jar WebVolanoChat.jar"

for d in $dirlist; do
    printf "\n"
    for f in $jarlist; do
        printf "$d/$f\t"
        printf "$(ls -l --si $d/$f | awk '{print $5}')\t"
        printf "$(unzip -l $d/$f | grep files | awk '{print $2}') files\n"
    done
done
printf "\n"
