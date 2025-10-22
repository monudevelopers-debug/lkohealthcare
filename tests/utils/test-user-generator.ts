import { faker } from '@faker-js/faker';

export interface TestUser {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  role: 'CUSTOMER' | 'PROVIDER' | 'ADMIN';
}

export class TestUserGenerator {
  private static instance: TestUserGenerator;
  private usedEmails: Set<string> = new Set();

  static getInstance(): TestUserGenerator {
    if (!TestUserGenerator.instance) {
      TestUserGenerator.instance = new TestUserGenerator();
    }
    return TestUserGenerator.instance;
  }

  generateCustomer(): TestUser {
    const email = this.generateUniqueEmail();
    return {
      email,
      password: 'TestPassword123!',
      firstName: faker.person.firstName(),
      lastName: faker.person.lastName(),
      phone: faker.phone.number('+91##########'),
      role: 'CUSTOMER'
    };
  }

  generateProvider(): TestUser {
    const email = this.generateUniqueEmail();
    return {
      email,
      password: 'TestPassword123!',
      firstName: faker.person.firstName(),
      lastName: faker.person.lastName(),
      phone: faker.phone.number('+91##########'),
      role: 'PROVIDER'
    };
  }

  generateAdmin(): TestUser {
    const email = this.generateUniqueEmail();
    return {
      email,
      password: 'TestPassword123!',
      firstName: faker.person.firstName(),
      lastName: faker.person.lastName(),
      phone: faker.phone.number('+91##########'),
      role: 'ADMIN'
    };
  }

  private generateUniqueEmail(): string {
    let email: string;
    do {
      email = faker.internet.email();
    } while (this.usedEmails.has(email));
    
    this.usedEmails.add(email);
    return email;
  }

  clearUsedEmails(): void {
    this.usedEmails.clear();
  }
}

export const testUserGenerator = TestUserGenerator.getInstance();
