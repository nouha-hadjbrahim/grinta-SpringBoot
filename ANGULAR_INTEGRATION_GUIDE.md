# Angular Integration Guide - JWT Authentication & RBAC

## Overview
This guide shows how to integrate the Backend Sport API's JWT authentication and role-based access control in an Angular application.

## 1. Authentication Service

### Create `auth.service.ts`
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface User {
  id: number;
  email: string;
  role: 'USER' | 'ADMIN';
}

export interface AuthResponse {
  token: string;
  user: User;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8000/api/auth';
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser$: Observable<User | null>;
  private tokenSubject: BehaviorSubject<string | null>;
  public token$: Observable<string | null>;

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<User | null>(
      this.getUserFromStorage()
    );
    this.currentUser$ = this.currentUserSubject.asObservable();

    this.tokenSubject = new BehaviorSubject<string | null>(
      this.getTokenFromStorage()
    );
    this.token$ = this.tokenSubject.asObservable();
  }

  // Login
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/sign-in`, credentials)
      .pipe(
        map((response) => {
          this.saveAuth(response);
          return response;
        })
      );
  }

  // Register
  register(user: any): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/sign-up`, user)
      .pipe(
        map((response) => {
          // Note: signup doesn't return token, only user info
          if (response.user) {
            this.currentUserSubject.next(response.user);
            localStorage.setItem('user', JSON.stringify(response.user));
          }
          return response;
        })
      );
  }

  // Logout
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
    this.tokenSubject.next(null);
  }

  // Get current user
  get currentUser(): User | null {
    return this.currentUserSubject.value;
  }

  // Get current token
  get token(): string | null {
    return this.tokenSubject.value;
  }

  // Check if authenticated
  get isAuthenticated(): boolean {
    return !!this.token;
  }

  // Check if admin
  get isAdmin(): boolean {
    return this.currentUser?.role === 'ADMIN';
  }

  // Check if user
  get isUser(): boolean {
    return this.currentUser?.role === 'USER';
  }

  // Private helpers
  private saveAuth(response: AuthResponse): void {
    localStorage.setItem('token', response.token);
    localStorage.setItem('user', JSON.stringify(response.user));
    this.currentUserSubject.next(response.user);
    this.tokenSubject.next(response.token);
  }

  private getTokenFromStorage(): string | null {
    return localStorage.getItem('token');
  }

  private getUserFromStorage(): User | null {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }
}
```

## 2. JWT Interceptor

### Create `jwt.interceptor.ts`
```typescript
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    const token = this.authService.token;

    // Add JWT token to requests if it exists
    if (token && !request.url.includes('/auth/')) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 || error.status === 403) {
          // Token invalid or user not authorized
          this.authService.logout();
          this.router.navigate(['/login']);
        }
        return throwError(() => error);
      })
    );
  }
}
```

## 3. Role-Based Route Guard

### Create `role.guard.ts`
```typescript
import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
} from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class RoleGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    const requiredRole = route.data['role'] as string;
    const userRole = this.authService.currentUser?.role;

    // Allow access if user has required role or is admin
    if (userRole === requiredRole || userRole === 'ADMIN') {
      return true;
    }

    // Redirect to unauthorized page
    this.router.navigate(['/unauthorized']);
    return false;
  }
}
```

### Create `auth.guard.ts`
```typescript
import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
} from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    if (this.authService.isAuthenticated) {
      return true;
    }

    // Redirect to login if not authenticated
    this.router.navigate(['/login']);
    return false;
  }
}
```

## 4. App Module Configuration

### Update `app.module.ts`
```typescript
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { JwtInterceptor } from './services/jwt.interceptor';
import { AuthService } from './services/auth.service';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    ReactiveFormsModule,
  ],
  providers: [
    AuthService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
```

## 5. Routing Configuration

### Create `app-routing.module.ts`
```typescript
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './services/auth.guard';
import { RoleGuard } from './services/role.guard';

// Components (import your components)
// import { LoginComponent } from './pages/login/login.component';
// import { AdminComponent } from './pages/admin/admin.component';
// import { UserComponent } from './pages/user/user.component';
// import { UnauthorizedComponent } from './pages/unauthorized/unauthorized.component';

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  // Public routes
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'unauthorized', component: UnauthorizedComponent },

  // Protected routes
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
  
  // User-specific routes
  {
    path: 'user',
    component: UserComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'USER' },
  },
  
  // Admin-specific routes
  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { role: 'ADMIN' },
  },

  { path: '**', redirectTo: '/home' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
```

## 6. Login Component

### Create `login.component.ts`
```typescript
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  loading = false;
  submitted = false;
  error = '';

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService
  ) {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  ngOnInit(): void {}

  get f() {
    return this.loginForm.controls;
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.authService.login(this.loginForm.value).subscribe({
      next: (response) => {
        // Navigate based on role
        if (response.user.role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/user']);
        }
      },
      error: (error) => {
        this.error = error.error || 'Login failed';
        this.loading = false;
      },
    });
  }
}
```

### Create `login.component.html`
```html
<div class="login-container">
  <h2>Login</h2>
  
  <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
    <div class="form-group">
      <label for="email">Email</label>
      <input
        id="email"
        type="email"
        formControlName="email"
        class="form-control"
        [ngClass]="{ 'is-invalid': submitted && f['email'].errors }"
      />
      <div *ngIf="submitted && f['email'].errors" class="invalid-feedback">
        <div *ngIf="f['email'].errors['required']">Email is required</div>
        <div *ngIf="f['email'].errors['email']">Email is invalid</div>
      </div>
    </div>

    <div class="form-group">
      <label for="password">Password</label>
      <input
        id="password"
        type="password"
        formControlName="password"
        class="form-control"
        [ngClass]="{ 'is-invalid': submitted && f['password'].errors }"
      />
      <div *ngIf="submitted && f['password'].errors" class="invalid-feedback">
        <div *ngIf="f['password'].errors['required']">Password is required</div>
        <div *ngIf="f['password'].errors['minlength']">
          Password must be at least 6 characters
        </div>
      </div>
    </div>

    <div *ngIf="error" class="alert alert-danger">{{ error }}</div>

    <button
      type="submit"
      class="btn btn-primary"
      [disabled]="loading"
    >
      <span *ngIf="loading" class="spinner-border spinner-border-sm mr-3"></span>
      Login
    </button>
  </form>

  <div class="register-link">
    Don't have an account?
    <a routerLink="/register">Register here</a>
  </div>
