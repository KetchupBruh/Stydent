<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import BackNavbar from "./BackNavbar.vue";
import ToastSuccess from '../components/ToastSuccess.vue';
import ToastError from '../components/ToastError.vue';

const showSuccess = ref(false);
const showError = ref(false);
const errorMessage = ref('Edit Summary Failed');
const successMessage = ref('Edit Summary Success');

const hideError = () => {
  showError.value = false;
};

const params = useRoute().params;
const summaryData = ref([]);
const originalDescription = ref("");
const originaltitle = ref('')
const fileUpload = ref(null);
const isFileSelected = ref(false);

const clearFile = () => {
  fileUpload.value = null;
  document.getElementById("fileUpload").value = "";
  isFileSelected.value = false;
};

const hasClickedEditButton = ref(false);

const maxfileDescriptionLength = 500;
const maxtitleLength = 150;

const isInvalidfileDescription = computed(() => {
  return summaryData.value.fileDescription.length > maxfileDescriptionLength;
});

const isInvalidtitle = computed(() => {
  return summaryData.value.title.length > maxtitleLength ;
});


const extractFileName = (filePath) => {
  if (filePath) {
    const parts = filePath.split("/");
    return parts[parts.length - 1];
  }
  return "";
};

const fetchSummary = async () => {
  try {
    const summaryResponse = await fetch(
      `${import.meta.env.VITE_BASE_URL}summary/${params.summaryid}`,
      {
        method: "GET",
        headers: {
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
      }
    );
    if (summaryResponse.ok) {
      const datajson = await summaryResponse.json();
      summaryData.value = datajson;
      originalDescription.value = summaryData.value.fileDescription;
      originaltitle.value = summaryData.value.title
    
      fetchFile();
    } else {
      console.error("ไม่สามารถดึงข้อมูลสรุปได้");
    }
  } catch (error) {
    console.error("เกิดข้อผิดพลาดในการดึงข้อมูลสรุป:", error);
  }
};

const fetchFile = async () => {
  try {
    if (summaryData.value.fileUpload) {
      const fileResponse = await fetch(
        `${import.meta.env.VITE_BASE_URL}files/${summaryData.value.fileUpload}`
      );

      if (fileResponse.ok) {
        console.log("get file success");
      } else {
        console.error("ไม่สามารถดึงข้อมูลไฟล์ได้");
      }
    }
  } catch (error) {
    console.error("เกิดข้อผิดพลาดในการดึงข้อมูลไฟล์:", error);
  }
};

const handleFileChange = (event) => {
  const maxSize = 15 * 1024 * 1024; // 15 MB ในไบต์
  const selectedFile = event.target.files[0];

  if (selectedFile && selectedFile.size > maxSize) {
    alert("ขนาดไฟล์เกิน 15 MB กรุณาเลือกไฟล์ขนาดเล็กกว่า");
    clearFileInput();
  } else {
    fileUpload.value = selectedFile;
  }
  isFileSelected.value = true;
};


const getFileSize = () => {
  return fileUpload.value
    ? (fileUpload.value.size / (1024 * 1024)).toFixed(2) + " MB"
    : "";
};


const clearFileInput = () => {
  document.getElementById("fileUpload").value = "";
  fileUpload.value = null;
};

const clearAll = () => {
  document.getElementById("fileUpload").value = "";
  fileUpload.value = null;
  summaryData.value.fileDescription = '';
  summaryData.value.title = '';
  isFileSelected.value = false; 
  hasClickedEditButton.value = true;
  hasClickedEditButton.value = false;
};



// const isDataChanged = computed(() => {
//   // console.log(originalDescription.value);
//   // console.log(summaryData.value.fileDescription);
//   // console.log(originaltitle.value);
//   // console.log(summaryData.value.title);
//   return (
//     originalDescription.value !== summaryData.value.fileDescription ||
//     fileUpload.value !== null || originaltitle.value !== summaryData.value.title
//   );
// });

const isDataChanged = computed(() => {
  return (
    (originalDescription.value !== summaryData.value.fileDescription ||
    fileUpload.value !== null || originaltitle.value !== summaryData.value.title) &&
    summaryData.value.fileDescription !== '' &&
    summaryData.value.title !== ''
  );
});



const saveChanges = async () => {
  hasClickedEditButton.value = true;

  // Validate before submitting the edit
  if (!isInputValid()) {
    return;
  }

  try {
    if (summaryData.value) {
      const formData = new FormData();

      // Check if a new file is selected
      if (fileUpload.value) {
        formData.append("fileUpload", fileUpload.value);
      }

      // Update this line based on your server's expectations
      formData.append("fileDescription", summaryData.value.fileDescription);

      formData.append("title", summaryData.value.title);



      const saveResponse = await fetch(
        `${import.meta.env.VITE_BASE_URL}summary/${params.summaryid}`,
        {
          method: "PUT",
          body: formData,
          headers: {
            Authorization: "Bearer " + localStorage.getItem("token"),
          },
        }
      );

      if (saveResponse.ok) {
        console.log("บันทึกสำเร็จ");
        showSuccess.value = true;

        setTimeout(function () {
          Summary();
        }, 1500);

      } else {
        console.error("ไม่สามารถบันทึกข้อมูลได้");
        showError.value = true;
      }
    }
  } catch (error) {
    console.error("เกิดข้อผิดพลาดในการบันทึกข้อมูล:", error);
  }
};

const isInputValid = () => {
  return !isInvalidfileDescription.value && !isInvalidtitle.value;;
};


const isFormEmpty = computed(() => {
  return (
    !summaryData.value.fileDescription &&
    !summaryData.value.title &&
    !fileUpload.value
  );
});


const appRouter = useRouter();
// const Summary = () => appRouter.push({ name: "Summary" });
const Summary = () => appRouter.go(-1);

onMounted(() => {
  fetchSummary();
});
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
        >Edit Summary &nbsp;> &nbsp; {{ summaryData.courseName }}
        {{ summaryData.courseFullName }} &nbsp;
        <span style="color: #4675c1"> > &nbsp; {{ params.summaryid }} </span>
      </span>
    </div>

    <div class="add-boxs">
      <div class="flex-container">
        <div class="line-review"></div>
        <p class="review">Edit Summary</p>
      </div>

      <div class="form-container">
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
          v-model.trim="summaryData.title"
          required
          id="fileDescription"
          class="dropdown"
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
          v-model.trim="summaryData.fileDescription"
          required
          id="fileDescription"
          class="dropdown"
        />
      </div>

      <div class="input-group">
        <label for="fileUpload" class="lable">File Upload</label> <br />

        <input
          type="file"
          @change="handleFileChange"
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

        <!-- <button
            v-if="isFileSelected"
            type="button"
     
            @click="clearFile"
          >
            Clear File
          </button> -->

      <span class="file-pre"
        >ไฟล์ที่เคยอัพโหลด : {{ extractFileName(summaryData.fileUpload).slice(14) }}</span
      >
      </div></div><br />

      <!-- <button
    @click="saveChanges"
    :disabled="!isDataChanged"
    class="editSummary"
      >
        Save Edit
      </button> -->


      <div class="button_add_clear">
        <button
          type="submit"
          class="editSummary"
          :disabled="!isDataChanged"
          @click="saveChanges"
        >
        Save Edit
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
    </div>
  </div>
  <BackNavbar />
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

.editSummary {
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

.editSummary:disabled {
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
  height:auto;
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

.file-pre {
  margin-left: 40px;
  display: inline-block;
}

.size-file {
  margin-left: 40px;
  margin-top: -10px;
}

#clearfile {
  margin-top: -30px;
  margin-left: 280px;
  width: 25px;
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
