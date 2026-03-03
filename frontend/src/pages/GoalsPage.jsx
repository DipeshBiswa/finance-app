import { useState, useEffect } from "react";
import api from "../api/axios.js";
import "./GoalsPage.css";

const CATEGORY_OPTIONS = [
    { value: "FOODDINING",     label: "Food & Dining" },
    { value: "SHOPPING",       label: "Shopping" },
    { value: "TRANSPORTATION", label: "Transportation" },
    { value: "RENT_AND_BILLS", label: "Rent & Bills" },
    { value: "INCOME",         label: "Income" },
    { value: "OTHER",          label: "Other" },
];

const CATEGORY_COLORS = {
    FOODDINING:      "#f97316",
    SHOPPING:        "#8b5cf6",
    TRANSPORTATION:  "#3b82f6",
    RENT_AND_BILLS:  "#ef4444",
    INCOME:          "#22c55e",
    OTHER:           "#6b7280",
};

function ProgressBar({ current, target, color }) {
    const raw = target > 0 ? (current / target) * 100 : 0;
    const pct = Math.min(raw, 100);
    const over = raw > 100;
    return (
        <div className="gp-bar-track">
            <div
                className="gp-bar-fill"
                style={{
                    width: `${pct}%`,
                    background: over
                        ? "linear-gradient(90deg, #22c55e, #16a34a)"
                        : `linear-gradient(90deg, ${color}99, ${color})`,
                }}
            />
        </div>
    );
}

