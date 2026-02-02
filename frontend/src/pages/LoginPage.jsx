import {useState, useEffect} from "react";
import api from "../api/axios.js";
import "./Loginpage.css"

function LoginPage() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    function handleChangeUsername(e){
        setUsername(e.target.value);
    }
    function handleChangePassword(e){
        setPassword(e.target.value);
    }
    const handleLogin = async (e) => {
        e.preventDefault();

        // Instead of { username, password }, send it like this:
        const params = new URLSearchParams();
        params.append('username', username);
        params.append('password', password);

        try {
            const response = await api.post('/api/auth/login', params, {
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
            });
            alert("User logged in successfully!");
            window.location.href = "/dashboard";

        } catch (error) {
            console.error("Login failed", error.response.status);
        }
    };

    return (
        <div>

        <form onSubmit={handleLogin}>
            <label>
                Enter username:
                <input type="text" value={username} onChange={handleChangeUsername} placeholder="username" />
            </label>
            <label>
                Enter Password:
                <input type="text"
                       value={password}
                       onChange={handleChangePassword}
                       placeholder="password" />
            </label>
            <input type="submit" value="Login" />
        </form>
            <p>Current username: {username}</p>
            <p>Current password: {password}</p>
        </div>

    );
}

export default LoginPage;