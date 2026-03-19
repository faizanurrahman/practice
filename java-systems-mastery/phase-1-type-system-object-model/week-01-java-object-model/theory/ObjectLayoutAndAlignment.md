## Object Layout and Alignment (Why Sizes and Offsets Surprise You)

Staff-level takeaway: *do not trust your intuition for object size and field offsets*.
HotSpot uses headers, alignment, compressed pointers, and field reordering to pack objects efficiently.

### 1. Object header components (HotSpot mental model)

An ordinary object typically has:

- **mark word**: identity hash (when computed), GC age, and locking state (biased/lightweight/heavy)
- **class pointer**: points to the class metadata (often compressed)

For arrays, an additional **length** field is included.

### 2. Alignment and padding

Objects are aligned to a word boundary (commonly 8 bytes).
Padding is added so the next object begins at an aligned address.

Implication:

- two fields that "should" add up may end up with extra padding
- adding a long/double field can change the layout of the entire object

### 3. Field ordering and cache locality

HotSpot may reorder fields to reduce padding and improve access patterns.
Even if you do not know exact offsets, you can still reason about:

- which fields are likely to share the same cache lines
- when false sharing can occur (multiple threads mutating different fields on the same line)

### 4. Measuring instead of guessing (JOL)

Use JOL (Java Object Layout) to get concrete numbers for:

- header size under your JVM flags
- reference sizes with compressed OOPs
- total object size and alignment padding

Senior habit:

- always measure on the same JVM/version/flags used in production

