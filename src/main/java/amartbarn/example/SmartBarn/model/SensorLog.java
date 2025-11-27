package amartbarn.example.SmartBarn.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sensor_logs")
public class SensorLog {

    @Id
    private String id;

    // Ví dụ: "ESP32_ChickenFarm_01"
    private String deviceId;

    // Nhiệt độ hiện tại (ví dụ: 28.5)
    private Double temperature;

    // Độ ẩm hiện tại (ví dụ: 70.2)
    private Double humidity;

    // Thời gian ghi nhận
    private LocalDateTime timestamp;
}
