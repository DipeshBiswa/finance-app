import api from "../api/axios.js";
import { useEffect, useState } from "react";
import "./TransactionCard.css";
import SpendingChart from "./SpendingChart.jsx";

const CATEGORY_CONFIG = {
    FOODDINING:      { label: "Food & Dining",     color: "#f97316" },
    SHOPPING:        { label: "Shopping",           color: "#8b5cf6" },
    TRANSPORTATION:  { label: "Transportation",     color: "#3b82f6" },
    RENT_AND_BILLS:  { label: "Rent & Bills",       color: "#ef4444" },
    INCOME:          { label: "Income",             color: "#22c55e" },
    OTHER:           { label: "Other",              color: "#6b7280" },
};

function formatDate(dateStr) {
    if (!dateStr) return "";
    const d = new Date(dateStr);
    return d.toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" });
}

function formatAmount(amount, category) {
    const num = parseFloat(amount);
    const isIncome = category === "INCOME";
    return (isIncome ? "+" : "-") + "$" + Math.abs(num).toFixed(2);
}

export default function TransactionCard() {
    const [loading, setLoading]           = useState(false);
    const [syncing, setSyncing]           = useState(false);
    const [transactions, setTransactions] = useState([]);
    const [monthTotal, setMonthTotal]     = useState(null);
    const [catSummary, setCatSummary]     = useState({});
    const [activeFilter, setActiveFilter] = useState("ALL");
    const [showChart, setShowChart]       = useState(false);

    const refreshData = async () => {
        setSyncing(true);
        try {
            await api.post('/plaid/sync');
        } catch (err) {
            if (err.response?.status !== 401) console.error("Sync failed", err);
        } finally {
            setSyncing(false);
        }

        setLoading(true);
        try {
            const [txnRes, monthRes, catRes] = await Promise.all([
                api.get('/transaction'),
                api.get('/transaction/summary/month'),
                api.get('/transaction/summary/category'),
            ]);
            setTransactions(Array.isArray(txnRes.data) ? txnRes.data : []);
            setMonthTotal(monthRes.data);
            setCatSummary(catRes.data || {});
        } catch (err) {
            if (err.response?.status !== 401) console.error("Fetch failed", err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { refreshData(); }, []);

    const categories = ["ALL", ...Object.keys(CATEGORY_CONFIG)];

    const filtered = activeFilter === "ALL"
        ? transactions
        : transactions.filter(t => t.catagory === activeFilter);

    // Group by date
    const grouped = filtered.reduce((acc, txn) => {
        const day = formatDate(txn.date);
        if (!acc[day]) acc[day] = [];
        acc[day].push(txn);
        return acc;
    }, {});

    const sortedDays = Object.keys(grouped).sort(
        (a, b) => new Date(b) - new Date(a)
    );

    return (
        <div className="tc-wrapper">
            {showChart && <SpendingChart onClose={() => setShowChart(false)} />}
            {/* ── Header ── */}
            <div className="tc-header">
                <div>
                    <h2 className="tc-title">Transactions</h2>
                    <p className="tc-subtitle">
                        {syncing ? "Syncing with bank…" : `${transactions.length} transactions`}
                    </p>
                </div>
                <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
                    <button
                        onClick={() => setShowChart(true)}
                        className="tc-chart-btn"
                    >
                        Charts
                    </button>
                    {monthTotal != null && (
                        <div className="tc-month-total">
                            <span className="tc-month-label">Spent this month</span>
                            <span className="tc-month-amount">${parseFloat(monthTotal).toFixed(2)}</span>
                        </div>
                    )}
                </div>
            </div>

            {/* ── Category summary pills ── */}
            {Object.keys(catSummary).length > 0 && (
                <div className="tc-cat-summary">
                    {Object.entries(catSummary).map(([cat, total]) => {
                        const cfg = CATEGORY_CONFIG[cat] || CATEGORY_CONFIG.OTHER;
                        return (
                            <div
                                key={cat}
                                className="tc-cat-pill"
                                style={{ borderColor: cfg.color, background: cfg.color + "18" }}
                            >
                                <span className="tc-cat-pill-label">{cfg.label}</span>
                                <span className="tc-cat-pill-amount" style={{ color: cfg.color }}>
                                    ${parseFloat(total).toFixed(2)}
                                </span>
                            </div>
                        );
                    })}
                </div>
            )}

            {/* ── Filter bar ── */}
            <div className="tc-filter-bar">
                {categories.map(cat => {
                    const cfg = CATEGORY_CONFIG[cat];
                    return (
                        <button
                            key={cat}
                            onClick={() => setActiveFilter(cat)}
                            className={`tc-filter-btn ${activeFilter === cat ? "active" : ""}`}
                            style={activeFilter === cat && cfg
                                ? { background: cfg.color, borderColor: cfg.color, color: "#fff" }
                                : {}}
                        >
                            {cfg ? cfg.label : "All"}
                        </button>
                    );
                })}
            </div>

            {/* ── Transaction list ── */}
            {loading ? (
                <div className="tc-loading">
                    <div className="tc-spinner" />
                    <p>Loading transactions…</p>
                </div>
            ) : filtered.length === 0 ? (
                <div className="tc-empty">
                    <p>No transactions yet. Link a bank to get started.</p>
                </div>
            ) : (
                <div className="tc-list">
                    {sortedDays.map(day => (
                        <div key={day} className="tc-day-group">
                            <p className="tc-day-label">{day}</p>
                            {grouped[day].map(txn => {
                                const cfg = CATEGORY_CONFIG[txn.catagory] || CATEGORY_CONFIG.OTHER;
                                const isIncome = txn.catagory === "INCOME";
                                return (
                                    <div key={txn.id || txn.plaidTransactionId} className="tc-row">
                                        <div
                                            className="tc-row-icon"
                                            style={{ background: cfg.color + "22", color: cfg.color }}
                                        >
                                            {cfg.label[0]}
                                        </div>
                                        <div className="tc-row-info">
                                            <p className="tc-row-desc">
                                                {txn.description || "Unknown transaction"}
                                            </p>
                                            <span
                                                className="tc-row-badge"
                                                style={{ background: cfg.color + "22", color: cfg.color }}
                                            >
                                                {cfg.label}
                                            </span>
                                        </div>
                                        <p
                                            className="tc-row-amount"
                                            style={{ color: isIncome ? "#22c55e" : "#ef4444" }}
                                        >
                                            {formatAmount(txn.amount, txn.catagory)}
                                        </p>
                                    </div>
                                );
                            })}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}