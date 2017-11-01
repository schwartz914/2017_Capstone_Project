package smg.xelas;

import android.app.Application;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.AndroidJUnitRunner;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;


import smg.xelas.TestApplication;
/**
 * Created by Sathya on 31/08/2017.
 */
@RunWith(AndroidJUnit4.class)
public class XelasTestRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, TestApplication.class.getName(), context);

    }


    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Login login  = new Login();
        //login.onClick(findViewById(R.id.btn_login));
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("smg.xelas", appContext.getPackageName());
    }
}
