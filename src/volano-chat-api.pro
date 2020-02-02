-include client.pro

-keep public class COM.volano.IClient {
    public protected *;
}
-keep public class COM.volano.Server {
    public protected *;
}

-keepattributes Exceptions,InnerClasses,Signature
-keep,allowobfuscation public class COM.volano.chat.security.DSAAppletSecurity
-adaptclassstrings COM.volano.chat.security.AppletSecurity

