<template>
  <div>
    <h2 style="margin-bottom: 20px">违规记录</h2>
    <el-button type="primary" @click="showAddDialog = true">记录违规</el-button>
    <el-table :data="violations" style="width: 100%; margin-top: 20px" border>
      <el-table-column prop="studentId" label="学生ID" width="150" />
      <el-table-column prop="dormitoryId" label="宿舍ID" width="150" />
      <el-table-column prop="violationType" label="违规类型" width="150" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="violationTime" label="违规时间" width="180" />
    </el-table>

    <el-dialog v-model="showAddDialog" title="记录违规" width="500px">
      <el-form :model="newViolation" label-width="120px">
        <el-form-item label="选择学生">
          <el-select v-model="newViolation.studentId" style="width: 100%">
            <el-option v-for="s in allocatedStudents" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="违规类型">
          <el-input v-model="newViolation.violationType" />
        </el-form-item>
        <el-form-item label="违规描述">
          <el-input v-model="newViolation.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addViolation">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api.js'

const violations = ref([])
const students = ref([])
const showAddDialog = ref(false)
const newViolation = ref({
  studentId: '',
  violationType: '',
  description: ''
})

const allocatedStudents = ref([])

const loadViolations = async () => {
  try {
    const res = await api.getViolations()
    violations.value = res.data
  } catch (e) {
    ElMessage.error('加载违规记录失败')
  }
}

const loadStudents = async () => {
  try {
    const res = await api.getStudents()
    students.value = res.data
    allocatedStudents.value = res.data.filter(s => s.dormitoryId)
  } catch (e) {
    ElMessage.error('加载学生列表失败')
  }
}

const addViolation = async () => {
  try {
    const student = allocatedStudents.value.find(s => s.id === newViolation.value.studentId)
    const data = { ...newViolation.value, dormitoryId: student?.dormitoryId, violationTime: new Date().toISOString() }
    await api.recordViolation(data)
    ElMessage.success('违规记录已提交，该学生将无法优先申请宿舍')
    showAddDialog.value = false
    newViolation.value = { studentId: '', violationType: '', description: '' }
    loadViolations()
    loadStudents()
  } catch (e) {
    ElMessage.error('提交失败')
  }
}

onMounted(() => {
  loadViolations()
  loadStudents()
})
</script>