import axios from 'axios';

// Đổi URL này thành IP máy tính chạy Spring Boot nếu bạn test qua mạng LAN
// Nếu chạy trên cùng máy tính thì để localhost
const BASE_URL = 'http://localhost:8080/api/web';

const apiClient = axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// --- CHÚ Ý DÒNG NÀY: Phải là "export const" ---
export const SmartBarnApi = {
    // 1. Lấy danh sách thiết bị
    getAllDevices: () => apiClient.get('/devices'),

    // 2. Lấy dữ liệu cảm biến mới nhất
    getLatestSensors: () => apiClient.get('/sensors/latest'),

    // 3. Lấy log hoạt động
    getLogs: () => apiClient.get('/logs'),

    // 4. Lấy cài đặt
    getSettings: () => apiClient.get('/settings'),

    // 5. Cập nhật cài đặt
    updateSettings: (settings) => apiClient.post('/settings', settings),

    // 6. Điều khiển Bật/Tắt (Manual Control)
    controlDevice: (deviceId, status) => 
        apiClient.post('/control', { deviceId, status }),

    // 7. Chuyển chế độ Auto/Manual
    switchMode: (deviceId, requestMode) => 
        apiClient.post('/mode', { deviceId, requestMode }),
};