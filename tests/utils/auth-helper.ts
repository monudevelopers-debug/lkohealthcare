import { Page } from '@playwright/test';
import { TestUser } from './test-user-generator.js';

class AuthHelper {
  constructor(private page: Page) {}

  async loginAsCustomer(user: TestUser): Promise<void> {
    await this.page.goto('/');
    
    // Wait for login modal or redirect to login page
    const loginButton = this.page.locator('text=Login').first();
    await loginButton.click();
    
    // Fill login form
    await this.page.fill('input[type="email"]', user.email);
    await this.page.fill('input[type="password"]', user.password);
    
    // Submit login
    await this.page.click('button[type="submit"]');
    
    // Wait for successful login (redirect to dashboard)
    await this.page.waitForURL('**/dashboard**', { timeout: 10000 });
  }

  async loginAsProvider(page: Page, user?: TestUser): Promise<void> {
    // Use default test provider if no user provided
    const testUser = user || {
      email: 'provider@example.com',
      password: 'password123',
      name: 'Test Provider',
      role: 'PROVIDER'
    };
    
    await page.goto('/login');
    
    // Fill login form
    await page.fill('input[name="email"]', testUser.email);
    await page.fill('input[name="password"]', testUser.password);
    
    // Submit login
    await page.click('button[type="submit"]');
    
    // Wait for successful login (redirect to dashboard)
    await page.waitForURL('**/', { timeout: 10000 });
  }

  async loginAsAdmin(user: TestUser): Promise<void> {
    await this.page.goto('/login');
    
    // Fill login form
    await this.page.fill('input[type="email"]', user.email);
    await this.page.fill('input[type="password"]', user.password);
    
    // Submit login
    await this.page.click('button[type="submit"]');
    
    // Wait for successful login (redirect to dashboard)
    await this.page.waitForURL('**/', { timeout: 10000 });
  }

  async logout(): Promise<void> {
    // Look for logout button in header or sidebar
    const logoutButton = this.page.locator('text=Logout').first();
    await logoutButton.click();
    
    // Wait for redirect to login or home page
    await this.page.waitForURL('**/login**', { timeout: 5000 });
  }

  async isAuthenticated(): Promise<boolean> {
    try {
      // Check if we're on a protected route and not redirected to login
      const currentUrl = this.page.url();
      return !currentUrl.includes('/login') && !currentUrl.includes('/auth');
    } catch {
      return false;
    }
  }

  async getStoredToken(): Promise<string | null> {
    return await this.page.evaluate(() => {
      return localStorage.getItem('token') || localStorage.getItem('lhc_auth');
    });
  }

  async clearAuth(): Promise<void> {
    await this.page.evaluate(() => {
      localStorage.removeItem('token');
      localStorage.removeItem('lhc_auth');
      localStorage.removeItem('user');
      sessionStorage.clear();
    });
  }
}

export default AuthHelper;
