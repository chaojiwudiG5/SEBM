package group5.sebm.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeoFenceUtilsTest {

    private static final double DELTA = 1.0; // 允许误差1米

    @Test
    void testDistance_samePoint_shouldReturnZero() {
        double distance = GeoFenceUtils.distance(116.397128, 39.916527, 116.397128, 39.916527);
        assertEquals(0.0, distance, DELTA);
    }

    @Test
    void testDistance_knownPoints_shouldReturnExpected() {
        // 北京天安门经纬度
        double lon1 = 116.397128;
        double lat1 = 39.916527;
        // 北京故宫博物院经纬度
        double lon2 = 116.403875;
        double lat2 = 39.915446;

        double distance = GeoFenceUtils.distance(lon1, lat1, lon2, lat2);
        // 实际距离约 545 米
        assertEquals(580, distance, 10); // 允许10米误差
    }

    @Test
    void testIsInGeofence_inside_shouldReturnTrue() {
        double centerLon = 116.397128;
        double centerLat = 39.916527;
        double radius = 600; // 600米

        String deviceLon = "116.403875";
        String deviceLat = "39.915446";

        boolean result = GeoFenceUtils.isInGeofence(deviceLon, deviceLat, centerLon, centerLat, radius);
        assertTrue(result);
    }

    @Test
    void testIsInGeofence_outside_shouldReturnFalse() {
        double centerLon = 116.397128;
        double centerLat = 39.916527;
        double radius = 100; // 100米

        String deviceLon = "116.403875";
        String deviceLat = "39.915446";

        boolean result = GeoFenceUtils.isInGeofence(deviceLon, deviceLat, centerLon, centerLat, radius);
        assertFalse(result);
    }

    @Test
    void testIsInGeofence_onBoundary_shouldReturnTrue() {
        double centerLon = 0;
        double centerLat = 0;
        double radius = 1000; // 1000米

        // 约 1000 米经度偏移
        String deviceLon = "0.008983"; // 约 1000米
        String deviceLat = "0";

        boolean result = GeoFenceUtils.isInGeofence(deviceLon, deviceLat, centerLon, centerLat, radius);
        assertTrue(result);
    }

    @Test
    void testIsInGeofence_invalidNumber_shouldThrowException() {
        double centerLon = 0;
        double centerLat = 0;
        double radius = 100;

        assertThrows(NumberFormatException.class, () -> {
            GeoFenceUtils.isInGeofence("invalid", "0", centerLon, centerLat, radius);
        });

        assertThrows(NumberFormatException.class, () -> {
            GeoFenceUtils.isInGeofence("0", "invalid", centerLon, centerLat, radius);
        });
    }
}
