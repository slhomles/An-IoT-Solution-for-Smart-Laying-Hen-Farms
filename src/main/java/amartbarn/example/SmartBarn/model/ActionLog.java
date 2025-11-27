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
@Document(collection = "action_logs")
public class ActionLog {

    @Id
    private String id;

    // Tên thiết bị bị tác động (VD: "FAN_SYSTEM", "MIST_SPRAYER")
    private String deviceId;

    // Hành động (VD: "TURN_ON", "TURN_OFF", "UPDATE_THRESHOLD")
    private String action;

    // Quan trọng: Ai thực hiện?
    // Nếu là Auto thì ghi "SYSTEM", nếu là người dùng thì ghi "ADMIN"
    private String triggeredBy;

    private String description; // Mô tả chi tiết (nếu cần)

    private LocalDateTime timestamp;
}
