import React, { useState, useEffect } from 'react';

const SystemSettingPanel = ({ currentSettings, onSave }) => {
    // Khởi tạo state mặc định để tránh lỗi uncontrolled input
    const [formData, setFormData] = useState({
        maxTemperatureThreshold: 30.0,
        maxHumidityThreshold: 70.0,
        sunriseTime: "05:00",
        sunsetTime: "18:00"
    });

    useEffect(() => {
        if (currentSettings) {
            setFormData({
                maxTemperatureThreshold: currentSettings.maxTemperatureThreshold || 30.0,
                maxHumidityThreshold: currentSettings.maxHumidityThreshold || 70.0,
                sunriseTime: currentSettings.sunriseTime || "05:00",
                sunsetTime: currentSettings.sunsetTime || "18:00"
            });
        }
    }, [currentSettings]);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    return (
        <div className="card shadow-sm mb-4">
            <div className="card-header bg-dark text-white">
                <h5 className="m-0">⚙️ Cài Đặt Ngưỡng Tự Động</h5>
            </div>
            <div className="card-body">
                <div className="row g-3">
                    <div className="col-md-6 border-end">
                        <h6 className="text-primary">Môi trường</h6>
                        <div className="mb-2">
                            <label className="form-label">Ngưỡng Nhiệt độ (°C):</label>
                            <input type="number" step="0.1" className="form-control" 
                                name="maxTemperatureThreshold"
                                value={formData.maxTemperatureThreshold} onChange={handleChange} />
                        </div>
                        <div className="mb-2">
                            <label className="form-label">Ngưỡng Độ ẩm (%):</label>
                            <input type="number" step="0.1" className="form-control" 
                                name="maxHumidityThreshold"
                                value={formData.maxHumidityThreshold} onChange={handleChange} />
                        </div>
                    </div>
                    <div className="col-md-6">
                        <h6 className="text-warning">Lịch Trình Đèn</h6>
                        <div className="mb-2">
                            <label className="form-label">Giờ Bình Minh:</label>
                            <input type="time" className="form-control" 
                                name="sunriseTime"
                                value={formData.sunriseTime} onChange={handleChange} />
                        </div>
                        <div className="mb-2">
                            <label className="form-label">Giờ Hoàng Hôn:</label>
                            <input type="time" className="form-control" 
                                name="sunsetTime"
                                value={formData.sunsetTime} onChange={handleChange} />
                        </div>
                    </div>
                </div>
                <div className="text-end mt-3">
                    <button className="btn btn-primary" onClick={() => onSave(formData)}>Lưu Cài Đặt</button>
                </div>
            </div>
        </div>
    );
};

// --- QUAN TRỌNG ---
export default SystemSettingPanel;