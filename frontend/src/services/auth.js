// src/services/auth.js
import axios from 'axios';

class AuthService {
  constructor() {
    this.baseURL = 'http://127.0.0.1:8000/api/auth';
  }

  getToken() {
    return localStorage.getItem('token');
  }

  getAuthHeader() {
    const token = this.getToken();
    return token ? { Authorization: `Bearer ${token}` } : {};
  }

  async getCurrentUser() {
    try {
      const token = this.getToken();
      if (!token) {
        throw new Error('No token found');
      }

      const response = await axios.get(`${this.baseURL}/user`, {
        headers: this.getAuthHeader(),
        params: { token: token } // Adding token as query parameter
      });

      return response.data.user;
    } catch (error) {
      console.error('Failed to fetch user details:', error);
      throw error;
    }
  }

  async logout() {
    try {
      const token = this.getToken();
      if (!token) {
        throw new Error('No token found');
      }

      const response = await axios.post(
        `${this.baseURL}/logout`,
        { token: token }, // Adding token in request body
        { headers: this.getAuthHeader() }
      );
      
      if (response.data.status === 'success') {
        localStorage.removeItem('token');
        localStorage.removeItem('tokenExpiration');
        return true;
      }
      return false;
    } catch (error) {
      console.error('Logout failed:', error);
      throw error;
    }
  }
}

export default new AuthService();