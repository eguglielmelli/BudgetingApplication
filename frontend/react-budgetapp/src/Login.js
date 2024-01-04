import React, { useState } from 'react';

function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [loginSuccessMessage, setLoginSuccessMessage] = useState(''); // Add state for login success message

    const handleSubmit = async (event) => {
        event.preventDefault();
        setErrorMessage(''); // Reset error message
        setLoginSuccessMessage(''); // Reset login success message

        try {
            const response = await fetch('http://localhost:8082/api/users/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email: username, password: password }),
            });

            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }

            const data = await response.json();
            console.log('Login successful:', data);
            setLoginSuccessMessage('Login successful!'); // Set success message
            // Additional handling like storing the token, redirecting, etc.
        } catch (error) {
            console.error('Login failed:', error);
            setErrorMessage('Login failed. Please check your credentials.');
        }
    };

    return (
        <div>
            <h2>Login</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Username:</label>
                    <input 
                        type="text" 
                        value={username} 
                        onChange={(e) => setUsername(e.target.value)} 
                    />
                </div>
                <div>
                    <label>Password:</label>
                    <input 
                        type="password" 
                        value={password} 
                        onChange={(e) => setPassword(e.target.value)} 
                    />
                </div>
                {errorMessage && <div style={{color: 'red'}}>{errorMessage}</div>}
                {loginSuccessMessage && <div style={{color: 'green'}}>{loginSuccessMessage}</div>} {/* Display success message */}
                <div>
                    <button type="submit">Login</button>
                </div>
            </form>
        </div>
    );
}

export default Login;