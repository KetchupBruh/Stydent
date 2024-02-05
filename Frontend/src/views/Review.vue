<script setup>
import { ref, onBeforeMount, computed } from "vue";
import { useRouter } from "vue-router";

const courses = ref([]);
const loading = ref(false);
const filteredCourses = ref([]);
const searchQuery = ref("");
const filterCriteria = ref("all");
const role = localStorage.getItem("role");

const currentPage = ref(1);
const totalPages = ref(1);
const pageSize = ref(9);

onBeforeMount(async () => {
  try {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}course/`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + localStorage.getItem("token"),
      },
    });

    if (response.status === 200) {
      const data = await response.json();
      courses.value = data;
      filteredCourses.value = data;
      // totalPages.value = data.totalPages;
    }
    if (response.status === 401) {
      signout();
    } else {
      console.error("Failed to fetch courses");
    }
  } catch (error) {
    console.error("An error occurred:", error);
  }
});

// const fetchCourses = async () => {
//   try {
//     const response = await fetch(
//       `${import.meta.env.VITE_BASE_URL}course/
//       }`,
//       {
//         method: "GET",
//         headers: {
//           "Content-Type": "application/json",
//         },
//       }
//     );

//     if (response.status === 200) {
//       const data = await response.json();
//       courses.value = data;
//       filteredCourses.value = data;
//       // totalPages.value = data.totalPages;
//     } else {
//       console.error("Failed to fetch courses");
//     }
//   } catch (error) {
//     console.error("An error occurred:", error);
//   }
// };

// const nextPage = async () => {
//   if (currentPage.value < totalPages.value) {
//     currentPage.value += 1;
//     await fetchCourses();
//   }
// };

// const prevPage = async () => {
//   if (currentPage.value > 1) {
//     currentPage.value -= 1;
//     await fetchCourses();
//   }
// };

// const gotoPage = async (page) => {
//   currentPage.value = page;
//   await fetchCourses();
// };

const filterCourses = () => {
  if (filterCriteria.value === "all") {
    // Filter by course name
    filteredCourses.value = courses.value.filter((course) =>
      `${course.courseName} ${course.courseFullName}`
        .toLowerCase()
        .includes(searchQuery.value.toLowerCase())
    );
  } else {
    // Filter by criteria (int, gen, ssc)
    filteredCourses.value = courses.value.filter(
      (course) =>
        course.courseName.toLowerCase().startsWith(filterCriteria.value) &&
        course.courseFullName
          .toLowerCase()
          .includes(searchQuery.value.toLowerCase())
    );
  }
};

const isSearchDisabled = computed(() => {
  return !searchQuery.value && filterCriteria.value === "all";
});

const clearFilter = () => {
  searchQuery.value = "";
  filterCriteria.value = "all";
  filterCourses();
};

const getCategoryColor = (categoryName) => {
  switch (categoryName) {
    case "GEN":
      return "#77D8DD";
    case "SSC":
      return "#A698F0";
    case "INT":
      return "#B7DB92";
  }
};

const signout = () => {
  localStorage.removeItem("username");
  localStorage.removeItem("email");
  localStorage.removeItem("role");
  localStorage.removeItem("token");
  alert("Token has expired. Please log in again.");
  Login();
};

const appRouter = useRouter();
const Login = () => appRouter.push({ name: "login" });
</script>

<template>
  <div class="container mx-auto mt-28 mb-10">
    <div class="search">
      <p class="topic">Search</p>
      <div class="line"></div>

      <div class="filter-container">
        <div class="filter-item">
          <p class="second">Category Subject</p>
          <select
            v-model="filterCriteria"
            class="dropdown appearance-none cursor-pointer border focus:border-blue-500 focus:outline-none"
          >
            <option value="all">ALL</option>
            <option value="int">INT</option>
            <option value="gen">GEN</option>
            <option value="ssc">SSC</option>
          </select>
        </div>

        <div class="filter-item">
          <p class="second">Subject Name</p>

          <input
            v-model.trim="searchQuery"
            list="coursesList"
            placeholder="Search for a course..."
            class="dropdown"
            required
          />

          <datalist id="coursesList" class="dropdown-list">
            <option
              v-for="course in courses"
              :key="course.id"
              :value="`${course.courseName} ${course.courseFullName}`"
            ></option>
          </datalist>
        </div>

        <div class="button-2">
          <button
            @click="filterCourses"
            :disabled="isSearchDisabled"
            class="button-search"
          >
            Search
          </button>
          <button
            @click="clearFilter"
            :disabled="isSearchDisabled"
            class="button-clear"
          >
            Clear
          </button>
        </div>
      </div>
    </div>

    <!-- ส่วนของแสดง course -->
    <div class="box-review">
      <div class="review-page">
        <div class="line-review"></div>
        <p class="review">Review</p>
      </div>

      <router-link :to="{ name: 'AddReview' }"
        ><button class="button-add-review" v-if="role === 'st_group'">
          <img src="../assets/add.png" class="add-icon" />
          Add
        </button>
      </router-link>

      <div v-if="filteredCourses.length > 0" class="flex flex-wrap">
        <div
          v-for="course in filteredCourses"
          :key="course.id"
          class="w-full sm:w-1/2 md:w-1/3 p-2"
        >
          <div class="course-card mx-auto">
            <div
              class="categoryname"
              :style="{
                backgroundColor: getCategoryColor(course.categoryName),
              }"
              v-if="course.categoryName === 'GEN'"
            >
              G
            </div>
            <div
              class="categoryname"
              :style="{
                backgroundColor: getCategoryColor(course.categoryName),
              }"
              v-if="course.categoryName === 'INT'"
            >
              I
            </div>
            <div
              class="categoryname"
              :style="{
                backgroundColor: getCategoryColor(course.categoryName),
              }"
              v-if="course.categoryName === 'SSC'"
            >
              S
            </div>

            <div class="course-detail">
              <h3 class="coursename">{{ course.courseName }}</h3>
              <p class="coursefullname">{{ course.courseFullName }}</p>
            </div>
            <div class="line-course"></div>
            <div class="credit">
              <img src="../assets/score.png" class="credit-icon" />
              Credit : {{ course.courseCredit }}
            </div>
            <router-link
              :to="{ name: 'ReviewDetail', params: { courseid: course.id } }"
              ><div class="see-detail">See Review


                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="33"
                  height="33"
                  viewBox="0 0 33 33"
                  class="arrow"
                >
                  <g
                    id="Icon_feather-arrow-right-circle"
                    data-name="Icon feather-arrow-right-circle"
                    transform="translate(-1.5 -1.5)"
                  >
                    <path
                      id="Path_134"
                      data-name="Path 134"
                      d="M33,18A15,15,0,1,1,18,3,15,15,0,0,1,33,18Z"
                      fill="none"
                      stroke="#4675c1"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="3"
                    />
                    <path
                      id="Path_135"
                      data-name="Path 135"
                      d="M18,24l6-6-6-6"
                      fill="none"
                      stroke="#4675c1"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="3"
                    />
                    <path
                      id="Path_136"
                      data-name="Path 136"
                      d="M12,18H24"
                      fill="none"
                      stroke="#4675c1"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="3"
                    />
                  </g>
                </svg>
              </div></router-link
            >
            
          </div>
        </div>
      </div>
      <div v-else>
        <div class="no-review mb-20">
          <svg
            class="notfound"
            xmlns="http://www.w3.org/2000/svg"
            width="57.258"
            height="59.981"
            viewBox="0 0 57.258 59.981"
          >
            <g id="Exp-2.-F" transform="translate(-2.866 -2.027)">
              <g id="Group_29519" data-name="Group 29519">
                <path
                  id="Path_19694"
                  data-name="Path 19694"
                  d="M5.061,34.389c5.109-2.222,5.877-5.062,5.767-6.923a4,4,0,0,0-7.949-.588,1,1,0,1,0,1.975.317,2,2,0,0,1,3.975.32c0,.02,0,.041,0,.061.085,1.394-.731,3.31-4.567,4.979a1,1,0,1,0,.8,1.834Z"
                  fill="#4675c1"
                  fill-rule="evenodd"
                />
                <path
                  id="Path_19695"
                  data-name="Path 19695"
                  d="M5.023,34.4c7.6-2.939,12.124-5.817,20.653-5.932a1,1,0,0,0-.027-2c-8.809.119-13.5,3.031-21.347,6.067A1,1,0,1,0,5.023,34.4Z"
                  fill="#4675c1"
                  fill-rule="evenodd"
                />
                <path
                  id="Path_19696"
                  data-name="Path 19696"
                  d="M5.229,40.3c8.21-5.638,12.854-7.632,29.445-7.824,17.911-.208,20.6-9.258,20.453-12.98a6.5,6.5,0,1,0-9.607,5.735,1,1,0,1,0,.958-1.755,4.5,4.5,0,1,1,6.65-3.954c0,.014,0,.029,0,.043.142,3.287-2.593,10.727-18.478,10.911-17.224.2-22.031,2.322-30.555,8.176A1,1,0,1,0,5.229,40.3Z"
                  fill="#4675c1"
                  fill-rule="evenodd"
                />
                <path
                  id="Path_19697"
                  data-name="Path 19697"
                  d="M23.265,40.27C26,38.2,31.484,37.787,35.422,39.195c2.4.859,4.256,2.417,4.247,4.883v0a3,3,0,1,1-5.874-.863,1,1,0,0,0-1.916-.576,5,5,0,1,0,9.79,1.441c.012-3.342-2.318-5.608-5.574-6.772-4.55-1.627-10.873-1.026-14.035,1.362a1,1,0,1,0,1.205,1.6Z"
                  fill="#4675c1"
                  fill-rule="evenodd"
                />
                <path
                  id="Path_19698"
                  data-name="Path 19698"
                  d="M43.714,35.471c4.371-.228,9.267,1.417,11.087,4.508a1,1,0,1,0,1.723-1.014c-2.133-3.625-7.789-5.759-12.914-5.492a1,1,0,0,0,.1,2Z"
                  fill="#4675c1"
                  fill-rule="evenodd"
                />
              </g>
              <path
                id="Path_19699"
                data-name="Path 19699"
                d="M13.448,21.172a15.945,15.945,0,0,0,8.367-2.385,1,1,0,1,0-1.147-1.638,13.559,13.559,0,0,1-7.114,2.026,1,1,0,1,0-.106,2Z"
                fill="#a4bbdb"
                fill-rule="evenodd"
              />
              <path
                id="Path_19700"
                data-name="Path 19700"
                d="M19.922,18.771C16.324,15.46,18.085,9.119,25.2,6.5c3.826-1.407,5.913-2.819,6.963-4a.959.959,0,0,1,1.7.082c3.768,8.942-6.051,23.448-13.936,16.19Z"
                fill="#cadcf0"
                fill-rule="evenodd"
              />
              <path
                id="Path_19701"
                data-name="Path 19701"
                d="M33.924,2.741c3.587,8.952-6.163,23.245-14,16.03a5.532,5.532,0,0,1-1.579-2.492c6.682,6.8,16.865-4.555,15.581-13.538Z"
                fill="#a4bbdb"
                fill-rule="evenodd"
              />
              <path
                id="Path_19702"
                data-name="Path 19702"
                d="M5.513,54.49a13.562,13.562,0,0,1,6.464-3.6,1,1,0,0,0-.348-1.97,15.947,15.947,0,0,0-7.6,4.23A1,1,0,1,0,5.513,54.49Z"
                fill="#a4bbdb"
                fill-rule="evenodd"
              />
              <path
                id="Path_19703"
                data-name="Path 19703"
                d="M10.3,50.273c-.2,4.884,5.526,8.123,12.407,4.943,3.7-1.71,6.174-2.187,7.755-2.093a.958.958,0,0,0,1.14-1.257c-3.658-8.987-20.858-12.3-21.3-1.593Z"
                fill="#cadcf0"
                fill-rule="evenodd"
              />
              <path
                id="Path_19704"
                data-name="Path 19704"
                d="M11.817,46A6.872,6.872,0,0,0,10.3,50.273c-.2,4.884,5.526,8.123,12.407,4.943,3.7-1.71,6.174-2.187,7.755-2.093a.958.958,0,0,0,.886-.22.878.878,0,0,1-.1-.037c-1.554-.342-4.073-.216-7.979,1.029-7.264,2.315-12.493-2-11.612-7.144q.066-.387.155-.747Z"
                fill="#a4bbdb"
                fill-rule="evenodd"
              />
              <path
                id="Path_19705"
                data-name="Path 19705"
                d="M43.472,47.535a13.866,13.866,0,0,1,5.592,2.352,1,1,0,0,0,1.151-1.635,16.06,16.06,0,0,0-6.5-2.7,1,1,0,1,0-.241,1.985Z"
                fill="#a4bbdb"
                fill-rule="evenodd"
              />
              <path
                id="Path_19706"
                data-name="Path 19706"
                d="M48.83,47.885c-3.1,2.473-2.034,7.7,3.57,10.221a14.955,14.955,0,0,1,5.228,3.417.955.955,0,0,0,1.683-.037c3.235-7.055-3.782-18.95-10.481-13.6Z"
                fill="#cadcf0"
                fill-rule="evenodd"
              />
              <path
                id="Path_19707"
                data-name="Path 19707"
                d="M47.336,50.03c-.846,2.649.785,6.151,5.064,8.076a14.955,14.955,0,0,1,5.228,3.417.952.952,0,0,0,.852.485.939.939,0,0,0,.487-.146.956.956,0,0,1-.389-.479c-.808-1.324-2.57-3.048-5.993-5.077A10.83,10.83,0,0,1,47.336,50.03Z"
                fill="#a4bbdb"
                fill-rule="evenodd"
              />
            </g>
          </svg>

          <p>No Course</p>
        </div>
      </div>
    </div>
  </div>

  <!-- <nav aria-label="Page navigation example" class="pagination-container">
    <ul class="flex items-center justify-center -space-x-px h-8 text-sm">
      <li>
        <button
          @click="prevPage"
          :disabled="currentPage === 1"
          :class="{ 'opacity-50 cursor-not-allowed': currentPage === 1 }"
          class="flex items-center justify-center px-3 h-8 ms-0 leading-tight text-gray-500 bg-white border border-e-0 border-gray-300 rounded-s-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
        >
          <span class="sr-only">Previous</span>
          <svg
            class="w-2.5 h-2.5 rtl:rotate-180"
            aria-hidden="true"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 6 10"
          >
            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 1 1 5l4 4" />
          </svg>
        </button>
      </li>

      <li v-for="page in totalPages" :key="page">
        <button
          @click="gotoPage(page)"
          :class="{ 'z-10 flex items-center justify-center px-3 h-8 leading-tight text-blue-600 border border-blue-300 bg-blue-50 hover:bg-blue-100 hover:text-blue-700 dark:border-gray-700 dark:bg-gray-700 dark:text-white': currentPage === page, 'flex items-center justify-center px-3 h-8 leading-tight text-gray-500 bg-white border border-gray-300 hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white': currentPage !== page }"
        >
          {{ page }}
        </button>
      </li>

      <li>
        <button
          @click="nextPage"
          :disabled="currentPage === totalPages"
          :class="{ 'opacity-50 cursor-not-allowed': currentPage === totalPages }"
          class="flex items-center justify-center px-3 h-8 leading-tight text-gray-500 bg-white border border-gray-300 rounded-e-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
        >
          <span class="sr-only">Next</span>
          <svg
            class="w-2.5 h-2.5 rtl:rotate-180"
            aria-hidden="true"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 6 10"
          >
            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="m1 9 4-4-4-4" />
          </svg>
        </button>
      </li>
    </ul>
  </nav> -->
