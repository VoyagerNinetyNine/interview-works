import os
import requests
from pathlib import Path

# ===== 配置 =====
BASE_DIR = r"D:\\Download\\tilemap\\tilemap"
SIZE_THRESHOLD = 1024  # 小于1KB认为是坏图
TIMEOUT = 10

URL_TEMPLATE = "https://clarity.maptiles.arcgis.com/arcgis/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}?blankTile=false"

# ===== 核心函数 =====
def is_bad_tile(file_path):
    return os.path.getsize(file_path) < SIZE_THRESHOLD


def download_tile(z, x, y, save_path):
    url = URL_TEMPLATE.format(z=z, x=x, y=y)
    try:
        r = requests.get(url, timeout=TIMEOUT)
        if r.status_code == 200 and len(r.content) > SIZE_THRESHOLD:
            with open(save_path, "wb") as f:
                f.write(r.content)
            print(f"[OK] {z}/{x}/{y}")
            return True
        else:
            print(f"[FAIL] 内容异常 {z}/{x}/{y}")
            return False
    except Exception as e:
        print(f"[ERROR] {z}/{x}/{y} -> {e}")
        return False


def fix_tiles():
    base = Path(BASE_DIR)

    for z_dir in base.iterdir():
        if not z_dir.is_dir():
            continue
        z = int(z_dir.name)

        for x_dir in z_dir.iterdir():
            if not x_dir.is_dir():
                continue
            x = int(x_dir.name)

            for file in x_dir.glob("*.png"):
                if not is_bad_tile(file):
                    continue

                # 当前是 y2（TMS）
                y2 = int(file.stem)

                # 转换为标准 y（XYZ）
                y = (2 ** z - 1) - y2

                print(f"[FIX] {z}/{x}/{y2} -> online y={y}")

                success = download_tile(z, x, y, file)

                if not success:
                    print(f"[SKIP] 修复失败: {file}")


if __name__ == "__main__":
    fix_tiles()