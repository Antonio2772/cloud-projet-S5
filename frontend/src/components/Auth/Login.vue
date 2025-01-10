<template>
  <div class="container">
    <div class="row justify-content-center min-vh-100 align-items-center">
      <div class="col-md-5">
        <div class="card shadow-lg border-0">
          <div class="card-body p-5">
            <div class="text-center mb-4">
              <i class="fas fa-user-circle fa-3x text-primary mb-3"></i>
              <h2 class="fw-bold">Login</h2>
              <p class="text-muted">Welcome back!</p>
            </div>

            <div v-if="message" :class="`alert alert-${status} alert-dismissible fade show`" role="alert">
              <i :class="`fas fa-${status === 'success' ? 'check-circle' : 'exclamation-circle'} me-2`"></i>
              {{ message }}
              <div v-if="attemptsLeft !== null" class="mt-2 small">
                Attempts left: {{ attemptsLeft }}
              </div>
              <button type="button" class="btn-close" @click="clearMessage"></button>
            </div>

            <form @submit.prevent="handleSubmit">
              <div class="form-floating mb-3">
                <input 
                  type="email" 
                  class="form-control" 
                  id="email" 
                  v-model="formData.email"
                  :class="{ 'is-invalid': errors.email }"
                  placeholder="name@example.com"
                  required
                >
                <label for="email">
                  <i class="fas fa-envelope text-muted me-2"></i>Email
                </label>
                <div class="invalid-feedback" v-if="errors.email">
                  {{ errors.email[0] }}
                </div>
              </div>

              <div class="form-floating mb-4">
                <input 
                  :type="showPassword ? 'text' : 'password'" 
                  class="form-control" 
                  id="password" 
                  v-model="formData.password"
                  :class="{ 'is-invalid': errors.password }"
                  placeholder="Password"
                  required
                >
                <label for="password">
                  <i class="fas fa-lock text-muted me-2"></i>Password
                </label>
                <span 
                  class="position-absolute end-0 top-50 translate-middle-y me-3" 
                  style="cursor: pointer;"
                  @click="togglePassword"
                >
                  <i :class="`fas fa-eye${showPassword ? '-slash' : ''} text-muted`"></i>
                </span>
                <div class="invalid-feedback" v-if="errors.password">
                  {{ errors.password[0] }}
                </div>
              </div>

              <button 
                class="btn btn-primary w-100 py-3 mb-3" 
                type="submit"
                :disabled="loading"
              >
                <span v-if="!loading">
                  <i class="fas fa-sign-in-alt me-2"></i>Login
                </span>
                <div v-else class="spinner-border spinner-border-sm"></div>
              </button>

              <p class="text-center mb-0">
                Not having an account ? 
                <router-link to="/signup" class="text-primary text-decoration-none">Sign up</router-link>
              </p>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Login',
  data() {
    return {
      formData: {
        email: '',
        password: ''
      },
      loading: false,
      message: '',
      status: '',
      errors: {},
      showPassword: false,
      attemptsLeft: null
    };
  },
  methods: {
    clearMessage() {
      this.message = '';
      this.status = '';
      this.attemptsLeft = null;
    },
    togglePassword() {
      this.showPassword = !this.showPassword;
    },
    async handleSubmit() {
      this.loading = true;
      this.clearMessage();
      this.errors = {};

      try {
        const response = await axios.post('http://127.0.0.1:8000/api/auth/login', this.formData, {
          headers: { 'Content-Type': 'application/json' }
        }); 

        console.log('Sending request to API with data:', this.formData);
        console.log('API response:', response.data);

        if (response.data.status === 'success') {
          localStorage.setItem('token', response.data.token);
          localStorage.setItem('tokenExpiration', response.data.expires_at);
          
          this.$router.push('/dashboard');
        }
      } catch (error) {
        this.status = 'danger';
        
        if (error.response?.status === 401) {
          this.message = error.response.data.message;
          this.attemptsLeft = error.response.data.attempts_left;
        } else if (error.response?.status === 403) {
          this.message = error.response.data.message;
        } else if (error.response?.data?.errors) {
          this.errors = error.response.data.errors;
          this.message = 'Please correct the errors below.';
        } else {
          this.message = error.response?.data?.message || 'Login failed';
        }
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.card {
  border-radius: 1rem;
  transition: all 0.3s ease;
}

.form-control {
  border-radius: 0.5rem;
}

.form-control:focus {
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.15);
}

.btn-primary {
  border-radius: 0.5rem;
  font-weight: 500;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(13, 110, 253, 0.3);
}

.alert {
  border-radius: 0.5rem;
}
</style>