<template>
  <div>
    <h2 style="margin-bottom: 20px">数据统计</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 40px; color: #409EFF; font-weight: bold">{{ statistics.emptyBeds }}</div>
            <div style="margin-top: 10px; color: #909399">空床位数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 40px; color: #E6A23C; font-weight: bold">{{ statistics.pendingApplications }}</div>
            <div style="margin-top: 10px; color: #909399">待处理申请</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 40px; color: #F56C6C; font-weight: bold">{{ statistics.violationCount }}</div>
            <div style="margin-top: 10px; color: #909399">违规记录</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 40px; color: #67C23A; font-weight: bold">{{ statistics.totalStudents }}</div>
            <div style="margin-top: 10px; color: #909399">学生总数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card shadow="hover">
          <div style="text-align: center">
            <div style="font-size: 40px; color: #909399; font-weight: bold">{{ statistics.totalDormitories }}</div>
            <div style="margin-top: 10px; color: #909399">宿舍总数</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-button style="margin-top: 20px" type="primary" @click="loadStatistics">刷新数据</el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api.js'

const statistics = ref({
  emptyBeds: 0,
  pendingApplications: 0,
  violationCount: 0,
  totalStudents: 0,
  totalDormitories: 0
})

const loadStatistics = async () => {
  try {
    const res = await api.getStatistics()
    statistics.value = res.data
  } catch (e) {
    ElMessage.error('加载统计数据失败')
  }
}

onMounted(() => {
  loadStatistics()
})
</script>