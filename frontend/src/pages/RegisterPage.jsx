import { useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios.js";
import "./AuthPages.css";

function RegisterPage() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail]       = useState("");
    const [error, setError]       = useState("");
    const [loading, setLoading]   = useState(false);

    const handleRegister = async (e) => {
        e.preventDefault();
        setError("");

        if (password.length < 8) { setError("Password must be at least 8 characters."); return; }
        if (username.length < 6) { setError("Username must be at least 6 characters."); return; }

        setLoading(true);
        try {
            await api.post("/auth/register", { username, password, email });
            window.location.href = "/";
        } catch (err) {
            setError(err.response?.data?.message || err.response?.data || "Registration failed. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-bg">
            <div className="auth-card">
                <div className="auth-icon">🌿</div>
                <h1 className="auth-title">Create account</h1>
                <p className="auth-subtitle">Start managing your finances today</p>

                {error && <div className="auth-error">{error}</div>}

                <form onSubmit={handleRegister} className="auth-form">
                    <div className="auth-field">
                        <label className="auth-label">Username</label>
                        <input
                            className="auth-input"
                            type="text"
                            value={username}
                            onChange={e => setUsername(e.target.value)}
                            placeholder="At least 6 characters"
                            required
                        />
                    </div>
                    <div className="auth-field">
                        <label className="auth-label">Email</label>
                        <input
                            className="auth-input"
                            type="email"
                            value={email}
                            onChange={e => setEmail(e.target.value)}
                            placeholder="you@example.com"
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
                            placeholder="At least 8 characters"
                            required
                        />
                    </div>
                    <button className="auth-btn" type="submit" disabled={loading}>
                        {loading ? "Creating account…" : "Create Account"}
                    </button>
                </form>

                <p className="auth-switch">
                    Already have an account? <Link to="/" className="auth-link">Sign in</Link>
                </p>
            </div>
        </div>
    );
}

export default RegisterPage;