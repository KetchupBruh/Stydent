<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import ToastSuccess from '../components/ToastSuccess.vue';
import ToastError from '../components/ToastError.vue';

const showSuccess = ref(false);
const showError = ref(false);
const errorMessage = ref('Login failed. Please check your username and password.');
const successMessage = ref('Login Success');

const hideError = () => {
  showError.value = false;
};

const username = ref("");
const password = ref("");
const loading = ref(false);


const login = async () => {
  loading.value = true;
  try {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username: username.value,
        password: password.value,
      }),
    });

    if (response.status === 200) {
      console.log("You have successfully logged in");

      const data = await response.json();
      localStorage.setItem("username", data.attributes.uid);
      localStorage.setItem("email", data.attributes.mail);
      localStorage.setItem("role", data.attributes.radiusGroupName);
      localStorage.setItem("token", data.jwtToken);

      showSuccess.value = true;

      setTimeout(function () {
        Dashboard();
      }, 1500);

    } else {
      console.error("Login failed");
      showError.value = true;
      username.value=''
      password.value=''

        // hideError();
    }
  } catch (error) {
    console.error("An error occurred:", error);
    showError.value = true;
    errorMessage.value = "An error occurred. Please try again later.";
  } finally {
    loading.value = false;
  }
};

const appRouter = useRouter();
const Dashboard = () => appRouter.push({ name: "Dashboard" });
</script>

<template>
  <div class="container" >
    <div class="toast-container">
      <ToastError :showError="showError"  :errorMessage="errorMessage" @hide-error="hideError"/>
      <ToastSuccess :showSuccess="showSuccess"  :successMessage="successMessage" />
    </div>

    <!-- <div class="overlay" :style="{ display: loading ? 'block' : 'none' }"></div> -->
    <div class="background">
      <img src="../assets/loginbackground.jpg" class="sit" />
      <img src="../assets/logo.jpg" class="stydent" />
      <div class="username-password">
        <form @submit.prevent="login">
          <p class="label_username">
            Username &nbsp;<span style="color: red">*</span>
          </p>
          <br />
          <input type="text" v-model="username" class="username" />
          <br />

          <p class="label_username">
            Password &nbsp;<span style="color: red">*</span>
          </p>
          <br />
          <input type="password" v-model="password" class="password" />
          <br />

          <button
            type="submit"
            class="login"
            :disabled="!username || !password"
          >
            Login
          </button>
        </form>
      </div>

      <div role="status" v-if="loading" class="spinner">
        <svg
          aria-hidden="true"
          class="w-8 h-8 mr-2 text-gray-200 animate-spin dark:text-gray-600 fill-blue-600"
          viewBox="0 0 100 101"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z"
            fill="currentColor"
          />
          <path
            d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z"
            fill="currentFill"
          />
        </svg>
      </div>
    </div>
  </div>
</template>

<style scoped>
.spinner {
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
  position: absolute; /* Position the spinner absolute */
  top: 50%; /* Center vertically */
  left: 50%; /* Center horizontally */
  transform: translate(-50%, -50%); /* Center the spinner perfectly */
}

.container {
  display: grid;
  place-items: center;
  height: 100%;
}

.sit {
  float: left;
  height: 700px;
  width: 830px;
  border-radius: 30px 0px 0px 30px;
}

.stydent {
  position: absolute;
  top: 22%;
  right: 0;
  left: 830px;
  margin: auto;
  width: 230px;
  height: 58px;
}

.background {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 1270px;
  height: 700px;
  background: #ffffff 0% 0% no-repeat padding-box;
  border-radius: 30px;
}

.username-password {
  position: absolute;
  top: 35%; /* อยู่ด้านล่างของ .stydent */
  left: 415px;
  width: 100%;
  text-align: center; /* จัดข้อความให้อยู่ตรงกลาง */
}

/* ปรับแต่งตำแหน่งและขนาดของ .username และ .password ตามต้องการ */
.username {
  top: 349px;
  left: 858px;
  width: 300px;
  height: 40px;
  background: #ffffff 0% 0% no-repeat padding-box;
  border: 1px solid #4675c126;
  border-radius: 8px;
  opacity: 1;
  margin-bottom: 20px;
  margin-top: 2px;
}

.password {
  top: 349px;
  left: 858px;
  width: 300px;
  height: 40px;
  background: #ffffff 0% 0% no-repeat padding-box;
  border: 1px solid #4675c126;
  border-radius: 8px;
  opacity: 1;
  margin-top: 2px;
  margin-bottom: 30px;
}

.label_username {
  float: left;
  color: #db0000;
  position: absolute;
  left: 485px;
  letter-spacing: 0.28px;
  color: #697a98;
  font: normal normal 400 14px/21px 'Poppins', 'Anuphan', cursive;
}


.login {
  width: 300px;
  height: 40px;
  font: normal normal 400 14px/21px Poppins;
  color: white;
  background: var(--unnamed-color-4675c0) 0% 0% no-repeat padding-box;
  background: #4675c0 0% 0% no-repeat padding-box;
  border: 1px solid #4675c126;
  border-radius: 8px;
  opacity: 1;
}

input {
  color: black;
  font: normal normal 400 14px/21px 'Poppins', 'Anuphan', cursive;
  padding-left: 15px;
}

.login:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.overlay {
  position: fixed; /* Fixed position to cover the entire screen */
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(
    209,
    209,
    209,
    0.5
  ); /* Red background with 50% opacity */
  z-index: 1; /* Place it below the spinner */
  display: none; /* Initially hide the overlay */
}

.toast-container {
  position: fixed;
  top: 20px; /* Adjust as needed */
  left: 50%;
  transform: translateX(-50%);
  z-index: 1000;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.success-background {
  /* background-color: #ff0000;  */
}

</style>
