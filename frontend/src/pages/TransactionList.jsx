import React, { useEffect, useState } from 'react';
import api from '../api/axios';
import TransactionCard from './TransactionCard';

const TransactionList = () => {
    const [transactions, setTransactions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [syncing, setSyncing] = useState(false);

    const fetchTransactions = async () => {
        try {
            const response = await api.get('/plaid/transactions');
            // Sort by date descending so newest are at the top
            const sorted = response.data.sort((a, b) => new Date(b.date) - new Date(a.date));
            setTransactions(sorted);
        } catch (err) {
            console.error("Failed to fetch transactions", err);
        } finally {
            setLoading(false);
        }
    };

    const handleSync = async () => {
        setSyncing(true);
        try {
            await api.post('/plaid/sync');
            await fetchTransactions(); // Refresh the list after sync finishes
        } catch (err) {
            console.error("Sync failed", err);
            alert("Failed to sync new transactions.");
        } finally {
            setSyncing(false);
        }
    };

    useEffect(() => {
        fetchTransactions();
    }, []);

    if (loading) return <p style={{ textAlign: 'center' }}>Loading your financial data...</p>;

    return (
        <div style={{ maxWidth: '600px', margin: '20px auto', fontFamily: 'sans-serif' }}>
            <div style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                marginBottom: '15px'
            }}>
                <h3 style={{ margin: 0 }}>Recent Activity</h3>
                <button
                    onClick={handleSync}
                    disabled={syncing}
                    style={{
                        padding: '8px 16px',
                        backgroundColor: '#007bff',
                        color: 'white',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: syncing ? 'not-allowed' : 'pointer'
                    }}
                >
                    {syncing ? "Syncing..." : "Refresh"}
                </button>
            </div>

            <div style={{
                borderRadius: '12px',
                boxShadow: '0 4px 12px rgba(0,0,0,0.08)',
                overflow: 'hidden',
                backgroundColor: '#fff',
                border: '1px solid #eaeaea'
            }}>
                {transactions.length === 0 ? (
                    <div style={{ padding: '40px', textAlign: 'center', color: '#666' }}>
                        <p>No transactions found.</p>
                        <p style={{ fontSize: '0.9rem' }}>Click "Refresh" to pull data from Plaid.</p>
                    </div>
                ) : (
                    transactions.map((txn) => (
                        <TransactionCard key={txn.plaidTransactionId || txn.id} transaction={txn} />
                    ))
                )}
            </div>
        </div>
    );
};

export default TransactionList;