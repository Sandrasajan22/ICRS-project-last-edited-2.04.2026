import { Routes, Route, Navigate } from "react-router-dom";

import Home from "./Components/Home";
import Login from "./Components/Login";
import Signup from "./Components/Signup";

import PendingVerification from "./pages/common/PendingVerification";
import VerificationUpload from "./pages/common/VerificationUpload";

import ProtectedRoute from "./Components/ProtectedRoute";

// Common
import Feed from "./pages/common/Feed";
import FindPeople from "./pages/FindPeople";
import PublicProfile from "./pages/common/PublicProfile";
import RegisterComplaint from "./pages/common/RegisterComplaint";
import NotificationsPage from "./pages/common/NotificationsPage";


// Resume Builder
import ResumeBuilder from "./pages/common/resume/ResumeBuilder";
//tests
import CommunicationTest from"./pages/common/tests/CommunicationTest";
import ConfidenceTasks from"./pages/common/tests/ConfidenceTasks";
import TechnicalTest from "./pages/common/tests/TechnicalTest";



// Student
import StudentLayout from "./pages/student/StudentLayout";
import StudentViewProfile from "./pages/student/StudentViewProfile";
import StudentUpdateProfile from "./pages/student/StudentUpdateProfile";
import PerformanceDashboard from "./pages/student/PerformanceDashboard";


// Jobseeker
import JobseekerLayout from "./pages/jobseeker/JobseekerLayout";
import JobSeekerViewProfile from "./pages/jobseeker/JobSeekerViewProfile";
import JobseekerUpdateProfile from "./pages/jobseeker/jobseekerupdateprofile";
import Academics from "./pages/common/Academicsview";
import UpdateAcademics from "./pages/common/Academicsupdate";

// Interview Availability and Bookings
import MentorAvailability from "./Components/interviews/MentorAvailability";
import StudentAvailability from "./Components/interviews/StudentAvailability";
import StudentBooked from "./Components/interviews/StudentBooked";
import StudentBookings from "./Components/interviews/StudentBookings";
import MentorFeedbackForm from "./Components/interviews/MentorFeedbackForm";


// Mentor
import MentorLayout from "./pages/mentor/MentorLayout";
import  MentorProfileView from ".//pages/mentor/MentorProfileView";
import  MentorProfileUpdate from ".//pages/mentor/MentorProfileUpdate";
import MentorBookings from "./Components/interviews/MentorBookings";
// Employer
import EmployerLayout from "./pages/employer/EmployerLayout";
import EmployerProfileView from "./pages/employer/EmployerProfileView";
import  EmployerProfileUpdate from ".//pages/employer/EmployerProfileUpdate"


// Institution
import InstitutionLayout from "./pages/training_institution/InstitutionLayout";
import InstitutionProfileView from "./pages/training_institution/InstitutionProfileView";
import InstitutionProfileUpdate from "./pages/training_institution/InstitutionProfileUpdate";
// Admin
import AdminLayout from "./pages/admin/AdminLayout";
import AdminUsers from "./pages/admin/AdminUsers";
import AdminVerification from "./pages/admin/AdminVerification";
import AdminNotification from "./pages/admin/AdminNotification";
import AdminAnalytics from "./pages/admin/AdminAnalytics";
import AdminComplaints from "./pages/admin/AdminComplaints";
import AdminImportModule from "./pages/admin/AdminImportModule";


// Trainer
import TrainerLayout from "./pages/trainer/TrainerLayout";
import ViewProfile from "./pages/trainer/ViewProfile";
import UpdateProfile from "./pages/trainer/UpdateProfile";
import Certifications from "./pages/trainer/Certifications";
import ManageCourses from "./pages/trainer/courses/ManageCourses";
import Post from "./pages/trainer/Post";
import SecuritySettings from "./pages/trainer/Settings";









