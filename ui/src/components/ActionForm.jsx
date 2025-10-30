import { useState } from 'react';

const ACTION_OPTIONS = [
  { value: 'SUBMIT', label: 'Submit for approval' },
  { value: 'ESCALATE', label: 'Escalate' },
  { value: 'REQUEST_INFO', label: 'Request more information' }
];

function ActionForm({ onSubmit, submitting }) {
  const [action, setAction] = useState(ACTION_OPTIONS[0].value);
  const [comments, setComments] = useState('');

  async function handleSubmit(event) {
    event.preventDefault();
    await onSubmit(action, comments);
    setComments('');
  }

  return (
    <form onSubmit={handleSubmit}>
      <div className="form-group">
        <label htmlFor="case-action">Action</label>
        <select
          id="case-action"
          value={action}
          onChange={(event) => setAction(event.target.value)}
        >
          {ACTION_OPTIONS.map((item) => (
            <option key={item.value} value={item.value}>
              {item.label}
            </option>
          ))}
        </select>
      </div>
      <div className="form-group">
        <label htmlFor="case-comments">Comments</label>
        <textarea
          id="case-comments"
          rows="4"
          value={comments}
          onChange={(event) => setComments(event.target.value)}
          placeholder="Add any notes or context"
        />
      </div>
      <button type="submit" disabled={submitting}>
        {submitting ? 'Submitting…' : 'Send action'}
      </button>
    </form>
  );
}

export default ActionForm;
