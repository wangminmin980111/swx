package com.example.day21;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private GridView gv;
    private ArrayList<Bean.Bean1> list;
    private Button bt1;
    private Button bt2;
    private Button bt3;
    private Button bt4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gv = findViewById(R.id.gv);
        list = new ArrayList<>();
        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
        bt3 = findViewById(R.id.bt3);
        bt4 = findViewById(R.id.bt4);

        //Toast
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "全部", Toast.LENGTH_SHORT).show();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "娱乐要闻", Toast.LENGTH_SHORT).show();
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "娱乐大事件", Toast.LENGTH_SHORT).show();
            }
        });
        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "拍客爆料", Toast.LENGTH_SHORT).show();
            }
        });
        //创建适配器
        MyAdapter myAdapter = new MyAdapter();
        gv.setAdapter(myAdapter);
        @SuppressLint("HandlerLeak") Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Toast.makeText(MainActivity.this, msg.arg1+"", Toast.LENGTH_SHORT).show();
            }
        };

        //启动异步
        new MyTask(list,myAdapter,handler).execute();

    }
    class MyAdapter extends BaseAdapter{
        private ArrayList<Bean.Bean1> list = new ArrayList<>();

        public void setList(ArrayList<Bean.Bean1> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHandler viewHandler = null;
            if (convertView == null){
                viewHandler = new ViewHandler();
                Log.i("aa","优化布局");
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item,parent,false);
                Log.i("aa","优化控件");
                viewHandler.iv = convertView.findViewById(R.id.iv);
                viewHandler.tv1 = convertView.findViewById(R.id.tv1);
                viewHandler.tv2 = convertView.findViewById(R.id.tv2);

                convertView.setTag(viewHandler);
            }
            else {
                viewHandler = (ViewHandler) convertView.getTag();
            }
            //添加内容
            Log.i("aa","添加内容");
            Picasso.with(MainActivity.this).load(list.get(position).getImg()).into(viewHandler.iv);
            viewHandler.tv1.setText(list.get(position).getTitle());
            viewHandler.tv2.setText(list.get(position).getSubtitle());

            //条目的监听
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String s = list.get(position).getSubtitle();
                    Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
        }
    }
    class ViewHandler{
        ImageView iv;
        TextView tv1;
        TextView tv2;
    }

}
//异步下载
class MyTask extends AsyncTask<String,Integer,String>{
    private ArrayList<Bean.Bean1> list;
    private MainActivity.MyAdapter myAdapter;
    private Handler handler;
    public MyTask(ArrayList<Bean.Bean1> list, MainActivity.MyAdapter myAdapter, Handler handler) {
        this.list = list;
        this.myAdapter = myAdapter;
        this.handler = handler;
    }
    @Override
    protected String doInBackground(String... strings) {
        StringBuffer sb = null;
        try {
            //Log.i("aa","开始下载");
            Log.i("aa","开始下载");
            URL url = new URL("https://gitee.com/little_bird_oh_777/test_data_collection/raw/master/test42018061010.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //Log.i("aa","开启连接");
            Log.i("aa","开启连接");
            connection.setRequestMethod("GET");
//            connection.setConnectTimeout(3000);
            int code = connection.getResponseCode();
            sb = new StringBuffer();
            Log.i("aa",sb.toString());
            if (code == 200){
                Log.i("aa","开吐");
                Message message = Message.obtain();
                message.arg1 = code;
                handler.sendMessage(message);
                Log.i("aa","准备读取");
                InputStream is = connection.getInputStream();
                int len = 0;
                byte byt[] = new byte[1024];
                Log.i("aa","正在读取");
                while ((len = is.read(byt)) != -1){
                    sb.append(new String(byt,0,len));
                }
                //    Log.i("aa","读取成功");
                Log.i("aa","读取成功");
                Log.i("aa",sb.toString());
            }
            else {
                //Log.i("aa","下载超时");
                Log.i("aa","下载超时");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("aa",sb.toString());
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //Log.i("aa","开始解析数据");
        Log.i("aa","开始解析数据");
        Gson gson = new Gson();
        Bean bean = gson.fromJson(s, Bean.class);
        list = bean.getItem();
        //Log.i("aa","启动适配器");
        Log.i("aa","启动适配器");
        myAdapter.setList(list);
        myAdapter.notifyDataSetChanged();

    }
}

//对应类
class Bean{
    private ArrayList<Bean1> item;
    private int httpStatusCode;

    class Bean1{
        private String summary;
        private String img;
        private String trackInfo;
        private String gifImg;
        private String summaryType;
        private String title;
        private String type;
        private String subtitleType;
        private int itemId;
        private ExtraExtend extraExtend;
        private String spm;
        private String subtitle;
        private Action action;
        private String scm;
            class ExtraExtend{
                private double seconds;
                private int paid;

                public ExtraExtend(double seconds, int paid) {
                    this.seconds = seconds;
                    this.paid = paid;
                }

                @Override
                public String toString() {
                    return "ExtraExtend{" +
                            "seconds=" + seconds +
                            ", paid=" + paid +
                            '}';
                }

                public double getSeconds() {
                    return seconds;
                }

                public void setSeconds(double seconds) {
                    this.seconds = seconds;
                }

                public int getPaid() {
                    return paid;
                }

                public void setPaid(int paid) {
                    this.paid = paid;
                }
            }
            class Action{
                private ReportExtend reportExtend;
                private Extra extra;
                private String type;
                    class ReportExtend{
                        private String spm;
                        private String trackInfo;
                        private String arg1;
                        private String scm;
                        private String pageName;

                        @Override
                        public String toString() {
                            return "ReportExtend{" +
                                    "spm='" + spm + '\'' +
                                    ", trackInfo='" + trackInfo + '\'' +
                                    ", arg1='" + arg1 + '\'' +
                                    ", scm='" + scm + '\'' +
                                    ", pageName='" + pageName + '\'' +
                                    '}';
                        }

                        public String getSpm() {
                            return spm;
                        }

                        public void setSpm(String spm) {
                            this.spm = spm;
                        }

                        public String getTrackInfo() {
                            return trackInfo;
                        }

                        public void setTrackInfo(String trackInfo) {
                            this.trackInfo = trackInfo;
                        }

                        public String getArg1() {
                            return arg1;
                        }

                        public void setArg1(String arg1) {
                            this.arg1 = arg1;
                        }

                        public String getScm() {
                            return scm;
                        }

                        public void setScm(String scm) {
                            this.scm = scm;
                        }

                        public String getPageName() {
                            return pageName;
                        }

                        public void setPageName(String pageName) {
                            this.pageName = pageName;
                        }

                        public ReportExtend(String spm, String trackInfo, String arg1, String scm, String pageName) {
                            this.spm = spm;
                            this.trackInfo = trackInfo;
                            this.arg1 = arg1;
                            this.scm = scm;
                            this.pageName = pageName;
                        }
                    }
                    class Extra{
                        private String value;

                        @Override
                        public String toString() {
                            return "Extra{" +
                                    "value='" + value + '\'' +
                                    '}';
                        }

                        public String getValue() {
                            return value;
                        }

                        public void setValue(String value) {
                            this.value = value;
                        }

                        public Extra(String value) {
                            this.value = value;
                        }
                    }

                @Override
                public String toString() {
                    return "Action{" +
                            "reportExtend=" + reportExtend +
                            ", extra=" + extra +
                            ", type='" + type + '\'' +
                            '}';
                }

                public ReportExtend getReportExtend() {
                    return reportExtend;
                }

                public void setReportExtend(ReportExtend reportExtend) {
                    this.reportExtend = reportExtend;
                }

                public Extra getExtra() {
                    return extra;
                }

                public void setExtra(Extra extra) {
                    this.extra = extra;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public Action(ReportExtend reportExtend, Extra extra, String type) {
                    this.reportExtend = reportExtend;
                    this.extra = extra;
                    this.type = type;

                }
            }

        @Override
        public String toString() {
            return "Bean1{" +
                    "summary='" + summary + '\'' +
                    ", img='" + img + '\'' +
                    ", trackInfo='" + trackInfo + '\'' +
                    ", gifImg='" + gifImg + '\'' +
                    ", summaryType='" + summaryType + '\'' +
                    ", title='" + title + '\'' +
                    ", type='" + type + '\'' +
                    ", subtitleType='" + subtitleType + '\'' +
                    ", itemId=" + itemId +
                    ", extraExtend=" + extraExtend +
                    ", spm='" + spm + '\'' +
                    ", subtitle='" + subtitle + '\'' +
                    ", action=" + action +
                    ", scm='" + scm + '\'' +
                    '}';
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getTrackInfo() {
            return trackInfo;
        }

        public void setTrackInfo(String trackInfo) {
            this.trackInfo = trackInfo;
        }

        public String getGifImg() {
            return gifImg;
        }

        public void setGifImg(String gifImg) {
            this.gifImg = gifImg;
        }

        public String getSummaryType() {
            return summaryType;
        }

        public void setSummaryType(String summaryType) {
            this.summaryType = summaryType;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSubtitleType() {
            return subtitleType;
        }

        public void setSubtitleType(String subtitleType) {
            this.subtitleType = subtitleType;
        }

        public int getItemId() {
            return itemId;
        }

        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        public ExtraExtend getExtraExtend() {
            return extraExtend;
        }

        public void setExtraExtend(ExtraExtend extraExtend) {
            this.extraExtend = extraExtend;
        }

        public String getSpm() {
            return spm;
        }

        public void setSpm(String spm) {
            this.spm = spm;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public Action getAction() {
            return action;
        }

        public void setAction(Action action) {
            this.action = action;
        }

        public String getScm() {
            return scm;
        }

        public void setScm(String scm) {
            this.scm = scm;
        }

        public Bean1(String summary, String img, String trackInfo, String gifImg, String summaryType, String title, String type, String subtitleType, int itemId, ExtraExtend extraExtend, String spm, String subtitle, Action action, String scm) {
            this.summary = summary;
            this.img = img;
            this.trackInfo = trackInfo;
            this.gifImg = gifImg;
            this.summaryType = summaryType;
            this.title = title;
            this.type = type;
            this.subtitleType = subtitleType;
            this.itemId = itemId;
            this.extraExtend = extraExtend;
            this.spm = spm;
            this.subtitle = subtitle;
            this.action = action;
            this.scm = scm;
        }
    }

    public Bean(ArrayList<Bean1> item, int httpStatusCode) {
        this.item = item;
        this.httpStatusCode = httpStatusCode;
    }

    @Override
    public String toString() {
        return "Bean{" +
                "item=" + item +
                ", httpStatusCode=" + httpStatusCode +
                '}';
    }

    public ArrayList<Bean1> getItem() {
        return item;
    }

    public void setItem(ArrayList<Bean1> item) {
        this.item = item;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }
}
