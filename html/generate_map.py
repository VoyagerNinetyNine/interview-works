import matplotlib.pyplot as plt
import cartopy.crs as ccrs
import cartopy.feature as cfeature
from cartopy.mpl.gridliner import LONGITUDE_FORMATTER, LATITUDE_FORMATTER
import numpy as np


def plot_map(min_lon, max_lon, min_lat, max_lat, output_filename='south.png', ratio=1):
    # 计算经纬度区域的长宽比
    lon_range = max_lon - min_lon
    lat_range = max_lat - min_lat
    fig_width = (lon_range * 4 * 8 * 2 + 1) / 100
    fig_height = (lat_range * 4 * 8 * 2 + 1) / 100
    
    # 创建画布和子图
    fig = plt.figure(figsize=(fig_width, fig_height))
    ax = fig.add_subplot(1, 1, 1, projection=ccrs.PlateCarree())
    
    # 设置地图范围
    ax.set_extent([min_lon, max_lon, min_lat, max_lat], crs=ccrs.PlateCarree())
    
    # 添加陆地和海洋颜色填充 - 使用更柔和的颜色
    ax.add_feature(cfeature.LAND, facecolor='#f0f0e0')  # 陆地浅灰色
    ax.add_feature(cfeature.OCEAN, facecolor='#d0e0f0')  # 海洋浅蓝色
    
    # 增强边界线可见性
    # 1. 添加海岸线 - 深蓝色，宽度增加
    ax.add_feature(cfeature.COASTLINE, linestyle='--', linewidth=1.2 / ratio, edgecolor='#004466')
    
    # 2. 添加国界 - 黑色，宽度增加
    ax.add_feature(cfeature.BORDERS, linestyle='--', linewidth=1.5 /ratio, edgecolor='black')

    # 3. 添加省界 - 深灰色，宽度增加
    ax.add_feature(cfeature.STATES, linestyle='--', linewidth=0.8 /ratio, edgecolor='#333333')
    
    # 移除所有文字和标签
    ax.set_xticks([])
    ax.set_yticks([])
    ax.spines['geo'].set_visible(False)  # 移除地图边框
    
    # 确保地图填满画布且比例正确
    plt.subplots_adjust(left=0, right=1, bottom=0, top=1)
    plt.savefig(output_filename, bbox_inches='tight', pad_inches=0, dpi=300)
    plt.close()


def check_data():
    file_path = "/home/ices/WeatherPrediction/display/backend/get_profile/datas/measured_data/south/profile/202304041400.npz"
    file = np.load(file_path)
    isobaric = file['isobaric']
    altitude = file['altitude']
    index = file['index']

    print(file.files)
    print(isobaric)
    print(altitude)
    print(index)

# south: 705x513, east:257x193
if __name__ == '__main__':
    # coordinate = {
    #     "east": [121, 127, 25, 33],
    #     "south": [107, 123, 3, 25]
    # }
    # for area in ["east", "south"]:
    #     plot_map(*coordinate[area], f"./images/{area}.png", 1 if area == "south" else 2)
    check_data()


        