<script setup>
import { ref, onBeforeMount, computed } from "vue";
import { useRouter } from "vue-router";
import moment from "moment";
import ToastSuccess from "../components/ToastSuccess.vue";
import ToastError from "../components/ToastError.vue";

const reviews = ref([]);
const summarys = ref([]);
const activeTab = ref("summary");
const summaryButtonColor = ref("#3498db"); // Color for the summary button
const reviewButtonColor = ref("#3498db"); // Color for the review button
const email = localStorage.getItem("email");
const role = localStorage.getItem("role");
const isDropdownOpen = ref([]);
const comments = ref([]);
const showPopup = ref(false);
const commentofsummary = ref([]);
const showSuccess = ref(false);
const showError = ref(false);
const errorMessage = ref("Add Comment Failed");
const successMessage = ref("Add Comment Success");

const hideError = () => {
  showError.value = false;
};

const hideSuccess = () => {
  showSuccess.value = false;
};

const itemsPerPage = ref(10);
const currentPage = ref(1);
const totalItems = computed(() => {
  return activeTab.value === "summary"
    ? summarys.value.length
    : reviews.value.length;
});

const paginatedItems = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  return activeTab.value === "summary"
    ? summarys.value.slice(start, end)
    : reviews.value.slice(start, end);
});

const totalPages = computed(() =>
  Math.ceil(totalItems.value / itemsPerPage.value)
);

const setPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

const togglePopup = (summaryId) => {
  showPopup.value = !showPopup.value;
  if (showPopup.value) {
    getcommentofsummary(summaryId);
  }
};

const editComment = (comment) => {
  comment.isEditing = true;
  comment.editingComment = comment.commentDetail;
};

const saveComment = async (commentId, newComment) => {
  try {
    const response = await fetch(
      `${import.meta.env.VITE_BASE_URL}comment/${commentId}`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
        body: JSON.stringify({
          commentDetail: newComment,
        }),
      }
    );

    if (response.status === 200) {
      console.log("Comment updated successfully");
      const editedComment = commentofsummary.value.find(
        (comment) => comment.id === commentId
      );
      if (editedComment) {
        editedComment.commentDetail = newComment;
        editedComment.isEditing = false;
      }
    } else {
      console.error("Failed to update comment");
    }
  } catch (error) {
    console.error("Error updating comment:", error);
  }
};

const cancelEdit = (comment) => {
  comment.isEditing = false;
};

const getcommentofsummary = async (summaryId) => {
  try {
    const response = await fetch(
      `${import.meta.env.VITE_BASE_URL}comment/${summaryId}/summary/`,
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
      commentofsummary.value = data.content;
      console.log(commentofsummary);
    } else {
      if (response.status === 404) {
        commentofsummary.value = [];
        console.log("No comments found");
      } else {
        console.error("Error: Failed to parse data");
      }
    }
  } catch (error) {
    console.error("Error fetching data:", error);
  }
};

const sendComment = async (index, summaryId) => {
  const commentValue = comments.value[index];

  if (commentValue.trim() !== "") {
    try {
      const response = await fetch(`${import.meta.env.VITE_BASE_URL}comment`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
        body: JSON.stringify({
          commentDetail: commentValue,
          courseFileIdcourseFile: summaryId,
          emailOwner: email,
        }),
      });

      if (response.status === 201) {
        console.log("Comment posted successfully");
        comments.value[index] = "";
        showSuccess.value = true;
        setTimeout(() => {
          hideSuccess();
        }, 1500);
      } else {
        console.error("Failed to post comment");
      }
    } catch (error) {
      console.error("Error posting comment:", error);
    }
  } else {
    console.log("Comment is empty");
  }
};

const getsummary = async () => {
  try {
    const response = await fetch(
      `${import.meta.env.VITE_BASE_URL}summary/owner`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
      }
    );

    if (response.status === 200) {
      const data = await response.json();
      summarys.value = data;
    }
    if (response.status === 401) {
      signout();
    } else {
      console.error("Get Summary failed");
    }
  } catch (error) {
    console.error("Error fetching summary data:", error);
  }
};

const getreview = async () => {
  try {
    const response = await fetch(
      `${import.meta.env.VITE_BASE_URL}review/owner`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
      }
    );

    if (response.status === 200) {
      const data = await response.json();
      reviews.value = data;
    } else {
      console.error("Get Review failed");
    }
    isDropdownOpen.value = Array(summarys.value.length).fill(false);
  } catch (error) {
    console.error("Error fetching review data:", error);
  }
};

const deleteSummary = async (id, index) => {
  if (confirm("You want to delete") == true) {
    const response = await fetch(
      `${import.meta.env.VITE_BASE_URL}summary/${id}`,
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
      alert("Delete!!!", `You delete summary success`, "success");

      setTimeout(function () {
        summarys.value = summarys.value.filter((e) => e.id !== id);
      }, 500);

      console.log("delete success");
    } else console.log("cannot delete");
  }
};

const deletecomment = async (id) => {
  if (confirm("You want to delete comment") == true) {
    const response = await fetch(
      `${import.meta.env.VITE_BASE_URL}comment/${id}`,
      {
        method: "DELETE",
        headers: {
          "content-type": "application/json",
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
      }
    );
    if (response.status === 200) {
      alert("Delete!!!", `You delete summary success`, "success");

      commentofsummary.value = commentofsummary.value.filter(
        (e) => e.id !== id
      );
    } else console.log("cannot delete");
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
        reviews.value = reviews.value.filter((e) => e.id !== id);
      }, 500);

      console.log("delete success");
    } else console.log("cannot delete");
  }
};

const getDownloadLink = (fileName) => {
  return `${import.meta.env.VITE_BASE_URL}files/${fileName}`;
};

const extractFileName = (filePath) => {
  const parts = filePath.split("/");
  return parts[parts.length - 1];
};

const toggleDropdown = (index) => {
  // ตรวจสอบว่า Object มีค่าหรือยัง ถ้าไม่มีให้สร้าง Object ใหม่
  if (!isDropdownOpen.value[index]) {
    isDropdownOpen.value[index] = false;
  }
  isDropdownOpen.value[index] = !isDropdownOpen.value[index];
};

onBeforeMount(() => {
  activeTab.value = "summary";
  updateButtonColors();
  getsummary();
  getreview();
});

const switchTab = (tab) => {
  activeTab.value = tab;
  updateButtonColors();
};

