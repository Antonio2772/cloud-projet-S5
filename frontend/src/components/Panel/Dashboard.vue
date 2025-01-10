<!-- Dashboard.vue -->
<template>
  <div class="dashboard-container">
    <!-- Sidebar -->
    <div class="sidebar text-white" :class="{ 'collapsed': isSidebarCollapsed }">
      <div class="sidebar-header p-3 border-bottom">
        <h4 class="mb-0">
          <i class="fas fa-chart-line me-2"></i>
          <span v-if="!isSidebarCollapsed">Dashboard</span>
        </h4>
      </div>
      <div class="sidebar-content p-3">

        <!-- SIDEBAR -->
        <ul class="nav flex-column">
          <li class="nav-item mb-3">
            <router-link to="/dashboard" class="nav-link text-white d-flex align-items-center">
              <i class="fas fa-home me-2"></i>
              <span v-if="!isSidebarCollapsed">Home</span>
            </router-link>
          </li>
          <li class="nav-item mb-3">
            <router-link to="/dashboard/crypto-prices" class="nav-link text-white d-flex align-items-center">
              <i class="fas fa-chart-bar me-2"></i>
              <span v-if="!isSidebarCollapsed">Crypto Prices</span>
            </router-link>
          </li>
          <li class="nav-item mb-3">
            <router-link to="/dashboard/transaction" class="nav-link text-white d-flex align-items-center">
              <i class="fas fa-exchange-alt me-2"></i>
              <span v-if="!isSidebarCollapsed">Cash Transaction</span>
            </router-link>
          </li>
          <li class="nav-item mb-3">
            <router-link to="/dashboard/crypto-transaction" class="nav-link text-white d-flex align-items-center">
              <i class="fas fa-coins me-2"></i>
              <span v-if="!isSidebarCollapsed">Crypto Transaction</span>
            </router-link>
          </li>
          <li class="nav-item mb-3">
            <router-link to="/dashboard/crypto-transactions-list" class="nav-link text-white d-flex align-items-center">
              <i class="fas fa-history me-2"></i>
              <span v-if="!isSidebarCollapsed">Transaction History</span>
            </router-link>
          </li>
          <li class="nav-item mb-3">
            <router-link to="/dashboard/balance" class="nav-link text-white d-flex align-items-center">
              <i class="fas fa-wallet me-2"></i>
              <span v-if="!isSidebarCollapsed">My Balance</span>
            </router-link>
          </li>
        </ul>

      </div>
      <div class="sidebar-footer">
        <div class="collapse-button" @click="toggleSidebar">
          <div class="icon-container">
            <i :class="isSidebarCollapsed ? 'fas fa-chevron-right' : 'fas fa-chevron-left'"></i>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="main-content">
      <!-- Header -->
      <header class="header bg-white shadow-sm">
        <div class="container-fluid">
          <div class="row align-items-center py-3">
            <div class="col">
              <div class="d-flex align-items-center">
                <h5 class="mb-0">Welcome</h5>
              </div>
            </div>
            <div class="col-auto">
              <div class="user-info d-flex align-items-center">
                <template v-if="loading">
                  <div class="spinner-border spinner-border-sm me-2"></div>
                  <span>Loading...</span>
                </template>
                <template v-else-if="error">
                  <span class="text-danger">{{ error }}</span>
                </template>
                <template v-else-if="currentUser">
                  <span class="me-3">
                    <i class="fas fa-user-circle me-2"></i>
                    {{ currentUser.name }}
                  </span>
                  <span class="badge bg-primary me-3">{{ currentUser.role }}</span>
                  <button 
                    @click="handleLogout" 
                    class="btn btn-outline-danger btn-sm"
                    :disabled="isLoggingOut"
                  >
                    <template v-if="!isLoggingOut">
                      <i class="fas fa-sign-out-alt me-2"></i>Logout
                    </template>
                    <div v-else class="spinner-border spinner-border-sm"></div>
                  </button>
                </template>
              </div>
            </div>
          </div>
        </div>
      </header>

      <!-- Page Content -->
      <div class="content p-4">
        <router-view></router-view>
      </div>

      <!-- Footer -->
      <footer class="footer bg-white border-top">
        <div class="container-fluid">
          <div class="row py-3">
            <div class="col text-center">
              <small class="text-muted">© 2024 Your Company. All rights reserved.</small>
            </div>
          </div>
        </div>
      </footer>
    </div>
  </div>
</template>

<script>
import AuthService from '@/services/auth';

export default {
  name: 'Dashboard',
  data() {
    return {
      isSidebarCollapsed: false,
      currentUser: null,
      isLoggingOut: false,
      loading: true,
      error: null
    }
  },
  methods: {
    async fetchUserDetails() {
      this.loading = true;
      this.error = null;
      try {
        this.currentUser = await AuthService.getCurrentUser();
        console.log('User', this.currentUser);
        
        // Check token expiration
        const expiresAt = new Date(this.currentUser.token_expires_at);
        if (expiresAt < new Date()) {
          await this.handleLogout();
          return;
        }

        // Set up expiration check
        this.setupExpirationCheck(expiresAt);
      } catch (error) {
        this.error = 'Failed to load user details';
        if (error.response?.status === 401) {
          this.$router.push('/login');
        }
      } finally {
        this.loading = false;
      }
    },
    setupExpirationCheck(expiresAt) {
      const timeUntilExpiration = expiresAt.getTime() - new Date().getTime();
      if (timeUntilExpiration > 0) {
        setTimeout(async () => {
          await this.handleLogout();
          this.$router.push('/login');
        }, timeUntilExpiration);
      }
    },
    async handleLogout() {
      if (this.isLoggingOut) return;
      
      this.isLoggingOut = true;
      try {
        await AuthService.logout();
        this.$router.push('/login');
      } catch (error) {
        alert('Logout failed. Please try again.');
      } finally {
        this.isLoggingOut = false;
      }
    }
  },
  created() {
    this.fetchUserDetails();
  }
}
</script>

<style scoped>
.dashboard-container {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 250px;
  transition: width 0.3s ease;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #1e7c34 0%, #2a9d57 100%);
}

.sidebar.collapsed {
  width: 70px;
}

.sidebar-content {
  flex-grow: 1;
}

.nav-link {
  border-radius: 8px;
  transition: all 0.2s ease;
  text-align: left;
  padding: 0.5rem 1rem;
  width: 100%;
}

.nav-link i {
  width: 20px;
  text-align: center;
}

.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
  transform: translateX(5px);
}

.main-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.header {
  position: sticky;
  top: 0;
  z-index: 1000;
}

.content {
  flex-grow: 1;
  background-color: #e8f5e9; /* Vert clair pour le fond */
}

.footer {
  position: sticky;
  bottom: 0;
}

/* Custom scrollbar */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #77b27a;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #4caf50;
}

.sidebar-footer {
  padding: 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.collapse-button {
  cursor: pointer;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.collapse-button:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.icon-container {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.icon-container i {
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.8rem;
}

.collapse-button:hover .icon-container {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}
</style>