import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Detailed, topic-by-topic demos for Collection + Map.
 *
 * Run (JDK 21):
 *   javac CollectionsMapDemo.java
 *   java CollectionsMapDemo
 *
 * Or run one section:
 *   java CollectionsMapDemo list
 *   java CollectionsMapDemo set
 *   java CollectionsMapDemo queue
 *   java CollectionsMapDemo ops
 *   java CollectionsMapDemo map
 *   java CollectionsMapDemo variants
 *   java CollectionsMapDemo compute
 *   java CollectionsMapDemo equals
 *   java CollectionsMapDemo treeset
 *   java CollectionsMapDemo nulls
 *   java CollectionsMapDemo concurrent
 *   java CollectionsMapDemo choose
 */
class CollectionsMapDemo {
    private static final Logger log = Logger.getLogger(CollectionsMapDemo.class.getName());

    public static void main(String[] args) {
        // String topic = args.length > 0 ? args[0].toLowerCase() : "all";
        
        String topic = "map";

        switch (topic) {
            case "list" -> demoList();
            case "set" -> demoSet();
            case "queue" -> demoQueue();
            case "ops" -> demoCommonOperations();
            case "map" -> demoMapBasics();
            case "variants" -> demoMapVariants();
            case "compute" -> demoComputeApis();
            case "equals" -> demoEqualsHashCodeKeys();
            case "treeset" -> demoTreeSetCompareGotcha();
            case "nulls" -> demoNullAndImmutability();
            case "concurrent" -> demoConcurrentMap();
            case "choose" -> demoChoosingQuickly();
            case "all" -> {
                demoList();
                demoSet();
                demoQueue();
                demoCommonOperations();
                demoMapBasics();
                demoMapVariants();
                demoComputeApis();
                demoEqualsHashCodeKeys();
                demoTreeSetCompareGotcha();
                demoNullAndImmutability();
                demoConcurrentMap();
                demoChoosingQuickly();
            }
            default -> log.warning("Unknown topic: " + topic + " (use all|list|set|queue|ops|map|variants|compute|equals|treeset|nulls|concurrent|choose)");
        }
    }

    // -------------------------------------------------------------------------
    // List — ordered, duplicates OK, index access
    // -------------------------------------------------------------------------

    static void demoList() {
        section("1. List — ArrayList (default), LinkedList (rare)");

        List<String> names = new ArrayList<>();
        names.add("Ada");
        names.add("Grace");
        names.add("Ada"); // duplicates allowed
        names.add(1, "Katherine"); // insert at index → [Ada, Katherine, Grace, Ada]

        log.info("list: " + names);
        log.info("get(0): " + names.get(0));
        log.info("set(0, 'Alan') replaces index 0");
        names.set(0, "Alan");
        log.info("after set: " + names);
        log.info("indexOf(Ada): " + names.indexOf("Ada") + "  lastIndexOf(Ada): " + names.lastIndexOf("Ada"));
        log.info("contains(Grace): " + names.contains("Grace"));
        log.info("remove(Ada) removes FIRST match: " + names.remove("Ada"));
        log.info("after remove(Ada): " + names);
        log.info("remove(0) by index: " + names.remove(0));
        log.info("final list size=" + names.size() + " → " + names);

        // LinkedList works as List, but indexed get is O(n) — prefer ArrayDeque for ends
        LinkedList<String> linked = new LinkedList<>();
        linked.addFirst("front");
        linked.addLast("back");
        linked.add(1, "middle");
        log.info("LinkedList as deque: " + linked + "  (prefer ArrayDeque for stack/queue)");
    }

    // -------------------------------------------------------------------------
    // Set — uniqueness; order depends on implementation
    // -------------------------------------------------------------------------

