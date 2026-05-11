const south_max_lon = 123;
const south_min_lon = 107;
const south_max_lat = 25;
const south_min_lat = 3;

const east_max_lon = 127;
const east_min_lon = 121;
const east_max_lat = 33;
const east_min_lat = 25;

const start_date = '2023-03-02 00:00:00';
const lon_lat_interval = 0.0625;

const number_of_test_cases = 5;

const dst_ip = "localhost"

let params_for_sst = {
    "sst":{    
            "area": 'east',
            "input_start_time": "2023-03-02 00:00:00",
            "predict_time": 0,
            "need_prompt": false,
            "optional_predict_time":[],
            "longitude": -1,
            "latitude": -1,
        },
    "precipitation":{    
            "area": 'east',
            "input_start_time": "2023-03-02 00:00:00",
            "predict_time": 0,
            "need_prompt": false,
            "optional_predict_time":[],
            "longitude": -1,
            "latitude": -1,
        },
    "cloud":{    
            "area": 'east',
            "input_start_time": "2023-03-02 00:00:00",
            "predict_time": 0,
            "need_prompt": false,
            "optional_predict_time":[],
            "longitude": -1,
            "latitude": -1,
        },
    "aerosol":{    
            "area": 'east',
            "input_start_time": "2023-03-02 00:00:00",
            "predict_time": 0,
            "need_prompt": false,
            "optional_predict_time":[],
            "longitude": -1,
            "latitude": -1,
        },
}

//  predict_start_hour指的是预测出来的第一帧跟输入数据的第一帧的间隔, 单位为小时, 整数
//  step指的是预测数据相邻帧的时间间隔, 单位为min
//  length指的是预测数据的总帧数
let params_for_predicttime = {
    "temperature": {"step":15, "length":24, "predict_start_hour":6},
    "humidity": {"step":15, "length":24, "predict_start_hour":6},
    "sst": {"step":15, "length":24, "predict_start_hour":6},
    "precipitation": {"step":15, "length":24, "predict_start_hour":6},
    "cloud": {"step":10, "length":36, "predict_start_hour":6},
    "aerosol": {"step":10, "length":36, "predict_start_hour":6},
}

let select_input_start_time = {
    'temperature': start_date,
    'humidity': start_date,
    'sst': start_date,
    'precipitation': start_date,
    'cloud': start_date,
    'aerosol': start_date
};

let has_init = {
    'temperature': false,
    'humidity': false,
    'sst': false,
    'precipitation': false,
    'cloud': false,
    'aerosol': false
};

const test_cases = {
    "temperature": [{
            "area": 'south',
            "input_start_time": "2023-04-04 08:00:00",
            "predict_time": 0
        },
        {
            "area": 'south',
            "input_start_time": "2023-04-04 13:30:00",
            "predict_time": 0
        },
        {
            "area": 'south',
            "input_start_time": "2023-04-06 07:00:00",
            "predict_time": 6
        },        
        {
            "area": 'south',
            "input_start_time": "2023-04-07 11:00:00",
            "predict_time": 1
        },
        {
            "area": 'south',
            "input_start_time": "2023-04-07 20:30:00",
            "predict_time": 22
        }
    ],
    "humidity": [{
            "area": 'south',
            "input_start_time": "2023-04-04 08:00:00",
            "predict_time": 0
        },
        {
            "area": 'south',
            "input_start_time": "2023-04-04 13:00:00",
            "predict_time": 2
        },
        {
            "area": 'south',
            "input_start_time": "2023-04-06 07:00:00",
            "predict_time": 6
        },        
        {
            "area": 'south',
            "input_start_time": "2023-04-07 11:00:00",
            "predict_time": 1
        },
        {
            "area": 'south',
            "input_start_time": "2023-04-07 20:30:00",
            "predict_time": 22
        }
    ],
    "sst": [{
            "area": 'south',
            "input_start_time_index": 0,
            "predict_time": 0
        },
        {
            "area": 'south',
            "input_start_time_index": 1,
            "predict_time": 1
        },
        {
            "area": 'south',
            "input_start_time_index": 2,
            "predict_time": 2
        },
        {
            "area": 'south',
            "input_start_time_index": 3,
            "predict_time": 3
        },
        {
            "area": 'south',
            "input_start_time_index": 4,
            "predict_time": 4
        },
    ]
}


