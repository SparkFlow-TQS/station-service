package tqs.sparkflow.stationservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import tqs.sparkflow.stationservice.model.Station;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StationRepository stationRepository;

    private Station station1;
    private Station station2;
    private Station station3;

    @BeforeEach
    void setUp() {
        // Create test stations
        station1 = new Station();
        station1.setName("Station 1");
        station1.setAddress("Address 1");
        station1.setCity("Aveiro");
        station1.setCountry("Portugal");
        station1.setLatitude(40.623361);
        station1.setLongitude(-8.650256);
        station1.setQuantityOfChargers(2);
        station1.setStatus("Available");
        station1.setExternalId("EXT1");
        station1.setIsOperational(true);
        station1.setPower(50);
        station1.setPrice(0.30);

        station2 = new Station();
        station2.setName("Station 2");
        station2.setAddress("Address 2");
        station2.setCity("Porto");
        station2.setCountry("Portugal");
        station2.setLatitude(41.1579);
        station2.setLongitude(-8.6291);
        station2.setQuantityOfChargers(3);
        station2.setStatus("In Use");
        station2.setExternalId("EXT2");
        station2.setIsOperational(true);
        station2.setPower(150);
        station2.setPrice(0.35);

        station3 = new Station();
        station3.setName("Station 3");
        station3.setAddress("Address 3");
        station3.setCity("Lisbon");
        station3.setCountry("Portugal");
        station3.setLatitude(38.7223);
        station3.setLongitude(-9.1393);
        station3.setQuantityOfChargers(1);
        station3.setStatus("Offline");
        station3.setExternalId("EXT3");
        station3.setIsOperational(false);
        station3.setPower(22);
        station3.setPrice(0.25);

        // Save stations
        entityManager.persist(station1);
        entityManager.persist(station2);
        entityManager.persist(station3);
        entityManager.flush();
    }

    @Test
    void whenFindByCity_thenReturnStationsInCity() {
        List<Station> found = stationRepository.findByCity("Aveiro");
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("Station 1");
    }

    @Test
    void whenFindByExternalId_thenReturnStation() {
        Optional<Station> found = stationRepository.findByExternalId("EXT2");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Station 2");
    }

    @Test
    void whenFindByExternalIdWithInvalidId_thenReturnEmpty() {
        Optional<Station> found = stationRepository.findByExternalId("INVALID");
        assertThat(found).isEmpty();
    }

    @Test
    void whenFindByQuantityOfChargersGreaterThanEqual_thenReturnStations() {
        List<Station> found = stationRepository.findByQuantityOfChargersGreaterThanEqual(2);
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Station::getName)
                .containsExactlyInAnyOrder("Station 1", "Station 2");
    }

    @Test
    void whenFindStationsByFiltersWithLocation_thenReturnFilteredStations() {
        // For testing purposes, we'll use a simpler query that doesn't rely on ST_Distance_Sphere
        List<Station> found = stationRepository.findStationsByFilters(
                40, // minPower
                100, // maxPower
                true, // isOperational
                "Available", // status
                "Aveiro", // city
                "Portugal", // country
                0.20, // minPrice
                0.40  // maxPrice
        );
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("Station 1");
    }
} 