document.addEventListener('DOMContentLoaded', () => {
    const apiUrl = 'http://localhost:8080/api/expenses'; // Adjust as needed
    const form = document.getElementById('expense-form');
    const expenseList = document.getElementById('expense-list');
    const totalAmountElem = document.getElementById('total-amount');

    // Function to fetch and display expenses
    async function fetchExpenses() {
        try {
            const response = await fetch(apiUrl);
            const expenses = await response.json();
            updateExpenseList(expenses);
            updateTotalAmount(expenses);
        } catch (error) {
            console.error('Error fetching expenses:', error);
        }
    }

    // Function to update expense list in the DOM
    function updateExpenseList(expenses) {
        expenseList.innerHTML = '';
        expenses.forEach(expense => {
            const li = document.createElement('li');
            li.textContent = `${expense.description} - $${expense.amount.toFixed(2)} (${new Date(expense.date).toLocaleDateString()})`;
            expenseList.appendChild(li);
        });
    }

    // Function to update total amount in the DOM
    function updateTotalAmount(expenses) {
        const totalAmount = expenses.reduce((sum, expense) => sum + expense.amount, 0);
        totalAmountElem.textContent = `$${totalAmount.toFixed(2)}`;
    }

    // Handle form submission
    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        const description = document.getElementById('description').value;
        const amount = parseFloat(document.getElementById('amount').value);
        const date = document.getElementById('date').value;

        if (description && !isNaN(amount) && date) {
            try {
                await fetch(apiUrl, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ description, amount, date }),
                });
                fetchExpenses();
                form.reset();
            } catch (error) {
                console.error('Error adding expense:', error);
            }
        }
    });

    // Initial fetch of expenses
    fetchExpenses();
});
