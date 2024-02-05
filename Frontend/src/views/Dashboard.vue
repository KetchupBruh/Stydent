<script setup>
import { ref, onBeforeMount } from "vue";
import { useRouter } from "vue-router";

const review = ref([]);
const summary = ref([]);

onBeforeMount(async () => {
  try {
    const reviewResponse = await fetch(
      `${import.meta.env.VITE_BASE_URL}review/`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
      }
    );

    if (reviewResponse.status === 200) {
      console.log("You have successfully logged in");

      const reviewData = await reviewResponse.json();
      review.value = reviewData;
    }
    if (reviewResponse.status === 401) {
      // login();
      signout();
    }

    const summaryResponse = await fetch(
      `${import.meta.env.VITE_BASE_URL}summary/`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: "Bearer " + localStorage.getItem("token"),
        },
      }
    );

    if (summaryResponse.status === 200) {
      console.log("Summary data fetched successfully");

      const summaryData = await summaryResponse.json();
      summary.value = summaryData;
    }
    if (summaryResponse.status === 401) {
      // login();
      // signout()
    }
  } catch (error) {
    console.error("An error occurred:", error);
  }
});

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
  <div class="container mx-auto px-4">
    <section class="grid gap-7 grid-cols-2 grid-rows-2 mt-14">
      <div id="item-1">
        <div class="box-line" style="background-color: #ea5355"></div>
        <p class="item-name" style="color: #ea5355">Summary</p>
        <p class="item-number" v-if="summary.length > 0">
          {{ summary.length }}
        </p>
        <p class="item-number" v-else>0</p>
        <div class="box-icon" style="background-color: #ea5355">
          <img src="../assets/summary.png" />
        </div>
      </div>

      <div id="item-2">
        <div class="box-line" style="background-color: #f7b54c"></div>
        <p class="item-name" style="color: #f7b54c">Review</p>
        <p class="item-number" v-if="review.length > 0">{{ review.length }}</p>
        <p class="item-number" v-else>0</p>
        <div class="box-icon" style="background-color: #f7b54c">
          <img src="../assets/review.png" />
        </div>
      </div>

      <div id="item-3">
        <!-- <img src="../assets/userdashboard.png" /> -->
        <svg
          xmlns="http://www.w3.org/2000/svg"
          xmlns:xlink="http://www.w3.org/1999/xlink"
          width="1008"
          height="358"
          viewBox="0 0 1008 358"
        >
          <defs>
            <linearGradient
              id="linear-gradient"
              x1="0.768"
              y1="0.5"
              x2="0.237"
              y2="0.5"
              gradientUnits="objectBoundingBox"
            >
              <stop offset="0" stop-color="#5379e8" />
              <stop offset="1" stop-color="#897add" />
            </linearGradient>
            <filter
              id="Rectangle_92"
              x="0"
              y="16"
              width="1008"
              height="342"
              filterUnits="userSpaceOnUse"
            >
              <feOffset dy="3" input="SourceAlpha" />
              <feGaussianBlur stdDeviation="12" result="blur" />
              <feFlood flood-color="#457aef" flood-opacity="0.353" />
              <feComposite operator="in" in2="blur" />
              <feComposite in="SourceGraphic" />
            </filter>
            <filter
              id="Rectangle_95"
              x="45"
              y="193"
              width="260"
              height="110"
              filterUnits="userSpaceOnUse"
            >
              <feOffset input="SourceAlpha" />
              <feGaussianBlur stdDeviation="10" result="blur-2" />
              <feFlood flood-color="#457aef" flood-opacity="0.149" />
              <feComposite operator="in" in2="blur-2" />
              <feComposite in="SourceGraphic" />
            </filter>
            <clipPath id="clip-path">
              <rect
                id="Rectangle_93"
                data-name="Rectangle 93"
                width="810"
                height="319"
                rx="20"
                transform="translate(3734 288)"
                fill="#457aef"
              />
            </clipPath>
          </defs>
          <g
            id="Group_334"
            data-name="Group 334"
            transform="translate(-344 -75)"
          >
            <g
              transform="matrix(1, 0, 0, 1, 344, 75)"
              filter="url(#Rectangle_92)"
            >
              <rect
                id="Rectangle_92-2"
                data-name="Rectangle 92"
                width="936"
                height="270"
                rx="20"
                transform="translate(36 49)"
                fill="url(#linear-gradient)"
              />
            </g>
            <path
              id="Path_177"
              data-name="Path 177"
              d="M126.561,44.35c118.747-66.069,143.823-57.62,255.994-56.986,35.71,5.738,21.417,45.943,21.7,89.783-3.366,57.706,0,125.271,0,143.676,0,35.554-6.208,36.307-39.969,36.4-43.482,3.2-119.412,0-175.4,0-52.932,0-133.341.955-192.991,0-20.255-.5-11.305-1.318-54.981,0-20.9-3.478-26.359-12.387-26.359-36.019C-85.444,135.328,7.815,110.42,126.561,44.35Z"
              transform="translate(912 137)"
              fill="rgba(255,255,255,0.15)"
            />
            <g
              id="Group_305"
              data-name="Group 305"
              transform="translate(0 -21)"
            >
              <line
                id="Line_13"
                data-name="Line 13"
                x2="83"
                transform="translate(760.5 371.5)"
                fill="none"
                stroke="rgba(255,255,255,0.2)"
                stroke-linecap="round"
                stroke-width="10"
              />
              <line
                id="Line_14"
                data-name="Line 14"
                x2="83"
                transform="translate(910.5 371.5)"
                fill="none"
                stroke="rgba(255,255,255,0.2)"
                stroke-linecap="round"
                stroke-width="10"
              />
              <line
                id="Line_15"
                data-name="Line 15"
                x2="8.5"
                transform="translate(872.5 371.932)"
                fill="none"
                stroke="rgba(255,255,255,0.2)"
                stroke-linecap="round"
                stroke-width="10"
              />
              <line
                id="Line_16"
                data-name="Line 16"
                x2="9"
                transform="translate(786.5 331.932)"
                fill="none"
                stroke="rgba(255,255,255,0.2)"
                stroke-linecap="round"
                stroke-width="10"
              />
              <line
                id="Line_17"
                data-name="Line 17"
                x2="41"
                transform="translate(817.5 331.932)"
                fill="none"
                stroke="rgba(255,255,255,0.2)"
                stroke-linecap="round"
                stroke-width="10"
              />
              <line
                id="Line_18"
                data-name="Line 18"
                x2="44"
                transform="translate(962.5 268.5)"
                fill="none"
                stroke="rgba(255,255,255,0.2)"
                stroke-linecap="round"
                stroke-width="10"
              />
              <line
                id="Line_20"
                data-name="Line 20"
                x2="8.5"
                transform="translate(1034.5 268.932)"
                fill="none"
                stroke="rgba(255,255,255,0.2)"
                stroke-linecap="round"
                stroke-width="10"
              />
              <line
                id="Line_21"
                data-name="Line 21"
                x2="78.5"
                transform="translate(1233 313.5)"
                fill="none"
                stroke="rgba(255,255,255,0.2)"
                stroke-linecap="round"
                stroke-width="10"
              />
            </g>
            <g
              transform="matrix(1, 0, 0, 1, 344, 75)"
              filter="url(#Rectangle_95)"
            >
              <rect
                id="Rectangle_95-2"
                data-name="Rectangle 95"
                width="200"
                height="50"
                rx="25"
                transform="translate(75 223)"
                fill="rgba(12,0,92,0.38)"
              />
            </g>
            <text
              id="STYDENT"
              transform="translate(470 332)"
              fill="#fff"
              font-size="24"
              font-family="Poppins-SemiBold, Poppins"
              font-weight="600"
              letter-spacing="0.03em"
            >
              <tspan x="0" y="0">Stydent</tspan>
            </text>
            <text
              id="A_website_that_collects_"
              data-name="A website that collects "
              transform="translate(419 186)"
              fill="#fff"
              font-size="16"
              font-family="Poppins-Medium, Poppins"
              font-weight="500"
              letter-spacing="0.05em"
            >
              <tspan x="0" y="0">A website that collects</tspan>
            </text>
            <text
              id="summary_information_and_review_"
              data-name="summary information and review "
              transform="translate(419 224)"
              fill="#fff"
              font-size="26"
              font-family="Poppins-Bold, Poppins"
              font-weight="700"
              letter-spacing="0.05em"
            >
              <tspan x="0" y="0">summary information and review</tspan>
            </text>
            <text
              id="in_the_faculty_of_information_Technology"
              data-name="in the faculty of information Technology"
              transform="translate(419 255)"
              fill="#fff"
              font-size="16"
              font-family="Poppins-Medium, Poppins"
              font-weight="500"
              letter-spacing="0.05em"
            >
              <tspan x="0" y="0">
                in the faculty of information Technology
              </tspan>
            </text>
            <g
              id="Mask_Group_5"
              data-name="Mask Group 5"
              transform="translate(-3228 -213)"
              clip-path="url(#clip-path)"
            >
              <g
                id="_6622372_3361499"
                data-name="6622372_3361499"
                transform="translate(4154 309.221)"
              >
                <g id="OBJECTS">
                  <path
                    id="Path_148"
                    data-name="Path 148"
                    d="M445.028,263.206"
                    transform="translate(-280.824 -68.044)"
                    fill="#f19a99"
                  />
                  <g id="Group_300" data-name="Group 300">
                    <g
                      id="Group_267"
                      data-name="Group 267"
                      transform="translate(184.866 251.825)"
                    >
                      <path
                        id="Path_149"
                        data-name="Path 149"
                        d="M288.439,317.786c28.562-.364,112.589,1.754,117.351,1.762,3.5,8.535,25.184,187.517,25.184,187.517H363.726l-14.118-77.034-9.234,77.034h-64.9S287.371,325.265,288.439,317.786Z"
                        transform="translate(-275.474 -317.744)"
                        fill="#ff7686"
                      />
                      <path
                        id="Path_150"
                        data-name="Path 150"
                        d="M353.787,363.676l23.734,17.835,6.208,46.73"
                        transform="translate(-309.594 -315.954)"
                        fill="none"
                        stroke="#031932"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-miterlimit="10"
                        stroke-width="2.072"
                      />
                    </g>
                    <g
                      id="Group_288"
                      data-name="Group 288"
                      transform="translate(222.468)"
                    >
                      <g
                        id="Group_268"
                        data-name="Group 268"
                        transform="translate(0)"
                      >
                        <path
                          id="Path_151"
                          data-name="Path 151"
                          d="M371.861,127.26s-.341,6.166,2.044,8.685c7.117,7.519,29.2,14.948,14.084,43.658L281.4,168.9s-4.125-12.449,8.452-18.183c0,0,4.4-2.267,2.642-10.606s5.128-13.446,13.735-21.3L323.741,97.3Z"
                          transform="translate(-280.751 -74.51)"
                          fill="#37305d"
                        />
                        <path
                          id="Path_152"
                          data-name="Path 152"
                          d="M327.168,171.038l52.384,7.018s18.957-81.91-16.655-97.239c-37.81-16.275-53.767,7.43-61.745,24.766Z"
                          transform="translate(-293.375 -75.365)"
                          fill="#37305d"
                        />
                      </g>
                      <g
                        id="Group_287"
                        data-name="Group 287"
                        transform="translate(0.706 0.225)"
                      >
                        <g id="Group_286" data-name="Group 286">
                          <g id="Group_285" data-name="Group 285">
                            <g id="Group_284" data-name="Group 284">
                              <g id="Group_277" data-name="Group 277">
                                <g id="Group_270" data-name="Group 270">
                                  <g id="Group_269" data-name="Group 269">
                                    <path
                                      id="Path_153"
                                      data-name="Path 153"
                                      d="M375.19,78.228a25.54,25.54,0,0,1,6.529,4.54c17.98,17.148,6.789,50.627-9.186,65.779-8.842,8.388-21.8,12.32-33.146,8.115-16.594-6.149-21.86-24.9-18.566-41.623,2.455-12.462,9.3-24.251,19.471-31.613S364.212,72.921,375.19,78.228Z"
                                      transform="translate(-319.889 -75.581)"
                                      fill="#f3b6b5"
                                    />
                                  </g>
                                </g>
                                <g
                                  id="Group_272"
                                  data-name="Group 272"
                                  transform="translate(58.537 42.639)"
                                >
                                  <g
                                    id="Group_271"
                                    data-name="Group 271"
                                    transform="translate(0)"
                                  >
                                    <path
                                      id="Path_154"
                                      data-name="Path 154"
                                      d="M310.922,136.847a13.076,13.076,0,0,0,12.805,2.841,14.207,14.207,0,0,0,8.683-10.056c.952-4.121-.12-8.8-3.162-11.3s-8.482-2.323-11.053.965Z"
                                      transform="translate(-310.922 -116.621)"
                                      fill="#f3b6b5"
                                    />
                                  </g>
                                </g>
                                <g
                                  id="Group_274"
                                  data-name="Group 274"
                                  transform="translate(18.724 51.913)"
                                >
                                  <g
                                    id="Group_273"
                                    data-name="Group 273"
                                    transform="translate(0)"
                                  >
                                    <path
                                      id="Path_155"
                                      data-name="Path 155"
                                      d="M371.911,125.547l-3.038,59.1a20.663,20.663,0,0,1-20.148,19.915h0a20.661,20.661,0,0,1-21.006-23.2l6.425-54.9Z"
                                      transform="translate(-327.559 -125.547)"
                                      fill="#f3b6b5"
                                    />
                                  </g>
                                </g>
                                <g
                                  id="Group_276"
                                  data-name="Group 276"
                                  transform="translate(20.97 72.967)"
                                >
                                  <g
                                    id="Group_275"
                                    data-name="Group 275"
                                    transform="translate(0)"
                                  >
                                    <path
                                      id="Path_156"
                                      data-name="Path 156"
                                      d="M337.6,162.748l.792-8.1s16.284,4.927,30.882-8.842C369.274,145.811,360.91,161.249,337.6,162.748Z"
                                      transform="translate(-337.6 -145.811)"
                                      fill="#111010"
                                    />
                                  </g>
                                </g>
                              </g>
                              <g
                                id="Group_283"
                                data-name="Group 283"
                                transform="translate(4.41 32.259)"
                              >
                                <g
                                  id="Group_282"
                                  data-name="Group 282"
                                  transform="translate(0)"
                                >
                                  <g
                                    id="Group_278"
                                    data-name="Group 278"
                                    transform="translate(7.219 12.513)"
                                  >
                                    <path
                                      id="Path_157"
                                      data-name="Path 157"
                                      d="M375.9,126.041a7.577,7.577,0,0,0-.926-2.052,11.6,11.6,0,0,0-1.3-1.747l-.5,1.624a5.779,5.779,0,0,0,2.788-2.211,8.341,8.341,0,0,0,.833-1.424,6.994,6.994,0,0,0,.518-1.558c-.866.679-1.642,1.356-2.425,1.953a7.381,7.381,0,0,1-2.287,1.282l-1.436.375.932,1.249a11.658,11.658,0,0,0,1.448,1.625,7.575,7.575,0,0,0,1.823,1.319.378.378,0,0,0,.508-.17A.369.369,0,0,0,375.9,126.041Z"
                                      transform="translate(-371.16 -118.673)"
                                      fill="#d87778"
                                    />
                                  </g>
                                  <g
                                    id="Group_279"
                                    data-name="Group 279"
                                    transform="translate(7.197 22.267)"
                                  >
                                    <path
                                      id="Path_158"
                                      data-name="Path 158"
                                      d="M363.524,130.888a6.834,6.834,0,0,0,3.164,3.254,6.523,6.523,0,0,0,4.808.425,8.834,8.834,0,0,0,3.911-2.615,19.226,19.226,0,0,0,1.309-1.845,18.234,18.234,0,0,0,.911-2.045,20.979,20.979,0,0,0-1.685,1.4c-.542.46-1.116.863-1.624,1.318a12.176,12.176,0,0,1-3.407,1.822,6.7,6.7,0,0,1-3.643.055A13.3,13.3,0,0,1,363.524,130.888Z"
                                      transform="translate(-363.524 -128.062)"
                                      fill="#d87778"
                                    />
                                  </g>
                                  <path
                                    id="Path_159"
                                    data-name="Path 159"
                                    d="M318.989,130.259c2.436.238,4.56-1.091,4.744-2.969a3.534,3.534,0,0,0-2.388-3.4"
                                    transform="translate(-256.174 -105.957)"
                                    fill="none"
                                    stroke="#d87778"
                                    stroke-linecap="round"
                                    stroke-miterlimit="10"
                                    stroke-width="1.744"
                                  />
                                  <g
                                    id="Group_280"
                                    data-name="Group 280"
                                    transform="translate(23.546 3.599)"
                                  >
                                    <path
                                      id="Path_160"
                                      data-name="Path 160"
                                      d="M351.617,110.314a15.829,15.829,0,0,0-.713,5.856,5.373,5.373,0,0,0,1.056,2.962c.186.21.41.4.62.591a5.934,5.934,0,0,0,.753.414,3.8,3.8,0,0,0,.809.215,2.623,2.623,0,0,0,.824.057,4.607,4.607,0,0,0,2.959-1.2,8.964,8.964,0,0,0,1.9-2.39,18.273,18.273,0,0,0,1.937-5.569.366.366,0,0,0-.712-.165l-.005.018a21.7,21.7,0,0,1-2.143,5.175,8.9,8.9,0,0,1-1.7,2.071,3.476,3.476,0,0,1-2.271.909,2.535,2.535,0,0,1-2.13-.823,4.7,4.7,0,0,1-.892-2.383,18.119,18.119,0,0,1,.306-5.574l0-.017a.31.31,0,0,0-.6-.146Z"
                                      transform="translate(-350.859 -110.093)"
                                      fill="#222c44"
                                    />
                                  </g>
                                  <g id="Group_281" data-name="Group 281">
                                    <path
                                      id="Path_161"
                                      data-name="Path 161"
                                      d="M376.348,106.854a15.175,15.175,0,0,0-.656,4.9,5.211,5.211,0,0,0,.711,2.553,2.657,2.657,0,0,0,1.086,1,2,2,0,0,0,.705.241c.121.026.242.056.364.074l.365-.008a3.725,3.725,0,0,0,2.5-1.169,8.386,8.386,0,0,0,1.467-2.118,16.883,16.883,0,0,0,1.452-4.728.366.366,0,0,0-.715-.155l0,.016a21.594,21.594,0,0,1-1.683,4.373,8.625,8.625,0,0,1-1.3,1.81,2.585,2.585,0,0,1-1.756.82,1.675,1.675,0,0,1-1.565-.756,4.646,4.646,0,0,1-.616-2.034,18.767,18.767,0,0,1,.248-4.672l0-.015a.31.31,0,0,0-.258-.355A.314.314,0,0,0,376.348,106.854Z"
                                      transform="translate(-375.682 -106.63)"
                                      fill="#222c44"
                                    />
                                  </g>
                                </g>
                              </g>
                            </g>
                          </g>
                        </g>
                      </g>
                      <path
                        id="Path_162"
                        data-name="Path 162"
                        d="M317.538,85.563a11.06,11.06,0,0,0-4.427,6.425c-1.2,5.147,1.211,12.547,24.827,12.809,21.966.244,17.239,15.035,32.219,13.4l9.028-3.424s-6.465-49.681-46.691-37.353C327.1,79.075,320.748,83.28,317.538,85.563Z"
                        transform="translate(-300.136 -75.361)"
                        fill="#37305d"
                      />
                    </g>
                    <g
                      id="Group_299"
                      data-name="Group 299"
                      transform="translate(0 81.03)"
                    >
                      <g id="Group_298" data-name="Group 298">
                        <g
                          id="Group_289"
                          data-name="Group 289"
                          transform="translate(61.613 77.286)"
                        >
                          <path
                            id="Path_163"
                            data-name="Path 163"
                            d="M449.178,268.581l-41.154,40.756,6.429,7.214a30.253,30.253,0,0,0,14.1,9.1c13,3.734,26.555-1.862,36.039-15.6l14.952-21.5Z"
                            transform="translate(-338.506 -226.15)"
                            fill="#f3b6b5"
                          />
                          <path
                            id="Path_164"
                            data-name="Path 164"
                            d="M523.016,321.594,443.544,246l19.563-18.259,81.653,66.373C552.363,300.3,545.1,333.249,523.016,321.594Z"
                            transform="translate(-443.544 -227.742)"
                            fill="#f3b6b5"
                          />
                        </g>
                        <path
                          id="Path_165"
                          data-name="Path 165"
                          d="M289.208,280.015c-14.117-18.522,32.49-92.912,51.929-105.1,11.127-6.975,24-5.489,32.736-5.718.473,3.981,4.3,9.313,14.333,11.524,15.2,3.348,24.792-1.56,29.259-3.843a145.265,145.265,0,0,1,30.14,9.839c12.11,5.72,21.11,96.713,3.333,105.619,2.7,19.213,5.267,48.165,5.267,48.165l-131.352-9.934,3.776-26.128S295.758,288.608,289.208,280.015Z"
                          transform="translate(-132.114 -152.738)"
                          fill="#fbe4e9"
                        />
                        <line
                          id="Line_12"
                          data-name="Line 12"
                          x1="3.398"
                          y2="33.128"
                          transform="translate(195.783 112.908)"
                          fill="none"
                          stroke="#031932"
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-miterlimit="10"
                          stroke-width="2.072"
                        />
                        <g
                          id="Group_293"
                          data-name="Group 293"
                          transform="translate(200.464 24.143)"
                        >
                          <g
                            id="Group_292"
                            data-name="Group 292"
                            transform="translate(0 91.282)"
                          >
                            <path
                              id="Path_166"
                              data-name="Path 166"
                              d="M404.29,296.771l8.133,3.822c.773.337-13.476,18.8-13.476,18.8s-38.48-9.413-40.792-11.281,12.769-.525,12.769-.525-13.982-4-17.388-7.073c-2.712-2.446,16.762-.477,16.762-.477-6.993-2.493-20.259-6.306-19.823-8.663.549-2.954,22.869,3.451,22.869,3.451s-15.261-5.752-15.536-8.685,29.934,4.828,29.934,4.828-4.162-5.891-2.547-12.364c.41-1.647,2.5-2.556,3.756-2.468C389.754,284.708,404.29,296.771,404.29,296.771Z"
                              transform="translate(-350.464 -263.996)"
                              fill="#f3b6b5"
                            />
                            <g
                              id="Group_291"
                              data-name="Group 291"
                              transform="translate(35.376)"
                            >
                              <g
                                id="Group_290"
                                data-name="Group 290"
                                transform="translate(0)"
                              >
                                <path
                                  id="Path_167"
                                  data-name="Path 167"
                                  d="M375.391,264.451s9.625,37.945,2.7,65.9c-11.542,46.6-117.942-16.986-117.942-16.986l7.146-21.185s77.269,18.687,76.795,15.728c-1.2-7.465-4.217-32.478-4.217-32.478Z"
                                  transform="translate(-260.149 -264.451)"
                                  fill="#f3b6b5"
                                />
                              </g>
                            </g>
                          </g>
                          <path
                            id="Path_168"
                            data-name="Path 168"
                            d="M266.115,300.435s66.714,19.1,65.4,1.691c-2.367-31.242-30.062-94.893-49.047-113.879-6.2-6.2-28.645-11.689-33.707-11.654Z"
                            transform="translate(-163.869 -176.593)"
                            fill="#fbe4e9"
                          />
                          <path
                            id="Path_169"
                            data-name="Path 169"
                            d="M310.972,298.464s-1.611-12.084-6.445-38.669"
                            transform="translate(-201.255 -173.35)"
                            fill="none"
                            stroke="#031932"
                            stroke-linecap="round"
                            stroke-linejoin="round"
                            stroke-miterlimit="10"
                            stroke-width="2.072"
                          />
                        </g>
                        <g id="Group_297" data-name="Group 297">
                          <g id="Group_295" data-name="Group 295">
                            <path
                              id="Path_170"
                              data-name="Path 170"
                              d="M476.94,246.686H420.972v-8.964H476.94a4.482,4.482,0,0,1,4.482,4.482h0A4.482,4.482,0,0,1,476.94,246.686Z"
                              transform="translate(-292.225 -150.067)"
                              fill="#d8dcf3"
                            />
                            <g id="Group_294" data-name="Group 294">
                              <path
                                id="Path_171"
                                data-name="Path 171"
                                d="M608.294,249.974H503.01A13.506,13.506,0,0,1,490,240.089l-20.53-73.759a10.232,10.232,0,0,1,9.857-12.976h96.7A8.908,8.908,0,0,1,584.64,160Z"
                                transform="translate(-469.088 -153.355)"
                                fill="#d8dcf3"
                              />
                              <path
                                id="Path_172"
                                data-name="Path 172"
                                d="M550.707,204.2a9.961,9.961,0,1,1-9.96-9.961A9.96,9.96,0,0,1,550.707,204.2Z"
                                transform="translate(-475.604 -151.762)"
                                fill="#fff"
                              />
                            </g>
                          </g>
                          <g
                            id="Group_296"
                            data-name="Group 296"
                            transform="translate(61.438 81.671)"
                          >
                            <path
                              id="Path_173"
                              data-name="Path 173"
                              d="M524.379,246.359s.039,10.778-13.075,9.086c0,0-6.7-3.675-5.856-20.019.1-1.927,2.052-3.455,4.4-3.463h0a4.267,4.267,0,0,1,4.35,2.963l2.573,11.46Z"
                              transform="translate(-484.311 -231.963)"
                              fill="#f3b6b5"
                            />
                            <path
                              id="Path_174"
                              data-name="Path 174"
                              d="M544.735,255.425s-5.469,2.565-10.733.01c0,0-4.881,1.169-8.5-1.254a6.253,6.253,0,0,1-4.568-.545,5.561,5.561,0,0,1-3.176-4.922c-.1-3.376.277-9.2,3.537-12.632a3.054,3.054,0,0,1,3-.842c.875.231,1.366.024,1.9,1.115,0,0,1.256-2.209,3.368-2.216a3.573,3.573,0,0,1,3.707,2.461s-.908-5.229,6.22-3.913S544.735,255.425,544.735,255.425Z"
                              transform="translate(-517.743 -231.943)"
                              fill="#f3b6b5"
                            />
                          </g>
                        </g>
                      </g>
                      <path
                        id="Path_175"
                        data-name="Path 175"
                        d="M372.016,176.88s-7.236,7.157-24.132,5.105a26.862,26.862,0,0,1-14.4-6.133,18.117,18.117,0,0,1-5.06-6.654"
                        transform="translate(-86.665 -152.738)"
                        fill="none"
                        stroke="#031932"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-miterlimit="10"
                        stroke-width="2.072"
                      />
                    </g>
                  </g>
                </g>
              </g>
            </g>
          </g>
        </svg>
      </div>
      <div class="feature">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          xmlns:xlink="http://www.w3.org/1999/xlink"
          width="1127"
          height="477.5"
          viewBox="0 0 1127 477.5"
        >
          <defs>
            <filter
              id="Rectangle_100"
              x="952"
              y="227.5"
              width="170"
              height="170"
              filterUnits="userSpaceOnUse"
            >
              <feOffset input="SourceAlpha" />
              <feGaussianBlur stdDeviation="12" result="blur" />
              <feFlood flood-color="#fed363" flood-opacity="0.29" />
              <feComposite operator="in" in2="blur" />
              <feComposite in="SourceGraphic" />
            </filter>
            <filter
              id="Rectangle_99"
              x="635"
              y="227.5"
              width="170"
              height="170"
              filterUnits="userSpaceOnUse"
            >
              <feOffset input="SourceAlpha" />
              <feGaussianBlur stdDeviation="12" result="blur-2" />
              <feFlood flood-color="#02bbb6" flood-opacity="0.29" />
              <feComposite operator="in" in2="blur-2" />
              <feComposite in="SourceGraphic" />
            </filter>
            <filter
              id="Rectangle_98"
              x="321"
              y="226.5"
              width="170"
              height="170"
              filterUnits="userSpaceOnUse"
            >
              <feOffset input="SourceAlpha" />
              <feGaussianBlur stdDeviation="12" result="blur-3" />
              <feFlood flood-color="#fe6b74" flood-opacity="0.29" />
              <feComposite operator="in" in2="blur-3" />
              <feComposite in="SourceGraphic" />
            </filter>
            <filter
              id="Rectangle_96"
              x="473.5"
              y="0"
              width="180"
              height="85"
              filterUnits="userSpaceOnUse"
            >
              <feOffset input="SourceAlpha" />
              <feGaussianBlur stdDeviation="7.5" result="blur-4" />
              <feFlood flood-color="#457aef" flood-opacity="0.149" />
              <feComposite operator="in" in2="blur-4" />
              <feComposite in="SourceGraphic" />
            </filter>
            <filter
              id="Rectangle_97"
              x="3"
              y="226.5"
              width="170"
              height="170"
              filterUnits="userSpaceOnUse"
            >
              <feOffset input="SourceAlpha" />
              <feGaussianBlur stdDeviation="12" result="blur-5" />
              <feFlood flood-color="#457aef" flood-opacity="0.29" />
              <feComposite operator="in" in2="blur-5" />
              <feComposite in="SourceGraphic" />
            </filter>
          </defs>
          <g
            id="Group_335"
            data-name="Group 335"
            transform="translate(-120 -474.5)"
          >
            <g
              transform="matrix(1, 0, 0, 1, 120, 474.5)"
              filter="url(#Rectangle_100)"
            >
              <rect
                id="Rectangle_100-2"
                data-name="Rectangle 100"
                width="98"
                height="98"
                rx="20"
                transform="translate(988 263.5)"
                fill="#fff"
              />
            </g>
            <g
              transform="matrix(1, 0, 0, 1, 120, 474.5)"
              filter="url(#Rectangle_99)"
            >
              <rect
                id="Rectangle_99-2"
                data-name="Rectangle 99"
                width="98"
                height="98"
                rx="20"
                transform="translate(671 263.5)"
                fill="#fff"
              />
            </g>
            <g
              transform="matrix(1, 0, 0, 1, 120, 474.5)"
              filter="url(#Rectangle_98)"
            >
              <rect
                id="Rectangle_98-2"
                data-name="Rectangle 98"
                width="98"
                height="98"
                rx="20"
                transform="translate(357 262.5)"
                fill="#fff"
              />
            </g>
            <text
              id="Manage_your_study_in_a_single_system"
              data-name="Manage your study in a single system"
              transform="translate(367 604)"
              fill="#19335a"
              font-size="32"
              font-family="Poppins-Medium, Poppins"
              font-weight="500"
              letter-spacing="0.02em"
            >
              <tspan x="0" y="0">Manage your study in a single system</tspan>
            </text>
            <g id="Group_312" data-name="Group 312" transform="translate(13)">
              <g
                transform="matrix(1, 0, 0, 1, 107, 474.5)"
                filter="url(#Rectangle_96)"
              >
                <rect
                  id="Rectangle_96-2"
                  data-name="Rectangle 96"
                  width="135"
                  height="40"
                  rx="20"
                  transform="translate(496 22.5)"
                  fill="rgba(69,122,239,0.17)"
                />
              </g>
              <text
                id="Features"
                transform="translate(632 523)"
                fill="#4675c0"
                font-size="16"
                font-family="Poppins-Medium, Poppins"
                font-weight="500"
                letter-spacing="0.02em"
              >
                <tspan x="0" y="0">FEATURES</tspan>
              </text>
            </g>
            <text
              id="Student_made_for_"
              data-name="Student made for ?"
              transform="translate(564 655)"
              fill="#b8bfd6"
              font-size="24"
              font-family="Poppins-Medium, Poppins"
              font-weight="500"
              letter-spacing="0.02em"
            >
              <tspan x="0" y="0">Stydent made for ?</tspan>
            </text>
            <g
              transform="matrix(1, 0, 0, 1, 120, 474.5)"
              filter="url(#Rectangle_97)"
            >
              <rect
                id="Rectangle_97-2"
                data-name="Rectangle 97"
                width="98"
                height="98"
                rx="20"
                transform="translate(39 262.5)"
                fill="#fff"
              />
            </g>
            <text
              id="Summary"
              transform="translate(157 880)"
              fill="#19335a"
              font-size="20"
              font-family="Poppins-Medium, Poppins"
              font-weight="500"
              letter-spacing="0.02em"
            >
              <tspan x="0" y="0">Summary</tspan>
            </text>
            <circle
              id="Ellipse_32"
              data-name="Ellipse 32"
              cx="12"
              cy="12"
              r="12"
              transform="translate(218 753)"
              fill="rgba(69,122,239,0.4)"
            />
            <path
              id="Icon_ionic-md-book"
              data-name="Icon ionic-md-book"
              d="M50.471,5.673,29.277,9.658,8.082,5.673c-2.715-.423-4.707,2.1-4.707,4.682V40.8c0,2.578,1.992,4.06,4.707,4.682l21.194,3.972,21.194-3.985c2.715-.623,4.707-2.1,4.707-4.682V10.356C55.178,7.778,53.186,5.25,50.471,5.673Zm0,35.117L31.643,44.775V14.328l18.828-3.985Zm-23.56,3.985L8.082,40.79V10.356l18.828,3.985Z"
              transform="translate(179.625 759.373)"
              fill="#5189d3"
            />
            <text
              id="xxxxxxxxxxxxxxxxx_Xxxxxxxxxxxxxxxxx"
              data-name="xxxxxxxxxxxxxxxxx
