<template>
	<div>
		<div class="center_view">
			<div class="list_search_view">
				<el-form :model="searchQuery" class="search_form" >
					<div class="search_view">
						<div class="search_label">
							账号：
						</div>
						<div class="search_box">
							<el-input class="search_inp" v-model="searchQuery.zhanghao" placeholder="账号"
								clearable>
							</el-input>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							手机：
						</div>
						<div class="search_box">
							<el-input class="search_inp" v-model="searchQuery.shouji" placeholder="手机"
								clearable>
							</el-input>
						</div>
					</div>
					<div class="search_view">
						<div class="search_label">
							发送结果：
						</div>
						<div class="search_box">
							<el-input class="search_inp" v-model="searchQuery.fasongjieguo" placeholder="发送结果"
								clearable>
							</el-input>
						</div>
					</div>
					<div class="search_btn_view">
						<el-button class="search_btn" type="primary" @click="searchClick()" size="small">搜索</el-button>
					</div>
				</el-form>
				<div class="btn_view">
					<el-button class="add_btn" type="danger" :disabled="selRows.length?false:true" @click="delClick(null)"  v-if="btnAuth('tongzhijilu','删除')">
						<i class="iconfont icon-shanchu4"></i>
						删除
					</el-button>
				</div>
			</div>
			<el-table
				v-loading="listLoading"
				border
				:stripe='false'
				@selection-change="handleSelectionChange"
				ref="table"
				v-if="btnAuth('tongzhijilu','查看')"
				:data="list"
				@row-click="listChange">
				<el-table-column :resizable='true' align="left" header-align="left" type="selection" width="55" />
				<el-table-column label="序号" width="70" :resizable='true' align="left" header-align="left">
					<template #default="scope">{{ (listQuery.page-1)*listQuery.limit+scope.$index + 1}}</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhiid"
					label="通知ID">
					<template #default="scope">
						{{scope.row.tongzhiid}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="zhanghao"
					label="账号">
					<template #default="scope">
						{{scope.row.zhanghao}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="shouji"
					label="手机">
					<template #default="scope">
						{{scope.row.shouji}}
					</template>
				</el-table-column>
				<el-table-column min-width="200"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="tongzhineirong"
					label="通知内容">
					<template #default="scope">
						{{scope.row.tongzhineirong}}
					</template>
				</el-table-column>
				<el-table-column min-width="140"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="fasongjieguo"
					label="发送结果">
					<template #default="scope">
						<el-tag v-if="scope.row.fasongjieguo == '发送成功'" type="success">{{scope.row.fasongjieguo}}</el-tag>
						<el-tag v-else-if="scope.row.fasongjieguo == '发送失败'" type="danger">{{scope.row.fasongjieguo}}</el-tag>
						<el-tag v-else-if="scope.row.fasongjieguo == '发送失败（已达到最大重试次数）'" type="warning">{{scope.row.fasongjieguo}}</el-tag>
						<el-tag v-else type="info">{{scope.row.fasongjieguo}}</el-tag>
					</template>
				</el-table-column>
				<el-table-column min-width="200"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="cuowuxinxi"
					label="错误信息">
					<template #default="scope">
						{{scope.row.cuowuxinxi}}
					</template>
				</el-table-column>
				<el-table-column min-width="160"
					:resizable='true'
					:sortable='true'
					align="left"
					header-align="left"
					prop="addtime"
					label="创建时间">
					<template #default="scope">
						{{scope.row.addtime}}
					</template>
				</el-table-column>
				<el-table-column label="操作" width="200" :resizable='true' :sortable='true' align="left" header-align="left">
					<template #default="scope">
						<el-button class="view_btn" type="info" v-if=" btnAuth('tongzhijilu','查看')" @click="infoClick(scope.row.id)">
							<i class="iconfont icon-sousuo2"></i>
							查看
						</el-button>
						<el-button class="del_btn" type="danger" @click="delClick(scope.row.id)"  v-if="btnAuth('tongzhijilu','删除')">
							<i class="iconfont icon-shanchu4"></i>
							删除
						</el-button>
					</template>
				</el-table-column>
			</el-table>
			<el-pagination
				background
				:layout="layouts.join(',')"
				:total="total"
				:page-size="listQuery.limit"
                v-model:current-page="listQuery.page"
				prev-text="上一页"
				next-text="下一页"
				:hide-on-single-page="false"
				:style='{}'
				:page-sizes="[10, 20, 30, 40, 50, 100]"
				@size-change="sizeChange"
				@current-change="currentChange"  />
		</div>
		<formModel ref="formRef" @formModelChange="formModelChange"></formModel>
	</div>
</template>
<script setup>
	import {
		reactive,
		ref,
		getCurrentInstance,
		nextTick,
		onMounted,
		watch,
		computed,
	} from 'vue'
	import {
		useRoute,
		useRouter
	} from 'vue-router'
	import {
		ElMessageBox
	} from 'element-plus'
	import {
		useStore
	} from 'vuex';
	const store = useStore()
	const user = computed(()=>store.getters['user/session'])
	const avatar = ref(store.state.user.avatar)
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	import formModel from './formModel.vue'

	const tableName = 'tongzhijilu'
	const formName = '通知记录'
	const route = useRoute()

	onMounted(()=>{
	})

	const list = ref(null)
	const table = ref(null)
	const listQuery = ref({
		page: 1,
		limit: 10,
		sort: 'id',
		order: 'desc'
	})
	const searchQuery = ref({})
	const selRows = ref([])
	const listLoading = ref(false)
	const listChange = (row) =>{
		nextTick(()=>{
			table.value.toggleRowSelection(row)
		})
	}

	const getList = () => {
		listLoading.value = true
		let params = JSON.parse(JSON.stringify(listQuery.value))
		params['sort'] = 'id'
		params['order'] = 'desc'
		if(searchQuery.value.zhanghao&&searchQuery.value.zhanghao!=''){
			params['zhanghao'] = '%' + searchQuery.value.zhanghao + '%'
		}
		if(searchQuery.value.shouji&&searchQuery.value.shouji!=''){
			params['shouji'] = '%' + searchQuery.value.shouji + '%'
		}
		if(searchQuery.value.fasongjieguo&&searchQuery.value.fasongjieguo!=''){
			params['fasongjieguo'] = '%' + searchQuery.value.fasongjieguo + '%'
		}
		context.$http({
			url: `${tableName}/page`,
			method: 'get',
			params: params
		}).then(res => {
			listLoading.value = false
			list.value = res.data.data.list
			total.value = Number(res.data.data.total)
		})
	}

	const delClick = (id) => {
		let ids = ref([])
		if (id) {
			ids.value = [id]
		} else {
			if (selRows.value.length) {
				for (let x in selRows.value) {
					ids.value.push(selRows.value[x].id)
				}
			} else {
				return false
			}
		}
		ElMessageBox.confirm(`是否删除选中${formName}`, '提示', {
			confirmButtonText: '是',
			cancelButtonText: '否',
			type: 'warning',
		}).then(() => {
			context.$http({
				url: `${tableName}/delete`,
				method: 'post',
				data: ids.value
			}).then(res => {
				context?.$toolUtil.message('删除成功', 'success',()=>{
					getList()
				})
			})
		}).catch(_ => {})
	}

	const handleSelectionChange = (e) => {
		selRows.value = e
	}

	const total = ref(0)
	const layouts = ref(["total","prev","pager","next","sizes","jumper"])
	const sizeChange = (size) => {
		listQuery.value.limit = size
		getList()
	}
	const currentChange = (page) => {
		listQuery.value.page = page
		getList()
	}

	const btnAuth = (e,a)=>{
		return context?.$toolUtil.isAuth(e,a)
	}

	const searchClick = () => {
		listQuery.value.page = 1
		getList()
	}

	const formRef = ref(null)
	const formModelChange=()=>{
		searchClick()
	}

	const infoClick = (id=null)=>{
		if(id){
			formRef.value.init(id,'info')
		}
		else if(selRows.value.length){
			formRef.value.init(selRows.value[0].id,'info')
		}
	}

	const init = () => {
		getList()
	}
	init()
</script>
<style lang="scss" scoped>
	.list_search_view {
		.search_form {
			.search_view {
				.search_label {
				}
				.search_box {
					:deep(.search_inp) {
					}
				}
			}
			.search_btn_view {
				.search_btn {
				}
				.search_btn:hover {
				}
			}
		}
		.btn_view {
			:deep(.el-button--default){
			}
			:deep(.el-button--default:hover){
			}
			:deep(.el-button--success){
			}
			:deep(.el-button--success:hover){
			}
			:deep(.el-button--danger){
			}
			:deep(.el-button--danger:hover){
			}
			:deep(.el-button--warning){
			}
			:deep(.el-button--warning:hover){
			}
		}
	}
	.el-table {
		:deep(.el-table__header-wrapper) {
			thead {
				tr {
					th {
						.cell {
						}
					}
				}
			}
		}
		:deep(.el-table__body-wrapper) {
			tbody {
				tr {
					td {
						.cell {
							.el-button--primary {
							}
							.el-button--primary:hover {
							}
							.el-button--info {
							}
							.el-button--info:hover {
							}
							.el-button--danger {
							}
							.el-button--danger:hover {
							}
							.el-button--success {
							}
							.el-button--success:hover {
							}
							.el-button--warning {
							}
							.el-button--warning:hover {
							}
						}
					}
				}
				tr:hover {
					td {
					}
				}
			}
		}
	}
	.el-pagination {
		:deep(.el-pagination__total) {
		}
		:deep(.btn-prev) {
		}
		:deep(.btn-next) {
		}
		:deep(.btn-prev:disabled) {
		}
		:deep(.btn-next:disabled) {
		}
		:deep(.el-pager) {
			.number {
			}
			.number:hover {
			}
			.number.is-active {
			}
		}
		:deep(.el-pagination__sizes) {
			display: inline-block;
			vertical-align: top;
			font-size: 13px;
			line-height: 28px;
			height: 28px;
			.el-select {
			}
		}
		:deep(.el-pagination__jump) {
			.el-input {
			}
		}
	}
</style>
