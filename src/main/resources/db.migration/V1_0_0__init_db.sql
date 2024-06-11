CREATE TABLE BILLS (
   id SERIAL PRIMARY KEY,
   valor DECIMAL(10, 2),
   descricao TEXT,
   data_vencimento DATE,
   data_pagamento DATE,
   status VARCHAR(20),
   situacao VARCHAR(20),
   data_inclusao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);