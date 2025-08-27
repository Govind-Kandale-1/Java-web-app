// Simple frontend JavaScript
function showStatus(message, isError = false) {
    const statusDiv = document.getElementById('status');
    statusDiv.textContent = message;
    statusDiv.className = isError ? 'status error' : 'status success';
    statusDiv.style.display = 'block';
    
    // Hide status after 3 seconds
    setTimeout(() => {
        statusDiv.style.display = 'none';
    }, 3000);
}

async function checkHealth() {
    try {
        const response = await fetch('/api/health');
        const message = await response.text();
        showStatus(`✅ Health Check: ${message}`);
    } catch (error) {
        showStatus(`❌ Health Check Failed: ${error.message}`, true);
    }
}

async function loadUsers() {
    try {
        const response = await fetch('/api/users');
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const users = await response.json();
        displayUsers(users);
        showStatus(`✅ Loaded ${users.length} users successfully`);
    } catch (error) {
        showStatus(`❌ Failed to load users: ${error.message}`, true);
    }
}

function displayUsers(users) {
    const container = document.getElementById('users-container');
    
    if (users.length === 0) {
        container.innerHTML = '<p>No users found.</p>';
        return;
    }
    
    const table = `
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Email</th>
                </tr>
            </thead>
            <tbody>
                ${users.map(user => `
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.name}</td>
                        <td>${user.email}</td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
    
    container.innerHTML = table;
}

// Load users automatically when page loads
document.addEventListener('DOMContentLoaded', function() {
    loadUsers();
});