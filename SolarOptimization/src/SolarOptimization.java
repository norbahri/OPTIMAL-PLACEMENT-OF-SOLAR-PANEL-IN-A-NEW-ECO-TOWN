import java.io.*;
import java.util.*;

class House {
    int id;
    int cost;
    int sunExposure;

    public House(int id, int cost, int sunExposure) {
        this.id = id;
        this.cost = cost;
        this.sunExposure = sunExposure;
    }
}

public class SolarOptimization {

    public static void main(String[] args) {
        String csvFile = "C:\\Users\\nazif\\Downloads\\solar_data(100).csv";
        int budget = 50000;

        List<House> houses = readHousesFromCSV(csvFile);

        if (houses.isEmpty()) {
            System.out.println("No data found or failed to read CSV.");
            return;
        }

        System.out.println("\n=== Dynamic Programming ===");
        long dpStart = System.currentTimeMillis();
        runDynamicProgramming(houses, budget);
        long dpEnd = System.currentTimeMillis();
        System.out.println("DP Execution Time: " + (dpEnd - dpStart) + " ms");

        System.out.println("\n=== Greedy Algorithm ===");
        long greedyStart = System.currentTimeMillis();
        runGreedy(houses, budget);
        long greedyEnd = System.currentTimeMillis();
        System.out.println("Greedy Execution Time: " + (greedyEnd - greedyStart) + " ms");

        System.out.println("\n=== Brute Force Algorithm (Sampled 20 Houses) ===");
        List<House> sampledHouses = getRandomSample(houses, 20);
        long bruteStart = System.currentTimeMillis();
        runBruteForce(sampledHouses, budget);
        long bruteEnd = System.currentTimeMillis();
        System.out.println("Brute Force Execution Time: " + (bruteEnd - bruteStart) + " ms");


    }

    public static List<House> readHousesFromCSV(String fileName) {
        List<House> houses = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length >= 3) {
                    int id = Integer.parseInt(values[0].trim());
                    int cost = Integer.parseInt(values[1].trim().replaceAll("[^0-9]", ""));
                    int sun = Integer.parseInt(values[2].trim().replaceAll("[^0-9]", ""));
                    houses.add(new House(id, cost, sun));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV: " + e.getMessage());
        }
        return houses;
    }

    public static void runDynamicProgramming(List<House> houses, int budget) {
        int n = houses.size();
        int[][] dp = new int[n + 1][budget + 1];

        for (int i = 1; i <= n; i++) {
            House h = houses.get(i - 1);
            for (int b = 0; b <= budget; b++) {
                if (h.cost <= b) {
                    dp[i][b] = Math.max(dp[i - 1][b], dp[i - 1][b - h.cost] + h.sunExposure);
                } else {
                    dp[i][b] = dp[i - 1][b];
                }
            }
        }

        List<Integer> selected = new ArrayList<>();
        int b = budget, totalCost = 0;
        for (int i = n; i > 0; i--) {
            if (dp[i][b] != dp[i - 1][b]) {
                House h = houses.get(i - 1);
                selected.add(h.id);
                b -= h.cost;
                totalCost += h.cost;
            }
        }
        Collections.reverse(selected);
        System.out.println("Selected House IDs: " + selected);
        System.out.println("Total Energy: " + dp[n][budget] + " kWh");
        System.out.println("Total Cost: RM " + totalCost);
    }

    public static void runGreedy(List<House> houses, int budget) {
        houses.sort((a, b) -> Double.compare((double) b.sunExposure / b.cost, (double) a.sunExposure / a.cost));
        int totalCost = 0, totalEnergy = 0;
        List<Integer> selected = new ArrayList<>();

        for (House h : houses) {
            if (totalCost + h.cost <= budget) {
                selected.add(h.id);
                totalCost += h.cost;
                totalEnergy += h.sunExposure;
            }
        }

        System.out.println("Selected House IDs: " + selected);
        System.out.println("Total Energy: " + totalEnergy + " kWh");
        System.out.println("Total Cost: RM " + totalCost);
    }

    public static void runBruteForce(List<House> houses, int budget) {
        int n = houses.size();
        int bestEnergy = 0;
        int bestCost = 0;
        List<Integer> bestCombo = new ArrayList<>();
        int totalCombos = 1 << n;

        for (int mask = 0; mask < totalCombos; mask++) {
            int sumCost = 0, sumEnergy = 0;
            List<Integer> combo = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    House h = houses.get(i);
                    sumCost += h.cost;
                    sumEnergy += h.sunExposure;
                    combo.add(h.id);
                }
            }

            if (sumCost <= budget && sumEnergy > bestEnergy) {
                bestEnergy = sumEnergy;
                bestCost = sumCost;
                bestCombo = new ArrayList<>(combo);
            }
        }

        System.out.println("Selected House IDs: " + bestCombo);
        System.out.println("Total Energy: " + bestEnergy + " kWh");
        System.out.println("Total Cost: RM " + bestCost);
      
    }

    public static List<House> getRandomSample(List<House> original, int sampleSize) {
        Collections.shuffle(original);
        return new ArrayList<>(original.subList(0, Math.min(sampleSize, original.size())));
    }
}
