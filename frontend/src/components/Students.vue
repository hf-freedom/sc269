<template>
  <div>
    <h2 style="margin-bottom: 20px">学生管理</h2>
    <el-row :gutter="15" style="margin-bottom: 20px">
      <el-col :span="3">
        <el-button type="primary" @click="showAddDialog = true">添加学生</el-button>
      </el-col>
      <el-col :span="6">
        <el-select v-model="filterCollege" placeholder="按学院筛选" clearable style="width: 100%" @change="applyFilters">
          <el-option v-for="c in colleges" :key="c" :label="c" :value="c" />
        </el-select>
      </el-col>
      <el-col :span="5">
        <el-select v-model="filterGrade" placeholder="按年级筛选" clearable style="width: 100%" @change="applyFilters">
          <el-option v-for="g in grades" :key="g" :label="g" :value="g" />
        </el-select>
      </el-col>
      <el-col :span="5">
        <el-select v-model="filterDormitory" placeholder="按宿舍筛选" clearable style="width: 100%" @change="applyFilters">
          <el-option v-for="d in dormitories" :key="d.id" :label="d.buildingNo + '-' + d.roomNo" :value="d.id" />
        </el-select>
      </el-col>
    </el-row>

    <el-table :data="filteredStudents" style="width: 100%" border>
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="studentNo" label="学号" width="120" />
      <el-table-column prop="college" label="学院" width="130" />
      <el-table-column prop="grade" label="年级" width="80" />
      <el-table-column prop="gender" label="性别" width="70" />
      <el-table-column prop="className" label="班级" width="100" />
      <el-table-column prop="dormitory" label="宿舍信息" width="180">
        <template #default="scope">
          <div v-if="getDormitoryName(scope.row.dormitoryId)">
            <el-tag type="success">{{ getDormitoryName(scope.row.dormitoryId) }}</el-tag>
          </div>
          <el-tag v-else type="info">未分配</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="hasViolation" label="违规状态" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.hasViolation ? 'danger' : 'success'" size="small">
            {{ scope.row.hasViolation ? '有违规' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="130">
        <template #default="scope">
          <el-button v-if="scope.row.dormitoryId" size="small" type="danger" @click="confirmGraduate(scope.row)">毕业离宿</el-button>
          <span v-else style="color: #909399; font-size: 12px">无宿舍</span>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showAddDialog" title="添加学生" width="500px">
      <el-form :model="newStudent" label-width="100px">
        <el-form-item label="姓名">
          <el-input v-model="newStudent.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="newStudent.studentNo" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="newStudent.college" placeholder="请输入学院" />
        </el-form-item>
        <el-form-item label="年级">
          <el-input v-model="newStudent.grade" placeholder="请输入年级" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="newStudent.gender" placeholder="请选择性别" style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="班级">
          <el-input v-model="newStudent.className" placeholder="请输入班级" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addStudent">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showGraduateDialog" title="毕业离宿确认" width="550px">
      <el-alert type="warning" :closable="false" show-icon style="margin-bottom: 20px">
        毕业生将自动释放宿舍床位，请确认以下信息
      </el-alert>
      
      <el-descriptions :column="2" border style="margin-bottom: 20px">
        <el-descriptions-item label="学生姓名">
          <span style="font-weight: bold">{{ graduatingStudent.name }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="学号">
          {{ graduatingStudent.studentNo }}
        </el-descriptions-item>
        <el-descriptions-item label="学院班级">
          {{ graduatingStudent.college }} {{ graduatingStudent.className }}
        </el-descriptions-item>
        <el-descriptions-item label="原宿舍">
          <el-tag type="success">{{ getDormitoryName(graduatingStudent.dormitoryId) }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">床位释放结果预览</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="原宿舍">
          {{ getDormitoryName(graduatingStudent.dormitoryId) }}
        </el-descriptions-item>
        <el-descriptions-item label="释放床位数">
          <el-tag type="success">1个</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="原宿舍人数">
          {{ getDormitoryOccupied(graduatingStudent.dormitoryId) }}人
        </el-descriptions-item>
        <el-descriptions-item label="释放后剩余">
          <el-tag type="info">{{ getDormitoryOccupied(graduatingStudent.dormitoryId) - 1 }}人</el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="showGraduateDialog = false">取消</el-button>
        <el-button type="primary" @click="executeGraduate">确认毕业离宿</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../api.js'

const students = ref([])
const dormitories = ref([])
const showAddDialog = ref(false)
const showGraduateDialog = ref(false)
const filterCollege = ref('')
const filterGrade = ref('')
const filterDormitory = ref('')

const newStudent = ref({
  name: '',
  studentNo: '',
  college: '',
  grade: '',
  gender: '',
  className: ''
})

const graduatingStudent = reactive({
  id: '',
  name: '',
  studentNo: '',
  college: '',
  className: '',
  dormitoryId: ''
})

const colleges = computed(() => {
  const set = new Set(students.value.map(s => s.college))
  return Array.from(set).filter(c => c)
})

const grades = computed(() => {
  const set = new Set(students.value.map(s => s.grade))
  return Array.from(set).filter(g => g)
})

const filteredStudents = computed(() => {
  return students.value.filter(s => {
    if (filterCollege.value && s.college !== filterCollege.value) return false
    if (filterGrade.value && s.grade !== filterGrade.value) return false
    if (filterDormitory.value && s.dormitoryId !== filterDormitory.value) return false
    return true
  })
})

const applyFilters = () => {}

const getDormitoryName = (dormitoryId) => {
  if (!dormitoryId) return null
  const dorm = dormitories.value.find(d => d.id === dormitoryId)
  return dorm ? `${dorm.buildingNo}-${dorm.roomNo}` : null
}

const getDormitoryOccupied = (dormitoryId) => {
  if (!dormitoryId) return 0
  const dorm = dormitories.value.find(d => d.id === dormitoryId)
  return dorm ? dorm.occupiedBeds : 0
}

const loadStudents = async () => {
  try {
    const res = await api.getStudents()
    students.value = res.data
  } catch (e) {
    ElMessage.error('加载学生列表失败')
  }
}

const loadDormitories = async () => {
  try {
    const res = await api.getDormitories()
    dormitories.value = res.data
  } catch (e) {
    ElMessage.error('加载宿舍列表失败')
  }
}

const addStudent = async () => {
  try {
    await api.createStudent(newStudent.value)
    ElMessage.success('添加成功')
    showAddDialog.value = false
    newStudent.value = { name: '', studentNo: '', college: '', grade: '', gender: '', className: '' }
    loadStudents()
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

const confirmGraduate = (student) => {
  Object.assign(graduatingStudent, {
    id: student.id,
    name: student.name,
    studentNo: student.studentNo,
    college: student.college,
    className: student.className,
    dormitoryId: student.dormitoryId
  })
  showGraduateDialog.value = true
}

const executeGraduate = async () => {
  try {
    const dormName = getDormitoryName(graduatingStudent.dormitoryId)
    await api.graduateStudent(graduatingStudent.id)
    ElMessage.success(`${graduatingStudent.name} 毕业离宿成功，${dormName} 床位已释放`)
    showGraduateDialog.value = false
    loadStudents()
    loadDormitories()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadStudents()
  loadDormitories()
})
</script>