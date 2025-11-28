import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

// Nếu bạn chưa cài bootstrap thì dòng dưới sẽ lỗi, nhưng ở bước trước mình đã hướng dẫn cài rồi
// npm install bootstrap
import 'bootstrap/dist/css/bootstrap.min.css'; 

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);