<template>
  <div>
    <Signup 
      v-if="!showVerification" 
      @signup-success="handleSignupSuccess"
    />
    <EmailVerification 
      v-else 
      :email="userEmail"
      @verification-success="handleVerificationSuccess"
    />
  </div>
</template>

<script>
import Signup from './Signup.vue';
import EmailVerification from './EmailVerification.vue';

export default {
  name: 'AuthFlow',
  components: {
    Signup,
    EmailVerification
  },
  data() {
    return {
      showVerification: false,
      userEmail: ''
    };
  },
  methods: {
    handleSignupSuccess(email) {
      this.userEmail = email;
      this.showVerification = true;
    },
    handleVerificationSuccess() {
      this.$router.push('/login');
    }
  }
};
</script>