import { useState } from 'react';
import {
  fetchCasesForAssignee,
  fetchCaseDetail,
  submitCaseAction,
  uploadDocument
} from './api.js';
import CaseList from './components/CaseList.jsx';
import CaseDetails from './components/CaseDetails.jsx';

function App() {
  const [assignee, setAssignee] = useState('');
  const [cases, setCases] = useState([]);
  const [selectedCaseId, setSelectedCaseId] = useState(null);
  const [selectedCase, setSelectedCase] = useState(null);
  const [loadingCases, setLoadingCases] = useState(false);
  const [loadingDetail, setLoadingDetail] = useState(false);
  const [submittingAction, setSubmittingAction] = useState(false);
  const [error, setError] = useState(null);
  const [statusMessage, setStatusMessage] = useState('');

  async function handleLoadCases(event) {
    event.preventDefault();
    if (!assignee.trim()) {
      setError('Please enter the assignee identifier to load cases.');
      return;
    }
    setError(null);
    setStatusMessage('');
    setLoadingCases(true);
    setSelectedCaseId(null);
    setSelectedCase(null);

    try {
      const caseList = await fetchCasesForAssignee(assignee.trim());
      setCases(caseList);
      if (caseList.length > 0) {
        await selectCase(caseList[0].id);
      }
    } catch (err) {
      console.error(err);
      setError(err.message);
    } finally {
      setLoadingCases(false);
    }
  }

  async function selectCase(caseId) {
    setSelectedCaseId(caseId);
    setLoadingDetail(true);
    setStatusMessage('');
    setError(null);
    try {
      const detail = await fetchCaseDetail(caseId);
      setSelectedCase(detail);
    } catch (err) {
      console.error(err);
      setError(err.message);
    } finally {
      setLoadingDetail(false);
    }
  }

  async function handleUploadDocument(file) {
    if (!selectedCaseId || !file) {
      return;
    }
    setError(null);
    try {
      await uploadDocument(selectedCaseId, file);
      const detail = await fetchCaseDetail(selectedCaseId);
      setSelectedCase(detail);
      setStatusMessage('Document uploaded successfully.');
    } catch (err) {
      console.error(err);
      setError(err.message);
    }
  }

  async function handleSubmitAction(action, comments) {
    if (!selectedCaseId) {
      return;
    }
    setSubmittingAction(true);
    setError(null);
    setStatusMessage('');
    try {
      await submitCaseAction(selectedCaseId, action, comments);
      const detail = await fetchCaseDetail(selectedCaseId);
      setSelectedCase(detail);
      setStatusMessage('Action submitted for approval.');
    } catch (err) {
      console.error(err);
      setError(err.message);
    } finally {
      setSubmittingAction(false);
    }
  }

  return (
    <div className="app-container">
      <header>
        <h1>Case Manager</h1>
        <form className="assignee-form" onSubmit={handleLoadCases}>
          <input
            type="text"
            placeholder="Enter assignee ID"
            value={assignee}
            onChange={(event) => setAssignee(event.target.value)}
          />
          <button type="submit" disabled={loadingCases}>
            {loadingCases ? 'Loading…' : 'Load cases'}
          </button>
        </form>
      </header>

      {error && <div className="error-message">{error}</div>}
      {statusMessage && <div className="status-banner">{statusMessage}</div>}

      <main>
        <section className="case-list">
          <h2>Assigned cases</h2>
          <CaseList
            cases={cases}
            loading={loadingCases}
            selectedCaseId={selectedCaseId}
            onSelectCase={selectCase}
          />
        </section>

        <section className="case-detail">
          <CaseDetails
            loading={loadingDetail}
            caseData={selectedCase}
            onUploadDocument={handleUploadDocument}
            onSubmitAction={handleSubmitAction}
            submittingAction={submittingAction}
          />
        </section>
      </main>
    </div>
  );
}

export default App;
