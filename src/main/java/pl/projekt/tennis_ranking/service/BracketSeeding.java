package pl.projekt.tennis_ranking.service;

import java.util.ArrayList;
import java.util.List;

public class BracketSeeding {

    // Zwraca listę seedów w kolejności slotów (slot 1..N)
    // np. N=8 -> [1,8,4,5,2,7,3,6]
    public static List<Integer> seededOrder(int n) {
        if ((n & (n - 1)) != 0) throw new IllegalArgumentException("n musi być potęgą 2");
        List<Integer> order = new ArrayList<>();
        order.add(1);
        order.add(2);
        int size = 2;
        while (size < n) {
            size *= 2;
            List<Integer> next = new ArrayList<>();
            for (int seed : order) {
                next.add(seed);
                next.add(size + 1 - seed);
            }
            order = next;
        }
        return order;
    }

    public static int nextPow2(int x) {
        int n = 1;
        while (n < x) n <<= 1;
        return n;
    }
}