Xxxxxxxxxxxxxxxxx"
              transform="translate(120 915)"
              fill="#697a98"
              font-size="20"
              font-family="Poppins-Medium, Poppins"
              font-weight="500"
              letter-spacing="0.02em"
            >
              <tspan x="0" y="0">xxxxxxxxxxxxxxxxx</tspan>
              <tspan x="0" y="30">Xxxxxxxxxxxxxxxxx</tspan>
            </text>
            <text
              id="Reviews"
              transform="translate(484 879)"
              fill="#19335a"
              font-size="20"
              font-family="Poppins-Medium, Poppins"
              font-weight="500"
              letter-spacing="0.02em"
            >
              <tspan x="0" y="0">Reviews</tspan>
            </text>
            <circle
              id="Ellipse_32-2"
              data-name="Ellipse 32"
              cx="12"
              cy="12"
              r="12"
              transform="translate(534 752)"
              fill="rgba(254,107,116,0.4)"
            />
            <path
              id="Icon_ionic-md-book-2"
              data-name="Icon ionic-md-book"
              d="M50.471,5.673,29.277,9.658,8.082,5.673c-2.715-.423-4.707,2.1-4.707,4.682V40.8c0,2.578,1.992,4.06,4.707,4.682l21.194,3.972,21.194-3.985c2.715-.623,4.707-2.1,4.707-4.682V10.356C55.178,7.778,53.186,5.25,50.471,5.673Zm0,35.117L31.643,44.775V14.328l18.828-3.985Zm-23.56,3.985L8.082,40.79V10.356l18.828,3.985Z"
              transform="translate(495.625 758.373)"
              fill="#fe6b74"
            />
            <text
              id="xxxxxxxxxxxxxxxxx_Xxxxxxxxxxxxxxxxx-2"
              data-name="xxxxxxxxxxxxxxxxx
