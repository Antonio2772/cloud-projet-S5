<template>
  <div class="container py-4">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <div class="card shadow">
          <div class="card-header bg-primary text-white">
            <h4 class="mb-0">
              <i class="fas fa-exchange-alt me-2"></i>
              New Transaction
            </h4>
          </div>
          <div class="card-body">
            <form @submit.prevent="handleSubmit" class="needs-validation" novalidate>
              <!-- Transaction Type Selection -->
              <div class="mb-4">
                <label class="form-label d-block">Transaction Type</label>
                <div class="btn-group" role="group">
                  <input type="radio" class="btn-check" name="transactionType" 
                         id="depositRadio" value="deposit" v-model="transactionType">
                  <label class="btn btn-outline-success" for="depositRadio">
                    <i class="fas fa-arrow-down me-2"></i>Deposit
                  </label>

                  <input type="radio" class="btn-check" name="transactionType" 
                         id="withdrawalRadio" value="withdrawal" v-model="transactionType">
                  <label class="btn btn-outline-danger" for="withdrawalRadio">
                    <i class="fas fa-arrow-up me-2"></i>Withdrawal
                  </label>
                </div>
              </div>

              <!-- Amount Input -->
              <div class="mb-4">
                <label class="form-label">
                  <i class="fas fa-dollar-sign me-2"></i>Amount
                </label>
                <input type="number" class="form-control" v-model="amount" 
                       step="0.01" min="0" required>
              </div>

              <!-- Transaction Date -->
              <div class="mb-4">
                <label class="form-label">
                  <i class="fas fa-calendar me-2"></i>Transaction Date
                </label>
                <input type="datetime-local" class="form-control" 
                       v-model="dateTransaction" required>
              </div>

              <!-- Submit Button -->
              <button type="submit" class="btn btn-primary w-100" 
                      :disabled="loading">
                <template v-if="!loading">
                  <i class="fas fa-paper-plane me-2"></i>Submit Transaction
                </template>
                <div v-else class="spinner-border spinner-border-sm"></div>
              </button>
            </form>

            <!-- Response Alert -->
            <div v-if="response" :class="[
              'alert',
              'mt-4',
              'alert-' + (response.status === 'success' ? 'success' : 'danger')
            ]" role="alert">
              <i :class="[
                'fas',
                'me-2',
                response.status === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'
              ]"></i>
              {{ response.message }}
            </div>
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
  name: 'TransactionForm',
  data() {
    return {
      transactionType: 'deposit',
      amount: '',
      dateTransaction: '',
      loading: false,
      response: null
    }
  },
  methods: {
    async handleSubmit() {
      this.loading = true;
      this.response = null;

      this.currentUser = await AuthService.getCurrentUser();
      console.log(this.currentUser);

      try {
        const payload = {
          userId: this.currentUser.id, 
          deposit: this.transactionType === 'deposit' ? parseFloat(this.amount) : 0.00,
          withdrawal: this.transactionType === 'withdrawal' ? parseFloat(this.amount) : 0.00,
          dateTransaction: this.dateTransaction
        };

        const response = await axios.post('http://localhost:8099/api/transactions', payload);
        console.log('Transaction response: ', response);

        this.response = {
          status: response.data.status,
          message: response.data.message
        };

        if (response.data.status === 'success') {
          // Reset form on success
          this.amount = '';
          this.dateTransaction = '';
        }
      } catch (error) {
        this.response = {
          status: 'error',
          message: error.response?.data?.message || 'An error occurred while processing your transaction.'
        };
      } finally {
        this.loading = false;
      }
    }
  },
  created() {
    // Set default date to current date and time
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
    this.dateTransaction = now.toISOString().slice(0, 16);
  }
}
</script>