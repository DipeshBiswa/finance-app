import React, { useState, useEffect } from 'react';
import { usePlaidLink } from 'react-plaid-link';
import api from '../api/axios';

const Dashboard = () => {
    const [linkToken, setLinkToken] = useState(null);

    const { open, ready } = usePlaidLink({
        token: linkToken,
        onSuccess: (publicToken) => {
            api.post('/plaid/exchange-public-token', { publicToken }); // Corrected path
        },
    });

    useEffect(() => {
        if (ready && linkToken) {
            open();
            setLinkToken(null);
        }
    }, [ready, linkToken, open]);

    const handleConnectClick = async () => {
        try {
            // Path is just '/plaid/...' because baseURL handles the '/api'
            const response = await api.post('/plaid/create-link-token');
            setLinkToken(response.data.linkToken);
        } catch (error) {
            alert("Error: " + (error.response?.status || "Check console"));
        }
    };

    return (
        <div style={{ textAlign: 'center', marginTop: '100px' }}>
            <button onClick={handleConnectClick}>Connect Bank Account</button>
        </div>
    );
};

export default Dashboard;