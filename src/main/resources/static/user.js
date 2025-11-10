// Wir gehen davon aus, dass dein Spring Boot Backend auf diesem Port läuft
const BASE_URL = 'http://localhost:8080/api/users';

// DOM-Elemente abrufen
const createUpdateForm = document.getElementById('createUpdateForm');
const formTitle = document.getElementById('formTitle');
const userIdField = document.getElementById('userId');
const submitButton = document.getElementById('submitButton');
const cancelButton = document.getElementById('cancelButton');
const userTableBody = document.querySelector('#userTable tbody');
const messageElement = document.getElementById('message');

document.addEventListener('DOMContentLoaded', () => {
    // 1. Beim Laden der Seite: Alle User abrufen und anzeigen
    fetchAndDisplayUsers();

    // 2. Event-Listener für das Erstellen/Aktualisieren
    createUpdateForm.addEventListener('submit', handleFormSubmit);

    // 3. Event-Listener für den Abbrechen-Button
    cancelButton.addEventListener('click', resetForm);
});

// --- FETCH & DISPLAY FUNKTIONEN ---

async function fetchAndDisplayUsers() {
    try {
        const response = await fetch(BASE_URL);
        const users = await response.json();

        userTableBody.innerHTML = ''; // Vorherige Einträge löschen

        if (users.length === 0) {
            userTableBody.innerHTML = '<tr><td colspan="5">Keine User gefunden.</td></tr>';
            return;
        }

        // Array mit Spaltenüberschriften für responsive Darstellung
        const dataLabels = ['ID', 'Vorname', 'Nachname', 'E-Mail', 'Aktionen'];

        users.forEach(user => {
            const row = userTableBody.insertRow();
            let cell; // Variable zur Wiederverwendung

            // ID
            cell = row.insertCell();
            cell.textContent = user.id;
            cell.setAttribute('data-label', dataLabels[0]);

            // Vorname
            cell = row.insertCell();
            cell.textContent = user.firstName;
            cell.setAttribute('data-label', dataLabels[1]);

            // Nachname
            cell = row.insertCell();
            cell.textContent = user.lastName;
            cell.setAttribute('data-label', dataLabels[2]);

            // E-Mail
            cell = row.insertCell();
            cell.textContent = user.email;
            cell.setAttribute('data-label', dataLabels[3]);

            // Zelle für Aktionen (Buttons)
            const actionsCell = row.insertCell();
            actionsCell.setAttribute('data-label', dataLabels[4]);

            // Bearbeiten-Button
            const editButton = document.createElement('button');
            editButton.textContent = 'Bearbeiten';
            editButton.onclick = () => fillFormForUpdate(user);
            actionsCell.appendChild(editButton);

            // Löschen-Button
            const deleteButton = document.createElement('button');
            deleteButton.textContent = 'Löschen';
            // Die inline-Style-Anweisung wird entfernt, da dies nun über CSS geregelt wird
            deleteButton.onclick = () => deleteUser(user.id, user.firstName);
            actionsCell.appendChild(deleteButton);
        });

    } catch (error) {
        showMessage('Fehler beim Abrufen der User: ' + error.message, 'error');
        console.error('Fetch-Fehler:', error);
    }
}

// --- ERSTELLEN & AKTUALISIEREN FUNKTIONEN ---

async function handleFormSubmit(event) {
    event.preventDefault();

    const id = userIdField.value;
    const isUpdate = id !== ''; // Wenn eine ID vorhanden ist, ist es ein Update

    // Daten aus den Feldern holen
    const userData = {
        firstName: document.getElementById('firstName').value,
        lastName: document.getElementById('lastName').value,
        email: document.getElementById('email').value,
    };

    const url = isUpdate ? `${BASE_URL}/${id}` : BASE_URL;
    const method = isUpdate ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });

        if (!response.ok) {
            throw new Error(`Serverfehler: ${response.status} ${response.statusText}`);
        }

        const actionText = isUpdate ? 'aktualisiert' : 'angelegt';
        showMessage(`User erfolgreich ${actionText}!`, 'success');

        resetForm();
        fetchAndDisplayUsers(); // Liste neu laden

    } catch (error) {
        showMessage(`Fehler beim Speichern des Users: ${error.message}`, 'error');
        console.error('API-Fehler:', error);
    }
}

function fillFormForUpdate(user) {
    // Setzt die verborgene ID
    userIdField.value = user.id;

    // Füllt die Felder
    document.getElementById('firstName').value = user.firstName;
    document.getElementById('lastName').value = user.lastName;
    document.getElementById('email').value = user.email;

    // Passt Formular-Titel und Buttons an
    formTitle.textContent = `User (ID: ${user.id}) bearbeiten`;
    submitButton.textContent = 'Änderungen speichern';
    cancelButton.style.display = 'inline';
}

function resetForm() {
    createUpdateForm.reset();
    userIdField.value = '';
    formTitle.textContent = 'Neuen User anlegen';
    submitButton.textContent = 'User speichern';
    cancelButton.style.display = 'none';
    messageElement.textContent = ''; // Nachricht löschen
}

// --- LÖSCHEN FUNKTION ---

async function deleteUser(id, name) {
    if (!confirm(`Soll der User "${name}" (ID: ${id}) wirklich gelöscht werden?`)) {
        return;
    }

    try {
        const response = await fetch(`${BASE_URL}/${id}`, {
            method: 'DELETE' // Nutzt den @DeleteMapping Endpunkt
        });

        if (!response.ok) {
            throw new Error(`Serverfehler: ${response.status}`);
        }

        showMessage(`User ${name} wurde erfolgreich gelöscht.`, 'success');
        fetchAndDisplayUsers(); // Liste neu laden

    } catch (error) {
        showMessage('Fehler beim Löschen des Users: ' + error.message, 'error');
        console.error('Delete-Fehler:', error);
    }
}

// --- HELPER FUNKTION ---

function showMessage(text, type) {
    messageElement.textContent = text;
    messageElement.className = type; // Fügt die CSS-Klasse (success/error) hinzu
}