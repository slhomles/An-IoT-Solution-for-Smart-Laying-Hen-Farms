import React from 'react';
// Đảm bảo bạn đã import đúng file constants (hoặc define tạm ở đây nếu chưa có)
const DEVICE_INFO = {
    FAN_SYSTEM: { name: "Hệ Thống Quạt", icon: "❄️", color: "primary", desc: "Làm mát" },
    MIST_SPRAYER: { name: "Phun Sương", icon: "💧", color: "info", desc: "Tăng ẩm" },
    LIGHT_SYSTEM: { name: "Đèn Chiếu Sáng", icon: "💡", color: "warning", desc: "Ánh sáng" },
    CONVEYOR_BELT: { name: "Băng Chuyền", icon: "⚙️", color: "secondary", desc: "Vận chuyển" },
    CLEANER_SYSTEM: { name: "Máy Dọn Phân", icon: "🧹", color: "dark", desc: "Vệ sinh" }
};

const DeviceCard = ({ device, onToggle, onSwitchMode }) => {
    // --- SỬA LỖI: Dùng device.id để lấy info ---
    const info = DEVICE_INFO[device.id] || { name: device.id, icon: "🔌", color: "secondary", desc: "Thiết bị" };
    
    // --- SỬA LỖI: Dùng device.mode thay vì device.currentMode ---
    // (Vì trong JSON trả về là "mode": "MANUAL")
    const isAuto = device.mode === 'AUTO';
    const isOn = device.currentStatus === 'ON';

    return (
        <div className="col-md-4 mb-4">
            <div className={`card h-100 shadow-sm ${isOn ? `border-${info.color}` : ''}`}>
                <div className="card-header d-flex justify-content-between align-items-center">
                    <h5 className="m-0">{info.icon} {info.name}</h5>
                    <span className={`badge ${isOn ? 'bg-success' : 'bg-secondary'}`}>
                        {isOn ? 'ĐANG CHẠY' : 'ĐÃ TẮT'}
                    </span>
                </div>
                
                <div className="card-body">
                    <p className="text-muted small mb-3">{info.desc}</p>

                    {/* Chuyển chế độ */}
                    <div className="d-flex justify-content-between align-items-center mb-3 p-2 bg-light rounded">
                        <span className="fw-bold" style={{fontSize: '0.9rem'}}>Chế độ:</span>
                        <div className="btn-group" role="group">
                            <button 
                                type="button" 
                                className={`btn btn-sm ${isAuto ? 'btn-primary' : 'btn-outline-primary'}`}
                                onClick={() => onSwitchMode(device.id, 'AUTO')} // Dùng device.id
                            >
                                Tự động
                            </button>
                            <button 
                                type="button" 
                                className={`btn btn-sm ${!isAuto ? 'btn-primary' : 'btn-outline-primary'}`}
                                onClick={() => onSwitchMode(device.id, 'MANUAL')} // Dùng device.id
                            >
                                Thủ công
                            </button>
                        </div>
                    </div>

                    {/* Nút điều khiển */}
                    <div className="d-grid">
                        <button 
                            className={`btn btn-lg ${isOn ? 'btn-danger' : 'btn-success'}`}
                            onClick={() => onToggle(device.id, isOn ? 'OFF' : 'ON')} // Dùng device.id
                            disabled={isAuto}
                            style={{ opacity: isAuto ? 0.6 : 1 }}
                        >
                            {isAuto ? <span><i className="bi bi-robot"></i> Hệ thống tự quản lý</span> : (isOn ? 'TẮT THIẾT BỊ' : 'BẬT THIẾT BỊ')}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DeviceCard;