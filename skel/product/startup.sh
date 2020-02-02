#!/bin/sh
#*****************************************************************************
# Sample Bourne Shell script for starting the VOLANO Chat Server.
# Specify the "-h" option for help.
#*****************************************************************************
if [ "$1" = "-h" ]
then
    echo "Usage: $0 [-h] [-r] [file]"
    echo "Start the VOLANO Chat Server. The default is to start the server"
    echo "once and load its properties from the file \"conf/properties.txt\"."
    echo "  -h    display this help information."
    echo "  -r    restart the server if it stops."
    echo "  file  the location of the server properties file."
    exit
fi

# Define the Java system properties and class path.
properties="-Dinstall.root=. -Dcatalina.home=."
security="-Djava.security.manager -Djava.security.policy=conf/policy.txt"
classpath=.

# Restrict the permissions on the server properties file.
chmod 600 conf/properties.txt

# Define settings specific to each operating system.
os=`uname -s`
case $os in
    CYGWIN*)    # Microsoft Windows with Cygwin <www.cygwin.com>
        for file in lib/*.jar; do classpath="$classpath;$file"; done
        # Java 8 Update 5 Server JRE installed in "C:\jdk1.8.0_05" requires:
        #   java=/cygdrive/c/jdk1.8.0_05/bin/java
        java=/cygdrive/c/Windows/System32/java.exe
        options="-Xmx256m -Xss256k"
        ;;

    Darwin)     # Apple Mac OS X
        ulimit -Sn 1024
        for file in lib/*.jar; do classpath="$classpath:$file"; done
        java=/usr/bin/java
        options="-server -Xmx256m -Xss256k"
        ;;

    FreeBSD)    # FreeBSD
        ulimit -Sn 1024
        for file in lib/*.jar; do classpath="$classpath:$file"; done
        java=/usr/local/java/bin/java
        options="-server -Xmx256m -Xss256k"
        ;;

    Linux)      # GNU/Linux
        ulimit -Sn 1024
        for file in lib/*.jar; do classpath="$classpath:$file"; done
        java=/usr/bin/java
        options="-server -Xmx256m -Xss256k"
        ;;

    SunOS)      # Oracle Solaris
        ulimit -Sn 1024
        for file in lib/*.jar; do classpath="$classpath:$file"; done
        java=/usr/java/bin/java
        options="-server -Xmx256m -Xss256k"
        ;;

    *)          # Unknown operating system
        echo "Unrecognized operating system name: $os"
        exit
        ;;
esac

# Quit if we cannot find the Java executable file.
if [ ! -x "$java" ]
then
    echo "Unable to find Java executable \"$java\"."
    exit
fi

# Print the Java version.
"$java" $options -version

# Start the VOLANO Chat Server.
# The "-r" option specifies to restart the server if it terminates.
if [ "$1" = "-r" ]
then
    while :
    do
        echo "[`date`] Starting server ..."
        "$java" $options $properties $security -cp $classpath COM.volano.Main $2
        code=$?
        echo "[`date`] Server terminated with exit code $code."
        sleep 15
    done
else
    "$java" $options $properties $security -cp $classpath COM.volano.Main $1
fi