const updateButtonColors = () => {
  summaryButtonColor.value =
    activeTab.value === "summary" ? "#FFFFFF" : "#E2E6F4";
  reviewButtonColor.value =
    activeTab.value === "review" ? "#FFFFFF" : "#E2E6F4";
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
  <div class="container mx-auto mt-28 mt-8 mb-10">
    <div class="toast-container">
      <ToastError
        :showError="showError"
        :errorMessage="errorMessage"
        @hide-error="hideError"
      />
      <ToastSuccess
        :showSuccess="showSuccess"
        :successMessage="successMessage"
        @hide-success="hideSuccess"
      />
    </div>
    <div class="line-review"></div>
    <p class="review">My Category</p>

    <div class="all">
      <button
        :style="{
          backgroundColor: summaryButtonColor,
          color: activeTab === 'summary' ? '#4675c0' : '#B8BFD6',
        }"
        @click="switchTab('summary')"
        class="font-light tab"
      >
        Summary
        <span class="count" :class="{ inactive: activeTab !== 'summary' }">{{
          summarys.length
        }}</span>
      </button>

      <button
        :style="{
          backgroundColor: reviewButtonColor,
          color: activeTab === 'review' ? '#4675c0' : '#B8BFD6',
        }"
        @click="switchTab('review')"
        class="font-light tab"
      >
        Review
        <span class="count" :class="{ inactive: activeTab !== 'review' }">{{
          reviews.length
        }}</span>
      </button>

      <!-- ส่วนของกล่อง summary -->
      <div
        class="box"
        v-if="
          activeTab === 'summary' &&
          summarys.length > 0 &&
          paginatedItems.length > 0
        "
      >
        <div
          class="review-box"
          v-for="(summary, index) in paginatedItems"
          :key="summary.id"
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
                :to="{ name: 'EditSummary', params: { summaryid: summary.id } }"
                tag="a"
                class="text-gray-700 block px-4 py-2.5 text-sm font-light flex items-center hover:bg-gray-100 dark dark:hover:bg-gray-200"
                v-if="role === 'st_group' && summary.emailOwner === email"
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
                @click="() => deleteSummary(summary.id, index)"
                v-if="
                  role === 'staff_group' ||
                  (role === 'st_group' && summary.emailOwner === email)
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
                v-if="role === 'st_group' && email !== summary.emailOwner"
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

          <!-- ส่วนของ summary แต่ละกล่อง -->
          <img src="../assets/student.png" id="profile" />
          <div class="box-title">
            <p class="title_summary">{{ summary.title }}</p>

            <span
              class="course_subject"
              :style="{
                backgroundColor: getCategoryColor(summary.categoryName),
              }"
              v-if="summary.categoryName === 'INT'"
              >{{ summary.courseName }} {{ summary.courseFullName }}</span
            >
            <span
              class="course_subject"
              :style="{
                backgroundColor: getCategoryColor(summary.categoryName),
              }"
              v-if="summary.categoryName === 'GEN'"
              >{{ summary.courseName }} {{ summary.courseFullName }}</span
            >
            <span
              class="course_subject"
              :style="{
                backgroundColor: getCategoryColor(summary.categoryName),
              }"
              v-if="summary.categoryName === 'SSC'"
              >{{ summary.courseName }} {{ summary.courseFullName }}</span
            ><br />
            <svg
              style="margin-right: 13px; width: 18px"
              xmlns="http://www.w3.org/2000/svg"
              width="15.84"
              height="19.87"
              viewBox="0 0 15.84 19.87"
              class="user"
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

            <span class="username">
              {{ summary.emailOwner.slice(0, 5) }}
              <div class="dot"></div>
              {{
                moment(summary.fileCreatedOn)
                  .locale("th")
                  .format("DD MMMM YYYY")
              }}
            </span>
          </div>
          <p class="filedescription">{{ summary.fileDescription }}</p>

          <p class="filedownload tooltip">
            <a :href="getDownloadLink(summary.fileUpload)" download>
              <div class="flex items-center space-x-2 ml-4">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  xmlns:xlink="http://www.w3.org/1999/xlink"
                  width="31"
                  height="31"
                  viewBox="0 0 31 31"
                >
                  <image
                    id="pdf"
                    width="31"
                    height="31"
                    xlink:href="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAgAAAAIACAYAAAD0eNT6AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAN1wAADdcBQiibeAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAB90SURBVHja7d0JlCR1fcDxX8997Mmy7MXCuotcEqIIymGEsF4RIooIiiheSDwSkyiYYBTxERUx8cCHCkESDCAqBpCIF4gHeCwgcoPcuyyLBPaeq69U9Y7GwAI72z0z1V2fz0ul8flsZn7dU/9vV3dXFarVatC6lq9c1Z/cHJxsS5Jt/ma2qaYEk+O+B1b8+MX77X2QSTAZCgKgJRf92cnNq5Lt1cn2kmTrMRXInhtuui1mTp8mAhAA1LXodyU3b0+2o5Nt/2RrMxXIfgCkRACTwSLR/At/IdnekPzj7cl2ZrK9yOMKzWX12nUH/uTn111tEggAtnTxT9/bX5ZsFyTbYhMBEQACoLUX/rnJ9p3kH69MtuebCIgAEACtv/jvNfqq/y9MA0QACIB8LP6vS25+mmzbmwaIABAArb/wpx/0+2jyjxclW5+JgAgAAZCDxT+5+Y9kOznZCiYCIgAEQD58JNneZAwgAkAA5OfV/xGjr/wBESACEAA5WfzTT/unh/4d9gdEAAIgJ4v/3OTm0vCBP0AEIABy5ezwVT9ABCAAcvXq/6Dk5lCTAEQAAiA/i3/6fv+nTAIQAQiAfDky2fYxBkAEIADy8+q/K7n5uEkAIgABkC9vD5f0BUQAAiB3jjYCQAQgAHJk+cpVs5Ob/U0CEAEIgHx5lccCEAEIgPx5tREAIgABkCPLV67qT25eYhKACEAA5MvBydZjDIAIQADkyxIjAEQAAiB/5hsBIAIQAAIAQAQgAAQAgAhAAAgAABGAABAAACIAAdCcphoBIAIQAACIAAQAACIAAQCACEAAACACEAAAiAAEAAAiAAEAgAhAAAAgAgQAAIgAAQAAIkAAACACEAAAiAAEAAAiAAEAgAhAAAAgAhAAAIgABAAAIgABAIAIQAAAIAIQAACIAAQAACIAAQCACEAAACACEAAAiAAEAAAiQAAAgAgQAAAgAgQAAIgAAQAAIkAAAIAIEAAAIAIEAACIAAEAACJAAAAgAkSAAABABCAAABABCAAARAACAAARgAAAQAQgAAAQAQgAAEQAAgAAESAAAEAECAAAEAECAABEgAAAABEgAABABAgAABABAgAARIAAAAARIAAAQAQIAAAQAQIAAESAAAAAESAAAEAECAAAEAECAABEgAAAQAQgAAAQAQgAAESAAAAAESAAAEAECAAAEAECAIB6tLfZBYsAAQCQO52dHYYgAgQAQP4CoNMQRIAAAMhdAHQ4AiACBABADo8ACAARIAAAchgA3gIQAQIAIHe6uwSACBAAALkzdUp/tBUKBiECBABAnrS1tcXUqVMMQgQIAIC8mTFtqiGIAAEAkDfTBYAIEAAA+dPR0R5T+vsMQgQIAIC8mTljuiGIAAEAkDfbbjMjuru6DEIECACAPCkUCjF/7nYGIQIEAEDezJwxLfr7eg1CBAgAgLxZMG+OIYgAAQCQN+m3AZwXQAQIAIAc2mH7+dHlGgEiQAAA5Et6XoAlixbWThOMCBAAADnS29MTixbONwgRIAAA8mbG9Gkxb85sgxABAgAgb9IA2GamswSKAAEAkDuLFi5wJEAECACAvB4JWLzj9j4YKAIEAEDepJ8J2HnJoujs9BVBESAAAHKlr7cndn32s2K6kwWJgAYoVKtVj9AkWr5ylQcAGLP1GzbGQ6t+FwMDg4bRBGZOn/bjF++390ECAAEANOYV5pp1sTIJgeGREcMQAQJAAAB5ku7H/+fxNUkMrI0NGwcMRAQIAAEA5E2pVI6169bHmmRbv35DVOzjRYAAEABAvlQqldpnBYZHilEsplspiqXSH/65nPz35DcCOjwMAK0pPW/A031jYMPAULING9TkOHDSnx8eA4B8mtLXk2zdBpHXQDQCABGAAABABCAAABABCAAARAACAAARgAAAQAQgAAAQAQgAAEQAAgAAEYAAAEAEIAAAEAEIAABEAAIAABGAAABABCAAABABCAAARAACAAARgAAAQAQIAAAQAQIAAESAAAAAESAAAEAECAAAEAECAAARIAIEAAAiAAEAgAhAAAAgAhAAAIgABAAAIgABAIAIQAAAIAIQAACIAAQAACIAAQCACEAAACACBAAAiAABAAAiQAAAgAgQAAAgAgQAAIgAAQAAIkAAAIAIEAAAIAIEAACIAAEAACJAAACACBAAACACBAAAIgABAIAIQAAAIAIQAACIAAQAACJAAACACBAAACACBAAAiAABAAAiQAAAgAgQAAAgAgQAAIgAAQAAIkAA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"
                  />
                </svg>
                <span
                  class="text-sm pl-2"
                  style="color: #4675c1; cursor: pointer"
                >
                  {{ extractFileName(summary.fileUpload).slice(14, 34) }}
                  <span v-if="summary.fileUpload.length > 40">...</span>
                </span>
              </div>
              <span class="tooltip-text">Click To Download File</span>
            </a>
          </p>

          <div
            class="count_comment flex items-center"
            @click="togglePopup(summary.id)"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="21.493"
              height="21.494"
              viewBox="0 0 21.493 21.494"
            >
              <g
                id="Iconly_Light-Outline_Chat"
                data-name="Iconly/Light-Outline/Chat"
                transform="translate(-1 -1)"
              >
                <g id="Chat" transform="translate(1 1)">
                  <path
                    id="Combined-Shape"
                    d="M10.748,0a10.748,10.748,0,0,1,7.6,18.35,10.8,10.8,0,0,1-12.13,2.155,3.579,3.579,0,0,0-1.106-.33,4.334,4.334,0,0,0-1.195.3c-.874.3-1.962.674-2.767-.128s-.432-1.888-.134-2.761a4.3,4.3,0,0,0,.3-1.21,3.513,3.513,0,0,0-.337-1.135,10.783,10.783,0,0,1,2.17-12.1A10.674,10.674,0,0,1,10.748,0Zm0,1.5A9.186,9.186,0,0,0,4.208,4.208a9.293,9.293,0,0,0-1.853,10.44,4.722,4.722,0,0,1,.46,1.729,5.407,5.407,0,0,1-.378,1.694c-.146.428-.367,1.074-.225,1.216S3,19.2,3.43,19.057a5.46,5.46,0,0,1,1.678-.382,4.646,4.646,0,0,1,1.706.453A9.255,9.255,0,0,0,17.289,4.207,9.186,9.186,0,0,0,10.749,1.5Z"
                    fill="#4675c0"
                    fill-rule="evenodd"
                  />
                </g>
              </g>
            </svg>
            <span class="text-sm font-light underline flex items-center"
              >Comment</span
            >
          </div>

          <!-- comment popup -->
          <div v-if="showPopup" class="popup-container bg-black bg-opacity-40">
            <div class="popup">
              <div class="popup-content">
                <div v-if="commentofsummary.length > 0">
                  <div class="comment_header">
                    ({{ commentofsummary.length }}) Comments
                  </div>
                  <div class="line-comment-popup"></div>
                  <div
                    v-for="(comment, commentIndex) in commentofsummary"
                    :key="commentIndex"
                  >
                    <div v-if="comment.isEditing" class="detail-comment">
                      <img src="../assets/student.png" id="profile_comment" />

                      <input
                        class="edit-detail-comment-box"
                        v-model.trim="comment.editingComment"
                      /><br />

                      <div
                        class="button-edit-delete flex items-center"
                        v-if="
                          role === 'st_group' && email === comment.emailOwner
                        "
                      >
                        <button
                          v-if="
                            role === 'st_group' && email === comment.emailOwner
                          "
                          class="save-comment flex items-center"
                          @click="
                            saveComment(comment.id, comment.editingComment)
                          "
                        >
                          Save
                        </button>
                        <div class="line-edit-delete"></div>
                        <button
                          v-if="
                            role === 'st_group' && email === comment.emailOwner
                          "
                          class="cancle-comment flex items-center"
                          @click="cancelEdit(comment)"
                        >
                          Cancel
                        </button>
                      </div>
                    </div>

                    <div class="detail-comment" v-else>
                      <img src="../assets/student.png" id="profile_comment" />

                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="15.84"
                        height="19.87"
                        viewBox="0 0 15.84 19.87"
                        class="user_comment"
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
                      <span class="username-comment">
                        {{ comment.emailOwner.slice(0, 5) }}
                        <div class="dot"></div>
                        {{
                          moment(summary.fileCreatedOn)
                            .locale("th")
                            .format("DD MMMM YYYY")
                        }}
                      </span>
                      <div class="detail-comment-box">
                        {{ comment.commentDetail }}
                      </div>
                      <br />

                      <div
                        class="button-edit-delete flex items-center"
                        v-if="
                          role === 'st_group' && email === comment.emailOwner
                        "
                      >
                        <button
                          v-if="
                            role === 'st_group' && email === comment.emailOwner
                          "
                          class="edit-comment flex items-center"
                          @click="editComment(comment)"
                        >
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            width="19.5"
                            height="18.94"
                            viewBox="0 0 19.5 18.94"
                            style="
                              margin-right: 10px;
                              margin-left: 8px;
                              width: 16px;
                            "
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
                                  fill="#19335a"
                                  fill-rule="evenodd"
                                />
                              </g>
                            </g>
                          </svg>
                          Edit
                        </button>

                        <div class="line-edit-delete"></div>

                        <button
                          v-if="
                            role === 'staff_group' ||
                            (role === 'st_group' &&
                              email === comment.emailOwner)
                          "
                          class="delete-comment flex items-center"
                          @click="deletecomment(comment.id)"
                        >
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            width="18.458"
                            height="20"
                            viewBox="0 0 18.458 20"
                            style="
                              margin-right: 10px;
                              margin-left: 8px;
                              width: 15px;
                            "
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
                          Delete
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
                <div v-else>
                  <div class="comment_header">
                    ({{ commentofsummary.length }}) Comment
                  </div>
                  <div class="line-no-comment-popup"></div>

                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="61"
                    height="51.516"
                    viewBox="0 0 61 51.516"
                    class="no_comment"
                  >
                    <g id="Exp-2.-F" transform="translate(-1 -5.742)">
                      <path
                        id="Path_19708"
                        data-name="Path 19708"
                        d="M27,9.677a1,1,0,0,1,1-1H50.554L62,21.37V37.323s.036,13.177-3.707,19.468a1,1,0,0,1-.842.464c-3.827,0-26.548,0-32.9,0a1,1,0,0,1-.882-1.47C26.773,49.051,27,37.323,27,37.323Z"
                        fill="#cadcf0"
                        fill-rule="evenodd"
                      />
                      <path
                        id="Path_19709"
                        data-name="Path 19709"
                        d="M62,37.323s.036,13.177-3.707,19.468a1,1,0,0,1-.842.464c-3.827,0-26.548,0-32.9,0a1,1,0,0,1-.882-1.47l.039-.086c12.314.579,30.2-1.578,32.551-2.5,2.516-.985,5.69-10.437,5.739-15.755Z"
                        fill="#a4bbdb"
                        fill-rule="evenodd"
                      />
                      <path
                        id="Path_19710"
                        data-name="Path 19710"
                        d="M62,21.37a18.063,18.063,0,0,0-9.549-.724,1,1,0,0,1-1.146-1.09,35.733,35.733,0,0,0-.751-10.879A41.33,41.33,0,0,1,62,21.37Z"
                        fill="#4675c1"
                        fill-rule="evenodd"
                      />
                      <path
                        id="Path_19711"
                        data-name="Path 19711"
                        d="M27,12.068a12.551,12.551,0,0,1,9.313,18.725l1.647,5.223a.721.721,0,0,1-.177.727l-.14.14a.678.678,0,0,1-.682.166l-5.249-1.655a12.5,12.5,0,0,1-4.728,1.58c.012-.331.016-.513.016-.513Z"
                        fill="#a4bbdb"
                        fill-rule="evenodd"
                      />
                      <path
                        id="Path_19712"
                        data-name="Path 19712"
                        d="M30.447,34.168a12,12,0,1,1,4.4-4.4l1.574,4.994a.687.687,0,0,1-.169.694l-.134.134a.648.648,0,0,1-.652.16Z"
                        fill="#e9f3fc"
                        fill-rule="evenodd"
                      />
                      <g id="Group_29520" data-name="Group 29520">
                        <path
                          id="Path_19713"
                          data-name="Path 19713"
                          d="M22.333,20.182a2.371,2.371,0,0,1,.888-2.057,2.18,2.18,0,0,1,2.868.18,2.127,2.127,0,0,1,.483,1.433,4.863,4.863,0,0,1-1.346,3.2,7.559,7.559,0,0,0-1.919,4.466,1,1,0,0,0,2,.109,5.7,5.7,0,0,1,1.516-3.367,6.7,6.7,0,0,0,1.752-4.427,4.074,4.074,0,0,0-.987-2.745,4.226,4.226,0,0,0-5.551-.462,4.3,4.3,0,0,0-1.7,3.714,1,1,0,0,0,2-.047Z"
                          fill="#4675c1"
                          fill-rule="evenodd"
                        />
                        <circle
                          id="Ellipse_1620"
                          data-name="Ellipse 1620"
                          cx="1.23"
                          cy="1.23"
                          r="1.23"
                          transform="translate(22.982 29.714)"
                          fill="#4675c1"
                        />
                        <path
                          id="Path_19714"
                          data-name="Path 19714"
                          d="M12.217,7.364l4,5.032a1,1,0,1,0,1.566-1.244l-4-5.032a1,1,0,0,0-1.566,1.244Z"
                          fill="#4675c1"
                          fill-rule="evenodd"
                        />
                        <path
                          id="Path_19715"
                          data-name="Path 19715"
                          d="M1.5,9.867l10.452,6a1,1,0,1,0,.995-1.734L2.5,8.133a1,1,0,0,0-1,1.734Z"
                          fill="#4675c1"
                          fill-rule="evenodd"
                        />
                        <path
                          id="Path_19716"
                          data-name="Path 19716"
                          d="M3,21h7a1,1,0,0,0,0-2H3a1,1,0,0,0,0,2Z"
                          fill="#4675c1"
                          fill-rule="evenodd"
                        />
                      </g>
                    </g>
                  </svg>

                  <p class="no-comment-text">No comments</p>
                </div>
              </div>

              <!-- Close Button -->
              <div class="close-button" @click="togglePopup">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="20.426"
                  height="20.423"
                  viewBox="0 0 13.426 13.423"
                >
                  <path
                    id="Icon_ionic-ios-close"
                    data-name="Icon ionic-ios-close"
                    d="M19.589,18l4.8-4.8A1.124,1.124,0,0,0,22.8,11.616l-4.8,4.8-4.8-4.8A1.124,1.124,0,1,0,11.616,13.2l4.8,4.8-4.8,4.8A1.124,1.124,0,0,0,13.2,24.384l4.8-4.8,4.8,4.8A1.124,1.124,0,1,0,24.384,22.8Z"
                    transform="translate(-11.285 -11.289)"
                  />
                </svg>
              </div>
            </div>
          </div>

          <p class="line-comment"></p>
          <form @submit.prevent="sendComment(index, summary.id)">

          <div class="box-comment">
            <input
              placeholder="แสดงความคิดเห็น ..."
              class="input-comment"
              v-model.trim="comments[index]"
              required
            />

            <button
              class="button-sendcomment"
            >
              <svg
                id="Send"
                xmlns="http://www.w3.org/2000/svg"
                width="25.756"
                height="25.69"
                viewBox="0 0 25.756 25.69"
              >
                <path
                  id="Fill-1"
                  d="M11.63,15.57l4.83,7.817a.663.663,0,0,0,.675.32.671.671,0,0,0,.571-.489l6.04-20.348a.686.686,0,0,0-.176-.692A.678.678,0,0,0,22.894,2L2.479,7.966a.7.7,0,0,0-.172,1.257l7.927,4.944,7-7.059A.99.99,0,0,1,18.647,8.5Zm5.4,10.121a2.639,2.639,0,0,1-2.259-1.267L9.653,16.135,1.257,10.9a2.67,2.67,0,0,1,.664-4.827L22.337.108A2.667,2.667,0,0,1,25.644,3.43L19.6,23.777A2.636,2.636,0,0,1,17.4,25.665a2.878,2.878,0,0,1-.366.025Z"
                  transform="translate(0 0)"
                  fill="#fff"
                  fill-rule="evenodd"
                />
              </svg>
            </button>
          </div>
          </form>
        </div>

        <div class="pagination">
          <ul class="flex items-center -space-x-px h-8 text-sm">
            <button
              class="flex items-center justify-center px-3 h-8 ms-0 leading-tight text-gray-500 bg-white border border-e-0 border-gray-300 rounded-s-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
              :class="{ 'opacity-50 cursor-not-allowed': currentPage === 1 }"
              @click="setPage(currentPage - 1)"
              :disabled="currentPage === 1"
            >
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
            <span
              class="flex items-center justify-center px-3 h-8 leading-tight text-sm text-gray-500 bg-white border border-gray-300 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400"
              >{{ currentPage }} of {{ totalPages }}</span
            >
            <button
              class="flex items-center justify-center px-3 h-8 leading-tight text-gray-500 bg-white border border-gray-300 rounded-e-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
              @click="setPage(currentPage + 1)"
              :class="{
                'opacity-50 cursor-not-allowed': currentPage === totalPages,
              }"
              :disabled="currentPage === totalPages"
            >
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
          </ul>
        </div>
      </div>

      <div v-else-if="activeTab === 'summary'" class="box no-data">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="50.493"
          height="59.146"
          viewBox="0 0 50.493 59.146"
          class="nosummary"
        >
          <g id="Exp-2.-F" transform="translate(-6.754 -2.383)">
            <path
              id="Path_19876"
              data-name="Path 19876"
              d="M52,35.617H12V54.129a2,2,0,0,0,1.425,1.916l18,5.4a2.01,2.01,0,0,0,1.15,0l18-5.4A2,2,0,0,0,52,54.129V35.617Z"
              fill="#cadcf0"
              fill-rule="evenodd"
            />
            <path
              id="Path_19877"
              data-name="Path 19877"
              d="M52,35.617H32s-.194,25.912,0,25.912a2.011,2.011,0,0,0,.575-.084l18-5.4A2,2,0,0,0,52,54.129V35.617Z"
              fill="#a4bbdb"
              fill-rule="evenodd"
            />
            <path
              id="Path_19878"
              data-name="Path 19878"
              d="M32,42.106,12,35.617,6.925,43.141a1,1,0,0,0,.52,1.511l18,5.846a1,1,0,0,0,1.137-.39Z"
              fill="#e9f3fc"
              fill-rule="evenodd"
            />
            <path
              id="Path_19879"
              data-name="Path 19879"
              d="M52,35.617,32,42.106l5.423,8a1,1,0,0,0,1.137.39l17.995-5.846a1,1,0,0,0,.52-1.511L52,35.617Z"
              fill="#e9f3fc"
              fill-rule="evenodd"
            />
            <path
              id="Path_19880"
              data-name="Path 19880"
              d="M27.159,21.986a1,1,0,0,0-1.136-.388l-18,5.847a1,1,0,0,0-.52,1.51L12,35.617l20-6.511Z"
              fill="#cadcf0"
              fill-rule="evenodd"
            />
            <path
              id="Path_19881"
              data-name="Path 19881"
              d="M56.493,28.955a1,1,0,0,0-.52-1.51l-18-5.847a1,1,0,0,0-1.136.388L32,29.106l20,6.511,4.493-6.662Z"
              fill="#e9f3fc"
              fill-rule="evenodd"
            />
            <path
              id="Path_19882"
              data-name="Path 19882"
              d="M52,35.617,32,29.106,12,35.617l20,6.489Z"
              fill="#cadcf0"
              fill-rule="evenodd"
            />
            <path
              id="Path_19883"
              data-name="Path 19883"
              d="M32,42.106v-13L12,35.617Z"
              fill="#a4bbdb"
              fill-rule="evenodd"
            />
            <g id="Group_29532" data-name="Group 29532">
              <path
                id="Path_19884"
                data-name="Path 19884"
                d="M27.982,31.978a8.757,8.757,0,0,1-1.124-.868,1,1,0,1,0-1.352,1.473,10.672,10.672,0,0,0,1.382,1.069,1,1,0,0,0,1.094-1.674Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
              <path
                id="Path_19885"
                data-name="Path 19885"
                d="M24.866,28.906a8.689,8.689,0,0,1-.754-1.213,1,1,0,1,0-1.783.907,10.74,10.74,0,0,0,.927,1.492,1,1,0,1,0,1.61-1.186Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
              <path
                id="Path_19886"
                data-name="Path 19886"
                d="M23.4,24.825a4.628,4.628,0,0,1,.177-1.169,1,1,0,0,0-1.923-.551,6.71,6.71,0,0,0-.254,1.67,1,1,0,0,0,2,.05Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
              <path
                id="Path_19887"
                data-name="Path 19887"
                d="M24.942,21.485a5.908,5.908,0,0,1,.86-.657,1,1,0,1,0-1.067-1.692,7.855,7.855,0,0,0-1.152.882,1,1,0,0,0,1.359,1.467Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
              <path
                id="Path_19888"
                data-name="Path 19888"
                d="M28.661,19.76a11.338,11.338,0,0,1,1.6-.176,1,1,0,0,0-.085-2,13.5,13.5,0,0,0-1.877.208,1,1,0,0,0,.364,1.967Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
              <path
                id="Path_19889"
                data-name="Path 19889"
                d="M33.873,19.7c.853.01,1.647-.02,2.384-.085a1,1,0,1,0-.177-1.992c-.675.059-1.4.087-2.184.077a1,1,0,0,0-.023,2Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
              <path
                id="Path_19890"
                data-name="Path 19890"
                d="M39.544,19a10.307,10.307,0,0,0,2.526-1.136,1,1,0,1,0-1.075-1.687,8.3,8.3,0,0,1-2.036.91A1,1,0,0,0,39.544,19Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
              <path
                id="Path_19891"
                data-name="Path 19891"
                d="M44.634,15.068a6.21,6.21,0,0,0,.607-3,1,1,0,0,0-2,.093,4.215,4.215,0,0,1-.41,2.04,1,1,0,1,0,1.8.87Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
              <path
                id="Path_19892"
                data-name="Path 19892"
                d="M44,8.529a4.738,4.738,0,0,0-2.8-1.892,1,1,0,0,0-.4,1.96A2.737,2.737,0,0,1,42.39,9.72,1,1,0,1,0,44,8.529Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
            </g>
            <path
              id="Path_19893"
              data-name="Path 19893"
              d="M30.933,6.831c1.082-6.127,10.459-5.731,5,0Z"
              fill="#cadcf0"
              fill-rule="evenodd"
            />
            <path
              id="Path_19894"
              data-name="Path 19894"
              d="M30.933,8.383c1.082,6.126,10.459,5.731,5,0Z"
              fill="#cadcf0"
              fill-rule="evenodd"
            />
            <path
              id="Path_19895"
              data-name="Path 19895"
              d="M30.843,8.617h6.7a1,1,0,0,0,0-2h-6.7a1,1,0,1,0,0,2Z"
              fill="#4675c1"
              fill-rule="evenodd"
            />
          </g>
        </svg>
        <span>No Summary</span>
      </div>

      <!-- ส่วนของกล่อง review -->

      <div class="box" v-if="activeTab === 'review' && reviews.length > 0">
        <div
          class="review-box"
          v-for="(review, index) in paginatedItems"
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
                  params: { reviewid: review.id },
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
                @click="() => deleteReview(review.id, index)"
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

            <span
              class="course_subject_review"
              :style="{
                backgroundColor: getCategoryColor(review.categoryName),
              }"
              v-if="review.categoryName === 'INT'"
              >{{ review.courseName }} {{ review.courseFullName }}</span
            >
            <span
              class="course_subject_review"
              :style="{
                backgroundColor: getCategoryColor(review.categoryName),
              }"
              v-if="review.categoryName === 'GEN'"
              >{{ review.courseName }} {{ review.courseFullName }}</span
            >
            <span
              class="course_subject_review"
              :style="{
                backgroundColor: getCategoryColor(review.categoryName),
              }"
              v-if="review.categoryName === 'SSC'"
              >{{ review.courseName }} {{ review.courseFullName }}</span
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
                  moment(review.fileCreatedOn)
                    .locale("th")
                    .format("DD MMMM YYYY")
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
                <div class="box_star">
                  ⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐
                </div>
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
                <div class="box_star">
                  ⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐
                </div>
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
                <div class="box_star">
                  ⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐
                </div>
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
                <div class="box_star">
                  ⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐
                </div>
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
                <div class="box_star">
                  ⭐&nbsp&nbsp⭐&nbsp&nbsp⭐&nbsp&nbsp⭐
                </div>
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

        <div class="pagination">
          <ul class="flex items-center -space-x-px h-8 text-sm">
            <button
              class="flex items-center justify-center px-3 h-8 ms-0 leading-tight text-gray-500 bg-white border border-e-0 border-gray-300 rounded-s-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
              :class="{ 'opacity-50 cursor-not-allowed': currentPage === 1 }"
              @click="setPage(currentPage - 1)"
              :disabled="currentPage === 1"
            >
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
            <span
              class="flex items-center justify-center px-3 h-8 leading-tight text-sm text-gray-500 bg-white border border-gray-300 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400"
              >{{ currentPage }} of {{ totalPages }}</span
            >
            <button
              class="flex items-center justify-center px-3 h-8 leading-tight text-gray-500 bg-white border border-gray-300 rounded-e-lg hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white"
              @click="setPage(currentPage + 1)"
              :class="{
                'opacity-50 cursor-not-allowed': currentPage === totalPages,
              }"
              :disabled="currentPage === totalPages"
            >
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
          </ul>
        </div>
      </div>

      <div v-else-if="activeTab === 'review'" class="box no-data">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="50.493"
          height="59.146"
          viewBox="0 0 50.493 59.146"
          class="nosummary"
        >
          <g id="Exp-2.-F" transform="translate(-6.754 -2.383)">
            <path
              id="Path_19876"
              data-name="Path 19876"
              d="M52,35.617H12V54.129a2,2,0,0,0,1.425,1.916l18,5.4a2.01,2.01,0,0,0,1.15,0l18-5.4A2,2,0,0,0,52,54.129V35.617Z"
              fill="#cadcf0"
              fill-rule="evenodd"
            />
            <path
              id="Path_19877"
              data-name="Path 19877"
              d="M52,35.617H32s-.194,25.912,0,25.912a2.011,2.011,0,0,0,.575-.084l18-5.4A2,2,0,0,0,52,54.129V35.617Z"
              fill="#a4bbdb"
              fill-rule="evenodd"
            />
            <path
              id="Path_19878"
              data-name="Path 19878"
              d="M32,42.106,12,35.617,6.925,43.141a1,1,0,0,0,.52,1.511l18,5.846a1,1,0,0,0,1.137-.39Z"
              fill="#e9f3fc"
              fill-rule="evenodd"
            />
            <path
              id="Path_19879"
              data-name="Path 19879"
              d="M52,35.617,32,42.106l5.423,8a1,1,0,0,0,1.137.39l17.995-5.846a1,1,0,0,0,.52-1.511L52,35.617Z"
              fill="#e9f3fc"
              fill-rule="evenodd"
            />
            <path
              id="Path_19880"
              data-name="Path 19880"
              d="M27.159,21.986a1,1,0,0,0-1.136-.388l-18,5.847a1,1,0,0,0-.52,1.51L12,35.617l20-6.511Z"
              fill="#cadcf0"
              fill-rule="evenodd"
            />
            <path
              id="Path_19881"
              data-name="Path 19881"
              d="M56.493,28.955a1,1,0,0,0-.52-1.51l-18-5.847a1,1,0,0,0-1.136.388L32,29.106l20,6.511,4.493-6.662Z"
              fill="#e9f3fc"
              fill-rule="evenodd"
            />
            <path
              id="Path_19882"
              data-name="Path 19882"
              d="M52,35.617,32,29.106,12,35.617l20,6.489Z"
              fill="#cadcf0"
              fill-rule="evenodd"
            />
            <path
              id="Path_19883"
              data-name="Path 19883"
              d="M32,42.106v-13L12,35.617Z"
              fill="#a4bbdb"
              fill-rule="evenodd"
            />
            <g id="Group_29532" data-name="Group 29532">
              <path
                id="Path_19884"
                data-name="Path 19884"
                d="M27.982,31.978a8.757,8.757,0,0,1-1.124-.868,1,1,0,1,0-1.352,1.473,10.672,10.672,0,0,0,1.382,1.069,1,1,0,0,0,1.094-1.674Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
              <path
                id="Path_19885"
                data-name="Path 19885"
                d="M24.866,28.906a8.689,8.689,0,0,1-.754-1.213,1,1,0,1,0-1.783.907,10.74,10.74,0,0,0,.927,1.492,1,1,0,1,0,1.61-1.186Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
              <path
                id="Path_19886"
                data-name="Path 19886"
                d="M23.4,24.825a4.628,4.628,0,0,1,.177-1.169,1,1,0,0,0-1.923-.551,6.71,6.71,0,0,0-.254,1.67,1,1,0,0,0,2,.05Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
              <path
                id="Path_19887"
                data-name="Path 19887"
                d="M24.942,21.485a5.908,5.908,0,0,1,.86-.657,1,1,0,1,0-1.067-1.692,7.855,7.855,0,0,0-1.152.882,1,1,0,0,0,1.359,1.467Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
              <path
                id="Path_19888"
                data-name="Path 19888"
                d="M28.661,19.76a11.338,11.338,0,0,1,1.6-.176,1,1,0,0,0-.085-2,13.5,13.5,0,0,0-1.877.208,1,1,0,0,0,.364,1.967Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
              <path
                id="Path_19889"
                data-name="Path 19889"
                d="M33.873,19.7c.853.01,1.647-.02,2.384-.085a1,1,0,1,0-.177-1.992c-.675.059-1.4.087-2.184.077a1,1,0,0,0-.023,2Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
              <path
                id="Path_19890"
                data-name="Path 19890"
                d="M39.544,19a10.307,10.307,0,0,0,2.526-1.136,1,1,0,1,0-1.075-1.687,8.3,8.3,0,0,1-2.036.91A1,1,0,0,0,39.544,19Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
              <path
                id="Path_19891"
                data-name="Path 19891"
                d="M44.634,15.068a6.21,6.21,0,0,0,.607-3,1,1,0,0,0-2,.093,4.215,4.215,0,0,1-.41,2.04,1,1,0,1,0,1.8.87Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
              <path
                id="Path_19892"
                data-name="Path 19892"
                d="M44,8.529a4.738,4.738,0,0,0-2.8-1.892,1,1,0,0,0-.4,1.96A2.737,2.737,0,0,1,42.39,9.72,1,1,0,1,0,44,8.529Z"
                fill="#4675c1"
                fill-rule="evenodd"
              />
            </g>
            <path
              id="Path_19893"
              data-name="Path 19893"
              d="M30.933,6.831c1.082-6.127,10.459-5.731,5,0Z"
              fill="#cadcf0"
              fill-rule="evenodd"
            />
            <path
              id="Path_19894"
              data-name="Path 19894"
              d="M30.933,8.383c1.082,6.126,10.459,5.731,5,0Z"
              fill="#cadcf0"
              fill-rule="evenodd"
            />
            <path
              id="Path_19895"
              data-name="Path 19895"
              d="M30.843,8.617h6.7a1,1,0,0,0,0-2h-6.7a1,1,0,1,0,0,2Z"
              fill="#4675c1"
              fill-rule="evenodd"
            />
          </g>
        </svg>
        No review
      </div>
    </div>
  </div>
