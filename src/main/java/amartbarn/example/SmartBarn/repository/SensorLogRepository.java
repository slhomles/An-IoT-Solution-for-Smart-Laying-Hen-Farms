package amartbarn.example.SmartBarn.repository;

import amartbarn.example.SmartBarn.model.SensorLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorLogRepository extends MongoRepository<SensorLog, String> {

    // 1. Lấy N bản ghi mới nhất để vẽ biểu đồ Real-time (Ví dụ lấy 10 mẫu gần nhất)
    // Sắp xếp giảm dần theo thời gian (mới nhất lên đầu)
    List<SensorLog> findTop10ByOrderByTimestampDesc();

    // 2. Lấy dữ liệu theo khoảng thời gian (Dùng cho chức năng "Xem lịch sử" hoặc "Xuất báo cáo")
    // Ví dụ: Xem nhiệt độ từ sáng đến chiều nay
    // LocalDateTime start, LocalDateTime end
    // List<SensorLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}