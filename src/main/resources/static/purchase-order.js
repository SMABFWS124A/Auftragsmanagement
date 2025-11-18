const PURCHASE_ORDER_BASE_URL = 'http://localhost:8080/api/purchase-orders';
const SUPPLIER_BASE_URL = 'http://localhost:8080/api/suppliers';
const ARTICLE_BASE_URL = 'http://localhost:8080/api/articles';

const createOrderForm = document.getElementById('createOrderForm');
const supplierSelect = document.getElementById('supplierSelect');
const itemsContainer = document.getElementById('itemsContainer');
const addItemButton = document.getElementById('addItemButton');
const purchaseOrderTableBody = document.querySelector('#purchaseOrderTable tbody');
const messageElement = document.getElementById('message');

let allArticles = [];

document.addEventListener('DOMContentLoaded', () => {
    Promise.all([
        fetchSuppliers(),
        fetchAllArticles()
    ]).then(() => {
        addItemToForm();
        fetchAndDisplayPurchaseOrders();
    });

    createOrderForm.addEventListener('submit', handlePurchaseOrderSubmit);
    addItemButton.addEventListener('click', addItemToForm);
});

async function fetchSuppliers() {
    try {
        const response = await fetch(SUPPLIER_BASE_URL);
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
        const suppliers = await response.json();

        suppliers.forEach(supplier => {
            const option = document.createElement('option');
            option.value = supplier.id;
            option.textContent = supplier.name + ' (' + (supplier.contactPerson || 'N/A') + ')';
            supplierSelect.appendChild(option);
        });
    } catch (error) {
        handleError(error, 'Fehler beim Laden der Lieferanten.');
    }
}

async function fetchAllArticles() {
    try {
        const response = await fetch(ARTICLE_BASE_URL);
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
        allArticles = await response.json();
    } catch (error) {
        handleError(error, 'Fehler beim Laden der Artikel.');
    }
}

function addItemToForm() {
    const itemIndex = itemsContainer.children.length;

    const itemGroup = document.createElement('div');
    itemGroup.classList.add('item-group');
    itemGroup.style.border = '1px dashed #ccc';
    itemGroup.style.padding = '10px';
    itemGroup.style.marginBottom = '10px';

    const articleSelect = document.createElement('select');
    articleSelect.name = `articleId-${itemIndex}`;
    articleSelect.required = true;
    articleSelect.style.width = '100%';
    articleSelect.style.marginBottom = '5px';

    const defaultOption = document.createElement('option');
    defaultOption.value = '';
    defaultOption.textContent = '--- Artikel wählen ---';
    defaultOption.disabled = true;
    defaultOption.selected = true;
    articleSelect.appendChild(defaultOption);

    allArticles.forEach(article => {
        const option = document.createElement('option');
        option.value = article.id;
        option.setAttribute('data-price', article.purchasePrice);
        option.textContent = article.articleNumber + ' - ' + article.articleName + ` (EK: ${article.purchasePrice.toFixed(2)} €)`;
        articleSelect.appendChild(option);
    });

    const quantityInput = document.createElement('input');
    quantityInput.type = 'number';
    quantityInput.name = `quantity-${itemIndex}`;
    quantityInput.placeholder = 'Menge';
    quantityInput.min = '1';
    quantityInput.required = true;
    quantityInput.style.marginRight = '10px';

    const unitPriceInput = document.createElement('input');
    unitPriceInput.type = 'number';
    unitPriceInput.name = `unitPrice-${itemIndex}`;
    unitPriceInput.placeholder = 'EK-Preis/Stk (€)';
    unitPriceInput.step = '0.01';
    unitPriceInput.required = true;

    articleSelect.addEventListener('change', () => {
        const selectedOption = articleSelect.options[articleSelect.selectedIndex];
        const defaultPrice = selectedOption.getAttribute('data-price');
        if (defaultPrice) {
            unitPriceInput.value = parseFloat(defaultPrice).toFixed(2);
        }
    });

    const inputRow = document.createElement('div');
    inputRow.style.display = 'flex';
    inputRow.style.gap = '10px';

    const quantityWrapper = document.createElement('div');
    quantityWrapper.style.flex = '1';
    quantityWrapper.appendChild(quantityInput);

    const priceWrapper = document.createElement('div');
    priceWrapper.style.flex = '1';
    priceWrapper.appendChild(unitPriceInput);

    inputRow.appendChild(quantityWrapper);
    inputRow.appendChild(priceWrapper);

    const removeButton = document.createElement('button');
    removeButton.textContent = 'Position entfernen';
    removeButton.type = 'button';
    removeButton.style.backgroundColor = '#dc3545';
    removeButton.style.color = 'white';
    removeButton.style.marginTop = '10px';
    removeButton.onclick = () => itemGroup.remove();

    itemGroup.appendChild(articleSelect);
    itemGroup.appendChild(inputRow);
    itemGroup.appendChild(removeButton);

    itemsContainer.appendChild(itemGroup);
}

function getFormData() {
    const supplierId = supplierSelect.value;
    const items = [];

    const itemGroups = itemsContainer.querySelectorAll('.item-group');

    itemGroups.forEach(group => {
        const articleSelect = group.querySelector('select[name^="articleId-"]');
        const quantityInput = group.querySelector('input[name^="quantity-"]');
        const unitPriceInput = group.querySelector('input[name^="unitPrice-"]');

        if (articleSelect && quantityInput && unitPriceInput && articleSelect.value) {
            items.push({
                articleId: parseInt(articleSelect.value),
                quantity: parseInt(quantityInput.value),
                unitPrice: parseFloat(unitPriceInput.value)
            });
        }
    });

    // Das DTO erwartet 'id' (nicht orderId), 'orderDate', 'supplierName' etc.
    // Das Backend setzt diese Werte. Wir senden nur, was das Backend braucht:
    return {
        supplierId: parseInt(supplierId),
        items: items
        // status, totalAmount etc. werden vom Backend gesetzt
    };
}

