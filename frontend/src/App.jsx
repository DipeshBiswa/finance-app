import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import LoginPage from './pages/LoginPage.jsx';
import RegisterPage from './pages/RegisterPage.jsx';

// Simple placeholder for now
const Dashboard = () => (
    <div className="p-8">
        <h2 className="text-2xl font-bold">Welcome to your Dashboard!</h2>
        <p>This is where your Plaid transactions will appear.</p>
    </div>
);

function App() {
    return (
        <BrowserRouter>
            <div className="min-h-screen bg-gray-50">
                {/* Simple Navigation Bar */}
                <nav className="p-4 bg-white shadow-md flex gap-4">
                    <Link to="/" className="text-blue-600 hover:underline">Login</Link>
                    <Link to="/register" className="text-blue-600 hover:underline">Register</Link>
                </nav>

                {/* Main Content Area */}
                <main className="container mx-auto mt-8">
                    <Routes>
                        <Route path="/" element={<LoginPage />} />
                        <Route path="/register" element={<RegisterPage />} />
                        <Route path="/dashboard" element={<Dashboard />} />
                    </Routes>
                </main>
            </div>
        </BrowserRouter>
    );
}

export default App;