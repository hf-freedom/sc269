<template>
  <div>
    <h2 style="margin-bottom: 20px">宿舍管理</h2>
    <el-row :gutter="15" style="margin-bottom: 20px">
      <el-col :span="3">
        <el-button type="primary" @click="showAddDialog = true">添加宿舍</el-button>
      </el-col>
      <el-col :span="4">
        <el-button type="danger" @click="showBatchGraduateDialog = true">批量毕业离宿</el-button>
      </el-col>
    </el-row>
    <el-table :data="dormitories" style="width: 100%" border>
      <el-table-column prop="buildingNo" label="楼栋号" width="100" />
      <el-table-column prop="roomNo" label="房间号" width="100" />
      <el-table-column prop="capacity" label="容量" width="80" />
      <el-table-column prop="occupiedBeds" label="已入住" width="80">
        <template #default="scope">
          <el-progress :percentage="scope.row.occupiedBeds / scope.row.capacity * 100" :format="() => scope.row.occupiedBeds" />
        </template>
      </el-table-column>
      <el-table-column prop="gender" label="性别" width="80" />
      <el-table-column prop="college" label="学院" width="130" />
      <el-table-column prop="grade" label="年级" width="80" />
      <el-table-column prop="status" label="入住状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'AVAILABLE' ? 'success' : 'warning'">
            {{ scope.row.status === 'AVAILABLE' ? '可用' : '已满' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="underMaintenance" label="维修状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.underMaintenance ? 'danger' : 'info'">
            {{ scope.row.underMaintenance ? '维修中' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="scope">
          <el-button size="small" type="primary" @click="viewResidents(scope.row.id)">查看住户</el-button>
          <el-button v-if="!scope.row.underMaintenance" size="small" type="warning" @click="startMaintenance(scope.row.id)">开始维修</el-button>
          <el-button v-else size="small" type="success" @click="endMaintenance(scope.row.id)">结束维修</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showAddDialog" title="添加宿舍" width="500px">
      <el-form :model="newDormitory" label-width="100px">
        <el-form-item label="楼栋号">
          <el-input v-model="newDormitory.buildingNo" placeholder="请输入楼栋号" />
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="newDormitory.roomNo" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="容量">
          <el-input-number v-model="newDormitory.capacity" :min="1" :max="8" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="newDormitory.gender" placeholder="请选择性别" style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="newDormitory.college" placeholder="请输入学院" />
        </el-form-item>
        <el-form-item label="年级">
          <el-input v-model="newDormitory.grade" placeholder="请输入年级" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addDormitory">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showMaintenanceDialog" title="开始维修 - 住户迁移确认" width="700px">
      <el-alert title="开始维修后，该宿舍所有住户将自动迁移到可用的临时房间" type="warning" :closable="false" show-icon style="margin-bottom: 20px" />
      
      <el-descriptions title="待维修宿舍信息" border :column="2" style="margin-bottom: 20px">
        <el-descriptions-item label="宿舍名称">{{ maintenanceDormInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="当前住户">
          <el-tag type="info">{{ maintenanceDormInfo.residentCount }}人</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="性别限制">{{ maintenanceDormInfo.gender }}生</el-descriptions-item>
        <el-descriptions-item label="容量">{{ maintenanceDormInfo.capacity }}人间</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">待迁移住户列表</el-divider>
      <el-table :data="residentsToTransfer" style="width: 100%; margin-bottom: 20px" border size="small">
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="studentNo" label="学号" width="150" />
        <el-table-column prop="college" label="学院" />
        <el-table-column prop="grade" label="年级" width="100" />
        <el-table-column prop="className" label="班级" width="120" />
      </el-table>

      <el-divider content-position="left">可用临时房间</el-divider>
      <el-alert v-if="availableTempDorms.length === 0" type="error" title="没有可用的临时房间，无法进行迁移！" :closable="false" show-icon />
      <el-table v-else :data="availableTempDorms" style="width: 100%; margin-bottom: 20px" border size="small">
        <el-table-column prop="name" label="宿舍名称" width="120" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="college" label="学院" width="150" />
        <el-table-column prop="grade" label="年级" width="100" />
        <el-table-column prop="availableBeds" label="可用床位" width="100">
          <template #default="scope">
            <el-tag type="success">{{ scope.row.availableBeds }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="selected" label="选择" width="80">
          <template #default="scope">
            <el-radio v-model="selectedTempDormId" :label="scope.row.id" />
          </template>
        </el-table-column>
      </el-table>

      <el-form label-width="100px">
        <el-form-item label="维修原因">
          <el-input v-model="maintenanceReason" type="textarea" :rows="3" placeholder="请输入维修原因" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="showMaintenanceDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmMaintenance" :disabled="!canStartMaintenance">开始维修并迁移住户</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showResidentsDialog" title="查看宿舍住户" width="600px">
      <el-descriptions :column="2" border style="margin-bottom: 15px">
        <el-descriptions-item label="宿舍名称">{{ currentDormInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="容量/已入住">{{ currentDormInfo.capacity }} / {{ currentDormInfo.occupiedBeds }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="currentResidents" style="width: 100%" border empty-text="暂无住户">
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="studentNo" label="学号" width="150" />
        <el-table-column prop="college" label="学院" />
        <el-table-column prop="grade" label="年级" width="100" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button size="small" type="danger" @click="graduateSingle(scope.row)">毕业离宿</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog v-model="showBatchGraduateDialog" title="批量毕业离宿" width="650px">
      <el-alert type="warning" :closable="false" show-icon style="margin-bottom: 20px">
        按年级批量处理毕业离宿，将自动释放所有对应学生的床位
      </el-alert>

      <el-form label-width="100px" style="margin-bottom: 20px">
        <el-form-item label="选择毕业年级">
          <el-select v-model="graduateGrade" placeholder="请选择毕业年级" style="width: 100%" @change="previewGraduate">
            <el-option v-for="g in allGrades" :key="g" :label="g + '级'" :value="g" />
          </el-select>
        </el-form-item>
      </el-form>

      <el-divider v-if="graduateGrade" content-position="left">毕业离宿预览</el-divider>
      <el-descriptions v-if="graduateGrade" :column="2" border style="margin-bottom: 20px">
        <el-descriptions-item label="毕业年级">
          <el-tag type="warning">{{ graduateGrade }}级</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="涉及学生数">
          <el-tag type="info">{{ graduatePreview.totalStudents }}人</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="已分配宿舍">
          <el-tag type="success">{{ graduatePreview.withDormitory }}人</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="将释放床位">
          <el-tag type="danger">{{ graduatePreview.withDormitory }}个</el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <el-divider v-if="graduatePreview.students.length > 0" content-position="left">学生列表</el-divider>
      <el-table v-if="graduatePreview.students.length > 0" :data="graduatePreview.students" size="small" border>
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="college" label="学院" width="130" />
        <el-table-column prop="className" label="班级" width="100" />
        <el-table-column prop="dormitory" label="宿舍" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.dormitory" type="success">{{ scope.row.dormitory }}</el-tag>
            <el-tag v-else type="info">无</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else-if="graduateGrade && graduatePreview.students.length === 0" description="该年级暂无学生" />

      <template #footer>
        <el-button @click="showBatchGraduateDialog = false">取消</el-button>
        <el-button type="primary" @click="executeBatchGraduate" :disabled="!graduateGrade || graduatePreview.totalStudents === 0">
          确认批量毕业离宿
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../api.js'

const dormitories = ref([])
const students = ref([])
const showAddDialog = ref(false)
const showMaintenanceDialog = ref(false)
const showResidentsDialog = ref(false)
const showBatchGraduateDialog = ref(false)
const maintenanceDormitoryId = ref('')
const selectedTempDormId = ref('')
const maintenanceReason = ref('')
const graduateGrade = ref('')

const newDormitory = ref({
  buildingNo: '',
  roomNo: '',
  capacity: 4,
  gender: '',
  college: '',
  grade: ''
})

const maintenanceDormInfo = reactive({
  name: '',
  residentCount: 0,
  gender: '',
  capacity: 0
})

const residentsToTransfer = ref([])
const availableTempDorms = ref([])

const currentDormInfo = reactive({
  id: '',
  name: '',
  capacity: 0,
  occupiedBeds: 0
})
const currentResidents = ref([])

const graduatePreview = reactive({
  totalStudents: 0,
  withDormitory: 0,
  students: []
})

const allGrades = computed(() => {
  const set = new Set(students.value.map(s => s.grade))
  return Array.from(set).filter(g => g).sort()
})

const getDormitoryName = (dormitoryId) => {
  if (!dormitoryId) return null
  const dorm = dormitories.value.find(d => d.id === dormitoryId)
  return dorm ? `${dorm.buildingNo}-${dorm.roomNo}` : null
}

const canStartMaintenance = computed(() => {
  const hasResidents = residentsToTransfer.value.length === 0 || selectedTempDormId.value
  return maintenanceReason.value && (availableTempDorms.value.length > 0 || residentsToTransfer.value.length === 0) && hasResidents
})

const previewGraduate = () => {
  const gradeStudents = students.value.filter(s => s.grade === graduateGrade.value)
  graduatePreview.totalStudents = gradeStudents.length
  graduatePreview.withDormitory = gradeStudents.filter(s => s.dormitoryId).length
  graduatePreview.students = gradeStudents.map(s => ({
    ...s,
    dormitory: getDormitoryName(s.dormitoryId)
  }))
}

const graduateSingle = async (student) => {
  try {
    await ElMessageBox.confirm(
      `确认 ${student.name} 毕业离宿？将自动释放 ${getDormitoryName(student.dormitoryId) || '其'} 的床位`,
      '毕业离宿确认',
      { type: 'warning' }
    )
    const dormName = getDormitoryName(student.dormitoryId)
    await api.graduateStudent(student.id)
    ElMessage.success(`${student.name} 毕业离宿成功${dormName ? `，${dormName} 床位已释放` : ''}`)
    loadDormitories()
    loadStudents()
    if (showResidentsDialog.value) {
      viewResidents(currentDormInfo.id)
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const executeBatchGraduate = async () => {
  try {
    await ElMessageBox.confirm(
      `确认 ${graduateGrade.value}级 ${graduatePreview.totalStudents} 名学生全部毕业离宿？将释放 ${graduatePreview.withDormitory} 个床位`,
      '批量毕业离宿确认',
      { type: 'warning' }
    )
    
    const result = await api.graduateStudentsByGrade(graduateGrade.value)
    ElMessage.success(`批量毕业离宿完成，共处理 ${result.data.totalStudents} 名学生，释放 ${result.data.releasedBeds} 个床位`)
    showBatchGraduateDialog.value = false
    graduateGrade.value = ''
    graduatePreview.totalStudents = 0
    graduatePreview.withDormitory = 0
    graduatePreview.students = []
    loadDormitories()
    loadStudents()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('操作失败')
    }
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

const loadStudents = async () => {
  try {
    const res = await api.getStudents()
    students.value = res.data
  } catch (e) {
    ElMessage.error('加载学生列表失败')
  }
}

const addDormitory = async () => {
  try {
    await api.createDormitory(newDormitory.value)
    ElMessage.success('添加成功')
    showAddDialog.value = false
    newDormitory.value = { buildingNo: '', roomNo: '', capacity: 4, gender: '', college: '', grade: '' }
    loadDormitories()
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

const viewResidents = (dormitoryId) => {
  const dorm = dormitories.value.find(d => d.id === dormitoryId)
  if (dorm) {
    currentDormInfo.id = dormitoryId
    currentDormInfo.name = `${dorm.buildingNo}-${dorm.roomNo}`
    currentDormInfo.capacity = dorm.capacity
    currentDormInfo.occupiedBeds = dorm.occupiedBeds
    currentResidents.value = students.value.filter(s => s.dormitoryId === dormitoryId)
  }
  showResidentsDialog.value = true
}

const startMaintenance = (id) => {
  const dorm = dormitories.value.find(d => d.id === id)
  if (!dorm) return

  maintenanceDormitoryId.value = id
  maintenanceReason.value = ''
  selectedTempDormId.value = ''

  maintenanceDormInfo.name = `${dorm.buildingNo}-${dorm.roomNo}`
  maintenanceDormInfo.residentCount = dorm.occupiedBeds
  maintenanceDormInfo.gender = dorm.gender
  maintenanceDormInfo.capacity = dorm.capacity

  residentsToTransfer.value = students.value.filter(s => s.dormitoryId === id)

  availableTempDorms.value = dormitories.value
    .filter(d => !d.underMaintenance && d.gender === dorm.gender && d.occupiedBeds < d.capacity)
    .map(d => ({
      id: d.id,
      name: `${d.buildingNo}-${d.roomNo}`,
      gender: d.gender,
      college: d.college,
      grade: d.grade,
      availableBeds: d.capacity - d.occupiedBeds
    }))

  if (availableTempDorms.value.length > 0 && residentsToTransfer.value.length > 0) {
    selectedTempDormId.value = availableTempDorms.value[0].id
  }

  showMaintenanceDialog.value = true
}

const confirmMaintenance = async () => {
  try {
    await api.startMaintenance({ 
      dormitoryId: maintenanceDormitoryId.value, 
      reason: maintenanceReason.value,
      temporaryDormitoryId: selectedTempDormId.value
    })
    ElMessage.success(`维修已开始，${residentsToTransfer.value.length}名住户已迁移到临时房间`)
    showMaintenanceDialog.value = false
    loadDormitories()
    loadStudents()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

const endMaintenance = async (id) => {
  try {
    await api.endMaintenance(id)
    ElMessage.success('维修已结束，房间恢复可用')
    loadDormitories()
    loadStudents()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadDormitories()
  loadStudents()
})
</script>