    static void demoSet() {
        section("2. Set — HashSet / LinkedHashSet / TreeSet");

        Set<String> hash = new HashSet<>();
        hash.add("java");
        hash.add("spring");
        hash.add("java"); // ignored — already present
        log.info("HashSet add('java') twice → size=" + hash.size() + "  contents (no order): " + hash);
        log.info("contains(spring): " + hash.contains("spring"));

        Set<String> linked = new LinkedHashSet<>();
        linked.add("first");
        linked.add("second");
        linked.add("third");
        linked.add("first"); // ignored, order kept
        log.info("LinkedHashSet (insertion order): " + linked);

        Set<String> tree = new TreeSet<>();
        tree.add("banana");
        tree.add("apple");
        tree.add("cherry");
        log.info("TreeSet (sorted natural order): " + tree);
        log.info("TreeSet first/last: " + ((TreeSet<String>) tree).first() + " / " + ((TreeSet<String>) tree).last());
    }

    // -------------------------------------------------------------------------
    // Queue / Deque / PriorityQueue
    // -------------------------------------------------------------------------

    static void demoQueue() {
        section("3. Queue / Deque — ArrayDeque + PriorityQueue");

        // FIFO queue
        Deque<String> queue = new ArrayDeque<>();
        queue.offer("job-1");
        queue.offer("job-2");
        queue.offer("job-3");
        log.info("FIFO queue: " + queue);
        log.info("poll() removes head: " + queue.poll()); // job-1
        log.info("peek() looks at head: " + queue.peek()); // job-2
        log.info("remaining: " + queue);

        // LIFO stack (prefer ArrayDeque over legacy Stack)
        Deque<String> stack = new ArrayDeque<>();
        stack.push("bottom");
        stack.push("middle");
        stack.push("top");
        log.info("stack after push: " + stack);
        log.info("pop() → " + stack.pop() + "  remaining: " + stack);

        // PriorityQueue — always poll the "smallest" (or custom priority)
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        pq.offer(30);
        pq.offer(10);
        pq.offer(20);
        log.info("PriorityQueue poll order (natural min-heap): "
                + pq.poll() + ", " + pq.poll() + ", " + pq.poll());

        PriorityQueue<String> byLength = new PriorityQueue<>(Comparator.comparingInt(String::length));
        byLength.offer("bbbb");
        byLength.offer("a");
        byLength.offer("ccc");
        log.info("PriorityQueue by length: "
                + byLength.poll() + ", " + byLength.poll() + ", " + byLength.poll());
    }

    // -------------------------------------------------------------------------
    // Common Collection operations
    // -------------------------------------------------------------------------

