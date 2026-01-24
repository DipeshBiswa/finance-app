import {useState, useEffect} from "react";
import { createRoot } from 'react-dom/client';
import App from "../App.jsx";

function RegisterPage() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [email, setEmail] = useState("");
    function handleChangeUsername(e){
        setUsername(e.target.value);
    }
    function handleChangePassword(e){
        setPassword(e.target.value);
    }
    function handelChangeEmail(e){
        setEmail(e.target.value);
    }
    function handleRegister(){

    }
    return (
        <>
            <form>
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
                <label>
                    Enter email:
                    <input type="email" value={email} onChange={handelChangeEmail} placeholder="email" />

                </label>
                <input type="submit" value="Register" />
            </form>
            <p>Current username: {username}</p>
            <p>Current password: {password}</p>
            <p>Current Email: {email}</p>
        </>

    );
}
export default RegisterPage;