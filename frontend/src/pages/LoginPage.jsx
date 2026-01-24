import {useState, useEffect} from "react";
import { createRoot } from 'react-dom/client';

function LoginPage() {

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    function handleChangeUsername(e){
        setUsername(e.target.value);
    }
    function handleChangePassword(e){
        setPassword(e.target.value);
    }
    function handleLogin(e){}
    function registerUser(){
        const navigate = useNavigate();


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
            <input type="submit" value="Login" />
        </form>
            <p>Current username: {username}</p>
            <p>Current password: {password}</p>
        </>

    );
}

export default LoginPage;