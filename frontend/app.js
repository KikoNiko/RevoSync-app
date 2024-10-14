const apiUrl = 'http://localhost:8080/api/expenses';
const statisticsUrl = 'http://localhost:8080/api/statistics';

document.getElementById('expenseForm').addEventListener('submit', addExpense);
document.getElementById('fetchExpensesBtn').addEventListener('click', fetchExpenses);
document.getElementById('searchByCategoryForm').addEventListener('submit', function(e) {
    e.preventDefault(); // Prevent form submission from reloading the page

    const category = document.getElementById('searchCategory').value.trim();
    if (category) {
        fetchExpensesByCategory(category); // Call the fetch method with the entered category
    }
});

document.getElementById('fetchSpentThisMonth').addEventListener('click', async function() {
    // Get the current year and month
    const currentYear = new Date().getFullYear();
    const currentMonth = new Date().getMonth() + 1;

    try {
        // Fetch the data from the backend API
        const response = await fetch(statisticsUrl + `/spent-this-month?year=${currentYear}&month=${currentMonth}`);
        
        if (response.ok) {
            const amountSpent = await response.json();
            // Update the result on the page
            document.getElementById('expense-result').textContent = `Total spent this month: ${amountSpent}lv`;
        } else {
            console.error('Failed to fetch data from the server');
        }
    } catch (error) {
        console.error('Error fetching the data:', error);
    }
});

document.getElementById('fetchSpentByYear').addEventListener('click', async function() {
    const currentYear = new Date().getFullYear();

    try {
        const response = await fetch(statisticsUrl + `/spent-by-year?year=${currentYear}`);
        
        if (response.ok) {
            const amountSpent = await response.json();
            document.getElementById('expense-result-year').textContent = `Total spent this year: ${amountSpent}lv`;
        } else {
            console.error('Failed to fetch data from the server');
        }
    } catch (error) {
        console.error('Error fetching the data:', error);
    }
});

function displayExpenses(expenses) {
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
                    <button onclick="deleteExpense(${expense.id})">Delete</button>
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


async function fetchExpensesByCategory(category) {
    try {
        const response = await fetch(`http://localhost:8080/api/expenses?cat=${category}`);
        if (!response.ok) {
            throw new Error('Failed to fetch expenses by category');
        }

        // Parse the JSON response
        const expensesByCategory = await response.json();
        displayExpenses(expensesByCategory); // Call display function to update the UI
    } catch (error) {
        console.error(error);
        alert('Error fetching expenses by category');
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
        document.getElementById("responseMessage").textContent = result;
    })
    .catch(error => {
        console.error('Error:', error);
        document.getElementById("responseMessage").textContent = "Error uploading file";
    });
});

