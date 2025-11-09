public class Incident // stores information about a single health incident.
{
    private int time;
    private String disease;
    private double latitude;
    private double longitude;
    private String location;
    private double infectionRate;
    private int populationAffected;
    private double severity;
    private String reportingAgency;
    private Position<Incident> positionInSeverityList; // reference to its own Position in the severity-ordered list (efficient removal)
    // constuctor
    public Incident(int time, String disease, double latitude, double longitude, String location, double infectionRate, int populationAffected, double severity, String reportingAgency) 
    {
        this.time = time;
        this.disease = disease;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.infectionRate = infectionRate;
        this.populationAffected = populationAffected;
        this.severity = severity;
        this.reportingAgency = reportingAgency;
        this.positionInSeverityList = null;
    }
    // getters
    public int getTime() { return time; }
    public String getDisease() { return disease; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getLocation() { return location; }
    public double getSeverity() { return severity; }
    public Position<Incident> getPositionInSeverityList() { return positionInSeverityList; }
    // setter
    public void setPositionInSeverityList(Position<Incident> positionInSeverityList) { this.positionInSeverityList = positionInSeverityList; }
}