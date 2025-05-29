Feature: Station Management
  As a user
  I want to manage charging stations
  So that I can find and use charging points

  Scenario: Get all stations
    Given there are stations in the system
    When I request all stations
    Then I should receive a list of stations

  Scenario: Get station by ID
    Given there is a station with ID "1"
    When I request the station with ID "1"
    Then I should receive the station details 