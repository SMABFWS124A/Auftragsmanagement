const AUTH_EMAIL = 'test@test.de';
const AUTH_PASSWORD = 'Test123';
const AUTH_KEY = 'auftragsmanagement-authenticated';
const AUTH_EMAIL_KEY = 'auftragsmanagement-email';

function isLoggedIn() {
    return localStorage.getItem(AUTH_KEY) === 'true';
}

function requireAuth() {
    if (!isLoggedIn()) {
        window.location.href = 'login.html';
    }
}

function login(email, password) {
    const isValidEmail = email.trim().toLowerCase() === AUTH_EMAIL;
    const isValidPassword = password === AUTH_PASSWORD;

    if (isValidEmail && isValidPassword) {
        localStorage.setItem(AUTH_KEY, 'true');
        localStorage.setItem(AUTH_EMAIL_KEY, email.trim());
        return true;
    }

    return false;
}

function logout() {
    localStorage.removeItem(AUTH_KEY);
    localStorage.removeItem(AUTH_EMAIL_KEY);
    window.location.href = 'login.html';
}

function getAuthenticatedEmail() {
    return localStorage.getItem(AUTH_EMAIL_KEY) || 'User';
}