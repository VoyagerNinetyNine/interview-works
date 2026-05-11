<template>
    <div class="map-container">
        <div ref="deckContainer" class="deckgl-container"></div>
        <div v-if="loading" class="loading">正在加载地图数据...</div>
    </div>
</template>

<script setup name="ScatterMap" lang="js" > 
import { ref, onMounted, watch } from 'vue';
import { Deck, TileLayer } from 'deck.gl';
import { GeoJsonLayer, ScatterplotLayer, IconLayer, BitmapLayer } from '@deck.gl/layers';
import { useInputStore } from '@/store/userinput'
import { useOutputStore } from '@/store/output'
import { color } from 'echarts';
import L from 'leaflet';
const inputStore = useInputStore()
const outputStore = useOutputStore()

const INITIAL_VIEW_STATE = {
    longitude: 116, // 经度
    latitude: 19,   // 纬度
    zoom: 3,        // 初始缩放级别 (根据数据范围可能需要调整)
    pitch: 0,
    bearing: 0
};

const deckContainer = ref(null);
const loading = ref(true);
let deckInstance = null;
let hasInited = ref(false)

let VIRIDIS;
let coordinates = {};
let scatterLayer = {};
let pixels = {};
let scatterData = {};

const leafletmap = ref(null);
let map;
let marker;

const ICON_MAPPING = {
    marker: { 
        x: 0, y: 0, 
        width: 96, height: 96, 
        anchorY: 96,
        anchorX: 48,
        mask: false 
    }
};

const MAP_LIMITS = {
    minLongitude: 73,
    maxLongitude: 135,
    minLatitude: 3,
    maxLatitude: 53
};

const handleViewStateChange = ({ viewState }) => {
    viewState.longitude = Math.min(
        MAP_LIMITS.maxLongitude,
        Math.max(MAP_LIMITS.minLongitude, viewState.longitude)
    );
    viewState.latitude = Math.min(
        MAP_LIMITS.maxLatitude,
        Math.max(MAP_LIMITS.minLatitude, viewState.latitude)
    );
    viewState.zoom = Math.max(2, Math.min(8, viewState.zoom));
};

function onMapClick(info, event) { 
    if (event.srcEvent && event.srcEvent.button !== 0) {
        return;
    }
    inputStore.userLon = info.coordinate[0]
    inputStore.userLat = info.coordinate[1]
    if( inputStore.submittedValues.displayArea.size === 0){
        ElMessage.warning("请先点击预测按钮完成预测，然后点击地图查看结果");
        return;
    }
    // if(!(inputStore.userInputParam === inputStore.submittedValues.param && inputStore.userInputEndTime === inputStore.submittedValues.time)){
    //     ElMessage.warning("界面中的参数已改变，但是未点击预测按钮，仍然使用旧参数");
    // }
    if(["temperature", "humidity"].includes(inputStore.userInputParam)){
        outputStore.getPointData("all", true, true);
    }else{
        outputStore.getPointData("timeline", true, true);
    }
}

async function loadViridis() {
    const res = await fetch('/viridis.bin');
    const buffer = await res.arrayBuffer();
    return new Uint8Array(buffer);
}

async function loadData(dataurl) {
    const res = await fetch(dataurl);
    const buffer = await res.arrayBuffer();
    return new Uint8Array(buffer);
}

async function loadCoordinates(coordinatesUrl) {
    const coordinates = await fetch(coordinatesUrl);
    const coordinatesBuffer = await coordinates.arrayBuffer();
    const coordinatesArray = new Float32Array(coordinatesBuffer);
    return coordinatesArray;
}

function updateLayers(newLayers) {
    const currentLayers = deckInstance.props.layers;
    const merged = [];
    for (const layer of currentLayers) {
        const shouldReplace = newLayers.some(nl => nl.id === layer.id);
        if (!shouldReplace) {
            merged.push(layer);
        }
    }
    merged.push(...newLayers);
    deckInstance.setProps({
        layers: merged
    });
}

async function addScatterLayer() { 
    for(const area of outputStore.areas) {
        coordinates[area] = await loadCoordinates(outputStore.coordinate_urls[`${area}_${inputStore.userInputParam}`]);
    }
    for(const area of outputStore.areas){
        const length = coordinates[area].length / 2;
        const indices = Array.from({ length }, (_, index) => index);
        if (inputStore.userInputParam === "sst"){
            const mask = await loadData(outputStore.sst_mask_urls[area]);
            scatterLayer[area] = new ScatterplotLayer({
                id: `${area}-scatter-layer`,
                data: indices,
                pickable: true,
                opacity: 0.8,
                getPosition: d => [coordinates[area][d * 2], coordinates[area][d * 2 + 1]], 
                getFillColor: d =>{
                    if(mask[d] === 0){
                        return [0, 0, 0, 0];
                    }
                    return [72, 118, 214];
                },
                radiusMinPixels: 1.5,
                radiusMaxPixels: 100,
            });
        }else{
            scatterLayer[area] = new ScatterplotLayer({
                id: `${area}-scatter-layer`,
                data: indices,
                pickable: true,
                opacity: 0.8,
                getPosition: d => [coordinates[area][d * 2], coordinates[area][d * 2 + 1]], 
                getFillColor: [72, 118, 214],
                radiusMinPixels: 1.5,
                radiusMaxPixels: 100,
            });
        }
        
    }
    updateLayers(Object.values(scatterLayer));
}

