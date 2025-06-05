package tqs.sparkflow.stationservice.dto;

public class ChargingStation {
    private Long id;
    private String name;
    private double latitude;
    private double longitude;
    private String address;
    private String city;
    private String country;
    private String connectorType;
    private String status;
    private boolean isOperational;

    public ChargingStation() {}

    public ChargingStation(Long id, String name, double latitude, double longitude, 
                          String address, String city, String country,
                          String connectorType, String status, boolean isOperational) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.city = city;
        this.country = country;
        this.connectorType = connectorType;
        this.status = status;
        this.isOperational = isOperational;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getConnectorType() { return connectorType; }
    public void setConnectorType(String connectorType) { this.connectorType = connectorType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isOperational() { return isOperational; }
    public void setOperational(boolean operational) { isOperational = operational; }
} 