</template>

<style scoped>
.dropdown::placeholder {
  font-size: 14px;
  font-weight: 200;
  opacity: 0.5;
}

.dropdown:valid {
  font-size: 14px;
  font-weight: 500;
}
.button-2 {
  /* background-color: #697a98; */
}

.button-search {
  background-color: #4675c0;
  height: 40px;
  width: 100px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 300;
  letter-spacing: 0.56px;
  color: white;
  margin-right: 20px;
  margin-left: 330px;
  margin-top: 57px;
}

.button-search:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.button-clear:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.button-search:hover {
  /* opacity: 0.5; */
  background-color: #2152a0;
}

.button-search:active,
.button-clear:active {
  transform: scale(0.92); /* ลดขนาดของปุ่มเมื่อถูกคลิก */
  transition: transform 0.2s ease-in-out; /* เพิ่มเอฟเฟค transition */
}

.button-search:active:disabled,
.button-clear:active:disabled {
  transform: none; /* กำหนดให้ไม่มีการเปลี่ยนขนาดเมื่อถูกคลิกแต่ยัง disable อยู่ */
}

.button-search:hover:disabled {
  background-color: #4675c0; /* สีที่คุณต้องการเมื่อ hover แต่ปุ่ม disable */
}

.button-clear:hover:disabled {
  background-color: #ffffff; /* ใส่สีที่คุณต้องการเมื่อ hover แต่ปุ่ม disable */
}

