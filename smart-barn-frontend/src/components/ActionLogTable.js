import React from 'react';

const ActionLogTable = ({ logs }) => {
    if (!logs) return null;

    return (
        <div className="card p-3 mt-3">
            <h5>Lịch sử Hoạt động</h5>
            <div className="table-responsive" style={{maxHeight: '300px'}}>
                <table className="table table-striped table-sm">
                    <thead>
                        <tr>
                            <th>Thời gian</th>
                            <th>Thiết bị</th>
                            <th>Hành động</th>
                            <th>Chi tiết</th>
                        </tr>
                    </thead>
                    <tbody>
                        {logs.map((log) => (
                            <tr key={log.id}>
                                <td>{log.timestamp}</td>
                                <td>{log.deviceId}</td>
                                <td>{log.actionType}</td>
                                <td>{log.description}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default ActionLogTable;