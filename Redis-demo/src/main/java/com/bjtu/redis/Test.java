package com.bjtu.redis;

import java.util.Scanner;

public class Test {
    public static void main(String[] args){
        //redis-server.exe redis.windows.conf
        MyJedis myJedis= null;
        try {
            myJedis = new MyJedis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int chose;
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("请选择要进行的操作");
            System.out.println("0、退出");
            System.out.println("1、INCR_USER");
            System.out.println("2、DECR_USER");
            System.out.println("3、SHOW_USER_FREQ");
            System.out.println("4、showList");
            System.out.println("5、showSet");
            System.out.println("6、showZSet");
            chose = sc.nextInt();
            switch (chose){
                case 0:
                    break;
                case 1:
                    myJedis.incr();
                    break;
                case 2:
                    myJedis.decr();
                    break;
                case 3:
                    myJedis.showUserFre();
                    break;
                case 4:
                    myJedis.showList();
                    break;
                case 5:
                    myJedis.showSet();
                    break;
                case 6:
                    myJedis.showZSet();

            }
            if(chose==0)
                break;
        }
    }
}
