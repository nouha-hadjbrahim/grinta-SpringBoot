# Test script for GET all users and GET user by ID endpoints
# Make sure the backend is running on http://localhost:8000

$BASE_URL = "http://localhost:8000"
$USER_ENDPOINT = "$BASE_URL/api/user"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Testing User Endpoints" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Create test users if they don't exist
Write-Host "Step 1: Creating test users..." -ForegroundColor Yellow
try {
    $body = @{
        email = "testuser1@example.com"
        password = "password123"
    } | ConvertTo-Json
    
    $signUpResponse = Invoke-RestMethod -Uri "$BASE_URL/api/auth/sign-up" `
        -Method Post `
        -ContentType "application/json" `
        -Body $body

    Write-Host "User 1 created: testuser1@example.com" -ForegroundColor Green
} catch {
    Write-Host "Error creating User 1: $($_.Exception.Message)" -ForegroundColor Red
}

try {
    $body = @{
        email = "testuser2@example.com"
        password = "password123"
    } | ConvertTo-Json
    
    $signUpResponse = Invoke-RestMethod -Uri "$BASE_URL/api/auth/sign-up" `
        -Method Post `
        -ContentType "application/json" `
        -Body $body

    Write-Host "User 2 created: testuser2@example.com" -ForegroundColor Green
} catch {
    Write-Host "Error creating User 2: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "Step 2: Testing GET all users endpoint..." -ForegroundColor Yellow
Write-Host "Endpoint: GET $USER_ENDPOINT/users" -ForegroundColor Cyan
Write-Host ""

try {
    $getAllResponse = Invoke-RestMethod -Uri "$USER_ENDPOINT/users" `
        -Method Get `
        -ContentType "application/json"
    
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

# Step 3: Test GET user by ID
Write-Host ""
Write-Host ""
Write-Host "Step 3: Testing GET user by ID endpoint..." -ForegroundColor Yellow

# First, get the list of users to get the first user's ID
try {
    $users = Invoke-RestMethod -Uri "$USER_ENDPOINT/users" `
        -Method Get `
        -ContentType "application/json"
    
    if ($users -is [array] -and $users.Count -gt 0) {
        $firstUserId = $users[0].id
        Write-Host "Testing with first user ID: $firstUserId" -ForegroundColor Cyan
        Write-Host "Endpoint: GET $USER_ENDPOINT/users/$firstUserId" -ForegroundColor Cyan
        Write-Host ""
        
        try {
            $getUserResponse = Invoke-RestMethod -Uri "$USER_ENDPOINT/users/$firstUserId" `
                -Method Get `
                -ContentType "application/json"
            
            Write-Host "Status: 200 OK" -ForegroundColor Green
            Write-Host "Response body:" -ForegroundColor Green
            $getUserResponse | ConvertTo-Json | Write-Host
        } catch {
            Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
            if ($_.ErrorDetails) {
                Write-Host "Response: $($_.ErrorDetails.Message)" -ForegroundColor Red
            }
        }
    } else {
        Write-Host "No users found to test with" -ForegroundColor Yellow
    }
} catch {
    Write-Host "Error fetching users: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 4: Test GET user by invalid ID
Write-Host ""
Write-Host ""
Write-Host "Step 4: Testing GET user by invalid ID (error case)..." -ForegroundColor Yellow
Write-Host "Endpoint: GET $USER_ENDPOINT/users/99999" -ForegroundColor Cyan
Write-Host ""

try {
    $getUserResponse = Invoke-RestMethod -Uri "$USER_ENDPOINT/users/99999" `
        -Method Get `
        -ContentType "application/json"
    
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

Write-Host ""
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Test Complete" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
