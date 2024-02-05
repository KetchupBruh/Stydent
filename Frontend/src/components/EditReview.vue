<script setup>
import { ref, onBeforeMount, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import BackNavbar from "./BackNavbar.vue";
import ToastSuccess from "../components/ToastSuccess.vue";
import ToastError from "../components/ToastError.vue";
const params = useRoute().params;

const showSuccess = ref(false);
const showError = ref(false);
const errorMessage = ref("Edit Review Failed");
const successMessage = ref("Edit Review Success");

const hideError = () => {
  showError.value = false;
};

const originalReviewData = ref([]);
const courses = ref([]);
const reviewData = ref({
  gradesReceived: "",
  instructorName: "",
  ratingOfInteresting: "",
  ratingOfGroupWork: "",
  ratingOfGradeCollect: "",
  ratingOfEasyExam: "",
  ratingOfIndividualWork: "",
  work: "",
  reviewDescription: "",
  courseIdcourse: "",
});

const hasClickedEditButton = ref(false);

const maxInstructorNameLength = 100;
const maxWorkLength = 500;
const maxReviewDescriptionLength = 1000;

const isInvalidInstructorName = computed(() => {
  return reviewData.value.instructorName.length > maxInstructorNameLength;
});

const isInvalidWork = computed(() => {
  return reviewData.value.work.length > maxWorkLength;
});

const isInvalidReviewDescription = computed(() => {
  return reviewData.value.reviewDescription.length > maxReviewDescriptionLength;
});

onBeforeMount(async () => {
  const reviewResponse = await fetch(
    `${import.meta.env.VITE_BASE_URL}review/${params.reviewid}`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + localStorage.getItem("token"),
      },
    }
  );

  if (reviewResponse.status === 200) {
    const data = await reviewResponse.json();
    reviewData.value = { ...data };
    originalReviewData.value = { ...data };

    console.log(reviewData.value);
  } else {
    console.error("Failed to fetch review data");
  }

  const courseResponse = await fetch(
    `${import.meta.env.VITE_BASE_URL}course/`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    }
  );

  if (courseResponse.status === 200) {
    console.log("You have successfully logged in");

    const courseData = await courseResponse.json();
    courses.value = courseData;
  } else {
    console.error("Login failed");
  }
});

const EditReview = async () => {
  hasClickedEditButton.value = true;

  // Validate before submitting the edit
  if (!isInputValid()) {
    return;
  }

  const response = await fetch(
    `${import.meta.env.VITE_BASE_URL}review/${params.reviewid}`,
    {
      method: "PUT",
      headers: {
        "content-type": "application/json",
        Authorization: "Bearer " + localStorage.getItem("token"),
      },
      body: JSON.stringify(reviewData.value),
    }
  );
  if (response.status === 200) {
    console.log("Edit Success");
    originalReviewData.value = { ...reviewData.value }; // Update original data
    showSuccess.value = true;

    setTimeout(function () {
      Review();
    }, 1500);
  } else {
    console.error("Edit failed");
    showError.value = true;
  }
};

const isInputValid = () => {
  return (
    !isInvalidInstructorName.value &&
    !isInvalidWork.value &&
    !isInvalidReviewDescription.value
  );
};

const isUnchanged = computed(() => {
  return (
    JSON.stringify(reviewData.value) ===
    JSON.stringify(originalReviewData.value)
  );
});

const isFormEmpty = computed(() => {
  return (
    !reviewData.value.gradesReceived ||
    !reviewData.value.instructorName ||
    !reviewData.value.ratingOfInteresting ||
    !reviewData.value.ratingOfGroupWork ||
    !reviewData.value.ratingOfGradeCollect ||
    !reviewData.value.ratingOfEasyExam ||
    !reviewData.value.ratingOfIndividualWork ||
    !reviewData.value.work ||
    !reviewData.value.reviewDescription
  );
});

const clearForm = () => {
  reviewData.value.gradesReceived = "";
  reviewData.value.instructorName = "";
  reviewData.value.ratingOfInteresting = "";
  reviewData.value.ratingOfGroupWork = "";
  reviewData.value.ratingOfGradeCollect = "";
  reviewData.value.ratingOfEasyExam = "";
  reviewData.value.ratingOfIndividualWork = "";
  reviewData.value.work = "";
  reviewData.value.reviewDescription = "";
};

const appRouter = useRouter();
// const Review = () => appRouter.push({ name: "Review" });
const Review = () => appRouter.go(-1);
</script>

