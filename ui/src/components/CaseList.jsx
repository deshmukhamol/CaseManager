function CaseList({ cases, loading, selectedCaseId, onSelectCase }) {
  if (loading) {
    return <p>Loading cases…</p>;
  }

  if (!cases || cases.length === 0) {
    return <div className="empty-state">No cases assigned yet.</div>;
  }

  return (
    <ul>
      {cases.map((item) => (
        <li
          key={item.id}
          className={item.id === selectedCaseId ? 'selected' : ''}
          onClick={() => onSelectCase(item.id)}
        >
          <h3>{item.title}</h3>
          <p>{item.description}</p>
          <div className="case-meta">
            <span className="badge">{item.status}</span>
            <small>Updated {new Date(item.updatedAt).toLocaleString()}</small>
          </div>
        </li>
      ))}
    </ul>
  );
}

export default CaseList;
