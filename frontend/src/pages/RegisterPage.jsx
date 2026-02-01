import { useState } from "react";
import api from "../api/axios.js";
import "./Registerpage.css"

function RegisterPage() {
    // 1. Keep your state at the top level
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");

    const handleRegister = async (e) => {
        if (e) e.preventDefault(); // Prevents page reload

        const userPayload = {
            username: username,
            password: password,
            email: email
        };

        try {
            const response = await api.post("/api/auth/register", userPayload);
            console.log("Registration Successful!", response.data);
            alert("User registered!");
            window.location.href = "/dashboard";
        } catch (err) {
            console.error("Error registering user", err.response?.data);
            alert("Registration failed: " + (err.response?.data?.message || "Check console"));
        }
    };

    return (
        <div className="p-4">
            <form onSubmit={handleRegister}>
                <div>
                    <label>Enter username:</label>
                    <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} />
                </div>
                <div>
                    <label>Enter Password:</label>
                    <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
                </div>
                <div>
                    <label>Enter email:</label>
                    <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
                </div>

                {/* Submit button inside form is standard */}
                <button type="submit" className="mt-4 bg-blue-500 text-white p-2">
                    Register
                </button>
            </form>

            <hr className="my-4" />
            <p>Current username: {username}</p>
            <p>Email: {email}</p>
        </div>
    );
}

export default RegisterPage;