<template>
  <div class="container mx-auto mt-18">
    <div class="toast-container">
      <ToastError
        :showError="showError"
        :errorMessage="errorMessage"
        @hide-error="hideError"
      />
      <ToastSuccess
        :showSuccess="showSuccess"
        :successMessage="successMessage"
      />
    </div>

    <!-- ส่วนของ breadcrumb -->
    <div class="navigation text-gray-600 font-light text-sm mt-8 my-6">
      <span style="color: #b8bfd6"
        >Edit Review &nbsp; > &nbsp; {{ reviewData.courseName }}
        {{ reviewData.courseFullName }}&nbsp;
        <span style="color: #4675c1"> > &nbsp; {{ params.reviewid }} </span>
      </span>
    </div>

    <div class="add-boxs">
      <div class="flex-container">
        <div class="line-review"></div>
        <p class="review">Edit Review</p>
      </div>

      <form @submit.prevent="EditReview" class="form-container">
        <div class="input-group">
          <label for="gradesReceived" class="lable">Grades Received</label
          ><br />
          <select v-model="reviewData.gradesReceived" required class="dropdown">
            <option value="A">A</option>
            <option value="B+">B+</option>
            <option value="B">B</option>
            <option value="C+">C+</option>
            <option value="C">C</option>
            <option value="D+">D+</option>
            <option value="D">D</option>
            <option value="F">F</option>
          </select>
        </div>

        <div class="input-group">
          <label for="instructorName" class="lable">Instructor Name</label
          ><span
            v-if="hasClickedEditButton && isInvalidInstructorName"
            class="error-tooltip"
          >
            * Maximum 100 characters allowed
          </span>
          <br />

          <input
            v-model.trim="reviewData.instructorName"
            type="text"
            required
            class="dropdown"
            :style="{
              border:
                isInvalidInstructorName && hasClickedEditButton
                  ? '1px solid red'
                  : '',
            }"
          />
        </div>

        <div class="input-group">
          <label for="work" class="lable">Assignment</label
          ><span
            v-if="hasClickedEditButton && isInvalidWork"
            class="error-tooltip"
          >
            * Maximum 500 characters allowed </span
          ><br />
          <input
            v-model.trim="reviewData.work"
            required
            class="dropdown"
            :style="{
              border:
                isInvalidWork && hasClickedEditButton ? '1px solid red' : '',
            }"
          />
        </div>

        <div class="input-group">
          <label for="reviewDescription" class="lable">Review Description</label
          ><span
            v-if="hasClickedEditButton && isInvalidReviewDescription"
            class="error-tooltip"
          >
            * Maximum 1000 characters allowed </span
          ><br />
          <input
            v-model.trim="reviewData.reviewDescription"
            required
            class="dropdown"
            :style="{
              border:
                isInvalidReviewDescription && hasClickedEditButton
                  ? '1px solid red'
                  : '',
            }"
          />
        </div>

        <div class="input-group">
          <label for="rating" class="lable"
            >Rating Of Interesting ( เนื้อหามีความน่าสนใจ )</label
          ><br />
          <select
            v-model="reviewData.ratingOfInteresting"
            required
            class="dropdown"
          >
            <option value="1">⭐</option>
            <option value="2">⭐⭐</option>
            <option value="3">⭐⭐⭐</option>
            <option value="4">⭐⭐⭐⭐</option>
            <option value="5">⭐⭐⭐⭐⭐</option>
          </select>
        </div>

        <div class="input-group">
          <label for="rating" class="lable"
            >Working As Individual Work ( เน้นการทำงานเดี่ยว )</label
          ><br />
          <select
            v-model="reviewData.ratingOfIndividualWork"
            required
            class="dropdown"
          >
            <option value="1">⭐</option>
            <option value="2">⭐⭐</option>
            <option value="3">⭐⭐⭐</option>
            <option value="4">⭐⭐⭐⭐</option>
            <option value="5">⭐⭐⭐⭐⭐</option>
          </select>
        </div>

        <div class="input-group">
          <label for="rating" class="lable"
            >Rating As Group Work ( เน้นการทำงานกลุ่ม )</label
          ><br />
          <select
            v-model="reviewData.ratingOfGroupWork"
            required
            class="dropdown"
          >
            <option value="1">⭐</option>
            <option value="2">⭐⭐</option>
            <option value="3">⭐⭐⭐</option>
            <option value="4">⭐⭐⭐⭐</option>
            <option value="5">⭐⭐⭐⭐⭐</option>
          </select>
        </div>

        <div class="input-group">
          <label for="rating" class="lable"
            >Grade Collection Subjects ( วิชาเก็บเกรด )</label
          ><br />
          <select
            v-model="reviewData.ratingOfGradeCollect"
            required
            class="dropdown"
          >
            <option value="1">⭐</option>
            <option value="2">⭐⭐</option>
            <option value="3">⭐⭐⭐</option>
            <option value="4">⭐⭐⭐⭐</option>
            <option value="5">⭐⭐⭐⭐⭐</option>
          </select>
        </div>

        <div class="input-group">
          <label for="rating" class="lable">Easy To Exam ( ข้อสอบไม่ยาก )</label
          ><br />
          <select
            v-model="reviewData.ratingOfEasyExam"
            required
            class="dropdown"
          >
            <option value="1">⭐</option>
            <option value="2">⭐⭐</option>
            <option value="3">⭐⭐⭐</option>
            <option value="4">⭐⭐⭐⭐</option>
            <option value="5">⭐⭐⭐⭐⭐</option>
          </select>
        </div>

        <div class="edit-container flex items-center">
          <button type="submit" class="editReview" :disabled="isUnchanged || isFormEmpty">
            Edit Review</button
          >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

          <button
            type="button"
            class="button-clear"
            @click="clearForm"
            :disabled="
              !reviewData.gradesReceived &&
              !reviewData.instructorName &&
              !reviewData.ratingOfInteresting &&
              !reviewData.ratingOfGroupWork &&
              !reviewData.ratingOfGradeCollect &&
              !reviewData.ratingOfEasyExam &&
              !reviewData.ratingOfIndividualWork &&
              !reviewData.work &&
              !reviewData.reviewDescription
            "
          >
            Clear
          </button>
        </div>
      </form>

      <BackNavbar :backToPage="{ name: 'Review' }" />
    </div>
  </div>
