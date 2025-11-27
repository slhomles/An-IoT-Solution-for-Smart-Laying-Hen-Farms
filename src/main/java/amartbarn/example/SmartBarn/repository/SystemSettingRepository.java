package amartbarn.example.SmartBarn.repository;

import amartbarn.example.SmartBarn.model.SystemSetting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemSettingRepository extends MongoRepository<SystemSetting, String> {

    // Thường collection này chỉ có duy nhất 1 document lưu cài đặt chung.
    // Chúng ta sẽ dùng .findById("DEFAULT_SETTING") để lấy ra.
}