// 标签页切换
document.addEventListener('DOMContentLoaded', function() {
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabPanes = document.querySelectorAll('.tab-pane');
    
    // 设置初始活动标签页
    updateActiveTab('temperature');
    
    // 为每个标签页按钮添加点击事件
    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            const tabId = this.getAttribute('data-tab');
            updateActiveTab(tabId);
        });
    });
    
    // 更新活动标签页的函数
    function updateActiveTab(tabId) {
        // 移除所有活动状态
        tabButtons.forEach(btn => btn.classList.remove('active'));
        tabPanes.forEach(pane => pane.classList.remove('active'));
        
        // 添加当前活动状态
        document.querySelector(`.tab-btn[data-tab="${tabId}"]`).classList.add('active');
        document.getElementById(tabId).classList.add('active');
        
        if (!has_init[tabId]){
            const activeTab = getActiveTab();
            // const need_prompt_checkbox = activeTab.querySelector('.need-prompt');
            // updateFormState(need_prompt_checkbox);
            createTestCaseOption();
            if (tabId == 'humidity' || tabId == 'temperature'){
                updateImgBlockPosition();
                updatePosition();
                updatelonlat();
            }
            else if (tabId == 'sst'){
                updatesstlonlat();
            }
            else{
                updatesstlonlat();
            }
            updatePredictTime(select_input_start_time[tabId]);
            has_init[tabId] = true;
        }
    }
    
    // 添加键盘导航支持
    document.addEventListener('keydown', function(e) {
        const activeTab = document.querySelector('.tab-btn.active');
        const tabs = Array.from(tabButtons);
        const currentIndex = tabs.indexOf(activeTab);
        
        // 左箭头键：切换到前一个标签页
        if (e.key === 'ArrowLeft') {
            const prevIndex = (currentIndex - 1 + tabs.length) % tabs.length;
            const prevTabId = tabs[prevIndex].getAttribute('data-tab');
            updateActiveTab(prevTabId);
        }
        
        // 右箭头键：切换到后一个标签页
        if (e.key === 'ArrowRight') {
            const nextIndex = (currentIndex + 1) % tabs.length;
            const nextTabId = tabs[nextIndex].getAttribute('data-tab');
            updateActiveTab(nextTabId);
        }
    });
});

// 获取当前激活的标签页
function getActiveTab() {
    const activeTab = document.querySelector('.tab-pane.active');
    return activeTab;
}

// 创建测试用例选项
function createTestCaseOption() {
    const activeTab = getActiveTab();
    const select = activeTab.querySelector(".test-cases");
    for (let i = 0; i < number_of_test_cases ; i++) {
        const option = document.createElement("option");
        option.value = i;
        option.text = `测试用例${i+1}`;
        select.appendChild(option);
    }
}

// 选择经纬度
function createCoordinateSelect(id, start, end, step) {
    const activeTab = getActiveTab();
    const select = activeTab.querySelector(id);
    select.innerHTML = "";
    if (start <= end){
        for (let value = start; value <= end; value += step) {
            const option = document.createElement('option');
            option.value = value;
            option.textContent = value.toFixed(2);
            select.appendChild(option);
        }
    }
    else {
        for (let value = start; value >= end; value -= step) {
            const option = document.createElement('option');
            option.value = value;
            option.textContent = value.toFixed(2);
            select.appendChild(option);
        }
    }
}

// 更新地图标记
function updateMarker(lon, lat, marker, mapImg, region) {
    const coordRanges = {
        south: { lon: [south_min_lon, south_max_lon], lat: [south_min_lat, south_max_lat] },
        east: { lon: [east_min_lon, east_max_lon], lat: [east_min_lat, east_max_lat] }
    };

    const range = coordRanges[region];
    const xRatio = (lon - range.lon[0]) / (range.lon[1] - range.lon[0]);
    const yRatio = (lat - range.lat[0]) / (range.lat[1] - range.lat[0]);

    const imgWidth = mapImg.offsetWidth;
    const imgHeight = mapImg.offsetHeight;
    const imgoffsetLeft = mapImg.offsetLeft;
    const imgoffsetTop = mapImg.offsetTop;
    marker.style.left = `${imgoffsetLeft + xRatio * imgWidth - 15}px`;
    marker.style.top = `${imgoffsetTop + (1 - yRatio) * imgHeight -30}px`;
}

