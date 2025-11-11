const ORDER_BASE_URL = 'http://localhost:8080/api/orders';
const CUSTOMER_BASE_URL = 'http://localhost:8080/api/users';
const ARTICLE_BASE_URL = 'http://localhost:8080/api/articles';

const createOrderForm = document.getElementById('createOrderForm');
const customerSelect = document.getElementById('customerSelect');
const itemsContainer = document.getElementById('itemsContainer');
const addItemButton = document.getElementById('addItemButton');
const orderTableBody = document.querySelector('#orderTable tbody');
const messageElement = document.getElementById('message');

let allArticles = [];

document.addEventListener('DOMContentLoaded', () => {
    Promise.all([
        fetchCustomers(),
        fetchAllArticles()
    ]).then(() => {
        addItemToForm();
        fetchAndDisplayOrders();
    });

    createOrderForm.addEventListener('submit', handleOrderSubmit);
    addItemButton.addEventListener('click', addItemToForm);
});

async function fetchCustomers() {
    try {
        const response = await fetch(CUSTOMER_BASE_URL);
        const customers = await response.json();

        customers.forEach(customer => {
            const option = document.createElement('option');
            option.value = customer.id;
            option.textContent = customer.firstName + ' ' + customer.lastName + ' (' + customer.email + ')';
            customerSelect.appendChild(option);
        });
    } catch (error) {
        showMessage('Fehler beim Laden der Kunden.', 'error');
        console.error('Fetch Kunden Fehler:', error);
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
        option.setAttribute('data-price', article.salesPrice);
        option.textContent = article.articleNumber + ' - ' + article.articleName + ` (VK: ${article.salesPrice.toFixed(2)} €)`;
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
    unitPriceInput.placeholder = 'VK-Preis/Stk (€)';
    unitPriceInput.step = '0.01';
    unitPriceInput.required = true;

    articleSelect.addEventListener('change', () => {
        const selectedOption = articleSelect.options[articleSelect.selectedIndex];
        const defaultPrice = selectedOption.getAttribute('data-price');
        if (defaultPrice) {
            unitPriceInput.value = parseFloat(defaultPrice).toFixed(2);
        }
    });

    const removeButton = document.createElement('button');
    removeButton.type = 'button';
    removeButton.textContent = 'Entfernen';
    removeButton.onclick = () => {
        if (itemsContainer.children.length > 1) {
            itemsContainer.removeChild(itemGroup);
        } else {
            showMessage('Mindestens eine Position ist erforderlich.', 'warning');
        }
    };
    removeButton.style.marginTop = '5px';

    // Bessere Platzierung für Menge/Preis (optional: CSS für Layout anpassen)
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

    itemGroup.appendChild(articleSelect);
    itemGroup.appendChild(document.createElement('br'));
    itemGroup.appendChild(inputRow);
    itemGroup.appendChild(removeButton);

    itemsContainer.appendChild(itemGroup);
}

async function handleOrderSubmit(event) {
    event.preventDefault();
    messageElement.textContent = '';
    messageElement.className = '';

    const items = [];
    const itemGroups = itemsContainer.querySelectorAll('.item-group');

    let totalAmount = 0;

    try {
        itemGroups.forEach(group => {
            const articleSelect = group.querySelector('select');
            const quantityInput = group.querySelector('input[name^="quantity-"]');
            const unitPriceInput = group.querySelector('input[name^="unitPrice-"]');

            if (!articleSelect.value || !quantityInput.value || !unitPriceInput.value) {
                throw new Error('Bitte alle Felder einer Position ausfüllen.');
            }

            const quantity = parseInt(quantityInput.value);
            const unitPrice = parseFloat(unitPriceInput.value);

            if (quantity <= 0 || unitPrice <= 0) {
                throw new Error('Menge und Preis müssen größer als Null sein.');
            }

            items.push({
                articleId: parseInt(articleSelect.value),
                quantity: quantity,
                unitPrice: unitPrice
            });

            totalAmount += quantity * unitPrice;
        });

        if (items.length === 0) {
            showMessage('Bitte fügen Sie mindestens eine Position hinzu.', 'warning');
            return;
        }

        const orderData = {
            customerId: parseInt(customerSelect.value),
            totalAmount: totalAmount,
            items: items
        };

        const response = await fetch(ORDER_BASE_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(orderData)
        });

        if (!response.ok) {
            const errorBody = await response.json();
            throw new Error(`Serverfehler: ${response.status} (${errorBody.message || 'Unbekannter Fehler'})`);
        }

        showMessage(`✅ Auftrag erfolgreich erstellt! Gesamtbetrag: ${totalAmount.toFixed(2)} €`, 'success');

        resetOrderForm();
        fetchAndDisplayOrders();

    } catch (error) {
        showMessage(`❌ Fehler beim Erstellen des Auftrags: ${error.message}`, 'error');
        console.error('API-Fehler:', error);
    }
}

