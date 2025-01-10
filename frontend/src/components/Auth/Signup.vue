<template>
  <div class="container">
    <div class="row justify-content-center min-vh-100 align-items-center">
      <div class="col-md-6">
        <div class="card shadow-lg border-0">
          <div class="card-body p-5">
            <!-- Header -->
            <div class="text-center mb-4">
              <h2 class="fw-bold">Create Account</h2>
              <p class="text-muted">Join us today!</p>
            </div>

            <!-- Alert Messages -->
            <div v-if="message" :class="`alert alert-${status === 'success' ? 'success' : 'danger'} alert-dismissible fade show`" role="alert">
              <i :class="`fas fa-${status === 'success' ? 'check-circle' : 'exclamation-circle'} me-2`"></i>
              {{ message }}
              <button type="button" class="btn-close" @click="clearMessage"></button>
            </div>


            <!-- Signup Form -->
            <form @submit.prevent="handleSubmit" class="needs-validation" novalidate>
              <!-- Name Input -->
              <div class="form-floating mb-3">
                <input 
                  type="text" 
                  class="form-control" 
                  id="name" 
                  v-model="formData.name" 
                  :class="{ 'is-invalid': errors.name }"
                  placeholder="Your Name"
                  required
                >
                <label for="name">
                  <i class="fas fa-user text-muted me-2"></i>Full Name
                </label>
                <div class="invalid-feedback" v-if="errors.name">
                  {{ errors.name[0] }}
                </div>
              </div>

              <!-- Email Input -->
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
                  <i class="fas fa-envelope text-muted me-2"></i>Email Address
                </label>
                <div class="invalid-feedback" v-if="errors.email">
                  {{ errors.email[0] }}
                </div>
              </div>

              <!-- Password Input -->
              <div class="form-floating mb-3">
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

              <!-- Birthday Input -->
              <div class="form-floating mb-4">
                <input 
                  type="date" 
                  class="form-control" 
                  id="birthday" 
                  v-model="formData.birthday"
                  :class="{ 'is-invalid': errors.birthday }"
                  required
                >
                <label for="birthday">
                  <i class="fas fa-calendar text-muted me-2"></i>Birthday
                </label>
                <div class="invalid-feedback" v-if="errors.birthday">
                  {{ errors.birthday[0] }}
                </div>
              </div>

              <!-- Submit Button -->
              <button 
                class="btn btn-primary w-100 py-3 mb-3 position-relative" 
                type="submit"
                :disabled="loading"
              >
                <span v-if="!loading">
                  <i class="fas fa-user-plus me-2"></i>Sign Up
                </span>
                <div v-else class="spinner-border spinner-border-sm" role="status">
                  <span class="visually-hidden">Loading...</span>
                </div>
              </button>

              <!-- Login Link -->
              <p class="text-center mb-0">
                Already have an account? 
                <router-link to="/login" class="text-primary text-decoration-none">Login</router-link>
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
  name: 'Signup',
  data() {
    return {
      formData: {
        name: '',
        email: '',
        password: '',
        birthday: ''
      },
      loading: false,
      message: '',
      status: '',
      errors: {},
      showPassword: false,
      showVerification: false
    };
  },
  methods: {
    clearMessage() {
      this.message = '';
      this.status = '';
    },
    togglePassword() {
      this.showPassword = !this.showPassword;
    },
    async handleSubmit() {
      this.loading = true;
      this.clearMessage();
      this.errors = {};

      try {
        const api = 'http://127.0.0.1:8000/api/auth/signup';
        const response = await axios.post(api, this.formData, {
          headers: { 'Content-Type': 'application/json' }
        });
        console.log('Sending request to API with data:', this.formData);
        console.log('API response:', response.data);

        if (response.data.status === 'error') {
          this.status = 'error';
          this.errors = response.data.errors;
          this.message = response.data.errors;

          return;
        }

        this.status = 'success';
        this.message = response.data.message;

        // Emit success event with user email
        this.$emit('signup-success', this.formData.email);

        // Clear form on success
        this.formData = {
          name: '',
          email: '',
          password: '',
          birthday: ''
        };
      } catch (error) {
        console.error('API error:', error.response?.data || error);

        this.status = 'error';
        if (error.response?.data?.errors) {
          this.errors = error.response.data.errors;
          this.message = 'Please correct the errors below.';
        } else if (error.response?.data?.message) {
          this.message = error.response.data.message;
        } else {
          this.message = 'An unexpected error occurred. Please try again.';
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

.card:hover {
  transform: translateY(-5px);
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

/* Custom date input styling */
input[type="date"]::-webkit-calendar-picker-indicator {
  cursor: pointer;
  opacity: 0.6;
  transition: opacity 0.2s ease;
}

input[type="date"]::-webkit-calendar-picker-indicator:hover {
  opacity: 1;
}
</style>