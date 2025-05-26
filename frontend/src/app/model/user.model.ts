export interface User {
  email: string;
  role: 'user' | 'admin' | 'secretary' | 'manager';
}
