package com.bjtu.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MyJedis {
    private JedisPool jedisPool;
    private Jedis jedis;
    private HashMap<String,HashMap<String,String>> actions;
    private HashMap<String,HashMap<String,String>> counters;
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式

    public MyJedis() throws Exception {
        actions = new HashMap<>();
        counters = new HashMap<>();

        jedis = JedisInstance.getInstance().getResource();
        init();

        FileMonitor m = new FileMonitor(500);//设置监控的间隔时间，初始化监听
        m.monitor(MyJedis.class.getClassLoader().getResource("./").getPath(), new FileListener(MyJedis.this)); //指定文件夹，添加监听
        m.start();//开启监听


    }


    public void incr(){
        HashMap INCR_USER = actions.get("INCR_USER");
        String retrieve = (String) INCR_USER.get("retrieve");
        String save = (String) INCR_USER.get("save");

        System.out.println("当前直播间中观众数目为"+jedis.get("UserNum"));

        HashMap incrUserNum = counters.get(save);
        String valueFields = (String) incrUserNum.get("valueFields");

        int value = Integer.parseInt(valueFields);
        System.out.println("新增观众数为"+value);


        String time = df.format(new Date());// new Date()为获取当前系统时间
        System.out.println(time);
        for(int i=0;i<value;i++){
            jedis.incr("UserNum");
            jedis.sadd("UserSet",time);
            jedis.lpush("UserList",time);
            jedis.zadd("UserZSet",Integer.parseInt(time.substring(4,10)),time);
        }
        System.out.println("当前直播间中观众数目为"+jedis.get("UserNum"));
    }

    public void decr(){
        HashMap INCR_USER = actions.get("DECR_USER");
        String retrieve = (String) INCR_USER.get("retrieve");
        String save = (String) INCR_USER.get("save");

        System.out.println("当前直播间中观众数目为"+jedis.get("UserNum"));

        HashMap decrUserNum = counters.get(save);
        String valueFields = (String) decrUserNum.get("valueFields");

        int value = Integer.parseInt(valueFields);
        System.out.println("减少观众数为"+value);

        for(int i=0;i<value;i++){
            jedis.decr("UserNum");
        }
        System.out.println("当前直播间中观众数目为"+jedis.get("UserNum"));
    }

    public void showUserFre(){
        HashMap INCR_USER = actions.get("SHOW_USER_FREQ");
        String retrieve = (String) INCR_USER.get("retrieve");
        HashMap decrUserNum = counters.get(retrieve);
        String valueFields = (String) decrUserNum.get("valueFields");
        System.out.println("当前统计时间段为"+valueFields);
        String [] arr = valueFields.split(" ");
        try {
            Date start=df.parse(arr[0]);
            Date end=df.parse(arr[1]);
            int num=0;
            List<String> list = jedis.lrange("UserList", 0, -1 );
            for(int i=0;i<list.size();i++){
                Date time=df.parse(list.get(i));
                if(time.compareTo(start)>=0 && time.compareTo(end)<=0)
                    num++;
            }
            System.out.println("该时间段内进入的人数为"+num);
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public void showList(){
        List<String> list = jedis.lrange("UserList", 0, -1 );
        System.out.println(list);
    }

    public void showSet(){
        Set<String> set = jedis.smembers("UserSet");
        System.out.println(set);
    }

    public void showZSet(){
        Set<String> zset = jedis.zrange("UserZSet", 0, -1);
        System.out.println(zset);
    }

    public void init(){
        actions.clear();
        counters.clear();
        String path=MyJedis.class.getClassLoader().getResource("./").getPath()+"/Actions.json";
        System.out.println("path:--------"+path);
        String s = readJsonFile(path);
        JSONObject jobj = JSON.parseObject(s);
        JSONArray array = jobj.getJSONArray("Actions");//构建JSONArray数组
        for (int i = 0 ; i < array.size();i++){
            JSONObject key = (JSONObject)array.get(i);
            HashMap<String,String> action=new HashMap<>();
            String name = (String)key.get("name");
            action.put("name",name);
            JSONArray a = key.getJSONArray("retrieve");
            for(int j=0;j<a.size();j++){
                JSONObject k = (JSONObject)a.get(j);
                String retrieve = (String)k.get("counterName");

                action.put("retrieve",retrieve);
            }
            JSONArray b = key.getJSONArray("save");
            if(b!=null) {
                for (int j = 0; j < b.size(); j++) {
                    JSONObject k = (JSONObject) b.get(j);
                    String save = (String) k.get("counterName");
                    action.put("save", save);
                }
            }
            actions.put(name,action);
        }
        path=MyJedis.class.getClassLoader().getResource("./").getPath()+"/Counter.json";
        s = readJsonFile(path);
        jobj = JSON.parseObject(s);
        array = jobj.getJSONArray("counters");//构建JSONArray数组
        for (int i = 0 ; i < array.size();i++){
            JSONObject key = (JSONObject)array.get(i);
            HashMap<String,String> counter=new HashMap<>();
            String name = (String)key.get("counterName");
            counter.put("counterName",name);
            String counterIndex = (String)key.get("counterIndex");
            counter.put("counterIndex",counterIndex);
            String type = (String)key.get("type");
            counter.put("type",type);
            String KeyFields = (String)key.get("KeyFields");
            counter.put("KeyFields",KeyFields);
            String valueFields = (String)key.get("valueFields");
            if(valueFields != null)
                counter.put("valueFields",valueFields);
            counters.put(name,counter);
        }

        List<String> list = jedis.lrange("UserList", 0, -1 );
        jedis.set("UserNum",String.valueOf(list.size()));
        System.out.println(actions);
        System.out.println(counters);
    }

    public String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
