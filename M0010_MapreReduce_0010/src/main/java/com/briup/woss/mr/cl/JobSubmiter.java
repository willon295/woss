package com.briup.woss.mr.cl;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;


/*提交作业的主类
 * */
public class JobSubmiter {


    static class MyMapper extends Mapper<LongWritable, Text, Text, BIDRWritable> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            DataParser parser = new DataParser();
            parser.parse(value);
            //传给 reduce值为   <ip , <BIDRWriteable@1 , BIDRWriteable@2>   >,其中一个有 IP ， 用户名，上线时间，nsa-ip，，另一个有 下线时间，IP
            context.write(new Text(parser.getBidr().getLoginIp()), parser.getBidr());
        }
    }

    //可以选择把  BIDR对象写入ＨＤＦＳ，此处将数据对象 toString 写入 HDFS
    static class MyReducer extends Reducer<Text, BIDRWritable, NullWritable, Text> {

        @Override
        protected void reduce(Text key, Iterable<BIDRWritable> values, Context context) throws IOException, InterruptedException {

            BIDRWritable completeBIDR = new BIDRWritable();

            for (BIDRWritable bidr : values) {

                completeBIDR.setLoginIp(bidr.getLoginIp());
                //如果用户名  不只包含  # ，说明是上线信息对象
                if (!bidr.getAaaName().equals("#")) {
                    completeBIDR.setAaaName(bidr.getAaaName());
                    completeBIDR.setNsaIp(bidr.getNsaIp());
                    completeBIDR.setLogoutTime(bidr.getLoginTime());
                } else {

                    completeBIDR.setLogoutTime(bidr.getLogoutTime());

                }
                //如果两项时间都不为空，则算出 在线时长
                if (completeBIDR.getLoginTime() != null && completeBIDR.getLogoutTime() != null) {
                    completeBIDR.setTimeDuration(completeBIDR.getLogoutTime() - completeBIDR.getLoginTime());
                }


            }
            context.write(NullWritable.get(), new Text(completeBIDR.toString()));

        }
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {


        Path input = new Path(args[0]);
        Path output = new Path(args[1]);


        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        job.setJarByClass(JobSubmiter.class);

        //map 类  输入 输出信息设置
        job.setMapperClass(MyMapper.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(BIDRWritable.class);
        TextInputFormat.addInputPath(job,input);


        //reduce 类  输出的信息
        job.setReducerClass(MyReducer.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setOutputKeyClass(NullWritable.class);
        job.setOutputValueClass(Text.class);
        TextOutputFormat.setOutputPath(job,output);

        boolean res = job.waitForCompletion(true);
        System.exit(res ? 0 : 1);

    }


}


