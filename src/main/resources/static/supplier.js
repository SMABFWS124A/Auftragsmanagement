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
        <div class="popup-card">
            <h2 class="popup-title">Lieferant: ${supplier.name} (ID: ${supplier.id})</h2>

            <div class="popup-section">
                <p><strong>Firma:</strong><span>${supplier.name}</span></p>
                <p><strong>Ansprechpartner:</strong><span>${supplier.contactPerson || 'N/A'}</span></p>
                <p><strong>E-Mail:</strong><span>${supplier.email}</span></p>
                <p><strong>Telefon:</strong><span>${supplier.phone || 'N/A'}</span></p>
                <p><strong>Adresse:</strong><span>${supplier.address || 'N/A'}</span></p>
            </div>

            <div class="popup-footer">
                <button type="button" onclick="window.close()">Schließen</button>
            </div>
        </div>
    `;

    const popup = window.open('', 'SupplierDetails', 'width=520,height=480,scrollbars=no,resizable=no');
    popup.document.write(`
        <!DOCTYPE html>
        <html lang="de">
        <head>
            <title>Lieferantendetails</title>
            <style>
                :root {
                  --bg-gradient: radial-gradient(circle at top, #4338ca 0%, #111827 45%, #020617 100%);
                  --card: rgba(15, 23, 42, 0.96);
                  --border: rgba(148, 163, 184, 0.25);
                  --text: #e5e7eb;
                  --text-muted: #94a3b8;
                  --accent: #6366f1;
                  --accent-soft: rgba(99, 102, 241, 0.2);
                  --shadow: 0 24px 45px rgba(2, 6, 23, 0.85);
                  --btn-bg: linear-gradient(120deg, #6366f1, #4f46e5);
                }

                * {
                    box-sizing: border-box;
                }

                body {
                    margin: 0;
                    min-height: 100vh;
                    padding: 24px;
                    background: var(--bg-gradient);
                    font-family: 'Plus Jakarta Sans', system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
                    color: var(--text);
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }

                .popup-card {
                    width: 100%;
                    max-width: 460px;
                    background: var(--card);
                    border-radius: 26px;
                    border: 1px solid var(--border);
                    box-shadow: var(--shadow);
                    padding: 22px 24px 18px;
                    backdrop-filter: blur(18px);
                }

                .popup-title {
                    margin: 0 0 18px 0;
                    font-size: 1.35rem;
                    font-weight: 600;
                    color: #c7d2fe;
                    letter-spacing: 0.03em;
                    border-bottom: 1px solid rgba(148, 163, 184, 0.35);
                    padding-bottom: 10px;
                }

                .popup-section {
                    border-radius: 20px;
                    padding: 18px 18px 10px;
                    background: radial-gradient(circle at top left, rgba(99,102,241,0.2), transparent 55%);
                    border: 1px solid rgba(30,64,175,0.45);
                }

                .popup-section p {
                    margin: 8px 0;
                    display: flex;
                    gap: 8px;
                    font-size: 0.95rem;
                }

                .popup-section strong {
                    flex: 0 0 130px;
                    color: var(--text-muted);
                    font-weight: 600;
                }

                .popup-section span {
                    flex: 1;
                    color: var(--text);
                    font-weight: 500;
                }

                .popup-footer {
                    margin-top: 16px;
                    display: flex;
                    justify-content: flex-end;
                }

                .popup-footer button {
                    border: none;
                    border-radius: 999px;
                    padding: 8px 18px;
                    font-size: 0.9rem;
                    font-weight: 600;
                    cursor: pointer;
                    background: var(--btn-bg);
                    color: #fff;
                    box-shadow: 0 12px 24px rgba(79, 70, 229, 0.45);
                    transition: transform 0.15s ease, box-shadow 0.2s ease, opacity 0.15s ease;
                }

                .popup-footer button:hover {
                    transform: translateY(-1px);
                    box-shadow: 0 16px 30px rgba(79, 70, 229, 0.55);
                }

                .popup-footer button:active {
                    transform: translateY(0);
                    box-shadow: 0 8px 18px rgba(79, 70, 229, 0.4);
                    opacity: 0.9;
                }
            </style>
        </head>
        <body>
            ${detailsContent}
        </body>
        </html>
    `);
    popup.document.close();
}