function updatePosition() {
    const activeTab = getActiveTab();
    const lon = parseFloat(activeTab.querySelector('.longitude-select').value);
    const lat = parseFloat(activeTab.querySelector('.latitude-select').value);
    const marker = activeTab.querySelector('.map-marker');
    const mapImg = activeTab.querySelector('.map');
    const region = activeTab.querySelector('.location-select').value;
    if (!mapImg){
        return;
    }
    updateMarker(lon, lat, marker, mapImg, region);
}

// 更新图片位置
function updateImgBlockPosition(){
    const activeTab = getActiveTab();
    const imgBlock = activeTab.querySelector('.img-block');
    const imgBlockWidth = imgBlock.offsetWidth;
    const mapImg = imgBlock.querySelector('.map');
    mapImg.style.left = `${imgBlockWidth / 2 - mapImg.offsetWidth / 2}px`;   
}

function updatelonlat() {
    const activeTab = getActiveTab();
    const locationSelect = activeTab.querySelector(".location-select");
    const longitudeSelect = activeTab.querySelector(".longitude-select");
    const latitudeSelect = activeTab.querySelector(".latitude-select");
    const map = activeTab.querySelector(".map");
    const selectedRegion = locationSelect.value;

    longitudeSelect.innerHTML = "";
    latitudeSelect.innerHTML = "";

    if (selectedRegion === "east") {
        createCoordinateSelect(".longitude-select", east_min_lon, east_max_lon, lon_lat_interval); // 东海经度范围
        createCoordinateSelect(".latitude-select", east_max_lat, east_min_lat, lon_lat_interval);   // 东海纬度范围
        map.src = "./images/east.png"
    } else if (selectedRegion === "south") {
        createCoordinateSelect(".longitude-select", south_min_lon, south_max_lon, lon_lat_interval); // 南海经度范围
        createCoordinateSelect(".latitude-select", south_max_lat, south_min_lat, lon_lat_interval);    // 南海纬度范围
        map.src = "./images/south.png"
    }
    updateImgBlockPosition();
    updatePosition();
}

function updatesstlonlat() {
    const activeTab = getActiveTab();
    const locationSelect = activeTab.querySelector(".sst-location-select");
    const longitudeSelect = activeTab.querySelector(".longitude-select");
    const latitudeSelect = activeTab.querySelector(".latitude-select");
    const selectedRegion = locationSelect.value;

    longitudeSelect.innerHTML = "";
    latitudeSelect.innerHTML = "";

    if (selectedRegion === "east") {
        createCoordinateSelect(".longitude-select", 123, 126, lon_lat_interval); // 东海经度范围
        createCoordinateSelect(".latitude-select", 31.5, 25.5, lon_lat_interval);   // 东海纬度范围
    } else if (selectedRegion === "south") {
        createCoordinateSelect(".longitude-select", 110, 117, lon_lat_interval); // 南海经度范围
        createCoordinateSelect(".latitude-select", 18, 9, lon_lat_interval);    // 南海纬度范围
    }
}

// 更新经纬度选择器选项
document.addEventListener("DOMContentLoaded", function () {
    updateImgBlockPosition();
    updatePosition();
    document.querySelectorAll(".location-select").forEach(locationSelect => {
        locationSelect.addEventListener("change", updatelonlat);
    });

    document.querySelectorAll(".sst-location-select").forEach(locationSelect => {
        locationSelect.addEventListener("change", updatesstlonlat);
    });
    
    document.querySelectorAll(".longitude-select").forEach(longitudeSelect => {
        longitudeSelect.addEventListener('change', updatePosition);
    });

    document.querySelectorAll(".latitude-select").forEach(latitudeSelect => {
        latitudeSelect.addEventListener('change', updatePosition);
    });
});

