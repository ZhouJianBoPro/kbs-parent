<template>
  <div class="token-stat-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-title">
        <h2>Token 统计</h2>
        <p>查看 Token 消耗数据面板</p>
      </div>
    </div>

    <!-- 统计维度选择 -->
    <div class="dimension-card">
      <el-radio-group v-model="dimension" @change="handleDimensionChange">
        <el-radio-button value="day">当天</el-radio-button>
        <el-radio-button value="week">本周</el-radio-button>
        <el-radio-button value="month">本月</el-radio-button>
        <el-radio-button value="year">本年</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 统计概览卡片 -->
    <div class="stat-cards">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon input-icon">
            <el-icon><Download /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">输入 Token</div>
            <div class="stat-value">{{ formatNumber(totalStat.totalInputToken) }}</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon output-icon">
            <el-icon><Upload /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">输出 Token</div>
            <div class="stat-value">{{ formatNumber(totalStat.totalOutputToken) }}</div>
          </div>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon total-icon">
            <el-icon><DataLine /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">总 Token</div>
            <div class="stat-value">{{ formatNumber(totalStat.totalToken) }}</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表和列表区域 -->
    <div class="content-grid">
      <!-- 趋势图 -->
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>Token 消耗趋势</span>
          </div>
        </template>
        <div v-loading="chartLoading" class="chart-container">
          <div ref="trendChartRef" style="width: 100%; height: 350px"></div>
        </div>
      </el-card>

      <!-- 排名前十用户 -->
      <el-card class="table-card">
        <template #header>
          <div class="card-header">
            <span>Top {{ topN }} 用户 Token 消耗</span>
          </div>
        </template>
        <div v-loading="tableLoading">
          <el-table :data="topUsers" style="width: 100%">
            <el-table-column prop="username" label="用户名" min-width="120" />
            <el-table-column prop="inputToken" label="输入" min-width="100">
              <template #default="{ row }">
                {{ formatNumber(row.inputToken) }}
              </template>
            </el-table-column>
            <el-table-column prop="outputToken" label="输出" min-width="100">
              <template #default="{ row }">
                {{ formatNumber(row.outputToken) }}
              </template>
            </el-table-column>
            <el-table-column prop="totalToken" label="总量" min-width="100">
              <template #default="{ row }">
                <span class="total-text">{{ formatNumber(row.totalToken) }}</span>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!tableLoading && topUsers.length === 0" description="暂无数据" />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { Download, Upload, DataLine } from '@element-plus/icons-vue'
import { tokenStat } from '@/api/tokenStat'
import * as echarts from 'echarts'

const dimension = ref('day')
const topN = ref(10)
const tableLoading = ref(false)
const chartLoading = ref(false)

const topUsers = ref([])
const totalStat = ref({
  totalInputToken: 0,
  totalOutputToken: 0,
  totalToken: 0,
  timeDimensionStats: []
})

const trendChartRef = ref(null)
let trendChart = null

// 格式化数字
const formatNumber = (num) => {
  if (!num) return '0'
  return num.toLocaleString()
}

// 获取统计数据
const fetchData = async () => {
  tableLoading.value = true
  chartLoading.value = true
  try {
    const res = await tokenStat.getStat({
      dimension: dimension.value,
      topN: topN.value
    })
    if (res.code === 200) {
      const data = res.data
      topUsers.value = data.topUsers || []
      totalStat.value = data.totalStat || {
        totalInputToken: 0,
        totalOutputToken: 0,
        totalToken: 0,
        timeDimensionStats: []
      }
      updateChart()
    }
  } finally {
    tableLoading.value = false
    chartLoading.value = false
  }
}

// 维度切换
const handleDimensionChange = () => {
  fetchData()
}

// 更新趋势图
const updateChart = () => {
  if (!trendChartRef.value) return

  const stats = totalStat.value.timeDimensionStats || []
  if (stats.length === 0) {
    if (trendChart) {
      trendChart.clear()
    }
    return
  }

  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value)
  }

  const labels = stats.map(item => item.timeLabel)
  const totalData = stats.map(item => item.totalToken)

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        return `<div>${params[0].name}</div>
          <div style="display: flex; align-items: center; gap: 8px;">
            <span style="display: inline-block; width: 10px; height: 10px; border-radius: 50%; background: ${params[0].color};"></span>
            总 Token: ${params[0].value?.toLocaleString() || 0}
          </div>`
      }
    },
    legend: {
      data: ['总 Token'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: labels
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: (value) => {
          if (value >= 10000) return (value / 10000) + 'w'
          if (value >= 1000) return (value / 1000) + 'k'
          return value
        }
      }
    },
    series: [
      {
        name: '总 Token',
        type: 'line',
        data: totalData,
        smooth: true,
        itemStyle: { color: '#e6a23c' },
        areaStyle: { color: 'rgba(230, 162, 60, 0.1)' }
      }
    ]
  }

  trendChart.setOption(option)
}

// 监听窗口大小变化
const handleResize = () => {
  trendChart?.resize()
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
})
</script>

<style scoped>
.token-stat-container {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
}

.page-title h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 4px 0;
}

.page-title p {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.dimension-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px 20px;
  margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.stat-card {
  border-radius: 8px;
}

.stat-card :deep(.el-card__body) {
  padding: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.input-icon {
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.output-icon {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.total-icon {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #8c8c8c;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.chart-card,
.table-card {
  border-radius: 8px;
}

.card-header {
  font-weight: 600;
  font-size: 16px;
}

.chart-container {
  min-height: 350px;
}

.total-text {
  font-weight: 600;
  color: #e6a23c;
}

@media (max-width: 1200px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: 1fr;
  }
}
</style>
