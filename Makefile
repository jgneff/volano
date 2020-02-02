# ======================================================================
# Makefile for building the VOLANO chat server and related software
#
# Filename extensions of text files
#   .bat, .css, .html, .sh, .txt, .xml
# Filename extensions of binary files
#   .au, .exe, .gif, .jar, .jar.gz, .jar.pack.gz
# ======================================================================

# ======================================================================
# Variable definitions
# ======================================================================

# Version X.Y.Z (Major.Minor.Patch, see http://semver.org/)
VER = 2.13.5

# Site identifier
SITENAME ?= localhost

# Default Codebase attribute for applet JAR files
CODEBASE ?= localhost ip6-localhost

# Default Application-Library-Allowable-Codebase attribute for applet JAR files
DOCBASE ?= $(CODEBASE)

# Location of the Java Development Kit
JDKHOME = /usr/lib/jvm/java-8-openjdk-amd64

# Commands
JAVA      = $(JDKHOME)/bin/java
JAVAC     = $(JDKHOME)/bin/javac
JAR       = $(JDKHOME)/bin/jar
PACK200   = $(JDKHOME)/bin/pack200
UNPACK200 = $(JDKHOME)/bin/unpack200
JAVADOC   = $(JDKHOME)/bin/javadoc
JARSIGNER = $(JDKHOME)/bin/jarsigner
PROGUARD  = /usr/bin/proguard
GZIP      = /bin/gzip
RSYNC     = /usr/bin/rsync
ZIP       = /usr/bin/zip
TAR       = /bin/tar
SED       = /bin/sed

# JAR files
JDK8_JAR     = /usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar
SERVLET_JAR  = skel/product/lib/servlet.jar
CATALINA_JAR = skel/product/lib/catalina.jar
TOMCAT_JAR   = skel/product/lib/tomcat4-coyote.jar

# ======================================================================
# RFC 3161 Time Stamping Authority certificates as of January 19, 2017
#
# Ascertia - http://services.globaltrustfinder.com/adss/tsa
# No longer available?
#
# Comodo - http://timestamp.comodoca.com/rfc3161
# Valid from: Wed Dec 30 16:00:00 PST 2015 until: Tue Jul 09 11:40:36 PDT 2019
#
# Starfield - http://tsa.starfieldtech.com
# Valid from: Mon Dec 12 23:00:00 PST 2016 until: Sun Dec 12 23:00:00 PST 2021
#
# Symantec - http://timestamp.geotrust.com/tsa
# Valid from: Wed Jun 10 17:00:00 PDT 2015 until: Tue Dec 29 15:59:59 PST 2020
# ======================================================================

# Other variables
API_TITLE = "VOLANO Chat Client API Version $(VER)"
TIME_URL  = "http://tsa.starfieldtech.com"
KEY_ALIAS = "comodo-2018"
KEY_STORE = "$(HOME)/Private/keystores/comodo.jks"

# Command options
CLIENT_PATH  = -classpath classes
SERVER_PATH  = -classpath classes:$(SERVLET_JAR):$(CATALINA_JAR):$(TOMCAT_JAR)
SERVLET_PATH = -classpath classes:$(SERVLET_JAR)

CLIENT_TARGET = -target 1.8 -bootclasspath $(JDK8_JAR) -extdirs ""
SWING_TARGET  = -target 1.8 -bootclasspath $(JDK8_JAR) -extdirs ""
SERVER_TARGET = -target 1.8 -bootclasspath $(JDK8_JAR) -extdirs ""

JAVAC_FLAGS   = -source 1.8 -sourcepath src -d classes
CLIENT_FLAGS  = $(JAVAC_FLAGS) $(CLIENT_PATH) $(CLIENT_TARGET)
SWING_FLAGS   = $(JAVAC_FLAGS) $(CLIENT_PATH) $(SWING_TARGET)
SERVER_FLAGS  = $(JAVAC_FLAGS) $(SERVER_PATH) $(SERVER_TARGET)
SERVLET_FLAGS = $(JAVAC_FLAGS) $(SERVLET_PATH) $(SERVER_TARGET)

PACK200_FLAGS   = --effort=9
UNPACK200_FLAGS = --deflate-hint=false
JAVADOC_FLAGS   = -public -source 1.8 \
    -classpath classes -bootclasspath $(JDK8_JAR) \
    -version -author -windowtitle $(API_TITLE)
JARSIGNER_FLAGS = -keystore $(KEY_STORE) -storepass:env STOREPASS \
    -sigfile STATUS6 -tsa $(TIME_URL)
GZIP_FLAGS      = --to-stdout --best
RSYNC_FLAGS     = --archive
ZIP_FLAGS       = --quiet -X --to-crlf --suffixes .au:.gif:.gz --recurse-paths
TAR_FLAGS       = --create --gzip --file