function formatDateTime(date) {
    const pad = (num) => num.toString().padStart(2, '0');
    return `${date.getFullYear()}-${pad(date.getMonth()+1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
}

// 创建Vue应用
const { createApp, ref } = Vue;

// 更新预测时间
function updatePredictTime(time){
    const activeTab = getActiveTab(); 
    const variable_name = activeTab.getAttribute('id');
    step = params_for_predicttime[variable_name]["step"]
    length = params_for_predicttime[variable_name]["length"]
    predict_start_hour = params_for_predicttime[variable_name]["predict_start_hour"]

    const name = activeTab.getAttribute('id');
    select_input_start_time[name] = time;
    select_predict_time = activeTab.querySelector('.predict-time');
    select_predict_time.innerHTML = ''
    time = new Date(time);
    predict_start_time = time.setHours(time.getHours() + predict_start_hour);
    for (let i = 0; i < length; i++) {
        const option = document.createElement('option');
        temptime = new Date(predict_start_time);
        temptime.setMinutes(temptime.getMinutes() + i * step);
        option.value = i;
        option.textContent = `${i.toString().padStart(2, '0')} : ${formatDateTime(new Date(temptime))}`;
        select_predict_time.appendChild(option);
    }
}

function initDatePickers() {
    document.querySelectorAll('.input-start-datetime').forEach(container => {
        const app = createApp({
            setup() {
                const timeValue = ref(start_date);
                updatePredictTime(timeValue.value);
                return { timeValue, updatePredictTime };
            }
        });
        app.use(TDesign);
        app.mount(container);
    });
}

document.addEventListener('DOMContentLoaded', initDatePickers);

function initInquireButton() {   
    document.querySelectorAll('.profile-inquire-button').forEach(button => {
        button.addEventListener('click', async function() {
            const activeTab = getActiveTab(); 
            const need_prompt = activeTab.querySelector('.need-prompt').checked;
            const name = activeTab.getAttribute('id');
            const variable = name == "temperature" ? "t" : "r";
            let area, longitude, latitude, input_start_time, predict_time;
            
            if (!need_prompt){
                area = activeTab.querySelector('.location-select').value;
                longitude = activeTab.querySelector('.longitude-select').value;
                latitude = activeTab.querySelector('.latitude-select').value;
                input_start_time = select_input_start_time[name];
                predict_time = activeTab.querySelector('.predict-time').value;
            }else{
                const test_case_index = activeTab.querySelector('.test-cases').value;
                area = test_cases[name][test_case_index]['area'];
                input_start_time = test_cases[name][test_case_index]['input_start_time'];
                predict_time = test_cases[name][test_case_index]['predict_time'];
                longitude = -1;
                latitude = -1;
            }
            const params = new URLSearchParams({
                area: area,
                variable: variable,
                longitude: longitude,
                latitude: latitude,
                input_start_time: input_start_time,
                predict_time: predict_time,
                need_prompt: need_prompt
            });
            const overlay = document.getElementById('loading-overlay');
            overlay.style.display = 'flex';
            const timeout = setTimeout(() => {
                overlay.style.display = 'none';
                alert('请求超时，请稍后重试');
            }, 20000);

            try{
                const response = await fetch(`http://${dst_ip}:8000/profile/?${params}`);
                data = await response.json();
            }catch(error){
                console.log(error);
                alert(`请求失败：${error.message}`);
            }finally{ 
                overlay.style.display = 'none';
                clearTimeout(timeout);
                const legend = activeTab.querySelector('.legend');
                legend.style.display = "block";
            }
            
            const isobaric = data.isobaric;
            const pred_data = data.pred_data;
            const gt_data = data.gt_data;
            const mre = data.mre;
            const nearest_distance = data.nearest_distance;
            const target_lat = data.target_lat;
            const target_lon = data.target_lon;
            const pred_time = data.pred_time;
            const img_url = data.img_url;
            const map = activeTab.querySelector('.map');
            map.src = img_url;

            const test_case_description = activeTab.querySelector('.test-case-description');
            test_case_description.style.paddingBottom = '15px';
            let area_chinese;
            if (area == 'south') {
                area_chinese = '南海';
            } else {
                area_chinese = '东海';
            }
            test_case_description.textContent = `预测区域: ${area_chinese}, 目标经度: ${target_lon}°E, 目标纬度: ${target_lat}°N, 输入数据开始时间: ${input_start_time}, 预测时间: ${predict_time}:${pred_time}`
            
            const image_block = activeTab.querySelector('.img-block');
            image_block.style.height = 330 + 'px';
            const location_description = activeTab.querySelector('.location-description');
            location_description.textContent = `目标经度: ${target_lon}°E, 目标纬度: ${target_lat}°N, 该点与最近的网格点相距${nearest_distance.toFixed(2)}km`;

            const list_length = isobaric.length;
            const dataSet1 = [];
            const dataSet2 = [];
            const step = 925 / list_length;
            const maxValue = Math.max(Math.max(...pred_data), Math.max(...gt_data));
            const minValue = Math.min(Math.min(...pred_data), Math.min(...gt_data));
            for (i = 0; i < list_length; i++){
                scaled_pred_data = (pred_data[i] - minValue) / (maxValue - minValue);
                y_pred = 450 - 65 - scaled_pred_data * 320;
                dataSet1.push({x: 50 + step*i, y: y_pred, label: isobaric[i], value: pred_data[i]});
                
                scaled_gt_data = (gt_data[i] - minValue) / (maxValue - minValue);
                y_gt = 450 - 65 - scaled_gt_data * 320;
                dataSet2.push({x: 50 + step*i, y: y_gt, label: isobaric[i], value: gt_data[i]});
            }

            const chart = activeTab.querySelector('.chart');
            const line1 = chart.querySelector('.line1');
            const line2 = chart.querySelector('.line2');
            const points1 = chart.querySelector('.points1');
            const points2 = chart.querySelector('.points2');
            const tooltip = activeTab.querySelector('.tooltip');
            const gridLines = chart.querySelector('g:first-child');
            const xAxisLabels = chart.querySelector('g.x-axis-labels');
            const yAxisLabels = chart.querySelector('g.y-axis-labels');
            
            // 创建折线路径
            function createPolyline(data) {
                return data.map(point => `${point.x},${point.y}`).join(' ');
            }

            function createGridLines(){
                gridLines.innerHTML = '';
                xAxisLabels.innerHTML = '';
                yAxisLabels.innerHTML = '';
                // 垂直网格线
                font_color = 'black';
                font_size = 12;
                font_weight = '500';
                vertical_interval = Math.ceil(list_length / 14);
                for (let i = 0; i < list_length / vertical_interval; i++) {
                    const line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                    line.setAttribute('x1', 50 + vertical_interval * step * i);
                    line.setAttribute('y1', 0);
                    line.setAttribute('x2', 50 + vertical_interval * step * i);
                    line.setAttribute('y2', 450);
                    line.setAttribute('stroke', 'gray');
                    gridLines.appendChild(line);

                    const text = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                    text.setAttribute('x', 50 + vertical_interval * step * i);
                    text.setAttribute('y', 463);
                    text.setAttribute('text-anchor', 'middle');
                    text.setAttribute('font-size', `${font_size}px`);
                    text.setAttribute('font-weight', font_weight);
                    text.setAttribute('fill', font_color);
                    text.textContent = isobaric[i * vertical_interval].toFixed(2);
                    xAxisLabels.appendChild(text);
                }

                // 水平网格线
                pixel_per_value = 320 / (maxValue - minValue);
                floor = Math.floor(minValue / 10) * 10;
                ceiling = Math.ceil(maxValue / 10) * 10;
                interval = 10;
                while ((ceiling - floor) * pixel_per_value > 400) {
                    interval = Math.floor(interval / 2);
                    floor = Math.floor(minValue / interval) * interval;
                    ceiling = Math.ceil(maxValue / interval) * interval;
                }

                count = (ceiling - floor) / interval;
                while (count > 15) {
                    interval += 10;
                    count = (ceiling - floor) / interval;
                }

                line_start_y = 450 - 65 + (minValue - floor) * pixel_per_value;
                for (let i = 0; i <= count; i++) {
                    y_coordinate = line_start_y - i * interval * pixel_per_value;
                    const line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                    line.setAttribute('x1', 50);
                    line.setAttribute('y1', y_coordinate);
                    line.setAttribute('x2', 975);
                    line.setAttribute('y2', y_coordinate);
                    line.setAttribute('stroke', 'gray');
                    gridLines.appendChild(line);

                    const text = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                    text.setAttribute('x', 47);
                    text.setAttribute('y', y_coordinate);
                    text.setAttribute('text-anchor', 'end');
                    text.setAttribute('dominant-baseline', 'middle');
                    text.setAttribute('alignment-baseline', 'central'); 
                    text.setAttribute('font-size', `${font_size}px`);
                    text.setAttribute('font-weight', font_weight);
                    text.setAttribute('fill', font_color);
                    text.textContent = floor + i * interval;
                    yAxisLabels.appendChild(text);
                }
            }

            // 显示工具提示
            function showTooltip(event) {
                const circle = event.target;
                const label = circle.getAttribute('data-label');
                const value = circle.getAttribute('data-value');
                const dataset = circle.getAttribute('data-dataset');
                const color = dataset === '预测值' ? '#00d2ff' : '#ff6b6b';
                
                tooltip.innerHTML = `
                    <div class="tooltip-title" style="color: ${color};">${dataset}</div>
                    <div class="tooltip-label">
                        <span>气压:</span>
                        <span>${parseFloat(label).toFixed(2)} hPa</span>
                    </div>
                    <div class="tooltip-value">
                        <span>数值:</span>
                        <span>${parseFloat(value).toFixed(2)}</span>
                    </div>
                `;
                tooltip.style.opacity = 1;
            }
            
            // 移动工具提示
            function moveTooltip(event) {
                const chartRect = chart.getBoundingClientRect();
                const x = event.clientX - chartRect.left;
                const y = event.clientY - chartRect.top;
                
                tooltip.style.left = `${Math.min(670, x + 20)}px`;
                tooltip.style.top = `${y - 15}px`;
            }
            
            // 隐藏工具提示
            function hideTooltip() {
                tooltip.style.opacity = 0;
            }

            function createPoints(data, group, color, datasetName) {
                data.forEach(point => {
                    const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
                    circle.setAttribute('cx', point.x);
                    circle.setAttribute('cy', point.y);
                    circle.setAttribute('r', 5);
                    circle.setAttribute('fill', color);
                    circle.setAttribute('stroke', 'white');
                    circle.setAttribute('stroke-width', '2');
                    circle.setAttribute('data-label', point.label);
                    circle.setAttribute('data-value', point.value);
                    circle.setAttribute('data-dataset', datasetName);
                    
                    // 鼠标事件
                    circle.addEventListener('mouseover', showTooltip);
                    circle.addEventListener('mousemove', moveTooltip);
                    circle.addEventListener('mouseout', hideTooltip);
                    
                    group.appendChild(circle);
                });
            }

            function createChart() {
                while (points1.firstChild) {
                    points1.removeChild(points1.firstChild);
                }
                while (points2.firstChild) {
                    points2.removeChild(points2.firstChild);
                }
                line1.setAttribute('points', createPolyline(dataSet1));
                line2.setAttribute('points', createPolyline(dataSet2));

                const mreInfo = chart.querySelector('.mre-info');
                if (mreInfo) {
                    mreInfo.textContent = `平均相对误差:${mre.toFixed(3)}%`;
                }
                
                createPoints(dataSet1, points1, '#00d2ff', '预测值');
                createPoints(dataSet2, points2, '#ff6b6b', '真值');
                createGridLines();
            }

            createChart();

        });
    });

    document.querySelectorAll('.sst-inquire-button').forEach(button => {
        if (button.getAttribute('data-name') == 'forward' || button.getAttribute('data-name') == 'backward') { 
            button.disabled = true;
        }
        button.addEventListener('click', async function() {
            const activeTab = getActiveTab(); 
            const variable_name = activeTab.getAttribute('id');
            const placeholdingDiv = activeTab.querySelector('.placeholding-in-rightcolumn');
            console.log(activeTab);
            console.log(placeholdingDiv)

            placeholdingDiv.style.display = 'none';
            const name = this.getAttribute('data-name');

            let area = null;
            let input_start_time = null;
            let predict_time = null;
            let need_prompt = null;
            let longitude = null;
            let latitude = null;
            
            if (name == "check"){
                need_prompt = activeTab.querySelector('.need-prompt').checked;
                if (!need_prompt){
                    area = activeTab.querySelector('.sst-location-select').value;
                    input_start_time = select_input_start_time[variable_name];
                    longitude = activeTab.querySelector('.longitude-select').value;
                    latitude = activeTab.querySelector('.latitude-select').value;

                    const predict_time_select = activeTab.querySelector('.predict-time');
                    predict_time = predict_time_select.selectedIndex;
                    // const optional_predict_time = predict_time_select.options;
                    // selectedOptions = predict_time_select.options[predict_time_select.selectedIndex].textContent;
                
                    params_for_sst[variable_name]["area"] = area;
                    params_for_sst[variable_name]["input_start_time"] = input_start_time;
                    params_for_sst[variable_name]["predict_time"] = predict_time;
                    params_for_sst[variable_name]["need_prompt"] = need_prompt;
                    // params_for_sst["optional_predict_time"] = optional_predict_time;
                    params_for_sst[variable_name]["longitude"] = longitude;
                    params_for_sst[variable_name]["latitude"] = latitude;
                }else {
                    const test_case_index = activeTab.querySelector('.test-cases').value;
                    area = test_cases["sst"][test_case_index]['area'];
                    input_start_time = test_cases["sst"][test_case_index]['input_start_time_index'];
                    predict_time = test_cases["sst"][test_case_index]['predict_time'];
                    longitude = -1;
                    latitude = -1;
                }

                // activeTab.querySelectorAll('.sst-inquire-button').forEach(button => {
                //     if (button.getAttribute('data-name') == 'forward') { 
                //         if (need_prompt){
                //             button.disabled = true;
                //         }else if (predict_time == 0){
                //             button.disabled = true;
                //         }else{
                //             button.disabled = false;
                //         }
                //     }
                //     if (button.getAttribute('data-name') == 'backward') { 
                //         if (need_prompt){
                //             button.disabled = true;
                //         }else if (predict_time == 23){
                //             button.disabled = true;
                //         }else{
                //             button.disabled = false;
                //         }
                //     }
                // });
            }else{
                if (name == "forward"){
                    predict_time = params_for_sst[variable_name]["predict_time"] - 1;
                }else if (name == "backward"){
                    predict_time = params_for_sst[variable_name]["predict_time"] + 1;
                }
                params_for_sst[variable_name]["predict_time"] = predict_time;
                area = params_for_sst[variable_name]["area"];
                input_start_time = params_for_sst[variable_name]["input_start_time"];
                need_prompt = params_for_sst[variable_name]["need_prompt"];
                // selectedOptions = params_for_sst["optional_predict_time"][predict_time].textContent;
                longitude = params_for_sst[variable_name]["longitude"];
                latitude = params_for_sst[variable_name]["latitude"];

                // activeTab.querySelectorAll('.sst-inquire-button').forEach(button => {
                //     if (button.getAttribute('data-name') == 'forward') { 
                //         if (predict_time == 0){
                //             button.disabled = true;
                //         }else{
                //             button.disabled = false;
                //         }
                //     }
                //     if (button.getAttribute('data-name') == 'backward') { 
                //         if (predict_time == 23){
                //             button.disabled = true;
                //         }else{
                //             button.disabled = false;
                //         }
                //     }
                // });
            }

            const params = new URLSearchParams({
                area: area,
                variable: variable_name,
                input_start_time: input_start_time,
                predict_time: predict_time,
                need_prompt: need_prompt,
                longitude: longitude,
                latitude: latitude,
            });

            const overlay = document.getElementById('loading-overlay');
            overlay.style.display = 'flex';
            const timeout = setTimeout(() => {
                overlay.style.display = 'none';
                alert('请求超时，请稍后重试');
            }, 30000);

            // 获取图片数据
            try{
                const response = await fetch(`http://${dst_ip}:8000/profile/?${params}`)
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                // const blob = await response.blob();
                // const objectUrl = URL.createObjectURL(blob);

                const data = await response.json();
                const error = data.error;
                if (error == "true"){
                    const error_message = data.message;
                    alert(error_message);
                    return;
                }
                
                const objectUrl = data.img_url;
                const gt_data = data.gt_data;
                const pred_data = data.pred_data;
                const mre = data.mre;
                const target_lat = data.target_lat;
                const target_lon = data.target_lon;
                const input_start_time = data.input_start_time;
                const selectedOptions = data.pred_time;

                const sst_data_description = activeTab.querySelector('.data-description');
                let area_chinese = "南海";
                if (area == "east"){
                    area_chinese = "东海";
                }
                sst_data_description.innerHTML = `预测区域: ${area_chinese} <br> 
                                                  预测位置经度: ${target_lon.toFixed(3)}°E <br> 
                                                  预测位置纬度: ${target_lat.toFixed(3)}°N <br>
                                                  输入数据开始时间: ${input_start_time} <br>
                                                  预测时间: ${selectedOptions}</br>
                                                  真值: ${gt_data.toFixed(3)}℃ <br> 
                                                  预测值: ${pred_data.toFixed(3)}℃ <br> 
                                                  <span style="color: red;">空间分辨率: 2km </span><br>
                                                  <span style="color: red;">相对误差: ${mre.toFixed(3)}%</span>`;
                sst_data_description.style.borderRadius = '12px';
                sst_data_description.style.display = 'block';
                sst_data_description.style.padding = "10px";
                sst_data_description.style.justifyContent = 'center';
                sst_data_description.style.alignItems = 'center';


                // 创建图片元素并显示
                const img = document.createElement('img');
                img.src = objectUrl;
                img.style.width = '90%'
                img.style.padding = "10px 0 20px 0"
                
                // 添加到页面
                imgContainer = activeTab.querySelector('.img-container')
                imgContainer.innerHTML = '';
                const title = document.createElement('h3');
                title.innerText = selectedOptions;
                imgContainer.appendChild(title);
                imgContainer.appendChild(img);
                
                // 清理对象URL（在图片加载后）
                img.onload = function() {
                    URL.revokeObjectURL(objectUrl);
                };
            }catch(error) {
                console.log(error);
                alert(`请求失败：${error.message}`);
            }finally { 
                overlay.style.display = 'none';
                clearTimeout(timeout);
            }
            if (name == "check"){
                activeTab.querySelectorAll('.sst-inquire-button').forEach(button => {
                    if (button.getAttribute('data-name') == 'forward') { 
                        if (need_prompt){
                            button.disabled = true;
                        }else if (predict_time == 0){
                            button.disabled = true;
                        }else{
                            button.disabled = false;
                        }
                    }
                    if (button.getAttribute('data-name') == 'backward') { 
                        if (need_prompt){
                            button.disabled = true;
                        }else if (predict_time == 23){
                            button.disabled = true;
                        }else{
                            button.disabled = false;
                        }
                    }
                });
            }else{
                activeTab.querySelectorAll('.sst-inquire-button').forEach(button => {
                    if (button.getAttribute('data-name') == 'forward') { 
                        if (predict_time == 0){
                            button.disabled = true;
                        }else{
                            button.disabled = false;
                        }
                    }
                    if (button.getAttribute('data-name') == 'backward') { 
                        if (predict_time == 23){
                            button.disabled = true;
                        }else{
                            button.disabled = false;
                        }
                    }
                });
            }
        });
    });
}

