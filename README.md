Overview
personal finance application designed to bridge the gap between banking data and actionable insights. By integrating the Plaid API, users can securely sync their real-world transactions into a custom dashboard. The app features a stateless JWT authentication system and an AI Chatbot to provide natural language feedback on spending habits.

🛠️ Tech Stack
Frontend: React.js,

Backend: Java Spring Boot, Spring Security, Spring Data JPA

Security: JSON Web Tokens (JWT), BCrypt Encryption

Database: PostgreSQL

Integrations: Plaid API (Banking), OpenAI API (Financial AI Advisor) (SOON!!)

✨ Key Features
Secure Authentication: Stateless login/logout flow using JWT stored in secure HttpOnly cookies.

Live Bank Sync: Integration with Plaid Link to connect sandbox and production bank accounts.

Transaction Pipeline: Automated fetching and categorization of transaction data via /plaid/sync.

AI Financial Advisor: An integrated chatbot that analyzes transaction history to suggest budget improvements.

Responsive Dashboard: Visualized spending data and transaction history with real-time updates.

🏗️ Architecture & Security logic
The project utilizes a Spring Security Filter Chain to manage request authorization.

Authentication: Users authenticate via /api/auth/login. Upon success, a JWT is generated and returned.

Authorization: Subsequent requests (to Plaid or Transaction endpoints) include the JWT. The backend validates the token before populating the Principal object.

Data Flow:

React → Spring Boot (JWT Validated)

Spring Boot → Plaid API (Access Token Exchange)

Plaid API → Spring Boot (Transaction JSON)

Spring Boot → PostgreSQL (Persistence)

📅 Project Roadmap
[x] Initial Spring Boot & React Setup

[x] JWT Authentication & Spring Security Integration

[x] Plaid Link Token & Access Token Exchange

[x] Transaction Syncing & Database Schema Design

[ ] In Progress: AI Chatbot integration for spending analysis

[x] Upcoming: Data visualization with Chart.js

[ ] Upcoming: Multi-account support
