import { useRef, useState } from 'react';

function DocumentUpload({ onUpload }) {
  const fileInput = useRef(null);
  const [uploading, setUploading] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();
    const file = fileInput.current?.files?.[0];
    if (!file) {
      return;
    }
    setUploading(true);
    try {
      await onUpload(file);
      if (fileInput.current) {
        fileInput.current.value = '';
      }
    } finally {
      setUploading(false);
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <div className="form-group">
        <label htmlFor="case-document">Upload supporting document</label>
        <input id="case-document" ref={fileInput} type="file" />
      </div>
      <button type="submit" disabled={uploading}>
        {uploading ? 'Uploading…' : 'Upload document'}
      </button>
    </form>
  );
}

export default DocumentUpload;
