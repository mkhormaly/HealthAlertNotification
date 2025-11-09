import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class HealthAlertNotification 
{
    // Data structures
    private static LinkedPositionalList<Watcher> watcherList = new LinkedPositionalList<>();
    private static LinkedQueue<Incident> incidentQueue = new LinkedQueue<>();
    private static LinkedPositionalList<Incident> severityList = new LinkedPositionalList<>();
    private static boolean all = false;
    public static void main(String[] args) 
    {
        // input arguments from terminal (with or without --all)
        String watcherFileName = null;
        String healthFileName = null;
        if (args.length == 2) 
        {
            watcherFileName = args[0];
            healthFileName = args[1];
        } 
        else if (args.length == 3 && args[0].equals("--all")) 
        {
            all = true;
            watcherFileName = args[1];
            healthFileName = args[2];
        } 
        else 
        {
            System.err.println("ERROR");
            System.exit(1);
        }
        // files and Scanner
        Scanner watcherScanner = null;
        Scanner healthScanner = null;
        try 
        {
            watcherScanner = new Scanner(new File(watcherFileName));
            healthScanner = new Scanner(new File(healthFileName));
        } 
        catch (FileNotFoundException e) 
        {
            System.err.println("Could not open input file. " + e.getMessage());
            System.exit(1);
        }
        // read data from input files for main loop
        String[] nextWatcher = readNextLine(watcherScanner);
        String[] nextHealth = readNextLine(healthScanner);
        int nextWatcherTime = Integer.MAX_VALUE;
        if (nextWatcher != null) 
        {
            nextWatcherTime = Integer.parseInt(nextWatcher[0]);
        }
        int nextHealthTime = Integer.MAX_VALUE;
        if (nextHealth != null)
        {
            nextHealthTime = Integer.parseInt(nextHealth[0]);
        }
        int currentTime = 0;
        // simulation loop
        while (nextWatcherTime != Integer.MAX_VALUE || nextHealthTime != Integer.MAX_VALUE) 
        {
            int timeToProcess = Math.min(nextWatcherTime, nextHealthTime);
            // advance time from currentTime up to timeToProcess, expiring incidents hour by hour
            while (currentTime < timeToProcess) 
            {
                processIncidentExpiry(currentTime);
                currentTime++;
            }
            // when currentTime == timeToProcess, process all events
            // begin with expire incidents at the current time
            processIncidentExpiry(currentTime);
            // then start by processing Watchers
            while (nextWatcherTime == currentTime) 
            {
                processWatcherEvent(nextWatcher);
                nextWatcher = readNextLine(watcherScanner);
                nextWatcherTime =  Integer.MAX_VALUE;
                if (nextWatcher != null) 
                {
                    nextWatcherTime = Integer.parseInt(nextWatcher[0]);
                }
            }
            // continue with processing Health incidents
            while (nextHealthTime == currentTime) 
            {
                processHealthEvent(nextHealth);
                nextHealth = readNextLine(healthScanner);
                nextHealthTime = Integer.MAX_VALUE;
                if (nextHealth != null)
                {
                    nextHealthTime = Integer.parseInt(nextHealth[0]);
                }
            }
        }
        watcherScanner.close();
        healthScanner.close();
    }
    private static String[] readNextLine(Scanner scanner) 
    {
        if (scanner.hasNextLine())
        {
            return scanner.nextLine().split(" ");
        }
        return null;
    }
    /*
    checks the incidentQueue for removeing any incidents that are 6 or more hours old
    removes them from the severityList using their stored position
  */
    private static void processIncidentExpiry(int currentTime) 
    {
        while (!incidentQueue.isEmpty() && incidentQueue.first().getTime() <= currentTime - 6) 
        {
            Incident oldIncident = incidentQueue.dequeue();
            Position<Incident> pos = oldIncident.getPositionInSeverityList();
            severityList.remove(pos);
        }
    }
    private static void processWatcherEvent(String[] parts) 
    {
        String command = parts[1];
        if (command.equals("add")) 
        {
            double lat = Double.parseDouble(parts[2]);
            double lon = Double.parseDouble(parts[3]);
            String name = parts[4];
            watcherList.addLast(new Watcher(name, lat, lon));
            System.out.println(name + " is added to the watcher-list");
        }
        else if (command.equals("delete")) 
        {
            String nameToDelete = parts[2];
            Position<Watcher> p = watcherList.first();
            boolean found = false;
            while (p != null) 
            {
                if (p.getElement().getName().equals(nameToDelete)) 
                {
                    watcherList.remove(p);
                    System.out.println(nameToDelete + " is removed from the watcher-list");
                    found = true;
                    break;
                }
                p = watcherList.after(p);
            }
        }
        else if (command.equals("query-highest")) 
        {
            if (severityList.isEmpty()) 
            {
                System.out.println("No records");
            } 
            else 
            {
                Incident highest = severityList.first().getElement();
                System.out.println("Most severe health incident in past 6 hours:");
                System.out.println("(Disease: " + highest.getDisease() + ") Severity: " + highest.getSeverity() + " at " + highest.getLocation());
            }
        }
        else if (command.equals("query-disease")) 
        {
            String disease = parts[2];
            for (Incident inc : severityList) 
            {
                if (inc.getDisease().equals(disease)) 
                {
                    System.out.println("(Disease: " + inc.getDisease() + ") Severity: " + inc.getSeverity() + " at " + inc.getLocation());
                }
            }
        }
        else if (command.equals("query-region")) 
        {
            double qLat = Double.parseDouble(parts[2]);
            double qLon = Double.parseDouble(parts[3]);
            double radius = Double.parseDouble(parts[4]);
            for (Incident inc : severityList) 
            {
                double dist = calculateDistance(qLat, qLon, inc.getLatitude(), inc.getLongitude());
                if (dist < radius) 
                {
                    System.out.println("(Disease: " + inc.getDisease() + ") Severity: " + inc.getSeverity() + " at " + inc.getLocation());
                }
            }
        }
    }
    private static void processHealthEvent(String[] parts) 
    {
        int time = Integer.parseInt(parts[0]);
        String disease = parts[1];
        double lat = Double.parseDouble(parts[2]);
        double lon = Double.parseDouble(parts[3]);
        String location = parts[4];
        double infectionRate = Double.parseDouble(parts[5]);
        int populationAffected = Integer.parseInt(parts[6]);
        double severity = Double.parseDouble(parts[7]);
        String reportingAgency = parts[8];
        Incident newIncident = new Incident(time, disease, lat, lon, location, infectionRate, populationAffected, severity, reportingAgency);
        if (all) // handle --all argument
        {
            System.out.println("(Disease: " + newIncident.getDisease() + ") at " + newIncident.getLocation() + " is inserted into incident-queue");
        }
        // add to severityList (descending order)
        Position<Incident> insertedPos = null;
        if (severityList.isEmpty()) 
        {
            insertedPos = severityList.addFirst(newIncident);
        } else {
            Position<Incident> p = severityList.first();
            while (p != null) 
            {
                if (newIncident.getSeverity() > p.getElement().getSeverity()) 
                {
                    insertedPos = severityList.addBefore(p, newIncident);
                    break;
                }
                p = severityList.after(p);
            }
            if (insertedPos == null) 
            {
                insertedPos = severityList.addLast(newIncident);
            }
        }
        newIncident.setPositionInSeverityList(insertedPos); // store the position back into the incident object
        incidentQueue.enqueue(newIncident); // add to incidentQueue
        // for watcher list
        for (Watcher w : watcherList) 
        {
            double dist = calculateDistance(w.getLatitude(), w.getLongitude(), newIncident.getLatitude(), newIncident.getLongitude());
            if (dist < 2 * newIncident.getSeverity()) 
            {
                System.out.println("(Disease: " + newIncident.getDisease() + ") at " + newIncident.getLocation() + " is close to " + w.getName());
            }
        }
    }
    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) 
    {
        return Math.sqrt(Math.pow(lat1 - lat2, 2) + Math.pow(lon1 - lon2, 2));
    }
}