.button-clear {
  background-color: white;
  border: solid 1px #4675c0;
  height: 40px;
  width: 100px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 300;
  letter-spacing: 0.56px;
  color: #4675c0;
}

.button-clear:hover {
  opacity: 0.5;
  /* background-color: #f4f8ff; */
}
.filter-container {
  display: flex;
  gap: 30px;
}

.filter-item {
  /* flex: 1; */
  margin-right: -20px;
}

.filter-item button {
  /* flex: 1; */
  margin-left: 50px;
}
.spinner {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
}
.search {
  position: absolute;
  left: 50%;
  right: 50%;
  transform: translate(-50%, -50%);
  width: 1255px;
  height: 175px;
  background: #ffffff 0% 0% no-repeat padding-box;
  box-shadow: 0px 0px 15px #457aef0d;
  border-radius: 20px;
  opacity: 1;
}

.search .topic {
  margin-top: 20px;
  margin-left: 30px;
  font: normal normal 500 18px/27px Poppins;
  letter-spacing: 0.72px;
  color: #4675c0;
  opacity: 1;
}

.search .second {
  margin-top: 25px;
  margin-left: 30px;
  font: normal normal 400 14px/27px Poppins;
  letter-spacing: 0.28px;
  color: #697a98;
  opacity: 1;
}

