import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css'; 

// ĐÚNG: import Dashboard from ... (Default import)
// SAI: import { Dashboard } from ... (Named import)
import Dashboard from './pages/Dashboard';

function App() {
  return (
    <div className="App">
      <Dashboard />
    </div>
  );
}

export default App;