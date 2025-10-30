const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '';

async function handleResponse(response) {
  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || 'Request failed');
  }
  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    return response.json();
  }
  return null;
}

export async function fetchCasesForAssignee(assignee) {
  const response = await fetch(`${API_BASE_URL}/api/cases/assigned/${encodeURIComponent(assignee)}`);
  return handleResponse(response);
}

export async function fetchCaseDetail(caseId) {
  const response = await fetch(`${API_BASE_URL}/api/cases/${caseId}`);
  return handleResponse(response);
}

export async function uploadDocument(caseId, file) {
  const formData = new FormData();
  formData.append('file', file);
  const response = await fetch(`${API_BASE_URL}/api/cases/${caseId}/documents`, {
    method: 'POST',
    body: formData
  });
  return handleResponse(response);
}

export async function submitCaseAction(caseId, action, comments) {
  const response = await fetch(`${API_BASE_URL}/api/cases/${caseId}/actions`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ action, comments })
  });
  return handleResponse(response);
}
