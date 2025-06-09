#!/bin/bash

# Latitude and longitude bounds for Portugal
min_lat=36.8
max_lat=42.1
min_lon=-9.5
max_lon=-6.2

# Degree steps (~0.1 ≈ 11 km lat, 8-9 km lon)
lat_step=0.1
lon_step=0.1

# Delay in seconds between calls (adjust as needed)
delay=1.5

echo "Starting to populate Portugal..."

for lat in $(seq $min_lat $lat_step $max_lat); do
  for lon in $(seq $min_lon $lon_step $max_lon); do
    echo "→ Populating at lat=$lat, lon=$lon"
    
    response=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST "http://localhost/station/api/v1/openchargemap/populate?latitude=$lat&longitude=$lon&radius=100")
    http_code=$(echo "$response" | grep "HTTP_CODE" | cut -d: -f2)
    body=$(echo "$response" | sed '/HTTP_CODE/d')

    echo "   ↳ Response ($http_code): $body"
    echo ""

    sleep $delay
  done
done

echo "✅ Population complete."