</template>

<style scoped>
.dropdown {
  float: right;
  position: relative;
}

.info {
  width: 40px;
  margin-top: -10px;
  margin-right: 0px;
  margin-left: 40%;
  z-index: 2;
}

.info:hover {
  opacity: 0.5;
}

.all {
  position: absolute;
  margin-left: 10px;
  margin-top: 10px;
}

.tab {
  color: #000000;
  font-size: 16px;
  padding-left: 20px;
  padding-right: 20px;
  /* margin-bottom: 10px; */
  /* margin-top: 15px; */
  /* margin-left: 5px; */
  margin-right: 12px;
  cursor: pointer;
  border: none;
  border-radius: 15px 15px 0px 0px;
  height: 50px;
  position: relative;
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

.review-box {
  position: relative;
  width: 1188px;
  /* height: 328px; */
  background: #fafafa 0% 0% no-repeat padding-box;
  box-shadow: 0px 0px 5px #efefef;
  border: 1px solid #efefef;
  border-radius: 20px;
  opacity: 1;
  /* margin-top: 10px; */
  margin-bottom: 30px;
  padding: 20px;
  white-space: pre-line; /* ทำให้ข้อความขึ้นบรรทัดใหม่ตามตำแหน่งของ "\n" */
  word-wrap: break-word; /* ทำให้ข้อความขึ้นบรรทัดใหม่ตามขอบกล่อง */
  overflow: hidden;
}

.box {
  position: relative;
  width: 1255px;
  /* height: 849px; */
  /* margin-top: 25px; */
  margin-left: auto;
  margin-right: auto;
  margin-bottom: 60px;
  padding-bottom: 30px;
  padding-top: 40px;
  background: #ffffff 0% 0% no-repeat padding-box;
  box-shadow: 0px 8px 15px #457aef0d;
  border-radius: 0px 20px 20px 20px;
  opacity: 1;

  display: flex;
  flex-direction: column;
  align-items: center;
}

.count {
  margin-left: 5px;
  padding: 4px 10px 4px 10px;
  border-radius: 8px;
  background-color: #4675c0;
  color: white;
  font-size: 13px;
  font-weight: 400;
}

.inactive {
  background-color: #b8bfd6;
  opacity: 0.5;
}

#profile {
  width: 70px;
  height: 70px;
  margin-left: 10px;
  margin-top: 5px;
}

