package amartbarn.example.SmartBarn.service;

import amartbarn.example.SmartBarn.common.DeviceConst;
import amartbarn.example.SmartBarn.model.*;
import amartbarn.example.SmartBarn.repository.ActionLogRepository;
import amartbarn.example.SmartBarn.repository.DeviceStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class DeviceService {

    @Autowired
    private DeviceStateRepository deviceRepository;
    @Autowired
    private ActionLogRepository actionLogRepository;
    @Autowired
    private SystemSettingService settingService;

    // --- 1. KHỞI TẠO THIẾT BỊ (Chạy 1 lần đầu) ---
    public void initDevices() {
        createIfNotExists(DeviceConst.FAN_ID, "Hệ Thống Quạt");
        createIfNotExists(DeviceConst.MIST_ID, "Phun Sương");
        createIfNotExists(DeviceConst.LIGHT_ID, "Đèn Chiếu Sáng");
        createIfNotExists(DeviceConst.CONVEYOR_ID, "Băng Chuyền");
        createIfNotExists(DeviceConst.CLEANER_ID, "Hệ Thống Dọn Phân");
    }

    private void createIfNotExists(String id, String name) {
        if (!deviceRepository.existsById(id)) {
            deviceRepository.save(DeviceState.builder()
                    .id(id)
                    .displayName(name)
                    .currentStatus(DeviceStatus.OFF)
                    .mode(DeviceMode.AUTO) // Mặc định là Tự động
                    .lastUpdated(LocalDateTime.now())
                    .build());
        }
    }

    // --- 2. XỬ LÝ LOGIC TỰ ĐỘNG (RULE ENGINE) ---
    // Hàm này được SensorService gọi mỗi khi có dữ liệu mới từ ESP32
    public void processEnvironmentRules(Double currentTemp, Double currentHum) {
        SystemSetting setting = settingService.getCurrentSetting();

        // LOGIC 1: NHIỆT ĐỘ -> QUẠT & PHUN SƯƠNG [cite: 165]
        // Nếu Nhiệt độ > Ngưỡng (28 độ) -> Bật Quạt & Phun Sương
        checkAndControlDevice(DeviceConst.FAN_ID, currentTemp > setting.getMaxTemperatureThreshold());
        checkAndControlDevice(DeviceConst.MIST_ID, currentTemp > setting.getMaxTemperatureThreshold());

        // LOGIC 2: ĐỘ ẨM -> QUẠT HÚT (Ở đây dùng chung FAN_SYSTEM cho đơn giản hóa demo)
        // Nếu Độ ẩm > 75% -> Bật Quạt, Tắt Phun Sương [cite: 166]
        if (currentHum > setting.getMaxHumidityThreshold()) {
            // Logic ưu tiên: Quá ẩm thì phải tắt phun sương ngay lập tức dù trời nóng
            forceTurnOff(DeviceConst.MIST_ID, "Auto: Độ ẩm quá cao (> " + setting.getMaxHumidityThreshold() + "%)");
            checkAndControlDevice(DeviceConst.FAN_ID, true);
        }
    }

    // Hàm hỗ trợ: Kiểm tra chế độ AUTO/MANUAL trước khi bật/tắt
    private void checkAndControlDevice(String deviceId, boolean shouldBeOn) {
        DeviceState device = deviceRepository.findById(deviceId).orElse(null);
        if (device == null) return;

        // QUAN TRỌNG: Nếu đang là MANUAL -> Hệ thống KHÔNG ĐƯỢC CAN THIỆP
        if (device.getMode() == DeviceMode.MANUAL) {
            return;
        }

        // Nếu đang AUTO -> So sánh trạng thái hiện tại với trạng thái mong muốn
        DeviceStatus desiredStatus = shouldBeOn ? DeviceStatus.ON : DeviceStatus.OFF;

        if (device.getCurrentStatus() != desiredStatus) {
            // Chỉ update và log khi có sự thay đổi (tránh spam log)
            device.setCurrentStatus(desiredStatus);
            device.setLastUpdated(LocalDateTime.now());
            deviceRepository.save(device);

            // Ghi Log hành động tự động
            logAction(deviceId, desiredStatus == DeviceStatus.ON ? "TURN_ON" : "TURN_OFF", "SYSTEM", "Auto control by Environment Rules");
        }
    }

    private void forceTurnOff(String deviceId, String reason) {
        DeviceState device = deviceRepository.findById(deviceId).orElse(null);
        if (device != null && device.getMode() == DeviceMode.AUTO && device.getCurrentStatus() == DeviceStatus.ON) {
            device.setCurrentStatus(DeviceStatus.OFF);
            deviceRepository.save(device);
            logAction(deviceId, "TURN_OFF", "SYSTEM", reason);
        }
    }

    // --- 3. ĐIỀU KHIỂN THỦ CÔNG (TỪ REACT) ---
    public DeviceState manualControl(String deviceId, DeviceStatus status) {
        DeviceState device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        // Khi người dùng bấm nút, tự động chuyển sang chế độ MANUAL
        device.setMode(DeviceMode.MANUAL);
        device.setCurrentStatus(status);
        device.setLastUpdated(LocalDateTime.now());

        logAction(deviceId, status.toString(), "USER_ADMIN", "Manual Control via Web Dashboard");
        return deviceRepository.save(device);
    }

    public DeviceState switchMode(String deviceId, DeviceMode mode) {
        DeviceState device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        device.setMode(mode);
        return deviceRepository.save(device);
    }

    // --- 4. TẠO PHẢN HỒI CHO ESP32 (POLLING) ---
    // Gom trạng thái của tất cả thiết bị để trả về cho ESP32
    public Map<String, String> getEsp32Commands() {
        Map<String, String> commands = new HashMap<>();

        // Lấy list thiết bị từ DB
        deviceRepository.findAll().forEach(device -> {
            // Map: "FAN_SYSTEM" -> "ON"
            commands.put(device.getId(), device.getCurrentStatus().toString());
        });

        return commands;
    }

    // Helper: Ghi log
    private void logAction(String deviceId, String action, String triggeredBy, String desc) {
        actionLogRepository.save(ActionLog.builder()
                .deviceId(deviceId)
                .action(action)
                .triggeredBy(triggeredBy)
                .description(desc)
                .timestamp(LocalDateTime.now())
                .build());
    }
}