function resetOrderForm() {
    createOrderForm.reset();
    customerSelect.selectedIndex = 0;
    itemsContainer.innerHTML = '';
    addItemToForm();
}

async function fetchAndDisplayOrders() {
    try {
        const response = await fetch(ORDER_BASE_URL);
        const orders = await response.json();

        orderTableBody.innerHTML = '';

        if (orders.length === 0) {
            orderTableBody.innerHTML = '<tr><td colspan="6">Keine Kundenaufträge gefunden.</td></tr>';
            return;
        }

        const dataLabels = ['ID', 'Kunde', 'Datum', 'Betrag (€)', 'Status', 'Aktionen'];

        orders.forEach(order => {
            const row = orderTableBody.insertRow();
            let cell;

            cell = row.insertCell();
            cell.textContent = order.id;
            cell.setAttribute('data-label', dataLabels[0]);

            cell = row.insertCell();
            // ANNAHME: Das Backend liefert den Namen des Kunden in der DTO zurück
            cell.textContent = order.customerName || order.customerId;
            cell.setAttribute('data-label', dataLabels[1]);

            cell = row.insertCell();
            cell.textContent = new Date(order.orderDate).toLocaleDateString('de-DE');
            cell.setAttribute('data-label', dataLabels[2]);

            cell = row.insertCell();
            cell.textContent = order.totalAmount ? order.totalAmount.toFixed(2) + ' €' : 'N/A';
            cell.setAttribute('data-label', dataLabels[3]);

            cell = row.insertCell();
            cell.textContent = order.status;
            cell.setAttribute('data-label', dataLabels[4]);

            const actionsCell = row.insertCell();
            actionsCell.setAttribute('data-label', dataLabels[5]);

            if (order.status !== 'GELIEFERT') {
                const deliverButton = document.createElement('button');
                deliverButton.textContent = 'Ausliefern (Bestand -)';
                deliverButton.className = 'deliver-btn';
                deliverButton.onclick = () => handleDeliverOrder(order.id);
                actionsCell.appendChild(deliverButton);
            } else {
                actionsCell.appendChild(document.createTextNode('Abgeschlossen'));
            }
        });
    } catch (error) {
        showMessage('Fehler beim Abrufen der Aufträge: ' + error.message, 'error');
        console.error('Fetch Orders Fehler:', error);
    }
}

async function handleDeliverOrder(id) {
    if (!confirm(`Soll der Auftrag (ID: ${id}) als GELIEFERT markiert und der Lagerbestand reduziert werden?`)) {
        return;
    }

    try {
        const response = await fetch(`${ORDER_BASE_URL}/${id}/deliver`, {
            method: 'PATCH'
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Serverfehler bei Auslieferung: ${response.status} (${errorText})`);
        }

        showMessage(`✅ Auftrag ${id} erfolgreich ausgeliefert. Bestand reduziert!`, 'success');
        fetchAndDisplayOrders();

    } catch (error) {
        showMessage('❌ Fehler bei der Auslieferung: ' + error.message, 'error');
        console.error('Deliver Order Fehler:', error);
    }
}

function showMessage(text, type) {
    messageElement.textContent = text;
    messageElement.className = type;
}