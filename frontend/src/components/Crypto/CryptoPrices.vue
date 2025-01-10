<template>
  <div class="crypto-prices-container">
    <h2 v-if="!selectedCrypto">Crypto Prices</h2>
    <h2 v-else>{{ selectedCrypto.cryptoLabel }} Price Evolution</h2>
    
    <div v-if="loading" class="text-center">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
    
    <div v-else-if="error" class="alert alert-danger text-center">
      {{ error }}
    </div>
    
    <div v-if="!selectedCrypto && !loading">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th>#</th>
            <th>Crypto</th>
            <th>Current Price</th>
            <th>Change (%)</th>
          </tr>
        </thead>
        <tbody>
          <tr 
            v-for="(crypto, index) in cryptoPrices" 
            :key="index" 
            @click="selectCrypto(crypto.cryptoId)"
            style="cursor: pointer;"
          >
            <td>{{ index + 1 }}</td>
            <td>{{ crypto.cryptoLabel }}</td>
            <td>{{ formatPrice(crypto.currentPrice) }}</td>
            <td>
              <span
                class="badge"
                :class="crypto.percentageChange >= 0 ? 'bg-success' : 'bg-danger'"
              >
                {{ formatChange(crypto.percentageChange) }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <div v-else-if="selectedCrypto && !loading">
      <div>
        <canvas id="cryptoGraph"></canvas>
      </div>
      <button @click="clearSelection" class="btn btn-primary mt-3">
        Back to Crypto Prices
      </button>
    </div>
  </div>
</template>

<script>
import Chart from "chart.js/auto";

export default {
  name: "CryptoPrices",
  
  data() {
    return {
      cryptoPrices: [],
      loading: true,
      error: null,
      selectedCrypto: null,
      chart: null,
    };
  },
  
  methods: {
    async fetchCryptoPrices() {
      try {
        const response = await fetch("http://localhost:8099/api/crypto/prices");
        if (!response.ok) {
          throw new Error(`Failed to fetch crypto prices: ${response.status}`);
        }
        const data = await response.json();
        this.cryptoPrices = Array.isArray(data) ? data : data.data || [];
      } catch (err) {
        this.error = "Error fetching data. Please try again later.";
        console.error('Error:', err);
      } finally {
        this.loading = false;
      }
    },

    async selectCrypto(id) {
      this.loading = true;
      try {
        const [priceResponse, historyResponse] = await Promise.all([
          fetch(`http://localhost:8099/api/crypto/prices/${id}`),
          fetch(`http://localhost:8099/api/crypto/prices/${id}/history`)
        ]);

        if (!priceResponse.ok || !historyResponse.ok) {
          throw new Error('Failed to fetch crypto data');
        }

        const [priceData, historyData] = await Promise.all([
          priceResponse.json(),
          historyResponse.json()
        ]);

        this.selectedCrypto = priceData;
        
        // Wait for the DOM to update before rendering the chart
        this.$nextTick(() => {
          this.renderChart(priceData, historyData);
        });
      } catch (err) {
        console.error('Error:', err);
        this.error = "Error fetching crypto details. Please try again later.";
      } finally {
        this.loading = false;
      }
    },

    renderChart(currentData, historyData) {
      if (this.chart) {
        this.chart.destroy();
      }

      const ctx = document.getElementById("cryptoGraph").getContext("2d");
      if (ctx) {
        // Format the data for the chart
        const labels = historyData.map(item => 
          new Date(item.dateCours).toLocaleDateString()
        );
        const prices = historyData.map(item => item.currentPrice);

        this.chart = new Chart(ctx, {
          type: "line",
          data: {
            labels: labels,
            datasets: [
              {
                label: `${currentData.cryptoLabel} Price Evolution`,
                data: prices,
                borderColor: "rgba(75, 192, 192, 1)",
                backgroundColor: "rgba(75, 192, 192, 0.2)",
              },
            ],
          },
          options: {
            responsive: true,
            plugins: {
              legend: {
                position: "top",
              },
            },
            scales: {
              y: {
                beginAtZero: false,
                ticks: {
                  callback: function(value) {
                    return '$' + value.toFixed(2);
                  }
                }
              }
            }
          },
        });
      } else {
        console.log("Canvas context not found");
      }
    },

    clearSelection() {
      this.selectedCrypto = null;
      if (this.chart) {
        this.chart.destroy();
        this.chart = null;
      }
    },

    formatPrice(value) {
      return value !== undefined && value !== null
        ? `$${value.toFixed(2)}`
        : "N/A";
    },

    formatChange(value) {
      return value !== undefined && value !== null
        ? `${value.toFixed(2)}%`
        : "N/A";
    },
  },
  
  mounted() {
    this.fetchCryptoPrices();
  },

  beforeUnmount() {
    if (this.chart) {
      this.chart.destroy();
    }
  }
};
</script>

<style scoped>
.crypto-prices-container {
  padding: 20px;
}

table {
  margin-top: 20px;
}

.badge {
  font-size: 1rem;
}

canvas {
  max-width: 100%;
  height: 400px;
}
</style>