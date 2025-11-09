public class Watcher // stores information about a single watcher.
{
    private String name;
    private double latitude;
    private double longitude;
    
    public Watcher(String name, double latitude, double longitude) // constructor
    {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    // getters
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}