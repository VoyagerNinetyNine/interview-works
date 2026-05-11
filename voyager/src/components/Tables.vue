<template>
    <el-form class="tables" label-width="90px" label-position="left">
        <el-form-item label="预测参数：">
            <el-select popper-class="glass-select-popper" v-model="inputStore.userInputParam" placeholder="选择参数">
                <el-option
                v-for="item in params_options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
                />
            </el-select>
        </el-form-item>

        <el-form-item label="经度：">
            <el-input-number v-model="inputStore.userLon" :min="lon_min" :max="lon_max" />
        </el-form-item>

        <el-form-item label="纬度：">
            <el-input-number v-model="inputStore.userLat" :min="lat_min" :max="lat_max" />
        </el-form-item>

        <el-form-item label="起报时间：">
            <t-space direction="vertical">
                <t-date-picker enable-time-picker allow-input clearable
                    v-model="inputStore.userInputEndTime"
                    placeholder="选择输入数据时间"
                    format="YYYY-MM-DD HH:mm"
                    :time-picker-props="timePickerProps"

                    :disable-date="isDateDisabled"
                    @month-change="handleMonthChange"
                    @year-change="handleYearChange"
                />
            </t-space>
        </el-form-item>

        <el-button class="submit" @click="submit">开始预测</el-button>
    </el-form>
</template>



<script setup lang="ts" name="Tables">
import { ref, computed, onMounted } from 'vue'
import { params, serverUrl } from './config'
import {useInputStore} from "@/store/userinput"
const inputStore = useInputStore()
import {useOutputStore} from "@/store/output"
const outputStore = useOutputStore()
let params_options = ref(params)
const lat_min = -90, lat_max = 90, lon_min = 0, lon_max = 360
const currentPanelYear = ref<number>(new Date().getFullYear());
const currentPanelMonth = ref<number>(new Date().getMonth());
const validDates = ref<Array<{start: Date, end: Date}>>([]);
let isValidDatesValid = ref(false);

const timePickerProps = computed(() => ({
    steps: [1, outputStore.timeinterval[inputStore.userInputParam]], // [时步长, 分步长, 秒步长]
    format: 'HH:mm'
}));

function handleMonthChange(month: Record<string, any>) {
    currentPanelMonth.value = month.month;
    fetchRanges(currentPanelYear.value, currentPanelMonth.value);
}

function handleYearChange(year: Record<string, any>) {
    currentPanelYear.value = year.year;
    fetchRanges(currentPanelYear.value, currentPanelMonth.value);
}

function fetchRanges(year: number, month: number) { 
    const params = new URLSearchParams({
        year : year.toString(),
        month: (month+1).toString(),
        variable: inputStore.userInputParam
    })
    const url = `${serverUrl}/predict/monthlytimerange?${params.toString()}`;
    
    fetch(url)
        .then(response => response.json())
        .then(data => {
            isValidDatesValid.value = true;
            validDates.value = data.ranges.map((item: { start: string; end: string }) => ({
                start: new Date(item.start),
                end: new Date(item.end)
            }));
        })
        .catch(error => {
            ElMessage.error('获取有效时间范围失败:', error);
            isValidDatesValid.value = false;
            validDates.value = [];
        });
}

function isDateDisabled(date: any): boolean {
    if (!isValidDatesValid.value) {
        return false;
    }
    if (validDates.value.length === 0){
        return true;
    }
    const checkDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());
    
    return !validDates.value.some(range => {
        const start = new Date(range.start.getFullYear(), range.start.getMonth(), range.start.getDate());
        const end = new Date(range.end.getFullYear(), range.end.getMonth(), range.end.getDate());
        return checkDate >= start && checkDate <= end;
    });
}

async function submit() {
    inputStore.overlay = true;
    if (inputStore.userInputEndTime === "") {
        alert("请选择输入数据最后一帧的时间！");
        inputStore.overlay = false;
        return;
    } 
    const temp = { ...inputStore.submittedValues };
    try{     
        let result;   
        if (["temperature", "humidity"].includes(inputStore.userInputParam)){
            result = await outputStore.getPointData("all");
        }else{
            await outputStore.getPointData("timeline");
        }
        if (result === "failed"){
            return;
        }

        if (inputStore.userInputParam === inputStore.submittedValues.param && inputStore.userInputEndTime === inputStore.submittedValues.time){
            inputStore.submittedValues.displayArea.add(outputStore.pointArea);
        }else{
            inputStore.submittedValues.displayArea = new Set([outputStore.pointArea]);
            outputStore.clearData();
        }
        inputStore.submittedValues.param = inputStore.userInputParam;
        inputStore.submittedValues.time = inputStore.userInputEndTime;    

        await outputStore.getImgData();        
    }catch(e: any){
        Object.assign(inputStore.submittedValues, {
            param: temp.param,
            time: temp.time,
            displayArea: new Set(temp.displayArea)
        });
        ElMessage.error(e.message);
    }finally{
        inputStore.overlay = false;
    }
}

onMounted(()=>{
    outputStore.getInitData();
    fetchRanges(currentPanelYear.value, currentPanelMonth.value);
})
</script>


<style scoped lang="css">
.tables {
    position: absolute;
    display: flex;
    flex-direction: column;
    gap: 5px;
    padding: 24px;
    width: 320px;
    margin-left: 20px;
    margin-top: 20px;
    
    /* 毛玻璃质感核心代码 */
    background: rgba(15, 23, 42, 0.65);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    
    /* 边框与阴影，增加精致感 */
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);
    z-index: 999;
}

/* 调整 Element Plus 内部标签颜色 */
:deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.85);
  font-weight: 500;
  justify-content: flex-start;
}

:deep(.el-input-number__decrease),
:deep(.el-input-number__increase)  {
  display: none;
}

/* 输入框样式微调 */
:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  background-color: rgba(255, 255, 255, 0.05) !important;
  box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.2) inset !important;
}

:deep(.el-input__inner) {
  color: #fff !important;
}

:deep(.el-select__selected-item span){
    color: white;
}

:deep(.t-input__inner) {
    color: white;
}

:deep(.t-input__placeholder) {
  color: rgba(73, 37, 37, 0.3) !important;
}

/* 按钮美化 */
.submit {
  width: 100%;
  margin-top: 10px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border: none;
  color: white;
  height: 40px;
  font-weight: bold;
  transition: all 0.3s ease;
}

.submit:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.4);
}

:deep(.t-input) {
    border-color: rgba(255, 255, 255, 0.2) !important;
    background-color: rgba(255, 255, 255, 0.05) !important;
}

/* 让所有表单项内的组件宽度强制一致 */
:deep(.el-form-item__content > *) {
  width: 100% !important;
}

/* 针对 Element Plus 的特殊处理 */
:deep(.el-select),
:deep(.el-input-number),
:deep(.el-input) {
  width: 100% !important;
}

/* 确保内部 input 填满 */
:deep(.el-input__wrapper) {
  width: 100%;
  box-sizing: border-box;
}

/* TDesign 时间选择器对齐 */
:deep(.t-date-picker) {
    width: 100%;
}

/* 修正 label 居中对齐问题 */
:deep(.el-form-item__label) {
  display: flex;
  align-items: center;
  color: rgba(255, 255, 255, 0.85);
}

</style>