</template>

<style scoped>
.navigation {
  margin-top: 35px;
  /* margin-left: 10px; */
  font: normal normal 400 14px/22px Poppins;
  letter-spacing: 0.28px;
  margin-left: 190px;
}

.dropdown[style*="border: 1px solid red"] {
  border: 1px solid red !important;
}

button {
  background-color: #4675c1;
  color: #fff;
  padding: 10px;
  cursor: pointer;
  border: none;
  border-radius: 5px;
  display: flex;
  justify-content: center;
}

.editReview {
  background-color: #4675c0;
  height: 40px;
  width: 120px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 300;
  letter-spacing: 0.56px;
  color: white;
  /* margin-top: 300px; */
}

.edit-container {
  position: relative;
  /* left: -45%; */
  transform: translate(-50%, -50%);
  /* width: 120px;  */
  margin-top: 4%;
  margin-left: 50%;
}

.button-clear {
  position: relative;
  background-color: white;
  border: solid 1px #4675c0;
  height: 40px;
  width: 120px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 300;
  letter-spacing: 0.56px;
  color: #4675c0;
  /* margin-left: 52%; */
  /* margin-top: -210px; */
}

.button-clear:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.editReview:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.error-tooltip {
  color: red;
  font-size: 12px;
  margin-left: 45px;
}

.add-boxs {
  display: flex;
  background-color: #4675c1;
  width: 900px;
  height: auto;
  background: #ffffff 0% 0% no-repeat padding-box;
  box-shadow: 0px 0px 15px #457aef0d;
  border-radius: 20px;
  opacity: 1;
  margin: auto;
  flex-direction: column;
  align-items: flex-start;
  padding-bottom: 15px;
  margin-bottom: 10%;
}

.line-review {
  position: absolute;
  width: 8px;
  height: 39px;
  border-radius: 15px;
  margin-left: 45px;
  margin-top: 30px;
  background-color: #4675c0;
}

.review {
  color: var(--unnamed-color-19335a);
  text-align: left;
  font: normal normal 600 26px/39px Poppins;
  letter-spacing: 1.04px;
  color: #19335a;
  opacity: 1;
  margin-top: 30px;
  margin-left: 70px;
  margin-bottom: 20px;
}

.lable {
  letter-spacing: 0.28px;
  color: #697a98;
  opacity: 1;
  font-size: 14px;
  font-weight: 400;
  margin-left: 45px;
}

.dropdown {
  margin-top: 8px;
  padding-left: 15px;
  width: 350px;
  height: 40px;
  background: #ffffff 0% 0% no-repeat padding-box;
  border: 1px solid #4675c126;
  border-radius: 8px;
  opacity: 1;
}

.dropdown::placeholder {
  font-size: 14px;
  font-weight: 200;
  opacity: 0.5;
}

.dropdown:valid {
  font-size: 14px;
  font-weight: 500;
}

.form-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.input-group {
  width: 44%; /* You can adjust the width based on your preference */
  margin-right: 50px;
  /* background-color: #19335a; */
}

.input-group input,
.input-group select,
.input-group textarea {
  margin-right: 30px;
  margin-bottom: 20px;
  margin-left: 45px;
}

.error-border {
  border: 1px solid red !important;
}

.error-tooltip {
  margin-left: 10px;
  font-weight: 100;
  font-size: 11px;
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
</style>
