<template>
  <div class="container">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <div class="card shadow">
          <div class="card-header bg-primary text-white">
            <h4 class="mb-0">
              <i class="fas fa-exchange-alt me-2"></i>
              New Crypto Transaction
            </h4>
          </div>
          <div class="card-body">
            <!-- Alert for response message -->
            <div v-if="response" :class="`alert alert-${response.status === 'success' ? 'success' : 'danger'}`" role="alert">
              <i :class="`fas fa-${response.status === 'success' ? 'check-circle' : 'exclamation-circle'} me-2`"></i>
              {{ response.message }}
            </div>

            <form @submit.prevent="handleSubmit" class="needs-validation">
              <!-- Transaction Type -->
              <div class="mb-4">
                <label class="form-label text-center w-100">Transaction Type</label>
                <div class="d-flex justify-content-center gap-4">
                  <div class="form-check">
                    <input
                      class="form-check-input"
                      type="radio"
                      name="transactionType"
                      id="typePurchase"
                      v-model="transactionType"
                      value="purchase"
                    >
                    <label class="form-check-label" for="typePurchase">
                      <i class="fas fa-shopping-cart text-success me-2"></i>Purchase
                    </label>
                  </div>
                  <div class="form-check">
                    <input
                      class="form-check-input"
                      type="radio"
                      name="transactionType"
                      id="typeSale"
                      v-model="transactionType"
                      value="sale"
                    >
                    <label class="form-check-label" for="typeSale">
                      <i class="fas fa-cash-register text-danger me-2"></i>Sale
                    </label>
                  </div>
                </div>
              </div>


              <!-- Crypto Select -->
              <div class="mb-3">
                <label for="cryptoSelect" class="form-label">Select Cryptocurrency</label>
                <div class="input-group">
                  <span class="input-group-text">
                    <i class="fas fa-coins"></i>
                  </span>
                  <select 
                    class="form-select" 
                    id="cryptoSelect" 
                    v-model="formData.crypto.id"
                    required
                    :disabled="loadingCryptos"
                  >
                    <option value="">Select a cryptocurrency</option>
                    <option 
                      v-for="crypto in cryptos" 
                      :key="crypto.id" 
                      :value="crypto.id"
                    >
                      {{ crypto.label }}
                    </option>
                  </select>
                </div>
                <!-- Loading state for cryptos -->
                <div v-if="loadingCryptos" class="text-muted small mt-1">
                  <i class="fas fa-spinner fa-spin me-1"></i>
                  Loading cryptocurrencies...
                </div>
                <!-- Error state for cryptos -->
                <div v-if="cryptoError" class="text-danger small mt-1">
                  <i class="fas fa-exclamation-circle me-1"></i>
                  {{ cryptoError }}
                </div>
              </div>

              <!-- Quantity -->
              <div class="mb-3">
                <label for="quantity" class="form-label">Quantity</label>
                <div class="input-group">
                  <span class="input-group-text">
                    <i class="fas fa-calculator"></i>
                  </span>
                  <input
                    type="number"
                    step="0.000001"
                    class="form-control"
                    id="quantity"
                    v-model="formData.quantity"
                    required
                  >
                </div>
              </div>

              <!-- Submit Button -->
              <button 
                type="submit" 
                class="btn btn-primary w-100"
                :disabled="loading || loadingCryptos"
              >
                <span v-if="!loading">
                  <i class="fas fa-paper-plane me-2"></i>Submit Transaction
                </span>
                <div v-else class="spinner-border spinner-border-sm" role="status">
                  <span class="visually-hidden">Loading...</span>
                </div>
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import AuthService from '@/services/auth';

export default {
  name: 'CryptoTransaction',
  data() {
    return {
      transactionType: 'purchase',
      loading: false,
      loadingCryptos: false,
      cryptoError: null,
      cryptos: [],
      response: null,
      formData: {
        userId: null, 
        crypto: {
          id: ''
        },
        isSale: false,
        isPurchase: true,
        quantity: null,
        dateTransaction: null
      }
    };
  },
  watch: {
    transactionType: {
      handler(newValue) {
        this.formData.isSale = newValue === 'sale';
        this.formData.isPurchase = newValue === 'purchase';
      },
      immediate: true
    }
  },
  methods: {
    async fetchCryptos() {
      this.loadingCryptos = true;
      this.cryptoError = null;

      try {
        const response = await axios.get('http://localhost:8099/api/crypto-transactions/cryptos');

        console.log('API response: ', response);
        this.cryptos = response.data;
      } catch (error) {
        this.cryptoError = 'Failed to load cryptocurrencies. Please try refreshing the page.';
        console.error('Error fetching cryptos:', error);
      } finally {
        this.loadingCryptos = false;
      }
    },
    async handleSubmit() {
      this.loading = true;
      this.response = null;

      this.currentUser = await AuthService.getCurrentUser();

      try {
        // Set current date and time
        this.formData.dateTransaction = new Date().toISOString();
        this.formData.userId = this.currentUser.id;

        const response = await axios.post(
          'http://localhost:8099/api/crypto-transactions',
          this.formData
        );

        this.response = {
          status: response.data.status,
          message: response.data.message
        };

        if (response.data.status === 'success') {
          // Reset form
          this.formData.crypto.id = '';
          this.formData.quantity = null;
          this.formData.userId = null;
        }
      } catch (error) {
        this.response = {
          status: 'error',
          message: error.response?.data?.message || 'An error occurred while processing your transaction'
        };
      } finally {
        this.loading = false;
      }
    }
  },
  created() {
    this.fetchCryptos();
  }
};
</script>

<style scoped>
.card {
  border: none;
  border-radius: 1rem;
}

.card-header {
  border-radius: 1rem 1rem 0 0;
}

.form-control:focus,
.form-select:focus {
  border-color: #28a745; /* Vert pour la bordure */
  box-shadow: 0 0 0 0.25rem rgba(40, 167, 69, 0.25); /* Vert clair pour l'ombre */
}

.btn-primary:disabled {
  cursor: not-allowed;
}

.alert {
  border-radius: 0.5rem;
}

/* Custom styling for select dropdown */
.form-select {
  padding-right: 2.5rem;
  background-position: right 0.75rem center;
}

</style>