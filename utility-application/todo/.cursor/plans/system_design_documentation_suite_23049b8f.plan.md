---
name: System Design Documentation Suite
overview: "Create a staff/principal-level system design documentation suite at docs/system-design/ with 80+ deep-dive files organized into 12 major sections: introduction, databases, concurrency, event-driven architecture, SQL programming, API design, network protocols, caching, reliability, security, observability, and scaling patterns. Each file will reference the work-platform project where applicable and include future implementation tasks."
todos: []
isProject: false
---

# System Design Documentation Suite

## Location

All files go under `work-platform/docs/system-design/`.

## Directory Structure

```
docs/system-design/
├── 00-introduction/                    # 6 files — from user's sample + extensions
├── 01-databases/
│   ├── relational/                     # 7 files — PostgreSQL, ACID, MVCC, indexing, scaling
│   ├── key-value/                      # 3 files — Redis internals, DynamoDB/consistent hashing
│   ├── document/                       # 2 files — MongoDB architecture, when to use
│   ├── wide-column/                    # 4 files — Cassandra deep dive, data modeling, scaling
│   ├── search-engines/                 # 3 files — Elasticsearch internals, search patterns
│   └── specialized/                    # 4 files — graph, time-series, vector, object storage
├── 02-concurrency/                     # 9 files — JMM, locks, concurrent collections, virtual threads
├── 03-event-driven/
│   ├── fundamentals/                   # 3 files — concepts, patterns (outbox/saga/CQRS)
│   ├── kafka/                          # 5 files — architecture, guarantees, patterns, operations, streams
│   ├── message-brokers/                # 2 files — RabbitMQ, cloud queues
│   └── task-queues/                    # 2 files — patterns, scheduling
├── collaborative editor
```

**Total: ~83 files**

---

## 00-introduction/ (6 files)

Based on the user's detailed sample content, split into focused chapters:

- `01-system-design-mindset.md` — The mental model: forces create need for tools, not the other way around. The system design map (all 9 layers: edge, communication, compute, data, cache, async, reliability, security, observability). When to pick each component category.
- `02-master-interview-flow.md` — The 14-step sequential flow: clarify, requirements, estimate scale, entities/workflows, simplest architecture, sync path, async boundaries, data stores, service boundaries, communication, scaling, reliability, security+observability, deep dive.
- `03-component-selection-framework.md` — Decision framework: when Redis, when Kafka, when BullMQ, when relational DB, when GraphQL, when gRPC, when WebSocket, when SSE, when API Gateway. The "shortest possible cheat-sheet."
- `04-anti-patterns.md` — The 9 anti-patterns: every component, premature microservices, shared DB, long sync chains, Kafka for everything, Redis as source of truth, ignoring idempotency, no read/write patterns, no failure model. Real examples from industry.
- `05-service-boundary-decisions.md` — When to split: bounded context, data ownership, independent scaling, deployment independence, fault isolation. Monolith vs modular monolith vs microservices decision tree. How our work-platform chose modular monolith and why.
- `06-end-to-end-example-food-delivery.md` — The full food delivery walkthrough from the sample: architecture, sync path, async boundaries, data store choices, Kafka vs BullMQ distinction, reliability design, scaling decisions, request flow diagrams.

---

## 01-databases/ (23 files across 6 subfolders)

### relational/ (7 files)