.inputsearch {
  letter-spacing: 0.28px;
  color: #636363;
  opacity: 1;
}

.textsearch {
  margin-left: 30px;
  margin-top: 5px;
  width: 300px;
  height: 40px;
  background: #ffffff 0% 0% no-repeat padding-box;
  border: 1px solid #4675c126;
  border-radius: 8px;
  opacity: 1;
}

.dropdown {
  margin-left: 30px;
  margin-top: 5px;
  padding-left: 15px;
  width: 300px;
  height: 40px;
  background: #ffffff 0% 0% no-repeat padding-box;
  border: 1px solid #4675c126;
  border-radius: 8px;
  opacity: 1;
}

.line {
  background-color: #ecf1f9;
  height: 1.5px;
  width: 95%;
  position: absolute;
  top: 58px;
  left: 50%;
  right: 50%;
  transform: translate(-50%, -50%);
}

.button {
  margin-top: -97px;
  margin-left: 730px;
  width: 100px;
  height: 42px;
  font: normal normal 400 14px/21px Poppins;
  color: white;
  background: var(--unnamed-color-4675c0) 0% 0% no-repeat padding-box;
  background: #4675c0 0% 0% no-repeat padding-box;
  border: 1px solid #4675c126;
  border-radius: 8px;
  opacity: 1;
}

