import { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Link, useNavigate, useLocation } from 'react-router-dom';
import LoginPage from './pages/LoginPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';
import Dashboard from './pages/Dashboard.jsx';
import GoalsPage from './pages/GoalsPage.jsx';
import './App.css';

function NavBar({ isLoggedIn, onLogout }) {
    const location = useLocation();
    const isAuth = location.pathname === '/' || location.pathname === '/login' || location.pathname === '/register';

    return (
        <nav className="app-nav">
            <div className="app-nav-brand">FinanceApp</div>
            <div className="app-nav-links">
                {isLoggedIn ? (
                    <>
                        <Link to="/dashboard" className={`app-nav-link ${location.pathname === '/dashboard' ? 'active' : ''}`}>
                            Dashboard
                        </Link>
                        <Link to="/goals" className={`app-nav-link ${location.pathname === '/goals' ? 'active' : ''}`}>
                            Goals
                        </Link>
                        <button className="app-nav-logout" onClick={onLogout}>
                            Logout
                        </button>
                    </>
                ) : (
                    <>
                        <Link to="/" className={`app-nav-link ${location.pathname === '/' || location.pathname === '/login' ? 'active' : ''}`}>
                            Login
                        </Link>
                        <Link to="/register" className={`app-nav-link ${location.pathname === '/register' ? 'active' : ''}`}>
                            Register
                        </Link>
                    </>
                )}
            </div>
        </nav>
    );
}

function AppContent() {
    const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('token'));
    const navigate = useNavigate();

    // Keep state in sync if token changes (e.g. after login redirect)
    useEffect(() => {
        const sync = () => setIsLoggedIn(!!localStorage.getItem('token'));
        window.addEventListener('storage', sync);
        // Also poll once on mount in case we just redirected
        sync();
        return () => window.removeEventListener('storage', sync);
    }, []);

    const handleLogout = () => {
        localStorage.removeItem('token');
        setIsLoggedIn(false);
        navigate('/');
    };

    return (
        <div className="app-shell">
            <NavBar isLoggedIn={isLoggedIn} onLogout={handleLogout} />
            <main className="app-main">
                <Routes>
                    <Route path="/" element={<LoginPage onLogin={() => setIsLoggedIn(true)} />} />
                    <Route path="/login" element={<LoginPage onLogin={() => setIsLoggedIn(true)} />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route path="/dashboard" element={<Dashboard />} />
                        <Route path="/goals" element={<GoalsPage />} />
                </Routes>
            </main>
        </div>
    );
}

function App() {
    return (
        <BrowserRouter>
            <AppContent />
        </BrowserRouter>
    );
}
export default App;