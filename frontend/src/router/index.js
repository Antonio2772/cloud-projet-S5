import { createRouter, createWebHistory } from 'vue-router';

import Login from '../components/Auth/Login.vue';
import AuthFlow from '../components/Auth/AuthFlow.vue';
import Dashboard from '../components/Panel/Dashboard.vue';
import CryptoPrices from '../components/Crypto/CryptoPrices.vue';
import TransactionForm from '../components/Transaction/TransactionForm.vue';
import CryptoTransaction from '../components/Crypto/CryptoTransaction.vue'; 
import CryptoTransactionsList from '../components/Crypto/CryptoTransactionsList.vue'
import UserBalance from '../components/User/UserBalance.vue';

const routes = [
  {
    path: '/',
    component: AuthFlow,
  },
  {
    path: '/login',
    component: Login,
  },
  {
    path: '/signup',
    component: AuthFlow,
  },
  {
    path: '/dashboard',
    component: Dashboard,
    children: [
      {
        path: 'crypto-prices',
        component: CryptoPrices,
      },
      {
        path: 'transaction',
        component: TransactionForm,
      },
      {
        path: 'crypto-transaction',
        component: CryptoTransaction
      },
      {
        path: 'crypto-transactions-list',
        component: CryptoTransactionsList
      },
      {
        path: 'balance',
        component: UserBalance
      }
    ],
  },
];

export const router = createRouter({
  history: createWebHistory(),
  routes,
});