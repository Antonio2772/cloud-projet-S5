<template>
  <div class="container">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <div class="card shadow">
          <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
            <h4 class="mb-0">
              <i class="fas fa-wallet me-2"></i>
              Your Balance
            </h4>
            <button 
              @click="refreshBalance" 
              class="btn btn-light btn-sm"
              :disabled="loading"
            >
              <i class="fas fa-sync-alt me-1" :class="{ 'fa-spin': loading }"></i>
              Refresh
            </button>
          </div>
          <div class="card-body">
            <!-- Loading State -->
            <div v-if="loading" class="text-center py-4">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
              </div>
              <p class="text-muted mt-2">Fetching your balance...</p>
            </div>

            <!-- Error State -->
            <div v-else-if="error" class="alert alert-danger" role="alert">
              <i class="fas fa-exclamation-circle me-2"></i>
              {{ error }}
            </div>

            <!-- Balance Display -->
            <div v-else class="text-center py-4">
              <div class="balance-container">
                <div class="display-4 fw-bold text-primary mb-2">
                  {{ formatBalance(balanceData.solde) }}
                </div>
                <div class="text-muted">
                  <i class="fas fa-check-circle me-1 text-success"></i>
                  {{ balanceData.message }}
                </div>
              </div>

              <!-- Last Updated -->
              <div class="text-muted small mt-3">
                <i class="fas fa-clock me-1"></i>
                Last updated: {{ lastUpdated }}
              </div>
            </div>
          </div>
        </div>

        <!-- Transaction Actions -->
        <div class="row mt-4 g-3">
          <div class="col-md-6">
            <router-link 
              to="/dashboard/crypto-transaction" 
              class="btn btn-outline-primary w-100 p-3"
            >
              <i class="fas fa-exchange-alt me-2"></i>
              New Transaction
            </router-link>
          </div>
          <div class="col-md-6">
            <router-link 
              to="/dashboard/crypto-transactions-list" 
              class="btn btn-outline-secondary w-100 p-3"
            >
              <i class="fas fa-history me-2"></i>
              View History
            </router-link>
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
  name: 'UserBalance',
  data() {
    return {
      balanceData: {
        solde: 0,
        message: '',
        status: ''
      },
      loading: false,
      error: null,
      lastUpdated: 'Never'
    };
  },
  methods: {
    formatBalance(value) {
      return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      }).format(value);
    },
    updateLastUpdated() {
      this.lastUpdated = new Date().toLocaleTimeString();
    },
    async fetchBalance() {
      this.loading = true;
      this.error = null;

      this.currentUser = await AuthService.getCurrentUser();

      try {
        const response = await axios.get('http://localhost:8099/api/users/' + this.currentUser.id + '/solde');
        
        this.balanceData = response.data;
        this.updateLastUpdated();
      } catch (error) {
        this.error = 'Failed to fetch balance. Please try again later.';
        console.error('Error fetching balance:', error);
      } finally {
        this.loading = false;
      }
    },
    refreshBalance() {
      this.fetchBalance();
    }
  },
  created() {
    this.fetchBalance();
  }
};
</script>

<style scoped>
.card {
  border: none;
  border-radius: 1rem;
  overflow: hidden;
}

.card-header {
  border-radius: 1rem 1rem 0 0;
}

.balance-container {
  padding: 1.5rem;
  background-color: rgba(13, 110, 253, 0.05);
  border-radius: 1rem;
}

/* Hover effects for buttons */
.btn-outline-primary:hover,
.btn-outline-secondary:hover {
  transform: translateY(-2px);
  transition: transform 0.2s ease;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .display-4 {
    font-size: 2.5rem;
  }
}

/* Animation for balance update */
@keyframes highlight {
  0% { background-color: rgba(13, 110, 253, 0.1); }
  100% { background-color: rgba(13, 110, 253, 0.05); }
}

.balance-container {
  animation: highlight 1s ease-out;
}
</style>