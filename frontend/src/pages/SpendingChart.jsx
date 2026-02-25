import { useState, useEffect } from "react";
import {
    BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell,
    PieChart, Pie, Legend,
} from "recharts";
import api from "../api/axios.js";
import "./SpendingChart.css";

const MONTH_NAMES = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];

const CATEGORY_CONFIG = {
    FOODDINING:      { label: "Food & Dining",  color: "#f97316" },
    SHOPPING:        { label: "Shopping",        color: "#8b5cf6" },
    TRANSPORTATION:  { label: "Transportation",  color: "#3b82f6" },
    RENT_AND_BILLS:  { label: "Rent & Bills",    color: "#ef4444" },
    INCOME:          { label: "Income",          color: "#22c55e" },
    OTHER:           { label: "Other",           color: "#6b7280" },
};

const CustomBarTooltip = ({ active, payload, label }) => {
    if (active && payload?.length) {
        return (
            <div className="sc-tooltip">
                <p className="sc-tooltip-label">{label}</p>
                <p className="sc-tooltip-value">${parseFloat(payload[0].value).toFixed(2)}</p>
            </div>
        );
    }
    return null;
};

const CustomPieTooltip = ({ active, payload }) => {
    if (active && payload?.length) {
        return (
            <div className="sc-tooltip">
                <p className="sc-tooltip-label">{payload[0].name}</p>
                <p className="sc-tooltip-value">${parseFloat(payload[0].value).toFixed(2)}</p>
            </div>
        );
    }
    return null;
};

export default function SpendingChart({ onClose }) {
    const currentYear = new Date().getFullYear();
    const [year, setYear]           = useState(currentYear);
    const [tab, setTab]             = useState("monthly"); // "monthly" | "category"
    const [monthlyData, setMonthly] = useState([]);
    const [catData, setCat]         = useState([]);
    const [loading, setLoading]     = useState(false);

    useEffect(() => {
        fetchData();
    }, [year]);

    const fetchData = async () => {
        setLoading(true);
        try {
            const [monthRes, catRes] = await Promise.all([
                api.get(`/transaction/summary?year=${year}`),
                api.get("/transaction/summary/category"),
            ]);

            // Build full 12-month array, filling missing months with 0
            const raw = monthRes.data || {};
            const built = MONTH_NAMES.map((name, i) => ({
                month: name,
                amount: parseFloat(raw[i + 1] ?? 0),
            }));
            setMonthly(built);

            // Build category array
            const catRaw = catRes.data || {};
            const catBuilt = Object.entries(catRaw)
                .filter(([, v]) => parseFloat(v) > 0)
                .map(([key, val]) => ({
                    name: CATEGORY_CONFIG[key]?.label || key,
                    value: parseFloat(val),
                    color: CATEGORY_CONFIG[key]?.color || "#6b7280",
                }))
                .sort((a, b) => b.value - a.value);
            setCat(catBuilt);
        } catch (err) {
            console.error("Chart fetch failed", err);
        } finally {
            setLoading(false);
        }
    };

    const yearOptions = Array.from({ length: 5 }, (_, i) => currentYear - i);

    // Close on backdrop click
    const handleBackdrop = (e) => {
        if (e.target === e.currentTarget) onClose();
    };

    return (
        <div className="sc-backdrop" onClick={handleBackdrop}>
            <div className="sc-modal">
                {/* Header */}
                <div className="sc-modal-header">
                    <div>
                        <h2 className="sc-modal-title">Spending Charts</h2>
                        <p className="sc-modal-subtitle">Visualise where your money goes</p>
                    </div>
                    <button className="sc-close-btn" onClick={onClose}>✕</button>
                </div>

                {/* Tab + Year bar */}
                <div className="sc-controls">
                    <div className="sc-tabs">
                        <button
                            className={`sc-tab ${tab === "monthly" ? "active" : ""}`}
                            onClick={() => setTab("monthly")}
                        >📅 Monthly</button>
                        <button
                            className={`sc-tab ${tab === "category" ? "active" : ""}`}
                            onClick={() => setTab("category")}
                        >🥧 By Category</button>
                    </div>
                    {tab === "monthly" && (
                        <select
                            className="sc-year-select"
                            value={year}
                            onChange={e => setYear(Number(e.target.value))}
                        >
                            {yearOptions.map(y => (
                                <option key={y} value={y}>{y}</option>
                            ))}
                        </select>
                    )}
                </div>

                {/* Chart area */}
                <div className="sc-chart-area">
                    {loading ? (
                        <div className="sc-loading">
                            <div className="sc-spinner" />
                            <p>Loading…</p>
                        </div>
                    ) : tab === "monthly" ? (
                        <>
                            <p className="sc-chart-heading">Total spending per month — {year}</p>
                            <ResponsiveContainer width="100%" height={280}>
                                <BarChart data={monthlyData} margin={{ top: 8, right: 16, left: 0, bottom: 0 }}>
                                    <CartesianGrid strokeDasharray="3 3" stroke="#ede7df" />
                                    <XAxis
                                        dataKey="month"
                                        tick={{ fontSize: 12, fill: "#a89584" }}
                                        axisLine={false}
                                        tickLine={false}
                                    />
                                    <YAxis
                                        tick={{ fontSize: 11, fill: "#a89584" }}
                                        axisLine={false}
                                        tickLine={false}
                                        tickFormatter={v => `$${v}`}
                                        width={55}
                                    />
                                    <Tooltip content={<CustomBarTooltip />} cursor={{ fill: "#f0e9df" }} />
                                    <Bar dataKey="amount" radius={[6, 6, 0, 0]} maxBarSize={40}>
                                        {monthlyData.map((entry, i) => (
                                            <Cell
                                                key={i}
                                                fill={entry.amount > 0 ? "#a0775a" : "#e8ddd0"}
                                            />
                                        ))}
                                    </Bar>
                                </BarChart>
                            </ResponsiveContainer>
                        </>
                    ) : catData.length === 0 ? (
                        <div className="sc-empty">
                            <span>🥧</span>
                            <p>No category data yet. Link a bank and sync transactions.</p>
                        </div>
                    ) : (
                        <>
                            <p className="sc-chart-heading">Spending breakdown by category</p>
                            <ResponsiveContainer width="100%" height={280}>
                                <PieChart>
                                    <Pie
                                        data={catData}
                                        dataKey="value"
                                        nameKey="name"
                                        cx="50%"
                                        cy="50%"
                                        innerRadius={60}
                                        outerRadius={100}
                                        paddingAngle={3}
                                    >
                                        {catData.map((entry, i) => (
                                            <Cell key={i} fill={entry.color} />
                                        ))}
                                    </Pie>
                                    <Tooltip content={<CustomPieTooltip />} />
                                    <Legend
                                        formatter={(value) => (
                                            <span style={{ fontSize: 12, color: "#5c4033" }}>{value}</span>
                                        )}
                                    />
                                </PieChart>
                            </ResponsiveContainer>

                            {/* Category breakdown table */}
                            <div className="sc-cat-table">
                                {catData.map((entry, i) => (
                                    <div key={i} className="sc-cat-row">
                                        <div className="sc-cat-dot" style={{ background: entry.color }} />
                                        <span className="sc-cat-name">{entry.name}</span>
                                        <span className="sc-cat-amount">${entry.value.toFixed(2)}</span>
                                    </div>
                                ))}
                            </div>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}
