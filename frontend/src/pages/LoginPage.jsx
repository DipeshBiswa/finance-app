import { useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios.js";
import "./AuthPages.css";

function LoginPage({ onLogin }) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError]       = useState("");
    const [loading, setLoading]   = useState(false);

    const handleLogin = async (e) => {
        e.preventDefault();
        setError("");
        setLoading(true);
        try {
            const response = await api.post('/auth/login', { username, password });
            const token = response.data.token;
            if (token) {
                localStorage.setItem('token', token);
                if (onLogin) onLogin();
                window.location.href = "/dashboard";
            }
        } catch (err) {
            setError(err.response?.data?.error || "Invalid username or password.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-bg">
            <div className="auth-card">
                <div className="auth-icon">💰</div>
                <h1 className="auth-title">Welcome back</h1>
                <p className="auth-subtitle">Sign in to your FinanceApp account</p>

                {error && <div className="auth-error">{error}</div>}

                <form onSubmit={handleLogin} className="auth-form">
                    <div className="auth-field">
                        <label className="auth-label">Username</label>
                        <input
                            className="auth-input"
                            type="text"
                            value={username}
                            onChange={e => setUsername(e.target.value)}
                            placeholder="Enter your username"
                            required
                        />
                    </div>
                    <div className="auth-field">
                        <label className="auth-label">Password</label>
                        <input
                            className="auth-input"
                            type="password"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            placeholder="Enter your password"
                            required
                        />
                    </div>
                    <button className="auth-btn" type="submit" disabled={loading}>
                        {loading ? "Signing in…" : "Sign In"}
                    </button>
                </form>

                <p className="auth-switch">
                    Don't have an account? <Link to="/register" className="auth-link">Register</Link>
                </p>
            </div>
        </div>
    );
}

export default LoginPage;