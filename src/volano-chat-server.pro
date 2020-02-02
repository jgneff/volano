-include server.pro
-libraryjars ../skel/product/lib/servlet.jar
-libraryjars ../skel/product/lib/catalina.jar
-libraryjars ../skel/product/lib/tomcat4-coyote.jar

-keep public class COM.volano.KeepAlive {
    public static void main(java.lang.String[]);
}
-keep public class COM.volano.Main {
    public static void main(java.lang.String[]);
}
-keep public class COM.volano.Shutdown {
    public static void main(java.lang.String[]);
}
-keep public class COM.volano.Status {
    public static void main(java.lang.String[]);
}

-keep public class COM.volano.CountServlet
-keep public class COM.volano.RegisterServlet

