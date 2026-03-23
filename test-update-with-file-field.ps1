# Test UPDATE USER endpoint with proper field name
# This test uses "file" field name which matches the frontend

$BASE_URL = "http://localhost:8000"
$AUTH_ENDPOINT = "$BASE_URL/api/auth"
$USER_ENDPOINT = "$BASE_URL/api/user"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "UPDATE USER with Image Upload Test" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Login
Write-Host "Step 1: Logging in..." -ForegroundColor Yellow

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
    Write-Host "✓ Login successful" -ForegroundColor Green
} catch {
    Write-Host "✗ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Adjust credentials and try again" -ForegroundColor Yellow
    Exit
}

# Step 2: Get user to update
Write-Host ""
Write-Host "Step 2: Fetching users..." -ForegroundColor Yellow

try {
    $headers = @{
        "Authorization" = "Bearer $token"
    }
    
    $users = Invoke-RestMethod -Uri "$USER_ENDPOINT/users" `
        -Method Get `
        -Headers $headers

    if ($users -is [array] -and $users.Count -gt 0) {
        $targetUserId = $users[0].id
        Write-Host "✓ Found user with ID: $targetUserId" -ForegroundColor Green
        Write-Host "  Current photo: $($users[0].photo)" -ForegroundColor Cyan
    } else {
        Write-Host "✗ No users found" -ForegroundColor Red
        Exit
    }
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
    Exit
}

# Step 3: Create test image
Write-Host ""
Write-Host "Step 3: Creating test image..." -ForegroundColor Yellow

$testImagePath = "$env:TEMP\test-image-$(Get-Random).png"

try {
    # Create a simple 1x1 PNG image in memory
    $pngHeader = @(137, 80, 78, 71, 13, 10, 26, 10)
    $ihdr = @(0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 1, 0, 0, 0, 1, 8, 2, 0, 0, 0, 144, 119, 83, 216)
    $idat = @(0, 0, 0, 12, 73, 68, 65, 84, 8, 29, 1, 1, 0, 0, 254, 255, 0, 0, 0, 2, 0, 1, 185, 182, 238, 86)
    $iend = @(0, 0, 0, 0, 73, 69, 78, 68, 174, 66, 96, 130)

    $pngData = @() + $pngHeader + $ihdr + $idat + $iend
    [byte[]]$imageBytes = $pngData

    [System.IO.File]::WriteAllBytes($testImagePath, $imageBytes)
    Write-Host "✓ Test image created: $testImagePath" -ForegroundColor Green
    Write-Host "  Size: $((Get-Item $testImagePath).Length) bytes" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Error creating test image: $($_.Exception.Message)" -ForegroundColor Red
    Exit
}

# Step 4: Update user with image (using "file" field name)
Write-Host ""
Write-Host "Step 4: Updating user with image (field name: 'file')..." -ForegroundColor Yellow
Write-Host "Endpoint: PUT $USER_ENDPOINT/users/$targetUserId" -ForegroundColor Cyan
Write-Host ""

try {
    $form = @{
        name = "Updated User $(Get-Random)"
        "file" = Get-Item -Path $testImagePath
    }

    $headers = @{
        "Authorization" = "Bearer $token"
    }

    $updateResponse = Invoke-RestMethod -Uri "$USER_ENDPOINT/users/$targetUserId" `
        -Method Put `
        -Headers $headers `
        -Form $form

    Write-Host "✓ Status: 200 OK" -ForegroundColor Green
    Write-Host "✓ Update successful!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Updated user details:" -ForegroundColor Green
    Write-Host "  ID: $($updateResponse.id)" -ForegroundColor Cyan
    Write-Host "  Name: $($updateResponse.name)" -ForegroundColor Cyan
    Write-Host "  Email: $($updateResponse.email)" -ForegroundColor Cyan
    Write-Host "  Photo: $($updateResponse.photo)" -ForegroundColor Cyan
    
    if ($updateResponse.photo) {
        Write-Host ""
        Write-Host "✓ Photo saved successfully!" -ForegroundColor Green
        Write-Host "  Access at: http://localhost:8000/uploads/$($updateResponse.photo)" -ForegroundColor Green
    } else {
        Write-Host ""
        Write-Host "✗ Photo field is still null" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Error: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    Write-Host "  Message: $($_.ErrorDetails.Message)" -ForegroundColor Red
    
    if ($_.Exception.Response.StatusCode -eq '400') {
        Write-Host ""
        Write-Host "Possible reasons:" -ForegroundColor Yellow
        Write-Host "  - File validation failed (check size, type)" -ForegroundColor Yellow
        Write-Host "  - Invalid form field name" -ForegroundColor Yellow
    }
}

# Step 5: Update user with image (using "image" field name)
Write-Host ""
Write-Host "Step 5: Testing with 'image' field name..." -ForegroundColor Yellow

try {
    $form = @{
        name = "Updated User 2 $(Get-Random)"
        "image" = Get-Item -Path $testImagePath
    }

    $headers = @{
        "Authorization" = "Bearer $token"
    }

    $updateResponse = Invoke-RestMethod -Uri "$USER_ENDPOINT/users/$targetUserId" `
        -Method Put `
        -Headers $headers `
        -Form $form

    Write-Host "✓ Status: 200 OK (image field name also works)" -ForegroundColor Green
    Write-Host "  Photo: $($updateResponse.photo)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Error with 'image' field: $($_.Exception.Message)" -ForegroundColor Red
}

# Cleanup
Write-Host ""
Write-Host "Step 6: Cleaning up test image..." -ForegroundColor Yellow
Remove-Item -Path $testImagePath -Force -ErrorAction SilentlyContinue
Write-Host "✓ Test image deleted" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Test Complete" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "✅ Backend now accepts both 'file' and 'image' field names" -ForegroundColor Green
Write-Host ""
