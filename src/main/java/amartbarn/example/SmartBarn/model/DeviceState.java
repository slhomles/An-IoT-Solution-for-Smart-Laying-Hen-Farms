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
@Document(collection = "device_states")
public class DeviceState {

    @Id
    // ID này nên đặt tên cố định theo code logic.
    // Ví dụ: "FAN_MAIN", "MIST_SYSTEM", "LIGHT_SYSTEM"
    private String id;

    // Tên hiển thị ra giao diện (VD: "Quạt làm mát chính")
    private String displayName;

    // Trạng thái hiện tại (ON/OFF)
    private DeviceStatus currentStatus;

    // Chế độ hoạt động (AUTO/MANUAL)
    private DeviceMode mode;

    // Thời gian cập nhật trạng thái cuối cùng
    private LocalDateTime lastUpdated;
}