- `01-relational-fundamentals.md` — Relational model, normalization (1NF-BCNF), denormalization trade-offs, constraints (PK, FK, unique, check), NULL semantics
- `02-acid-deep-dive.md` — Atomicity (undo log/WAL), Consistency (constraints + application invariants), Isolation (read phenomena: dirty read, non-repeatable read, phantom; isolation levels: READ UNCOMMITTED through SERIALIZABLE; MVCC implementation), Durability (WAL, fsync, checkpoints). How PostgreSQL implements each property.
- `03-postgresql-internals.md` — Architecture: postmaster, backend processes, shared buffers, WAL writer, checkpointer, autovacuum. MVCC: xmin/xmax/cmin/cmax, transaction snapshots, visibility rules. TOAST for large values. Tablespaces. System catalogs. **Reference: work-platform uses PostgreSQL 16.**
- `04-indexing-deep-dive.md` — B-tree (structure, splits, fill factor), Hash index, GIN (full-text, JSONB), GiST (geometric, range), BRIN (block range, time-series), SP-GiST. Partial indexes, covering indexes (INCLUDE), expression indexes, multi-column index ordering. Index-only scans. When NOT to index. **Reference: work-platform V003 creates indexes on tasks.project_id, tasks.path.**
- `05-query-planning-and-optimization.md` — EXPLAIN/EXPLAIN ANALYZE output reading, cost model (seq_page_cost, random_page_cost), statistics (pg_stats, histogram bounds), join strategies (nested loop, hash join, merge join), parallel query, JIT compilation. Common anti-patterns.
- `06-transactions-locking-concurrency.md` — PostgreSQL locking: row-level (FOR UPDATE, FOR SHARE, FOR KEY SHARE), table-level (ACCESS SHARE through ACCESS EXCLUSIVE), advisory locks. Deadlock detection. Optimistic locking (@Version in JPA) vs pessimistic locking. Connection-level vs statement-level isolation. Serializable snapshot isolation. **Reference: work-platform uses @Version on tasks for optimistic locking.**
- `07-scaling-relational.md` — Streaming replication (sync/async), logical replication, read replicas with Spring @Transactional(readOnly=true) routing. Connection pooling (PgBouncer, HikariCP). Table partitioning (range, list, hash). Sharding strategies (application-level, Citus). Vertical scaling limits. **Reference: work-platform HikariCP pool, scaling path in 17-scalability doc.**

### key-value/ (3 files)

- `01-redis-internals.md` — Single-threaded event loop (epoll/kqueue), data structures in memory (SDS, ziplist, skiplist, intset, hashtable), persistence (RDB snapshots, AOF, hybrid), replication (PSYNC, partial resync), Redis Cluster (hash slots, gossip protocol, MOVED/ASK redirects), Sentinel for HA. Memory management and eviction policies. **Reference: work-platform uses Redis 7 for cache, locks, pub/sub, presence.**
- `02-redis-advanced-patterns.md` — Distributed locks (Redlock debate, single-instance SETNX), rate limiting (sliding window, token bucket with Lua scripts), pub/sub vs Streams, sorted set leaderboards, HyperLogLog for cardinality, Bloom filters, Redis Streams for event sourcing. **Reference: work-platform DistributedLockService, RedisPubSubBroadcastAdapter, sorted set presence.**
- `03-dynamo-style-stores.md` — DynamoDB / Dynamo paper: consistent hashing with virtual nodes, vector clocks for conflict resolution, sloppy quorum (W+R>N), hinted handoff, Merkle trees for anti-entropy, read repair. Single-table design for DynamoDB. GSI/LSI. Capacity modes.

### document/ (2 files)

- `01-mongodb-architecture.md` — BSON format, WiredTiger storage engine (B-tree + LSM hybrid), document validation, indexes (single, compound, multikey, text, geospatial, TTL), aggregation pipeline, change streams, sharding (shard key selection, chunks, balancer), replica sets (elections, oplog, read preferences, write concerns).
- `02-document-modeling-patterns.md` — Embedding vs referencing, bucket pattern, outlier pattern, subset pattern, computed pattern, schema versioning pattern. When documents beat relational. Anti-patterns.

### wide-column/ (4 files)

