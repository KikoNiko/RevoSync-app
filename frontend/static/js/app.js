const apiUrl = 'http://localhost:8080/api/expenses';

const expenseForm = document.getElementById('expenseForm');
expenseForm.addEventListener('submit', addExpense);
document.getElementById('fetchAllBtn').addEventListener('click', fetchExpenses);
document.getElementById('fetchThisMonthBtn').addEventListener('click', fetchExpensesThisMonth);
document.getElementById('searchByCategoryForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const category = document.getElementById('searchCategory').value.trim();
    if (category) {
        fetchExpensesByCategory(category);
    }
});

let pageIndex = 0;
const rowsPerPage = 10;

function displayExpenses(expenses) {

    const tbody = document.querySelector('#expenseTable tbody');
    tbody.innerHTML = '';

    for (let i = pageIndex * rowsPerPage; i < (pageIndex * rowsPerPage) + rowsPerPage; i++) {
        if (!expenses[i]) break;
        const expense = expenses[i];

        const row = document.createElement('tr');
        row.classList.add('innerList');
        row.innerHTML = `
                <td data-label="amount">${expense.amount}</td>
                <td data-label="category">${expense.category}</td>
                <td data-label="date">${expense.date}</td>
                <td data-label="comment">${expense.comment}</td>
                <td data-label="action">
                    <button class="editBtn"><i class="fa-solid fa-pen-to-square"></i></button>
                    <button onclick="deleteExpense(${expense.id})" class="deleteBtn"><i class="fa-solid fa-trash"></i></button>
                </td>
            `;

        row.querySelector('.editBtn').addEventListener('click', () => enableInlineEditing(row, expense));

        tbody.appendChild(row);
    }
    loadPageNav(expenses);
}


function loadPageNav(expenses) {
    const nav = document.getElementById('pagingNav');
    nav.innerHTML = "";
    for (let i = 0; i < (expenses.length / rowsPerPage); i++) {
        const span = document.createElement('span');
        span.innerHTML = i + 1;
        span.addEventListener('click', (e) => {
            pageIndex = e.target.innerHTML - 1;
            displayExpenses(expenses);
        });
        if (i === pageIndex) {
            span.classList.toggle('clicked');
        }
        nav.append(span);
    }
}


function fetchExpensesThisMonth() {
    const currentMonth = new Date().getMonth() + 1;
    const currentYear = new Date().getFullYear();
    fetchExpensesByMonth(currentYear, currentMonth);
}


document.getElementById('statementUploadForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const fileInput = document.getElementById('fileInput');
    const file = fileInput.files[0];
    const responseMessage = document.getElementById('responseMessage');

    if (!file) {
        alert('Please select a file first.');
        return;
    }

    const formData = new FormData();
    formData.append('file', file);

    uploadStatement(formData, responseMessage);
});


function enableInlineEditing(row, expense) {
    const cells = row.querySelectorAll('td[data-label]');
    const actionsCell = row.querySelector('td:last-child');

    // Store original values to restore if canceled
    const originalValues = Array.from(cells).reduce((acc, cell) => {
        const field = cell.getAttribute('data-label');
        acc[field] = cell.textContent;
        return acc;
    }, {});

    // Helper to create input fields
    const createInput = (field, value) => {
        const input = field === 'comment' ? document.createElement('textarea') : document.createElement('input');
        input.type = field === 'date' ? 'date' : field === 'amount' ? 'number' : 'text';
        input.value = value;
        input.classList.add('formInput-sm');
        return input;
    };

    // Replace cell content with inputs
    cells.forEach((cell) => {
        const field = cell.getAttribute('data-label');
        if (!field || field === 'action') {
            return;
        }
        cell.innerHTML = '';
        cell.appendChild(createInput(field, originalValues[field]));
    });

    // Create Save and Cancel buttons
    const saveButton = document.createElement('button');
    saveButton.textContent = 'Save';
    saveButton.classList.add('btn-submit-sm');
    const cancelButton = document.createElement('button');
    cancelButton.classList.add('btn-cancel-sm');
    cancelButton.textContent = 'X';

    actionsCell.innerHTML = '';
    actionsCell.appendChild(saveButton);
    actionsCell.appendChild(cancelButton);

    saveButton.addEventListener('click', async () => {
        const modifiedExpense = {};
        let hasError = false;
    
        // Collect modified fields
        cells.forEach((cell) => {
            const field = cell.getAttribute('data-label');
            if (!field || field === 'action') return;
    
            const input = cell.querySelector('input, textarea');
            if (input) {
                // Check if the value is actually modified
                const newValue = field === 'amount' ? parseFloat(input.value) : input.value;
                if (newValue !== expense[field]) {
                    modifiedExpense[field] = newValue;
                }
            } else {
                console.error(`No input found for field: ${field}`);
                hasError = true;
            }
        });
    
        if (hasError) {
            alert('Could not save changes. Please try again.');
            return;
        }
    
        // Add ID for API update (needed for the PATCH endpoint)
        modifiedExpense.id = expense.id;
    
        // Client-side validations
        if (modifiedExpense.hasOwnProperty('amount') && (isNaN(modifiedExpense.amount) || modifiedExpense.amount <= 0)) {
            alert('Amount must be a positive number.');
            return;
        }
        if (modifiedExpense.hasOwnProperty('category') && !modifiedExpense.category.trim()) {
            alert('Category is required.');
            return;
        }
        if (modifiedExpense.hasOwnProperty('date') && !modifiedExpense.date.trim()) {
            alert('Date is required.');
            return;
        }
    
        console.log('Modified Expense Payload:', modifiedExpense);
    
        // Update the expense via the API
        const success = await updateExpense(modifiedExpense);
        if (success) {
            // Update only the modified fields in the table
            cells.forEach((cell) => {
                const field = cell.getAttribute('data-label');
                if (!field || field === 'action') return;
                
                cell.textContent = modifiedExpense.hasOwnProperty(field)
                ? modifiedExpense[field]
                : expense[field];
            });
    
            // Restore actions column and reattach edit button functionality
            actionsCell.innerHTML = `<button class="editBtn"><i class="fa-solid fa-pen-to-square"></i></button>
                    <button onclick="deleteExpense(${expense.id})" class="deleteBtn"><i class="fa-solid fa-trash"></i></button>`;
            actionsCell.querySelector('.editBtn').addEventListener('click', () => enableInlineEditing(row, expense));

            Object.assign(expense, modifiedExpense);
        } else {
            console.error('Update failed:', modifiedExpense);
            alert('Failed to update expense. Please check your inputs.');
        }
    });


    // Cancel editing
    cancelButton.addEventListener('click', () => {
        // Restore original cell content
        cells.forEach((cell) => {
            const field = cell.getAttribute('data-label');
            if (!field || field === 'action') return;
            cell.textContent = originalValues[field];
        });

        // Restore Edit button
        actionsCell.innerHTML = `<button class="editBtn"><i class="fa-solid fa-pen-to-square"></i></button>
                    <button onclick="deleteExpense(${expense.id})" class="deleteBtn"><i class="fa-solid fa-trash"></i></button>`;
        actionsCell.querySelector('.editBtn').addEventListener('click', () => enableInlineEditing(row, expense));
    });
}


