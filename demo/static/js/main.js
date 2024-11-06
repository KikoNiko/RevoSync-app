const apiUrl = 'http://localhost:8080/api/expenses';
const statisticsUrl = 'http://localhost:8080/api/statistics';

document.getElementById('expenseForm').addEventListener('submit', addExpense);
document.getElementById('fetchAllBtn').addEventListener('click', fetchExpenses);


function displayExpenses(expenses) {
    const tbody = document.querySelector('#expenseTable tbody');
        tbody.innerHTML = '';
        
        expenses.forEach(expense => {
            const row = document.createElement('tr');
            row.classList.add('innerList');
            row.innerHTML = `
                <td data-label="Amount">${expense.amount}</td>
                <td data-label="Category">${expense.category}</td>
                <td data-label="Date">${expense.date}</td>
                <td data-label="Comment">${expense.comment}</td>
                <td data-label="Action">
                    <button onclick="deleteExpense(${expense.id})" class="deleteBtn">Delete</button>
                </td>
            `;
            tbody.appendChild(row);
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


document.addEventListener("DOMContentLoaded", function() {
    async function fetchCategories() {
        try {
            // Fetch categories from the backend API
            const response = await fetch('http://localhost:8080/api/categories');
            const categories = await response.json();

            // Find the select element
            const categorySelect = document.getElementById('category-select');

            // Populate the select element with categories
            categories.forEach(category => {
                const option = document.createElement('option');
                option.value = category.id; // Use category ID as the value
                option.text = category.name; // Display category name
                categorySelect.appendChild(option);
            });
        } catch (error) {
            console.error('Error fetching categories:', error);
        }
    }

    // Fetch categories when the page is loaded
    fetchCategories();
});

document.getElementById("statementUploadForm").addEventListener("submit", function(e) {
    e.preventDefault();

    const fileInput = document.getElementById("fileInput");
    const file = fileInput.files[0];
    const responseMessage = document.getElementById("responseMessage");

    if (!file) {
        alert("Please select a file first.");
        return;
    }

    const formData = new FormData();
    formData.append("file", file);

    // Send the file using fetch
    fetch("http://localhost:8080/api/expenses/upload", {
        method: "POST",
        body: formData
    })
    .then(response => response.text())
    .then(result => {
        if (result == -1) {
            responseMessage.textContent = "Statement already uploaded!";
        } else {
            responseMessage.textContent = `Added ${result} new expenses!`;
        }
        
    })
    .catch(error => {
        console.error('Error:', error);
        responseMessage.textContent = "Error uploading file";
    });
});


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