#profile_comment {
  width: 70px;
  height: 70px;
  float: left;
  margin: 15px;
}

.title_summary {
  width: auto;
  height: 28px;
  color: var(--unnamed-color-4675c0);
  text-align: left;
  /* font: normal normal 600 20px/30px Poppins; */
  letter-spacing: 0.8px;
  color: #4675c0;
  opacity: 1;
  margin-top: -70px;
  margin-bottom: 5px;
  font-size: 20px;
  font-weight: 600;
}

.filedescription {
  color: var(--unnamed-color-697a98);
  text-align: left;
  font: normal normal light 14px Poppins;
  font-weight: 400;
  font-size: 14px;
  letter-spacing: 0px;
  color: #697a98;
  opacity: 1;
  margin-left: 15px;
  margin-right: 30px;
  margin-bottom: 18px;
  margin-top: 20px;
}

.filedownload {
  background: #ffffff 0% 0% no-repeat padding-box;
  box-shadow: 0px 0px 15px #457aef0d;
  border: 1px solid #4675c158;
  border-radius: 10px;
  opacity: 1;
  width: 280px;
  height: 50px;
  padding-top: 10px;
  padding-bottom: 10px;
  margin-left: 15px;
}

.box-title {
  margin-top: -75px;
  margin-left: 105px;
}

