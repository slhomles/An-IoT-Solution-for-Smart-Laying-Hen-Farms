package amartbarn.example.SmartBarn.service;

import amartbarn.example.SmartBarn.common.DeviceConst;
import amartbarn.example.SmartBarn.model.SystemSetting;
import amartbarn.example.SmartBarn.repository.SystemSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemSettingService {

    @Autowired
    private SystemSettingRepository settingRepository;

    // Lấy cài đặt hiện tại. Nếu chưa có thì tạo mặc định.
    public SystemSetting getCurrentSetting() {
        return settingRepository.findById(DeviceConst.SETTING_ID)
                .orElseGet(() -> {
                    SystemSetting defaultSetting = SystemSetting.builder()
                            .id(DeviceConst.SETTING_ID)
                            .maxTemperatureThreshold(28.0) // Đã xóa tag lỗi
                            .maxHumidityThreshold(75.0)    // Đã xóa tag lỗi
                            .sunriseTime("05:00")          // Đã xóa tag lỗi
                            .sunsetTime("20:00")
                            .simulationDurationMinutes(30)
                            .build();
                    return settingRepository.save(defaultSetting);
                });
    }

    public SystemSetting updateSetting(SystemSetting newSetting) {
        newSetting.setId(DeviceConst.SETTING_ID);
        return settingRepository.save(newSetting);
    }
}