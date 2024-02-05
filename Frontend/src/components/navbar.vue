<script setup>
import { ref, onMounted, onBeforeMount } from "vue";
import { useRouter } from "vue-router";
const activeLink = ref(localStorage.getItem("activeLink"));
const dropdown = ref(false);

const username = localStorage.getItem("username");
const email = localStorage.getItem("email");
const role = localStorage.getItem("role");


function setActiveLink(link) {
    activeLink.value = link;
    localStorage.setItem("activeLink", link);
}

function toggleDropdown() {
  dropdown.value = !dropdown.value;
}

onMounted(() => {
    setActiveLink(activeLink.value);
});

const signout = () => {
  localStorage.removeItem("username");
  localStorage.removeItem("email");
  localStorage.removeItem("role");
  localStorage.removeItem("token");
  setActiveLink("dashboard");

    Login();

};

const appRouter = useRouter();
const Login = () => appRouter.push({ name: "login" });

const Mycategory = () => {
  toggleDropdown();
  setActiveLink();
  appRouter.push({ name: "Mycategory" });
};

</script>

<template>
  <section class="navbar">
    <div class="logo">
      <img src="../assets/logo.jpg" style="width: 186px; height: 46px" />
    </div>

    <ul>
      <li :class="{ active_nav: activeLink === 'dashboard' }">
        <svg
          id="ss"
          xmlns="http://www.w3.org/2000/svg"
          width="19.5"
          height="19.5"
          viewBox="0 0 19.5 19.5"
          :class="{ active_icon: activeLink === 'dashboard' }"
        >
          <g
            id="Iconly_Light-Outline_Category"
            data-name="Iconly/Light-Outline/Category"
            transform="translate(-2 -2)"
          >
            <g id="Category" transform="translate(2 2)">
              <path
                id="Combined-Shape"
                d="M4.25,11c4.069,0,4.25,1.38,4.25,4.25,0,1.438,0,2.461-.6,3.21-.695.863-1.96,1.04-3.646,1.04S1.3,19.323.6,18.46C0,17.711,0,16.689,0,15.275l.75-.025H0C0,12.38.181,11,4.25,11Zm11,0c4.069,0,4.25,1.38,4.25,4.25,0,1.438,0,2.461-.6,3.21-.695.863-1.96,1.04-3.646,1.04S12.3,19.323,11.6,18.46c-.6-.749-.6-1.771-.6-3.185l.75-.025H11C11,12.38,11.181,11,15.25,11ZM4.456,12.5H4.25c-2.609,0-2.743.187-2.75,2.377v.853a3.392,3.392,0,0,0,.27,1.79c.265.328,1.052.48,2.479.48s2.214-.153,2.479-.481A4.513,4.513,0,0,0,7,15.274C7,12.774,7,12.512,4.456,12.5Zm11,0H15.25c-2.609,0-2.743.187-2.75,2.377v.853a3.392,3.392,0,0,0,.27,1.79c.265.328,1.052.48,2.479.48s2.214-.153,2.479-.481A4.513,4.513,0,0,0,18,15.274C18,12.774,18,12.512,15.456,12.5ZM4.25,0C8.319,0,8.5,1.38,8.5,4.25c0,1.438,0,2.461-.6,3.21C7.2,8.323,5.936,8.5,4.25,8.5S1.3,8.323.6,7.46C0,6.711,0,5.689,0,4.275L.75,4.25H0C0,1.38.181,0,4.25,0Zm11,0c4.069,0,4.25,1.38,4.25,4.25,0,1.438,0,2.461-.6,3.21-.695.863-1.96,1.04-3.646,1.04S12.3,8.323,11.6,7.46C11,6.711,11,5.689,11,4.275l.75-.025H11C11,1.38,11.181,0,15.25,0ZM4.456,1.5H4.25c-2.609,0-2.743.187-2.75,2.377V4.73a3.392,3.392,0,0,0,.27,1.79C2.036,6.848,2.823,7,4.25,7s2.214-.153,2.479-.481A4.513,4.513,0,0,0,7,4.274C7,1.774,7,1.512,4.456,1.5Zm11,0H15.25c-2.609,0-2.743.187-2.75,2.377V4.73a3.392,3.392,0,0,0,.27,1.79c.265.328,1.052.48,2.479.48s2.214-.153,2.479-.481A4.513,4.513,0,0,0,18,4.274C18,1.774,18,1.512,15.456,1.5Z"
                fill-rule="evenodd"
              />
            </g>
          </g>
        </svg>
        <span>
          <router-link
            :to="{ name: 'Dashboard' }"
            style="text-decoration: none; color: inherit"
            @click="setActiveLink('dashboard')"
            >Dashboard</router-link
          >
        </span>
      </li>

      <li :class="{ active_nav: activeLink === 'summary' }">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="20.697"
          height="20.669"
          viewBox="0 0 20.697 20.669"
          :class="{ active_icon: activeLink === 'summary' }"
        >
          <g
            id="Iconly_Light-Outline_Folder"
            data-name="Iconly/Light-Outline/Folder"
            transform="translate(-2 -2)"
          >
            <g id="Folder" transform="translate(2 2)">
              <path
                id="Combined-Shape"
                d="M7.643,0a3.046,3.046,0,0,1,2.425,1.214l.911,1.212a1.548,1.548,0,0,0,1.228.614h2.829C18.9,3.04,20.7,5.019,20.7,9.268l-.028,4.967c0,3.968-2.467,6.434-6.437,6.434H6.45C2.472,20.669,0,18.2,0,14.232v-7.8C0,2.164,1.9,0,5.643,0Zm0,1.5h-2C2.739,1.5,1.5,2.976,1.5,6.433v7.8c0,3.184,1.758,4.937,4.95,4.937h7.782c3.184,0,4.937-1.753,4.937-4.937v0L19.2,9.264c0-3.4-1.166-4.724-4.161-4.724h-2.83A3.053,3.053,0,0,1,9.781,3.328L8.868,2.114A1.539,1.539,0,0,0,7.642,1.5Zm7.074,10.713a.75.75,0,0,1,0,1.5H5.981a.75.75,0,0,1,0-1.5Z"
                fill-rule="evenodd"
              />
            </g>
          </g>
        </svg>

        <span>
          <router-link
            :to="{ name: 'Summary' }"
            style="text-decoration: none; color: inherit"
            @click="setActiveLink('summary')"
            >Summary</router-link
          >
        </span>
      </li>

      <li :class="{ active_nav: activeLink === 'review' }">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="18.165"
          height="19.91"
          viewBox="0 0 18.165 19.91"
          :class="{ active_icon: activeLink === 'review' }"
        >
          <g
            id="Iconly_Light-Outline_Document"
            data-name="Iconly/Light-Outline/Document"
            transform="translate(-3 -2)"
          >
            <g id="Document" transform="translate(3 2)">
              <path
                id="Combined-Shape"
                d="M12.909,0a5.024,5.024,0,0,1,5.256,5.357v9.2a4.974,4.974,0,0,1-5.215,5.354l-7.693,0A5.025,5.025,0,0,1,0,14.553v-9.2A4.972,4.972,0,0,1,5.215,0l7.693,0Zm0,1.5L5.22,1.5A3.494,3.494,0,0,0,1.5,5.357v9.2A3.5,3.5,0,0,0,5.256,18.41l7.689,0a3.5,3.5,0,0,0,3.72-3.854v-9.2A3.5,3.5,0,0,0,12.909,1.5Zm-.193,11.974a.75.75,0,0,1,0,1.5H5.5a.75.75,0,1,1,0-1.5Zm0-4.186a.75.75,0,1,1,0,1.5H5.5a.75.75,0,0,1,0-1.5ZM8.25,5.11a.75.75,0,0,1,0,1.5H5.5a.75.75,0,0,1,0-1.5Z"
                fill-rule="evenodd"
              />
            </g>
          </g>
        </svg>
        <span>
          <router-link
            :to="{ name: 'Review' }"
            style="text-decoration: none; color: inherit"
            @click="setActiveLink('review')"
            >Review</router-link
          >
        </span>
      </li>

      <!-- report -->
      <li
        :class="{ active_nav: activeLink === 'report' }"
        v-if="role === 'staff_group'"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="20.014"
          height="18.186"
          viewBox="0 0 20.014 18.186"
          :class="{ active_icon: activeLink === 'report' }"
        >
          <g
            id="Iconly_Light-Outline_Danger-Triangle"
            data-name="Iconly/Light-Outline/Danger-Triangle"
            transform="translate(-2 -3)"
          >
            <g id="Group-8" transform="translate(2 3)">
              <path
                id="Combined-Shape"
                d="M10.014,0a2.779,2.779,0,0,1,2.439,1.415l7.186,12.564a2.812,2.812,0,0,1-2.44,4.207H2.816A2.812,2.812,0,0,1,.375,13.979l7.2-12.566A2.777,2.777,0,0,1,10.013,0Zm0,1.5a1.3,1.3,0,0,0-1.138.659l-7.2,12.565a1.312,1.312,0,0,0,1.139,1.962H17.2a1.311,1.311,0,0,0,1.137-1.962L11.151,2.159A1.3,1.3,0,0,0,10.013,1.5Zm-.007,11a1,1,0,1,1-.01,0Zm0-5.935a.75.75,0,0,1,.75.75v3.1a.75.75,0,0,1-1.5,0v-3.1A.75.75,0,0,1,10,6.564Z"
                fill-rule="evenodd"
              />
            </g>
          </g>
        </svg>

        <span>
          <router-link
            :to="{ name: 'Report' }"
            style="text-decoration: none; color: inherit"
            @click="setActiveLink('report')"
            >Report</router-link
          >
        </span>
      </li>
    </ul>

    <div class="dropdown">
      <img src="../assets/admin.png" id="profile" v-if="role === 'staff_group'"/>
      
      <img src="../assets/student.png" id="profile" v-if="role === 'st_group'"/>
      <div
        class="backdrop"
        @click="toggleDropdown"
        :class="{ 'active-backdrop': dropdown }"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="14.004"
          height="8.007"
          viewBox="0 0 14.004 8.007"
        >
          <g
            id="_15"
            data-name="15"
            transform="translate(19 -7.994) rotate(90)"
          >
            <path
              id="Path_1"
              data-name="Path 1"
              d="M9,19a1,1,0,0,1-.71-1.71L13.59,12,8.29,6.71A1,1,0,1,1,9.71,5.29l6,6a1,1,0,0,1,0,1.41l-6,6A1,1,0,0,1,9,19Z"
            />
          </g>
        </svg>
      </div>
    </div>
  </section>

  <div
    class="absolute right-12 z-40 mt-2 w-58 rounded-lg divide-y divide-gray-100 bg-white shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none"
    v-if="dropdown"
  >
    <div class="py-1" role="none">
      <a
        class="text-gray-700 block px-5 py-3 text-sm font-light flex items-center"
        ><svg
          style="margin-right: 13px; width: 18px;"
          xmlns="http://www.w3.org/2000/svg"
          width="15.84"
          height="19.87"
          viewBox="0 0 15.84 19.87"
        >
          <g
            id="Iconly_Light-Outline_Profile"
            data-name="Iconly/Light-Outline/Profile"
            transform="translate(-4 -2)"
          >
            <g id="Profile" transform="translate(4 2)">
              <path
                id="Combined-Shape"
                d="M15.84,16.193c0,3.3-4.52,3.677-7.919,3.677H7.678C5.512,19.865,0,19.728,0,16.173c0-3.229,4.338-3.66,7.711-3.677h.453C10.33,12.5,15.84,12.638,15.84,16.193ZM7.921,14C3.66,14,1.5,14.728,1.5,16.173s2.16,2.2,6.421,2.2,6.419-.732,6.419-2.177S12.181,14,7.921,14Zm0-14a5.31,5.31,0,0,1,0,10.619H7.889A5.31,5.31,0,0,1,7.921,0Zm0,1.428a3.882,3.882,0,0,0-.029,7.764l.029.714V9.192a3.882,3.882,0,0,0,0-7.764Z"
                fill-rule="evenodd"
                fill="#4A5568"
              />
            </g>
          </g>
        </svg>
        {{ username }}

        <div class="role">
          {{
            role === "st_group"
              ? "Student"
              : role === "staff_group"
              ? "Admin"
              : ""
          }}
        </div>
      </a>
      <a
        class="text-gray-700 block px-6 py-3 text-sm font-light flex items-center"
        ><svg
          style="margin-right: 14px; margin-left: -5px; width: 18px;"
          xmlns="http://www.w3.org/2000/svg"
          width="21.5"
          height="19.5"
          viewBox="0 0 21.5 19.5"
        >
          <g
            id="Iconly_Light-Outline_Message"
            data-name="Iconly/Light-Outline/Message"
            transform="translate(-1 -2)"
          >
            <g id="Message" transform="translate(1 1)">
              <path
                id="Combined-Shape"
                d="M15.659,1C18.989,1,21.5,3.717,21.5,7.32v6.868a6.541,6.541,0,0,1-1.837,4.692,5.529,5.529,0,0,1-3.988,1.62H5.822a5.524,5.524,0,0,1-3.985-1.62A6.541,6.541,0,0,1,0,14.188V7.32C0,3.717,2.511,1,5.841,1Zm0,1.5H5.841C3.326,2.5,1.5,4.527,1.5,7.32v6.868a5.053,5.053,0,0,0,1.4,3.629A4.05,4.05,0,0,0,5.825,19h9.834s.01,0,.016,0A4.049,4.049,0,0,0,18.6,17.817,5.049,5.049,0,0,0,20,14.188V7.32C20,4.527,18.174,2.5,15.659,2.5Zm1.576,4.629a.751.751,0,0,1-.109,1.055L12.682,11.8a3.042,3.042,0,0,1-3.8.006L4.4,8.186a.75.75,0,1,1,.941-1.168L9.82,10.63a1.544,1.544,0,0,0,1.923,0L16.179,7.02A.749.749,0,0,1,17.235,7.129Z"
                fill-rule="evenodd"
                fill="#4A5568"
              />
            </g>
          </g></svg
        >{{ email }}</a
      >
      <a
        href="#"
        class="text-gray-700 block px-5 py-3 text-sm font-light flex items-center hover:bg-gray-100 dark hover:dark:bg-gray-200 rounded-lg"
        @click="Mycategory"
        v-if="role === 'st_group'"
      >
        <svg
          style="margin-right: 13px; width: 18px;"
          xmlns="http://www.w3.org/2000/svg"
          width="20.473"
          height="19.501"
          viewBox="0 0 20.473 19.501"
          class="mr-2"
        >
          <g
            id="Iconly_Light-Outline_Heart"
            data-name="Iconly/Light-Outline/Heart"
            transform="translate(-2 -3)"
          >
            <g id="Heart" transform="translate(2 3)">
              <path
                id="Combined-Shape"
                d="M10.235,1.039A6.891,6.891,0,0,1,15.887.325c4.059,1.309,5.319,5.734,4.192,9.255-1.739,5.53-9.166,9.655-9.481,9.828a.748.748,0,0,1-.719,0C9.566,19.239,2.193,15.175.4,9.581a0,0,0,0,1,0,0C-.733,6.058.523,1.632,4.578.325A6.729,6.729,0,0,1,10.235,1.039Zm-5.2.714C1.757,2.811.933,6.34,1.824,9.123c1.4,4.362,6.941,7.889,8.413,8.762,1.477-.882,7.056-4.448,8.413-8.758.891-2.786.064-6.315-3.222-7.374a5.289,5.289,0,0,0-4.731.792.75.75,0,0,1-.91.006A5.234,5.234,0,0,0,5.038,1.753Zm9.43,1.986A3.525,3.525,0,0,1,16.9,6.814a.75.75,0,0,1-.687.808c-.021,0-.041,0-.062,0a.75.75,0,0,1-.747-.689,2.024,2.024,0,0,0-1.4-1.769.75.75,0,1,1,.46-1.428Z"
                fill-rule="evenodd"
                fill="#4A5568"
              />
            </g>
          </g>
        </svg>

        My category</a
      >
    </div>
    <div class="py-1" role="none">
      <a
        href="#"
        class="text-gray-700 block px-5 py-3 text-sm font-light flex items-center hover:bg-gray-100 dark dark:hover:bg-gray-200 rounded-lg"
        @click="signout()"
        
      >
        <svg
          style="margin-right: 11px ; margin-left: 3px; width: 18px;"
          xmlns="http://www.w3.org/2000/svg"
          width="20.537"
          height="20"
          viewBox="0 0 20.537 20"
        >
          <g
            id="Iconly_Light-Outline_Logout"
            data-name="Iconly/Light-Outline/Logout"
            transform="translate(-2 -2)"
          >
            <g id="Logout" transform="translate(2 2)">
              <path
                id="Combined-Shape"
                d="M9.308,0a4.441,4.441,0,0,1,4.436,4.436v.932a.75.75,0,0,1-1.5,0V4.436A2.939,2.939,0,0,0,9.308,1.5H4.433A2.937,2.937,0,0,0,1.5,4.436V15.565A2.937,2.937,0,0,0,4.433,18.5H9.319a2.929,2.929,0,0,0,2.925-2.924v-.943a.75.75,0,0,1,1.5,0v.943A4.43,4.43,0,0,1,9.319,20H4.433A4.439,4.439,0,0,1,0,15.565V4.436A4.44,4.44,0,0,1,4.433,0Zm8.08,6.554,2.928,2.915a.739.739,0,0,1,.071.081l-.071-.081a.736.736,0,0,1,.094.114c.012.018.024.038.034.057s.017.031.024.048.012.029.018.043.014.039.02.058.008.031.012.047.008.038.011.058,0,.026,0,.039a.669.669,0,0,1,0,.067l-.005.062,0,.04v.005L20.537,10a.751.751,0,0,1-.018.164c0,.016-.008.031-.012.047s-.012.04-.02.059-.012.029-.018.043-.015.032-.024.048-.022.038-.035.057l-.023.032a.786.786,0,0,1-.078.088l-2.921,2.909a.75.75,0,1,1-1.058-1.062l1.64-1.635H7.746a.75.75,0,0,1,0-1.5H17.972L16.33,7.616a.75.75,0,1,1,1.058-1.062Z"
                fill-rule="evenodd"
                fill="#4A5568"
              />
            </g>
          </g>
        </svg>
        Sign out</a
      >
    </div>
  </div>

  <!-- <div class="overlay " @click="toggleDropdown" v-if="dropdown"></div> -->
  <div class="overlay fixed inset-0 bg-black bg-opacity-40 z-30" @click="toggleDropdown" v-if="dropdown"></div>

