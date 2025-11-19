const BASE_URL = 'http://localhost:8080/api/suppliers';

const createUpdateForm = document.getElementById('createUpdateSupplierForm');
const formTitle = document.getElementById('formTitle');
const supplierIdField = document.getElementById('supplierId');
const submitButton = document.getElementById('submitButton');
const cancelButton = document.getElementById('cancelButton');
const supplierTableBody = document.querySelector('#supplierTable tbody');
const messageElement = document.getElementById('message');

document.addEventListener('DOMContentLoaded', () => {
    fetchAndDisplaySuppliers();
    createUpdateForm.addEventListener('submit', handleFormSubmit);
    cancelButton.addEventListener('click', resetForm);
});

async function fetchAndDisplaySuppliers() {
    try {
        const response = await fetch(BASE_URL);
        const suppliers = await response.json();

        supplierTableBody.innerHTML = '';

        if (suppliers.length === 0) {
            supplierTableBody.innerHTML = '<tr><td colspan="6">Keine Lieferanten gefunden.</td></tr>';
            return;
        }

        const dataLabels = ['ID', 'Name', 'Ansprechpartner', 'E-Mail', 'Aktionen'];

        suppliers.forEach(supplier => {
            const row = supplierTableBody.insertRow();
            let cell;

            cell = row.insertCell();
            cell.textContent = supplier.id;
            cell.setAttribute('data-label', dataLabels[0]);

            cell = row.insertCell();
            cell.textContent = supplier.name;
            cell.setAttribute('data-label', dataLabels[1]);

            cell = row.insertCell();
            cell.textContent = supplier.contactPerson || 'N/A';
            cell.setAttribute('data-label', dataLabels[2]);

            cell = row.insertCell();
            cell.textContent = supplier.email;
            cell.setAttribute('data-label', dataLabels[3]);

            const actionsCell = row.insertCell();
            actionsCell.setAttribute('data-label', dataLabels[4]);

            const infoButton = document.createElement('button');
            infoButton.textContent = 'Details';
            infoButton.onclick = () => showSupplierDetails(supplier);
            actionsCell.appendChild(infoButton);

            const editButton = document.createElement('button');
            editButton.textContent = 'Bearbeiten';
            editButton.onclick = () => fillFormForUpdate(supplier);
            actionsCell.appendChild(editButton);

            const deleteButton = document.createElement('button');
            deleteButton.textContent = 'Löschen';
            deleteButton.onclick = () => deleteSupplier(supplier.id, supplier.name);
            actionsCell.appendChild(deleteButton);
        });

    } catch (error) {
        showMessage('Fehler beim Abrufen der Lieferanten: ' + error.message, 'error');
        console.error('Fetch-Fehler:', error);
    }
}

async function handleFormSubmit(event) {
    event.preventDefault();

    messageElement.textContent = '';
    messageElement.className = '';

    const id = supplierIdField.value;
    const isUpdate = id !== '';

    const supplierData = {
        name: document.getElementById('supplierName').value,
        contactPerson: document.getElementById('contactPerson').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value,
        address: document.getElementById('address').value,
    };

    if (isUpdate) {
        supplierData.id = id;
    }

    const url = isUpdate ? `${BASE_URL}/${id}` : BASE_URL;
    const method = isUpdate ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(supplierData)
        });

        if (!response.ok) {
            const errorBody = await response.json();
            throw new Error(`Serverfehler: ${response.status} ${errorBody.message || response.statusText}`);
        }

        const actionText = isUpdate ? 'aktualisiert' : 'angelegt';
        showMessage(`✅ Lieferant erfolgreich ${actionText}!`, 'success');

        resetFormFields();
        fetchAndDisplaySuppliers();

    } catch (error) {
        showMessage(`❌ Fehler beim Speichern des Lieferanten: ${error.message}`, 'error');
        console.error('API-Fehler:', error);
    }
}

function resetFormFields() {
    createUpdateForm.reset();
    supplierIdField.value = '';
    formTitle.textContent = 'Neuen Lieferanten anlegen';
    submitButton.textContent = 'Lieferant speichern';
    cancelButton.style.display = 'none';
}

function resetForm() {
    resetFormFields();
    messageElement.textContent = '';
    messageElement.className = '';
}

function fillFormForUpdate(supplier) {
    messageElement.textContent = '';
    messageElement.className = '';

    supplierIdField.value = supplier.id;
    document.getElementById('supplierName').value = supplier.name;
    document.getElementById('contactPerson').value = supplier.contactPerson;
    document.getElementById('email').value = supplier.email;
    document.getElementById('phone').value = supplier.phone;
    document.getElementById('address').value = supplier.address;

    formTitle.textContent = `Lieferant (ID: ${supplier.id}) bearbeiten`;
    submitButton.textContent = 'Änderungen speichern';
    cancelButton.style.display = 'inline';
}

async function deleteSupplier(id, name) {
    if (!confirm(`Soll der Lieferant "${name}" (ID: ${id}) wirklich gelöscht werden?`)) {
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

        showMessage(`✅ Lieferant ${name} wurde erfolgreich gelöscht.`, 'success');
        fetchAndDisplaySuppliers();

    } catch (error) {
        showMessage('❌ Fehler beim Löschen des Lieferanten: ' + error.message, 'error');
        console.error('Delete-Fehler:', error);
    }
}

function showMessage(text, type) {
    messageElement.textContent = text;
    messageElement.className = type;
}

function showSupplierDetails(supplier) {
    const detailsContent = `
        <h3>Details für: ${supplier.name} (ID: ${supplier.id})</h3>
        <hr>
        <p><strong>Name der Firma:</strong> ${supplier.name}</p>
        <p><strong>Ansprechpartner:</strong> ${supplier.contactPerson || 'N/A'}</p>
        <p><strong>E-Mail:</strong> ${supplier.email}</p>
        <p><strong>Telefon:</strong> ${supplier.phone || 'N/A'}</p>
        <p><strong>Adresse:</strong> ${supplier.address || 'N/A'}</p>
    `;

    const popup = window.open('', 'SupplierDetails', 'width=500,height=400,scrollbars=yes');
    popup.document.write(`
        <!DOCTYPE html>
        <html lang="de">
        <head>
            <title>Lieferantendetails</title>
            <style>
                body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 20px; line-height: 1.6; }
                h3 { color: #007bff; border-bottom: 2px solid #007bff; padding-bottom: 10px; }
                hr { border: none; border-top: 1px solid #ccc; margin: 15px 0; }
                p strong { display: inline-block; width: 150px; }
            </style>
        </head>
        <body>
            ${detailsContent}
        </body>
        </html>
    `);
    popup.document.close();
}