- `01-cassandra-architecture.md` — Ring topology, consistent hashing with vnodes, gossip protocol (Phi accrual failure detector), coordinator node, partitioner (Murmur3), replication strategies (SimpleStrategy, NetworkTopologyStrategy), tunable consistency (ONE, QUORUM, LOCAL_QUORUM, ALL, ANY). Hinted handoff, read repair, anti-entropy repair.
- `02-cassandra-storage-engine.md` — Write path: memtable, commit log, SSTable flush. Read path: memtable check, bloom filter, partition summary, partition index, SSTable. Compaction strategies (SizeTiered, Leveled, TimeWindow). Tombstones and gc_grace_seconds. Why deletes are expensive.
- `03-cassandra-data-modeling.md` — Query-first design, partition key selection, clustering columns for sort order, denormalization as a feature, materialized views, secondary indexes (when to avoid), SASI indexes, SAI indexes. Common patterns: time-series, messaging, activity feeds. **Reference: work-platform plans Cassandra for future 1v1 chat.**
- `04-cassandra-operations.md` — Nodetool commands, repair scheduling, compaction monitoring, read/write latency percentiles, heap tuning, gc pauses, multi-DC setup, rack awareness, rolling upgrades, streaming during scale-out.

### search-engines/ (3 files)

- `01-elasticsearch-internals.md` — Lucene foundation: inverted index (term dictionary, postings list), segments (immutable, merge), doc values for aggregations. ES layer: shards (Lucene instances), replicas, routing, cluster state, master election. Write path: refresh (near-realtime), flush, translog. Search path: query then fetch. **Reference: work-platform uses ES 8.13 for task/project/user search.**
- `02-search-patterns.md` — Full-text search (analyzers, tokenizers, filters), autocomplete (edge-ngram, completion suggester), fuzzy search, highlighting, faceted search (aggregations: terms, range, histogram), relevance scoring (TF-IDF, BM25), function_score, boosting, search-as-you-type. **Reference: work-platform SearchIndexInitializer, autocomplete endpoint.**
- `03-elasticsearch-operations.md` — Index lifecycle management (ILM), index templates, aliases for zero-downtime reindexing, snapshot/restore, cluster health monitoring, shard sizing (aim for 10-50GB), split-brain prevention, cross-cluster search, hot-warm-cold architecture.

### specialized/ (4 files)

- `01-graph-databases.md` — Property graph model (Neo4j), Cypher query language, index-free adjacency, when graphs beat relational (social networks, recommendation engines, fraud detection, knowledge graphs), graph algorithms (shortest path, PageRank, community detection).
- `02-time-series-databases.md` — Time-series characteristics (append-heavy, time-ordered, downsampling), InfluxDB (TSM engine, retention policies, continuous queries), TimescaleDB (PostgreSQL extension, hypertables, automatic partitioning), Prometheus (pull model, PromQL). When to use vs regular DB with time column.
- `03-vector-databases.md` — Embeddings, similarity search (cosine, euclidean, dot product), ANN algorithms (HNSW, IVF, PQ), Pinecone/Milvus/pgvector/Weaviate, RAG pattern, semantic search vs keyword search. When vector search adds value.
- `04-object-storage.md` — S3 model (buckets, objects, keys), consistency model (strong read-after-write), storage classes, lifecycle policies, multipart upload, pre-signed URLs, cross-region replication. MinIO as S3-compatible local alternative. **Reference: work-platform uses MinIO with Strategy pattern for storage switching.**

---

## 02-concurrency/ (9 files)

