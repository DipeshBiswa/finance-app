import api from "../api/axios.js";
import { useEffect, useState } from "react";

export default function TransactionCard() {
    const [loading, setLoading] = useState(false);
    const [transactions, setTransactions] = useState([]);

    const refreshData = async () => {
        setLoading(true);
        try {
            // Step 1: Sync (Plaid to DB)
            await api.post('/plaid/sync');
            // Step 2: Get (DB to React)
            const response = await api.get('/transaction');
            setTransactions(response.data);
        } catch (err) {
            // If it's a 401, it means we aren't logged in.
            // We'll just fail silently instead of alerting.
            if (err.response?.status !== 401) {
                console.error("Transaction fetch failed", err);
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        refreshData();
    }, []);

    return (
        <div>
            <h3>Recent Transactions {loading && "(Updating...)"}</h3>
            <ul>
                {/* Array.isArray checks if it's an array before mapping */}
                {Array.isArray(transactions) ? transactions.map((txn) => (
                    <li key={txn.id || txn.plaidTransactionId}>
                        {txn.description}: ${txn.amount}
                    </li>
                )) : (
                    <p>No valid transaction data found.</p>
                )}
            </ul>
            {/* If it's an empty array and not loading, show a message */}
            {Array.isArray(transactions) && transactions.length === 0 && !loading && (
                <p>No transactions yet. Link a bank to get started.</p>
            )}
        </div>
    );
}