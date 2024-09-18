const apiUrl = 'http://localhost:8080/api/expenses'; // Update with your backend URL

document.getElementById('expenseForm').addEventListener('submit', addExpense);
document.getElementById('fetchExpensesBtn').addEventListener('click', fetchExpenses);

async function fetchExpenses() {
    try {
        const response = await fetch(apiUrl + '/all');
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const expenses = await response.json();
        const tbody = document.querySelector('#expenseTable tbody');
        tbody.innerHTML = '';

        expenses.forEach(expense => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${expense.amount}</td>
                <td>${expense.category}</td>
                <td>${expense.date}</td>
                <td>${expense.comment}</td>
                <td>
                    <button onclick="editExpense(${expense.id})">Edit</button>
                    <button onclick="deleteExpense(${expense.id})">Delete</button>
                </td>
            `;
            tbody.appendChild(row);
        });
    } catch (error) {
        console.error('Error fetching expenses:', error);
        alert('Failed to fetch expenses. Check the console for more details.');
    }
}

async function addExpense(event) {
    event.preventDefault();
    const expense = {
        amount: parseFloat(document.getElementById('amount').value),
        category: document.getElementById('category').value,
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

async function deleteExpense(id) {
    const confirmDelete = confirm("Are you sure you want to delete this expense?");
    if (!confirmDelete) {
        return; // Exit the function if the user cancels
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

async function editExpense(id) {
    const expense = await fetch(`${apiUrl}/${id}`).then(res => res.json());
    document.getElementById('amount').value = expense.amount;
    document.getElementById('category').value = expense.category;
    document.getElementById('date').value = expense.date;
    document.getElementById('comment').value = expense.comment;

    // Update form submission to edit expense
    document.getElementById('expenseForm').onsubmit = async function(event) {
        event.preventDefault();
        const updatedExpense = {
            ...expense,
            amount: parseFloat(document.getElementById('amount').value),
            category: document.getElementById('category').value,
            date: document.getElementById('date').value,
            comment: document.getElementById('comment').value
        };

        await fetch(`${apiUrl}/${id}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatedExpense)
        });

        document.getElementById('expenseForm').reset();
        document.getElementById('expenseForm').onsubmit = addExpense;
        fetchExpenses();
    };
}