.searchbutton {
  margin-top: -34px;
  margin-left: 2px;
}

.box-review {
  margin-top: 80px;
  padding-top: 120px;
}

.semi-transparent {
  opacity: 0.3;
}

.line-review {
  position: absolute;
  width: 8px;
  height: 39px;
  border-radius: 15px;
  margin-left: 15px;
  background-color: #4675c0;
}

.review {
  margin-left: 40px;
  color: var(--unnamed-color-19335a);
  text-align: left;
  font: normal normal 600 26px/39px Poppins;
  letter-spacing: 1.04px;
  color: #19335a;
  opacity: 1;
  margin-bottom: 20px;
}

.course-card {
  width: 390px;
  height: 236px;
  background-color: white;
  box-shadow: 0px 0px 15px #457aef0d;
  border-radius: 20px;
  opacity: 1;
  margin-bottom: 20px;
  position: relative;
}

.categoryname {
  width: 71px;
  height: 70px;
  background: #77d8dd 0% 0% no-repeat padding-box;
  border-radius: 15px;
  opacity: 1;
  text-align: center;
  font: normal normal 600 40px/60px Poppins;
  color: #ffffff;
  padding-top: 6px;
  position: absolute;
  margin-left: 20px;
  margin-top: 20px;
}

.coursename {
  color: var(--unnamed-color-19335a);
  text-align: left;
  font: normal normal 600 20px/30px Poppins;
  letter-spacing: 0.4px;
  color: #19335a;
  opacity: 1;
  margin-left: 110px;
}
.coursefullname {
  color: var(--unnamed-color-b8bfd6);
  text-align: left;
  font: normal normal lighter 14px/22px Poppins;
  letter-spacing: 0.28px;
  color: #b8bfd6;
  text-transform: uppercase;
  opacity: 1;
  margin-left: 110px;
  margin-top: 3px;
}

