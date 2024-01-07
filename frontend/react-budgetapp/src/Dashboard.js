import React, { Component } from 'react';
import { jwtDecode } from 'jwt-decode';

class Dashboard extends Component {
    constructor(props) {
        super(props);
        this.state = {
            accounts: [],
            categories: [],
            isLoading: true,
            error: null,
            showAddAccountForm: false,
            newAccountName: '',
            newAccountBalance: '',
            newAccountType: 'checking',
            editingCategoryId: null,
            newBudgetedAmount: '',
        };
    }

    componentDidMount() {
        this.fetchUserData();
    }

    fetchUserData = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            this.setState({ error: "No token found. Please login.", isLoading: false });
            return;
        }

        const decodedToken = jwtDecode(token);
        const userId = decodedToken.userId;

        const headers = {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
        };

        try {
            const accountsResponse = await fetch(`http://localhost:8082/api/users/${userId}/accounts`, { headers });
            const accountsData = await accountsResponse.json();

            const categoriesResponse = await fetch(`http://localhost:8082/api/users/${userId}/categories`, { headers });
            const categoriesData = await categoriesResponse.json();

            this.setState({
                accounts: accountsData,
                categories: categoriesData,
                isLoading: false,
            });
        } catch (error) {
            this.setState({ error: error.message, isLoading: false });
        }
    };

    toggleAddAccountForm = () => {
        this.setState(prevState => ({
            showAddAccountForm: !prevState.showAddAccountForm,
            newAccountName: '',
            newAccountBalance: '',
            newAccountType: 'checking',
        }));
    };

    handleAccountFormChange = (event) => {
        this.setState({ [event.target.name]: event.target.value });
    };

    handleAddAccount = async (event) => {
        event.preventDefault();
        const { newAccountName, newAccountBalance, newAccountType } = this.state;
        const token = localStorage.getItem('token');
        const decodedToken = jwtDecode(token);
        const userId = decodedToken.userId;

        const account = {
            accountName: newAccountName,
            balance: newAccountBalance,
            accountType: newAccountType,
        };

        const headers = {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
        };

        try {
            const response = await fetch(`http://localhost:8082/api/accounts/${userId}`, {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(account),
            });

            if (!response.ok) {
                throw new Error('Failed to create account');
            }

            const createdAccount = await response.json();
            this.setState(prevState => ({
                accounts: [...prevState.accounts, createdAccount],
                showAddAccountForm: false,
                newAccountName: '',
                newAccountBalance: '',
                newAccountType: 'checking',
            }));
        } catch (error) {
            this.setState({ error: error.message });
        }
    };

    handleEditBudget = (categoryId, currentBudgetedAmount) => {
        this.setState({
            editingCategoryId: categoryId,
            newBudgetedAmount: currentBudgetedAmount.toString(),
        });
    };

    handleBudgetChange = (event) => {
        this.setState({ newBudgetedAmount: event.target.value });
    };

    handleBudgetUpdate = async (categoryId) => {
        const { newBudgetedAmount } = this.state;
        const token = localStorage.getItem('token');
        const headers = {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
        };

        try {
            const response = await fetch(`http://localhost:8082/api/categories/${categoryId}/updateAmount`, {
                method: 'PUT',
                headers: headers,
                body: JSON.stringify({ updatedAmount: newBudgetedAmount }),
            });

            if (!response.ok) {
                throw new Error('Failed to update budgeted amount');
            }

            const updatedCategory = await response.json();
            this.setState(prevState => ({
                categories: prevState.categories.map(category => category.categoryId === categoryId ? updatedCategory : category),
                editingCategoryId: null,
            }));
        } catch (error) {
            this.setState({ error: error.message });
        }
    };

    render() {
        const { accounts, categories, isLoading, error, showAddAccountForm, editingCategoryId, newBudgetedAmount } = this.state;

        if (isLoading) return <div>Loading...</div>;
        if (error) return <div>Error: {error}</div>;

        return (
            <div className="dashboard">
                <h1>Dashboard</h1>

                <button onClick={this.toggleAddAccountForm}>
                    {showAddAccountForm ? "Cancel" : "Add New Account"}
                </button>

                {showAddAccountForm && (
                    <form onSubmit={this.handleAddAccount}>
                        <div>
                            <label>Account Name:</label>
                            <input 
                                type="text"
                                name="newAccountName"
                                value={this.state.newAccountName}
                                onChange={this.handleAccountFormChange}
                                required
                            />
                        </div>
                        <div>
                            <label>Working Balance:</label>
                            <input 
                                type="number"
                                name="newAccountBalance"
                                value={this.state.newAccountBalance}
                                onChange={this.handleAccountFormChange}
                                required
                            />
                        </div>
                        <div>
                            <label>Account Type:</label>
                            <select 
                                name="newAccountType"
                                value={this.state.newAccountType}
                                onChange={this.handleAccountFormChange}
                                required
                            >
                                <option value="checking">Checking</option>
                                <option value="savings">Savings</option>
                            </select>
                        </div>
                        <button type="submit">Create Account</button>
                    </form>
                )}

                <h2>Accounts</h2>
                <ul>
                    {accounts.map(account => (
                        <li key={account.accountId}>
                            {account.accountName}: {account.balance}
                        </li>
                    ))}
                </ul>

                <h2>Categories</h2>
                <ul>
                    {categories.map(category => (
                        <li key={category.categoryId}>
                            {category.name} - 
                            Budgeted: 
                            {editingCategoryId === category.categoryId ? (
                                <input 
                                    type="number" 
                                    value={newBudgetedAmount} 
                                    onChange={this.handleBudgetChange} 
                                    onBlur={() => this.handleBudgetUpdate(category.categoryId)}
                                />
                            ) : (
                                <span onClick={() => this.handleEditBudget(category.categoryId, category.budgetedAmount)}>
                                    {category.budgetedAmount}
                                </span>
                            )},
                            Spent: {category.spent}, 
                            Available: {category.available}
                        </li>
                    ))}
                </ul>
            </div>
        );
    }
}

export default Dashboard;
