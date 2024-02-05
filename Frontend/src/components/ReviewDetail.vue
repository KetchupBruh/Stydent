<script setup>
import { useRoute } from "vue-router";
import { useRouter } from "vue-router";
import { ref, onBeforeMount, watch } from "vue";
import moment from "moment";
import BackNavbar from "./BackNavbar.vue";

const params = useRoute().params;

const reviews = ref([]);
const course = ref([]);
const showNoData = ref(false);
const isDropdownOpen = ref([]);
const currentPage = ref(1);
const totalPages = ref(1);
const pageSize = ref(10);
const email = localStorage.getItem("email");
const role = localStorage.getItem("role");

const nextPage = async () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value += 1;
    await fetchReviews();
  }
};

const prevPage = async () => {
  if (currentPage.value > 1) {
    currentPage.value -= 1;
    await fetchReviews();
  }
};

// const gotoPage = async (page) => {
//   currentPage.value = page;
//   await fetchReviews();
// };

const fetchReviews = async () => {
  try {
    const response = await fetch(
      `${import.meta.env.VITE_BASE_URL}course/${params.courseid}/review?page=${
        currentPage.value
      }&size=${pageSize.value}`,
      {
        method: "GET",
        headers: {
          "content-type": "application/json",
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
      }
    );

    if (response.ok) {
      const data = await response.json();
      if (data) {
        reviews.value = data.content;
        totalPages.value = data.totalPages;
        isDropdownOpen.value = Array(reviews.value.length).fill(false);
      } else {
        console.error("Error: Failed to parse data");
      }
    } else {
      console.error("Error:", response.status);
    }
  } catch (error) {
    console.error("Error:", error);
  }
};

onBeforeMount(async () => {
  try {
    const responsereview = await fetch(
      `${import.meta.env.VITE_BASE_URL}course/${params.courseid}/review`,
      {
        method: "GET",
        headers: {
          "content-type": "application/json",
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
      }
    );

    const responsecourse = await fetch(
      `${import.meta.env.VITE_BASE_URL}course/${params.courseid}`,
      {
        method: "GET",
        headers: {
          "content-type": "application/json",
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
      }
    );

    if (responsereview.ok && responsecourse.ok) {
      const data_review = await responsereview.json();
      const data_course = await responsecourse.json();

      if (data_review && data_course) {
        reviews.value = data_review.content;
        totalPages.value = data_review.totalPages;
        course.value = data_course;
        isDropdownOpen.value = Array(reviews.value.length).fill(false);

        setTimeout(() => {
          showNoData.value = true;
        }, 10);
      } else {
        console.error("Error: Failed to parse data");
      }
    }

    if (responsereview.status === 401 || responsecourse.status === 401) {
      signout();
      return;
    } else {
      console.error("Error:", responsereview.status, responsecourse.status);
    }
  } catch (error) {
    console.error("Error:", error);
  }
});

const toggleDropdown = (index) => {
  if (isDropdownOpen.value && isDropdownOpen.value[index] !== undefined) {
    isDropdownOpen.value[index] = !isDropdownOpen.value[index];
  }
};

const deleteReview = async (id, index) => {
  if (confirm("You want to delete") == true) {
    const response = await fetch(
      `${import.meta.env.VITE_BASE_URL}review/${id}`,
      {
        method: "DELETE",
        headers: {
          "content-type": "application/json",
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
      }
    );
    if (response.status === 200) {
      isDropdownOpen.value[index] = false;
      alert("Delete!!!", `You delete event success`, "success");

      setTimeout(function () {
        reviews.value = reviews.value.filter((e) => e.idReview !== id);
      }, 500);

      console.log("delete success");
    } else console.log("cannot delete");
  }
};

const report = () => {
  alert("ยังไม่ได้ทำจ้า ไม่ต้องกดเนอะ");
};

const getGradeColor = (gradesReceived) => {
  switch (gradesReceived) {
    case "A":
      return "#4A328B";
    case "B+":
      return "#457AEF";
    case "B":
      return "#01A29A";
    case "C+":
      return "#1BA32E";
    case "C":
      return "#B9CA02";
    case "D+":
      return "#E59E00";
    case "D":
      return "#EA5E0E";
    case "F":
      return "#DD001B";
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
  <div class="container mx-auto mt-28">
    <!-- ส่วนของ breadcrumb -->
    <div class="navigation">
      <span style="color: #b8bfd6"
        >Review &nbsp;
        <span style="color: #4675c1">
          > &nbsp; {{ course.courseName }} {{ course.courseFullName }}
        </span>
      </span>
    </div>

    <!-- ส่วนของกรอบ box -->
    <div class="box" v-if="reviews.length > 0">
      <div class="box-content">
        <div class="box-line"></div>
        <div class="subject">
          {{ course.courseName }}
          <span class="subject-full"> ( {{ course.courseFullName }} )</span>
        </div>
      </div>

      <!-- ส่วนของ review แต่ละอัน -->
      <div
        class="review-box"
        v-for="(review, index) in reviews"
        :key="review.id"
      >
        <!-- ส่วนของ dropdown เพื่อจะแก้ไข + ลบ -->
        <div class="dropdown">
          <img
            class="info"
            src="../assets/info.png"
            @click="() => toggleDropdown(index)"
          />
        </div>

        <div class="dropdown-click">
          <div
            class="absolute right-3 z-12 mt-2 w-32 divide-y divide-gray-100 rounded-md bg-white shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none"
            v-if="isDropdownOpen[index]"
          >
            <router-link
              :to="{
                name: 'EditReview',
                params: { reviewid: review.idReview },
              }"
              tag="a"
              class="text-gray-700 block px-4 py-2.5 text-sm font-light flex items-center hover:bg-gray-100 dark dark:hover:bg-gray-200"
              v-if="role === 'st_group' && review.emailOwner === email"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="19.5"
                height="18.94"
                viewBox="0 0 19.5 18.94"
                style="margin-right: 15px; margin-left: 8px; width: 16px"
              >
                <g
                  id="Iconly_Light-Outline_Edit"
                  data-name="Iconly/Light-Outline/Edit"
                  transform="translate(-2 -3)"
                >
                  <g id="Edit" transform="translate(2 3)">
                    <path
                      id="Combined-Shape"
                      d="M18.75,17.44a.75.75,0,0,1,0,1.5H11.5a.75.75,0,0,1,0-1.5ZM14.116.654l1.723,1.339A2.821,2.821,0,0,1,17.1,3.768a2.862,2.862,0,0,1-.368,2.2l-.015.022L16.712,6c-.068.089-.362.461-1.847,2.322a.591.591,0,0,1-.046.069.749.749,0,0,1-.081.09l-.321.4-.228.285L12.5,11.285l-.34.425L7.148,17.98a2.447,2.447,0,0,1-1.886.914l-3.639.046h-.01a.751.751,0,0,1-.73-.577L.064,14.892a2.371,2.371,0,0,1,.46-2.037L9.944,1.073l.011-.013A3,3,0,0,1,14.116.654ZM8.894,4.787l-7.2,9a.879.879,0,0,0-.171.755l.681,2.885,3.039-.038a.949.949,0,0,0,.733-.352l3.235-4.048.417-.521h0l.417-.522,3.109-3.891Zm2.216-2.77-1.279,1.6,4.261,3.271c.82-1.027,1.36-1.7,1.41-1.768a1.36,1.36,0,0,0,.142-1,1.411,1.411,0,0,0-.652-.887c-.071-.049-1.756-1.357-1.808-1.4A1.5,1.5,0,0,0,11.11,2.017Z"
                      fill-rule="evenodd"
                    />
                  </g>
                </g>
              </svg>

              Edit
            </router-link>

            <a
              href="#"
              class="text-gray-700 block px-4 py-2.5 text-sm font-light flex items-center hover:bg-gray-100 dark dark:hover:bg-gray-200"
              @click="() => deleteReview(review.idReview, index)"
              v-if="
                role === 'staff_group' ||
                (role === 'st_group' && review.emailOwner === email)
              "
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="18.458"
                height="20"
                viewBox="0 0 18.458 20"
                style="margin-right: 15px; margin-left: 8px; width: 15px"
              >
                <g
                  id="Iconly_Light-Outline_Delete"
                  data-name="Iconly/Light-Outline/Delete"
                  transform="translate(-3 -2)"
                >
                  <g id="Delete" transform="translate(3 2)">
                    <path
                      id="Combined-Shape"
                      d="M16.385,6.72a.751.751,0,0,1,.688.808c-.006.068-.548,6.779-.86,9.594a2.976,2.976,0,0,1-3.09,2.842C11.79,19.987,10.5,20,9.247,20c-1.355,0-2.676-.015-3.983-.042a2.967,2.967,0,0,1-3.018-2.829c-.315-2.84-.854-9.534-.859-9.6a.749.749,0,0,1,.687-.808.77.77,0,0,1,.808.687c0,.043.224,2.777.464,5.482l.048.54c.121,1.344.244,2.636.343,3.536a1.472,1.472,0,0,0,1.558,1.494c2.5.053,5.051.056,7.8.006a1.5,1.5,0,0,0,1.626-1.507c.31-2.794.85-9.482.856-9.55A.766.766,0,0,1,16.385,6.72ZM11.345,0a2.033,2.033,0,0,1,1.962,1.506l.254,1.261a.9.9,0,0,0,.865.722h3.282a.75.75,0,1,1,0,1.5H.75a.75.75,0,1,1,0-1.5H4.031l.1-.006A.9.9,0,0,0,4.9,2.767L5.14,1.551A2.043,2.043,0,0,1,7.112,0Zm0,1.5H7.112a.529.529,0,0,0-.512.392l-.233,1.17a2.38,2.38,0,0,1-.128.427h5.979a2.386,2.386,0,0,1-.128-.427l-.243-1.216A.524.524,0,0,0,11.345,1.5Z"
                      fill-rule="evenodd"
                    />
                  </g>
                </g>
              </svg>

              Delete</a
            >

            <a
              href="#"
              class="text-gray-700 block px-4 py-2.5 text-sm font-light flex items-center hover:bg-gray-100 dark dark:hover:bg-gray-200"
              v-if="role === 'st_group' && email !== review.emailOwner"
              @click="report()"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="20.014"
                height="18.186"
                viewBox="0 0 20.014 18.186"
                style="margin-right: 15px; margin-left: 8px; width: 16px"
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

              Report</a
            >
          </div>
        </div>

        <!-- <img src="../assets/user.png" class="user" /> -->
        <img src="../assets/student.png" id="profile_review" />
        <div class="detail-user-review">
          <span
              class="grade flex items-center"
              :style="{
                borderColor: getGradeColor(review.gradesReceived),
              }"
            >
              <p
                class="grade_received"
                :style="{
                  backgroundColor: getGradeColor(review.gradesReceived),
                }"
              >
                {{ review.gradesReceived }}
              </p>
              <span
                :style="{
                  color: getGradeColor(review.gradesReceived),
                }"
                >Grades Received</span
              ></span
            >
         
          <p class="instructor flex items-center">
            <svg
              style="margin-right: 13px"
              xmlns="http://www.w3.org/2000/svg"
              width="11.118"
              height="14.18"
              viewBox="0 0 11.118 14.18"
            >
              <g id="teacher-svgrepo-com" transform="translate(-43.369 -28)">
                <g
                  id="Group_318"
                  data-name="Group 318"
                  transform="translate(43.369 28)"
                >
                  <g
                    id="Group_317"
                    data-name="Group 317"
                    transform="translate(0 0)"
                  >
                    <path
                      id="Path_178"
                      data-name="Path 178"
                      d="M89.844,6.616a3.308,3.308,0,1,0-3.308-3.308A3.312,3.312,0,0,0,89.844,6.616Zm0-5.826a2.518,2.518,0,1,1-2.518,2.518A2.521,2.521,0,0,1,89.844.79Z"
                      transform="translate(-84.285)"
                      fill="#697a98"
                    />
                    <path
                      id="Path_179"
                      data-name="Path 179"
                      d="M54.453,173.614l-1.085-4.5a1.181,1.181,0,0,0-1.152-.907H45.639a1.181,1.181,0,0,0-1.152.907l-1.085,4.5a1.185,1.185,0,0,0,1.152,1.463H53.3A1.185,1.185,0,0,0,54.453,173.614Zm-9.9.673a.4.4,0,0,1-.384-.487l1.085-4.5a.394.394,0,0,1,.384-.3h1.8l-.5,3.523a.4.4,0,0,0,.076.294l1.117,1.473Zm3.19-1.819L48.235,169H49.62l.491,3.471-1.183,1.56Zm5.557,1.819H49.723l1.117-1.473a.4.4,0,0,0,.076-.294l-.5-3.523h1.8a.394.394,0,0,1,.384.3l1.085,4.5A.4.4,0,0,1,53.3,174.287Z"
                      transform="translate(-43.369 -160.897)"
                      fill="#697a98"
                    />
                  </g>
                </g>
              </g>
            </svg>

            <span>{{ review.instructorName }}</span>
          </p>

          <p class="flex items-center">
            <svg
              style="margin-right: 13px; width: 18px"
              xmlns="http://www.w3.org/2000/svg"
              width="15.84"
              height="19.87"
              viewBox="0 0 15.84 19.87"
              class="user_review"
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
                    fill="#697A98"
                  />
                </g>
              </g>
            </svg>
            <span class="username_review">
              {{ review.emailOwner.slice(0, 5) }}
              <div class="dot_review"></div>
              {{
                moment(review.fileCreatedOn).locale("th").format("DD MMMM YYYY")
              }}
            </span>
          </p>
        </div>

        <div class="line_review"></div>
        <p class="assignment">
          Assignment
          <br />
          <span class="in_assignment">{{ review.work }}</span>
        </p>

        <p class="description">
          Description
          <br />
          <span class="in_description">{{ review.reviewDescription }}</span>
        </p>

        <!-- เนื้อหามีความน่าสนใจ -->
        <div class="rating_container">
          <div class="rating_review">
            <div v-if="review.ratingOfInteresting === 1">
              <p class="label_rating">เนื้อหามีความน่าสนใจ</p>
              <div class="box_star">⭐</div>
            </div>
            <div v-if="review.ratingOfInteresting === 2">
              <p class="label_rating">เนื้อหามีความน่าสนใจ</p>
              <div class="box_star">⭐&nbsp&nbsp⭐</div>
            </div>
            <div v-if="review.ratingOfInteresting === 3">
              <p class="label_rating">เนื้อหามีความน่าสนใจ</p>
              <div class="box_star">⭐&nbsp&nbsp⭐&nbsp&nbsp⭐</div>
            </div>
            <div v-if="review.ratingOfInteresting === 4">
              <p class="label_rating">เนื้อหามีความน่าสนใจ</p>
              <div class="box_star">⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐</div>
            </div>
            <div v-if="review.ratingOfInteresting === 5">
              <p class="label_rating">เนื้อหามีความน่าสนใจ</p>
              <div class="box_star">
                ⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐
              </div>
            </div>
          </div>

          <!-- เน้นการทำงานเดี่ยว -->
          <div class="rating_review">
            <div v-if="review.ratingOfIndividualWork === 1">
              <p class="label_rating">เน้นการทำงานเดี่ยว</p>
              <div class="box_star">⭐</div>
            </div>
            <div v-if="review.ratingOfIndividualWork === 2">
              <p class="label_rating">เน้นการทำงานเดี่ยว</p>
              <div class="box_star">⭐&nbsp&nbsp⭐</div>
            </div>
            <div v-if="review.ratingOfIndividualWork === 3">
              <p class="label_rating">เน้นการทำงานเดี่ยว</p>
              <div class="box_star">⭐&nbsp&nbsp⭐&nbsp&nbsp⭐</div>
            </div>
            <div v-if="review.ratingOfIndividualWork === 4">
              <p class="label_rating">เน้นการทำงานเดี่ยว</p>
              <div class="box_star">⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐</div>
            </div>
            <div v-if="review.ratingOfIndividualWork === 5">
              <p class="label_rating">เน้นการทำงานเดี่ยว</p>
              <div class="box_star">
                ⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐
              </div>
            </div>
          </div>

          <!-- เน้นการทำงานกลุ่ม -->
          <div class="rating_review">
            <div v-if="review.ratingOfGroupWork === 1">
              <p class="label_rating">เน้นการทำงานกลุ่ม</p>
              <div class="box_star">⭐</div>
            </div>
            <div v-if="review.ratingOfGroupWork === 2">
              <p class="label_rating">เน้นการทำงานกลุ่ม</p>
              <div class="box_star">⭐&nbsp&nbsp⭐</div>
            </div>
            <div v-if="review.ratingOfGroupWork === 3">
              <p class="label_rating">เน้นการทำงานกลุ่ม</p>
              <div class="box_star">⭐&nbsp&nbsp⭐&nbsp&nbsp⭐</div>
            </div>
            <div v-if="review.ratingOfGroupWork === 4">
              <p class="label_rating">เน้นการทำงานกลุ่ม</p>
              <div class="box_star">⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐</div>
            </div>
            <div v-if="review.ratingOfGroupWork === 5">
              <p class="label_rating">เน้นการทำงานกลุ่ม</p>
              <div class="box_star">
                ⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐
              </div>
            </div>
          </div>

          <div class="rating_review">
            <div v-if="review.ratingOfGradeCollect === 1">
              <p class="label_rating">วิชาเก็บเกรด</p>
              <div class="box_star">⭐</div>
            </div>
            <div v-if="review.ratingOfGradeCollect === 2">
              <p class="label_rating">วิชาเก็บเกรด</p>
              <div class="box_star">⭐&nbsp&nbsp⭐</div>
            </div>
            <div v-if="review.ratingOfGradeCollect === 3">
              <p class="label_rating">วิชาเก็บเกรด</p>
              <div class="box_star">⭐&nbsp&nbsp⭐&nbsp&nbsp⭐</div>
            </div>
            <div v-if="review.ratingOfGradeCollect === 4">
              <p class="label_rating">วิชาเก็บเกรด</p>
              <div class="box_star">⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐</div>
            </div>
            <div v-if="review.ratingOfGradeCollect === 5">
              <p class="label_rating">วิชาเก็บเกรด</p>
              <div class="box_star">
                ⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐
              </div>
            </div>
          </div>

          <div class="rating_review">
            <div v-if="review.ratingOfEasyExam === 1">
              <p class="label_rating">ข้อสอบไม่ยาก</p>
              <div class="box_star">⭐</div>
            </div>
            <div v-if="review.ratingOfEasyExam === 2">
              <p class="label_rating">ข้อสอบไม่ยาก</p>
              <div class="box_star">⭐&nbsp&nbsp⭐</div>
            </div>
            <div v-if="review.ratingOfEasyExam === 3">
              <p class="label_rating">ข้อสอบไม่ยาก</p>
              <div class="box_star">⭐&nbsp&nbsp⭐&nbsp&nbsp⭐</div>
            </div>
            <div v-if="review.ratingOfEasyExam === 4">
              <p class="label_rating">ข้อสอบไม่ยาก</p>
              <div class="box_star">⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐</div>
            </div>
            <div v-if="review.ratingOfEasyExam === 5">
              <p class="label_rating">ข้อสอบไม่ยาก</p>
              <div class="box_star">
                ⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- pagination -->
      <nav aria-label="Page navigation example" class="pagination-container">
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
                <path
                  stroke="currentColor"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M5 1 1 5l4 4"
                />
              </svg>
            </button>
          </li>
          <span
            class="flex items-center justify-center px-3 h-8 leading-tight text-sm text-gray-500 bg-white border border-gray-300 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400"
            >{{ currentPage }} of {{ totalPages }}</span
          >

          <!-- <li v-for="page in totalPages" :key="page">
            <button
              @click="gotoPage(page)"
              :class="{
                'z-10 flex items-center justify-center px-3 h-8 leading-tight text-blue-600 border border-blue-300 bg-blue-50 hover:bg-blue-100 hover:text-blue-700 dark:border-gray-700 dark:bg-gray-700 dark:text-white':
                  currentPage === page,
                'flex items-center justify-center px-3 h-8 leading-tight text-gray-500 bg-white border border-gray-300 hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white':
                  currentPage !== page,
              }"
            >
              {{ page }}
            </button>
          </li> -->

          <li>
            <button
              @click="nextPage"
              :disabled="currentPage === totalPages"
              :class="{
                'opacity-50 cursor-not-allowed': currentPage === totalPages,
              }"
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
                <path
                  stroke="currentColor"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="m1 9 4-4-4-4"
                />
              </svg>
            </button>
          </li>
        </ul>
      </nav>
    </div>

    <div v-else>
      <div class="no-review" v-if="showNoData">
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

        <p>No review</p>
      </div>
    </div>

    <BackNavbar />
  </div>
