package com.example.aircraftwar.application;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.example.aircraftwar.R;
import com.example.aircraftwar.aircraft.BossEnemy;
import com.example.aircraftwar.aircraft.EliteEnemy;
import com.example.aircraftwar.aircraft.HeroAircraft;
import com.example.aircraftwar.aircraft.MobEnemy;
import com.example.aircraftwar.bullet.EnemyAbstractBullet;
import com.example.aircraftwar.bullet.HeroAbstractBullet;
import com.example.aircraftwar.prop.BloodSupplyProp;
import com.example.aircraftwar.prop.BombSupplyProp;
import com.example.aircraftwar.prop.FireSupplyProp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 综合管理图片的加载，访问
 * 提供图片的静态访问方法
 *
 * @author hitsz
 */
public class ImageManager {

    /**
     * 类名-图片 映射，存储各基类的图片 <br>
     * 可使用 CLASSNAME_IMAGE_MAP.get( obj.getClass().getName() ) 获得 obj 所属基类对应的图片
     */
    private static final Map<String, Bitmap> CLASSNAME_IMAGE_MAP = new HashMap<>();

    public static Bitmap BACKGROUND_IMAGE;
    public static Bitmap HERO_IMAGE;
    public static Bitmap HERO_BULLET_IMAGE;
    public static Bitmap ENEMY_BULLET_IMAGE;
    public static Bitmap MOB_ENEMY_IMAGE;
    public static Bitmap ELITE_ENEMY_IMAGE;
    public static Bitmap PROP_BLOOD_IMAGE;
    public static Bitmap PROP_BOMB_IMAGE;
    public static Bitmap PROP_BULLET_IMAGE;
    public static Bitmap BOSS_IMAGE;
    public static ArrayList<Bitmap> HEROIMAGES;
    public static Resources resources;

    public static void loadimg(){

        BACKGROUND_IMAGE = BitmapFactory.decodeResource(getResource(),MainActivity.bgImagePath);
        float scaleW = (float)MainActivity.WINDOW_WIDTH/BACKGROUND_IMAGE.getWidth();
        float scaleH = (float)MainActivity.WINDOW_HEIGHT/BACKGROUND_IMAGE.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW,scaleH);
        BACKGROUND_IMAGE = Bitmap.createBitmap(BACKGROUND_IMAGE,0,0,BACKGROUND_IMAGE.getWidth(),BACKGROUND_IMAGE.getHeight(),matrix,true);

        loadHeroImage();
        MOB_ENEMY_IMAGE = BitmapFactory.decodeResource(getResource(),R.drawable.mob);
        HERO_BULLET_IMAGE = BitmapFactory.decodeResource(getResource(),MainActivity.heroBulletPath);
        ENEMY_BULLET_IMAGE = BitmapFactory.decodeResource(getResource(),R.drawable.bullet_enemy);
        ELITE_ENEMY_IMAGE = BitmapFactory.decodeResource(getResource(),R.drawable.elite);
        PROP_BLOOD_IMAGE = BitmapFactory.decodeResource(getResource(),R.drawable.prop_blood);
        PROP_BOMB_IMAGE = BitmapFactory.decodeResource(getResource(),R.drawable.prop_bomb);
        PROP_BULLET_IMAGE = BitmapFactory.decodeResource(getResource(),R.drawable.prop_bullet);
        BOSS_IMAGE = BitmapFactory.decodeResource(getResource(),R.drawable.boss);

        CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);
        CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(EliteEnemy.class.getName(), ELITE_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(HeroAbstractBullet.class.getName(), HERO_BULLET_IMAGE);
        CLASSNAME_IMAGE_MAP.put(EnemyAbstractBullet.class.getName(), ENEMY_BULLET_IMAGE);
        CLASSNAME_IMAGE_MAP.put(BloodSupplyProp.class.getName(), PROP_BLOOD_IMAGE);
        CLASSNAME_IMAGE_MAP.put(BombSupplyProp.class.getName(), PROP_BOMB_IMAGE);
        CLASSNAME_IMAGE_MAP.put(FireSupplyProp.class.getName(), PROP_BULLET_IMAGE);
        CLASSNAME_IMAGE_MAP.put(BossEnemy.class.getName(), BOSS_IMAGE);

    }

    public static Bitmap get(String className){
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    public static Bitmap get(Object obj){
        if (obj == null){
            return null;
        }
        return get(obj.getClass().getName());
    }

    public static void setResource(Resources sources){
        resources = sources;
    }

    public static Resources getResource(){
        return resources;
    }

    public static void loadHeroImage(){
        Bitmap heroimage = BitmapFactory.decodeResource(getResource(),MainActivity.heroImagePath);
        HEROIMAGES = new ArrayList<>();
        switch (MainActivity.heroImageNumber) {
            case 1: HERO_IMAGE = heroimage;
                    HEROIMAGES.add(heroimage);
            break;
            case 2: HERO_IMAGE = Bitmap.createBitmap(heroimage,0,0,heroimage.getWidth()/2,heroimage.getHeight());
                    HEROIMAGES.add(HERO_IMAGE);
                    HEROIMAGES.add(Bitmap.createBitmap(heroimage,heroimage.getWidth()/2,0,heroimage.getWidth()/2,heroimage.getHeight()));
            break;
            case 3: HERO_IMAGE = Bitmap.createBitmap(heroimage,0,0,heroimage.getWidth()/3,heroimage.getHeight());
                    HEROIMAGES.add(HERO_IMAGE);
                    HEROIMAGES.add(Bitmap.createBitmap(heroimage,heroimage.getWidth()/3,0,heroimage.getWidth()/3,heroimage.getHeight()));
                    HEROIMAGES.add(Bitmap.createBitmap(heroimage,2*heroimage.getWidth()/3,0,heroimage.getWidth()/3,heroimage.getHeight()));
            break;
        }
    }
}