.course-detail {
  padding-top: 17px;
}

.line-course {
  border: 1px solid #4675c11a;
  height: 1.5px;
  width: 88%;
  position: absolute;
  transform: translate(-50%, -50%);
  top: 50%;
  left: 50%;
  margin-top: -5px;
}

.see-detail {
  width: 88%;
  height: 44px;
  background: #d8e7ff 0% 0% no-repeat padding-box;
  box-shadow: 0px 0px 15px #457aef0d;
  border-radius: 15px;
  opacity: 1;
  text-align: center;
  position: absolute;
  bottom: 25px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  justify-content: center;

  font-weight: 500;
  font-size: 14px;
  letter-spacing: 0.2px;
  color: #4675c1;
  opacity: 1;
}

.credit {
  display: flex;
  align-items: flex-end;
  margin-top: 10px;
  position: absolute;
  bottom: 85px;
  margin-left: 75px;

  transform: translateX(-50%);
  color: var(--unnamed-color-b8bfd6);
  text-align: left;
  font-weight: 500;
  font-size: 14px;
  letter-spacing: 0.2px;
  color: #b8bfd6;
  opacity: 1;
}

.credit-icon {
  width: 25px;
  margin-right: 5px;
}

.add-icon {
  width: 13px;
  margin-right: 8px;
  margin-top: 0; /* Remove this line */
}

.button-add-review {
  font-size: 14px;
  font-weight: 300;
  letter-spacing: 0.56px;
  color: white;

  width: 100px;
  height: 40px;
  background: var(--unnamed-color-4675c0) 0% 0% no-repeat padding-box;
  background: #4675c0 0% 0% no-repeat padding-box;
  border: 1px solid #4675c126;
  border-radius: 10px;
  opacity: 1;
  margin-left: 1162px;
  /* float: right;
margin-right: 10px; */
  position: absolute;
  margin-top: -60px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
}

.button-add-review:hover {
  background-color: #2152a0;
}

.button-add-review:active {
  transform: scale(0.92);
  transition: transform 0.2s ease-in-out;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
  margin-bottom: 50px;
}

.pagination-container ul {
  display: inline-flex;
}

.no-review {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  height: 100%;
  margin-top: 5%;
}

.notfound {
  width: 20%;
  height: 20%;
}

.no-review p {
  margin-top: 10px;
  color: #b8bfd6;
  font: normal normal 400 18px/22px Poppins;
  letter-spacing: 0.28px;
}

.arrow {
  width: 20px;
  margin-left: 10px;
}

.see-detail:hover {
  background-color: #2152a0;
  background: #c3daff 0% 0% no-repeat padding-box;
}

</style>
