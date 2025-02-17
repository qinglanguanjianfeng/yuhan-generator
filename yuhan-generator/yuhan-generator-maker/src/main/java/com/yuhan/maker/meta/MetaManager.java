package com.yuhan.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

//读取json文件信息
public class MetaManager {

    //volatile关键字用于确保多线程环境下的内存可见性，从而确保一旦对meta对象进行了修改，其他线程都能看见
    private static volatile Meta meta;

    //双检索单例模式
    public static Meta getMetaObject() {
        //外层if不能去掉，因为锁比较消耗性能，必须在必要时候才去运行
        if(meta == null){
            //多线程访问会创建多个对象----加锁解决sychronized本地锁
            synchronized (MetaManager.class) {
                if (meta == null) {
                    meta = initMeta();
                }
            }
        }
        return meta;
    }

    //创建meta对象并通过json文件中的元信息进行初始化
    public static Meta initMeta(){
        String metaJson = ResourceUtil.readUtf8Str("springboot-init-meta.json");//hutool工具读取resources文件夹下的json
        Meta newMeta = JSONUtil.toBean(metaJson,Meta.class);//将字符串转化为对象
        //校验配置文件，处理默认值
        MetaValidator.doValidAndFill(newMeta);
        System.out.println(newMeta);
        return newMeta;
    }
}
