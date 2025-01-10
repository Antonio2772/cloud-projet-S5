<template>
  <div class="container">
    <div class="card shadow">
      <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
        <h4 class="mb-0">
          <i class="fas fa-history me-2"></i>
          Crypto Transactions History
        </h4>
        <button 
          @click="refreshTransactions" 
          class="btn btn-light btn-sm"
          :disabled="loading"
        >
          <i class="fas fa-sync-alt me-1" :class="{ 'fa-spin': loading }"></i>
          Refresh
        </button>
      </div>
      <div class="card-body">
        <!-- Loading State -->
        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
          <p class="text-muted mt-2">Loading transactions...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="alert alert-danger" role="alert">
          <i class="fas fa-exclamation-circle me-2"></i>
          {{ error }}
        </div>

        <!-- No Data State -->
        <div v-else-if="!transactions.length" class="text-center py-5">
          <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
          <p class="text-muted">No transactions found</p>
        </div>

        <!-- Transactions Table -->
        <div v-else class="table-responsive">
          <table class="table table-hover">
            <thead class="table-light">
              <tr>
                <th scope="col">
                  <i class="fas fa-user me-1"></i>
                  User ID
                </th>
                <th scope="col">
                  <i class="fas fa-coins me-1"></i>
                  Cryptocurrency
                </th>
                <th scope="col">
                  <i class="fas fa-calculator me-1"></i>
                  Quantity
                </th>
                <th scope="col">
                  <i class="fas fa-exchange-alt me-1"></i>
                  Type
                </th>
                <th scope="col">
                  <i class="fas fa-calendar-alt me-1"></i>
                  Date
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(transaction, index) in transactions" :key="index">
                <td>{{ transaction.userId }}</td>
                <td>
                  <span class="fw-medium">{{ transaction.cryptoLabel }}</span>
                </td>
                <td>
                  <span class="badge bg-light text-dark">
                    {{ formatQuantity(transaction.quantity) }}
                  </span>
                </td>
                <td>
                  <span 
                    :class="`badge ${transaction.isPurchase ? 'bg-success' : 'bg-danger'}`"
                  >
                    <i :class="`fas fa-${transaction.isPurchase ? 'shopping-cart' : 'cash-register'} me-1`"></i>
                    {{ transaction.isPurchase ? 'Purchase' : 'Sale' }}
                  </span>
                </td>
                <td>
                  {{ formatDate(transaction.dateTransaction) }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'CryptoTransactionsList',
  data() {
    return {
      transactions: [],
      loading: false,
      error: null
    };
  },
  methods: {
    formatQuantity(value) {
      return new Intl.NumberFormat('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 6
      }).format(value);
    },
    formatDate(date) {
      return new Date(date).toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    },
    async fetchTransactions() {
      this.loading = true;
      this.error = null;

      try {
        const response = await axios.get('http://localhost:8099/api/crypto-transactions/all');
        this.transactions = response.data;
      } catch (error) {
        this.error = 'Failed to load transactions. Please try again later.';
        console.error('Error fetching transactions:', error);
      } finally {
        this.loading = false;
      }
    },
    refreshTransactions() {
      this.fetchTransactions();
    }
  },
  created() {
    this.fetchTransactions();
  }
};
</script>