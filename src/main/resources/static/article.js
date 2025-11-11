const BASE_URL = 'http://localhost:8080/api/articles';

const createUpdateForm = document.getElementById('createUpdateForm');
const formTitle = document.getElementById('formTitle');
const articleIdField = document.getElementById('articleId');
const submitButton = document.getElementById('submitButton');
const cancelButton = document.getElementById('cancelButton');
const articleTableBody = document.querySelector('#articleTable tbody');
const messageElement = document.getElementById('message');
const categoryDropdown = document.getElementById('category');

const STATIC_CATEGORIES = ['Elektronik', 'Kleidung', 'Lebensmittel', 'Bücher', 'Dienstleistung'];

document.addEventListener('DOMContentLoaded', () => {
    populateCategoryDropdown();
    fetchAndDisplayArticles();
    createUpdateForm.addEventListener('submit', handleFormSubmit);
    cancelButton.addEventListener('click', resetForm);
});

function populateCategoryDropdown() {
    STATIC_CATEGORIES.forEach(category => {
        const option = document.createElement('option');
        option.value = category;
        option.textContent = category;
        categoryDropdown.appendChild(option);
    });
}

async function fetchAndDisplayArticles() {
    try {
        const response = await fetch(BASE_URL);
        const articles = await response.json();

        articleTableBody.innerHTML = '';

        if (articles.length === 0) {
            articleTableBody.innerHTML = '<tr><td colspan="6">Keine Artikel gefunden.</td></tr>';
            return;
        }

        const dataLabels = ['ID', 'Art.Nr.', 'Name', 'VK-Preis', 'Bestand', 'Aktionen'];

        articles.forEach(article => {
            const row = articleTableBody.insertRow();
            let cell;

            cell = row.insertCell();
            cell.textContent = article.id;
            cell.setAttribute('data-label', dataLabels[0]);

            cell = row.insertCell();
            cell.textContent = article.articleNumber;
            cell.setAttribute('data-label', dataLabels[1]);

            cell = row.insertCell();
            cell.textContent = article.articleName;
            cell.setAttribute('data-label', dataLabels[2]);

            cell = row.insertCell();
            cell.textContent = article.salesPrice;
            cell.setAttribute('data-label', dataLabels[3]);

            cell = row.insertCell();
            cell.textContent = article.inventory;
            cell.setAttribute('data-label', dataLabels[4]);

            const actionsCell = row.insertCell();
            actionsCell.setAttribute('data-label', dataLabels[5]);

            const editButton = document.createElement('button');
            editButton.textContent = 'Bearbeiten';
            editButton.onclick = () => fillFormForUpdate(article);
            actionsCell.appendChild(editButton);

            const deleteButton = document.createElement('button');
            deleteButton.textContent = 'Löschen';
            deleteButton.onclick = () => deleteArticle(article.id, article.articleName);
            actionsCell.appendChild(deleteButton);
        });

    } catch (error) {
        showMessage('Fehler beim Abrufen der Artikel: ' + error.message, 'error');
        console.error('Fetch-Fehler:', error);
    }
}

async function handleFormSubmit(event) {
    event.preventDefault();

    messageElement.textContent = '';
    messageElement.className = '';

    const id = articleIdField.value;
    const isUpdate = id !== '';

    const articleData = {
        articleNumber: document.getElementById('articleNumber').value,
        articleName: document.getElementById('articleName').value,
        purchasePrice: parseFloat(document.getElementById('purchasePrice').value),
        salesPrice: parseFloat(document.getElementById('salesPrice').value),
        category: document.getElementById('category').value,
        inventory: parseInt(document.getElementById('inventory').value),
        description: document.getElementById('description').value,
        active: document.getElementById('active').checked,
        creationDate: null
    };

    if (isUpdate) {
        articleData.id = parseInt(id);
    }

    const url = isUpdate ? `${BASE_URL}/${id}` : BASE_URL;
    const method = isUpdate ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(articleData)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Serverfehler: ${response.status} ${errorText || response.statusText}`);
        }

        const actionText = isUpdate ? 'aktualisiert' : 'angelegt';
        showMessage(`✅ Artikel erfolgreich ${actionText}!`, 'success');

        resetFormFields();
        fetchAndDisplayArticles();

    } catch (error) {
        showMessage(`❌ Fehler beim Speichern des Artikels: ${error.message}`, 'error');
        console.error('API-Fehler:', error);
    }
}

function resetFormFields() {
    createUpdateForm.reset();
    document.getElementById('active').checked = true;
    articleIdField.value = '';
    formTitle.textContent = 'Neuen Artikel anlegen';
    submitButton.textContent = 'Artikel speichern';
    cancelButton.style.display = 'none';

    categoryDropdown.value = '';
}

function resetForm() {
    resetFormFields();
    messageElement.textContent = '';
    messageElement.className = '';
}

function fillFormForUpdate(article) {
    messageElement.textContent = '';
    messageElement.className = '';

    articleIdField.value = article.id;
    document.getElementById('articleNumber').value = article.articleNumber;
    document.getElementById('articleName').value = article.articleName;
    document.getElementById('purchasePrice').value = article.purchasePrice;
    document.getElementById('salesPrice').value = article.salesPrice;
    document.getElementById('category').value = article.category;
    document.getElementById('inventory').value = article.inventory;
    document.getElementById('description').value = article.description;
    document.getElementById('active').checked = article.active;

    formTitle.textContent = `Artikel (ID: ${article.id}) bearbeiten`;
    submitButton.textContent = 'Änderungen speichern';
    cancelButton.style.display = 'inline';
}


async function deleteArticle(id, name) {
    if (!confirm(`Soll der Artikel "${name}" (ID: ${id}) wirklich gelöscht werden?`)) {
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

        showMessage(`✅ Artikel ${name} wurde erfolgreich gelöscht.`, 'success');
        fetchAndDisplayArticles();

    } catch (error) {
        showMessage('❌ Fehler beim Löschen des Artikels: ' + error.message, 'error');
        console.error('Delete-Fehler:', error);
    }
}

function showMessage(text, type) {
    messageElement.textContent = text;
    messageElement.className = type;
}