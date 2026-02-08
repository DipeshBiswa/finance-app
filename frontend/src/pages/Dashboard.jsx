import React, { useState, useEffect } from 'react';
import { usePlaidLink } from 'react-plaid-link';
import api from '../api/axios';
import TransactionCard from './TransactionCard.jsx';

const Dashboard = () => {
    const [linkToken, setLinkToken] = useState(null);
    const [refreshKey, setRefreshKey] = useState(0);

    // usePlaidLink should only initialize when linkToken is actually present
    const { open, ready } = usePlaidLink({
        token: linkToken,
        onSuccess: async (publicToken) => {
            try {
                // This will fail if principal is null!
                await api.post('/plaid/exchange-public-token', { publicToken });
                setRefreshKey(prev => prev + 1);
                setLinkToken(null); // Clear token after success
            } catch (err) {
                console.error("Link flow failed", err);
            }
        },
        onExit: () => setLinkToken(null), // Clear token if user closes popup
    });

    // Only call open() once when the token is ready
    useEffect(() => {
        if (ready && linkToken) {
            open();
        }
    }, [ready, linkToken]);

    const handleConnectClick = async () => {
        try {
            const response = await api.post('/plaid/create-link-token');
            setLinkToken(response.data.linkToken);
        } catch (error) {
            console.error("Check if you are logged in!", error);
        }
    };

    return (
        <div>
            <button onClick={handleConnectClick}>Connect Bank</button>
            <TransactionCard key={refreshKey} />
        </div>
    );
};

export default Dashboard;