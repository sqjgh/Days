package lanou.days.write;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import lanou.days.R;
import lanou.days.base.BaseFragment;

/**
 *  写笔记的Fragment
 * Created by dllo on 16/11/22.
 */
public class WriteFragment extends BaseFragment implements View.OnClickListener {
    private EditText title,content;
    private LinearLayout updata,template,chooseTypeOne,chooseTypeTwo,chooseTypeThree;
    private Button btnQuit;
    private View mView;
    private PopupWindow mPopup;
    private String mTime;

    @Override
    protected void initData() {
        mPopup = new PopupWindow(mView,720,300);
        mPopup.setAnimationStyle(R.style.PopupAnimation);
        //TODO 封装
        SharedPreferences getSp = getActivity().getSharedPreferences("write",Context.MODE_PRIVATE);
        title.setText(getSp.getString("文章标题",""));
        content.setText(getSp.getString("文章内容",""));

    }

    @Override
    protected void initView() {
        mView = getActivity().getLayoutInflater().inflate(R.layout.pop_write_template,null);
        title = bindView(R.id.et_write_title);
        content = bindView(R.id.et_write_content);
        updata = bindView(R.id.ll_write_updata);
        template = bindView(R.id.ll_write_template);
        template.setOnClickListener(this);
        chooseTypeOne = bindView(mView,R.id.ll_write_pop_one);
        chooseTypeOne.setOnClickListener(this);
        chooseTypeTwo = bindView(mView,R.id.ll_write_pop_two);
        chooseTypeTwo.setOnClickListener(this);
        chooseTypeThree = bindView(mView,R.id.ll_write_pop_three);
        chooseTypeThree.setOnClickListener(this);
        btnQuit = bindView(mView,R.id.btn_write_pop_quit);
        btnQuit.setOnClickListener(this);
        updata.setOnClickListener(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_write;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_write_updata:
                writeDialog();
                break;
            case R.id.ll_write_template:
                if (!mPopup.isShowing()){
                    chooseTemplate();
                }
                break;
            case R.id.btn_write_pop_quit:
                mPopup.dismiss(); // 关闭
                break;
            case R.id.ll_write_pop_one:
                getTemplateOne();
                break;
            case R.id.ll_write_pop_two:
                getTemplateTwo();
                break;
            case R.id.ll_write_pop_three:
                getTemplateThree();
                break;
            default:
                break;
        }
    }

    private void getTemplateThree() {
        title.setText((mTime + "日记"));
        content.setText("天气 ☁                            心情 (｡・`ω´･)\n" +
                "=====================================\n" +
                "   (´・ω・)ﾉ     每天写日记是很好的习惯喵");
        mPopup.dismiss();
    }

    private void getTemplateTwo() {
        getSystemTime();
        title.setText(("会议记录:" +" "+ mTime ));
        content.setText("会议日期: \n"+
                "______________________________________________\n" +
                "会议主题: \n" +
                "______________________________________________\n" +
                "会议内容\n" +
                "     1.\n" +
                "\n" +
                "     2.\n" +
                "\n" +
                "     3.\n" +
                "\n" +
                "     4.\n" +
                "\n" +
                "______________________________________________\n" +
                "ToDo事项\n" +
                "     ①:\n" +
                "     ②:\n" +
                "     ③:\n" +
                "     ④:\n" +
                "     ⑤:\n" +
                "     ⑥:\n" +
                "     ⑦:\n" +
                "\n" +
                "\n" +
                "______________________________________________\n" +
                "Days提醒您,要做的事情最优数量是3喵");
        mPopup.dismiss();
    }

    private void getTemplateOne() {
        getSystemTime();
        title.setText((mTime + "的账单"));
        content.setText("收入:\n" +
                "\n" +
                "\n" +
                "渠道:\n" +
                "\n" +
                "\n" +
                "\n" +
                "支出:\n" +
                "\n" +
                "\n" +
                "方式:\n" +
                "\n" +
                "\n" +
                "\n" +
                "**********************************************\n" +
                "今日总账:\n" +
                "\n" +
                "\n" +
                "小结:");
        mPopup.dismiss();
    }


    private void writeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("确定发送喵?")
                .setIcon(R.mipmap.days_icon)
                .setMessage("您已经确定写完了喵?\n上传后可就会清空了喵");
        builder.setPositiveButton("上传", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (title.getText().toString().equals("")){
                    Toast.makeText(getContext(), "至少得写一个标题喵", Toast.LENGTH_SHORT).show();
                } else {
                    upData();
                }
            }
        });
        builder.setNegativeButton("先等等", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               // 什么也不做,\(^o^)/~
            }
        });
        builder.show();
    }

    /**
     * 用于上传的方法,先判断用户是否登录
     */
    private void upData() {
        BmobUser bmobUser = BmobUser.getCurrentUser();
        if(bmobUser != null){
            // 允许用户使用应用
            // 继续上传
            WriteBean writeBean = new WriteBean();
            String titleStr = title.getText().toString();
            writeBean.setTitle(titleStr);
            String contentStr = content.getText().toString();
            writeBean.setContent(contentStr);
            writeBean.setAuthor(bmobUser);
            writeBean.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        Toast.makeText(getContext(), "笔记创建成功了喵", Toast.LENGTH_SHORT).show();
                        title.getText().clear();
                        content.getText().clear();
                    } else {
                        Toast.makeText(getContext(), "失败了喵", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            //缓存用户对象为空时， 可打开用户注册界面
            Toast.makeText(getContext(), "您还没有登录喵,请先登录喵", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 选择模板的方法
     */
    private void chooseTemplate() {
        // pop设置
        mPopup.showAtLocation(mView, Gravity.CENTER,0,0);
        mPopup.setOutsideTouchable(false);
        mPopup.setFocusable(true); // 设置PopupWindow可获得焦点
        mPopup.setTouchable(true);

    }

    @Override
    public void onDestroyView() {
        SharedPreferences sp = getActivity().getSharedPreferences("write", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("文章标题",title.getText().toString());
        editor.putString("文章内容",content.getText().toString());
        editor.apply();
           super.onDestroyView();
    }
    private void getSystemTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        mTime = formatter.format(curDate);
    }
}