</template>

<style scoped>
.navbar {
  display: flex;
  /* border: 1px solid red; */
  /* width: 1366px; */
  height: 80px;
  background: #ffffff;
  box-shadow: 0px 3px 10px #457aef0d;
  border-radius: 0px 0px 30px 30px;
  opacity: 1;
}

.logo {
  /* border: 1px solid red; */
  width: 30%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.dropdown {
  /* border: 1px solid red; */
  width: 25%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.backdrop {
  /* background-color: red; */
  width: 50px;
  height: 50px;
  margin-left: -18px;
}

.backdrop svg {
  justify-content: center;
  align-items: center;
  fill: #4675c0;
  margin-top: 15px;
  margin-left: 15px;
}

.backdrop:hover svg {
  fill: #b8bfd6;
}

#profile {
  width: 50px;
  height: 50px;
  margin-right: 20px;
  margin-left: 50px;
}

svg {
  width: 20px;
  height: 20px;
  vertical-align: middle;
  margin-right: 10px;
  fill: #b8bfd6;
  /* border: 1px solid red; */
}

ul {
  /* border: 1px solid red; */
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  list-style: none;
  letter-spacing: 0.18px;
  opacity: 1;
  font-family: Poppins;
  font-size: 16px;
  font-weight: 400;
  color: #b8bfd6;
}

ul li {
  margin-right: 70px;
  display: flex;
  /* border: 1px solid red; */
}

ul li:hover svg,
ul li:hover span {
  color: #4675c0;
  fill: #4675c0;
  cursor: pointer;
  text-decoration: none;
}

.active_nav {
  /* background-color: #4675c0; */
  color: #4675c0;
}

.active_icon {
  fill: #4675c0;
}

.user {
  position: absolute;
  /* margin-top: -5px; */
  margin-left: 81.6%;
  float: right;

  border-radius: 12px;
  background-color: rgb(255, 255, 255);
  width: 15%;
  height: 20%;
  z-index: 1;
  box-shadow: 0px 0px 30px #1236840d;
}

.role {
  background-color: #4675c0;
  font-size: 12px;
  width: auto;
  z-index: 1;
  color: white;
  font-weight: 100;
  letter-spacing: 0.8px;
  float: right;
  border-radius: 5px;
  padding: 0px 10px 0px 10px;
  margin-left: auto;
}
</style>
