package std;

public class Random {
    static java.util.Random r = new java.util.Random(0);

    public void initRandom(long seed) {
        r = new java.util.Random(seed);
    }

    public long longRandom() {
        return r.nextLong();
    }
}