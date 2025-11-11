const BASE_URL = 'http://localhost:8080/api/users';

const createUpdateForm = document.getElementById('createUpdateForm');
const formTitle = document.getElementById('formTitle');
const userIdField = document.getElementById('userId');
const submitButton = document.getElementById('submitButton');
const cancelButton = document.getElementById('cancelButton');
const userTableBody = document.querySelector('#userTable tbody');
const messageElement = document.getElementById('message');

document.addEventListener('DOMContentLoaded', () => {
    fetchAndDisplayUsers();
    createUpdateForm.addEventListener('submit', handleFormSubmit);
    cancelButton.addEventListener('click', resetForm);
});

async function fetchAndDisplayUsers() {
    try {
        const response = await fetch(BASE_URL);
        const users = await response.json();

        userTableBody.innerHTML = '';

        if (users.length === 0) {
            userTableBody.innerHTML = '<tr><td colspan="5">Keine User gefunden.</td></tr>';
            return;
        }

        const dataLabels = ['ID', 'Vorname', 'Nachname', 'E-Mail', 'Aktionen'];

        users.forEach(user => {
            const row = userTableBody.insertRow();
            let cell;

            cell = row.insertCell();
            cell.textContent = user.id;
            cell.setAttribute('data-label', dataLabels[0]);

            cell = row.insertCell();
            cell.textContent = user.firstName;
            cell.setAttribute('data-label', dataLabels[1]);

            cell = row.insertCell();
            cell.textContent = user.lastName;
            cell.setAttribute('data-label', dataLabels[2]);

            cell = row.insertCell();
            cell.textContent = user.email;
            cell.setAttribute('data-label', dataLabels[3]);

            const actionsCell = row.insertCell();
            actionsCell.setAttribute('data-label', dataLabels[4]);

            const editButton = document.createElement('button');
            editButton.textContent = 'Bearbeiten';
            editButton.onclick = () => fillFormForUpdate(user);
            actionsCell.appendChild(editButton);

            const deleteButton = document.createElement('button');
            deleteButton.textContent = 'Löschen';
            deleteButton.onclick = () => deleteUser(user.id, user.firstName);
            actionsCell.appendChild(deleteButton);
        });

    } catch (error) {
        showMessage('❌ Fehler beim Abrufen der User: ' + error.message, 'error');
        console.error('Fetch-Fehler:', error);
    }
}

async function handleFormSubmit(event) {
    event.preventDefault();

    messageElement.textContent = '';
    messageElement.className = '';

    const id = userIdField.value;
    const isUpdate = id !== '';

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
        showMessage(`✅ User erfolgreich ${actionText}!`, 'success');

        resetFormFields();
        fetchAndDisplayUsers();

    } catch (error) {
        showMessage(`❌ Fehler beim Speichern des Users: ${error.message}`, 'error');
        console.error('API-Fehler:', error);
    }
}

function resetFormFields() {
    createUpdateForm.reset();
    userIdField.value = '';
    formTitle.textContent = 'Neuen User anlegen';
    submitButton.textContent = 'User speichern';
    cancelButton.style.display = 'none';
}

function resetForm() {
    resetFormFields();
    messageElement.textContent = '';
    messageElement.className = '';
}


function fillFormForUpdate(user) {
    messageElement.textContent = '';
    messageElement.className = '';

    userIdField.value = user.id;

    document.getElementById('firstName').value = user.firstName;
    document.getElementById('lastName').value = user.lastName;
    document.getElementById('email').value = user.email;

    formTitle.textContent = `User (ID: ${user.id}) bearbeiten`;
    submitButton.textContent = 'Änderungen speichern';
    cancelButton.style.display = 'inline';
}


async function deleteUser(id, name) {
    if (!confirm(`Soll der User "${name}" (ID: ${id}) wirklich gelöscht werden?`)) {
        return;
    }

    messageElement.textContent = '';
    messageElement.className = '';

    try {
        const response = await fetch(`${BASE_URL}/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(`Serverfehler: ${response.status}`);
        }

        showMessage(`✅ User ${name} wurde erfolgreich gelöscht.`, 'success');
        fetchAndDisplayUsers();

    } catch (error) {
        showMessage('❌ Fehler beim Löschen des Users: ' + error.message, 'error');
        console.error('Delete-Fehler:', error);
    }
}

function showMessage(text, type) {
    messageElement.textContent = text;
    messageElement.className = type;
}