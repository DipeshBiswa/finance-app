import React, { useState, useEffect } from 'react';
import { usePlaidLink } from 'react-plaid-link';
import api from '../api/axios';
import TransactionCard from './TransactionCard.jsx'

const Dashboard = () => {
    const [linkToken, setLinkToken] = useState(null);
    const [isConnecting, setIsConnecting] = useState(false);

    const { open, ready } = usePlaidLink({
        token: linkToken,
        onSuccess: async (publicToken) => {
            try {
                await api.post('/plaid/exchange-public-token', { publicToken });
                alert("Bank connected successfully!");
                setLinkToken(null);
            } catch (err) {
                console.error("Exchange failed", err);
            }
        },
    });

    useEffect(() => {
        if (ready && linkToken) {
            open();
            setLinkToken(null);
            setIsConnecting(false);
        }
    }, [ready, linkToken, open]);

    const handleConnectClick = async () => {
        setIsConnecting(true);
        try {
            const response = await api.post('/plaid/create-link-token');
            setLinkToken(response.data.linkToken);
        } catch (error) {
            setIsConnecting(false);
            alert("Error: " + (error.response?.status || "Check console"));
        }
    };

    return (
        <div style={{
            fontFamily: 'Arial, sans-serif',
            backgroundColor: '#f9f9f9',
            minHeight: '100vh',
            padding: '20px'
        }}>
            {/* Top Navigation / Action Bar */}
            <div style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                maxWidth: '800px',
                margin: '0 auto 30px auto',
                padding: '20px',
                backgroundColor: 'white',
                borderRadius: '12px',
                boxShadow: '0 2px 4px rgba(0,0,0,0.05)'
            }}>
                <h1 style={{ margin: 0, fontSize: '1.5rem', color: '#333' }}>Financial Overview</h1>
                <button
                    onClick={handleConnectClick}
                    disabled={isConnecting}
                    style={{
                        backgroundColor: '#007bff',
                        color: 'white',
                        border: 'none',
                        padding: '10px 20px',
                        borderRadius: '6px',
                        cursor: 'pointer',
                        fontWeight: '600'
                    }}
                >
                    {isConnecting ? "Opening Plaid..." : "Connect New Bank"}
                </button>
            </div>

            {/* Main Content Area */}
            <div style={{ maxWidth: '800px', margin: '0 auto' }}>
               <TransactionCard></TransactionCard>
            </div>
        </div>
    );
};

export default Dashboard;