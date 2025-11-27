package amartbarn.example.SmartBarn.service;

import amartbarn.example.SmartBarn.model.SensorLog;
import amartbarn.example.SmartBarn.repository.SensorLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SensorService {

    @Autowired
    private SensorLogRepository sensorLogRepository;

    @Autowired
    private DeviceService deviceService;

    // Hàm này sẽ được Controller gọi khi ESP32 gửi data lên
    public void processSensorData(String deviceId, Double temp, Double hum) {

        // 1. Lưu vào Database (để vẽ biểu đồ)
        SensorLog log = SensorLog.builder()
                .deviceId(deviceId)
                .temperature(temp)
                .humidity(hum)
                .timestamp(LocalDateTime.now())
                .build();
        sensorLogRepository.save(log);

        // 2. Kích hoạt "Rule Engine" bên DeviceService
        // Để kiểm tra xem có cần bật/tắt quạt không
        deviceService.processEnvironmentRules(temp, hum);
    }

    // Hàm lấy dữ liệu cho Dashboard
    // ...
}