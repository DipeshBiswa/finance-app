import React, { useCallback, useEffect, useState } from 'react';
import { usePlaidLink } from 'react-plaid-link';
import api from '../api/axios'; // Assuming your axios instance is here

/**
 * SUB-COMPONENT: PlaidLinkButton
 * This only renders when we have a valid linkToken.
 * This prevents the "embedded more than once" script error.
 */
const PlaidLinkButton = ({ token }) => {
    const onSuccess = useCallback(async (publicToken, metadata) => {
        try {
            // Send the publicToken to your exchange endpoint
            // Note: Using camelCase 'publicToken' to match your Java Controller payload.get("publicToken")
            await api.post('/api/plaid/exchange-public-token', { publicToken });
            alert("Bank connected successfully!");
            // Optionally: Trigger a redirect or a state update to show transactions
        } catch (error) {
            console.error("Token exchange failed:", error);
        }
    }, []);

    const { open, ready, error } = usePlaidLink({
        token,
        onSuccess,
    });

    if (error) {
        console.error("Plaid Link Error:", error);
    }

    return (
        <button
            onClick={() => open()}
            disabled={!ready}
            className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-8 py-3 rounded-xl shadow-lg transition-all disabled:bg-gray-400 disabled:cursor-not-allowed"
        >
            {ready ? "Connect a Bank Account" : "Securely Loading..."}
        </button>
    );
};

/**
 * MAIN COMPONENT: Dashboard
 */
const Dashboard = () => {
    const [linkToken, setLinkToken] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const generateToken = async () => {
            try {
                const response = await api.post('/api/plaid/create-link-token');
                setLinkToken(response.data.linkToken);
            } catch (error) {
                // Add this line to see the SPECIFIC error from the server
                console.log("Full Error Object:", error.response);
                alert("Error: " + (error.response?.status || "Server Down"));
            }
        };

        generateToken();
    }, []);

    return (
        <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center p-6">
            <div className="max-w-md w-full bg-white rounded-3xl shadow-xl p-8 text-center">
                <h1 className="text-3xl font-extrabold text-gray-900 mb-2">Welcome</h1>
                <p className="text-gray-500 mb-8">
                    Connect your bank to start tracking your expenses automatically.
                </p>

                {loading ? (
                    <div className="animate-pulse flex flex-col items-center">
                        <div className="h-10 w-48 bg-gray-200 rounded-lg mb-4"></div>
                        <p className="text-sm text-gray-400">Verifying secure connection...</p>
                    </div>
                ) : linkToken ? (
                    <PlaidLinkButton token={linkToken} />
                ) : (
                    <div className="text-red-500 bg-red-50 p-4 rounded-lg">
                        <p className="font-semibold">Connection Error</p>
                        <p className="text-sm">Unable to reach the secure server. Please try again later.</p>
                    </div>
                )}

                <footer className="mt-8 text-xs text-gray-400 flex items-center justify-center gap-2">
                    <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clipRule="evenodd" />
                    </svg>
                    Bank-level 256-bit encryption
                </footer>
            </div>
        </div>
    );
};

export default Dashboard;