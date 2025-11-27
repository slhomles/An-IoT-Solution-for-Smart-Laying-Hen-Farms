package amartbarn.example.SmartBarn.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "system_settings")
public class SystemSetting {

    @Id
    // Thường chỉ có 1 document duy nhất, ID có thể để là "DEFAULT_SETTING"
    private String id;

    // Ngưỡng nhiệt độ bật quạt (VD: 28.0)
    private Double maxTemperatureThreshold;

    // Ngưỡng độ ẩm bật hút ẩm (VD: 75.0)
    private Double maxHumidityThreshold;

    // Giờ bắt đầu bình minh (VD: "05:00") [cite: 170]
    private String sunriseTime;

    // Giờ bắt đầu hoàng hôn (VD: "20:00") [cite: 170]
    private String sunsetTime;

    // Thời gian chạy mô phỏng ánh sáng (phút) - VD: 30 phút
    private Integer simulationDurationMinutes;
}