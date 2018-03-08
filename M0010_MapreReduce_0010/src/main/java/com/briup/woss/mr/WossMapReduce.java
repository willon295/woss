package com.briup.woss.mr;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import java.io.IOException;


public class WossMapReduce {


    /**
     * Mapper 类
     * <p>
     * 将用户的  ip 作为输出的 key ，value为每行数据
     */
    static class WossMapper extends Mapper<LongWritable, Text, Text, Text> {

        private WossDataParser wossDataParser = new WossDataParser();
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            wossDataParser.parse(value);

            if (wossDataParser.isValid()) {
                context.write(new Text(wossDataParser.getUserIp()), value);
                //输出的数据格式：  <ip ,  [#briup1388|037:wKgB1660A|7|1285376771|70.41.11.217,#|037:wKgB1660A|8|1285376771|70.41.11.217...]>
            }
        }
    }


    /**
     * Reducer类
     */
    static class WossReduce extends Reducer<Text, Text, NullWritable, Text> {

        private WossDataParser parser = new WossDataParser();

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String aaaName=null;
            String loginIp=null;
            Long loginDate=null;
            Long logoutDate=null;
            String nasIp=null;
            Long timeDuration=null;
            StringBuilder sb=new StringBuilder();
            for(Text value : values){
                parser.parse(value);
                if(parser.isValid()){
                    //根据上下线信息作出不同处理，给变量赋值
                    if("7".equals(parser.getFlag())){
                        aaaName=parser.getAaaName();
                        loginIp=parser.getUserIp();
                        loginDate=parser.getTime();
                        nasIp=parser.getNasIp();
                    }else if("8".equals(parser.getFlag())){
                        logoutDate=parser.getTime();
                    }
                    //上线下线都不为null，计算时长
                    if(logoutDate!=null&&loginDate!=null){
                        timeDuration=logoutDate-loginDate;
                    }
                }
            }
            //当所有属性赋值完毕，按此顺序添加
            sb.append(aaaName).append("\t");
            sb.append(loginDate).append("\t");
            sb.append(logoutDate).append("\t");
            sb.append(loginIp).append("\t");
            sb.append(nasIp).append("\t");
            sb.append(timeDuration).append("\t").append("\r\n");
            //传给上下文
            context.write(NullWritable.get(),new Text(sb.toString()));

        }
    }


    public static void main(String[] args) throws Exception {


        Configuration conf = new Configuration();

        //从运行终端 获取输入输出路径，第一个参数为 输入，第二个为输出
        Path input = new Path(args[0]);
        Path output = new Path(args[1]);

        //定义作业对象 获取job对象
        Job job = Job.getInstance(conf, "WossMR");

        //设置作业要执行的类
        job.setJarByClass(WossMapReduce.class);

        //设置 mapper 输出相关信息
        job.setMapperClass(WossMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.addInputPath(job, input);


        //设置 Reducer 相关信息
        job.setReducerClass(WossReduce.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, output);

        //提交作业，返回值是作业提交成功是否
        boolean res = job.waitForCompletion(true);
        System.exit(res ? 0 : 1);
    }

}
