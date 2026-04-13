<template>
  <div class="user-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="page-title">
        <h2>用户管理</h2>
        <p>管理系统用户账号</p>
      </div>
    </div>

    <!-- 筛选卡片 -->
    <div class="filter-card">
      <el-input
        v-model="queryParams.username"
        placeholder="搜索用户名"
        clearable
        style="width: 200px"
        :prefix-icon="Search"
        @keyup.enter="handleQuery"
      />
      <el-input
        v-model="queryParams.nickname"
        placeholder="搜索昵称"
        clearable
        style="width: 200px"
        :prefix-icon="Search"
        @keyup.enter="handleQuery"
      />
      <el-input
        v-model="queryParams.phone"
        placeholder="搜索手机号"
        clearable
        style="width: 200px"
        :prefix-icon="Search"
        @keyup.enter="handleQuery"
      />
      <el-select v-model="queryParams.status" placeholder="用户状态" clearable style="width: 140px">
        <el-option label="正常" :value="0" />
        <el-option label="禁用" :value="1" />
      </el-select>
      <el-button type="primary" @click="handleQuery">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <!-- 用户列表 -->
    <div class="table-card" v-loading="loading">
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="danger"
              link
              @click="handleUpdateStatus(row)"
            >
              禁用
            </el-button>
            <el-button
              v-else
              type="success"
              link
              @click="handleUpdateStatus(row)"
            >
              启用
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && tableData.length === 0" description="暂无用户数据" />
    </div>

    <!-- 分页 -->
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="queryParams.pageNo"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @change="getList"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { user } from '@/api/user'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = ref({
  pageNo: 1,
  pageSize: 10,
  username: '',
  nickname: '',
  phone: '',
  status: null
})

const getList = async () => {
  loading.value = true
  try {
    const res = await user.getPage(queryParams.value)
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.value.pageNo = 1
  getList()
}

const handleReset = () => {
  queryParams.value = {
    pageNo: 1,
    pageSize: 10,
    username: '',
    nickname: '',
    phone: '',
    status: null
  }
  getList()
}

const handleUpdateStatus = async (row) => {
  const action = row.status === 0 ? '禁用' : '启用'
  await ElMessageBox.confirm(`确认${action}用户 "${row.username}" 吗？`, '提示', { type: 'warning' })

  const newStatus = row.status === 0 ? 1 : 0
  const res = await user.updateStatus(row.id, newStatus)
  if (res.code === 200) {
    ElMessage.success(`${action}成功`)
    getList()
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.user-container {
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

.filter-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px 20px;
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.table-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
