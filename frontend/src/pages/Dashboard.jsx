import React, { useState, useEffect, useCallback } from 'react';
import { usePlaidLink } from 'react-plaid-link';
import api from '../api/axios';
import TransactionCard from './TransactionCard.jsx';

// Isolated component so usePlaidLink only mounts when a real token exists.
// This prevents the "Plaid script embedded more than once" warning.
const PlaidLinkLauncher = ({ token, onSuccess, onExit }) => {
    const { open, ready } = usePlaidLink({ token, onSuccess, onExit });
    useEffect(() => {
        if (ready) open();
    }, [ready, open]);
    return null;
};

const Dashboard = () => {
    const [linkToken, setLinkToken] = useState(null);
    const [refreshKey, setRefreshKey] = useState(0);

    // Redirect to login if not authenticated
    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            console.warn("No auth token found — redirecting to login");
            window.location.href = "/";
        }
    }, []);

    const onSuccess = useCallback(async (publicToken) => {
        try {
            await api.post('/plaid/exchange-public-token', { publicToken });
            setRefreshKey(prev => prev + 1);
            setLinkToken(null);
        } catch (err) {
            console.error("Link flow failed", err);
        }
    }, []);

    const onExit = useCallback(() => setLinkToken(null), []);

    const handleConnectClick = async () => {
        try {
            const response = await api.post('/plaid/create-link-token');
            setLinkToken(response.data.linkToken);
        } catch (error) {
            console.error("Failed to get link token:", error.response?.status, error.response?.data);
        }
    };

    return (
        <div style={{ maxWidth: 760, margin: "0 auto", padding: "2rem 1rem" }}>
            <div style={{ display: "flex", justifyContent: "flex-end", marginBottom: "1rem" }}>
                <button
                    onClick={handleConnectClick}
                    style={{
                        display: "inline-flex",
                        alignItems: "center",
                        gap: 8,
                        padding: "10px 22px",
                        background: "linear-gradient(135deg, #6366f1, #8b5cf6)",
                        color: "#fff",
                        border: "none",
                        borderRadius: 10,
                        fontWeight: 600,
                        fontSize: "0.9rem",
                        cursor: "pointer",
                        boxShadow: "0 4px 14px rgba(99,102,241,0.35)",
                    }}
                >
                    Connect Bank
                </button>
            </div>
            {linkToken && (
                <PlaidLinkLauncher
                    token={linkToken}
                    onSuccess={onSuccess}
                    onExit={onExit}
                />
            )}
            <TransactionCard key={refreshKey} />
        </div>
    );
};

export default Dashboard;