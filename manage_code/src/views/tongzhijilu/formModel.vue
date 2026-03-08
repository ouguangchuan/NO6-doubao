<template>
	<div>
		<el-dialog modal-class="edit_form_modal" class="edit_form" v-model="formVisible" :title="formTitle" width="50%" destroy-on-close :fullscreen='false'>
			<el-form class="formModel_form" ref="formRef" :model="form" :rules="rules">
				<el-row >
					<el-col :span="24">
						<el-form-item label="通知ID" prop="tongzhiid">
							<el-input class="list_inp" v-model="form.tongzhiid" :readonly="true" placeholder="通知ID" />
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="账号" prop="zhanghao">
							<el-input class="list_inp" v-model="form.zhanghao" placeholder="账号"
                                type="text"
								:readonly="true" />
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="手机" prop="shouji">
							<el-input class="list_inp" v-model="form.shouji" placeholder="手机"
                                type="text"
								:readonly="true" />
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="通知内容" prop="tongzhineirong">
							<el-input
								class="list_inp"
								type="textarea"
								:rows="4"
								v-model="form.tongzhineirong"
								:readonly="true"
								placeholder="通知内容" />
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="发送结果" prop="fasongjieguo">
							<el-input class="list_inp" v-model="form.fasongjieguo" placeholder="发送结果"
                                type="text"
								:readonly="true" />
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="错误信息" prop="cuowuxinxi">
							<el-input
								class="list_inp"
								type="textarea"
								:rows="3"
								v-model="form.cuowuxinxi"
								:readonly="true"
								placeholder="错误信息" />
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="创建时间" prop="addtime">
							<el-input class="list_inp" v-model="form.addtime" placeholder="创建时间"
                                type="text"
								:readonly="true" />
						</el-form-item>
					</el-col>

				</el-row>
			</el-form>
		</el-dialog>
	</div>
</template>
<script setup>
	import {
		reactive,
		ref,
		getCurrentInstance,
		nextTick,
		computed,
		defineEmits
	} from 'vue'
    import {
        useStore
    } from 'vuex';
    const store = useStore()
    const user = computed(()=>store.getters['user/session'])
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const emit = defineEmits(['formModelChange'])

	const tableName = 'tongzhijilu'
	const formName = '通知记录'

	const form = ref({})
	const formVisible = ref(false)
	const isAdd = ref(false)
	const formTitle = ref('')

	const rules = ref({
		tongzhiid: [
		],
		zhanghao: [
		],
		shouji: [
		],
		tongzhineirong: [
		],
		fasongjieguo: [
		],
		cuowuxinxi: [
		],
		addtime: [
		],
	})

	const formRef = ref(null)
	const id = ref(0)
	const type = ref('')

	const resetForm = () => {
		form.value = {
			tongzhiid: '',
			zhanghao: '',
			shouji: '',
			tongzhineirong: '',
			fasongjieguo: '',
			cuowuxinxi: '',
			addtime: '',
		}
	}

	const getInfo = ()=>{
		context?.$http({
			url: `${tableName}/info/${id.value}`,
			method: 'get'
		}).then(res => {
			form.value = res.data.data
			formVisible.value = true
		})
	}

	const init=(formId=null,formType='add')=>{
		resetForm()
		if(formId){
			id.value = formId
			type.value = formType
		}
		if(formType == 'info'){
			isAdd.value = false
			formTitle.value = '查看' + formName
			getInfo()
		}
	}

	defineExpose({
		init
	})

	const closeClick = () => {
		formVisible.value = false
	}
</script>
<style lang="scss" scoped>
	.formModel_form{
		:deep(.el-form-item) {
			.el-form-item__label {
			}
			.el-form-item__content {
				.list_inp {
				}
				.list_date {
				}
			}
		}
	}
</style>
