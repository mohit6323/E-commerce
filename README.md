# ShopEase - Modern E-commerce Platform

ShopEase is a full-stack, production-ready e-commerce platform built with a modern technology stack. It provides a seamless shopping experience for customers and robust management tools for administrators.

---

## 🚀 Project Overview

ShopEase enables users to browse products, manage their cart, place orders, and track order history. Admins can manage products, categories, and orders through a secure dashboard. The platform features JWT authentication, role-based access, and a polished, user-friendly UI.

---

## ✨ Features

- Customer registration, login, and profile management
- Secure JWT + refresh token authentication
- Role-based access: CUSTOMER and ADMIN
- Product catalog with categories and search
- Shopping cart and checkout flow
- Order placement and order history
- Admin dashboard for managing products, categories, and orders
- Real-time order status updates (admin)
- Responsive, modern UI/UX
- API documentation via Swagger (Spring Boot)

---

## 🛠️ Tech Stack

- **Frontend:** React, TypeScript, Vite, CSS Modules
- **Backend:** Spring Boot, Java, Spring Security, JPA/Hibernate
- **Database:** H2 (dev), easily switchable to PostgreSQL/MySQL
- **API:** RESTful, OpenAPI/Swagger
- **Testing:** Jest, React Testing Library (frontend); JUnit (backend)

---

## 🏛️ Architecture

- **Monorepo:** `/frontend` (React) and `/src/main/java` (Spring Boot)
- **Clean separation** of concerns: services, controllers, DTOs, entities
- **JWT Auth:** Access + refresh tokens, secure storage, auto-refresh
- **Role-based guards:** Protect admin/customer routes and APIs
- **Reusable UI components** and hooks

---

## 🔒 Authentication & Security

- JWT authentication with refresh token rotation
- Passwords hashed with BCrypt
- Role-based access control (Spring Security)
- Input validation (backend + frontend)
- CSRF protection (where applicable)
- Secure HTTP headers

---

## 📖 API Documentation

- **Swagger UI:**
  - Visit `/swagger-ui.html` (when backend is running)
  - Explore and test all REST endpoints interactively

---

## ⚙️ Setup Instructions

### Prerequisites
- Node.js (v18+ recommended)
- Java 17+
- Maven 3.8+

### Backend (Spring Boot)
1. `cd E-Commerce`
2. `./mvnw spring-boot:run` (or `mvn spring-boot:run`)
3. Access API at `http://localhost:8080/api`

### Frontend (React)
1. `cd E-Commerce/frontend`
2. `npm install`
3. `npm run dev`
4. Visit `http://localhost:5173`

---

## 🚀 Deployment Guide

- **Backend:** Deploy as a standard Spring Boot JAR or WAR to any Java server (Heroku, AWS, Azure, etc.)
- **Frontend:** Deploy static build (`npm run build`) to Vercel, Netlify, or any static host
- **Environment variables:** Configure API base URLs and secrets as needed

---

## 🛣️ Future Improvements

- Add payment gateway integration (Stripe, PayPal)
- Add product reviews and ratings
- Implement email notifications
- Add multi-language support
- Improve accessibility (a11y)
- Add analytics/dashboard for admins
- Dockerize for easy deployment

---

## 📝 Conclusion

ShopEase demonstrates best practices in full-stack development, security, and user experience. It is ready for real-world deployment and makes an excellent portfolio project for showcasing modern web engineering skills.

---

**Questions or feedback?** Open an issue or contact the maintainer.

