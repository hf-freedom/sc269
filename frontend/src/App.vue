<template>
  <el-container style="height: 100vh">
    <el-header style="background: #409EFF; color: white; font-size: 24px; line-height: 60px; padding: 0 20px">
      校园宿舍分配系统
    </el-header>
    <el-container>
      <el-aside width="200px" style="background: #f5f7fa">
        <el-menu :default-active="activeTab" @select="handleMenuSelect">
          <el-menu-item index="statistics">数据统计</el-menu-item>
          <el-menu-item index="students">学生管理</el-menu-item>
          <el-menu-item index="dormitories">宿舍管理</el-menu-item>
          <el-menu-item index="allocations">分配申请</el-menu-item>
          <el-menu-item index="transfers">调宿申请</el-menu-item>
          <el-menu-item index="violations">违规记录</el-menu-item>
        </el-menu>
      </el-aside>
      <el-main style="padding: 20px">
        <statistics v-if="activeTab === 'statistics'" />
        <students v-if="activeTab === 'students'" />
        <dormitories v-if="activeTab === 'dormitories'" />
        <allocations v-if="activeTab === 'allocations'" />
        <transfers v-if="activeTab === 'transfers'" />
        <violations v-if="activeTab === 'violations'" />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import statistics from './components/Statistics.vue'
import students from './components/Students.vue'
import dormitories from './components/Dormitories.vue'
import allocations from './components/Allocations.vue'
import transfers from './components/Transfers.vue'
import violations from './components/Violations.vue'

const activeTab = ref('statistics')

const handleMenuSelect = (key) => {
  activeTab.value = key
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}
</style>