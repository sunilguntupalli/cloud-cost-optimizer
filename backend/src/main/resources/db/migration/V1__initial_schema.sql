CREATE TABLE cloud_resource (
  id BIGSERIAL PRIMARY KEY,
  azure_resource_id VARCHAR(500),
  name VARCHAR(255) NOT NULL,
  resource_type VARCHAR(255) NOT NULL,
  region VARCHAR(255),
  status VARCHAR(100),
  utilization_percent INTEGER,
  monthly_cost NUMERIC(19,2) NOT NULL DEFAULT 0
);

CREATE TABLE recommendation (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  category VARCHAR(100) NOT NULL,
  resource_name VARCHAR(255),
  impact VARCHAR(50),
  status VARCHAR(50) NOT NULL,
  monthly_savings NUMERIC(19,2) NOT NULL DEFAULT 0,
  description VARCHAR(1000)
);

CREATE TABLE cost_snapshot (
  id BIGSERIAL PRIMARY KEY,
  observed_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
  period VARCHAR(20) NOT NULL,
  currency VARCHAR(10) NOT NULL,
  source VARCHAR(100) NOT NULL,
  amount NUMERIC(19,2) NOT NULL DEFAULT 0
);

CREATE INDEX idx_recommendation_status ON recommendation(status);
CREATE INDEX idx_cost_snapshot_observed_at ON cost_snapshot(observed_at DESC);
