import { defineStore } from "pinia"
import { serverUrl } from "../components/config"
import { useInputStore } from "@/store/userinput"


interface OutputState {
    isobaric: any[]
    predict_start_time: string
    timelineData: any[]
    profileData: any[]
    imgData: Record<string, number[]>
    scatterData: Record<string, Uint8Array>
    areaRangeImages: Record<string, any>
    output_data_len: Record<string, any>
    // bounds: Record<string, any>
    coordinate_urls: Record<string, string> 
    sst_mask_urls: Record<string, string> 
    timeinterval: Record<string, any>
    areas: string[]
    pointArea: string,
    hasLoaded: boolean,
    extremaValue: Record<string, any>,
    shape: Record<string, number[]> 
}
export const useOutputStore = defineStore("userOutput", {
    state(): OutputState {
        return {
            isobaric: [],
            predict_start_time: "",
            timelineData: [],
            profileData: [],
            imgData: {},
            scatterData: {},
            areaRangeImages: {},
            output_data_len: {},
            // bounds: {},
            coordinate_urls: {},
            sst_mask_urls: {},
            timeinterval: {},
            areas: [],
            pointArea: "",
            hasLoaded: false,
            extremaValue: {},
            shape: {},
        }
    },
    getters: {
        inputStore: () => {
            const inputStore = useInputStore()  // ✅ 在 getter 中获取
            return inputStore
        }
    },
    actions: {
        async getInitData(){
            try{
                const response = await fetch(`${serverUrl}/predict/init/`)
                const data = await response.json()
                if (data.status === "failed"){
                    alert(data.message)
                    return
                }
                this.isobaric = data.isobaric
                this.areas = data.areas
                this.timeinterval = data.time_interval
                Object.assign(this.coordinate_urls, data.coordinate_urls)
                Object.assign(this.sst_mask_urls, data.sst_mask_urls)
                // this.areaRangeImages = data.img_url
                Object.assign(this.output_data_len, data.output_data_len)
                // this.bounds = data.bounds
                this.hasLoaded = true
            }catch(error: any){
                console.log(error);
                ElMessage.error(`请求失败：${error.message}`);
            }
        },
        async getImgData(area: string = "", usingCachedParam: boolean = false){
            if(this.inputStore.submittedValues.param !== "" && usingCachedParam){
                if(!(this.inputStore.userInputParam === this.inputStore.submittedValues.param && this.inputStore.userInputEndTime === this.inputStore.submittedValues.time)){
                    ElMessage.warning("界面中的参数已改变，但是未点击预测按钮，仍然使用旧参数");
                }
            }
            let params: URLSearchParams
            if (usingCachedParam){
                params = new URLSearchParams({
                    lon: String(this.inputStore.userLon),
                    lat: String(this.inputStore.userLat),
                    variable: this.inputStore.submittedValues.param,
                    input_end_date: this.inputStore.submittedValues.time,
                });
            }else{
                params = new URLSearchParams({
                    lon: String(this.inputStore.userLon),
                    lat: String(this.inputStore.userLat),
                    variable: this.inputStore.userInputParam,
                    input_end_date: this.inputStore.userInputEndTime,
                });
            }
            if(["temperature", "humidity"].includes(this.inputStore.userInputParam)){
                params.append("isobaric_index", String(this.inputStore.userIsobaricIndex));
            }

            if (area !== ""){
                params.append("area", area);
            }
            try{
                let response = await fetch(`${serverUrl}/predict/imgdata/?${params}`);
                const data = await response.json();
                if (data.status === "failed"){
                    ElMessage.warning(data.message);
                    return;
                }
                const dataurl = data.dataurl;
                const area = data.area;
                response = await fetch(dataurl);
                const Buffer = await response.arrayBuffer();
                this.scatterData[area] = new Uint8Array(Buffer);
                this.shape[area] = data.shape;
                this.extremaValue[area] = {
                    min: parseFloat(data.vmin),
                    max: parseFloat(data.vmax),
                };
                const index = this.inputStore.userPredictTimeIndex;
                const length = this.shape[area][1] * this.shape[area][2];
                this.imgData[area] = Array.from(this.scatterData[area].slice(index * length, (index + 1) * length));
            }catch(error: any){
                console.log(error);
                ElMessage.error(`请求失败：${error.message}`);
            }finally{
                
            }
        },
        clearData(){
            this.scatterData = {}
            this.imgData = {}
        },
        // 获取该点的timeline数据和profile数据
        async getPointData(dataType: string, withArea:boolean = false, usingCachedParam: boolean = false){
            if(this.inputStore.submittedValues.param !== "" && usingCachedParam){
                if(!(this.inputStore.userInputParam === this.inputStore.submittedValues.param && this.inputStore.userInputEndTime === this.inputStore.submittedValues.time)){
                    ElMessage.warning("界面中的参数已改变，但是未点击预测按钮，仍然使用旧参数");
                }
            }
            try{
                let params: URLSearchParams
                if (usingCachedParam){
                    params = new URLSearchParams({
                        lon: String(this.inputStore.userLon),
                        lat: String(this.inputStore.userLat),
                        variable: this.inputStore.submittedValues.param,
                        input_end_date: this.inputStore.submittedValues.time,
                    });
                }else{
                    params = new URLSearchParams({
                        lon: String(this.inputStore.userLon),
                        lat: String(this.inputStore.userLat),
                        variable: this.inputStore.userInputParam,
                        input_end_date: this.inputStore.userInputEndTime,
                    });
                }
                
                if (dataType === "all"){
                    params.append("predict_time_index", String(this.inputStore.userPredictTimeIndex));
                    params.append("isobaric_index", String(this.inputStore.userIsobaricIndex));
                }else if (dataType === "profile"){
                    params.append("predict_time_index", String(this.inputStore.userPredictTimeIndex));
                }else if (dataType === "timeline"){
                    const variable = params.get("variable");
                    if (variable && ["temperature", "humidity"].includes(variable)){
                        params.append("isobaric_index", String(this.inputStore.userIsobaricIndex));
                    }
                }else{
                    alert("参数错误")
                    return "failed";
                }
                if (withArea){
                    params.append("area", Array.from(this.inputStore.submittedValues.displayArea).join(","))
                }
                const response = await fetch(`${serverUrl}/predict/pointdata/?${params}`)
                const data = await response.json()
                if (data.status === "failed"){
                    ElMessage.warning(data.message);
                    return "failed";
                }
                this.pointArea = data.point_area
                this.predict_start_time = data.predict_start_time
                if (dataType === "all"){
                    this.profileData = data.profile_data
                    this.timelineData = data.timeline_data
                }else if (dataType === "timeline"){
                    this.timelineData = data.timeline_data
                }else if (dataType === "profile"){
                    this.profileData = data.profile_data
                }
                return "success";
            }catch(error: any){ 
                ElMessage.error(`请求失败：${error.message}`);
                return "failed";
            }
        }
    }
})