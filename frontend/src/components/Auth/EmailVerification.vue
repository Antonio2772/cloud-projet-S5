<template>
  <div class="container">
    <div class="row justify-content-center min-vh-100 align-items-center">
      <div class="col-md-5">
        <div class="card shadow-lg border-0">
          <div class="card-body p-5">
            <div class="text-center mb-4">
              <div class="verification-icon mb-3">
                <i class="fas fa-envelope-open-text fa-3x text-primary"></i>
              </div>
              <h2 class="fw-bold">Verify Your Email</h2>
              <p class="text-muted">Enter the 4-digit code sent to your email</p>
            </div>

            <div v-if="message" :class="`alert alert-${status} alert-dismissible fade show`" role="alert">
              <i :class="`fas fa-${status === 'success' ? 'check-circle' : 'exclamation-circle'} me-2`"></i>
              {{ message }}
              <button type="button" class="btn-close" @click="clearMessage"></button>
            </div>

            <form @submit.prevent="handleSubmit">
              <div class="verification-code-container mb-4">
                <input
                  v-for="(digit, index) in 4"
                  :key="index"
                  type="text"
                  :ref="`digit${index}`"
                  v-model="verificationCode[index]"
                  class="form-control verification-input"
                  maxlength="1"
                  @input="handleInput($event, index)"
                  @keydown.delete="handleBackspace($event, index)"
                  @paste="handlePaste"
                  :class="{ 'is-invalid': errors.verification_code }"
                  required
                >
              </div>

              <div class="invalid-feedback text-center mb-3" v-if="errors.verification_code">
                {{ errors.verification_code[0] }}
              </div>

              <button 
                class="btn btn-primary w-100 py-3 mb-3" 
                type="submit"
                :disabled="loading || isCodeIncomplete"
              >
                <span v-if="!loading">
                  <i class="fas fa-check-circle me-2"></i>Verify Email
                </span>
                <div v-else class="spinner-border spinner-border-sm"></div>
              </button>

              <div class="text-center">
                <button 
                  type="button" 
                  class="btn btn-link text-decoration-none"
                  @click="resendCode"
                  :disabled="resendLoading"
                >
                  <i class="fas fa-redo-alt me-2"></i>
                  {{ resendLoading ? 'Sending...' : 'Resend Code' }}
                </button>
              </div>
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
  name: 'EmailVerification',
  props: {
    email: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      verificationCode: ['', '', '', ''],
      loading: false,
      resendLoading: false,
      message: '',
      status: '',
      errors: {}
    };
  },
  computed: {
    isCodeIncomplete() {
      return this.verificationCode.some(digit => !digit);
    }
  },
  methods: {
    clearMessage() {
      this.message = '';
      this.status = '';
    },
    handleInput(event, index) {
      const input = event.target;
      input.value = input.value.replace(/\D/g, '');
      
      if (input.value && index < 3) {
        this.$refs[`digit${index + 1}`]?.focus(); // Safely access and focus
      }
    },
    handleBackspace(event, index) {
      if (!this.verificationCode[index] && index > 0) {
        this.$refs[`digit${index - 1}`]?.focus(); // Safely access and focus
      }
    },
    handlePaste(event) {
      event.preventDefault();
      const pastedText = event.clipboardData.getData('text').replace(/\D/g, '').slice(0, 4);
      this.verificationCode = [...pastedText.padEnd(4, '')];
    },
    async handleSubmit() {
      this.loading = true;
      this.clearMessage();
      this.errors = {};

      try {
        const payload = {
          email: this.email,
          verification_code: this.verificationCode.join('')
        };
        console.log('Request Payload:', payload);

        const response = await axios.post(
          'http://127.0.0.1:8000/api/auth/verify-email',
          payload,
          {
            headers: { 'Content-Type': 'application/json' }
          }
        );
        
        if (response.status === 'error') {
          this.status = 'danger';
          this.errors = response.errors;
          this.message = 'Correct this error below';

          return;
        }

        this.status = 'success';
        this.message = response.data.message;
        this.$emit('verification-success');
        console.log('API Response:', response.data);
      } 
      catch (error) {
        console.error('API Error:', error.response?.data || error);

        this.status = 'danger';
        if (error.response?.data?.errors) {
          this.errors = error.response.data.errors;
          console.log('Validation Errors:', this.errors);
        }
        this.message = error.response?.data?.message || 'Verification failed';
      } 
      finally {
        this.loading = false;
      }
    },
    async resendCode() {
      this.resendLoading = true;

      // TODO: Implement resend code logic here
      this.resendLoading = false;
    }
  }
};
</script>

<style scoped>
.verification-code-container {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.verification-input {
  width: 3.5rem;
  height: 3.5rem;
  text-align: center;
  font-size: 1.5rem;
  border-radius: 0.5rem;
  border: 2px solid #dee2e6;
  transition: all 0.3s ease;
}

.verification-input:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

.verification-icon {
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
  100% { transform: translateY(0px); }
}

.card {
  border-radius: 1rem;
}

.btn-primary {
  border-radius: 0.5rem;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.alert {
  border-radius: 0.5rem;
}
</style>