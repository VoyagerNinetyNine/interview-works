export const params = [
    { label: '温度廓线', value: 'temperature' },
    { label: '湿度廓线', value: 'humidity' },
    { label: '海表温度', value: 'sst' },
    { label: '云光学厚度', value: 'cloud' },
    { label: '降水强度', value: 'rain' },
    { label: '气溶胶光学厚度', value: 'aerosol' },
]

export const profile_charts_text = {
    name: {
        "temperature": "温度",
        "humidity": "湿度",
        "sst": "海表温度",
        "cloud": "云光学厚度",
        "rain": "降水强度",
        "aerosol": "气溶胶光学厚度",
    },
    units: {
        "temperature": "K",
        "humidity": "%",
        "sst": "℃",
        "cloud": "",
        "rain": "mm/h",
        "aerosol": "",
    },
}


const get_serverUrl = async () => {
    const response = await fetch('/serverIP/server_ip.json');
    const data = await response.json();
    return `http://${data.server_ip}:8000`;
}
const get_system_name = async () => {
    const response = await fetch('/serverIP/server_ip.json');
    const data = await response.json();
    return data.system_name;
}
export const serverUrl = await get_serverUrl();
export const system_name = await get_system_name();
export const area_names = {
    "east": "东海",
    "south": "南海"
}