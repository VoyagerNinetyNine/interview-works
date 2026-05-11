import { defineStore } from "pinia"

export const useInputStore = defineStore("userInput", {
    state(){
        return {
            userLon: 0,
            userLat: 0,
            userInputEndTime: "",
            userInputParam: "temperature",
            userIsobaricIndex: 0,
            userPredictTimeIndex: 0,
            overlay: false,
            submittedValues: {
                param: "",
                time: "",
                displayArea: new Set<string>(),
            },
        }
    },
})