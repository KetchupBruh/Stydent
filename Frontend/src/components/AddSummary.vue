<script setup>
import { ref, onBeforeMount, computed } from "vue";
import { useRouter } from "vue-router";
import BackNavbar from "./BackNavbar.vue";
import ToastSuccess from '../components/ToastSuccess.vue';
import ToastError from '../components/ToastError.vue';

const showSuccess = ref(false);
const showError = ref(false);
const errorMessage = ref('Add Summary Failed');
const successMessage = ref('Add Summarry Success');

const hideError = () => {
  showError.value = false;
};

const courses = ref([]);
const fileDescription = ref("");
const fileUpload = ref();
const title = ref();
const isFileSelected = ref(false);
const email = localStorage.getItem("email");


const hasClickedEditButton = ref(false);

const maxfileDescriptionLength = 500;
const maxtitleLength = 150;

const isInvalidfileDescription = computed(() => {
  return fileDescription.value.length > maxfileDescriptionLength ;
});

const isInvalidtitle = computed(() => {
  return title.value.length > maxtitleLength ;
});

const handleFileUpload = (event) => {
  const maxSize = 15 * 1024 * 1024; // 15 MB
  fileUpload.value = event.target.files[0];

  if (fileUpload.value.size > maxSize) {
    console.log("File size exceeds 15 MB. Clearing file...");
    alert("File size exceeds 15 MB. Please select a smaller file.");
    clearFile();
    // fileUpload.value = null;
  } else {
    console.log("File size within limits.");
  }
  isFileSelected.value = true;
};

const getFileSize = () => {
  return fileUpload.value
    ? (fileUpload.value.size / (1024 * 1024)).toFixed(2) + " MB"
    : "";
};


const clearFile = () => {
  fileUpload.value = null;
  document.getElementById("fileUpload").value = "";
  isFileSelected.value = false;

};


const clearAll = () => {
  fileUpload.value = null;
  document.getElementById("fileUpload").value = "";
  isFileSelected.value = false;
  selectedCourseName.value = "";
  fileDescription.value = '';
  title.value = '';
};

onBeforeMount(async () => {
  const response = await fetch(`${import.meta.env.VITE_BASE_URL}course/`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      "Authorization": "Bearer " + localStorage.getItem("token"),
    },
  });

  if (response.status === 200) {
    console.log("You have successfully logged in");

    const data = await response.json();
    courses.value = data;
  } else {
    console.error("Login failed");
  }
});

const addSummary = async () => {
  const formData = new FormData();
  formData.append("fileDescription", fileDescription.value);
  formData.append("emailOwner", email);
  formData.append("course_idCourse", selectedCourseId.value);
  formData.append("fileUpload", fileUpload.value);
  formData.append("title", title.value);


  hasClickedEditButton.value = true;

  // Validate before submitting the add
  if (!isInputValid()) {
    return;
  }

  try {
    const response = await fetch(`${import.meta.env.VITE_BASE_URL}summary/`, {
      method: "POST",
      body: formData,
      headers: {
      "Authorization": "Bearer " + localStorage.getItem("token"),
    },
    });

    if (response.ok) {
      console.log("Summary added successfully!");
      // alert("Summary add successfully!", "success");
      showSuccess.value = true;

      setTimeout(function () {
        Summary();
      }, 1500);
    } else {
      console.error("Failed to add summary");
      showError.value = true;
    }
  } catch (error) {
    console.error("Error adding summary:", error);
  }
};

const isInputValid = () => {
  return !isInvalidfileDescription.value && !isInvalidtitle.value;
};

const isFormEmpty = computed(() => {
  return !fileDescription.value && !selectedCourseId.value && !fileUpload.value && !title.value;
});

const selectedCourseName = ref("");

const selectedCourseId = computed(() => {
  const selectedName = selectedCourseName.value.trim();
  const selectedCourse = courses.value.find(
    (course) => `${course.courseName} ${course.courseFullName}` === selectedName
  );
  return selectedCourse ? selectedCourse.id : "";
});

const appRouter = useRouter();
const Summary = () => appRouter.push({ name: "Summary" });
</script>

