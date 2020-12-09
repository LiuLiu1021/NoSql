package com.company;

import java.lang.reflect.Array;
import java.util.Arrays;

public class rank {
    public static String[] reRank(String[] input){
        if(input==null || input.length==0)
            return new String[0];
        int r=0,g=0,b=0;
        for(int i=0;i< input.length;i++){
            if(input[i].equals("r"))
                r++;
            else if(input[i].equals("g"))
                g++;
            else if(input[i].equals("b"))
                b++;
        }

        String[] ans=new String[input.length];
        for(int i=0;i<r;i++){
            ans[i]="r";
        }
        for(int i=0;i<g;i++){
            ans[r+i]="g";
        }
        for(int i=0;i<b;i++){
            ans[r+g+i]="b";
        }

        return ans;
    }

    public static String[] reRankTwoPointers(String[] input){
        if(input==null || input.length==0)
            return new String[0];
        int i=0;
        int left=0;
        int right=input.length-1;
        while(i<=right){
            if(input[i].equals("r")){
                String t=input[i];
                input[i]=input[left];
                input[left]=t;
                i++;
                left++;
            }else if(input[i].equals("g")){
                i++;
            }
            else if(input[i].equals("b")){
                String t=input[i];
                input[i]=input[right];
                input[right]=t;
                right--;
            }
        }
        return input;
    }

    public static void main(String[] args){
        String[] input=new String[]{"r","r","b","g","b","r","g"};
        String[] ans1=reRank(input);
        String[] ans2=reRankTwoPointers(input);
        for(int i=0;i<input.length;i++){
            System.out.print(ans1[i]);
        }
        System.out.println();
        for(int i=0;i<input.length;i++){
            System.out.print(ans2[i]);
        }
    }

}
