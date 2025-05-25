package tqs.sparkflow.stationservice.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tqs.sparkflow.stationservice.model.Station;

/**
 * Repository for managing stations.
 */
@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
  /**
   * Finds stations by city.
   *
   * @param city The city to find stations in
   * @return A list of stations in the given city
   */
  List<Station> findByCity(String city);

  /**
   * Finds a station by its external ID.
   *
   * @param externalId The external ID to find the station by
   * @return An Optional containing the station if found
   */
  Optional<Station> findByExternalId(String externalId);

  /**
   * Searches for stations based on various criteria.
   *
   * @param name the name to search for
   * @param city the city to search in
   * @param country the country to search in
   * @param connectorType the type of connector to search for
   * @return a list of matching stations
   */
  List<Station> findByNameContainingAndCityContainingAndCountryContainingAndConnectorTypeContaining(
      String name, String city, String country, String connectorType);

  /**
   * Finds stations by connector type.
   *
   * @param connectorType The type of connector to search for
   * @return A list of stations with the given connector type
   */
  List<Station> findByConnectorType(String connectorType);
}