</template>

<style scoped>
.navigation {
  margin-top: -80px;
  margin-left: 10px;
  font: normal normal 400 14px/22px Poppins;
  letter-spacing: 0.28px;
}
.no-review {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  height: 100%;
  margin-top: 8%;
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

.box {
  position: relative;
  width: 1266px;
  /* height: 849px; */
  margin-top: 25px;
  margin-left: auto;
  margin-right: auto;
  padding-bottom: 30px;
  background: #ffffff 0% 0% no-repeat padding-box;
  box-shadow: 0px 0px 15px #457aef0d;
  border-radius: 20px;
  opacity: 1;

  display: flex;
  flex-direction: column;
  align-items: center;
}

.box-content {
  margin-left: -93%;
}
.box-line {
  float: left;
  margin-top: 22px;
  margin-bottom: 22px;
  width: 8px;
  height: 39px;
  background: #4675c0;
  border-radius: 15px;
  opacity: 1;
}

.subject {
  position: absolute;
  margin-left: 30px;
  padding-top: 22px;
  color: var(--unnamed-color-19335a);
  text-align: left;
  font: normal normal 600 26px/39px Poppins;
  letter-spacing: 1.2px;
  color: #19335a;
  opacity: 1;
}

.subject-full {
  color: var(--unnamed-color-b8bfd6);
  text-align: left;
  font: normal normal 400 14px/22px Poppins;
  /* font-size: 12px; */
  letter-spacing: 0.28px;
  color: #b8bfd6;
  opacity: 1;
}

.review-box {
  position: relative;
  width: 1188px;
  /* height: 328px; */
  /* UI Properties */
  background: #fafafa 0% 0% no-repeat padding-box;
  box-shadow: 0px 0px 5px #efefef;
  border: 1px solid #efefef;
  border-radius: 20px;
  opacity: 1;
  margin-top: 10px;
  margin-bottom: 20px;
  padding-bottom: 20px;
}

.review-box {
  white-space: pre-line; /* ทำให้ข้อความขึ้นบรรทัดใหม่ตามตำแหน่งของ "\n" */
  word-wrap: break-word; /* ทำให้ข้อความขึ้นบรรทัดใหม่ตามขอบกล่อง */
  overflow: hidden; /* ซ่อนข้อความที่เกินขอบกล่อง */
}

.box {
  margin-bottom: 100px;
}

.dropdown {
  float: right;
  position: relative;
  /* display: inline-block; */
  /* position: relative; */
}

.dropdown-click {
  position: relative;
  top: 40px;
  margin-top: 10px;
  z-index: 1;
}

.dropdown-content {
  display: none;
  position: absolute;
  background-color: #f9f9f9;
  min-width: 160px;
  box-shadow: 0px 8px 16px 0px rgba(0, 0, 0, 0.2);
  z-index: 1;
}

.dropdown-content a {
  color: black;
  padding: 12px 16px;
  text-decoration: none;
  display: block;
}

.dropdown-content a:hover {
  background-color: #f1f1f1;
}

.dropdown:hover .dropdown-content {
  display: block;
}

.semi-transparent {
  opacity: 0.3;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
  margin-bottom: 10px;
}

.info {
  width: 40px;
  margin-top: 10px;
  margin-right: 3px;
  z-index: 2;
}

.info:hover {
  opacity: 0.5;
}

.user_review {
  width: 69px;
  position: relative;
  margin-left: 20px;
  margin-top: 20px;
}

.assignment {
  text-align: left;
  font: normal normal medium 14px/21px Poppins;
  font-size: 14px;
  font-weight: 300;
  letter-spacing: 0.56px;
  opacity: 1;
  margin-bottom: 10px;
  margin-left: 40px;
  margin-top: 50px;
  color: #b8bfd6;
  width: 95%;
}

.in_assignment {
  color: #697a98;
  font-size: 14px;
}

.in_description {
  color: #697a98;
  font-size: 14px;
}
.description {
  color: #b8bfd6;
  text-align: left;
  font: normal normal medium 14px/21px Poppins;
  font-size: 14px;
  font-weight: 300;
  letter-spacing: 0.56px;
  opacity: 1;
  margin-bottom: 10px;
  margin-left: 40px;
  margin-top: 20px;
  width: 95%;
}

.rating_container {
  display: flex;
  justify-content: center;
}
.rating_review {
  width: 210px;
  height: 84px;
  /* UI Properties */
  background: #ffffff 0% 0% no-repeat padding-box;
  box-shadow: 0px 0px 15px #457aef0d;
  border: 1px solid #4675c158;
  border-radius: 15px;
  opacity: 1;
  /* display: inline-block; */
  /* margin-right: 20px; */
  margin-left: 12px;
  margin-right: 5px;
  margin-top: 30px;
  margin-bottom: 10px;
}

.createon {
  color: var(--unnamed-color-b8bfd6);
  text-align: left;
  font: normal normal medium 14px/21px Poppins;
  font-weight: 300;
  letter-spacing: 0.56px;
  color: #b8bfd6;
  opacity: 1;
  margin-bottom: 20px;
  /* padding-top:20px; */
}

.line_review {
  background-color: #e6e6e6;
  height: 1px;
  width: 95%;
  position: absolute;
  left: 50%;
  right: 50%;
  transform: translate(-50%, -50%);
  margin-top: 30px;
}

.detail-1 {
  margin-left: 120px;
  margin-top: -70px;
  position: relative;
  width: 1000px;
}

.createon-icon {
  width: 20px;
  margin-right: 10px;
  margin-top: -20px;
}
.createon-container {
  display: flex;
  align-items: center;
}

#profile_review {
  width: 70px;
  height: 70px;
  margin-left: 30px;
  margin-top: 20px;
}

