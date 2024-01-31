package krsto.zaric.shoppinglist;

public class JNIexample {

    static {
        System.loadLibrary("MyLibrary");
    }

    public native int increment(int x);
}
