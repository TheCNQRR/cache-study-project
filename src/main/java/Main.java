public class Main {
    public static void main(String[] args) throws InterruptedException {

        CacheWithTTL<String, String> cache = new CacheWithTTL<>(3);

        cache.put("key1", "value1", 5000L);
        System.out.println(cache.get("key1"));

        Thread.sleep(3000);
        System.out.println(cache.get("key1"));

        Thread.sleep(2000);
        System.out.println(cache.get("key1"));

        cache.shutdown();
    }
}