Xxxxxxxxxxxxxxxxx"
              transform="translate(436 914)"
              fill="#697a98"
              font-size="20"
              font-family="Poppins-Medium, Poppins"
              font-weight="500"
              letter-spacing="0.02em"
            >
              <tspan x="0" y="0">xxxxxxxxxxxxxxxxx</tspan>
              <tspan x="0" y="30">Xxxxxxxxxxxxxxxxx</tspan>
            </text>
            <text
              id="Comments"
              transform="translate(783 880)"
              fill="#19335a"
              font-size="20"
              font-family="Poppins-Medium, Poppins"
              font-weight="500"
              letter-spacing="0.02em"
            >
              <tspan x="0" y="0">Comments</tspan>
            </text>
            <circle
              id="Ellipse_32-3"
              data-name="Ellipse 32"
              cx="12"
              cy="12"
              r="12"
              transform="translate(849 753)"
              fill="rgba(81,207,205,0.4)"
            />
            <path
              id="Icon_ionic-md-book-3"
              data-name="Icon ionic-md-book"
              d="M50.471,5.673,29.277,9.658,8.082,5.673c-2.715-.423-4.707,2.1-4.707,4.682V40.8c0,2.578,1.992,4.06,4.707,4.682l21.194,3.972,21.194-3.985c2.715-.623,4.707-2.1,4.707-4.682V10.356C55.178,7.778,53.186,5.25,50.471,5.673Zm0,35.117L31.643,44.775V14.328l18.828-3.985Zm-23.56,3.985L8.082,40.79V10.356l18.828,3.985Z"
              transform="translate(810.625 759.373)"
              fill="#02bbb6"
            />
            <text
              id="xxxxxxxxxxxxxxxxx_Xxxxxxxxxxxxxxxxx-3"
              data-name="xxxxxxxxxxxxxxxxx
