import ActionForm from './ActionForm.jsx';
import DocumentUpload from './DocumentUpload.jsx';

function CaseDetails({ loading, caseData, onUploadDocument, onSubmitAction, submittingAction }) {
  if (loading) {
    return <p>Loading case details…</p>;
  }

  if (!caseData) {
    return <div className="empty-state">Select a case to review its details.</div>;
  }

  return (
    <div>
      <header>
        <div>
          <h2>{caseData.title}</h2>
          <p>{caseData.description}</p>
        </div>
        <span className="badge">{caseData.status}</span>
      </header>

      <section className="case-meta">
        <div>
          <strong>Assignee</strong>
          <p>{caseData.assignee}</p>
        </div>
        <div>
          <strong>Created</strong>
          <p>{new Date(caseData.createdAt).toLocaleString()}</p>
        </div>
        <div>
          <strong>Last updated</strong>
          <p>{new Date(caseData.updatedAt).toLocaleString()}</p>
        </div>
      </section>

      <section className="documents">
        <h3>Supporting documents</h3>
        {caseData.documents && caseData.documents.length > 0 ? (
          <ul>
            {caseData.documents.map((document) => (
              <li key={document.id}>
                <strong>{document.fileName}</strong>
                <div>{new Date(document.uploadedAt).toLocaleString()}</div>
                <div>{document.contentType}</div>
              </li>
            ))}
          </ul>
        ) : (
          <p>No documents uploaded yet.</p>
        )}
        <DocumentUpload onUpload={onUploadDocument} />
      </section>

      <section>
        <h3>Submit action</h3>
        <ActionForm onSubmit={onSubmitAction} submitting={submittingAction} />
      </section>
    </div>
  );
}

export default CaseDetails;
