# Collections and Map (Java)

> Easy mental model: pick the structure that matches how you store and look up data.

## Big picture

```
Iterable
  └── Collection          // a group of elements
        ├── List          // ordered, allows duplicates, index access
        ├── Set           // unique elements
        └── Queue         // typically FIFO / priority

Map                       // key → value (NOT a Collection)
```

- `Collection` = many values.
- `Map` = many key/value pairs.
- Prefer interface types in APIs (`List`, `Set`, `Map`) and pick the concrete class for behavior.

---

## Collection

### List — ordered, duplicates OK

| Type | Good for | Avoid when |
|------|----------|------------|
| `ArrayList` | Default choice: random access, append, iterate | Frequent insert/remove in the middle of huge lists |
| `LinkedList` | Rare; deque-style add/remove at ends | Indexed get/set (slow) |
| `CopyOnWriteArrayList` | Tiny, read-mostly listener lists | Frequent writes |

```java
List<String> names = new ArrayList<>();
names.add("Ada");
names.add("Ada");           // duplicates allowed
names.get(0);               // index access
names.contains("Ada");      // linear scan
```

### Set — unique elements

| Type | Uniqueness by | Order |
|------|---------------|-------|
| `HashSet` | `equals` + `hashCode` | None |
| `LinkedHashSet` | `equals` + `hashCode` | Insertion order |
| `TreeSet` | `compareTo` / `Comparator` | Sorted |

```java
Set<String> tags = new HashSet<>();
tags.add("java");
tags.add("java");           // ignored — already present
```

**Gotcha:** `TreeSet` uniqueness is `compare == 0`, not `equals`. If the comparator ignores a field, distinct objects can vanish.

### Queue / Deque — processing order

| Type | Use |
|------|-----|
| `ArrayDeque` | Fast stack/queue (prefer over `Stack` / `LinkedList`) |
| `PriorityQueue` | Always poll the “smallest” / highest priority |
| `LinkedBlockingQueue` | Producer/consumer between threads |

```java
Deque<String> q = new ArrayDeque<>();
q.offer("first");
q.offer("second");
q.poll();                   // "first"
```

### Common Collection operations

| Method | Meaning |
|--------|---------|
| `add(e)` | Insert |
| `remove(e)` / `remove(index)` | Delete |
| `contains(e)` | Membership |
| `size()` / `isEmpty()` | Count |
| `iterator()` / for-each | Walk elements |
| `clear()` | Empty |

---

## Map

A `Map` stores **key → value**. Keys must be unique for that map’s equality rules.

| Type | Lookup | Order | Notes |
|------|--------|-------|-------|
| `HashMap` | Default, fast | None | Needs correct `equals`/`hashCode` on keys |
| `LinkedHashMap` | Fast | Insertion (or access order) | Access-order can build a simple LRU |
| `TreeMap` | `O(log n)` | Sorted by key | Range queries (`subMap`, `headMap`) |
| `ConcurrentHashMap` | Thread-safe ops | None | Safe per-key ops; not multi-key transactions |
| `EnumMap` | Very fast | Enum order | Keys must be one enum type |

```java
Map<String, Integer> ages = new HashMap<>();
ages.put("Ada", 36);
ages.get("Ada");            // 36
ages.getOrDefault("Bob", 0);
ages.containsKey("Ada");
ages.keySet();              // Set of keys
ages.values();              // Collection of values
ages.entrySet();            // Set of Map.Entry
```

### Useful Map APIs

| Method | Use |
|--------|-----|
| `putIfAbsent(k, v)` | Insert only if missing |
| `computeIfAbsent(k, fn)` | Build value lazily once |
| `merge(k, v, remappingFn)` | Upsert / combine |
| `replace(k, old, new)` | Conditional update |
| `getOrDefault(k, def)` | Avoid null checks |

---

## Choosing quickly

```
Need index + order + duplicates?     → List  (ArrayList)
Need uniqueness only?                → Set   (HashSet)
Need uniqueness + insertion order?   → LinkedHashSet
Need sorted unique / range?          → TreeSet / TreeMap
Need key → value?                    → Map   (HashMap)
Need sorted keys / ranges?           → TreeMap
Need thread-safe single-key ops?     → ConcurrentHashMap
```

---

## Keys, equals, and hashCode

For `HashMap` / `HashSet`:

1. `a.equals(b)` ⇒ `a.hashCode() == b.hashCode()`
2. Hash picks the bucket; `equals` finds the entry
3. Do **not** mutate fields used by `equals`/`hashCode` after the object is in a hash structure

For `TreeMap` / `TreeSet`:

- Ordering defines uniqueness (`compare == 0`)
- Ordering should stay consistent with `equals` when possible

Prefer immutable keys (`String`, records, IDs).

---

## Null and immutability

| API | Null keys/values |
|-----|------------------|
| `HashMap` | One null key; null values OK |
| `Hashtable` / `ConcurrentHashMap` | No nulls |
| `TreeMap` | No null key (unless custom comparator allows) |
| `List.of` / `Set.of` / `Map.of` | No nulls; immutable |

```java
List<String> fixed = List.of("a", "b");     // immutable
List<String> view  = Collections.unmodifiableList(mutable);
// view cannot be changed, but mutable still can — view reflects it
```

---

## Common mistakes

1. Using `LinkedList` “because inserts are O(1)” for random middle edits — usually worse than `ArrayList`.
2. Forgetting `hashCode` when overriding `equals`.
3. Mutating a map key after `put`.
4. Assuming `ConcurrentHashMap` makes multi-step logic atomic (`check-then-act` still races).
5. Returning a live mutable collection from an API when callers should not mutate internals.
6. Using `TreeSet` uniqueness as if it were `equals`.

---

## Tiny cheat sheet

```java
// List
List<String> list = new ArrayList<>(List.of("a", "b"));

// Set
Set<String> set = new HashSet<>(list);

// Map
Map<String, Integer> map = new HashMap<>();
map.put("a", 1);
map.computeIfAbsent("b", k -> 0);

// Iterate map
for (var e : map.entrySet()) {
    // e.getKey(), e.getValue()
}

// Prefer modern iteration
map.forEach((k, v) -> { /* ... */ });
```

See also: `Collections-and-Streams.md` for senior / interview depth, and `scratch/CollectionsMapDemo.java` for a runnable walkthrough.
