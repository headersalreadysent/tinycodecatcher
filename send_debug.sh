#!/bin/bash



# Send intent using adb
adb shell 'am broadcast -p co.ec.cnsyn.codecatcher -a co.ec.cnsyn.codecatcher.DEBUG_SMS --es "sender" "Test Sender" \
    --es "message" "'$1'"'
