<script setup>
import Navbar from "./components/navbar.vue";
import { useRoute } from "vue-router";
const route = useRoute();
</script>

<template>
  <Navbar v-if="route.path !== '/'"/>
  <router-view />
</template>

<style>
  @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap');
  @import url('https://fonts.googleapis.com/css2?family=Prompt&display=swap');
  @import url('https://fonts.googleapis.com/css2?family=Mali:wght@500&display=swap');
  @import url('https://fonts.googleapis.com/css2?family=IBM+Plex+Sans+Thai:wght@300;400&display=swap');
  @import url('https://fonts.googleapis.com/css2?family=Anuphan:wght@300;400&display=swap');

body {
  margin: 0;
  padding: 0;
  background-color: #f0f3fc;
  font-family: 'Poppins','Anuphan', cursive;
  font-weight: 700;
}
</style>
