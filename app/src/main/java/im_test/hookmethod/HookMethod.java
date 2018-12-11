package im_test.hookmethod;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


/**
 * Created by im-mac on 2018/1/22.
 */

public class HookMethod  implements IXposedHookLoadPackage {

    private String packageName;
    private ClassLoader classLoader;
    private XC_MethodHook methodhook;

    private static String confFileName = "/data/data/im_test.hookmethod/monitor.conf";


    private boolean tryHookMethodByNameWithMethod(String className, String methodName, XC_MethodHook methodhook)
    {
        try {
            Class<?> classNeedHook = classLoader.loadClass(className);
            Method[] deMethods = classNeedHook.getDeclaredMethods();

            for(Method deMethod:deMethods)
            {
                //XposedBridge.log(deMethod.getName());
                if(methodName.equals(deMethod.getName()))
                {
                    //XposedBridge.log("start to hook method :" +className + "#"+ methodName);
                    //XposedBridge.log("start to hook method :" +className + "#"+ methodName);
                    XposedBridge.hookMethod(deMethod, methodhook);

                }
            }

            return true;
        }catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return false;

    }

    private boolean tryHookConstructorsByName(String className, XC_MethodHook methodhook)
    {
        try {
            Class<?> classNeedHook = classLoader.loadClass(className);

            XposedBridge.hookAllConstructors(classNeedHook, methodhook);

            return true;
        }catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return false;

    }


    protected void tryToHookProgram(XC_LoadPackage.LoadPackageParam loadPackageParam, String tmpString) {
        String params[] = tmpString.split(" ");
        //Log.d("monitor ", " start to hook " + tmpString);

        if(params.length == 3)
        {
            String _packageName = params[0];
            String _className = params[1];
            String _methodName = params[2];
            //Log.d("monitor " + loadPackageParam.packageName, tmpString);
            if(_packageName.equals(loadPackageParam.packageName))
            {
                packageName = loadPackageParam.packageName;
                Log.d("monitor " + loadPackageParam.packageName, " start to hook " + packageName);

                classLoader = loadPackageParam.classLoader;
                methodhook = new MyMethodHook(packageName, classLoader);

                tryHookMethodByNameWithMethod(_className, _methodName, methodhook);

            }
            //XposedBridge.log(String.format("try to hook method Error by %s， example: packageName ClassName MethodName", tmpString));
            return;
        }else
        if(params.length == 2) {
            String _packageName = params[0];
            String _className = params[1];
            //Log.d("monitor " + loadPackageParam.packageName, tmpString);
            if (_packageName.equals(loadPackageParam.packageName)) {
                packageName = loadPackageParam.packageName;
                Log.d("monitor " + loadPackageParam.packageName, " start to hook " + packageName);

                classLoader = loadPackageParam.classLoader;
                methodhook = new MyMethodHook(packageName, classLoader);

                tryHookConstructorsByName(_className, methodhook);

            }

        }


    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {

        File file = new File(confFileName);
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader(file));
            String tmpString = null;

            while((tmpString = reader.readLine()) != null)
            {
                if(tmpString.trim().length() > 0 && !tmpString.trim().startsWith("#"))
                    tryToHookProgram(loadPackageParam, tmpString.trim());
            }
            reader.close();
        }catch(IOException e){
            Log.e("monitor", "read conf file error!");
        }finally {
            if(reader != null)
                try{
                    reader.close();
                }catch(IOException e1)
                {

                }
        }
    }
}

