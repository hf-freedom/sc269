<template>
  <div>
    <h2 style="margin-bottom: 20px">调宿申请</h2>
    <el-button type="primary" @click="showAddDialog = true">提交申请</el-button>
    <el-table :data="transfers" style="width: 100%; margin-top: 20px" border>
      <el-table-column prop="studentId" label="学生ID" width="100" />
      <el-table-column prop="studentName" label="学生姓名" width="100">
        <template #default="scope">
          {{ getStudentName(scope.row.studentId) }}
        </template>
      </el-table-column>
      <el-table-column prop="fromDormitory" label="原宿舍" width="150">
        <template #default="scope">
          {{ getDormitoryName(scope.row.fromDormitoryId) }}
        </template>
      </el-table-column>
      <el-table-column prop="toDormitory" label="目标宿舍" width="150">
        <template #default="scope">
          {{ getDormitoryName(scope.row.toDormitoryId) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="reason" label="原因" width="150" />
      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-button v-if="scope.row.status === 'PENDING'" size="small" type="success" @click="process(scope.row.id)">处理</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showAddDialog" title="提交调宿申请" width="700px">
      <el-form :model="newTransfer" label-width="100px">
        <el-form-item label="选择学生">
          <el-select v-model="newTransfer.studentId" style="width: 100%" @change="onStudentChange" placeholder="请选择调宿学生">
            <el-option v-for="s in allocatedStudents" :key="s.id" :value="s.id">
              <span>{{ s.name }}</span>
              <span style="margin-left: 10px; color: #909399; font-size: 12px">
                原宿舍: {{ getDormitoryName(s.dormitoryId) }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-divider content-position="left">原宿舍信息</el-divider>
        <el-descriptions :column="3" border size="small" style="margin-bottom: 15px">
          <el-descriptions-item label="宿舍名称">
            <el-tag type="info">{{ fromDormitoryInfo.name || '-' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="床位状态">
            <el-tag :type="fromDormitoryInfo.bedCheck.pass ? 'success' : 'danger'">
              {{ fromDormitoryInfo.bedCheck.message }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="维修状态">
            <el-tag :type="fromDormitoryInfo.maintenanceCheck.pass ? 'success' : 'danger'">
              {{ fromDormitoryInfo.maintenanceCheck.message }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <el-form-item label="目标宿舍">
          <el-select v-model="newTransfer.toDormitoryId" style="width: 100%" @change="onDormitoryChange" placeholder="请选择目标宿舍">
            <el-option v-for="d in availableDormitories" :key="d.id" :value="d.id">
            <span>{{ d.buildingNo }}-{{ d.roomNo }}</span>
            <span style="margin-left: 10px; color: #909399; font-size: 12px">
              {{ d.gender }}生 | {{ d.college }} | {{ d.grade }}级 | 剩余{{ d.capacity - d.occupiedBeds }}床位
            </span>
          </el-option>
          </el-select>
        </el-form-item>

        <el-divider content-position="left">目标宿舍校验结果</el-divider>
        <el-alert :type="toDormitoryInfo.overallCheck.pass ? 'success' : 'error'" :title="toDormitoryInfo.overallCheck.message" :closable="false" style="margin-bottom: 15px">
          <template v-if="newTransfer.toDormitoryId">
            <el-descriptions :column="2" border size="small">
              <el-descriptions-item label="性别规则">
                <el-tag :type="toDormitoryInfo.genderCheck.pass ? 'success' : 'danger'">
                  {{ toDormitoryInfo.genderCheck.message }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="床位状态">
                <el-tag :type="toDormitoryInfo.bedCheck.pass ? 'success' : 'danger'">
                  {{ toDormitoryInfo.bedCheck.message }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="维修状态">
                <el-tag :type="toDormitoryInfo.maintenanceCheck.pass ? 'success' : 'danger'">
                  {{ toDormitoryInfo.maintenanceCheck.message }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="学院年级规则">
                <el-tag :type="toDormitoryInfo.groupCheck.pass ? 'success' : 'warning'">
                  {{ toDormitoryInfo.groupCheck.message }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </template>
          <span v-else style="color: #909399">请先选择目标宿舍</span>
        </el-alert>

        <el-form-item label="调宿原因">
          <el-input v-model="newTransfer.reason" type="textarea" :rows="3" placeholder="请输入调宿原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addTransfer" :disabled="!canSubmit">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api.js'

const transfers = ref([])
const students = ref([])
const dormitories = ref([])
const showAddDialog = ref(false)
const newTransfer = ref({
  studentId: '',
  toDormitoryId: '',
  reason: ''
})

const allocatedStudents = ref([])
const availableDormitories = ref([])
const selectedStudent = ref(null)

const fromDormitoryInfo = reactive({
  name: '',
  bedCheck: { pass: true, message: '正常' },
  maintenanceCheck: { pass: true, message: '正常' }
})

const toDormitoryInfo = reactive({
  overallCheck: { pass: false, message: '请选择目标宿舍' },
  genderCheck: { pass: false, message: '未校验' },
  bedCheck: { pass: false, message: '未校验' },
  maintenanceCheck: { pass: false, message: '未校验' },
  groupCheck: { pass: false, message: '未校验' }
})

const canSubmit = computed(() => {
  return newTransfer.value.studentId && 
         newTransfer.value.toDormitoryId && 
         toDormitoryInfo.overallCheck.pass
})

const getStudentName = (studentId) => {
  const student = students.value.find(s => s.id === studentId)
  return student ? student.name : '-'
}

const getDormitoryName = (dormitoryId) => {
  const dorm = dormitories.value.find(d => d.id === dormitoryId)
  return dorm ? `${dorm.buildingNo}-${dorm.roomNo}` : '-'
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
  selectedStudent.value = students.value.find(s => s.id === studentId)
  if (selectedStudent.value) {
    const fromDorm = dormitories.value.find(d => d.id === selectedStudent.value.dormitoryId)
    if (fromDorm) {
      fromDormitoryInfo.name = `${fromDorm.buildingNo}-${fromDorm.roomNo}`
      fromDormitoryInfo.bedCheck = {
        pass: fromDorm.occupiedBeds > 0,
        message: fromDorm.occupiedBeds > 0 ? `当前${fromDorm.occupiedBeds}人入住，迁出后剩余${fromDorm.occupiedBeds - 1}床位` : '床位状态异常'
      }
      fromDormitoryInfo.maintenanceCheck = {
        pass: !fromDorm.underMaintenance,
        message: fromDorm.underMaintenance ? '原宿舍正在维修' : '正常'
      }
    }
    if (newTransfer.value.toDormitoryId) {
      validateToDormitory()
    }
  }
}

const onDormitoryChange = () => {
  validateToDormitory()
}

const validateToDormitory = () => {
  if (!newTransfer.value.toDormitoryId || !selectedStudent.value) {
    return
  }

  const toDorm = dormitories.value.find(d => d.id === newTransfer.value.toDormitoryId)
  if (!toDorm) return

  const student = selectedStudent.value

  toDormitoryInfo.genderCheck = {
    pass: toDorm.gender === student.gender,
    message: toDorm.gender === student.gender 
      ? `性别匹配(${student.gender}生宿舍)` 
      : `性别不匹配(目标为${toDorm.gender}生宿舍)`
  }

  toDormitoryInfo.bedCheck = {
    pass: toDorm.occupiedBeds < toDorm.capacity,
    message: toDorm.occupiedBeds < toDorm.capacity
      ? `床位充足(剩余${toDorm.capacity - toDorm.occupiedBeds}/${toDorm.capacity}床位)`
      : `床位已满(${toDorm.occupiedBeds}/${toDorm.capacity})`
  }

  toDormitoryInfo.maintenanceCheck = {
    pass: !toDorm.underMaintenance,
    message: !toDorm.underMaintenance ? '正常(未维修)' : '宿舍正在维修中'
  }

  const collegeMatch = toDorm.college === student.college
  const gradeMatch = toDorm.grade === student.grade
  toDormitoryInfo.groupCheck = {
    pass: collegeMatch && gradeMatch,
    message: collegeMatch && gradeMatch 
      ? `学院年级匹配(${student.college} ${student.grade}级)`
      : collegeMatch 
        ? `学院匹配，年级不匹配(目标${toDorm.grade}级)`
        : gradeMatch
          ? `年级匹配，学院不匹配(目标${toDorm.college})`
          : `学院年级均不匹配`
  }

  const allPass = toDormitoryInfo.genderCheck.pass && 
                   toDormitoryInfo.bedCheck.pass && 
                   toDormitoryInfo.maintenanceCheck.pass

  toDormitoryInfo.overallCheck = {
    pass: allPass,
    message: allPass ? '校验通过，可以调宿' : '校验未通过，无法调宿'
  }
}

const loadTransfers = async () => {
  try {
    const res = await api.getTransfers()
    transfers.value = res.data
  } catch (e) {
    ElMessage.error('加载申请列表失败')
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

const loadDormitories = async () => {
  try {
    const res = await api.getDormitories()
    dormitories.value = res.data
    availableDormitories.value = res.data.filter(d => !d.underMaintenance && d.occupiedBeds < d.capacity)
  } catch (e) {
    ElMessage.error('加载宿舍列表失败')
  }
}

const addTransfer = async () => {
  try {
    await api.applyTransfer(newTransfer.value)
    ElMessage.success('申请提交成功')
    showAddDialog.value = false
    newTransfer.value = { studentId: '', toDormitoryId: '', reason: '' }
    selectedStudent.value = null
    resetValidation()
    loadTransfers()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '提交失败')
  }
}

const resetValidation = () => {
  fromDormitoryInfo.name = ''
  fromDormitoryInfo.bedCheck = { pass: true, message: '正常' }
  fromDormitoryInfo.maintenanceCheck = { pass: true, message: '正常' }
  toDormitoryInfo.overallCheck = { pass: false, message: '请选择目标宿舍' }
  toDormitoryInfo.genderCheck = { pass: false, message: '未校验' }
  toDormitoryInfo.bedCheck = { pass: false, message: '未校验' }
  toDormitoryInfo.maintenanceCheck = { pass: false, message: '未校验' }
  toDormitoryInfo.groupCheck = { pass: false, message: '未校验' }
}

const process = async (id) => {
  try {
    await api.processTransfer(id)
    ElMessage.success('处理完成')
    loadTransfers()
    loadStudents()
    loadDormitories()
  } catch (e) {
    ElMessage.error('处理失败')
  }
}

onMounted(() => {
  loadTransfers()
  loadStudents()
  loadDormitories()
})
</script>