- `01-java-memory-model.md` — Happens-before relationships, instruction reordering (compiler, JIT, CPU), memory barriers, visibility guarantees, the volatile keyword (prevents reordering, ensures visibility, NOT atomicity for compound operations), final field semantics, safe publication.
- `02-synchronized-and-intrinsic-locks.md` — Monitor concept, synchronized (method, block, static), reentrancy, memory visibility effects of synchronization, wait/notify/notifyAll, spurious wakeups, why synchronized is the foundation.
- `03-explicit-locks.md` — ReentrantLock (fairness, tryLock, lockInterruptibly), ReadWriteLock (concurrent reads, exclusive writes), StampedLock (optimistic reads), Condition objects, lock ordering to prevent deadlocks, try-finally pattern.
- `04-atomic-operations-and-cas.md` — CAS (Compare-And-Swap) instruction, AtomicInteger/Long/Reference, AtomicFieldUpdater, VarHandle, ABA problem, lock-free vs wait-free algorithms, LongAdder/LongAccumulator for high-contention counters.
- `05-concurrent-collections.md` — ConcurrentHashMap (segment locking, then Node+CAS in Java 8+, computeIfAbsent), CopyOnWriteArrayList/Set (snapshot iteration), BlockingQueue (ArrayBlockingQueue, LinkedBlockingQueue, SynchronousQueue, PriorityBlockingQueue), ConcurrentSkipListMap, TransferQueue.
- `06-executors-and-thread-pools.md` — Executor framework, ThreadPoolExecutor (core/max size, queue strategies, rejection policies, keep-alive), ForkJoinPool (work-stealing, RecursiveTask/Action), ScheduledExecutorService, CompletableFuture (thenApply, thenCompose, allOf, exceptionally), structured concurrency (Java 21 preview).
- `07-virtual-threads-and-project-loom.md` — Platform threads vs virtual threads, carrier threads, thread pinning (synchronized blocks on virtual threads), when virtual threads help (I/O-bound work), when they don't help (CPU-bound), migration strategy, Spring Boot integration (spring.threads.virtual.enabled=true). **Reference: work-platform is virtual-thread-ready but not yet enabled.**
- `08-spring-boot-concurrency-model.md` — Tomcat thread pool, request-per-thread model, @Async and custom TaskExecutor, @Scheduled and TaskScheduler (single thread default!), @EventListener (sync by default), @TransactionalEventListener (after commit), WebFlux reactive model (when to use), thread-local pitfalls (SecurityContext, MDC, @Transactional proxy). **Reference: work-platform 14-concurrency doc.**
- `09-distributed-concurrency.md` — Distributed locks (Redis SETNX, ZooKeeper recipes, etcd), leader election, fencing tokens, split-brain problem, consensus algorithms overview (Raft, Paxos), distributed coordination (ZooKeeper, etcd), clock problems (wall clock vs logical clocks, Lamport timestamps, vector clocks). **Reference: work-platform DistributedLockService.**

---

## 03-event-driven/ (12 files across 4 subfolders)

### fundamentals/ (3 files)

- `01-event-driven-concepts.md` — Events vs commands vs queries, event notification vs event-carried state transfer, event sourcing (store events as source of truth, rebuild state by replay), CQRS (separate read/write models), event-driven vs request-driven architecture, eventual consistency.
- `02-messaging-patterns.md` — Point-to-point vs publish-subscribe, competing consumers, message ordering, exactly-once vs at-least-once vs at-most-once, dead letter queues, poison messages, message deduplication, correlation IDs, content-based routing.
- `03-integration-patterns.md` — Outbox pattern (write event + business data in same TX), saga pattern (choreography vs orchestration), two-phase commit (and why to avoid it), change data capture (CDC with Debezium), transactional messaging, idempotent consumers, event schema evolution (backward/forward compatibility). **Reference: work-platform uses outbox pattern, OutboxToKafkaBridge.**

### kafka/ (5 files)

- `01-kafka-architecture.md` — Brokers, topics, partitions (ordered append-only log), consumer groups, offsets, ISR (in-sync replicas), leader election, partition assignment strategies (range, round-robin, sticky, cooperative-sticky). KRaft mode (Raft-based metadata management, no ZooKeeper). **Reference: work-platform uses Kafka 3.7 KRaft.**
- `02-kafka-producer-deep-dive.md` — Producer internals: batching (linger.ms, batch.size), compression (snappy, lz4, zstd), partitioning (key hashing, custom partitioner), acks (0, 1, all), idempotent producer
