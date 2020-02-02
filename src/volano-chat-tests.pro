-include server.pro
-repackageclasses 'COM.volano.test'

-keep public class COM.volano.Mark {
    public static void main(java.lang.String[]);
}
-keep public class COM.volano.Test {
    public static void main(java.lang.String[]);
}

