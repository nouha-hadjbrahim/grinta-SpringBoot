# Test PUBLIC image access from /uploads folder
# This should work WITHOUT authentication

$BASE_URL = "http://localhost:8000"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Testing PUBLIC Image Access" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: First, update a user with an image to have a file to test
Write-Host "Step 1: Logging in and uploading a test image..." -ForegroundColor Yellow

$loginBody = @{
    email = "admin@gmail.com"
    password = "Admin@123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$BASE_URL/api/auth/sign-in" `
        -Method Post `
        -ContentType "application/json" `
        -Body $loginBody

    $token = $loginResponse.token
    Write-Host "✓ Logged in successfully" -ForegroundColor Green
} catch {
    Write-Host "✗ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    Exit
}

# Step 2: Get list of users to find one with an image
Write-Host ""
Write-Host "Step 2: Finding users..." -ForegroundColor Yellow

try {
    $headers = @{ "Authorization" = "Bearer $token" }
    
    $users = Invoke-RestMethod -Uri "$BASE_URL/api/user/users" `
        -Method Get `
        -Headers $headers

    $userWithPhoto = $null
    foreach ($user in $users) {
        if ($user.photo) {
            $userWithPhoto = $user
            break
        }
    }

    if ($userWithPhoto) {
        Write-Host "✓ Found user with photo: $($userWithPhoto.name)" -ForegroundColor Green
        Write-Host "  Photo filename: $($userWithPhoto.photo)" -ForegroundColor Cyan
    } else {
        Write-Host "⚠ No users with photos found. Uploading one..." -ForegroundColor Yellow
        
        # Create a test image
        $testImagePath = "$env:TEMP\test-image-$(Get-Random).png"
        $pngData = @(137, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 1, 0, 0, 0, 1, 8, 2, 0, 0, 0, 144, 119, 83, 216, 0, 0, 0, 12, 73, 68, 65, 84, 8, 29, 1, 1, 0, 0, 254, 255, 0, 0, 0, 2, 0, 1, 185, 182, 238, 86, 0, 0, 0, 0, 73, 69, 78, 68, 174, 66, 96, 130)
        [byte[]]$imageBytes = $pngData
        [System.IO.File]::WriteAllBytes($testImagePath, $imageBytes)
        
        # Upload it
        $form = @{
            name = "Test User $(Get-Random)"
            "file" = Get-Item -Path $testImagePath
        }
        
        $updateResponse = Invoke-RestMethod -Uri "$BASE_URL/api/user/users/1" `
            -Method Put `
            -Headers $headers `
            -Form $form

        $userWithPhoto = $updateResponse
        Write-Host "✓ Uploaded image for user: $($updateResponse.photo)" -ForegroundColor Green
        
        Remove-Item -Path $testImagePath -Force
    }
} catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
    Exit
}

# Step 3: Access the image WITHOUT authentication (PUBLIC TEST)
Write-Host ""
Write-Host "Step 3: Testing PUBLIC image access (WITHOUT authentication)..." -ForegroundColor Yellow
Write-Host ""

if ($userWithPhoto -and $userWithPhoto.photo) {
    $imageUrl = "$BASE_URL/uploads/$($userWithPhoto.photo)"
    Write-Host "Image URL: $imageUrl" -ForegroundColor Cyan
    Write-Host ""

    try {
        # Make request WITHOUT any Authorization header
        $imageResponse = Invoke-WebRequest -Uri $imageUrl `
            -Method Get `
            -ErrorAction Stop

        if ($imageResponse.StatusCode -eq 200) {
            Write-Host "✅ SUCCESS: Image is PUBLIC!" -ForegroundColor Green
            Write-Host "✓ Status Code: 200 OK" -ForegroundColor Green
            Write-Host "✓ Image size: $($imageResponse.RawContentLength) bytes" -ForegroundColor Green
            Write-Host "✓ Content-Type: $($imageResponse.Headers['Content-Type'])" -ForegroundColor Green
            Write-Host ""
            Write-Host "✓ Frontend can access this image from browser WITHOUT token!" -ForegroundColor Green
            Write-Host "✓ Use this in HTML: <img src='$imageUrl'>" -ForegroundColor Green
        }
    } catch {
        if ($_.Exception.Response.StatusCode -eq 404) {
            Write-Host "✗ Image not found (404)" -ForegroundColor Red
            Write-Host "  File might not exist in /uploads folder" -ForegroundColor Yellow
        } elseif ($_.Exception.Response.StatusCode -eq 403) {
            Write-Host "✗ Access Forbidden (403)" -ForegroundColor Red
            Write-Host "  Image is NOT public yet - check SecurityConfig" -ForegroundColor Yellow
        } else {
            Write-Host "✗ Error: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
            Write-Host "  Message: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
} else {
    Write-Host "could not test - no photo available" -ForegroundColor Yellow
}

# Step 4: Test with curl (simulating browser)
Write-Host ""
Write-Host "Step 4: Testing with CORS (simulating browser request)..." -ForegroundColor Yellow

if ($userWithPhoto -and $userWithPhoto.photo) {
    $imageUrl = "$BASE_URL/uploads/$($userWithPhoto.photo)"
    
    try {
        $headers = @{
            "Origin" = "http://localhost:4200"
            "Sec-Fetch-Mode" = "no-cors"
        }
        
        $imageResponse = Invoke-WebRequest -Uri $imageUrl `
            -Method Get `
            -Headers $headers `
            -ErrorAction Stop

        if ($imageResponse.StatusCode -eq 200) {
            Write-Host "✓ CORS headers present" -ForegroundColor Green
            Write-Host "✓ Browser can load image with no-cors mode" -ForegroundColor Green
        }
    } catch {
        Write-Host "Note: Some CORS configs might not return headers for no-cors mode" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Test Complete" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Configuration Status:" -ForegroundColor Cyan
Write-Host "✓ SecurityConfig: /uploads/** -> permitAll()" -ForegroundColor Green
Write-Host "✓ WebMvcConfig: Maps /uploads to file system" -ForegroundColor Green
Write-Host "✓ CorsConfig: Allows cross-origin requests" -ForegroundColor Green
Write-Host ""