async function fetchExpenses() {
    try {
        const response = await fetch(apiUrl + '/all');
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const expenses = await response.json();
        displayExpenses(expenses);
    } catch (error) {
        console.error('Error fetching expenses:', error);
        alert('Failed to fetch expenses. Check the console for more details.');
    }
}


async function fetchExpenseById(expenseId) {
    try {
        const response = await fetch(apiUrl + `/${expenseId}`);
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        const expense = await response.json();
        return expense;
    } catch (error) {
        console.error('Error fetching expense:', error);
        alert('Failed to fetch expense.');
    }
}


async function fetchExpensesByMonth(year, month) {
    try {
        const response = await fetch(apiUrl + `/${year}/${month}`);
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const expenses = await response.json();
        displayExpenses(expenses);
    } catch (error) {
        console.error('Error fetching expenses:', error);
        alert('Failed to fetch expenses. Check the console for more details.');
    }
}


async function fetchExpensesByCategory(category) {
    try {
        const response = await fetch(`http://localhost:8080/api/expenses?cat=${category}`);
        if (!response.ok) {
            throw new Error('Failed to fetch expenses by category');
        }
        const expensesByCategory = await response.json();
        displayExpenses(expensesByCategory);
    } catch (error) {
        console.error(error);
        alert('Error fetching expenses by category');
    }
}


async function addExpense(e) {
    e.preventDefault();
    const categorySelect = document.getElementById('category-select');
    const expense = {
        amount: parseFloat(document.getElementById('amount').value),
        category: categorySelect.options[categorySelect.selectedIndex].text,
        date: document.getElementById('date').value,
        comment: document.getElementById('comment').value
    };

    await fetch(apiUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(expense)
    });

    document.getElementById('expenseForm').reset();
    fetchExpenses(); // Refresh the expense list after adding
}


async function uploadStatement(data, responseMessage) {
    fetch(apiUrl + '/upload', {
        method: "POST",
        body: data
    })
        .then(response => response.text())
        .then(result => {
            if (result == -1) {
                responseMessage.textContent = 'Statement already uploaded!';
            } else {
                responseMessage.textContent = `Added ${result} new expenses!`;
            }

        })
        .catch(error => {
            console.error('Error:', error);
            responseMessage.textContent = 'Error uploading file';
        });
}


async function updateExpense(expense) {
    try {
        const response = await fetch(`${apiUrl}/${expense.id}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(expense),
        });

        if (!response.ok) {
            const error = await response.json();
            console.error('Backend Error:', error);
            return false;
        }

        return true;
    } catch (error) {
        console.error('Network Error:', error);
        return false;
    }
}


async function deleteExpense(id) {
    const confirmDelete = confirm('Are you sure you want to delete this expense?');
    if (!confirmDelete) {
        return;
    }

    try {
        const response = await fetch(`${apiUrl}/${id}`, { method: 'DELETE' });
        if (!response.ok) {
            throw new Error(`Error: ${response.statusText}`);
        }
        fetchExpenses();
    } catch (error) {
        console.error('Error deleting expense:', error);
        alert('Failed to delete expense. Check the console for more details.');
    }
}


document.addEventListener('DOMContentLoaded', function () {
    async function fetchCategories() {
        try {
            const response = await fetch('http://localhost:8080/api/categories');
            const categories = await response.json();

            const categorySelect = document.getElementById('category-select');
            categories.forEach(category => {
                const option = document.createElement('option');
                option.value = category.id;
                option.text = category.name;
                categorySelect.appendChild(option);
            });
        } catch (error) {
            console.error('Error fetching categories:', error);
        }
    }
    // Fetch categories when the page is loaded
    fetchCategories();
});
