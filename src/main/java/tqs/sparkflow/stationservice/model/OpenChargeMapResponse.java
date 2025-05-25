package tqs.sparkflow.stationservice.model;

import java.util.List;

/**
 * Response model for OpenChargeMap API.
 */
public class OpenChargeMapResponse {
  private List<OpenChargeMapStation> stations;

  public List<OpenChargeMapStation> getStations() {
    return stations;
  }

  public void setStations(List<OpenChargeMapStation> stations) {
    this.stations = stations;
  }
}
