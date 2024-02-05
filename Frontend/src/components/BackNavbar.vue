<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const props = defineProps(['backToPage']);
const router = useRouter();

// const goBack = () => {
//   router.push(props.backToPage);
// };

const goBack = () => {
  router.go(-1);
};
</script>

<template>
  <div class="back-navbar">
    <span class="back" @click="goBack"> &#60; &nbsp; Back</span>
  </div>
</template>

<style scoped>
.back {
  color: #ffffff;
  letter-spacing: 0.28px;
  cursor: pointer;
}

.back:hover {
  opacity: 0.5;
  cursor: pointer;
}

.back-navbar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 50px;
  background: #4675c1;
  color: #fff;
  padding: 10px 0;
  padding-left: 80px;
  transition: background 0.3s;
  font: normal normal 400 14px/22px Poppins;
  letter-spacing: 0.28px;
  padding-top: 14px;
}
</style>