Xxxxxxxxxxxxxxxxx"
              transform="translate(751 915)"
              fill="#697a98"
              font-size="20"
              font-family="Poppins-Medium, Poppins"
              font-weight="500"
              letter-spacing="0.02em"
            >
              <tspan x="0" y="0">xxxxxxxxxxxxxxxxx</tspan>
              <tspan x="0" y="30">Xxxxxxxxxxxxxxxxx</tspan>
            </text>
            <text
              id="To_Improve"
              data-name="To Improve"
              transform="translate(1098 880)"
              fill="#19335a"
              font-size="20"
              font-family="Poppins-Medium, Poppins"
              font-weight="500"
              letter-spacing="0.02em"
            >
              <tspan x="0" y="0">To Improve</tspan>
            </text>
            <circle
              id="Ellipse_32-4"
              data-name="Ellipse 32"
              cx="12"
              cy="12"
              r="12"
              transform="translate(1165 753)"
              fill="rgba(254,211,99,0.33)"
            />
            <path
              id="Icon_ionic-md-book-4"
              data-name="Icon ionic-md-book"
              d="M50.471,5.673,29.277,9.658,8.082,5.673c-2.715-.423-4.707,2.1-4.707,4.682V40.8c0,2.578,1.992,4.06,4.707,4.682l21.194,3.972,21.194-3.985c2.715-.623,4.707-2.1,4.707-4.682V10.356C55.178,7.778,53.186,5.25,50.471,5.673Zm0,35.117L31.643,44.775V14.328l18.828-3.985Zm-23.56,3.985L8.082,40.79V10.356l18.828,3.985Z"
              transform="translate(1126.625 759.373)"
              fill="#fed363"
            />
            <text
              id="xxxxxxxxxxxxxxxxx_Xxxxxxxxxxxxxxxxx-4"
              data-name="xxxxxxxxxxxxxxxxx