<template>
  <div class="container mx-auto mt-18">
    <div class="toast-container">
      <ToastError :showError="showError"  :errorMessage="errorMessage" @hide-error="hideError"/>
      <ToastSuccess :showSuccess="showSuccess"  :successMessage="successMessage" />
    </div>
    <!-- ส่วนของ breadcrumb -->
    <div class="navigation text-gray-600 font-light text-sm mt-8 my-6">
      <span style="color: #b8bfd6"
        >Summary &nbsp;
        <span style="color: #4675c1"> > &nbsp; Add Summary </span>
      </span>
    </div>

    <div class="add-boxs">
      <div class="flex-container">
        <div class="line-review"></div>
        <p class="review">Add Summary</p>
      </div>

      <form @submit.prevent="addSummary" class="form-container">
        <div class="input-group">
          <label for="courseIdcourse" class="lable">Course</label><br />
          <input
            v-model.trim="selectedCourseName"
            list="coursesList"
            placeholder="Search for a course..."
            required
            class="dropdown"
          />
          <datalist id="coursesList">
            <option
              v-for="course in courses"
              :key="course.id"
              :value="`${course.courseName} ${course.courseFullName}`"
            ></option>
          </datalist>
        </div>

        <div class="input-group">
          <label for="title" class="lable">Title</label
          ><span
            v-if="hasClickedEditButton && isInvalidtitle"
            class="error-tooltip"
          >
            * Maximum 150 characters allowed
          </span>
          <br />

          <input
            v-model.trim="title"
            type="text"
            required
            id="fileDescription"
            class="dropdown"
            :style="{
              border:
              isInvalidtitle && hasClickedEditButton
                  ? '1px solid red'
                  : '',
            }"
          />
        </div>

        <div class="input-group">
          <label for="fileDescription" class="lable">File Description</label
          ><span
            v-if="hasClickedEditButton && isInvalidfileDescription"
            class="error-tooltip"
          >
            * Maximum 500 characters allowed
          </span>
          <br />

          <input
            v-model.trim="fileDescription"
            type="text"
            required
            id="fileDescription"
            class="dropdown"
            :style="{
              border:
              isInvalidfileDescription && hasClickedEditButton
                  ? '1px solid red'
                  : '',
            }"
          />
        </div>

        <div class="input-group">
          <label for="fileUpload" class="lable">File Upload</label> <br />

          <input
            type="file"
            @change="handleFileUpload"
            id="fileUpload"
            required
          /> 
          
          <p v-if="isFileSelected" class="size-file">
            Selected file size: {{ getFileSize() }}
          </p>
          <svg
          v-if="isFileSelected"
            type="button"
            @click="clearFile"
            id="clearfile"
            class="h-8 w-8 text-red-500"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <polyline points="3 6 5 6 21 6" />
            <path
              d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"
            />
            <line x1="10" y1="11" x2="10" y2="17" />
            <line x1="14" y1="11" x2="14" y2="17" />
          </svg>

        </div>


        <div class="button_add_clear">
        <button
          type="submit"
          class="addSummary"
          :disabled="!fileDescription || !selectedCourseId || !fileUpload || !title"
        >
          Add Summary
        </button>

        <button
            type="button"
            class="button-clear"
            @click="clearAll()"
            :disabled="isFormEmpty"      
          >
            Clear
          </button>

        </div>


      </form>
    </div>
  </div>
  <BackNavbar :backToPage="{ name: 'Summary' }" />
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

.button_add_clear {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
  margin-left: 40%;
}

.addSummary {
  background-color: #4675c0;
  height: 40px;
  width: 135px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 300;
  letter-spacing: 0.56px;
  color: white;
  margin-right: 30px;
  margin-left: -60px;
}

.button-clear {
  background-color: white;
  border: solid 1px #4675c0;
  height: 40px;
  width: 135px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 300;
  letter-spacing: 0.56px;
  color: #4675c0;
}
.button-clear:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.addSummary:disabled {
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
  padding-bottom: 30px;
  
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
  font: normal normal 400 14px/27px Poppins;
  letter-spacing: 0.28px;
  color: #697a98;
  opacity: 1;
  margin-left: 45px;
}

.dropdown {
  margin-top: 5px;
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

#fileUpload {
  margin-top: 8px;
}

.size-file {
  margin-left: 40px;
  margin-top: -10px;
}
#clearfile{
  margin-top: -30px;
  margin-left: 280px;
  width:25px;

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
