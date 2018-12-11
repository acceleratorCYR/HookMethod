package im_test.hookmethod;

import android.text.SpannableStringBuilder;
import android.util.Log;

import javax.crypto.CipherSpi;

import de.robv.android.xposed.XC_MethodHook;

/**
 * Created by im-mac on 2018/1/23.
 */

public class MyMethodHook extends XC_MethodHook {
    private String packageName;
    private ClassLoader classLoader;

    private String tag;

    public MyMethodHook(String packageName, ClassLoader classLoader) {
        this.packageName = packageName;
        this.classLoader = classLoader;
        this.tag = "monitor_" + packageName;
    }

    protected String getStackTracesStr(Throwable e){
        StackTraceElement []stackTraces = e.getStackTrace();
        String str="";
        if(stackTraces != null)
        {
            for(int i=0; i< (stackTraces.length < 20? stackTraces.length: 20); i++)
            {
                str +=(String.format("\n      : %s #%s :%s",
                        stackTraces[i].getClassName(),
                        stackTraces[i].getMethodName(), stackTraces[i].getLineNumber()));
            }
            if(stackTraces.length >= 20)
                str +=("\n       ...   ...    \n");
        }

        return str;
    }

    protected boolean CheckType(Object arg, Class<?> clz)
    {
        boolean result;
        if(arg == null)
            return false;
        else {
            try {
                clz.cast(arg);
                result = true;
            }catch(ClassCastException e)
            {
                result = false;
            }
        }

        return result;
    }


    protected void OutputByteArray(StringBuffer outputStr, byte[] param)
    {

        if(param == null) {
            outputStr.append(" null") ;
            return;
        }

        outputStr.append(String.format("length:%d ", param.length));
        for(int bi=0;bi< (param.length>300?300:param.length);bi++) {
            outputStr.append(Integer.toString(param[bi] >> 4 & 0xF, 16));
            outputStr.append(Integer.toString(param[bi] & 0xF, 16));
        }
        String str = new String(param);

        if(str.length() <= 300)
            outputStr.append(" \t" + str);
        else
            outputStr.append(" \t" + str.substring(0,300));

    }

    protected void TryOutputParamOrResult(StringBuffer outputStr,
                                          Object obj, String paramtag, int i) throws Throwable
    {
        if(obj == null)
        {
            outputStr.append("\n | " + paramtag + " " + Integer.toString(i) + ": "
                    +  "null ");
            return;
        }
        else
        {
            outputStr.append( "\n | " + paramtag + " " + Integer.toString(i) + ": "
                    + obj.getClass().getName() + "\t ");
        }

        if(CheckType(obj, String.class))
        {
            outputStr.append((String)obj);

        }

        if(CheckType(obj, byte[].class))
        {
            OutputByteArray( outputStr, (byte[])obj);
        }else if(CheckType(obj, boolean.class))
        {
            outputStr.append((boolean)obj?"true":"false");

        }else if(CheckType(obj, Long.class))
        {
            outputStr.append(Long.toString((Long)obj));

        }else if(CheckType(obj, long.class))
        {
            outputStr.append(Long.toString((long)obj));

        }else if(CheckType(obj, Integer.class))
        {
            outputStr.append(Integer.toString((Integer)obj));
        }else
        if(CheckType(obj, String[].class))
        {
            for(int si=0; si<((String[])obj).length; si++)
            {
                outputStr.append("\n \t ||");
                outputStr.append(((String[])obj)[si]);
            }
        }else
        if(CheckType(obj, int.class))
        {
            outputStr.append(Integer.toString((Integer)obj));
        }else
        if(CheckType(obj, android.text.SpannableStringBuilder.class))
        {
            outputStr.append(((SpannableStringBuilder)obj).toString());
        }else
        if(CheckType(obj, javax.crypto.spec.SecretKeySpec.class))
        {
            byte[] keys = ((javax.crypto.spec.SecretKeySpec)obj).getEncoded();
            OutputByteArray( outputStr, (byte[])keys);
        }

    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Throwable th = new Throwable();
        StackTraceElement[] stackTraces = th.getStackTrace();

        StringBuffer stackOutput = new StringBuffer("monitor HookMethodStackTraceAfter "
                + param.getClass().getName());

        if(stackTraces != null)
        {
            for(int i=0; i< (stackTraces.length < 20? stackTraces.length: 20); i++)
            {
                stackOutput.append(String.format("\n      StackTrace: %s #%s :%s",
                        stackTraces[i].getClassName(),
                        stackTraces[i].getMethodName(), stackTraces[i].getLineNumber()));

            }
            if(stackTraces.length >= 20)
            {
                stackOutput.append("\n      ......   ...... \n");
            }
        }

        Object methodResult = param.getResult();

        //StringBuffer outputStr;
        if(param.args == null)
            stackOutput.append("\nafter Call paramsLen: 0 ");
        else {
            stackOutput.append("\nafter Call paramsLen:" + param.args.length + " ");

            for (int i = 0; i < param.args.length; i++) {
                TryOutputParamOrResult(stackOutput, param.args[i], "param", i);

            }
        }

        TryOutputParamOrResult(stackOutput, methodResult, "result", 0);

        stackOutput.append("\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        Log.w(tag, stackOutput.toString());


        super.afterHookedMethod(param);
    }
}