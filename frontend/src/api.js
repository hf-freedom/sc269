import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8006/api',
  timeout: 10000
})

export default {
  getStudents: () => api.get('/students'),
  createStudent: (data) => api.post('/students', data),
  graduateStudent: (id) => api.post(`/students/${id}/graduate`),
  graduateStudentsByGrade: (grade) => api.post(`/students/graduate/grade/${grade}`),

  getDormitories: () => api.get('/dormitories'),
  createDormitory: (data) => api.post('/dormitories', data),
  startMaintenance: (data) => api.post('/maintenance/start', data),
  endMaintenance: (id) => api.post(`/maintenance/${id}/end`),

  getAllocations: () => api.get('/allocations'),
  applyAllocation: (data) => api.post('/allocations', data),
  processAllocation: (id) => api.post(`/allocations/${id}/process`),

  getTransfers: () => api.get('/transfers'),
  applyTransfer: (data) => api.post('/transfers', data),
  processTransfer: (id) => api.post(`/transfers/${id}/process`),

  getViolations: () => api.get('/violations'),
  recordViolation: (data) => api.post('/violations', data),

  getStatistics: () => api.get('/statistics')
}