Xxxxxxxxxxxxxxxxx"
              transform="translate(1067 915)"
              fill="#697a98"
              font-size="20"
              font-family="Poppins-Medium, Poppins"
              font-weight="500"
              letter-spacing="0.02em"
            >
              <tspan x="0" y="0">xxxxxxxxxxxxxxxxx</tspan>
              <tspan x="0" y="30">Xxxxxxxxxxxxxxxxx</tspan>
            </text>
          </g>
        </svg>
      </div>
    </section>
  </div>
</template>

<style scoped>
#item-1 {
  grid-row-start: 1;
  grid-column-start: 1;
  grid-row-end: 2;
  grid-column-end: 3;
  width: 320px;
  height: 120px;
  background: #ffffff 0% 0% no-repeat padding-box;
  box-shadow: 0px 0px 20px #457aef26;
  border-radius: 20px;
  opacity: 1;
  margin-left: -30px;
  /* border: 1px solid red; */
}
#item-2 {
  grid-row-start: 2;
  grid-column-start: 1;
  grid-row-end: 3;
  grid-column-end: 3;
  width: 320px;
  height: 120px;
  background: #ffffff 0% 0% no-repeat padding-box;
  box-shadow: 0px 0px 20px #457aef26;
  border-radius: 20px;
  opacity: 1;
  margin-left: -30px;
}
#item-3 {
  grid-row-start: 1;
  grid-column-start: 3;
  grid-row-end: 3;
  grid-column-end: 6;
  width: 900px;
  height: 270px;
  border-radius: 20px;
  opacity: 1;
  margin-top: -51px;
  margin-left: -5%;
}

