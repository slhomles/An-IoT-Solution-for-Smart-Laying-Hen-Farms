package amartbarn.example.SmartBarn.repository;

import amartbarn.example.SmartBarn.model.ActionLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActionLogRepository extends MongoRepository<ActionLog, String> {

    // 1. Lấy danh sách lịch sử điều khiển mới nhất (để hiển thị lên bảng Log ở Dashboard)
    List<ActionLog> findTop20ByOrderByTimestampDesc();

    // 2. Tìm kiếm lịch sử theo thiết bị (Ví dụ: Chỉ xem lịch sử bật/tắt của "QUẠT")
    List<ActionLog> findByDeviceIdOrderByTimestampDesc(String deviceId);
}