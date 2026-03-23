# Test script for GET all users and GET user by ID endpoints with JWT Authentication
# Make sure the backend is running on http://localhost:8000

$BASE_URL = "http://localhost:8000"
$AUTH_ENDPOINT = "$BASE_URL/api/auth"
$USER_ENDPOINT = "$BASE_URL/api/user"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Testing User Endpoints with JWT Auth" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Register test users
Write-Host "Step 1: Registering test users..." -ForegroundColor Yellow

try {
    $registerBody = @{
        name = "Test User 1"
        email = "testuser1@example.com"
        phoneNumber = "123456789"
        password = "password123"
        confirmPassword = "password123"
    } | ConvertTo-Json
    
    $signUpResponse = Invoke-RestMethod -Uri "$AUTH_ENDPOINT/sign-up" `
        -Method Post `
        -ContentType "application/json" `
        -Body $registerBody

    Write-Host "User 1 registered: testuser1@example.com" -ForegroundColor Green
} catch {
    if ($_.Exception.Response.StatusCode -eq '409') {
        Write-Host "User 1 already exists" -ForegroundColor Yellow
    } else {
        Write-Host "Error registering User 1: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "Details: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
}

try {
    $registerBody = @{
        name = "Test User 2"
        email = "testuser2@example.com"
        phoneNumber = "987654321"
        password = "password123"
        confirmPassword = "password123"
    } | ConvertTo-Json
    
    $signUpResponse = Invoke-RestMethod -Uri "$AUTH_ENDPOINT/sign-up" `
        -Method Post `
        -ContentType "application/json" `
        -Body $registerBody

    Write-Host "User 2 registered: testuser2@example.com" -ForegroundColor Green
} catch {
    if ($_.Exception.Response.StatusCode -eq '409') {
        Write-Host "User 2 already exists" -ForegroundColor Yellow
    } else {
        Write-Host "Error registering User 2: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "Details: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
}

# Step 2: Login to get JWT token
Write-Host ""
Write-Host "Step 2: Logging in to get JWT token..." -ForegroundColor Yellow

$loginBody = @{
    email = "testuser1@example.com"
    password = "password123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$AUTH_ENDPOINT/sign-in" `
        -Method Post `
        -ContentType "application/json" `
        -Body $loginBody

    $token = $loginResponse.token
    Write-Host "Login successful" -ForegroundColor Green
    Write-Host "Token: $($token.Substring(0, 50))..." -ForegroundColor Green
} catch {
    Write-Host "Error logging in: $($_.Exception.Message)" -ForegroundColor Red
    Exit
}

# Step 3: Test GET all users endpoint with authentication
Write-Host ""
Write-Host ""
Write-Host "Step 3: Testing GET all users endpoint..." -ForegroundColor Yellow
Write-Host "Endpoint: GET $USER_ENDPOINT/users" -ForegroundColor Cyan
Write-Host ""

try {
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    $getAllResponse = Invoke-RestMethod -Uri "$USER_ENDPOINT/users" `
        -Method Get `
        -Headers $headers

    Write-Host "Status: 200 OK" -ForegroundColor Green
    Write-Host "Response body:" -ForegroundColor Green
    $getAllResponse | ConvertTo-Json | Write-Host
    
    if ($getAllResponse -is [array]) {
        Write-Host ""
        Write-Host "Total users found: $($getAllResponse.Count)" -ForegroundColor Green
        $userIds = $getAllResponse | ForEach-Object { $_.id }
        Write-Host "User IDs: $($userIds -join ', ')" -ForegroundColor Green
    }
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.ErrorDetails) {
        Write-Host "Response: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
}

# Step 4: Test GET user by ID endpoint with authentication
Write-Host ""
Write-Host ""
Write-Host "Step 4: Testing GET user by ID endpoint..." -ForegroundColor Yellow

try {
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    $users = Invoke-RestMethod -Uri "$USER_ENDPOINT/users" `
        -Method Get `
        -Headers $headers
    
    if ($users -is [array] -and $users.Count -gt 0) {
        $firstUserId = $users[0].id
        Write-Host "Testing with first user ID: $firstUserId" -ForegroundColor Cyan
        Write-Host "Endpoint: GET $USER_ENDPOINT/users/$firstUserId" -ForegroundColor Cyan
        Write-Host ""
        
        try {
            $getUserResponse = Invoke-RestMethod -Uri "$USER_ENDPOINT/users/$firstUserId" `
                -Method Get `
                -Headers $headers

            Write-Host "Status: 200 OK" -ForegroundColor Green
            Write-Host "Response body:" -ForegroundColor Green
            $getUserResponse | ConvertTo-Json | Write-Host
        } catch {
            Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
            if ($_.ErrorDetails) {
                Write-Host "Response: $($_.ErrorDetails.Message)" -ForegroundColor Red
            }
        }
    }
} catch {
    Write-Host "Error fetching users: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 5: Test GET user by invalid ID
Write-Host ""
Write-Host ""
Write-Host "Step 5: Testing GET user by invalid ID (error case)..." -ForegroundColor Yellow
Write-Host "Endpoint: GET $USER_ENDPOINT/users/99999" -ForegroundColor Cyan
Write-Host ""

try {
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    $getUserResponse = Invoke-RestMethod -Uri "$USER_ENDPOINT/users/99999" `
        -Method Get `
        -Headers $headers
    
    Write-Host "Response: $($getUserResponse | ConvertTo-Json)" -ForegroundColor Green
} catch {
    if ($_.Exception.Response.StatusCode -eq '404') {
        Write-Host "Status: 404 Not Found" -ForegroundColor Green
        Write-Host "Response body:" -ForegroundColor Green
        Write-Host $_.ErrorDetails.Message -ForegroundColor Green
    } else {
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.ErrorDetails) {
            Write-Host "Response: $($_.ErrorDetails.Message)" -ForegroundColor Red
        }
    }
}

# Step 6: Test endpoint without authentication (should return 403)
Write-Host ""
Write-Host ""
Write-Host "Step 6: Testing GET /users without authentication (should be forbidden)..." -ForegroundColor Yellow
Write-Host "Endpoint: GET $USER_ENDPOINT/users" -ForegroundColor Cyan
Write-Host ""

try {
    $getAllResponse = Invoke-RestMethod -Uri "$USER_ENDPOINT/users" `
        -Method Get `
        -ContentType "application/json"
    
    Write-Host "Unexpected success: $($getAllResponse | ConvertTo-Json)" -ForegroundColor Yellow
} catch {
    if ($_.Exception.Response.StatusCode -eq '403') {
        Write-Host "Status: 403 Forbidden (as expected)" -ForegroundColor Green
        Write-Host "Endpoint correctly requires authentication" -ForegroundColor Green
    } else {
        Write-Host "Unexpected error: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Test Complete" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