export default function GoalsPage() {
    const [goals, setGoals]       = useState([]);
    const [loading, setLoading]   = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [error, setError]       = useState("");
    const [saving, setSaving]     = useState(false);

    const [form, setForm] = useState({
        title: "",
        description: "",
        target_amount: "",
        category: "FOODDINING",
    });

    useEffect(() => {
        if (!localStorage.getItem("token")) {
            window.location.href = "/";
            return;
        }
        fetchGoals();
    }, []);

    const fetchGoals = async () => {
        setLoading(true);
        try {
            const res = await api.get("/goal");
            setGoals(Array.isArray(res.data) ? res.data : []);
        } catch (err) {
            if (err.response?.status !== 401) console.error("Failed to load goals", err);
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        if (!form.title.trim()) { setError("Title is required."); return; }
        if (!form.target_amount || isNaN(form.target_amount) || Number(form.target_amount) <= 0) {
            setError("Enter a valid target amount."); return;
        }
        setSaving(true);
        try {
            await api.post("/goal/create", {
                title: form.title,
                description: form.description,
                target_amount: parseFloat(form.target_amount),
                category: form.category,
            });
            setForm({ title: "", description: "", target_amount: "", category: "FOODDINING" });
            setShowForm(false);
            fetchGoals();
        } catch (err) {
            setError(err.response?.data?.message || "Failed to create goal.");
        } finally {
            setSaving(false);
        }
    };

    const handleDelete = async (id) => {
        try {
            await api.delete(`/goal/${id}`);
            setGoals(prev => prev.filter(g => g.id !== id));
        } catch (err) {
            console.error("Failed to delete goal", err);
        }
    };

    return (
        <div className="gp-wrapper">

            {/* ── Page header ── */}
            <div className="gp-header">
                <div>
                    <h1 className="gp-title">Spending Goals</h1>
                    <p className="gp-subtitle">Track your spending against category targets</p>
                </div>
                <button className="gp-add-btn" onClick={() => setShowForm(v => !v)}>
                    {showForm ? "✕ Cancel" : "+ New Goal"}
                </button>
            </div>

            {/* ── Create form ── */}
            {showForm && (
                <div className="gp-form-card">
                    <h2 className="gp-form-title">Create a Goal</h2>
                    {error && <div className="gp-error">{error}</div>}
                    <form onSubmit={handleSubmit} className="gp-form">
                        <div className="gp-form-row">
                            <div className="gp-field">
                                <label className="gp-label">Title</label>
                                <input
                                    className="gp-input"
                                    name="title"
                                    value={form.title}
                                    onChange={handleChange}
                                    placeholder="e.g. Reduce dining out"
                                    required
                                />
                            </div>
                            <div className="gp-field">
                                <label className="gp-label">Target Amount ($)</label>
                                <input
                                    className="gp-input"
                                    name="target_amount"
                                    type="number"
                                    min="1"
                                    step="0.01"
                                    value={form.target_amount}
                                    onChange={handleChange}
                                    placeholder="e.g. 300"
                                    required
                                />
                            </div>
                        </div>
                        <div className="gp-form-row">
                            <div className="gp-field">
                                <label className="gp-label">Category</label>
                                <select
                                    className="gp-input"
                                    name="category"
                                    value={form.category}
                                    onChange={handleChange}
                                >
                                    {CATEGORY_OPTIONS.map(opt => (
                                        <option key={opt.value} value={opt.value}>{opt.label}</option>
                                    ))}
                                </select>
                            </div>
                            <div className="gp-field">
                                <label className="gp-label">Description <span className="gp-optional">(optional)</span></label>
                                <input
                                    className="gp-input"
                                    name="description"
                                    value={form.description}
                                    onChange={handleChange}
                                    placeholder="e.g. Keep food spending under $300/mo"
                                />
                            </div>
                        </div>
                        <button className="gp-submit-btn" type="submit" disabled={saving}>
                            {saving ? "Saving…" : "Create Goal"}
                        </button>
                    </form>
                </div>
            )}

            {/* ── Goals list ── */}
            {loading ? (
                <div className="gp-loading">
                    <div className="gp-spinner" />
                    <p>Loading goals…</p>
                </div>
            ) : goals.length === 0 ? (
                <div className="gp-empty">
                    <p>No goals yet. Create one to start tracking your spending.</p>
                </div>
            ) : (
                <div className="gp-grid">
                    {goals.map(goal => {
                        const color   = CATEGORY_COLORS[goal.category] || "#6b7280";
                        const catOpt  = CATEGORY_OPTIONS.find(o => o.value === goal.category);
                        const current = parseFloat(goal.current_amount || 0);
                        const target  = parseFloat(goal.target_amount || 1);
                        const pct     = Math.min((current / target) * 100, 100).toFixed(0);
                        const over    = current >= target;

                        return (
                            <div key={goal.id} className="gp-card" style={{ borderTopColor: color }}>
                                <div className="gp-card-top">
                                    <div>
                                        <p className="gp-card-category" style={{ color }}>
                                            {catOpt?.label || goal.category}
                                        </p>
                                        <h3 className="gp-card-title">{goal.title}</h3>
                                        {goal.description && (
                                            <p className="gp-card-desc">{goal.description}</p>
                                        )}
                                    </div>
                                    <button
                                        className="gp-delete-btn"
                                        onClick={() => handleDelete(goal.id)}
                                        title="Delete goal"
                                    >✕</button>
                                </div>

                                <ProgressBar current={current} target={target} color={color} />

                                <div className="gp-card-stats">
                                    <div>
                                        <p className="gp-stat-label">Spent</p>
                                        <p className="gp-stat-value" style={{ color: over ? "#22c55e" : "#3d2b1f" }}>
                                            ${current.toFixed(2)}
                                        </p>
                                    </div>
                                    <div className="gp-stat-pct" style={{ color }}>
                                        {pct}%
                                    </div>
                                    <div style={{ textAlign: "right" }}>
                                        <p className="gp-stat-label">Target</p>
                                        <p className="gp-stat-value">${target.toFixed(2)}</p>
                                    </div>
                                </div>

                                {over && (
                                    <div className="gp-reached" style={{ background: color + "18", color }}>
                                        Target reached!
                                    </div>
                                )}
                            </div>
                        );
                    })}
                </div>
            )}
        </div>
    );
}
