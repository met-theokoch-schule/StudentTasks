
// Session Keep-Alive Ping
// This script pings the server every 5 minutes to prevent session timeout
(function() {
    'use strict';
    
    const PING_INTERVAL = 5 * 60 * 1000; // 5 minutes in milliseconds
    
    function ping() {
        fetch('/ping')
            .then(response => response.text())
            .then(data => {
                console.log('Session ping:', data);
            })
            .catch(error => {
                console.error('Session ping failed:', error);
            });
    }
    
    // Start pinging every 5 minutes
    setInterval(ping, PING_INTERVAL);
    
    // Optional: Send initial ping on load
    // ping();
    
    console.log('Session keep-alive initialized (ping every 5 minutes)');
})();
