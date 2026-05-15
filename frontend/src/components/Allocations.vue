<template>
  <div>
    <h2 style="margin-bottom: 20px">分配申请</h2>
    <el-button type="primary" @click="showAddDialog = true">提交申请</el-button>
    <el-table :data="allocations" style="width: 100%; margin-top: 20px" border>
      <el-table-column prop="studentId" label="学生ID" width="120" />
      <el-table-column prop="studentName" label="学生姓名" width="100">
        <template #default="scope">
          {{ getStudentName(scope.row.studentId) }}
        </template>
      </el-table-column>
      <el-table-column prop="college" label="学院" width="120">
        <template #default="scope">
          {{ getStudentCollege(scope.row.studentId) }}
        </template>
      </el-table-column>
      <el-table-column prop="grade" label="年级" width="80">
        <template #default="scope">
          {{ getStudentGrade(scope.row.studentId) }}
        </template>
      </el-table-column>
      <el-table-column prop="gender" label="性别" width="80">
        <template #default="scope">
          {{ getStudentGender(scope.row.studentId) }}
        </template>
      </el-table-column>
      <el-table-column prop="preferredBuilding" label="偏好楼栋" width="100" />
      <el-table-column prop="preferredRoomType" label="偏好房型" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="allocatedDormitoryId" label="分配宿舍ID" width="150" />
      <el-table-column prop="remarks" label="备注" width="150" />
      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-button v-if="scope.row.status === 'PENDING'" size="small" type="success" @click="process(scope.row.id)">处理</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showAddDialog" title="提交分配申请" width="600px">
      <el-form :model="newAllocation" label-width="100px">
        <el-form-item label="选择学生">
          <el-select v-model="newAllocation.studentId" style="width: 100%" @change="onStudentChange">
            <el-option v-for="s in unallocatedStudents" :key="s.id" :value="s.id">
              <span>{{ s.name }}</span>
              <span style="margin-left: 10px; color: #909399; font-size: 12px">
                {{ s.college }} | {{ s.grade }}级 | {{ s.gender }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-divider content-position="left">分组信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="学院">
              <el-input :value="selectedStudentInfo.college" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="年级">
              <el-input :value="selectedStudentInfo.grade" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="性别">
              <el-input :value="selectedStudentInfo.gender" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">偏好设置</el-divider>
        <el-form-item label="偏好楼栋">
          <el-input v-model="newAllocation.preferredBuilding" placeholder="请输入偏好楼栋" />
        </el-form-item>
        <el-form-item label="偏好房型">
          <el-select v-model="newAllocation.preferredRoomType" style="width: 100%" placeholder="请选择偏好房型">
            <el-option label="四人间" value="四人间" />
            <el-option label="六人间" value="六人间" />
            <el-option label="八人间" value="八人间" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addAllocation" :disabled="!newAllocation.studentId">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api.js'

const allocations = ref([])
const students = ref([])
const showAddDialog = ref(false)
const newAllocation = ref({
  studentId: '',
  preferredBuilding: '',
  preferredRoomType: ''
})

const unallocatedStudents = ref([])

const selectedStudentInfo = reactive({
  name: '',
  college: '',
  grade: '',
  gender: ''
})

const getStudentName = (studentId) => {
  const student = students.value.find(s => s.id === studentId)
  return student ? student.name : '-'
}

const getStudentCollege = (studentId) => {
  const student = students.value.find(s => s.id === studentId)
  return student ? student.college : '-'
}

const getStudentGrade = (studentId) => {
  const student = students.value.find(s => s.id === studentId)
  return student ? student.grade : '-'
}

const getStudentGender = (studentId) => {
  const student = students.value.find(s => s.id === studentId)
  return student ? student.gender : '-'
}

const getStatusType = (status) => {
  const map = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = { PENDING: '待处理', APPROVED: '已通过', REJECTED: '已拒绝' }
  return map[status] || status
}

const onStudentChange = (studentId) => {
  const student = students.value.find(s => s.id === studentId)
  if (student) {
    selectedStudentInfo.name = student.name
    selectedStudentInfo.college = student.college
    selectedStudentInfo.grade = student.grade
    selectedStudentInfo.gender = student.gender
  } else {
    selectedStudentInfo.name = ''
    selectedStudentInfo.college = ''
    selectedStudentInfo.grade = ''
    selectedStudentInfo.gender = ''
  }
}

const loadAllocations = async () => {
  try {
    const res = await api.getAllocations()
    allocations.value = res.data
  } catch (e) {
    ElMessage.error('加载申请列表失败')
  }
}

const loadStudents = async () => {
  try {
    const res = await api.getStudents()
    students.value = res.data
    unallocatedStudents.value = res.data.filter(s => !s.dormitoryId)
  } catch (e) {
    ElMessage.error('加载学生列表失败')
  }
}

const addAllocation = async () => {
  try {
    await api.applyAllocation(newAllocation.value)
    ElMessage.success('申请提交成功')
    showAddDialog.value = false
    newAllocation.value = { studentId: '', preferredBuilding: '', preferredRoomType: '' }
    selectedStudentInfo.name = ''
    selectedStudentInfo.college = ''
    selectedStudentInfo.grade = ''
    selectedStudentInfo.gender = ''
    loadAllocations()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '提交失败')
  }
}

const process = async (id) => {
  try {
    await api.processAllocation(id)
    ElMessage.success('处理完成')
    loadAllocations()
    loadStudents()
  } catch (e) {
    ElMessage.error('处理失败')
  }
}

onMounted(() => {
  loadAllocations()
  loadStudents()
})
</script>