const apiUrl = 'http://localhost:8080/api/expenses';
const statisticsUrl = 'http://localhost:8080/api/statistics';

document.getElementById('expenseForm').addEventListener('submit', addExpense);
document.getElementById('fetchAllBtn').addEventListener('click', fetchExpenses);
document.getElementById('fetchThisMonthBtn').addEventListener('click', fetchExpensesThisMonth);
document.getElementById('searchByCategoryForm').addEventListener('submit', function(e) {
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
                <td data-label="Amount">${expense.amount}</td>
                <td data-label="Category">${expense.category}</td>
                <td data-label="Date">${expense.date}</td>
                <td data-label="Comment">${expense.comment}</td>
                <td data-label="Action">
                    <button onclick="openEditForm(${expense.id})" class="editBtn"><i class="fa-solid fa-pen-to-square"></i></button>
                    <button onclick="deleteExpense(${expense.id})" class="deleteBtn"><i class="fa-solid fa-trash"></i></button>
                </td>
            `;
            tbody.appendChild(row);
        }
        loadPageNav(expenses);
}


function openEditForm(expenseId) {
    (async () => {
        const expense = await getExpenseById(expenseId);
        if (expense) {
            console.log('Fetched expense:', expense);
        } else {
            console.log('Failed to fetch expense.');
        }

        editExpense(expense);
    })();
   
}

async function getExpenseById(expenseId) {
    try {
        const expense = fetchExpenseById(expenseId);
        return expense;
    } catch {
        console.error('Failed to fetch the object:', error);
        return null; // Return null or handle the error as needed
    }
}

function editExpense(expense) {
    const rowToEdit = document.querySelector('.innerList');
    rowToEdit.innerHTML = 
       `
        <td data-label="Amount">
        <input type="number" id="amount" name="amount" step="0.1" required>
        </td>
        <td data-label="Category">
        <input type="text" id="category" name="category" value=${expense.category}>
        </td>
        <td data-label="Date">
        <input type="date" id="date" name="date" required>
        </td>
        <td data-label="Comment">
        <input type="text" id="comment" name="comment" placeholder="Comment...">
        </td>
        <td data-label="Action">
        <button onclick="updateExpense(${expense.id})" class="editBtn"><i class="fa-solid fa-circle-check"></i></button>
        </td>
    `
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

function fetchExpensesThisMonth() {
    const currentMonth = new Date().getMonth() + 1;
    const currentYear = new Date().getFullYear();
    fetchExpensesByMonth(currentYear, currentMonth);
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


document.addEventListener('DOMContentLoaded', function() {
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


async function addExpense(event) {
    event.preventDefault();
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


document.getElementById('statementUploadForm').addEventListener('submit', function(e) {
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

    fetch(apiUrl + '/upload', {
        method: "POST",
        body: formData
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
});


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


async function updateExpense(expenseId) {
    
    try {
        const response = await fetch(`${apiUrl}/${expenseId}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatedData)
        });

        if (!response.ok) {
            throw new Error(`Failed to update expense: ${response.statusText}`);
        }

        const updatedExpense = await response.json();
        console.log('Updated expense:', updatedExpense);
        return updatedExpense;
    } catch (error) {
        console.error('Error updating expense:', error);
    }
}

// TODO: Finish code for updating expense 



