import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080/api",
    withCredentials: true
});

// Add JWT token to all requests
api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    console.log(`[Axios] ${config.method?.toUpperCase()} ${config.url}`);
    
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
        console.log(`[Axios] ✓ Added Bearer token (${token.substring(0, 20)}...)`);
    } else {
        console.warn(`[Axios] ⚠ WARNING: No token in localStorage!`);
    }
    
    console.log(`[Axios] Headers:`, config.headers);
    return config;
});

// Log response errors
api.interceptors.response.use(
    (response) => {
        console.log(`[Axios] ✓ ${response.status} ${response.config.url}`);
        return response;
    },
    (error) => {
        console.error(`[Axios] ✗ ${error.response?.status} ${error.config?.url}`);
        if (error.response?.status === 401) {
            console.error("[Axios] 401 Unauthorized - Check if token is valid");
            const hasToken = !!localStorage.getItem('token');
            console.error("[Axios] Token in storage:", hasToken ? "YES" : "NO");
        }
        return Promise.reject(error);
    }
);

export default api;