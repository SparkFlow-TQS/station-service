package tqs.sparkflow.stationservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "route.planning")
public class RoutePlanningConfig {
  private double minBatteryPercentage = 0.2;
  private double maxBatteryPercentage = 0.8;
  private double maxDetourDistance = 20.0;
  private double requestsPerSecond = 10.0;

  public double getMinBatteryPercentage() {
    return minBatteryPercentage;
  }

  public void setMinBatteryPercentage(double minBatteryPercentage) {
    this.minBatteryPercentage = minBatteryPercentage;
  }

  public double getMaxBatteryPercentage() {
    return maxBatteryPercentage;
  }

  public void setMaxBatteryPercentage(double maxBatteryPercentage) {
    this.maxBatteryPercentage = maxBatteryPercentage;
  }

  public double getMaxDetourDistance() {
    return maxDetourDistance;
  }

  public void setMaxDetourDistance(double maxDetourDistance) {
    this.maxDetourDistance = maxDetourDistance;
  }

  public double getRequestsPerSecond() {
    return requestsPerSecond;
  }

  public void setRequestsPerSecond(double requestsPerSecond) {
    this.requestsPerSecond = requestsPerSecond;
  }
}
