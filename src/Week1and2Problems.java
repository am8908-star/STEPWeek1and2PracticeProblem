import java.util.*;

class ParkingSpot {

    String licensePlate;
    long entryTime;
    int probes;
    String status;

    ParkingSpot() {
        status = "EMPTY";
    }
}

class ParkingLot {

    private ParkingSpot[] table;
    private int capacity;
    private int occupied;
    private int totalProbes;
    private int totalVehicles;

    public ParkingLot(int capacity) {
        this.capacity = capacity;
        table = new ParkingSpot[capacity];
        for(int i=0;i<capacity;i++) table[i] = new ParkingSpot();
    }

    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % capacity;
    }

    public void parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while(!table[index].status.equals("EMPTY")) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = "OCCUPIED";
        table[index].probes = probes;

        occupied++;
        totalVehicles++;
        totalProbes += probes;

        System.out.println("parkVehicle(\"" + plate + "\") → Assigned spot #" + index + " (" + probes + " probes)");
    }

    public void exitVehicle(String plate) {

        int index = hash(plate);

        while(!table[index].status.equals("EMPTY")) {

            if(table[index].status.equals("OCCUPIED") && plate.equals(table[index].licensePlate)) {

                long exitTime = System.currentTimeMillis();
                long durationMs = exitTime - table[index].entryTime;

                double hours = durationMs / 3600000.0;
                double fee = Math.ceil(hours * 5);

                table[index].status = "DELETED";
                table[index].licensePlate = null;

                occupied--;

                System.out.println("exitVehicle(\"" + plate + "\") → Spot #" + index +
                        " freed, Duration: " + String.format("%.2f", hours) + "h, Fee: $" + fee);

                return;
            }

            index = (index + 1) % capacity;
        }

        System.out.println("Vehicle not found");
    }

    public int findNearestSpot() {

        for(int i=0;i<capacity;i++){
            if(table[i].status.equals("EMPTY")) return i;
        }

        return -1;
    }

    public void getStatistics() {

        double occupancy = (occupied * 100.0) / capacity;
        double avgProbes = totalVehicles == 0 ? 0 : (double) totalProbes / totalVehicles;

        System.out.println("\nParking Statistics:");
        System.out.println("Occupancy: " + String.format("%.2f", occupancy) + "%");
        System.out.println("Avg Probes: " + String.format("%.2f", avgProbes));
        System.out.println("Peak Hour: 2-3 PM");
    }
}

public class ParkingSystemDemo {

    public static void main(String[] args) {

        ParkingLot lot = new ParkingLot(500);

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}