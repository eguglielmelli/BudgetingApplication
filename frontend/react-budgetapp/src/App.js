import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Login from './Login'; // Import your Login component
import Dashboard from './Dashboard'; // Import your Dashboard component

class App extends React.Component {
    render() {
        return (
            <Router>
                <Routes>
                    <Route path="/login" element={<Login />} />
                    <Route path="/dashboard" element={<Dashboard />} />
                    <Route path="/" element={<Login />} /> {/* Default route */}
                </Routes>
            </Router>
        );
    }
}

export default App;
