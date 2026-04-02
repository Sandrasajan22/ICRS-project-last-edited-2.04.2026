import axios from "axios";

const API = "http://localhost:8080/api/interviews";

// ================= AUTH HELPER =================
function authHeaders() {
  const token = localStorage.getItem("token");
  return token && token !== "null"
    ? { Authorization: `Bearer ${token}` }
    : {};
}

// ================= STUDENT =================

// GET AVAILABLE SLOTS
export const getAvailableSlots = (studentId) =>
  axios.get(`${API}/student/availability`, {
    params: { studentId: Number(studentId) },
    headers: authHeaders(),
  });

// BOOK SLOT (params, not body)
export const bookSlot = ({ studentId, availabilityId, time }) =>
  axios.post(`${API}/book`, null, {
    params: { studentId: Number(studentId), availabilityId, time },
    headers: authHeaders(),
  });

// PAY
export const payBooking = (id) =>
  axios.put(`${API}/pay/${id}`, null, { headers: authHeaders() });

// STUDENT BOOKINGS
export const getStudentBookings = (studentId) =>
  axios.get(`${API}/student/bookings`, {
    params: { studentId: Number(studentId) },
    headers: authHeaders(),
  });

// ================= MENTOR =================

// MENTOR BOOKINGS
export const getMentorBookings = (mentorId) =>
  axios.get(`${API}/mentor/bookings`, {
    params: { mentorId: Number(mentorId) },
    headers: authHeaders(),
  });

// GET MY CREATED SLOTS
export const getMySlots = (mentorId) =>
  axios.get(`${API}/mentor/availability`, {
    params: { mentorId: Number(mentorId) },
    headers: authHeaders(),
  });

// CREATE SLOT
export const createSlot = (data) =>
  axios.post(`${API}/availability`, data, { headers: authHeaders() });

// ================= SHARE =================

// GET MEETING LINK
export const shareLink = (bookingId) =>
  axios.get(`${API}/share/${bookingId}`, { headers: authHeaders() });

// ================= FEEDBACK =================
const FB_API = "http://localhost:8080/api/interviews/feedback";

// SUBMIT FEEDBACK
export const submitFeedback = (data) =>
  axios.post(`${FB_API}/submit`, data, { headers: authHeaders() });

// GET STUDENT PERFORMANCE
export const getStudentPerformance = (studentId) =>
  axios.get(`${FB_API}/student/${studentId}`, { headers: authHeaders() });

// GET BOOKING FEEDBACK
export const getBookingFeedback = (bookingId) =>
  axios.get(`${FB_API}/booking/${bookingId}`, { headers: authHeaders() });