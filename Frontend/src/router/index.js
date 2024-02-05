import { createRouter, createWebHistory } from 'vue-router'
import Dashboard from '../views/Dashboard.vue'
import Summary from '../views/Summary.vue'
import Review from '../views/Review.vue'
import ReviewDetail from '../components/ReviewDetail.vue'
import login from '../views/Login.vue'
import AddReview from '../components/AddReview.vue'
import EditReview from '../components/EditReview.vue'
import AddSummary from '../components/AddSummary.vue'
import SummaryDetail from '../components/SummaryDetail.vue'
import EditSummary from '../components/EditSummary.vue'
import Report from'../views/Report.vue'
import Mycategory from '../components/Mycategory.vue'

const history = createWebHistory('/sy2/')

const routes = [
  
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true } 
  },
  {
    path: '/summary',
    name: 'Summary',
    component: Summary,
    meta: { requiresAuth: true } 
  },
  {
    path: '/review',
    name: 'Review',
    component: Review,
    meta: { requiresAuth: true } 
  },
  {
    path: '/course-review/:courseid',
    name: 'ReviewDetail',
    component: ReviewDetail,
    meta: { requiresAuth: true } 
  },
  {
    path: '/',
    name: 'login',
    component: login,
    meta: { requiresAuth: false } 
  },
  {
    path: '/add-review',
    name: 'AddReview',
    component: AddReview,
    meta: { requiresAuth: true } 
  },
  {
    path: '/edit-review/:reviewid',
    name: 'EditReview',
    component: EditReview,
    meta: { requiresAuth: true } 
  },
  {
    path: '/add-summary',
    name: 'AddSummary',
    component: AddSummary,
    meta: { requiresAuth: true } 
  },
  {
    path: '/edit-summary/:summaryid',
    name: 'EditSummary',
    component: EditSummary,
    meta: { requiresAuth: true } 
  },
  {
    path: '/course-summary/:courseid',
    name: 'SummaryDetail',
    component: SummaryDetail,
    meta: { requiresAuth: true } 
  },
  {
    path: '/report',
    name: 'Report',
    component: Report,
    meta: { requiresAuth: true } 
  },
  {
    path: '/my-category',
    name: 'Mycategory',
    component: Mycategory,
    meta: { requiresAuth: true } 
  },

]
const router = createRouter({ history, routes ,linkActiveClass: 'active_nav'})


// router.beforeEach((to, from, next) => {
//   const isAuthenticated = localStorage.getItem("token") !== null;

//   if (to.path === '/' && isAuthenticated) {
//     // ถ้ามีการเข้าสู่ระบบแล้ว ให้ไปที่หน้าปัจจุบันที่อยู่
//     next({ name: from.name || 'Dashboard' }); // ถ้าไม่มีหน้าปัจจุบันก็ให้ไปที่หน้า Dashboard
//   } else if (to.matched.some(record => record.meta.requiresAuth)) {
//     // ถ้าหน้านี้ต้องการการเข้าสู่ระบบ
//     if (!isAuthenticated) {
//       next({ name: 'login' });
//     } else {
//       next();
//     }
//   } else {
//     next();
//   }
// });


router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem("token") !== null;
  const userRole = localStorage.getItem("role");

  if (to.path === '/' && isAuthenticated) {
    next({ name: from.name || 'Dashboard' });
  } else if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isAuthenticated) {
      next({ name: 'login' });
    } else {
      if (userRole === 'staff_group' && restrictedRoutesForAdmin.includes(to.name)) {
        next({ name: 'Dashboard' }); 
      } else if (userRole === 'st_group' && restrictedRoutesForStudent.includes(to.name)) {
        next({ name: 'Dashboard' });
      } else {
        next();
      }
    }
  } else {
    next();
  }
});

const restrictedRoutesForAdmin = ['Mycategory', 'AddReview', 'AddSummary', 'EditReview', 'EditSummary'];
const restrictedRoutesForStudent = ['Report'];


export default router
