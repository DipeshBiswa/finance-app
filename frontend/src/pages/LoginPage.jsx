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

        // 1. Create Form Data instead of a raw object
        const params = new URLSearchParams();
        params.append('username', username);
        params.append('password', password);

        try {
            // 2. Send the params. Axios will automatically set
            // the Content-Type to application/x-www-form-urlencoded
            const response = await api.post("/auth/login", params);

            if (response.status === 200) {
                console.log("Logged in successfully!");
                // Use window.location or useNavigate to go to /dashboard
                window.location.href = "/dashboard";
            }
        } catch (err) {
            // If it's still 401, check if the password was BCrypt encoded in the DB!
            console.error("Login failed. Status:", err.response?.status);
            alert("Invalid credentials or Server Error");
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