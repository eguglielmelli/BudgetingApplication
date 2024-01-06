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
        };
    }

    componentDidMount() {
        this.fetchUserData();
    }

    fetchUserData = async () => {
        try {
            const token = localStorage.getItem('token');
            if (!token) {
                this.setState({ error: "No token found. Please login.", isLoading: false });
                return;
            }
            const decodedToken = jwtDecode(token);
            const userId = decodedToken.userId; // Adjust based on your token's structure

            const headers = {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
            };

            const accountsResponse = await fetch(`http://localhost:8082/api/users/${userId}/accounts`, { headers });
            if (!accountsResponse.ok) {
                throw new Error('Failed to fetch accounts');
            }
            const accountsData = await accountsResponse.json();

            const categoriesResponse = await fetch(`http://localhost:8082/api/users/${userId}/categories`, { headers });
            if (!categoriesResponse.ok) {
                throw new Error('Failed to fetch categories');
            }
            const categoriesData = await categoriesResponse.json();

            this.setState({
                accounts: Array.isArray(accountsData) ? accountsData : [],
                categories: Array.isArray(categoriesData) ? categoriesData : [],
                isLoading: false,
            });
        } catch (error) {
            this.setState({ error: error.message, isLoading: false });
        }
    };

    render() {
        const { accounts, categories, isLoading, error } = this.state;

        if (isLoading) return <div>Loading...</div>;
        if (error) return <div>Error: {error}</div>;

        return (
            <div className="dashboard">
                <h1>Dashboard</h1>
                
                <h2>Accounts</h2>
                <ul>
                    {accounts.map(account => (
                        <li key={account.id}>
                            {account.accountName}: {account.balance}
                        </li>
                    ))}
                </ul>

                <h2>Categories</h2>
                <ul>
                    {categories.map(category => (
                        <li key={category.id}>
                            {category.name} - 
                            Budgeted: {category.budgetedAmount}, 
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