ALTER TABLE book ADD COLUMN IF NOT EXISTS content TEXT;

CREATE INDEX IF NOT EXISTS idx_book_embedding_hnsw
    ON book USING hnsw (embedding vector_cosine_ops);
