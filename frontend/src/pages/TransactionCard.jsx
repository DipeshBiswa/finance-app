const TransactionCard = ({ transaction }) => {
    const formattedDate = new Date(transaction.date).toLocaleDateString(undefined, {
        month: 'short', day: 'numeric'
    });

    const isExpense = transaction.amount > 0;

    return (
        <div style={{
            display: 'flex',
            justifyContent: 'space-between',
            padding: '16px',
            borderBottom: '1px solid #f0f0f0'
        }}>
            <div>
                {/* Use .description here to match your Java Entity */}
                <div style={{ fontWeight: '500', marginBottom: '4px' }}>{transaction.description}</div>
                <div style={{ fontSize: '0.8rem', color: '#888', textTransform: 'capitalize' }}>
                    {transaction.catagory?.toLowerCase() || 'unclassified'} • {formattedDate}
                </div>
            </div>
            <div style={{
                fontWeight: '600',
                color: isExpense ? '#d93025' : '#188038'
            }}>
                {isExpense ? '-' : '+'} ${Math.abs(transaction.amount).toFixed(2)}
            </div>
        </div>
    );
};

export default TransactionCard;