    static void demoCommonOperations() {
        section("4. Common Collection operations");

        List<String> list = new ArrayList<>(List.of("a", "b", "c", "d"));
        log.info("start: " + list + "  size=" + list.size() + "  isEmpty=" + list.isEmpty());

        log.info("contains(b): " + list.contains("b"));
        list.remove("b");
        log.info("after remove(b): " + list);

        // for-each
        StringBuilder walk = new StringBuilder("for-each: ");
        for (String s : list) {
            walk.append(s).append(' ');
        }
        log.info(walk.toString().trim());

        // iterator + safe remove during iteration
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().equals("c")) {
                it.remove(); // safe
            }
        }
        log.info("after iterator.remove(c): " + list);

        // fail-fast: mutating while for-each throws
        try {
            for (String s : list) {
                list.add("boom"); // structural change during iteration
                log.info("saw " + s);
            }
        } catch (ConcurrentModificationException e) {
            log.info("ConcurrentModificationException (expected): do not mutate list inside for-each");
        }

        list.clear();
        log.info("after clear: " + list + "  isEmpty=" + list.isEmpty());
    }

    // -------------------------------------------------------------------------
    // Map basics
    // -------------------------------------------------------------------------

    static void demoMapBasics() {
        section("5. Map basics — key → value (HashMap)");

        Map<String, Integer> ages = new HashMap<>();
        ages.put("Ada", 36);
        ages.put("Grace", 45);
        ages.put("Ada", 37); // same key → replaces value
        log.info("map after puts: " + ages);
        log.info("get(Ada): " + ages.get("Ada"));
        log.info("get(missing) returns null: " + ages.get("Bob"));
        log.info("getOrDefault(Bob, 0): " + ages.getOrDefault("Bob", 0));
        log.info("containsKey(Grace): " + ages.containsKey("Grace"));
        log.info("containsValue(45): " + ages.containsValue(45));

        log.info("keySet():   " + ages.keySet());
        log.info("values():   " + ages.values());
        log.info("entrySet(): " + ages.entrySet());

        log.info("iterate entrySet:");
        for (Map.Entry<String, Integer> e : ages.entrySet()) {
            log.info("  " + e.getKey() + " → " + e.getValue());
        }

        ages.forEach((name, age) -> log.info("forEach: " + name + " is " + age));
        ages.remove("Grace");
        log.info("after remove(Grace): " + ages);
    }

    // -------------------------------------------------------------------------
    // Map variants — HashMap, LinkedHashMap, TreeMap, EnumMap, LRU
    // -------------------------------------------------------------------------

    static void demoMapVariants() {
        section("6. Map variants — order, ranges, EnumMap, simple LRU");

        Map<String, Integer> hash = new HashMap<>();
        hash.put("b", 2);
        hash.put("a", 1);
        hash.put("c", 3);
        log.info("HashMap (no guaranteed order): " + hash);

        Map<String, Integer> linked = new LinkedHashMap<>();
        linked.put("b", 2);
        linked.put("a", 1);
        linked.put("c", 3);
        log.info("LinkedHashMap (insertion order): " + linked);

        TreeMap<String, Integer> tree = new TreeMap<>();
        tree.put("banana", 2);
        tree.put("apple", 1);
        tree.put("cherry", 3);
        tree.put("date", 4);
        log.info("TreeMap (sorted keys): " + tree);
        log.info("  firstKey/lastKey: " + tree.firstKey() + " / " + tree.lastKey());
        log.info("  headMap(cherry)  keys < cherry:  " + tree.headMap("cherry"));
        log.info("  tailMap(cherry)  keys >= cherry: " + tree.tailMap("cherry"));
        log.info("  subMap(apple, date) [apple, date): " + tree.subMap("apple", "date"));

        // EnumMap — keys are one enum type; dense and fast
        enum Priority { LOW, MEDIUM, HIGH }
        Map<Priority, String> labels = new EnumMap<>(Priority.class);
        labels.put(Priority.LOW, "later");
        labels.put(Priority.HIGH, "now");
        log.info("EnumMap (enum key order): " + labels);

        // Access-order LinkedHashMap → tiny LRU (eldest removed when over capacity)
        final int capacity = 3;
        Map<String, String> lru = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                return size() > capacity;
            }
        };
        lru.put("a", "A");
        lru.put("b", "B");
        lru.put("c", "C");
        log.info("LRU after a,b,c: " + lru);
        lru.get("a"); // access moves "a" to newest
        log.info("LRU after get(a): " + lru);
        lru.put("d", "D"); // evicts eldest ("b")
        log.info("LRU after put(d) capacity=" + capacity + ": " + lru);
    }

    // -------------------------------------------------------------------------
    // Useful Map APIs
    // -------------------------------------------------------------------------

    static void demoComputeApis() {
        section("7. Map compute APIs — putIfAbsent / computeIfAbsent / merge / replace");

        Map<String, Integer> counts = new HashMap<>();

        counts.putIfAbsent("hits", 0);
        counts.putIfAbsent("hits", 99); // ignored — key already present
        log.info("putIfAbsent: " + counts);

        // build value lazily only when key is missing
        Integer created = counts.computeIfAbsent("misses", k -> {
            log.info("  computeIfAbsent loader ran for key=" + k);
            return 0;
        });
        log.info("computeIfAbsent created misses=" + created + " → " + counts);
        counts.computeIfAbsent("misses", k -> {
            log.info("  loader should NOT run again");
            return 999;
        });
        log.info("computeIfAbsent second call (no reload): " + counts);

        // merge = upsert + combine
        counts.merge("hits", 1, Integer::sum);   // 0+1
        counts.merge("hits", 1, Integer::sum);   // 1+1
        counts.merge("errors", 1, Integer::sum); // insert 1
        log.info("after merge: " + counts);

        // replace — conditional update
        boolean replaced = counts.replace("hits", 2, 100);
        log.info("replace(hits, 2, 100) success=" + replaced + " → " + counts);
        boolean skipped = counts.replace("hits", 2, 50); // old value wrong → no-op
        log.info("replace(hits, 2, 50) success=" + skipped + " → " + counts);

        log.info("getOrDefault(unknown, -1): " + counts.getOrDefault("unknown", -1));
    }

    // -------------------------------------------------------------------------
    // Keys, equals, hashCode
    // -------------------------------------------------------------------------

    static void demoEqualsHashCodeKeys() {
        section("8. Keys — equals/hashCode contract + mutable key danger");

        UserKey a = new UserKey(1, "Ada");
        UserKey b = new UserKey(1, "Ada");
        log.info("a == b: " + (a == b) + "  (different objects)");
        log.info("a.equals(b): " + a.equals(b) + "  same hash? " + (a.hashCode() == b.hashCode()));

        Set<UserKey> set = new HashSet<>();
        set.add(a);
        set.add(b);
        log.info("HashSet size with equal keys (expect 1): " + set.size());

        Map<UserKey, String> map = new HashMap<>();
        map.put(a, "engineer");
        log.info("map.get(b) finds via equals/hashCode: " + map.get(b));

        // Broken: equals without hashCode → equal objects land in different buckets
        BrokenKey x = new BrokenKey(1, "Ada");
        BrokenKey y = new BrokenKey(1, "Ada");
        Set<BrokenKey> broken = new HashSet<>();
        broken.add(x);
        broken.add(y);
        log.info("BrokenKey equals=true but default hashCode → HashSet size (often 2): " + broken.size());

        // Mutating a key field used by hashCode after put loses the entry
        UserKey key = new UserKey(1, "Ada");
        Map<UserKey, String> fragile = new HashMap<>();
        fragile.put(key, "engineer");
        key.setName("Grace"); // changes hash bucket
        log.info("after mutate name: map.get(same instance)=" + fragile.get(key)
                + "  containsValue(engineer)=" + fragile.containsValue("engineer"));
        log.info("  → entry still in map but unreachable by new hash — prefer immutable keys");
    }

    // -------------------------------------------------------------------------
    // TreeSet compare gotcha
    // -------------------------------------------------------------------------

    static void demoTreeSetCompareGotcha() {
        section("9. TreeSet/TreeMap — uniqueness is compare==0, not equals");

        record Person(int id, String name) {}

        Set<Person> byEquals = new HashSet<>();
        byEquals.add(new Person(1, "Ada"));
        byEquals.add(new Person(1, "Grace")); // different equals (name differs)
        log.info("HashSet (equals) size expect 2: " + byEquals.size() + " → " + byEquals);

        Set<Person> byIdOnly = new TreeSet<>(Comparator.comparingInt(Person::id));
        byIdOnly.add(new Person(1, "Ada"));
        byIdOnly.add(new Person(1, "Grace")); // compare==0 → treated as duplicate, second dropped
        log.info("TreeSet(id-only) size expect 1: " + byIdOnly.size() + " → " + byIdOnly);

        TreeMap<Person, String> treeMap = new TreeMap<>(Comparator.comparingInt(Person::id));
        treeMap.put(new Person(1, "Ada"), "first");
        treeMap.put(new Person(1, "Grace"), "second"); // replaces — same compare key
        log.info("TreeMap(id-only) also collapses: " + treeMap);
    }

    // -------------------------------------------------------------------------
    // Null + immutability
    // -------------------------------------------------------------------------

    static void demoNullAndImmutability() {
        section("10. Null policy + immutable vs unmodifiable");

        Map<String, String> hash = new HashMap<>();
        hash.put(null, "null-key-ok");
        hash.put("x", null);
        log.info("HashMap allows one null key + null values: " + hash);

        try {
            ConcurrentHashMap<String, String> chm = new ConcurrentHashMap<>();
            chm.put("a", null);
        } catch (NullPointerException e) {
            log.info("ConcurrentHashMap rejects null values: " + e);
        }

        try {
            new TreeMap<String, String>().put(null, "nope");
        } catch (NullPointerException e) {
            log.info("TreeMap rejects null keys: " + e);
        }

        // Immutable factories — no nulls, no mutation
        List<String> fixed = List.of("a", "b");
        Set<String> fixedSet = Set.of("x", "y");
        Map<String, Integer> fixedMap = Map.of("a", 1, "b", 2);
        log.info("List.of / Set.of / Map.of: " + fixed + " " + fixedSet + " " + fixedMap);
        try {
            fixed.add("c");
        } catch (UnsupportedOperationException e) {
            log.info("List.of is immutable: " + e);
        }

        // Unmodifiable VIEW — cannot change through view, but backing list still can
        List<String> mutable = new ArrayList<>(List.of("a", "b"));
        List<String> view = Collections.unmodifiableList(mutable);
        try {
            view.add("c");
        } catch (UnsupportedOperationException e) {
            log.info("unmodifiableList rejects view.add: " + e);
        }
        mutable.add("c"); // backing still mutable
        log.info("after mutable.add(c), view reflects it: " + view);
        log.info("  → prefer List.copyOf / List.of at API boundaries when you want a true snapshot");
    }

    // -------------------------------------------------------------------------
    // ConcurrentHashMap — per-key safe, multi-step still races
    // -------------------------------------------------------------------------

    static void demoConcurrentMap() {
        section("11. ConcurrentHashMap — safe ops, not multi-key transactions");

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("hits", 0);

        // Prefer atomic methods over check-then-act
        map.compute("hits", (k, v) -> v + 1);
        map.merge("hits", 1, Integer::sum);
        log.info("atomic updates: " + map);

        // Illustrate check-then-act is NOT atomic even on ConcurrentHashMap
        // (single-threaded demo of the pattern — in real races two threads both "see" 0)
        Integer seen = map.get("hits");
        if (seen != null && seen < 100) {
            map.put("hits", seen + 1); // another thread could interleave between get and put
        }
        log.info("check-then-act pattern is racy under concurrency; use compute/merge instead → " + map);

        map.putIfAbsent("config", 42);
        log.info("putIfAbsent(config,42): " + map);
    }

    // -------------------------------------------------------------------------
    // Choosing quickly — same data, different structures
    // -------------------------------------------------------------------------

    static void demoChoosingQuickly() {
        section("12. Choosing quickly — same names, different jobs");

        List<String> asList = new ArrayList<>(List.of("Ada", "Grace", "Ada"));
        Set<String> asSet = new HashSet<>(asList);
        Map<String, Integer> asMap = new HashMap<>();
        asList.forEach(n -> asMap.merge(n, 1, Integer::sum));

        log.info("List  (order + duplicates):     " + asList);
        log.info("Set   (unique only):            " + asSet);
        log.info("Map   (name → count):           " + asMap);
        log.info("pick List for sequences, Set for membership, Map for keyed lookup");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    static void section(String title) {
        log.info("");
        log.info("========== " + title + " ==========");
    }

    /** Correct key: id + name drive equals and hashCode. */
    static final class UserKey {
        private final int id;
        private String name;

        UserKey(int id, String name) {
            this.id = id;
            this.name = name;
        }

        void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UserKey other)) return false;
            return id == other.id && Objects.equals(name, other.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }

        @Override
        public String toString() {
            return "UserKey{id=" + id + ", name='" + name + "'}";
        }
    }

    /** Broken: equals without hashCode. */
    static final class BrokenKey {
        private final int id;
        private final String name;

        BrokenKey(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BrokenKey other)) return false;
            return id == other.id && Objects.equals(name, other.name);
        }

        @Override
        public String toString() {
            return "BrokenKey{id=" + id + ", name='" + name + "'}";
        }
    }
}