#item-1 .item-name,
#item-2 .item-name {
  position: absolute;
  letter-spacing: 0.18px;
  opacity: 1;
  font: normal normal medium 18px/27px Poppins;
  font-family: Poppins;
  font-size: 18px;
  font-weight: 400;
  margin-top: 22px;
}

#item-1 .item-number,
#item-2 .item-number {
  position: absolute;
  font: normal normal 550 40px/60px Poppins;
  letter-spacing: 1.2px;
  color: #19335a;
  opacity: 1;
  margin-top: 45px;
}

#item-1 p,
#item-2 p {
  margin-left: 52px;
  /* margin-top:30px; */
}

#item-3 img {
  width: 340px;
  float: right;
  margin-right: 50px;
  margin-top: -40px;
}

.box-icon {
  width: 58px;
  height: 58px;
  border-radius: 15px;
  opacity: 1;
  float: right;
  margin-top: 10%;
  margin-bottom: 10%;
  margin-right: 8%;
}

.box-icon img {
  height: 35px;
  width: 35px;
  margin: auto auto;
  margin-top: 20%;
  margin-bottom: 20%;
  display: block;
}

.box-line {
  width: 8px;
  height: 75px;
  border-radius: 15px;
  float: left;
  margin-left: 8%;
  margin-top: 22px;
  margin-bottom: 22px;
}

.feature{
margin-left: 50px;
margin-top: 60px;
margin-bottom: 150px;
}
</style>
