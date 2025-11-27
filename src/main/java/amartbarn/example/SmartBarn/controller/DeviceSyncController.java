package amartbarn.example.SmartBarn.controller;

import amartbarn.example.SmartBarn.service.DeviceService;
import amartbarn.example.SmartBarn.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/device")
public class DeviceSyncController {

    @Autowired
    private SensorService sensorService;

    @Autowired
    private DeviceService deviceService;

    // API DUY NHẤT CHO ESP32 (Polling)
    // ESP32 gửi: { "temp": 28.5, "hum": 70.0 }
    // Server trả về: { "FAN_SYSTEM": "ON", "MIST_SPRAYER": "OFF", ... }
    @PostMapping("/sync")
    public ResponseEntity<Map<String, String>> syncWithDevice(@RequestBody Map<String, Double> payload) {

        // 1. Lấy dữ liệu từ JSON gửi lên
        Double temp = payload.get("temp");
        Double hum = payload.get("hum");

        // 2. Xử lý Logic (Lưu log + Rule Engine tự động)
        // ID thiết bị ở đây tạm để cứng hoặc ESP32 có thể gửi thêm field "deviceId"
        sensorService.processSensorData("ESP32_MAIN", temp, hum);

        // 3. Lấy danh sách lệnh điều khiển hiện tại (từ DB) để trả về
        Map<String, String> commands = deviceService.getEsp32Commands();

        return ResponseEntity.ok(commands);
    }
}