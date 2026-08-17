import java.util.Iterator;
import java.util.concurrent.*;

public class CacheWithTTL<K, V> {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentLinkedDeque<K> calls = new ConcurrentLinkedDeque<>();
    private final int maxSize;

    public CacheWithTTL(int maxSize) {
        this.maxSize = maxSize;
        scheduler.scheduleAtFixedRate(this::clean, 5, 10, TimeUnit.SECONDS);
    }

    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();

    public void put(K key, V value, Long ttl) {
        long expiredAt = System.currentTimeMillis() + ttl;

        CacheEntry<V> cacheEntry = new CacheEntry<>();
        cacheEntry.setValue(value);
        cacheEntry.setExpiredAt(expiredAt);

        synchronized (this) {
            calls.remove(key);

            if (cache.size() >= maxSize) {
                K oldestKey = calls.pollFirst();
                if (oldestKey != null) {
                    cache.remove(oldestKey);
                }
            }

            cache.put(key, cacheEntry);
            calls.addLast(key);
        }
    }

    public CacheEntry<V> get(K key) {
        CacheEntry<V> cacheEntry = cache.get(key);
        if (cacheEntry == null) {
            return null;
        }

        if (cacheEntry.getExpiredAt() <= System.currentTimeMillis()) {
            synchronized (this) {
                cache.remove(key);
                calls.remove(key);
            }
            return null;
        }

        synchronized (this) {
            calls.remove(key);
            calls.addLast(key);
        }
        return cacheEntry;
    }

    private void clean() {
        synchronized (this) {
            Iterator<K> iterator = calls.iterator();

            while (iterator.hasNext()) {
                K key = iterator.next();
                CacheEntry<V> entry = cache.get(key);

                if (entry == null || entry.getExpiredAt() <= System.currentTimeMillis()) {
                    cache.remove(key);
                    iterator.remove();
                }
            }
        }
    }

    public void shutdown() {
        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
