package amartbarn.example.SmartBarn.service;

import amartbarn.example.SmartBarn.common.DeviceConst;
import amartbarn.example.SmartBarn.model.*;
import amartbarn.example.SmartBarn.repository.ActionLogRepository;
import amartbarn.example.SmartBarn.repository.DeviceStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
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

    @Scheduled(cron = "0 * * * * ?")
    public void autoControlLightByTime() {
        // 1. Lấy cài đặt thời gian từ DB
        SystemSetting setting = settingService.getCurrentSetting();
        String sunriseStr = setting.getSunriseTime(); // Giờ tắt đèn (VD: 05:00)
        String sunsetStr = setting.getSunsetTime();   // Giờ bật đèn (VD: 18:00)

        // 2. Kiểm tra định dạng giờ (tránh lỗi null hoặc sai format)
        if (sunriseStr == null || sunsetStr == null) return;

        try {
            LocalTime now = LocalTime.now();
            LocalTime sunrise = LocalTime.parse(sunriseStr);
            LocalTime sunset = LocalTime.parse(sunsetStr);

            // 3. Xác định trạng thái mong muốn
            // Đèn sẽ BẬT khi trời tối (sau hoàng hôn HOẶC trước bình minh)
            // Ví dụ: 18:00 -> 05:00 sáng hôm sau
            boolean shouldBeOn = now.isAfter(sunset) || now.isBefore(sunrise);

            // 4. Gọi hàm điều khiển chung (đã có sẵn logic check AUTO/MANUAL)
            checkAndControlDevice(DeviceConst.LIGHT_ID, shouldBeOn);

        } catch (DateTimeParseException e) {
            System.err.println("Lỗi định dạng giờ trong SystemSetting: " + e.getMessage());
        }
    }
    @Scheduled(cron = "* * * * * ?")
    public void runScheduledTasks() {
        // Lấy setting hiện tại
        SystemSetting setting = settingService.getCurrentSetting();
        LocalTime now = LocalTime.now();

        // 1. Xử lý Băng Chuyền
        processTimerLogic(
                DeviceConst.CONVEYOR_ID,
                setting.getConveyorStartTime(),
                setting.getConveyorDurationSeconds(),
                now
        );

        // 2. Xử lý Máy Dọn Phân
        processTimerLogic(
                DeviceConst.CLEANER_ID,
                setting.getCleanerStartTime(),
                setting.getCleanerDurationSeconds(),
                now
        );
    }

    // --- HÀM XỬ LÝ CHUNG (Đã tối ưu cho GIÂY) ---
    private void processTimerLogic(String deviceId, String startTimeStr, Integer durationSeconds, LocalTime now) {
        // Kiểm tra input: Nếu chưa cài đặt giờ hoặc thời gian chạy <= 0 thì bỏ qua
        if (startTimeStr == null || durationSeconds == null || durationSeconds <= 0) {
            return;
        }

        try {
            // Parse giờ bắt đầu
            LocalTime start = LocalTime.parse(startTimeStr);

            // Tính giờ kết thúc bằng cách cộng thêm GIÂY
            LocalTime end = start.plusSeconds(durationSeconds);

            // Logic kiểm tra thời gian (xử lý cả trường hợp qua đêm)
            boolean shouldBeOn;
            if (end.isBefore(start)) {
                // Trường hợp qua đêm (VD: chạy từ 23:59:50 đến 00:00:10)
                shouldBeOn = now.isAfter(start) || now.isBefore(end);
            } else {
                // Trường hợp trong ngày
                shouldBeOn = now.isAfter(start) && now.isBefore(end);
            }

            // Gọi hàm điều khiển cốt lõi (Hàm này đã có logic check AUTO/MANUAL)
            checkAndControlDevice(deviceId, shouldBeOn);

        } catch (DateTimeParseException e) {
            // Log lỗi nhẹ (có thể dùng log.warn nếu cấu hình logger)
            System.err.println("Lỗi định dạng giờ cho thiết bị " + deviceId);
        }
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