.username {
  color: var(--unnamed-color-697a98);
  text-align: left;
  font: normal normal medium 12px/18px Poppins;
  letter-spacing: 0.48px;
  font-size: 12px;
  font-weight: 500;
  color: #697a98;
  opacity: 1;
  margin-left: -8px;
  display: inline-block;
  margin-top: 10px;
}

.username-comment {
  color: var(--unnamed-color-697a98);
  text-align: left;
  font: normal normal medium 12px/18px Poppins;
  letter-spacing: 0.48px;
  font-size: 12px;
  font-weight: 500;
  color: #697a98;
  opacity: 1;
  display: inline-block;
  margin-top: 20px;
  margin-left: -48%;
  padding-left: -70%;
}

.dot {
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

.user {
  width: 12px;
  height: 12px;
  opacity: 1;
  display: flex inline;
  z-index: 1;
}

.user_comment {
  width: 12px;
  height: 12px;
  opacity: 1;
  display: flex inline;
  z-index: 1;
  float: left;
  margin-top: 23px;
  margin-left: 10px;
  margin-right: 20px;
}

.line-comment {
  background-color: #e6e6e6;
  height: 1px;
  width: 95%;
  position: absolute;
  left: 50%;
  right: 50%;
  margin-top: 20px;
  transform: translate(-50%, -50%);
}

.input-comment {
  margin-left: 15px;
  margin-top: 40px;
  padding-left: 15px;
  width: 100%;
  height: 50px;
  background: #ffffff 0% 0% no-repeat padding-box;
  border: 1px solid #4675c126;
  border-radius: 8px;
  opacity: 1;
  margin-bottom: 5px;
}

.input-comment::placeholder {
  font-size: 14px;
  font-weight: 200;
  opacity: 0.5;
}

.input-comment:valid {
  font-size: 14px;
  font-weight: 500;
}

.button-sendcomment {
  background-color: #4675c0;
  height: 50px;
  width: 65px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 300;
  letter-spacing: 0.56px;
  color: white;
  /* margin-right: 20px; */
  margin-left: 15px;
  margin-top: 40px;
  display: inline-block;
}

.button-sendcomment svg {
  margin-left: 15px;
}

.box-comment {
  display: flex;
}

.course_subject {
  background-color: #4675c0;
  font-size: 12px;
  font-weight: 100;
  border-radius: 6px;
  padding-left: 15px;
  padding-right: 15px;
  padding-top: 3px;
  padding-bottom: 3px;
  margin-top: 5px;
  letter-spacing: 0.48px;
  color: white;
}

.course_subject_review {
  background-color: #4675c0;
  font-size: 12px;
  font-weight: 100;
  border-radius: 6px;
  padding-left: 15px;
  padding-right: 15px;
  padding-top: 3px;
  padding-bottom: 3px;
  margin-left: 95px;
  letter-spacing: 0.48px;
  color: white;
}

.dropdown-click {
  position: relative;
  top: 30px;
  margin-right: -15px;
  z-index: 1;
}

.nosummary {
  width: 15%;
  height: 15%;
  padding-bottom: 20px;
  padding-top: 30px;
}

.no-data {
  color: #b8bfd6;
  font: normal normal 400 18px/22px Poppins;
  letter-spacing: 0.28px;
  padding-bottom: 80px;
}

.box-comment {
  display: flex;
}

.count_comment span {
  letter-spacing: 0.8px;
  color: #4675c0;
  font-weight: 500;
  padding-left: 7px;
}

.count_comment {
  float: right;
  margin-top: 20px;
  margin-right: 10px;
}

.popup-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.popup {
  padding: 30px;
  position: relative;
  width: 652px;
  height: 600px;
  padding-bottom: 50px;
  background: #ffffff 0% 0% no-repeat padding-box;
  box-shadow: 0px 0px 15px #457aef0d;
  border-radius: 20px;
  opacity: 1;
  margin-bottom: 30px;
  bottom: 30px;
  overflow: auto;
  top: 2%;
}

.popup-content {
  text-align: center;
}

.detail-comment {
  display: block;
  margin: auto;
  position: relative;
  width: 554px;
  height: auto;
  background: #f7f7fa 0% 0% no-repeat padding-box;
  border: 1px solid #ededed;
  border-radius: 20px;
  opacity: 1;
  margin-bottom: 25px;
  top: 50px;
}

.edit-comment {
  height: 40px;
  width: 100px;
  /* border-radius: 10px; */
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.56px;
  color: #19335a;
  margin-right: 70px;
  margin-left: 60px;
}

.edit-comment:hover {
  opacity: 0.5;
}

.delete-comment {
  height: 40px;
  width: 100px;
  /* border-radius: 10px; */
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.56px;
  color: #19335a;
  margin-right: 55px;
  margin-left: 90px;
}

.delete-comment:hover {
  opacity: 0.5;
}

.save-comment {
  height: 40px;
  width: 100px;
  /* border-radius: 10px; */
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.56px;
  color: #19335a;
  margin-right: 55px;
  margin-left: 90px;
}

.save-comment:hover {
  opacity: 0.5;
}

.cancle-comment {
  height: 40px;
  width: 100px;
  /* border-radius: 10px; */
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.56px;
  color: #19335a;
  margin-right: 55px;
  margin-left: 110px;
}

.cancle-comment:hover {
  opacity: 0.5;
}

.line-edit-delete {
  background-color: #ffffff;
  height: 30px;
  width: 2px;
}

.button-edit-delete {
  width: 554px;
  height: 46px;
  background: #ececf2 0% 0% no-repeat padding-box;
  border-radius: 0px 0px 20px 20px;
  opacity: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.detail-comment-box {
  /* float: left; */
  color: var(--unnamed-color-697a98);
  text-align: left;
  font-weight: 400;
  font-size: 14px;
  letter-spacing: 0px;
  color: #697a98;
  opacity: 1;
  margin-left: 20%;
  margin-right: 30px;
  margin-bottom: 18px;
  margin-top: 5px;
}

.edit-detail-comment-box {
  color: var(--unnamed-color-697a98);
  text-align: left;
  font-weight: 400;
  font-size: 14px;
  letter-spacing: 0px;
  color: #697a98;
  opacity: 1;
  margin-right: 30px;
  margin-bottom: 18px;
  margin-top: 20px;
  width: 70%;
  height: 35px;
  padding: 10px;
  border-radius: 8px;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  cursor: pointer;
  color: #333;
  padding: 20px;
}

.close-button:hover {
  opacity: 0.5;
}

.comment_header {
  color: var(--unnamed-color-4675c0);
  text-align: left;
  /* font: normal normal 600 20px/30px Poppins; */
  letter-spacing: 0.8px;
  color: #4675c0;
  opacity: 1;
  font-size: 20px;
  font-weight: 600;
  text-align: center;
  font: normal normal 600 28px/42px Poppins;
  letter-spacing: 1.12px;
  color: #4675c0;
}

.line-comment-popup {
  background-color: #e6e6e6;
  height: 1px;
  width: 551px;
  position: absolute;
  left: 50%;
  right: 50%;
  margin-top: 20px;
  transform: translate(-50%, -50%);
}

.line-no-comment-popup {
  background-color: #e6e6e6;
  height: 1px;
  width: 551px;
  position: absolute;
  left: 50%;
  right: 50%;
  margin-top: 20px;
  transform: translate(-50%, -50%);
}

.toast-container {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1000;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.no_comment {
  width: 40%;
  height: 90%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-left: 25%;
  margin-top: 120px;
}

.no-comment-text {
  color: #b8bfd6;
  font: normal normal 400 18px/22px Poppins;
  letter-spacing: 0.28px;
  padding-bottom: 80px;
  margin-top: 30px;
}

.tooltip {
  position: relative;
  display: inline-block;
}

.tooltip-text {
  visibility: hidden;
  width: 160px;
  background-color: #000000;
  color: #fff;
  font: normal normal 400 18px/22px Poppins;
  letter-spacing: 0.28px;
  font-size: 11px;
  text-align: center;
  border-radius: 6px;
  padding: 3px;
  position: absolute;
  z-index: 1;
  bottom: 125%;
  left: 50%;
  margin-left: -85px;
  opacity: 0;
  transition: opacity 0.3s, visibility 0.3s;
}

.tooltip-text:after {
  content: "";
  position: absolute;
  bottom: -50%;
  left: 50%;
  margin-left: -8px;
  border-width: 8px;
  border-style: solid;
  border-color: #000000 transparent transparent transparent;
}

.tooltip:hover .tooltip-text {
  visibility: visible;
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
  padding-bottom: 30px;
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
  font-size: 14px;
  font-weight: 300;
  letter-spacing: 0.56px;
  opacity: 1;
  margin-bottom: 20px;
  margin-left: 20px;
  margin-top: 80px;
  color: #b8bfd6;
  width: 97%;
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
  margin-bottom: 0px;
  margin-left: 20px;
  margin-top: 20px;
  width: 97%;
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
  margin-left: 9px;
  margin-right: 8px;
  margin-top: 30px;
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
  margin-top: 60px;
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
  margin-left: 10px;
  margin-top: 10px;
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
  margin-left: 95px;
  margin-bottom: 8px;
  margin-top: -5px;
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
  margin-left: 95px;
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
  margin-left: 100px;
  margin-top: 10px;
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