.grade {
  background-color: white;
  width: 190px;
  height: 25px;
  border: 1px solid var(--unnamed-color-457aef);
  background: #b8bfd600 0% 0% no-repeat padding-box;
  box-shadow: 0px 0px 15px #457aef0d;
  border: 1px solid #457aef;
  border-radius: 13px;
  opacity: 1;
  margin-left: 112px;
  margin-bottom: 10px;
}

.grade span {
  color: var(--unnamed-color-457aef);
  text-align: left;
  font-size: 12px;
  letter-spacing: 0.48px;
  color: #457aef;
  opacity: 1;
  padding-left: 20px;
}

.grade_received {
  color: wite;
  width: 35px;
  height: 25px;
  /* UI Properties */
  background: #457aef 0% 0% no-repeat padding-box;
  font-size: 14px;
  font-weight: 300;

  border-radius: 13px 0px 0px 13px;
  opacity: 1;
  color: white;
  text-align: center;
  justify-content: center;
  padding: 2px;
}

.user_review {
  width: 12px;
  height: 12px;
  opacity: 1;
  /* display: flex inline; */
  z-index: 1;
  /* margin-left: -56.5px; */
  margin-left: 112px;
}

.instructor {
  color: var(--unnamed-color-697a98);
  text-align: left;
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.48px;
  color: #697a98;
  opacity: 1;
  margin-bottom: -18px;
  margin-left: 115px;
}