# Local shell scripts
delete_classes = rm -rf classes/*

# Sed scripts
manifest_codebase = 's/CODEBASE/$(CODEBASE)/'
manifest_docbase = 's/DOCBASE/$(DOCBASE)/'

# Sed command options
sed_manifest = -e $(manifest_codebase) -e $(manifest_docbase)

# Local variables
upgrade = opt/volano-upgrade-$(VER)-$(SITENAME)
product = opt/volano-$(VER)-$(SITENAME)
clientapi = opt/volano-clientapi-$(VER)-$(SITENAME)
checksum = opt/md5sum-$(VER)-$(SITENAME)
benchmark = opt/volano-benchmark-$(VER)

# Lists of prerequisites
constants = src/COM/volano/chat/security/Constants.java
clientlist = \
    ria/Agent.jar \
    ria/Agent.jar.pack.gz \
    ria/Agent.jar.gz \
    ria/VolanoChat.jar \
    ria/VolanoChat.jar.pack.gz \
    ria/VolanoChat.jar.gz \
    ria/MyVolanoChat.jar \
    ria/MyVolanoChat.jar.pack.gz \
    ria/MyVolanoChat.jar.gz \
    ria/WebVolanoChat.jar \
    ria/WebVolanoChat.jar.pack.gz \
    ria/WebVolanoChat.jar.gz
apilist = \
    ria/volano-chat-api.jar \
    ria/volano-chat-api.jar.pack.gz \
    ria/volano-chat-api.jar.gz
serverlist = \
    lib/volano-chat-server.jar \
    lib/volano-chat-servlets.jar
testlist = \
    lib/volano-chat-server.jar \
    lib/volano-chat-tests.jar
javadoclist = \
    src/COM/volano/IClient.java \
    src/COM/volano/Server.java

# ======================================================================
# Implicit pattern rules
# ======================================================================

.PRECIOUS: lib/%.jar
lib/%.jar: tmp/%.jar
	$(PROGUARD) @src/$*.pro -printmapping tmp/$*.map -injars $< -outjars $@

tmp/%.mf: src/%.mf
	$(SED) $(sed_manifest) $< > $@

tmp/%.site.jar: lib/%.jar tmp/%.mf
	cp $< $@
	$(JAR) -umf tmp/$*.mf $@

tmp/%.repacked.jar: tmp/%.site.jar
	$(PACK200) --repack $@ $<

ria/%.jar: tmp/%.repacked.jar
	$(JARSIGNER) $(JARSIGNER_FLAGS) -signedjar $@ $< $(KEY_ALIAS)

ria/%.jar.pack.gz: ria/%.jar
	$(PACK200) $(PACK200_FLAGS) $@ $<

tmp/%.stored.jar: ria/%.jar.pack.gz
	$(UNPACK200) $(UNPACK200_FLAGS) $< $@

# Solid compression (16 percent smaller)
ria/%.jar.gz: tmp/%.stored.jar
	$(GZIP) $(GZIP_FLAGS) $< > $@

# Final software packages
opt/%.zip: opt/%
	cd $(dir $@); $(ZIP) $(ZIP_FLAGS) $(notdir $@) $(notdir $<)

opt/%.tar.gz: opt/%
	cd $(dir $@); $(TAR) $(TAR_FLAGS) $(notdir $@) $(notdir $<)

# ======================================================================
# Phony targets
# ======================================================================

.PHONY: all keys upgrade product clientapi checksum benchmark license

all: $(constants)
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/IClient.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/Server.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/Agent.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/VolanoChat.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/MyVolanoChat.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/WebVolanoChat.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/BannerPlayer.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/chat/security/DSAAppletSecurity.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/awt/AWTTextPanel.java
	$(JAVAC) $(SWING_FLAGS) src/COM/volano/swing/SwingTextPanel.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/Mark.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/Test.java
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/KeepAlive.java
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/Main.java
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/Shutdown.java
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/Status.java
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/CountServlet.java
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/RegisterServlet.java
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/Sign.java
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/Verify.java
	$(JAVAC) $(SERVLET_FLAGS) src/COM/volano/ConfigServlet.java
	$(JAVAC) $(SERVLET_FLAGS) src/COM/volano/ProxyServlet.java

keys:
	touch src/COM/volano/chat/security/Keys.java

upgrade: $(upgrade).zip $(upgrade).tar.gz

product: $(product).zip $(product).tar.gz

clientapi: $(clientapi).zip $(clientapi).tar.gz

checksum: $(checksum).txt

benchmark: $(benchmark).zip $(benchmark).tar.gz

license: lib/volano-chat-license.jar

# Site-specific targets
include sites.mk

# ======================================================================
# Explicit rules
# ======================================================================

$(constants): src/COM/volano/chat/security/Keys.java \
    src/COM/volano/chat/security/AppletSecureRandom.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/chat/security/Keys.java
	$(JAVA) $(CLIENT_PATH) COM.volano.chat.security.Keys COM.volano.chat.security > $@

tmp/volano-chat-api.jar: $(constants)
	$(delete_classes)
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/IClient.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/Server.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/chat/security/DSAAppletSecurity.java
	$(JAR) -cf $@ -C classes COM

tmp/Agent.jar:
	$(delete_classes)
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/Agent.java
	$(JAR) -cf $@ -C classes COM

tmp/VolanoChat.jar: $(constants)
	$(delete_classes)
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/VolanoChat.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/BannerPlayer.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/chat/security/DSAAppletSecurity.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/awt/AWTTextPanel.java
	$(JAVAC) $(SWING_FLAGS) src/COM/volano/swing/SwingTextPanel.java
	$(JAR) -cf $@ -C classes COM

tmp/MyVolanoChat.jar: $(constants)
	$(delete_classes)
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/MyVolanoChat.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/BannerPlayer.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/chat/security/DSAAppletSecurity.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/awt/AWTTextPanel.java
	$(JAVAC) $(SWING_FLAGS) src/COM/volano/swing/SwingTextPanel.java
	$(JAR) -cf $@ -C classes COM

tmp/WebVolanoChat.jar: $(constants)
	$(delete_classes)
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/WebVolanoChat.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/chat/security/DSAAppletSecurity.java
	$(JAVAC) $(CLIENT_FLAGS) src/COM/volano/awt/AWTTextPanel.java
	$(JAVAC) $(SWING_FLAGS) src/COM/volano/swing/SwingTextPanel.java
	$(JAR) -cf $@ -C classes COM

tmp/volano-chat-tests.jar:
	$(delete_classes)
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/Mark.java
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/Test.java
	$(JAR) -cf $@ -C classes COM

tmp/volano-chat-server.jar: $(constants)
	$(delete_classes)
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/KeepAlive.java
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/Main.java
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/Shutdown.java
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/Status.java
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/CountServlet.java
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/RegisterServlet.java
	$(JAR) -cf $@ -C classes COM

tmp/volano-chat-license.jar:
	$(delete_classes)
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/Sign.java
	$(JAVAC) $(SERVER_FLAGS) src/COM/volano/Verify.java
	$(JAR) -cf $@ -C classes COM

tmp/volano-chat-servlets.jar:
	$(delete_classes)
	$(JAVAC) $(SERVLET_FLAGS) src/COM/volano/ConfigServlet.java
	$(JAVAC) $(SERVLET_FLAGS) src/COM/volano/ProxyServlet.java
	$(JAR) -cf $@ -C classes COM

$(upgrade): $(clientlist) $(serverlist)
	rm -rf $@
	$(RSYNC) $(RSYNC_FLAGS) skel/upgrade/ $@/
	cp lib/volano-chat-server.jar   $@/lib/
	cp lib/volano-chat-servlets.jar $@/webapps/ROOT/WEB-INF/lib/
	cp $(clientlist) $@/webapps/ROOT/vcclient/COM/volano/
	touch $@

$(product): $(upgrade)
	rm -rf $@
	$(RSYNC) $(RSYNC_FLAGS) skel/product/ $@/
	$(RSYNC) $(RSYNC_FLAGS) $</ $@/
	touch $@

$(clientapi): $(apilist)
	rm -rf $@
	$(RSYNC) $(RSYNC_FLAGS) skel/clientapi/ $@/
	$(JAVADOC) $(JAVADOC_FLAGS) -d $@/doc $(javadoclist)
	cp $(apilist) $@/lib/
	touch $@

$(benchmark): $(testlist)
	rm -rf $@
	$(RSYNC) $(RSYNC_FLAGS) skel/benchmark/ $@/
	$(RSYNC) $(RSYNC_FLAGS) skel/product/conf/ $@/conf/
	$(RSYNC) $(RSYNC_FLAGS) skel/product/lib/ $@/lib/
	cp $(testlist) $@/lib/
	touch $@

$(upgrade).zip: $(upgrade)
	cd $<; $(ZIP) $(ZIP_FLAGS) ../$(notdir $@) *

$(upgrade).tar.gz: $(upgrade)
	cd $<; $(TAR) $(TAR_FLAGS) ../$(notdir $@) *

$(checksum).txt:
	cd opt; md5sum volano*$(VER)-$(SITENAME).* > $(notdir $@)

# ======================================================================
# Phony rules for debugging and clean-up
# ======================================================================

.PHONY: debug clean

debug:
	ln --symbolic --force --no-target-directory $(notdir $(product)) opt/volano

clean:
	$(delete_classes)
	rm -f lib/*.jar
	rm -rf tmp/* ria/* opt/*
