#!/bin/bash
#***********************************************************************
# startup.sh - VOLANO Chat Server Benchmark sample startup script.
# Usage: startup.sh server|client loop|net <vendor>
#***********************************************************************

# Name of the host running the server side of the network test.
host=test.example.com

# Number of messages sent per user.
count=100

#***********************************************************************
# Microsoft Windows with Cygwin <www.cygwin.com>
#***********************************************************************
cygwin () {
    for file in lib/*.jar; do classpath="$classpath;$file"; done
    case $2 in
        sun)
            java=/cygdrive/c/Windows/System32/java.exe
            if [ "$1" = "loop" ]
            then
                options="-server -Xmx64m"
            else
                options="-server -Xmx256m -Xss256k"
            fi
            ;;
        *)
            echo "Vendor choice is: sun"
            exit
            ;;
    esac
}

#***********************************************************************
# Apple Mac OS X
#***********************************************************************
darwin () {
    ulimit -Sn 10240
    for file in lib/*.jar; do classpath="$classpath:$file"; done
    case $2 in
        sun)
            java=/usr/bin/java
            if [ "$1" = "loop" ]
            then
                options="-server -Xmx64m"
            else
                options="-server -Xmx256m -Xss256k"
            fi
            ;;
        *)
            echo "Vendor choice is: sun"
            exit
            ;;
    esac
}

#***********************************************************************
# FreeBSD
#***********************************************************************
freebsd () {
    ulimit -Sn 10240
    for file in lib/*.jar; do classpath="$classpath:$file"; done
    case $2 in
        sun)
            java=/usr/local/java/bin/java
            if [ "$1" = "loop" ]
            then
                options="-server -Xmx64m"
            else
                options="-server -Xmx256m -Xss256k"
            fi
            ;;
        *)
            echo "Vendor choice is: sun"
            exit
            ;;
    esac
}

#***********************************************************************
# GNU/Linux
#***********************************************************************
linux () {
    ulimit -Sn 10240
    for file in lib/*.jar; do classpath="$classpath:$file"; done
    case $2 in
        sun)
            java=/usr/bin/java
            if [ "$1" = "loop" ]
            then
                options="-server -Xmx64m"
            else
                options="-server -Xmx256m -Xss256k"
            fi
            ;;
        *)
            echo "Vendor choice is: sun"
            exit
            ;;
    esac
}

#***********************************************************************
# Sun Solaris
#***********************************************************************
sunos () {
    ulimit -Sn 10240
    for file in lib/*.jar; do classpath="$classpath:$file"; done
    case $2 in
        sun)
            java=/usr/java/bin/java
            # Try also "-Xconcurrentio" option.
            if [ "$1" = "loop" ]
            then
                options="-server -Xmx64m"
            else
                options="-server -Xmx256m -Xss256k"
            fi
            ;;
        *)
            echo "Vendor choice is: sun"
            exit
            ;;
    esac
}

#***********************************************************************
# Start up the server side for the loopback and network test.
#***********************************************************************
server () {
    args="$options $properties -cp $classpath COM.volano.Main"
    # Remove server log files from previous tests.
    [ -f logs/support.log ] && rm logs/*.log

    sync
    echo "$java" $args
    echo
    # Start the VOLANO Chat Server.
    "$java" $args
}

#***********************************************************************
# Start up the client side for the loopback test.
#***********************************************************************
loopclient () {
    args="$options $properties -cp $classpath COM.volano.Mark -count $count"
    # Remove the test log files from previous tests.
    [ -f test-1.log ] && rm test-*.log

    sync
    echo "$java" $args
    echo
    for i in 1 2 3 4
    do
        sleep 60
        "$java" $args
        mv volano-mark.log test-$i.log
        echo -n "test-$i.log: " >> $logfile
        grep throughput test-$i.log >> $logfile
    done
}

#***********************************************************************
# Start up the client side for the network test.
#***********************************************************************
netclient () {
    args="$options $properties -cp $classpath COM.volano.Mark -count $count -host $host"
    # Remove the test log files from previous tests.
    [ -f test-1.log ] && rm test-*.log

    sync
    echo "$java" $args
    echo
    for i in $(seq 1 10)
    do
        sleep `expr $i \* 60`
        rooms=`expr $i \* 50`
        "$java" $args -rooms $rooms
        mv volano-mark.log test-$i.log
        echo -n "test-$i.log: " >> $logfile
        grep throughput test-$i.log >> $logfile
    done
}

#***********************************************************************
# Main body of this script.
# Usage: startup.sh server|client loop|net <vendor>
#***********************************************************************
side=$1     # server or client
type=$2     # loop or net
vendor=$3   # Java vendor name (depends on operating system)

# Check parameters.
if [ \( "$side" != "server" -a "$side" != "client" \) -o \( "$type" != "loop" -a "$type" != "net" \) ]
then
    echo "Usage: $0 server|client loop|net <vendor>"
    exit
fi

# Get operating system name and define output file.
os=`uname -s`
if [ -n "$vendor" ]
then
    logfile=${os}-${type}-${vendor}.txt
else
    logfile=${os}-${type}.txt
fi

# Define the Java system properties and class path.
properties="-Dinstall.root=. -Dcatalina.home=. -Djava.security.manager -Djava.security.policy=conf/policy.txt"
classpath=.

# Define any settings specific to each operating system.
case "$os" in
    CYGWIN*)
        cygwin $type $vendor
        ;;
    Darwin)
        darwin $type $vendor
        ;;
    FreeBSD)
        freebsd $type $vendor
        ;;
    Linux)
        linux $type $vendor
        ;;
    SunOS)
        sunos $type $vendor
        ;;
    *)
        echo "Unable to recognize operating system name \"$os\"."
        exit
        ;;
esac

# Quit if we cannot find the Java executable file.
if [ ! -x "$java" ]
then
    echo "Unable to find Java executable \"$java\"."
    exit
fi

# Print descriptor limit, Java version and command line.
echo "ulimit -Sn = `ulimit -Sn`"
echo
"$java" $options -version
echo

if [ "$side" = "server" ]
then
    server
else
    echo "[`date`] Test started." >> $logfile
    if [ "$type" = "loop" ]
    then
        loopclient
    else
        netclient
    fi
    echo "[`date`] Test ended." >> $logfile
fi

