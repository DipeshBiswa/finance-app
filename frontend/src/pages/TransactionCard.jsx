import api from "../api/axios.js";
import {useEffect, useState} from "react";
export default function TransactionCard(){
    const [syncedTransaction, setSynchTransaction] = useState(null);
    const [transactions, setTransactions] = useState([]);
    const refreshData = async () => {
        setSynchTransaction(true);
        try {
            await api.post('/plaid/sync');
            const response = await api.get('/transaction');
            setTransactions(response.data);
        } catch (err) {
            console.error("Sync failed", err);
            alert("Failed to sync new transactions.");
        } finally {
            setSynchTransaction(false);
        }
    };
    useEffect(() => {
        refreshData();
    },[]);
    return(
        <div>

        </div>
    );
}