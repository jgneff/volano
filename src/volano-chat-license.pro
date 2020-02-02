-include server.pro
-libraryjars ../skel/product/lib/catalina.jar

-keep public class COM.volano.Sign {
    public static void main(java.lang.String[]);
}
-keep public class COM.volano.Verify {
    public static void main(java.lang.String[]);
}

