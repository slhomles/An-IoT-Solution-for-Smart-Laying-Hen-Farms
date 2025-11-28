import React, { useEffect, useState } from 'react';
import { SmartBarnApi } from '../api/axiosClient';
import DeviceCard from '../components/DeviceCard';
import SystemSettingPanel from '../components/SystemSettingPanel';
import SensorChart from '../components/SensorChart';

const Dashboard = () => {
    const [devices, setDevices] = useState([]);
    const [settings, setSettings] = useState(null);
    const [sensorData, setSensorData] = useState([]);

    const loadData = async () => {
        try {
            const [devRes, setRes, sensorRes] = await Promise.all([
                SmartBarnApi.getAllDevices(),
                SmartBarnApi.getSettings(),
                SmartBarnApi.getLatestSensors()
            ]);
            
            // --- SỬA LỖI TẠI ĐÂY: Dùng .id thay vì .deviceId ---
            // Kiểm tra dữ liệu trước khi sort để tránh lỗi
            const deviceList = devRes.data || [];
            const sortedDevices = deviceList.sort((a, b) => {
                const idA = a.id || "";
                const idB = b.id || "";
                return idA.localeCompare(idB);
            });

            setDevices(sortedDevices);
            setSettings(setRes.data);
            setSensorData(sensorRes.data);
        } catch (error) {
            console.error("Lỗi tải dữ liệu:", error);
        }
    };

    useEffect(() => {
        loadData();
        const interval = setInterval(loadData, 2000); 
        return () => clearInterval(interval);
    }, []);

    const handleToggle = async (deviceId, status) => {
        await SmartBarnApi.controlDevice(deviceId, status);
        loadData();
    };

    const handleSwitchMode = async (deviceId, mode) => {
        await SmartBarnApi.switchMode(deviceId, mode);
        loadData();
    };

    const handleSaveSettings = async (newSettings) => {
        await SmartBarnApi.updateSettings(newSettings);
        alert("Đã cập nhật cài đặt!");
        loadData();
    };

    return (
        <div className="container-fluid py-4 bg-light min-vh-100">
            <h2 className="text-center mb-4 text-uppercase fw-bold text-success">
                🌱 Smart Barn Control Center
            </h2>

            <div className="row justify-content-center">
                <div className="col-lg-10">
                    <SystemSettingPanel currentSettings={settings} onSave={handleSaveSettings} />
                </div>
            </div>

            <div className="row mb-4">
                <div className="col-12">
                    <SensorChart data={sensorData} />
                </div>
            </div>

            <h4 className="mb-3 border-bottom pb-2">Danh Sách Thiết Bị</h4>
            <div className="row">
                {/* Kiểm tra mảng rỗng để hiện thông báo */}
                {devices.length === 0 ? (
                    <div className="text-center text-muted p-5">
                        Đang tải dữ liệu hoặc chưa có thiết bị...
                    </div>
                ) : (
                    devices.map(device => (
                        <DeviceCard 
                            key={device.id}  // Dùng .id
                            device={device} 
                            onToggle={handleToggle}
                            onSwitchMode={handleSwitchMode}
                        />
                    ))
                )}
            </div>
        </div>
    );
};

export default Dashboard;