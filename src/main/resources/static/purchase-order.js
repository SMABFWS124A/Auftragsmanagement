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
        const suppliers = await response.json();

        suppliers.forEach(supplier => {
            const option = document.createElement('option');
            option.value = supplier.id;
            option.textContent = supplier.name + ' (' + (supplier.contactPerson || 'N/A') + ')';
            supplierSelect.appendChild(option);
        });
    } catch (error) {
        showMessage('Fehler beim Laden der Lieferanten.', 'error');
        console.error('Fetch Lieferanten Fehler:', error);
    }
}

async function fetchAllArticles() {
    try {
        const response = await fetch(ARTICLE_BASE_URL);
        allArticles = await response.json();
    } catch (error) {
        showMessage('Fehler beim Laden der Artikel.', 'error');
        console.error('Fetch Artikel Fehler:', error);
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

    let defaultOption = document.createElement('option');
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

    // Fortsetzung des itemGroup Aufbaus

    // Container für Menge und Preis (Inline-Layout)
    const inputRow = document.createElement('div');
    inputRow.style.display = 'flex';
    inputRow.style.gap = '10px';

    // Menge Wrapper
    const quantityWrapper = document.createElement('div');
    quantityWrapper.style.flex = '1';
    quantityWrapper.appendChild(quantityInput);

    // Preis Wrapper
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

    return {
        supplierId: parseInt(supplierId),
        items: items
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
        showMessage(`❌ Fehler beim Anlegen des Bestellauftrags: ${error.message}`, 'error');
        console.error('API-Fehler:', error);
    }
}

function resetForm() {
    createOrderForm.reset();
    itemsContainer.innerHTML = '';
    addItemToForm(); // Fügt eine leere Position hinzu
    messageElement.textContent = '';
    messageElement.className = '';
}

async function fetchAndDisplayPurchaseOrders() {
    try {
        const response = await fetch(PURCHASE_ORDER_BASE_URL);
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
            cell.textContent = order.id;
            cell.setAttribute('data-label', dataLabels[0]);

            cell = row.insertCell();
            // ANNAHME: Das Backend liefert den Namen des Lieferanten in der DTO zurück
            cell.textContent = order.supplierName || 'N/A';
            cell.setAttribute('data-label', dataLabels[1]);

            cell = row.insertCell();
            cell.textContent = new Date(order.orderDate).toLocaleDateString();
            cell.setAttribute('data-label', dataLabels[2]);

            cell = row.insertCell();
            cell.textContent = order.totalAmount ? order.totalAmount.toFixed(2) + ' €' : '0.00 €';
            cell.setAttribute('data-label', dataLabels[3]);

            cell = row.insertCell();
            cell.textContent = order.status;
            cell.setAttribute('data-label', dataLabels[4]);

            const actionsCell = row.insertCell();
            actionsCell.setAttribute('data-label', dataLabels[5]);

            // Liefer-Button
            if (order.status !== 'GELIEFERT') {
                const receiveButton = document.createElement('button');
                receiveButton.textContent = 'Lieferung empfangen';
                receiveButton.classList.add('receive-btn');
                receiveButton.onclick = () => handleReceiveOrder(order.id);
                actionsCell.appendChild(receiveButton);
            } else {
                actionsCell.textContent = '—';
            }
        });

    } catch (error) {
        showMessage('Fehler beim Abrufen der Bestellaufträge: ' + error.message, 'error');
        console.error('Fetch-Fehler:', error);
    }
}

async function handleReceiveOrder(id) {
    if (!confirm(`Soll der Bestellauftrag (ID: ${id}) als GELIEFERT markiert und der Lagerbestand erhöht werden?`)) {
        return;
    }

    messageElement.textContent = '';
    messageElement.className = '';

    try {
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
        showMessage('❌ Fehler beim Empfangen der Lieferung: ' + error.message, 'error');
        console.error('Receive Order Fehler:', error);
    }
}

function showMessage(text, type) {
    messageElement.textContent = text;
    messageElement.className = type;
}