.instructor span {
  margin-left: -2px;
}
.username_review {
  color: var(--unnamed-color-697a98);
  text-align: left;
  font: normal normal medium 12px/18px Poppins;
  letter-spacing: 0.48px;
  font-size: 12px;
  font-weight: 500;
  color: #697a98;
  opacity: 1;
  margin-top: 23px;
  margin-left: -5px;
}

.dot_review {
  width: 5px;
  height: 5px;
  background: #b8bfd6;
  opacity: 1;
  display: flex inline;
  z-index: 1;
  border-radius: 50%;
  margin-left: 8px;
  margin-right: 5px;
}

.detail-user-review {
  float: left;
  margin-top: -60px;
  margin-left: 8px;
}

.label_rating {
  /* background-color: #457aef; */
  /* margin-top: 30px; */
  letter-spacing: var(--unnamed-character-spacing-0-48);
  color: var(--unnamed-color-457aef);
  text-align: left;
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.48px;
  color: #457aef;
  opacity: 1;
  padding-top: 10px;
  padding-bottom: 5px;
  text-align: center;
}

.box_star {
  width: 175px;
  height: 34px;
  /* UI Properties */
  background: #f1f6ff 0% 0% no-repeat padding-box;
  border-radius: 10px;
  opacity: 1;
  padding: 5px;
  text-align: center;
  margin-left: 8%;
  margin-right: 8%;
}
</style>