document.addEventListener('DOMContentLoaded', initInquireButton);

// function initCheckboxBehavior() {
//     document.querySelectorAll('.need-prompt').forEach(checkbox => {
//         checkbox.addEventListener('change', function() {
//             updateFormState(this);
//         });
//     });
// }

// function updateFormState(changedCheckbox) {
//     const activeTab = getActiveTab();
//     const isChecked = changedCheckbox.checked;
    
//     // 禁用或启用input-left-column中的组件
//     const inputLeftColumn = activeTab.querySelector('.input-left-column');
//     if (inputLeftColumn) {
//         // const inputsToDisable = inputLeftColumn.querySelectorAll(
//         //     'select:not(.test-cases), input:not(.need-prompt), .t-date-picker, .custom-checkbox:not(.test-cases)'
//         // );
//         const inputsToDisable = inputLeftColumn.querySelectorAll(
//             '.custom-checkbox, .t-date-picker'
//         );

//         const datePickers = inputLeftColumn.querySelectorAll('.t-date-picker');
//         datePickers.forEach(picker => {
//             if (isChecked) {
//                 picker.setAttribute('disabled', '');
//                 picker.classList.add('t-date-picker--disabled');
//             } else {
//                 picker.removeAttribute('disabled');
//                 picker.classList.remove('t-date-picker--disabled');
//             }
//         });
        
//         inputsToDisable.forEach(element => {
//             element.disabled = isChecked;
//         });
//     }
    
//     // 控制测试用例下拉框
//     const testCasesSelect = activeTab.querySelector('.test-cases');
//     if (testCasesSelect) {
//         testCasesSelect.disabled = !isChecked;
//     }
// }

// // 在页面加载完成后初始化
// document.addEventListener('DOMContentLoaded', function() {
//     initCheckboxBehavior();
// });