async function handlePurchaseOrderSubmit(event) {
    event.preventDefault();

    messageElement.textContent = '';
    messageElement.className = '';

    const orderData = getFormData();

    if (!orderData.supplierId || orderData.items.length === 0) {
        showMessage('Bitte wählen Sie einen Lieferanten und fügen Sie mindestens eine Position hinzu.', 'error');
        return;
    }

    try {
        const response = await fetch(PURCHASE_ORDER_BASE_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(orderData)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Serverfehler: ${response.status} ${errorText}`);
        }

        showMessage('✅ Bestellauftrag erfolgreich angelegt!', 'success');
        resetForm();
        fetchAndDisplayPurchaseOrders();

    } catch (error) {
        handleError(error, 'Fehler beim Anlegen des Bestellauftrags.');
    }
}

function resetForm() {
    createOrderForm.reset();
    itemsContainer.innerHTML = '';
    addItemToForm();
    messageElement.textContent = '';
    messageElement.className = '';
}

async function fetchAndDisplayPurchaseOrders() {
    try {
        const response = await fetch(PURCHASE_ORDER_BASE_URL);
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
        const orders = await response.json();

        purchaseOrderTableBody.innerHTML = '';

        if (orders.length === 0) {
            purchaseOrderTableBody.innerHTML = '<tr><td colspan="6">Keine Bestellaufträge gefunden.</td></tr>';
            return;
        }

        const dataLabels = ['ID', 'Lieferant', 'Datum', 'Gesamt (€)', 'Status', 'Aktionen'];

        orders.forEach(order => {
            const row = purchaseOrderTableBody.insertRow();
            let cell;

            cell = row.insertCell();
            cell.textContent = order.id; // <-- Greift auf 'id' zu (wie im DTO definiert)
            cell.setAttribute('data-label', dataLabels[0]);

            cell = row.insertCell();
            cell.textContent = order.supplierName || 'N/A'; // <-- KORRIGIERT
            cell.setAttribute('data-label', dataLabels[1]);

            cell = row.insertCell();
            cell.textContent = new Date(order.orderDate).toLocaleDateString(); // <-- KORRIGIERT
            cell.setAttribute('data-label', dataLabels[2]);

            cell = row.insertCell();
            cell.textContent = order.totalAmount ? order.totalAmount.toFixed(2) + ' €' : '0.00 €';
            cell.setAttribute('data-label', dataLabels[3]);

            cell = row.insertCell();
            cell.textContent = order.status;
            cell.setAttribute('data-label', dataLabels[4]);

            const actionsCell = row.insertCell();
            actionsCell.setAttribute('data-label', dataLabels[5]);
            actionsCell.style.display = 'flex';
            actionsCell.style.gap = '5px';

            if (order.status !== 'GELIEFERT') {
                const receiveButton = document.createElement('button');
                receiveButton.textContent = 'Lieferung empfangen';
                receiveButton.classList.add('receive-btn');
                receiveButton.onclick = () => handleReceiveOrder(order.id);
                actionsCell.appendChild(receiveButton);
            }

            // NEU: LÖSCHEN-BUTTON
            const deleteButton = document.createElement('button');
            deleteButton.textContent = 'Löschen';
            deleteButton.classList.add('delete-btn'); // CSS-Klasse für rotes Styling
            deleteButton.setAttribute('data-id', order.id); // Wichtig für den Handler
            deleteButton.onclick = () => handleDeleteOrder(order.id);
            actionsCell.appendChild(deleteButton);
        });

    } catch (error) {
        handleError(error, 'Fehler beim Abrufen der Bestellaufträge.');
    }
}

async function handleReceiveOrder(id) {
    if (!confirm(`Soll der Bestellauftrag (ID: ${id}) als GELIEFERT markiert und der Lagerbestand erhöht werden?`)) {
        return;
    }

    messageElement.textContent = '';
    messageElement.className = '';

    try {
        // HINWEIS: Der Endpunkt /receive ist ggf. @PatchMapping
        const response = await fetch(`${PURCHASE_ORDER_BASE_URL}/${id}/receive`, {
            method: 'PATCH'
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Serverfehler: ${response.status} ${errorText}`);
        }

        showMessage(`✅ Bestellauftrag ${id} erfolgreich empfangen. Bestand aktualisiert!`, 'success');
        fetchAndDisplayPurchaseOrders();

    } catch (error) {
        handleError(error, 'Fehler beim Empfangen der Lieferung.');
    }
}

/**
 * NEUE FUNKTION FÜR DEN LÖSCHEN-BUTTON
 */
async function handleDeleteOrder(id) {
    if (!confirm(`Soll der Bestellauftrag (ID: ${id}) wirklich endgültig gelöscht werden?`)) {
        return;
    }

    messageElement.textContent = '';
    messageElement.className = '';

    try {
        const response = await fetch(`${PURCHASE_ORDER_BASE_URL}/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Serverfehler: ${response.status} ${errorText}`);
        }

        showMessage(`✅ Bestellauftrag ${id} erfolgreich gelöscht.`, 'success');
        fetchAndDisplayPurchaseOrders(); // Liste neu laden

    } catch (error) {
        handleError(error, 'Fehler beim Löschen der Bestellung.');
    }
}

function showMessage(text, type) {
    messageElement.textContent = text;
    messageElement.className = type;
}

function handleError(error, userMessage) {
    console.error('Error:', error);
    showMessage(userMessage, 'error');
}