# KitchenSink User Journey Flow

## Table of Contents
1. [Overview](#overview)
2. [User Personas](#user-personas)
3. [Entry Points](#entry-points)
4. [Authentication Journey](#authentication-journey)
5. [Admin User Journey](#admin-user-journey)
6. [Regular User Journey](#regular-user-journey)
7. [Registration Flows](#registration-flows)
8. [Error Scenarios](#error-scenarios)
9. [Session Management](#session-management)
10. [Logout Flow](#logout-flow)

## Overview

This document outlines the complete user journey flow for the KitchenSink application, covering all possible user interactions, authentication paths, and system responses. The application supports two main user types: **Admin Users** and **Regular Users**, each with different access levels and capabilities.

## User Personas

### 👑 Admin User
- **Access Level**: Full system access
- **Capabilities**: User management, system monitoring, data administration
- **Default Credentials**: admin@admin.com / admin123
- **Dashboard**: Admin dashboard with member management interface

### 👤 Regular User
- **Access Level**: Limited access to personal profile
- **Capabilities**: View and update personal information
- **Default Credentials**: user@user.com / user123
- **Dashboard**: User profile page

## Entry Points

### 1. Root URL Access (`/`)
- **Action**: Automatic redirect to JWT login page
- **Destination**: `/jwt-login`
- **Purpose**: Ensures users always start at the authentication page

### 2. Direct Login Page Access (`/jwt-login`)
- **Action**: Display login form
- **Components**: Email, password fields, login button, signup link
- **Purpose**: Primary authentication entry point

### 3. Direct Signup Page Access (`/jwt-signup`)
- **Action**: Display registration form
- **Components**: Name, email, password, confirm password fields
- **Purpose**: New user registration

## Authentication Journey

### Step 1: Initial Access
```
User visits application → Root redirect → JWT Login Page
```

**User Experience**:
- User opens browser and navigates to `http://localhost:8080`
- System automatically redirects to `/jwt-login`
- User sees clean login interface with form fields

### Step 2: Login Form Interaction
```
User enters credentials → Form validation → Authentication request
```

**User Actions**:
1. **Email Entry**: User types email address
2. **Password Entry**: User types password
3. **Form Validation**: Client-side validation ensures required fields
4. **Submit**: User clicks "Login" button

**System Response**:
- **Valid Credentials**: Redirect to appropriate dashboard
- **Invalid Credentials**: Display error message, stay on login page
- **Missing Fields**: Highlight required fields

### Step 3: Authentication Processing
```
Login request → JWT Token generation → Session creation → Dashboard redirect
```

**System Flow**:
1. **Credential Verification**: System checks email/password against database
2. **JWT Generation**: Creates access and refresh tokens
3. **Session Setup**: Stores tokens in session/cookies
4. **Role Determination**: Identifies user role (ADMIN/USER)
5. **Redirect**: Sends user to appropriate dashboard

## Admin User Journey

### Dashboard Access (`/admin/home`)
```
Admin login → Admin dashboard → Member management interface
```

**Dashboard Features**:
- **Member List**: View all registered users
- **Add Member**: Registration form for new users
- **User Management**: Admin can register users with different roles
- **Session Information**: Display current user and session status

### Admin Registration Flow
```
Admin fills form → Strategy pattern → User creation → Dashboard update
```

**Process Steps**:
1. **Form Completion**: Admin fills registration form with user details
2. **Strategy Selection**: System uses `AdminRegistrationStrategy`
3. **User Creation**: New user account created in database
4. **Role Assignment**: Admin can assign ADMIN or USER role
5. **Dashboard Update**: Member list refreshes with new user

### Admin Capabilities
- **View All Members**: Complete list of registered users
- **Register New Users**: Create accounts for other users
- **Role Management**: Assign different roles to users
- **System Monitoring**: Access to application health and status

## Regular User Journey

### Profile Access (`/user-profile`)
```
User login → User profile page → Personal information display
```

**Profile Features**:
- **Personal Information**: Display user's name, email, role
- **Session Tokens**: Show current authentication status
- **Profile Management**: View personal details

### User Registration Flow
```
User fills signup form → Strategy pattern → Account creation → Login redirect
```

**Process Steps**:
1. **Form Completion**: User fills registration form
2. **Strategy Selection**: System uses `UserRegistrationStrategy`
3. **Account Creation**: New user account created
4. **Default Role**: Automatically assigned USER role
5. **Login Redirect**: User redirected to login page

### User Capabilities
- **View Profile**: Access personal information
- **Session Management**: View current session status
- **Limited Access**: Cannot access admin features

## Registration Flows

### Public User Registration (`/jwt-signup`)
```
New user → Signup form → UserRegistrationStrategy → Account creation → Login page
```

**User Experience**:
1. **Form Access**: User clicks "Sign Up" link from login page
2. **Information Entry**: User provides name, email, password
3. **Validation**: System validates email format and password strength
4. **Account Creation**: New account created with USER role
5. **Success Redirect**: User redirected to login page

**Strategy Pattern Implementation**:
- **Context**: `RegistrationContext` receives "register" source
- **Strategy**: `UserRegistrationStrategy` handles the registration
- **Logic**: Checks for existing users, handles password updates
- **Result**: Redirects to login page with success message

### Admin-Initiated Registration (`/admin/home`)
```
Admin → Registration form → AdminRegistrationStrategy → User creation → Dashboard
```

**Admin Experience**:
1. **Form Access**: Admin uses registration form on dashboard
2. **User Details**: Admin enters user information
3. **Role Selection**: Admin can choose USER or ADMIN role
4. **Account Creation**: New account created with selected role
5. **Dashboard Update**: Member list refreshes

**Strategy Pattern Implementation**:
- **Context**: `RegistrationContext` receives "index" source
- **Strategy**: `AdminRegistrationStrategy` handles the registration
- **Logic**: Comprehensive error handling, role management
- **Result**: Stays on admin dashboard with updated member list

## Error Scenarios

### Authentication Errors
```
Invalid credentials → Error message → Stay on login page
```

**Common Scenarios**:
- **Wrong Password**: "Invalid credentials" message
- **Non-existent Email**: "User not found" message
- **Empty Fields**: Form validation highlights required fields
- **Network Issues**: "Connection error" message

### Registration Errors
```
Registration failure → Error message → Stay on form
```

**Common Scenarios**:
- **Existing Email**: "Email already registered" message
- **Weak Password**: "Password too weak" validation
- **Invalid Email**: "Invalid email format" message
- **Missing Fields**: Required field validation

### Authorization Errors
```
Unauthorized access → Error page → Redirect to login
```

**Error Pages**:
- **401 Unauthorized**: `/401` - Invalid or missing authentication
- **403 Forbidden**: `/403` - Insufficient permissions
- **404 Not Found**: Resource not found error

## Session Management

### Session Creation
```
Successful login → JWT tokens → Session storage → Dashboard access
```

**Token Storage**:
- **Access Token**: Short-lived token for API access
- **Refresh Token**: Long-lived token for session renewal
- **Session Attributes**: User email, role, authentication status

### Session Validation
```
Request → Token validation → Authentication check → Response
```

**Validation Process**:
1. **Token Extraction**: System extracts JWT from request
2. **Token Validation**: Verifies token signature and expiration
3. **User Context**: Sets authentication context
4. **Authorization**: Checks user permissions for requested resource

### Session Expiration
```
Token expires → Refresh attempt → New tokens → Continued access
```

**Refresh Flow**:
1. **Expiration Detection**: System detects expired access token
2. **Refresh Request**: Client requests new tokens using refresh token
3. **Token Renewal**: System generates new access and refresh tokens
4. **Session Update**: Updated tokens stored in session

## Logout Flow

### User-Initiated Logout
```
Logout request → Session cleanup → Redirect to login
```

**Process Steps**:
1. **Logout Request**: User clicks logout button
2. **Session Cleanup**: System removes all session attributes
3. **Token Invalidation**: JWT tokens marked as invalid
4. **Redirect**: User redirected to login page

### Session Timeout
```
Inactive session → Automatic logout → Login page redirect
```

**Timeout Handling**:
1. **Inactivity Detection**: System monitors user activity
2. **Session Expiration**: Automatic session cleanup
3. **Security Redirect**: User redirected to login page
4. **Message Display**: "Session expired" notification

### Clear Session Parameter
```
URL parameter → Session cleanup → Page refresh
```

**Implementation**:
- **Parameter**: `?clearSession=true`
- **Action**: Immediate session cleanup
- **Result**: User logged out and redirected

## User Journey Summary

### Complete Flow for New User
```
1. Visit application → 2. Redirect to login → 3. Click signup → 4. Fill registration form → 5. Account created → 6. Redirect to login → 7. Login with new credentials → 8. Access user profile
```

### Complete Flow for Existing User
```
1. Visit application → 2. Redirect to login → 3. Enter credentials → 4. Authentication → 5. Role-based redirect → 6. Access appropriate dashboard
```

### Complete Flow for Admin
```
1. Login with admin credentials → 2. Access admin dashboard → 3. View member list → 4. Register new users → 5. Manage user roles → 6. Monitor system status
```

## Key User Experience Features

### 🔐 Security
- **JWT Authentication**: Secure token-based authentication
- **Session Management**: Automatic session handling
- **Role-based Access**: Different interfaces for different user types
- **Error Handling**: Clear error messages and recovery paths

### 🎯 Usability
- **Intuitive Navigation**: Clear paths between different sections
- **Responsive Design**: Works on desktop and mobile devices
- **Form Validation**: Real-time feedback on form inputs
- **Success Messages**: Clear confirmation of successful actions

### 🔄 Strategy Pattern Benefits
- **Dynamic Registration**: Different registration flows based on context
- **Maintainable Code**: Easy to add new registration strategies
- **Consistent Interface**: Unified registration experience
- **Flexible Logic**: Different business rules for different user types

## Technical Implementation Notes

### Frontend Components
- **Thymeleaf Templates**: Server-side rendered pages
- **JavaScript Integration**: Dynamic form handling and validation
- **CSS Styling**: Modern, responsive design
- **Error Handling**: User-friendly error messages

### Backend Services
- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Business logic implementation
- **Strategy Pattern**: Dynamic registration behavior
- **Security Layer**: JWT authentication and authorization

### Database Integration
- **MongoDB**: NoSQL database for user storage
- **Repository Pattern**: Clean data access layer
- **Entity Mapping**: Efficient data transformation
- **Data Initialization**: Default user creation

This comprehensive user journey flow ensures a smooth, secure, and intuitive experience for all users of the KitchenSink application, with clear paths for different user types and robust error handling throughout the system. 