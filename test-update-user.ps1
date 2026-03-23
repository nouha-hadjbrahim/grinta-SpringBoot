# Test script for UPDATE USER endpoint with image upload
# Make sure the backend is running on http://localhost:8000

$BASE_URL = "http://localhost:8000"
$AUTH_ENDPOINT = "$BASE_URL/api/auth"
$USER_ENDPOINT = "$BASE_URL/api/user"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Testing UPDATE USER Endpoint" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Login to get JWT token
Write-Host "Step 1: Logging in to get JWT token..." -ForegroundColor Yellow

$loginBody = @{
    email = "admin@gmail.com"
    password = "Admin@123"
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
    Write-Host "Note: Adjust email and password according to your test user" -ForegroundColor Yellow
    Exit
}

# Step 2: Get a user ID to update
Write-Host ""
Write-Host "Step 2: Fetching users to find one to update..." -ForegroundColor Yellow

try {
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    $users = Invoke-RestMethod -Uri "$USER_ENDPOINT/users" `
        -Method Get `
        -Headers $headers

    if ($users -is [array] -and $users.Count -gt 0) {
        $targetUserId = $users[0].id
        $targetUserName = $users[0].name
        Write-Host "Found user: ID=$targetUserId, Name=$targetUserName" -ForegroundColor Green
    } else {
        Write-Host "No users found" -ForegroundColor Red
        Exit
    }
} catch {
    Write-Host "Error fetching users: $($_.Exception.Message)" -ForegroundColor Red
    Exit
}

# Step 3: Test UPDATE without image (form data)
Write-Host ""
Write-Host ""
Write-Host "Step 3: Testing UPDATE user without image..." -ForegroundColor Yellow
Write-Host "Endpoint: PUT $USER_ENDPOINT/users/$targetUserId" -ForegroundColor Cyan
Write-Host ""

try {
    # Create form data
    $form = @{
        name = "Updated Name $(Get-Random)"
        email = "updated+$(Get-Random)@example.com"
        phoneNumber = "+1234567890"
        status = "ACTIVE"
    }

    $headers = @{
        "Authorization" = "Bearer $token"
    }

    $updateResponse = Invoke-RestMethod -Uri "$USER_ENDPOINT/users/$targetUserId" `
        -Method Put `
        -Headers $headers `
        -Form $form

    Write-Host "Status: 200 OK" -ForegroundColor Green
    Write-Host "Response:" -ForegroundColor Green
    $updateResponse | ConvertTo-Json | Write-Host
    Write-Host ""
    Write-Host "User successfully updated!" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    if ($_.Exception.Response.StatusCode -eq 405) {
        Write-Host "STATUS 405 - Method Not Allowed" -ForegroundColor Red
        Write-Host "The endpoint doesn't support PUT method on this path" -ForegroundColor Yellow
    }
    Write-Host "Message: $($_.ErrorDetails.Message)" -ForegroundColor Red
}

# Step 4: Test UPDATE with image (if you have a test image)
Write-Host ""
Write-Host ""
Write-Host "Step 4: Testing UPDATE user with image..." -ForegroundColor Yellow

$testImagePath = "C:\temp\test-image.jpg"
if (Test-Path $testImagePath) {
    Write-Host "Found test image at: $testImagePath" -ForegroundColor Cyan
    
    try {
        $form = @{
            name = "Updated With Image $(Get-Random)"
            email = "withimage+$(Get-Random)@example.com"
            image = Get-Item -Path $testImagePath
        }

        $headers = @{
            "Authorization" = "Bearer $token"
        }

        $updateResponse = Invoke-RestMethod -Uri "$USER_ENDPOINT/users/$targetUserId" `
            -Method Put `
            -Headers $headers `
            -Form $form

        Write-Host "Status: 200 OK" -ForegroundColor Green
        Write-Host "Updated photo: $($updateResponse.photo)" -ForegroundColor Green
        Write-Host "Image upload successful!" -ForegroundColor Green
    } catch {
        Write-Host "Error uploading image: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "Test image not found at $testImagePath" -ForegroundColor Yellow
    Write-Host "To test with image, create a test image at: $testImagePath" -ForegroundColor Yellow
}

# Step 5: Test UPDATE with invalid ID (should return 404)
Write-Host ""
Write-Host ""
Write-Host "Step 5: Testing UPDATE with invalid ID (error case)..." -ForegroundColor Yellow
Write-Host "Endpoint: PUT $USER_ENDPOINT/users/99999" -ForegroundColor Cyan
Write-Host ""

try {
    $form = @{
        name = "This should fail"
    }

    $headers = @{
        "Authorization" = "Bearer $token"
    }

    $updateResponse = Invoke-RestMethod -Uri "$USER_ENDPOINT/users/99999" `
        -Method Put `
        -Headers $headers `
        -Form $form

    Write-Host "Unexpected success: $($updateResponse | ConvertTo-Json)" -ForegroundColor Yellow
} catch {
    if ($_.Exception.Response.StatusCode -eq '404') {
        Write-Host "Status: 404 Not Found (as expected)" -ForegroundColor Green
        Write-Host "Error message: $($_.ErrorDetails.Message)" -ForegroundColor Green
    } else {
        Write-Host "Error: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
        Write-Host "Message: $($_.ErrorDetails.Message)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Test Complete" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Endpoint: PUT /api/user/users/{id}" -ForegroundColor Green
Write-Host "Status: Ready for testing" -ForegroundColor Green
Write-Host ""