</div>
```

## 7. App Component with Role-Based Navigation

### Create `app.component.ts`
```typescript
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, User } from './services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  currentUser: User | null = null;
  isAuthenticated = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user;
      this.isAuthenticated = !!user;
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  navigateByRole(): void {
    if (this.currentUser?.role === 'ADMIN') {
      this.router.navigate(['/admin']);
    } else if (this.currentUser?.role === 'USER') {
      this.router.navigate(['/user']);
    }
  }
}
```

### Create `app.component.html`
```html
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container-fluid">
    <a class="navbar-brand" href="#">Grinta Sport</a>
    <button
      class="navbar-toggler"
      type="button"
      data-bs-toggle="collapse"
      data-bs-target="#navbarNav"
    >
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
      <ul class="navbar-nav ms-auto">
        <li class="nav-item" *ngIf="isAuthenticated">
          <span class="nav-link">Welcome, {{ currentUser?.email }}</span>
        </li>
        <li class="nav-item" *ngIf="isAuthenticated && currentUser?.role === 'ADMIN'">
          <a class="nav-link" routerLink="/admin">Admin</a>
        </li>
        <li class="nav-item" *ngIf="isAuthenticated && currentUser?.role === 'USER'">
          <a class="nav-link" routerLink="/user">Profile</a>
        </li>
        <li class="nav-item" *ngIf="!isAuthenticated">
          <a class="nav-link" routerLink="/login">Login</a>
        </li>
        <li class="nav-item" *ngIf="isAuthenticated">
          <button class="btn btn-outline-light" (click)="logout()">
            Logout
          </button>
        </li>
      </ul>
    </div>
  </div>
</nav>

<main class="container mt-4">
  <router-outlet></router-outlet>
</main>
```

## 8. User Profile Component

### Create `user.component.ts`
```typescript
import { Component, OnInit } from '@angular/core';
import { AuthService, User } from '../../services/auth.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css'],
})
export class UserComponent implements OnInit {
  currentUser: User | null = null;
  loading = true;
  error = '';

  constructor(
    private authService: AuthService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.currentUser;
    if (!this.currentUser) {
      // Redirect to login if not authenticated
      // This should be handled by AuthGuard, but just in case
      this.error = 'User not found';
    }
    this.loading = false;
  }
}
```

## 9. Admin Component

### Create `admin.component.ts`
```typescript
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';

interface Dashboard {
  totalUsers: number;
  adminUsers: number;
  regularUsers: number;
}

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css'],
})
export class AdminComponent implements OnInit {
  dashboard: Dashboard | null = null;
  loading = true;
  error = '';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.http
      .get<Dashboard>('http://localhost:8000/api/admin/dashboard')
      .subscribe({
        next: (data) => {
          this.dashboard = data;
          this.loading = false;
        },
        error: (error) => {
          this.error = 'Failed to load dashboard';
          this.loading = false;
        },
      });
  }
}
```

## Key Takeaways

1. **Authentication Service**: Centralized auth logic, stores token and user
2. **JWT Interceptor**: Automatically adds token to requests
3. **Guards**: Protect routes based on authentication status and role
4. **Observable-based**: Use RxJS observables for reactive updates
5. **Error Handling**: Handle 401/403 errors and redirect to login
6. **Role-based UI**: Show/hide components based on user role
7. **LocalStorage**: Persist auth state across page refreshes

## Testing Checklist

- [ ] Login redirects to admin for ADMIN users
- [ ] Login redirects to user profile for USER users
- [ ] Protected routes blocked without authentication
- [ ] Admin routes blocked for non-admin users
- [ ] Logout clears token and user data
- [ ] JWT token automatically added to API requests
- [ ] 401 errors trigger logout and redirect to login
- [ ] Navigation menu shows appropriate links for user role
- [ ] Page refresh maintains authentication state
