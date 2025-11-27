package amartbarn.example.SmartBarn.controller;

import amartbarn.example.SmartBarn.model.*;
import amartbarn.example.SmartBarn.repository.ActionLogRepository;
import amartbarn.example.SmartBarn.repository.DeviceStateRepository;
import amartbarn.example.SmartBarn.repository.SensorLogRepository;
import amartbarn.example.SmartBarn.service.DeviceService;
import amartbarn.example.SmartBarn.service.SystemSettingService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/web")
@CrossOrigin(origins = "*") // Cho phép React truy cập
public class WebController {

    @Autowired
    private SensorLogRepository sensorLogRepo;
    @Autowired
    private DeviceStateRepository deviceStateRepo;
    @Autowired
    private ActionLogRepository actionLogRepo;

    @Autowired
    private DeviceService deviceService;
    @Autowired
    private SystemSettingService settingService;

    // --- NHÓM 1: LẤY DỮ LIỆU HIỂN THỊ (GET) ---

    // 1. Lấy trạng thái các thiết bị (Quạt, Đèn...)
    @GetMapping("/devices")
    public List<DeviceState> getAllDevices() {
        return deviceStateRepo.findAll();
    }

    // 2. Lấy 10 mẫu nhiệt độ/độ ẩm mới nhất để vẽ biểu đồ
    @GetMapping("/sensors/latest")
    public List<SensorLog> getLatestSensors() {
        return sensorLogRepo.findTop10ByOrderByTimestampDesc();
    }

    // 3. Lấy lịch sử hoạt động (Log)
    @GetMapping("/logs")
    public List<ActionLog> getLatestLogs() {
        return actionLogRepo.findTop20ByOrderByTimestampDesc();
    }

    // 4. Lấy cài đặt hiện tại (Ngưỡng nhiệt độ...)
    @GetMapping("/settings")
    public SystemSetting getSettings() {
        return settingService.getCurrentSetting();
    }

    // --- NHÓM 2: ĐIỀU KHIỂN & CÀI ĐẶT (POST) ---

    // 5. Cập nhật cài đặt (Ví dụ: Chỉnh ngưỡng nóng lên 30 độ)
    @PostMapping("/settings")
    public SystemSetting updateSettings(@RequestBody SystemSetting setting) {
        return settingService.updateSetting(setting);
    }

    // 6. Điều khiển Thủ công (Bật/Tắt thiết bị)
    // Payload: { "deviceId": "FAN_SYSTEM", "status": "ON" }
    @PostMapping("/control")
    public DeviceState manualControl(@RequestBody ControlRequest request) {
        return deviceService.manualControl(request.getDeviceId(), request.getStatus());
    }

    // 7. Chuyển chế độ (Auto <-> Manual)
    // Payload: { "deviceId": "FAN_SYSTEM", "mode": "AUTO" }
    @PostMapping("/mode")
    public DeviceState switchMode(@RequestBody ModeRequest request) {
        return deviceService.switchMode(request.getDeviceId(), request.getRequestMode());
    }

    // --- DTO (Data Transfer Object) CLASSES ---
    // Dùng để hứng dữ liệu JSON từ React gửi lên cho gọn
    @Data
    static class ControlRequest {
        private String deviceId;
        private DeviceStatus status;
    }

    @Data
    static class ModeRequest {
        private String deviceId;
        private DeviceMode requestMode;
    }
}