watch(() => [inputStore.userLon, inputStore.userLat], () => {
    const iconlayer = new IconLayer({
        id: 'IconLayer',
        data: [{position: [inputStore.userLon, inputStore.userLat]}],
        iconAtlas: '/maps/marker.png',
        getIcon: d => 'marker',
        getPosition: d => d.position,
        getSize: d => 40,
        sizeScale: 1,
        iconMapping: ICON_MAPPING,
        pickable: true
    });
    updateLayers([iconlayer]);
});

watch(()=>[outputStore.hasLoaded, hasInited.value], ()=>{
    if(outputStore.hasLoaded && hasInited.value){
        addScatterLayer();
    }
}, {immediate: true})

watch(()=>inputStore.userInputParam, async ()=>{
    outputStore.clearData();
    // addScatterLayer();
})

watch(() => [inputStore.userPredictTimeIndex, outputStore.scatterData], ()=>{
    if(Object.keys(outputStore.scatterData).length === 0){
        addScatterLayer();
    }
    for(const [key, value] of Object.entries(outputStore.scatterData)){
        const length = coordinates[key].length / 2;
        const indices = Array.from({ length }, (_, index) => index);
        const img_data = Array.from(value.slice(inputStore.userPredictTimeIndex * length, (inputStore.userPredictTimeIndex + 1) * length));
        scatterLayer[key] = new ScatterplotLayer({
            id: `${key}-scatter-layer`,
            data: indices,
            pickable: true,
            opacity: 0.8,
            getPosition: d => [coordinates[key][d * 2], coordinates[key][d * 2 + 1]], 
            getFillColor: d => {
                    const idx = img_data[d];
                    if (idx === 0) return [0, 0, 0, 0];
                    return [
                        VIRIDIS[idx * 3],
                        VIRIDIS[idx * 3 + 1],
                        VIRIDIS[idx * 3 + 2],
                        255
                    ];
                },
            radiusMinPixels: 1.5,
            radiusMaxPixels: 100,
        });
    }
    updateLayers(Object.values(scatterLayer));
}, {deep: true})


watch(() => inputStore.userIsobaricIndex, async ()=>{
    inputStore.overlay = true;
    for (const area of inputStore.submittedValues.displayArea) {
        await outputStore.getImgData(area, true);
    }
    await outputStore.getPointData("timeline", false, true);
    inputStore.overlay = false;
}, {deep: true})


const initMap = async () => {
    if (!deckContainer.value) return;
    VIRIDIS = await loadViridis();
    try{
        let response = await fetch('/maps/display.json');
        if (!response.ok) throw new Error('无法加载 GeoJSON 数据');
        const geodata = await response.json();
        
        const geoJsonLayer = new GeoJsonLayer({
            id: 'geojson-layer',
            data: geodata,
            filled: true,       // 是否填充
            stroked: true,
            filled: false,
            lineWidthMinPixels: 1,
            lineWidthUnits: 'pixels',
            getLineColor: [255, 255, 255, 100],            // 边框颜色
            getLineWidth: 1,
            pickable: true,
        });

        const tilemaplayer = new TileLayer({
            id: 'tilemap-layer',
            getTileData: ({ index, signal }) => {
                const { x, y, z } = index;
                const y2 = Math.pow(2, z) - 1 - y;
                return `/maps/tilemap/${z}/${x}/${y2}.png`;
                // return `https://clarity.maptiles.arcgis.com/arcgis/rest/services/World_Imagery/MapServer/tile/${z}/${y}/${x}?blankTile=false`
            },
            // data: 'https://webst01.is.autonavi.com/appmaptile?style=6&x={x}&y={y}&z={z}',
            // data: 'https://clarity.maptiles.arcgis.com/arcgis/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}?blankTile=false',
            minZoom: 0,
            maxZoom: 10,
            tileSize: 256,
            opacity: 0.8,
            pickable: true,
            onTileError: () => {} ,// 忽略瓦片加载错误
            renderSubLayers: props => {
                const { data, tile } = props;
                if (!data) return null;
                return new BitmapLayer({
                    id: `bitmap-${props.id}`,
                    data: null,
                    image: data,
                    bounds: [tile.bbox.west, tile.bbox.south, tile.bbox.east, tile.bbox.north],
                    opacity: 0.8,
                    pickable: true
                });
            }
        });

        deckInstance = new Deck({
            initialViewState: INITIAL_VIEW_STATE,
            controller: true,
            layers: [tilemaplayer, geoJsonLayer],
            parent: deckContainer.value,
            onClick: onMapClick,
            onViewStateChange: handleViewStateChange,     
        });

        loading.value = false;
        hasInited.value = true
    } catch (error) {
        console.error('地图初始化失败:', error);
        loading.value = false;
    }
};

onMounted(() => {
    initMap();
});
</script>


<style scoped>
.map-container {
    position: relative;
    width: 100%;
    height: 100%;
    overflow: hidden;
    background: #0b0e14;
}

.deckgl-container {
    position: absolute;
    width: 100%;
    height: 100%;
}

.deckgl-container::after {
  content: "";
  position: absolute;
  inset: 0;
  box-shadow: inset 0 0 150px rgba(0, 0, 0, 0.8);
  pointer-events: none; /* 确保不影响鼠标操作地图 */
  z-index: 999;
}

.loading {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: rgba(255, 255, 255, 0.8);
    padding: 10px 20px;
    border-radius: 4px;
    z-index: 10;
    font-family: sans-serif;
}
</style>

