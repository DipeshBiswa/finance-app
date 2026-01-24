import { useEffect, useState } from 'react';
import api from './api/axios';
import LoginPage from './pages/LoginPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';
import { BrowserRouter, Routes, Route, Link, useNavigate } from 'react-router-dom';

function App() {


    return (
        <div>
        <BrowserRouter>
            <nav>
                <Link to="/login">Login</Link>
                <Link to="/register">Register</Link>
            </nav>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
            </Routes>
        </BrowserRouter>
        </div>

    );
}

export default App;