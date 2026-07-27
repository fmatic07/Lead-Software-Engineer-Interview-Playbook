import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Equals / hashCode contract:
 *   1. a.equals(b) == true  ⇒  a.hashCode() == b.hashCode()
 *   2. Unequal objects MAY share a hash (collisions OK)
 *   3. Both must stay consistent while fields used by them don't change
 *   4. HashMap / HashSet: hash picks the bucket, equals finds the entry
 */
class EqualsHashCodeDemo {
    private static final Logger log = Logger.getLogger(EqualsHashCodeDemo.class.getName());

    public static void main(String[] args) {
        // demoCorrectContract();
        demoBrokenHashCode();
        // demoMutatedKey();
    }

    /** Correct: equal instances behave as one key in HashSet / HashMap. */
    static void demoCorrectContract() {
        log.info("--- correct equals + hashCode ---");
        Employee a = new Employee(1, "Ada");
        Employee b = new Employee(1, "Ada");

        log.info("a == b: " + (a == b));                 // false — different objects
        log.info("a.equals(b): " + a.equals(b));         // true  — same logical identity
        log.info("same hash: " + (a.hashCode() == b.hashCode()));
        log.info("a.hashCode(): " + a.hashCode());

        HashSet<Employee> set = new HashSet<>();
        set.add(a);
        set.add(b);
        log.info("HashSet size (expect 1): " + set.size());

        HashMap<Employee, String> map = new HashMap<>();
        map.put(a, "engineer");
        String c = map.get(b); // value type is String, key type is Employee
        log.info("map.get(b) (expect engineer): " + c);
    }

    /** Broken: equals without hashCode → equal objects land in different buckets. */
    static void demoBrokenHashCode() {
        log.info("--- equals only, default hashCode ---");
        BrokenEmployee a = new BrokenEmployee(1, "Ada");
        BrokenEmployee b = new BrokenEmployee(1, "Ada");

        log.info("a.equals(b): " + a.equals(b));         // true
        log.info("same hash: " + (a.hashCode() == b.hashCode())); // usually false (identity hash)

        HashSet<BrokenEmployee> set = new HashSet<>();
        set.add(a);
        set.add(b);
        log.info("HashSet size (expect 1, often 2): " + set.size());
    }

    /** Mutating a field used by hashCode after insert loses the key. */
    static void demoMutatedKey() {
        log.info("--- mutable key after HashMap.put ---");
        Employee key = new Employee(1, "Ada");
        HashMap<Employee, String> map = new HashMap<>();
        map.put(key, "engineer");

        key.setName("Grace"); // changes hash bucket
        log.info("map.get(same instance) after mutate: " + map.get(key)); // often null
        log.info("map.containsValue(engineer): " + map.containsValue("engineer")); // still true
    }
}

/** Correct implementation — id + name drive both equals and hashCode. */
class Employee {
    private final int id;
    private String name;

    Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee other)) return false;
        return id == other.id && Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Employee{id=" + id + ", name='" + name + "'}";
    }
}

/** Broken: overrides equals but not hashCode (identity hash remains). */
class BrokenEmployee {
    private final int id;
    private final String name;

    BrokenEmployee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BrokenEmployee other)) return false;
        return id == other.id && Objects.equals(name, other.name);
    }

    // hashCode intentionally NOT overridden

    @Override
    public String toString() {
        return "BrokenEmployee{id=" + id + ", name='" + name + "'}";
    }
}