function App() {
  return (
    <Routes>
      {/* Public */}
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/signup" element={<Signup />} />
      <Route path="/pending-verification" element={<PendingVerification />} />
      <Route path="/verify-account" element={<VerificationUpload />} />

      {/* Shared read-only public routes */}
      <Route path="/feed" element={<Feed />} />
      <Route path="/profile/:userId" element={<PublicProfile />} />

      {/* Student */}
      <Route
        path="/student"
        element={
          <ProtectedRoute role="student">
            <StudentLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="feed" replace />} />
        <Route path="feed" element={<Feed />} />
        <Route path="resume" element={<ResumeBuilder />} />
        <Route path="academics" element={<Academics />} />
        <Route path="academics/update" element={<UpdateAcademics />} />
        <Route path="notifications" element={<NotificationsPage />} />
         {/* Post routes */}
        <Route path="posts" element={<Post />} />
        <Route path="posts/create" element={<Post />} />
        <Route path="posts/:postId" element={<Post />} />
        <Route path="performance" element={<PerformanceDashboard />} />
     <Route path="tasks/confidence" element={<ConfidenceTasks/>} />
     <Route path="tests/technical" element={<TechnicalTest />} />
        <Route path="tests/communication" element={<CommunicationTest />} />
        <Route path="search" element={<FindPeople />} />
        <Route path="student" element={<StudentAvailability />} />
        <Route path="booked" element={<StudentBooked />} />
        <Route path="bookings" element={<StudentBookings />} />
        <Route path="profile" element={<StudentViewProfile />} />
        <Route path="update" element={<StudentUpdateProfile />} />
                <Route path="complaints" element={<RegisterComplaint />} />

        <Route path="settings/security" element={<SecuritySettings />} />
      </Route>

      {/* Jobseeker */}
      <Route
        path="/jobseeker"
        element={
          <ProtectedRoute role="job_seeker">
            <JobseekerLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="feed" replace />} />
        <Route path="feed" element={<Feed />} />
        <Route path="resume" element={<ResumeBuilder />} />
        <Route path="academics" element={<Academics />} />
        <Route path="academics/update" element={<UpdateAcademics />} />
        <Route path="notifications" element={<NotificationsPage />} />

         {/* Post routes */}
        <Route path="posts" element={<Post />} />
     
        <Route path="performance" element={<PerformanceDashboard />} />
        <Route path="tests/technical" element={<TechnicalTest />} />
        <Route path="tests/communication" element={<CommunicationTest />} />
             <Route path="tasks/confidence" element={<ConfidenceTasks />} />

        <Route path="search" element={<FindPeople />} />
        <Route path="student" element={<StudentAvailability />} />
        <Route path="booked" element={<StudentBooked />} />
        <Route path="bookings" element={<StudentBookings />} />
        <Route path="profile" element={<JobSeekerViewProfile />} />
        <Route path="update" element={<JobseekerUpdateProfile />} />
        <Route path="settings/security" element={<SecuritySettings />} />
                <Route path="complaints" element={<RegisterComplaint />} />
                        <Route path="notifications" element={<NotificationsPage />} />


      </Route>

      {/* Mentor */}
      <Route
        path="/mentor"
        element={
          <ProtectedRoute role="mentor">
            <MentorLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="feed" replace />} />
        <Route path="feed" element={<Feed />} />
        <Route path="profile" element={<MentorProfileView />} />
        <Route path="update" element={<MentorProfileUpdate />} />
         <Route path="posts" element={<Post />} />
        <Route path="interviews/booked" element={<MentorBookings />} />
        <Route path="interviews/slot" element={<MentorAvailability />} />
        <Route path="feedback" element={<MentorFeedbackForm />} />
        <Route path="search" element={<FindPeople />} />
        <Route path="settings/security" element={<SecuritySettings />} />
        <Route path="complaints" element={<RegisterComplaint />} />
                <Route path="notifications" element={<NotificationsPage />} />

      </Route>

      {/* Employer */}
      <Route
        path="/employer"
        element={
          <ProtectedRoute role="employer">
            <EmployerLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="feed" replace />} />
        <Route path="feed" element={<Feed />} />
        <Route path="search" element={<FindPeople />} />
        
        {/* Post routes */}
        <Route path="posts" element={<Post />} />
        
        <Route path="profile" element={<EmployerProfileView />} />

        <Route path="update" element={<EmployerProfileUpdate />} />
        <Route path="settings/security" element={<SecuritySettings />} />
        <Route path="complaints" element={<RegisterComplaint />} />
                <Route path="notifications" element={<NotificationsPage />} />

      </Route>

      {/* Institution */}
      <Route
        path="/training_institution"
        element={
          <ProtectedRoute role="institution">
            <InstitutionLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="feed" replace />} />
        <Route path="feed" element={<Feed />} />
        <Route path="profile" element={<InstitutionProfileView />} />
        <Route path="update" element={<InstitutionProfileUpdate />} />
        <Route path="complaints" element={<RegisterComplaint />} />
      
        {/* Post routes */}
        <Route path="posts" element={<Post />} />
        <Route path="posts/create" element={<Post />} />
        <Route path="posts/:postId" element={<Post />} />
       
        <Route path="search" element={<FindPeople />} />
        <Route path="settings/security" element={<SecuritySettings />} />
                <Route path="notifications" element={<NotificationsPage />} />

      </Route>

      {/* Trainer */}
      <Route
        path="/trainer"
        element={
          <ProtectedRoute role="trainer">
            <TrainerLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="feed" replace />} />
        <Route path="feed" element={<Feed />} />
        <Route path="profile" element={<ViewProfile />} />
        <Route path="update" element={<UpdateProfile />} />
        <Route path="certifications" element={<Certifications />} />
        <Route path="courses" element={<ManageCourses />} />

        {/* Post routes */}
        <Route path="posts" element={<Post />} />
        <Route path="posts/create" element={<Post />} />
        <Route path="posts/:postId" element={<Post />} />
        <Route path="notifications" element={<NotificationsPage />} />
        <Route path="search" element={<FindPeople />} />
        <Route path="settings/security" element={<SecuritySettings />} />
        <Route path="complaints" element={<RegisterComplaint />} />
      </Route>

      {/* Admin */}
      <Route
        path="/admin"
        element={
          <ProtectedRoute role="admin">
            <AdminLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="verification" replace />} />
        <Route path="verification" element={<AdminVerification />} />
        <Route path="users" element={<AdminUsers />} />
        <Route path="notifications" element={<AdminNotification />} />
        <Route path="adminimport" element={<AdminImportModule />} />

        <Route path="analytics" element={<AdminAnalytics />} />
        <Route path="complaints" element={<AdminComplaints />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;