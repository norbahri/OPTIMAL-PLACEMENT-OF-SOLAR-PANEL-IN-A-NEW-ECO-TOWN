import java.io.*;
import java.util.*;

// Class to represent each house's data
class House {
    int id;             // Unique house ID
    int cost;           // Installation cost in RM
    int sunExposure;    // Energy generation in kWh/month

    public House(int id, int cost, int sunExposure) {
        this.id = id;
        this.cost = cost;
        this.sunExposure = sunExposure;
    }
}

public class SolarOptimization {

    public static void main(String[] args) {

        // Path to the CSV file containing house data
        String csvFile = "C:\\Users\\nazif\\Downloads\\solar_data.csv";

        // Budget for installing solar panels (in RM)
        int budget = 50000;

        // Read house data from the CSV file
        List<House> houses = readHousesFromCSVUsingStream(csvFile);

        // If reading failed or file is empty
        if (houses.isEmpty()) {
            System.out.println("No data found or failed to read CSV.");
            return;
        }

        // Run optimization to select best combination of houses
        optimizeSolarPlacement(houses, budget);
    }

    // Reads data from CSV using FileInputStream
    public static List<House> readHousesFromCSVUsingStream(String fileName) {
        List<House> houses = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(fileName);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            br.readLine(); // Skip the CSV header

            // Read each line of the CSV
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                // Ensure the line has all three required fields
                if (values.length >= 3) {
                    int id = Integer.parseInt(values[0].trim());

                    // Remove non-numeric characters like "RM" or "kWh" before parsing
                    int cost = Integer.parseInt(values[1].trim().replaceAll("[^0-9]", ""));
                    int sunExposure = Integer.parseInt(values[2].trim().replaceAll("[^0-9]", ""));

                    // Add the house to the list
                    houses.add(new House(id, cost, sunExposure));
                }
            }

        } catch (IOException e) {
            System.out.println("Error using FileInputStream: " + e.getMessage());
        }

        return houses;
    }

    // Applies dynamic programming to find the best combination of houses
    public static void optimizeSolarPlacement(List<House> houses, int budget) {
        int n = houses.size();

        // dp[i][b] = max sun exposure using first i houses with budget b
        int[][] dp = new int[n + 1][budget + 1];

        // Build DP table
        for (int i = 1; i <= n; i++) {
            House house = houses.get(i - 1);
            for (int b = 0; b <= budget; b++) {
                if (house.cost <= b) {
                    // Either include the house or not, choose max sun exposure
                    dp[i][b] = Math.max(dp[i - 1][b], dp[i - 1][b - house.cost] + house.sunExposure);
                } else {
                    // Can't include the house — too expensive
                    dp[i][b] = dp[i - 1][b];
                }
            }
        }

        // Trace back to find which houses were selected
        int b = budget;
        List<Integer> selectedHouseIds = new ArrayList<>();
        int totalCost = 0;

        for (int i = n; i > 0; i--) {
            if (dp[i][b] != dp[i - 1][b]) {
                House house = houses.get(i - 1);
                selectedHouseIds.add(house.id);
                b -= house.cost;
                totalCost += house.cost;
            }
        }

        // Reverse the list to maintain original order
        Collections.reverse(selectedHouseIds);

        // Final output
        System.out.println("Selected Building IDs: " + selectedHouseIds);
        System.out.println("Total Energy Generated: " + dp[n][budget] + " kWh/month");
        System.out.println("Total Installation Cost: RM " + totalCost);
    }
}
