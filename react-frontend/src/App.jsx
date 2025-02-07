import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import "./css/App.css";

const apiUrl = "http://localhost:8080/api/expenses";

function App() {
  return (
    <Router>
      <NavBar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/statistics" element={<Statistics />} />
      </Routes>
      <Footer />
    </Router>
  );
}

const NavBar = () => {
  return (
    <nav className="navContainer">
      <ul>
        <li>
          <Link to="#" className="burger-menu">
            <i className="fa-solid fa-bars"></i>
          </Link>
          <div className="menu">
            <Link to="/">Home</Link>
            <Link to="#upload">Upload Statement</Link>
            <Link to="/statistics">Statistics</Link>
          </div>
        </li>
      </ul>
    </nav>
  );
};

const Home = () => {
  return (
    <main>
      <ExpenseForm />
      <UploadSection />
      <ExpenseList />
    </main>
  );
};

const ExpenseForm = () => {
  const [amount, setAmount] = useState("");
  const [category, setCategory] = useState("");
  const [date, setDate] = useState("");
  const [comment, setComment] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    const expense = { amount: parseFloat(amount), category, date, comment };

    await fetch(apiUrl, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(expense),
    });

    setAmount("");
    setCategory("");
    setDate("");
    setComment("");
  };

  return (
    <section className="formSection">
      <div className="container">
        <form onSubmit={handleSubmit} className="formContainer addExpenseContainer">
          <legend>Add a new expense</legend>

          <label>Enter amount in lv. :</label>
          <input type="number" value={amount} onChange={(e) => setAmount(e.target.value)} required />

          <label>Select category :</label>
          <input type="text" value={category} onChange={(e) => setCategory(e.target.value)} required />

          <label>Enter the date :</label>
          <input type="date" value={date} onChange={(e) => setDate(e.target.value)} required />

          <label>Comment :</label>
          <input type="text" value={comment} onChange={(e) => setComment(e.target.value)} />

          <button type="submit">Add Expense</button>
        </form>
      </div>
    </section>
  );
};

const UploadSection = () => {
  return (
    <section className="uploadSection">
      <h2>Upload Revolut Statement</h2>
      <input type="file" />
      <button>Upload</button>
    </section>
  );
};

const ExpenseList = () => {
  const [expenses, setExpenses] = useState([]);

  useEffect(() => {
    fetch(apiUrl + "/all")
      .then((res) => res.json())
      .then(setExpenses);
  }, []);

  return (
    <section>
      <h2>My Expenses</h2>
      <table>
        <thead>
          <tr>
            <th>Amount</th>
            <th>Category</th>
            <th>Date</th>
            <th>Comment</th>
          </tr>
        </thead>
        <tbody>
          {expenses.map((expense) => (
            <tr key={expense.id}>
              <td>{expense.amount}</td>
              <td>{expense.category}</td>
              <td>{expense.date}</td>
              <td>{expense.comment}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
};

const Footer = () => {
  return (
    <footer>
      <p>Expense tracker project 2024</p>
    </footer>
  );
};

const Statistics = () => {
  return <h2>Statistics Page</h2>;
};

export default App;
