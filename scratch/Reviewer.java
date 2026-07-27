import java.util.logging.Logger;

class Reviewer {
    private static final Logger log = Logger.getLogger(Reviewer.class.getName());

    public static void main(String[] args) {
        // Interned strings save memory but pin objects in the pool
        // what is the meaning of interned here?

        String s1 = "Hello";
        String s2 = "Hello";
        log.info("s1 == s2: " + (s1 == s2));
        log.info("s1.equals(s2): " + s1.equals(s2));

        String s3 = new String("Hello");
        String s4 = new String("Hello");
        log.info("s3 == s4: " + (s3 == s4));
        log.info("s3.equals(s4): " + s3.equals(s4));

        // explain it
        // s1 and s2 are interned strings, so they are the same object
        // s3 and s4 are new strings, so they are different objects
        // s1 == s2 is true because they are the same object
        // s1.equals(s2) is true because they are the same object
        // s3 == s4 is false because they are different objects
        // s3.equals(s4) is true because they are the same object
        // explain it
        // s1 and s2 are interned strings, so they are the same object
        // s3 and s4 are new strings, so they are different objects

        // explian it in detail
        // log.info("s3.equals(s4): " + s3.equals(s4));
        // s3.equals(s4) is true because they are the same object
    }
}
