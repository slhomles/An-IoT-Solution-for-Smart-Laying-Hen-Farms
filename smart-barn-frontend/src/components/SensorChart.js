import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

const SensorChart = ({ data }) => {
    if (!data) return <div>Đang tải biểu đồ...</div>;

    // Đảo ngược mảng để hiển thị thời gian từ trái sang phải
    const chartData = [...data].reverse(); 

    return (
        <div className="card p-3">
            <h5>Biểu đồ Môi trường</h5>
            <div style={{ width: '100%', height: 300 }}>
                <ResponsiveContainer>
                    <LineChart data={chartData}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="timestamp" tick={{fontSize: 10}} />
                        <YAxis />
                        <Tooltip />
                        <Legend />
                        <Line type="monotone" dataKey="temperature" stroke="#ff7300" name="Nhiệt độ (°C)" />
                        <Line type="monotone" dataKey="humidity" stroke="#387908" name="Độ ẩm (%)" />
                    </LineChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
};

export default SensorChart;