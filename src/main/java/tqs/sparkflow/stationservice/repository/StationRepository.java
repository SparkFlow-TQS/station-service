package tqs.sparkflow.stationservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tqs.sparkflow.stationservice.model.Station;

/** Repository for managing stations. */
@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
  /**
   * Finds stations by city.
   *
   * @param city The city to find stations in
   * @return A list of stations in the given city (limited to 500 results)
   */
  @Query("SELECT s FROM Station s WHERE s.city = :city ORDER BY s.id LIMIT 500")
  List<Station> findByCity(@Param("city") String city);

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
   * @param minChargers the minimum number of chargers
   * @return a list of matching stations
   */
  List<Station> findByNameContainingAndCityContainingAndCountryContainingAndQuantityOfChargersGreaterThanEqual(
      String name, String city, String country, Integer minChargers);

  /**
   * Finds stations by minimum number of chargers.
   *
   * @param minChargers The minimum number of chargers to search for
   * @return A list of stations with at least the given number of chargers
   */
  List<Station> findByQuantityOfChargersGreaterThanEqual(Integer minChargers);

  @Query("SELECT s FROM Station s WHERE "
         + "(:minPower IS NULL OR s.power >= :minPower) AND "
         + "(:maxPower IS NULL OR s.power <= :maxPower) AND "
         + "(:isOperational IS NULL OR s.isOperational = :isOperational) AND "
         + "(:status IS NULL OR s.status = :status) AND "
         + "(:city IS NULL OR s.city = :city) AND "
         + "(:country IS NULL OR s.country = :country) AND "
         + "(:minPrice IS NULL OR s.price >= :minPrice) AND "
         + "(:maxPrice IS NULL OR s.price <= :maxPrice)")
  List<Station> findStationsByFilters(
      @Param("minPower") Integer minPower,
      @Param("maxPower") Integer maxPower,
      @Param("isOperational") Boolean isOperational,
      @Param("status") String status,
      @Param("city") String city,
      @Param("country") String country,
      @Param("minPrice") Double minPrice,
      @Param("maxPrice") Double maxPrice
  );

  @Query(value = "SELECT * FROM stations s WHERE "
         + "(:minPower IS NULL OR s.power >= :minPower) AND "
         + "(:maxPower IS NULL OR s.power <= :maxPower) AND "
         + "(:isOperational IS NULL OR s.is_operational = :isOperational) AND "
         + "(:status IS NULL OR s.status = :status) AND "
         + "(:city IS NULL OR s.city = :city) AND "
         + "(:country IS NULL OR s.country = :country) AND "
         + "(:minPrice IS NULL OR s.price >= :minPrice) AND "
         + "(:maxPrice IS NULL OR s.price <= :maxPrice) AND "
         + "(:latitude IS NULL OR :longitude IS NULL OR :radius IS NULL OR "
         + "ST_Distance_Sphere(point(s.longitude, s.latitude), point(:longitude, :latitude)) <= :radius * 1000) "
         + "ORDER BY s.id LIMIT 500",
         nativeQuery = true)
  List<Station> findStationsByFiltersWithLocation(
      @Param("minPower") Integer minPower,
      @Param("maxPower") Integer maxPower,
      @Param("isOperational") Boolean isOperational,
      @Param("status") String status,
      @Param("city") String city,
      @Param("country") String country,
      @Param("minPrice") Double minPrice,
      @Param("maxPrice") Double maxPrice,
      @Param("latitude") Double latitude,
      @Param("longitude") Double longitude,
      @Param("radius") Integer radius
  );
}
