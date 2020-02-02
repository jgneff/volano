-include client.pro

-keep public class COM.volano.VolanoChat
-keep public class COM.volano.BannerPlayer

-keep,allowobfuscation public class COM.volano.chat.security.DSAAppletSecurity
-keep,allowobfuscation public class COM.volano.awt.AWTTextPanel
-keep,allowobfuscation public class COM.volano.swing.SwingTextPanel

-adaptclassstrings COM.volano.chat.security.AppletSecurity
-adaptclassstrings COM.volano.awt.TextPanel

