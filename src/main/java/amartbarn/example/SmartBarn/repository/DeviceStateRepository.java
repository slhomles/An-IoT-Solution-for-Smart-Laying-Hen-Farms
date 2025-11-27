package amartbarn.example.SmartBarn.repository;

import amartbarn.example.SmartBarn.model.DeviceState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceStateRepository extends MongoRepository<DeviceState, String> {

    // Repository này chủ yếu dùng các hàm có sẵn:
    // .findAll() -> Lấy danh sách trạng thái tất cả thiết bị để hiển thị lên App
    // .findById(id) -> Lấy trạng thái 1 thiết bị cụ thể để cập nhật
    // .save(deviceState) -> Lưu trạng thái mới (Bật/Tắt)
}